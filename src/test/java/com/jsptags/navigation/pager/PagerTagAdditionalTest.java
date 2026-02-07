package com.jsptags.navigation.pager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.lang.reflect.Field;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class PagerTagAdditionalTest {

	@Test
	void setIndexRejectsInvalidValue() {
		PagerTag tag = new PagerTag();
		assertThatThrownBy(() -> tag.setIndex("bad"))
				.isInstanceOf(JspTagException.class);
	}

	@Test
	void setIndexAcceptsNullAndKnownValues() throws JspException {
		PagerTag tag = new PagerTag();
		tag.setIndex(null);
		tag.setIndex("center");
		tag.setIndex("forward");
		tag.setIndex("half-full");
		assertThat(tag.getIndex()).isEqualTo("half-full");
	}

	@Test
	void setScopeRejectsInvalidValue() {
		PagerTag tag = new PagerTag();
		assertThatThrownBy(() -> tag.setScope("bad"))
				.isInstanceOf(JspTagException.class);
	}

	@Test
	void setScopeAcceptsNullAndKnownValues() throws JspException {
		PagerTag tag = new PagerTag();
		tag.setScope(null);
		tag.setScope("page");
		tag.setScope("request");
		assertThat(tag.getScope()).isEqualTo("request");
	}

	@Test
	void setExportRejectsInvalidExpression() {
		PagerTag tag = new PagerTag();
		assertThatThrownBy(() -> tag.setExport("="))
				.isInstanceOf(JspTagException.class);
	}

	@Test
	void setExportAcceptsNullAndValidExpression() throws JspException {
		PagerTag tag = new PagerTag();
		tag.setExport(null);
		tag.setExport("pageOffset,pageNumber");
		assertThat(tag.getExport()).isEqualTo("pageOffset,pageNumber");
	}

	@Test
	void pageScopeExportsAndRestoresAttributes() throws JspException {
		TestContext ctx = TestContext.create("10");
		ctx.pageAttributes.put("pageOffset", Integer.valueOf(1));
		ctx.pageAttributes.put("pageNumber", Integer.valueOf(2));

		PagerTag tag = new PagerTag();
		tag.setPageContext(ctx.pageContext);
		tag.setUrl("newsList.do");
		tag.setExport("pageOffset,pageNumber");
		tag.setScope("page");
		tag.doStartTag();

		assertThat(ctx.pageAttributes.get("pageOffset"))
				.isEqualTo(Integer.valueOf(10));
		assertThat(ctx.pageAttributes.get("pageNumber"))
				.isEqualTo(Integer.valueOf(2));

		tag.doEndTag();
		assertThat(ctx.pageAttributes.get("pageOffset"))
				.isEqualTo(Integer.valueOf(1));
		assertThat(ctx.pageAttributes.get("pageNumber"))
				.isEqualTo(Integer.valueOf(2));
	}

	@Test
	void isOffsetSetsItemCountFromOffset() throws JspException {
		TestContext ctx = TestContext.create("7");

		PagerTag tag = new PagerTag();
		tag.setPageContext(ctx.pageContext);
		tag.setUrl("newsList.do");
		tag.setIsOffset(true);
		tag.doStartTag();

		assertThat(tag.getItemCount()).isEqualTo(7);
	}

	@Test
	void offsetParamInvalidIsIgnored() throws JspException {
		TestContext ctx = TestContext.create("nope");

		PagerTag tag = new PagerTag();
		tag.setPageContext(ctx.pageContext);
		tag.setUrl("newsList.do");
		tag.doStartTag();

		assertThat(tag.getOffset()).isEqualTo(0);
		assertThat(tag.getPageNumber()).isEqualTo(0);
	}

	@Test
	void addParamWithNullAndNoRequestValuesDoesNothing() throws JspException {
		TestContext ctx = TestContext.create("0");
		Mockito.when(ctx.request.getParameterValues("filter")).thenReturn(null);

		PagerTag tag = new PagerTag();
		tag.setPageContext(ctx.pageContext);
		tag.setUrl("newsList.do");
		tag.doStartTag();

		tag.addParam("filter", null);
		assertThat(tag.getPageUrl(0))
				.isEqualTo("newsList.do?pager.offset=0");
	}

	@Test
	void addParamAppendsMultipleParameters() throws JspException {
		TestContext ctx = TestContext.create("0");
		Mockito.when(ctx.request.getCharacterEncoding()).thenReturn("UTF-8");

		PagerTag tag = new PagerTag();
		tag.setPageContext(ctx.pageContext);
		tag.setUrl("newsList.do");
		tag.doStartTag();

		tag.addParam("q", "hello");
		tag.addParam("lang", "en");

		assertThat(tag.getPageUrl(0))
				.isEqualTo("newsList.do?q=hello&lang=en&pager.offset=0");
	}

	@Test
	void addParamFailsOnUnsupportedEncoding() throws JspException {
		TestContext ctx = TestContext.create("0");
		Mockito.when(ctx.request.getCharacterEncoding())
				.thenReturn("NO_SUCH_ENCODING");

		PagerTag tag = new PagerTag();
		tag.setPageContext(ctx.pageContext);
		tag.setUrl("newsList.do");
		tag.doStartTag();

		assertThatThrownBy(() -> tag.addParam("q", "x"))
				.isInstanceOf(RuntimeException.class);
	}

	@Test
	void requestUriWithoutQueryIsUsedWhenUrlMissing() throws JspException {
		HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
		Mockito.when(request.getRequestURI()).thenReturn("/news/list");
		Mockito.when(request.getParameter("pager.offset")).thenReturn("0");
		Mockito.when(request.getCharacterEncoding()).thenReturn("UTF-8");

		TestContext ctx = TestContext.create("0");
		Mockito.when(ctx.pageContext.getRequest()).thenReturn(request);

		PagerTag tag = new PagerTag();
		tag.setPageContext(ctx.pageContext);
		tag.setScope("request");
		tag.doStartTag();

		assertThat(tag.getPageUrl(0)).isEqualTo("/news/list?pager.offset=0");
	}

	@Test
	void requestScopeRestoresExportedValues() throws JspException {
		TestContext ctx = TestContext.create("10");
		ctx.requestAttributes.put("pager", "oldPager");
		ctx.requestAttributes.put("pageOffset", Integer.valueOf(1));
		ctx.requestAttributes.put("pageNumber", Integer.valueOf(2));

		PagerTag tag = new PagerTag();
		tag.setPageContext(ctx.pageContext);
		tag.setUrl("newsList.do");
		tag.setExport("pageOffset,pageNumber");
		tag.setScope("request");
		tag.doStartTag();

		assertThat(ctx.requestAttributes.get("pageOffset"))
				.isEqualTo(Integer.valueOf(10));
		assertThat(ctx.requestAttributes.get("pageNumber"))
				.isEqualTo(Integer.valueOf(2));

		tag.doEndTag();
		assertThat(ctx.requestAttributes.get("pager")).isEqualTo("oldPager");
		assertThat(ctx.requestAttributes.get("pageOffset"))
				.isEqualTo(Integer.valueOf(1));
		assertThat(ctx.requestAttributes.get("pageNumber"))
				.isEqualTo(Integer.valueOf(2));
	}

	@Test
	void requestScopeWithoutExportStillRestoresPager() throws JspException {
		TestContext ctx = TestContext.create("0");
		ctx.requestAttributes.put("pager", "old");

		PagerTag tag = new PagerTag();
		tag.setPageContext(ctx.pageContext);
		tag.setUrl("newsList.do");
		tag.setScope("request");
		tag.doStartTag();

		assertThat(ctx.requestAttributes.get("pager")).isSameAs(tag);
		tag.doEndTag();
		assertThat(ctx.requestAttributes.get("pager")).isEqualTo("old");
	}

	@Test
	void pageScopeDoesNotTouchRequestAttributes() throws JspException {
		TestContext ctx = TestContext.create("0");
		ctx.requestAttributes.put("pageOffset", Integer.valueOf(5));

		PagerTag tag = new PagerTag();
		tag.setPageContext(ctx.pageContext);
		tag.setUrl("newsList.do");
		tag.setExport("pageOffset");
		tag.setScope("page");
		tag.doStartTag();

		assertThat(ctx.requestAttributes.get("pageOffset")).isEqualTo(Integer.valueOf(5));
		tag.doEndTag();
		assertThat(ctx.requestAttributes.get("pageOffset")).isEqualTo(Integer.valueOf(5));
	}

	@Test
	void doStartTagReusesUriBuffer() throws JspException {
		TestContext ctx = TestContext.create("0");

		PagerTag tag = new PagerTag();
		tag.setPageContext(ctx.pageContext);
		tag.setUrl("newsList.do");
		tag.setScope("request");
		tag.doStartTag();
		String firstUrl = tag.getPageUrl(0);

		tag.doStartTag();
		String secondUrl = tag.getPageUrl(0);

		assertThat(firstUrl).isEqualTo("newsList.do?pager.offset=0");
		assertThat(secondUrl).isEqualTo(firstUrl);
	}

	@Test
	void noOffsetParamKeepsDefaults() throws JspException {
		TestContext ctx = TestContext.create(null);

		PagerTag tag = new PagerTag();
		tag.setPageContext(ctx.pageContext);
		tag.setUrl("newsList.do");
		tag.doStartTag();

		assertThat(tag.getOffset()).isEqualTo(0);
		assertThat(tag.getPageNumber()).isEqualTo(0);
	}

	@Test
	void getFirstIndexPageWhenPagesWithinLimit() throws JspException {
		TestContext ctx = TestContext.create("0");
		PagerTag tag = new PagerTag();
		tag.setPageContext(ctx.pageContext);
		tag.setUrl("newsList.do");
		tag.setItems(30);
		tag.setMaxPageItems(10);
		tag.setMaxIndexPages(10);
		tag.doStartTag();

		assertThat(tag.getFirstIndexPage()).isEqualTo(0);
	}

	@Test
	void getFirstIndexPageAdjustsWhenNearEnd() throws JspException {
		TestContext ctx = TestContext.create("110");

		PagerTag tag = new PagerTag();
		tag.setPageContext(ctx.pageContext);
		tag.setUrl("newsList.do");
		tag.setIndex("center");
		tag.setItems(120);
		tag.setMaxPageItems(10);
		tag.setMaxIndexPages(5);
		tag.doStartTag();

		int firstPage = tag.getFirstIndexPage();
		assertThat(firstPage).isEqualTo(7);
		assertThat(tag.getLastIndexPage(firstPage)).isEqualTo(11);
	}

	@Test
	void forwardAndHalfFullIndexModes() throws JspException {
		TestContext ctxForward = TestContext.create("20");
		PagerTag forward = new PagerTag();
		forward.setPageContext(ctxForward.pageContext);
		forward.setUrl("newsList.do");
		forward.setIndex("forward");
		forward.setItems(120);
		forward.setMaxPageItems(10);
		forward.setMaxIndexPages(5);
		forward.doStartTag();
		assertThat(forward.getFirstIndexPage()).isEqualTo(3);

		TestContext ctxHalf = TestContext.create("10");
		PagerTag half = new PagerTag();
		half.setPageContext(ctxHalf.pageContext);
		half.setUrl("newsList.do");
		half.setIndex("half-full");
		half.setItems(120);
		half.setMaxPageItems(10);
		half.setMaxIndexPages(6);
		half.doStartTag();
		int firstPage = half.getFirstIndexPage();
		assertThat(firstPage).isEqualTo(0);
		assertThat(half.getLastIndexPage(firstPage)).isEqualTo(3);
	}

	@Test
	void forwardIndexClampsToPageCount() throws JspException {
		TestContext ctx = TestContext.create("100");
		PagerTag tag = new PagerTag();
		tag.setPageContext(ctx.pageContext);
		tag.setUrl("newsList.do");
		tag.setIndex("forward");
		tag.setItems(30);
		tag.setMaxPageItems(10);
		tag.setMaxIndexPages(5);
		tag.doStartTag();

		assertThat(tag.getFirstIndexPage()).isEqualTo(3);
	}

	@Test
	void hasPageHandlesNegativeAndBeyondRange() throws JspException {
		TestContext ctx = TestContext.create("0");
		PagerTag tag = new PagerTag();
		tag.setPageContext(ctx.pageContext);
		tag.setUrl("newsList.do");
		tag.setItems(15);
		tag.setMaxPageItems(10);
		tag.doStartTag();

		assertThat(tag.hasPage(-1)).isFalse();
		assertThat(tag.hasPage(2)).isFalse();
	}

	@Test
	void doEndTagDropsLargeUriBuffer() throws Exception {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < 1500; i++) {
			builder.append('a');
		}

		TestContext ctx = TestContext.create("0");
		PagerTag tag = new PagerTag();
		tag.setPageContext(ctx.pageContext);
		tag.setUrl(builder.toString());
		tag.setScope("request");
		tag.doStartTag();

		Field uriField = PagerTag.class.getDeclaredField("uri");
		uriField.setAccessible(true);
		Object before = uriField.get(tag);
		assertThat(before).isNotNull();

		tag.doEndTag();
		Object after = uriField.get(tag);
		assertThat(after).isNull();
	}

	@Test
	void releaseResetsDefaults() throws JspException {
		PagerTag tag = new PagerTag();
		tag.setUrl("newsList.do");
		tag.setItems(10);
		tag.setMaxItems(20);
		tag.setMaxPageItems(5);
		tag.setMaxIndexPages(3);
		tag.setIsOffset(true);
		tag.setScope("request");
		tag.release();

		assertThat(tag.getUrl()).isNull();
		assertThat(tag.getItems()).isEqualTo(0);
		assertThat(tag.getMaxItems()).isEqualTo(Integer.MAX_VALUE);
		assertThat(tag.getMaxPageItems()).isEqualTo(10);
		assertThat(tag.getMaxIndexPages()).isEqualTo(10);
		assertThat(tag.getIsOffset()).isFalse();
		assertThat(tag.getScope()).isNull();
	}
}
