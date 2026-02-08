package com.jsptags.navigation.pager;

import static org.assertj.core.api.Assertions.assertThat;

import javax.servlet.jsp.JspException;

import org.junit.jupiter.api.Test;

class PageTagTest {

	@Test
	void pageTagUsesOffsetAttributes() throws JspException {
		TestContext ctx = TestContext.create("10");

		PagerTag pager = new PagerTag();
		pager.setPageContext(ctx.pageContext);
		pager.setUrl("newsList.do");
		pager.setItems(25);
		pager.setMaxPageItems(10);
		pager.setScope("request");
		pager.doStartTag();

		PageTag tag = new PageTag();
		tag.setPageContext(ctx.pageContext);

		assertThat(tag.doStartTag()).isEqualTo(PageTag.EVAL_BODY_INCLUDE);
		assertThat(ctx.pageAttributes.get("pageUrl"))
				.isEqualTo("newsList.do?pager.offset=10");
		assertThat(ctx.pageAttributes.get("pageNumber"))
				.isEqualTo(Integer.valueOf(2));
	}
}

