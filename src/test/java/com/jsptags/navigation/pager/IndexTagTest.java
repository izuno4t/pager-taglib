package com.jsptags.navigation.pager;

import static org.assertj.core.api.Assertions.assertThat;

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
}
