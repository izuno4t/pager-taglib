package com.jsptags.navigation.pager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import javax.servlet.jsp.JspException;

import org.junit.jupiter.api.Test;

class SkipJumpTagTest {

	@Test
	void nextAndPrevTagsRespectPaging() throws JspException {
		TestContext ctx = TestContext.create("0");

		PagerTag pager = new PagerTag();
		pager.setPageContext(ctx.pageContext);
		pager.setUrl("newsList.do");
		pager.setItems(25);
		pager.setMaxPageItems(10);
		pager.setScope("request");
		pager.doStartTag();

		NextTag next = new NextTag();
		next.setPageContext(ctx.pageContext);
		assertThat(next.doStartTag()).isEqualTo(NextTag.EVAL_BODY_INCLUDE);
		assertThat(ctx.pageAttributes.get("pageUrl"))
				.isEqualTo("newsList.do?pager.offset=10");

		PrevTag prev = new PrevTag();
		prev.setPageContext(ctx.pageContext);
		assertThat(prev.doStartTag()).isEqualTo(PrevTag.SKIP_BODY);
	}

	@Test
	void skipTagHandlesIfNullAndPaging() throws JspException {
		TestContext ctx = TestContext.create("0");

		PagerTag pager = new PagerTag();
		pager.setPageContext(ctx.pageContext);
		pager.setUrl("newsList.do");
		pager.setItems(25);
		pager.setMaxPageItems(10);
		pager.setScope("request");
		pager.doStartTag();

		SkipTag skip = new SkipTag();
		skip.setPageContext(ctx.pageContext);
		skip.setPages(1);
		assertThat(skip.doStartTag()).isEqualTo(SkipTag.EVAL_BODY_INCLUDE);
		assertThat(ctx.pageAttributes.get("pageNumber"))
				.isEqualTo(Integer.valueOf(2));

		SkipTag missing = new SkipTag();
		missing.setPageContext(ctx.pageContext);
		missing.setPages(10);
		missing.setIfNull(true);
		ctx.pageAttributes.put("pageUrl", "old");
		ctx.pageAttributes.put("pageNumber", Integer.valueOf(99));
		assertThat(missing.doStartTag()).isEqualTo(SkipTag.EVAL_BODY_INCLUDE);
		assertThat(ctx.pageAttributes.containsKey("pageUrl")).isFalse();
		assertThat(ctx.pageAttributes.containsKey("pageNumber")).isFalse();
	}

	@Test
	void jumpTagsRespectUnless() throws JspException {
		TestContext ctx = TestContext.create("0");

		PagerTag pager = new PagerTag();
		pager.setPageContext(ctx.pageContext);
		pager.setUrl("newsList.do");
		pager.setItems(25);
		pager.setMaxPageItems(10);
		pager.setMaxIndexPages(5);
		pager.setScope("request");
		pager.doStartTag();

		FirstTag first = new FirstTag();
		first.setPageContext(ctx.pageContext);
		first.setUnless(JumpTagSupport.CURRENT);
		assertThat(first.doStartTag()).isEqualTo(FirstTag.SKIP_BODY);

		LastTag last = new LastTag();
		last.setPageContext(ctx.pageContext);
		last.setUnless(JumpTagSupport.INDEXED);
		assertThat(last.doStartTag()).isEqualTo(LastTag.SKIP_BODY);
	}

	@Test
	void jumpTagRejectsInvalidUnless() {
		FirstTag first = new FirstTag();
		assertThatThrownBy(() -> first.setUnless("nope"))
				.isInstanceOf(JspException.class);
	}
}
