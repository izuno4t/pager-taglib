package com.jsptags.navigation.pager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.TagSupport;

import org.junit.jupiter.api.Test;

class PagerTagTest {

	@Test
	void setMaxPageItemsRejectsZero() {
		PagerTag tag = new PagerTag();
		assertThatThrownBy(() -> tag.setMaxPageItems(0))
				.isInstanceOf(JspTagException.class);
	}

	@Test
	void doStartTagRequestScopeExportsAttributesAndComputesPageNumber()
			throws JspException {
		PagerTag tag = new PagerTag();
		tag.setUrl("newsList.do");
		tag.setMaxPageItems(10);
		tag.setExport("pageOffset,currentPageNumber=pageNumber");
		tag.setScope("request");

		PageContext pageContext = mock(PageContext.class);
		HttpServletRequest request = mock(HttpServletRequest.class);
		when(pageContext.getRequest()).thenReturn(request);
		when(request.getParameter("pager.offset")).thenReturn("20");
		when(request.getAttribute("pager")).thenReturn(null);

		tag.setPageContext(pageContext);
		int result = tag.doStartTag();

		assertThat(result).isEqualTo(TagSupport.EVAL_BODY_INCLUDE);
		verify(request).setAttribute("pager", tag);
		verify(request).setAttribute("pageOffset", Integer.valueOf(20));
		verify(request).setAttribute("currentPageNumber", Integer.valueOf(3));
	}

	@Test
	void indexAndPagingHelpersRespectConfiguration() throws JspException {
		PagerTag tag = new PagerTag();
		tag.setUrl("newsList.do");
		tag.setIndex("center");
		tag.setItems(100);
		tag.setMaxPageItems(10);
		tag.setMaxIndexPages(5);

		PageContext pageContext = mock(PageContext.class);
		HttpServletRequest request = mock(HttpServletRequest.class);
		when(pageContext.getRequest()).thenReturn(request);
		when(request.getParameter("pager.offset")).thenReturn("30");

		tag.setPageContext(pageContext);
		tag.doStartTag();

		assertThat(tag.getPageCount()).isEqualTo(10);
		int firstPage = tag.getFirstIndexPage();
		assertThat(firstPage).isEqualTo(1);
		assertThat(tag.getLastIndexPage(firstPage)).isEqualTo(5);
		assertThat(tag.isIndexNeeded()).isTrue();
		assertThat(tag.hasPrevPage()).isTrue();
		assertThat(tag.hasNextPage()).isTrue();
		assertThat(tag.getPrevOffset()).isEqualTo(20);
		assertThat(tag.getNextOffset()).isEqualTo(40);
		assertThat(tag.getPageUrl(2)).isEqualTo("newsList.do?pager.offset=20");
	}

	@Test
	void nextItemRespectsOffsetsAndMaxItems() throws JspException {
		PagerTag tag = new PagerTag();
		tag.setUrl("newsList.do");
		tag.setMaxItems(3);
		tag.setMaxPageItems(2);

		PageContext pageContext = mock(PageContext.class);
		HttpServletRequest request = mock(HttpServletRequest.class);
		when(pageContext.getRequest()).thenReturn(request);
		when(request.getParameter("pager.offset")).thenReturn("1");

		tag.setPageContext(pageContext);
		tag.doStartTag();

		assertThat(tag.nextItem()).isFalse();
		assertThat(tag.nextItem()).isTrue();
		assertThat(tag.nextItem()).isTrue();
		assertThat(tag.nextItem()).isFalse();
	}

	@Test
	void addParamEncodesValueAndBuildsUrl() throws JspException {
		TestContext ctx = TestContext.create("0");
		when(ctx.request.getCharacterEncoding()).thenReturn("UTF-8");

		PagerTag tag = new PagerTag();
		tag.setPageContext(ctx.pageContext);
		tag.setUrl("newsList.do");
		tag.setScope("request");
		tag.doStartTag();

		tag.addParam("q", "hello world");

		assertThat(tag.getPageUrl(0))
				.isEqualTo("newsList.do?q=hello+world&pager.offset=0");
	}

	@Test
	void addParamUsesRequestValuesWhenValueNull() throws JspException {
		TestContext ctx = TestContext.create("0");
		when(ctx.request.getCharacterEncoding()).thenReturn("UTF-8");
		when(ctx.request.getParameterValues("filter"))
				.thenReturn(new String[] { "a b", "c" });

		PagerTag tag = new PagerTag();
		tag.setPageContext(ctx.pageContext);
		tag.setUrl("newsList.do");
		tag.setScope("request");
		tag.doStartTag();

		tag.addParam("filter", null);

		assertThat(tag.getPageUrl(0))
				.isEqualTo("newsList.do?filter=a+b&filter=c&pager.offset=0");
	}

	@Test
	void baseUriStripsQueryString() throws JspException {
		PageContext pageContext = mock(PageContext.class);
		HttpServletRequest request = mock(HttpServletRequest.class);
		when(pageContext.getRequest()).thenReturn(request);
		when(request.getRequestURI()).thenReturn("/news/list?x=1");
		when(request.getParameter("pager.offset")).thenReturn("0");
		when(request.getCharacterEncoding()).thenReturn("UTF-8");

		PagerTag tag = new PagerTag();
		tag.setPageContext(pageContext);
		tag.setScope("request");
		tag.doStartTag();

		assertThat(tag.getPageUrl(0))
				.isEqualTo("/news/list?pager.offset=0");
	}
}
