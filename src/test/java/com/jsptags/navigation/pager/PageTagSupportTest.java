package com.jsptags.navigation.pager;

import static org.assertj.core.api.Assertions.assertThat;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import org.junit.jupiter.api.Test;

class PageTagSupportTest {

	private static final class TestPageTagSupport extends PageTagSupport {
		private static final long serialVersionUID = 1L;

		void callSetPageAttributes(int page) {
			setPageAttributes(page);
		}

		void callSetOffsetAttributes(int offset) {
			setOffsetAttributes(offset);
		}

		void callRemoveAttributes() {
			removeAttributes();
		}
	}

	@Test
	void setPageAttributesUsesDefaultNames() throws JspException {
		TestContext ctx = TestContext.create("0");

		PagerTag pager = new PagerTag();
		pager.setPageContext(ctx.pageContext);
		pager.setUrl("newsList.do");
		pager.setItems(25);
		pager.setMaxPageItems(10);
		pager.setScope("request");
		pager.doStartTag();

		TestPageTagSupport tag = new TestPageTagSupport();
		tag.setPageContext(ctx.pageContext);
		tag.doStartTag();

		tag.callSetPageAttributes(1);
		assertThat(ctx.pageAttributes.get("pageUrl"))
				.isEqualTo("newsList.do?pager.offset=10");
		assertThat(ctx.pageAttributes.get("pageNumber"))
				.isEqualTo(Integer.valueOf(2));

		assertThat(tag.doEndTag()).isEqualTo(TagSupport.EVAL_PAGE);
	}

	@Test
	void setOffsetAttributesUsesCustomNamesAndCalculatesItems() throws JspException {
		TestContext ctx = TestContext.create("0");

		PagerTag pager = new PagerTag();
		pager.setPageContext(ctx.pageContext);
		pager.setUrl("newsList.do");
		pager.setItems(25);
		pager.setMaxPageItems(10);
		pager.setScope("request");
		pager.doStartTag();

		TestPageTagSupport tag = new TestPageTagSupport();
		tag.setPageContext(ctx.pageContext);
		tag.setExport("link=pageUrl,num=pageNumber,firstIdx=firstItem,lastIdx=lastItem");
		tag.doStartTag();

		tag.callSetOffsetAttributes(10);
		assertThat(ctx.pageAttributes.get("link"))
				.isEqualTo("newsList.do?pager.offset=10");
		assertThat(ctx.pageAttributes.get("num"))
				.isEqualTo(Integer.valueOf(2));
		assertThat(ctx.pageAttributes.get("firstIdx"))
				.isEqualTo(Integer.valueOf(11));
		assertThat(ctx.pageAttributes.get("lastIdx"))
				.isEqualTo(Integer.valueOf(20));

		tag.callRemoveAttributes();
		assertThat(ctx.pageAttributes.containsKey("link")).isFalse();
		assertThat(ctx.pageAttributes.containsKey("num")).isFalse();
		assertThat(ctx.pageAttributes.containsKey("firstIdx")).isFalse();
		assertThat(ctx.pageAttributes.containsKey("lastIdx")).isFalse();

		assertThat(tag.doEndTag()).isEqualTo(TagSupport.EVAL_PAGE);
	}
}
