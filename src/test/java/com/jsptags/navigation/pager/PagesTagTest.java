package com.jsptags.navigation.pager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyContent;

import org.junit.jupiter.api.Test;

class PagesTagTest {

	@Test
	void iteratesPagesAndWritesBody() throws Exception {
		TestContext ctx = TestContext.create("0");

		PagerTag pager = new PagerTag();
		pager.setPageContext(ctx.pageContext);
		pager.setUrl("newsList.do");
		pager.setItems(30);
		pager.setMaxPageItems(10);
		pager.setMaxIndexPages(5);
		pager.setScope("request");
		pager.doStartTag();

		PagesTag tag = new PagesTag();
		tag.setPageContext(ctx.pageContext);

		BodyContent bodyContent = mock(BodyContent.class);
		JspWriter enclosingWriter = mock(JspWriter.class);
		when(bodyContent.getEnclosingWriter()).thenReturn(enclosingWriter);
		doNothing().when(bodyContent).writeOut(enclosingWriter);

		int start = tag.doStartTag();
		assertThat(start).isEqualTo(PagesTag.EVAL_BODY_TAG);

		tag.setBodyContent(bodyContent);
		tag.doInitBody();
		assertThat(ctx.pageAttributes.get("pageNumber"))
				.isEqualTo(Integer.valueOf(1));

		assertThat(tag.doAfterBody()).isEqualTo(PagesTag.EVAL_BODY_BUFFERED);
		assertThat(ctx.pageAttributes.get("pageNumber"))
				.isEqualTo(Integer.valueOf(2));

		assertThat(tag.doAfterBody()).isEqualTo(PagesTag.EVAL_BODY_BUFFERED);
		assertThat(ctx.pageAttributes.get("pageNumber"))
				.isEqualTo(Integer.valueOf(3));

		assertThat(tag.doAfterBody()).isEqualTo(PagesTag.SKIP_BODY);
		verify(bodyContent).writeOut(enclosingWriter);

		assertThat(tag.doEndTag()).isEqualTo(PagesTag.EVAL_PAGE);
		tag.release();
	}

	@Test
	void returnsSkipBodyWhenNoPages() throws Exception {
		TestContext ctx = TestContext.create("0");

		PagerTag pager = new PagerTag();
		pager.setPageContext(ctx.pageContext);
		pager.setUrl("newsList.do");
		pager.setItems(0);
		pager.setMaxPageItems(10);
		pager.setMaxIndexPages(5);
		pager.setScope("request");
		pager.doStartTag();

		PagesTag tag = new PagesTag();
		tag.setPageContext(ctx.pageContext);

		int start = tag.doStartTag();
		assertThat(start).isEqualTo(PagesTag.SKIP_BODY);
	}

	@Test
	void doAfterBodyThrowsWhenWriteOutFails() throws Exception {
		TestContext ctx = TestContext.create("0");

		PagerTag pager = new PagerTag();
		pager.setPageContext(ctx.pageContext);
		pager.setUrl("newsList.do");
		pager.setItems(10);
		pager.setMaxPageItems(10);
		pager.setMaxIndexPages(5);
		pager.setScope("request");
		pager.doStartTag();

		PagesTag tag = new PagesTag();
		tag.setPageContext(ctx.pageContext);

		BodyContent bodyContent = mock(BodyContent.class);
		JspWriter enclosingWriter = mock(JspWriter.class);
		when(bodyContent.getEnclosingWriter()).thenReturn(enclosingWriter);
		org.mockito.Mockito.doThrow(new java.io.IOException("fail"))
				.when(bodyContent).writeOut(enclosingWriter);

		assertThat(tag.doStartTag()).isEqualTo(PagesTag.EVAL_BODY_TAG);
		tag.setBodyContent(bodyContent);
		tag.doInitBody();

		assertThatThrownBy(() -> tag.doAfterBody())
				.isInstanceOf(JspException.class);
	}
}
