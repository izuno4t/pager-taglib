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
	void nextTagIfNullRemovesAttributesWhenMissing() throws JspException {
		TestContext ctx = TestContext.create("0");

		PagerTag pager = new PagerTag();
		pager.setPageContext(ctx.pageContext);
		pager.setUrl("newsList.do");
		pager.setItems(10);
		pager.setMaxPageItems(10);
		pager.setScope("request");
		pager.doStartTag();

		NextTag next = new NextTag();
		next.setPageContext(ctx.pageContext);
		next.setIfNull(true);
		ctx.pageAttributes.put("pageUrl", "old");
		ctx.pageAttributes.put("pageNumber", Integer.valueOf(99));
		assertThat(next.doStartTag()).isEqualTo(NextTag.EVAL_BODY_INCLUDE);
		assertThat(ctx.pageAttributes.containsKey("pageUrl")).isFalse();
		assertThat(ctx.pageAttributes.containsKey("pageNumber")).isFalse();
	}

	@Test
	void prevTagProvidesAttributesWhenAvailable() throws JspException {
		TestContext ctx = TestContext.create("10");

		PagerTag pager = new PagerTag();
		pager.setPageContext(ctx.pageContext);
		pager.setUrl("newsList.do");
		pager.setItems(30);
		pager.setMaxPageItems(10);
		pager.setScope("request");
		pager.doStartTag();

		PrevTag prev = new PrevTag();
		prev.setPageContext(ctx.pageContext);
		assertThat(prev.doStartTag()).isEqualTo(PrevTag.EVAL_BODY_INCLUDE);
		assertThat(ctx.pageAttributes.get("pageUrl"))
				.isEqualTo("newsList.do?pager.offset=0");
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
	void skipTagSkipsWhenPageMissingAndIfNullFalse() throws JspException {
		TestContext ctx = TestContext.create("0");
		ctx.pageAttributes.put("pageUrl", "old");
		ctx.pageAttributes.put("pageNumber", Integer.valueOf(9));

		PagerTag pager = new PagerTag();
		pager.setPageContext(ctx.pageContext);
		pager.setUrl("newsList.do");
		pager.setItems(10);
		pager.setMaxPageItems(10);
		pager.setScope("request");
		pager.doStartTag();

		SkipTag skip = new SkipTag();
		skip.setPageContext(ctx.pageContext);
		skip.setPages(1);
		assertThat(skip.doStartTag()).isEqualTo(SkipTag.SKIP_BODY);
		assertThat(ctx.pageAttributes.get("pageUrl")).isEqualTo("old");
		assertThat(ctx.pageAttributes.get("pageNumber")).isEqualTo(Integer.valueOf(9));
	}

	@Test
	void skipTagAccessorAndReleaseResetState() {
		SkipTag tag = new SkipTag();
		tag.setIfNull(true);
		tag.setPages(2);

		assertThat(tag.getIfNull()).isTrue();
		assertThat(tag.getPages()).isEqualTo(2);

		tag.release();
		assertThat(tag.getIfNull()).isFalse();
		assertThat(tag.getPages()).isEqualTo(0);
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
	void jumpTagsRunWhenUnlessNotSet() throws JspException {
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
		assertThat(first.doStartTag()).isEqualTo(FirstTag.EVAL_BODY_INCLUDE);

		LastTag last = new LastTag();
		last.setPageContext(ctx.pageContext);
		assertThat(last.doStartTag()).isEqualTo(LastTag.EVAL_BODY_INCLUDE);
	}

	@Test
	void jumpTagsRunWhenUnlessDoesNotMatch() throws JspException {
		TestContext ctx = TestContext.create("10");

		PagerTag pager = new PagerTag();
		pager.setPageContext(ctx.pageContext);
		pager.setUrl("newsList.do");
		pager.setItems(50);
		pager.setMaxPageItems(10);
		pager.setMaxIndexPages(3);
		pager.setScope("request");
		pager.doStartTag();

		FirstTag first = new FirstTag();
		first.setPageContext(ctx.pageContext);
		first.setUnless(JumpTagSupport.CURRENT);
		assertThat(first.doStartTag()).isEqualTo(FirstTag.EVAL_BODY_INCLUDE);
		assertThat(ctx.pageAttributes.get("pageNumber"))
				.isEqualTo(Integer.valueOf(1));

		LastTag last = new LastTag();
		last.setPageContext(ctx.pageContext);
		last.setUnless(JumpTagSupport.INDEXED);
		assertThat(last.doStartTag()).isEqualTo(LastTag.EVAL_BODY_INCLUDE);
		assertThat(ctx.pageAttributes.get("pageUrl"))
				.isEqualTo("newsList.do?pager.offset=40");
	}

	@Test
	void jumpTagStoresUnlessAndReleaseClearsIt() throws JspException {
		FirstTag first = new FirstTag();
		first.setUnless(JumpTagSupport.CURRENT);
		assertThat(first.getUnless()).isEqualTo(JumpTagSupport.CURRENT);

		first.release();
		assertThat(first.getUnless()).isNull();
	}

	@Test
	void jumpTagAcceptsNullUnless() throws JspException {
		FirstTag first = new FirstTag();
		first.setUnless(null);
		assertThat(first.getUnless()).isNull();
	}

	@Test
	void jumpTagSkipsWhenIndexedRangeContainsJumpPage() throws JspException {
		TestContext ctx = TestContext.create("0");

		PagerTag pager = new PagerTag();
		pager.setPageContext(ctx.pageContext);
		pager.setUrl("newsList.do");
		pager.setItems(25);
		pager.setMaxPageItems(10);
		pager.setMaxIndexPages(5);
		pager.setScope("request");
		pager.doStartTag();

		LastTag last = new LastTag();
		last.setPageContext(ctx.pageContext);
		last.setUnless(JumpTagSupport.INDEXED);
		assertThat(last.doStartTag()).isEqualTo(LastTag.SKIP_BODY);
	}

	@Test
	void jumpTagRunsWhenIndexedRangeStartsAfterJumpPage() throws JspException {
		TestContext ctx = TestContext.create("10");

		PagerTag pager = new PagerTag();
		pager.setPageContext(ctx.pageContext);
		pager.setUrl("newsList.do");
		pager.setIndex("forward");
		pager.setItems(50);
		pager.setMaxPageItems(10);
		pager.setMaxIndexPages(3);
		pager.setScope("request");
		pager.doStartTag();

		FirstTag first = new FirstTag();
		first.setPageContext(ctx.pageContext);
		first.setUnless(JumpTagSupport.INDEXED);

		assertThat(first.doStartTag()).isEqualTo(FirstTag.EVAL_BODY_INCLUDE);
		assertThat(ctx.pageAttributes.get("pageUrl"))
				.isEqualTo("newsList.do?pager.offset=0");
	}

	@Test
	void jumpTagRejectsInvalidUnless() {
		FirstTag first = new FirstTag();
		assertThatThrownBy(() -> first.setUnless("nope"))
				.isInstanceOf(JspException.class);
	}
}
