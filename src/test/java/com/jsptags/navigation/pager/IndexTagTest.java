package com.jsptags.navigation.pager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import org.junit.jupiter.api.Test;

class IndexTagTest {

	@Test
	void doStartTagSetsExportsAndSkipsWhenIndexNotNeeded() throws Exception {
		TestContext ctx = TestContext.create("0");

		PagerTag pager = new PagerTag();
		pager.setPageContext(ctx.pageContext);
		pager.setUrl("newsList.do");
		pager.setItems(10);
		pager.setMaxPageItems(10);
		pager.setScope("request");
		pager.doStartTag();

		ctx.pageAttributes.put("itemCount", Integer.valueOf(99));
		ctx.pageAttributes.put("pageCount", Integer.valueOf(88));

		IndexTag tag = new IndexTag();
		tag.setPageContext(ctx.pageContext);
		tag.setExport("itemCount,pageCount");

		int result = tag.doStartTag();
		assertThat(result).isEqualTo(TagSupport.SKIP_BODY);
		assertThat(ctx.pageAttributes.get("itemCount")).isEqualTo(Integer.valueOf(10));
		assertThat(ctx.pageAttributes.get("pageCount")).isEqualTo(Integer.valueOf(1));

		tag.doEndTag();
		assertThat(ctx.pageAttributes.get("itemCount")).isEqualTo(Integer.valueOf(99));
		assertThat(ctx.pageAttributes.get("pageCount")).isEqualTo(Integer.valueOf(88));
	}

	@Test
	void doStartTagWithoutExportEvaluatesWhenIndexNeeded() throws Exception {
		TestContext ctx = TestContext.create("10");

		PagerTag pager = new PagerTag();
		pager.setPageContext(ctx.pageContext);
		pager.setUrl("newsList.do");
		pager.setItems(30);
		pager.setMaxPageItems(10);
		pager.setScope("request");
		pager.doStartTag();

		IndexTag tag = new IndexTag();
		tag.setPageContext(ctx.pageContext);

		int result = tag.doStartTag();
		assertThat(result).isEqualTo(TagSupport.EVAL_BODY_INCLUDE);
		assertThat(tag.doEndTag()).isEqualTo(TagSupport.EVAL_PAGE);
	}

	@Test
	void exportOnlyItemCountSkipsPageCountUpdates() throws Exception {
		TestContext ctx = TestContext.create("0");

		PagerTag pager = new PagerTag();
		pager.setPageContext(ctx.pageContext);
		pager.setUrl("newsList.do");
		pager.setItems(30);
		pager.setMaxPageItems(10);
		pager.setScope("request");
		pager.doStartTag();

		ctx.pageAttributes.put("itemCount", Integer.valueOf(99));
		ctx.pageAttributes.put("pageCount", Integer.valueOf(88));

		IndexTag tag = new IndexTag();
		tag.setPageContext(ctx.pageContext);
		tag.setExport("itemCount");

		int result = tag.doStartTag();
		assertThat(result).isEqualTo(TagSupport.EVAL_BODY_INCLUDE);
		assertThat(ctx.pageAttributes.get("itemCount")).isEqualTo(Integer.valueOf(30));
		assertThat(ctx.pageAttributes.get("pageCount")).isEqualTo(Integer.valueOf(88));

		tag.doEndTag();
		assertThat(ctx.pageAttributes.get("itemCount")).isEqualTo(Integer.valueOf(99));
		assertThat(ctx.pageAttributes.get("pageCount")).isEqualTo(Integer.valueOf(88));
	}

	@Test
	void exportOnlyPageCountSkipsItemCountUpdates() throws Exception {
		TestContext ctx = TestContext.create("0");

		PagerTag pager = new PagerTag();
		pager.setPageContext(ctx.pageContext);
		pager.setUrl("newsList.do");
		pager.setItems(30);
		pager.setMaxPageItems(10);
		pager.setScope("request");
		pager.doStartTag();

		ctx.pageAttributes.put("itemCount", Integer.valueOf(99));
		ctx.pageAttributes.put("pageCount", Integer.valueOf(88));

		IndexTag tag = new IndexTag();
		tag.setPageContext(ctx.pageContext);
		tag.setExport("pageCount");

		int result = tag.doStartTag();
		assertThat(result).isEqualTo(TagSupport.EVAL_BODY_INCLUDE);
		assertThat(ctx.pageAttributes.get("itemCount")).isEqualTo(Integer.valueOf(99));
		assertThat(ctx.pageAttributes.get("pageCount")).isEqualTo(Integer.valueOf(3));

		tag.doEndTag();
		assertThat(ctx.pageAttributes.get("itemCount")).isEqualTo(Integer.valueOf(99));
		assertThat(ctx.pageAttributes.get("pageCount")).isEqualTo(Integer.valueOf(88));
	}

	@Test
	void setExportIgnoresSameInstance() throws JspException {
		IndexTag tag = new IndexTag();
		String expr = "itemCount";
		tag.setExport(expr);
		tag.setExport(expr);
		assertThat(tag.getExport()).isEqualTo(expr);
	}

	@Test
	void setExportRejectsInvalidExpression() {
		IndexTag tag = new IndexTag();
		assertThatThrownBy(() -> tag.setExport("="))
				.isInstanceOf(JspException.class);
	}

	@Test
	void releaseResetsExport() {
		IndexTag tag = new IndexTag();
		tag.release();
		assertThat(tag.getExport()).isNull();
	}
}
