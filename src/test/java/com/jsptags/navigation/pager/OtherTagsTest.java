package com.jsptags.navigation.pager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import org.junit.jupiter.api.Test;

class OtherTagsTest {

	private static final class TestPagerTagSupport extends PagerTagSupport {
		private static final long serialVersionUID = 1L;
	}

	@Test
	void pagerTagSupportRequiresPagerInRequest() {
		TestContext ctx = TestContext.create("0");
		TestPagerTagSupport tag = new TestPagerTagSupport();
		tag.setPageContext(ctx.pageContext);
		tag.setId("missing");

		assertThatThrownBy(() -> tag.doStartTag())
				.isInstanceOf(JspException.class);
	}

	@Test
	void pagerTagSupportRejectsNonPagerAttribute() {
		TestContext ctx = TestContext.create("0");
		ctx.requestAttributes.put("pager", "notPager");

		TestPagerTagSupport tag = new TestPagerTagSupport();
		tag.setPageContext(ctx.pageContext);
		tag.setId("pager");

		assertThatThrownBy(() -> tag.doStartTag())
				.isInstanceOf(JspException.class);
	}

	@Test
	void pagerTagSupportUsesDefaultPagerWhenIdMissing() throws JspException {
		TestContext ctx = TestContext.create("0");

		PagerTag pager = new PagerTag();
		pager.setPageContext(ctx.pageContext);
		pager.setUrl("newsList.do");
		pager.setScope("request");
		pager.doStartTag();

		TestPagerTagSupport tag = new TestPagerTagSupport();
		tag.setPageContext(ctx.pageContext);

		assertThat(tag.doStartTag()).isEqualTo(TagSupport.EVAL_BODY_INCLUDE);
		assertThat(tag.doEndTag()).isEqualTo(TagSupport.EVAL_PAGE);
	}

	@Test
	void pagerTagSupportUsesPagerWithExplicitId() throws JspException {
		TestContext ctx = TestContext.create("0");

		PagerTag pager = new PagerTag();
		pager.setPageContext(ctx.pageContext);
		pager.setUrl("newsList.do");
		pager.setScope("request");
		pager.setId("custom");
		pager.doStartTag();

		TestPagerTagSupport tag = new TestPagerTagSupport();
		tag.setPageContext(ctx.pageContext);
		tag.setId("custom");

		assertThat(tag.doStartTag()).isEqualTo(TagSupport.EVAL_BODY_INCLUDE);
		assertThat(tag.doEndTag()).isEqualTo(TagSupport.EVAL_PAGE);
	}

	@Test
	void pagerTagSupportUsesAncestorWhenIdNull() throws JspException {
		TestContext ctx = TestContext.create("0");

		PagerTag pager = new PagerTag();
		pager.setPageContext(ctx.pageContext);
		pager.setUrl("newsList.do");
		pager.setScope("request");
		pager.doStartTag();

		ParamTag param = new ParamTag();
		param.setPageContext(ctx.pageContext);
		param.setParent(pager);
		param.setName("q");
		param.setValue("hello");

		assertThat(param.doStartTag()).isEqualTo(TagSupport.EVAL_BODY_INCLUDE);
		assertThat(pager.getPageUrl(0))
				.isEqualTo("newsList.do?q=hello&pager.offset=0");
	}

	@Test
	void pagerTagSupportFailsWhenNoPagerFound() {
		TestContext ctx = TestContext.create("0");

		TestPagerTagSupport tag = new TestPagerTagSupport();
		tag.setPageContext(ctx.pageContext);
		tag.setId(null);

		assertThatThrownBy(() -> tag.doStartTag())
				.isInstanceOf(JspException.class);
	}

	@Test
	void paramTagAddsParameterToPager() throws JspException {
		TestContext ctx = TestContext.create("0");

		PagerTag pager = new PagerTag();
		pager.setPageContext(ctx.pageContext);
		pager.setUrl("newsList.do");
		pager.setScope("request");
		pager.doStartTag();

		ParamTag param = new ParamTag();
		param.setPageContext(ctx.pageContext);
		param.setName("q");
		param.setValue("hello");
		assertThat(param.doStartTag()).isEqualTo(TagSupport.EVAL_BODY_INCLUDE);

		assertThat(pager.getPageUrl(0))
				.isEqualTo("newsList.do?q=hello&pager.offset=0");
	}

	@Test
	void paramTagReleaseResetsFields() {
		ParamTag param = new ParamTag();
		param.setName("q");
		param.setValue("hello");
		param.release();
		assertThat(param.getName()).isNull();
		assertThat(param.getValue()).isNull();
	}

	@Test
	void itemTagAdvancesItemCount() throws JspException {
		TestContext ctx = TestContext.create("0");

		PagerTag pager = new PagerTag();
		pager.setPageContext(ctx.pageContext);
		pager.setUrl("newsList.do");
		pager.setMaxItems(2);
		pager.setMaxPageItems(1);
		pager.setScope("request");
		pager.doStartTag();

		ItemTag item = new ItemTag();
		item.setPageContext(ctx.pageContext);
		assertThat(item.doStartTag()).isEqualTo(TagSupport.EVAL_BODY_INCLUDE);
		assertThat(item.doStartTag()).isEqualTo(TagSupport.SKIP_BODY);
	}
}
