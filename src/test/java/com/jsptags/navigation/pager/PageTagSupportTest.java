package com.jsptags.navigation.pager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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

	@Test
	void setPageAttributesUsesCustomNamesWhenFieldsAreMissing() throws JspException {
		TestContext ctx = TestContext.create("0");

		PagerTag pager = new PagerTag();
		pager.setPageContext(ctx.pageContext);
		pager.setUrl("newsList.do");
		pager.setItems(5);
		pager.setMaxPageItems(2);
		pager.setScope("request");
		pager.doStartTag();

		TestPageTagSupport tag = new TestPageTagSupport();
		tag.setPageContext(ctx.pageContext);
		tag.setExport("link=pageUrl");
		tag.doStartTag();

		tag.callSetPageAttributes(1);
		assertThat(ctx.pageAttributes.get("link"))
				.isEqualTo("newsList.do?pager.offset=2");

		assertThat(ctx.pageAttributes.containsKey("pageNumber")).isFalse();
		assertThat(ctx.pageAttributes.containsKey("firstItem")).isFalse();
		assertThat(ctx.pageAttributes.containsKey("lastItem")).isFalse();

		assertThat(tag.doEndTag()).isEqualTo(TagSupport.EVAL_PAGE);
	}

	@Test
	void setPageAttributesCustomOnlyNumberSkipsUrl() throws JspException {
		TestContext ctx = TestContext.create("0");

		PagerTag pager = new PagerTag();
		pager.setPageContext(ctx.pageContext);
		pager.setUrl("newsList.do");
		pager.setItems(5);
		pager.setMaxPageItems(2);
		pager.setScope("request");
		pager.doStartTag();

		TestPageTagSupport tag = new TestPageTagSupport();
		tag.setPageContext(ctx.pageContext);
		tag.setExport("num=pageNumber");
		tag.doStartTag();

		tag.callSetPageAttributes(0);
		assertThat(ctx.pageAttributes.get("num")).isEqualTo(Integer.valueOf(1));
		assertThat(ctx.pageAttributes.containsKey("pageUrl")).isFalse();
	}

	@Test
	void setPageAttributesCustomFirstLastOnly() throws JspException {
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
		tag.setExport("firstItem,lastItem");
		tag.doStartTag();

		tag.callSetPageAttributes(1);
		assertThat(ctx.pageAttributes.get("firstItem"))
				.isEqualTo(Integer.valueOf(11));
		assertThat(ctx.pageAttributes.get("lastItem"))
				.isEqualTo(Integer.valueOf(20));
		assertThat(ctx.pageAttributes.containsKey("pageUrl")).isFalse();
		assertThat(ctx.pageAttributes.containsKey("pageNumber")).isFalse();
	}

	@Test
	void setOffsetAttributesUsesDefaultNames() throws JspException {
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

		tag.callSetOffsetAttributes(10);
		assertThat(ctx.pageAttributes.get("pageUrl"))
				.isEqualTo("newsList.do?pager.offset=10");
		assertThat(ctx.pageAttributes.get("pageNumber"))
				.isEqualTo(Integer.valueOf(2));
		assertThat(ctx.pageAttributes.containsKey("firstItem")).isFalse();
		assertThat(ctx.pageAttributes.containsKey("lastItem")).isFalse();
	}

	@Test
	void setOffsetAttributesCustomWithoutFirstLast() throws JspException {
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
		tag.setExport("link=pageUrl");
		tag.doStartTag();

		tag.callSetOffsetAttributes(10);
		assertThat(ctx.pageAttributes.get("link"))
				.isEqualTo("newsList.do?pager.offset=10");
		assertThat(ctx.pageAttributes.containsKey("firstItem")).isFalse();
		assertThat(ctx.pageAttributes.containsKey("lastItem")).isFalse();
	}

	@Test
	void setOffsetAttributesCustomFirstLastOnly() throws JspException {
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
		tag.setExport("firstItem,lastItem");
		tag.doStartTag();

		tag.callSetOffsetAttributes(10);
		assertThat(ctx.pageAttributes.get("firstItem"))
				.isEqualTo(Integer.valueOf(11));
		assertThat(ctx.pageAttributes.get("lastItem"))
				.isEqualTo(Integer.valueOf(20));
		assertThat(ctx.pageAttributes.containsKey("pageUrl")).isFalse();
		assertThat(ctx.pageAttributes.containsKey("pageNumber")).isFalse();
	}

	@Test
	void customExportRestoresFirstAndLastItems() throws JspException {
		TestContext ctx = TestContext.create("0");
		ctx.pageAttributes.put("firstItem", Integer.valueOf(1));
		ctx.pageAttributes.put("lastItem", Integer.valueOf(2));

		PagerTag pager = new PagerTag();
		pager.setPageContext(ctx.pageContext);
		pager.setUrl("newsList.do");
		pager.setItems(25);
		pager.setMaxPageItems(10);
		pager.setScope("request");
		pager.doStartTag();

		TestPageTagSupport tag = new TestPageTagSupport();
		tag.setPageContext(ctx.pageContext);
		tag.setExport("firstItem,lastItem");
		tag.doStartTag();

		tag.callSetOffsetAttributes(10);
		assertThat(ctx.pageAttributes.get("firstItem"))
				.isEqualTo(Integer.valueOf(11));
		assertThat(ctx.pageAttributes.get("lastItem"))
				.isEqualTo(Integer.valueOf(20));

		tag.doEndTag();
		assertThat(ctx.pageAttributes.get("firstItem"))
				.isEqualTo(Integer.valueOf(1));
		assertThat(ctx.pageAttributes.get("lastItem"))
				.isEqualTo(Integer.valueOf(2));
	}

	@Test
	void removeAttributesCustomFirstLastOnly() throws JspException {
		TestContext ctx = TestContext.create("0");
		ctx.pageAttributes.put("firstItem", Integer.valueOf(11));
		ctx.pageAttributes.put("lastItem", Integer.valueOf(20));

		PagerTag pager = new PagerTag();
		pager.setPageContext(ctx.pageContext);
		pager.setUrl("newsList.do");
		pager.setItems(25);
		pager.setMaxPageItems(10);
		pager.setScope("request");
		pager.doStartTag();

		TestPageTagSupport tag = new TestPageTagSupport();
		tag.setPageContext(ctx.pageContext);
		tag.setExport("firstItem,lastItem");
		tag.doStartTag();

		tag.callRemoveAttributes();
		assertThat(ctx.pageAttributes.containsKey("firstItem")).isFalse();
		assertThat(ctx.pageAttributes.containsKey("lastItem")).isFalse();
	}

	@Test
	void releaseResetsExport() throws JspException {
		TestPageTagSupport tag = new TestPageTagSupport();
		tag.setExport("pageUrl");
		tag.release();
		assertThat(tag.getExport()).isNull();
	}

	@Test
	void removeAttributesHandlesDefaultExports() throws JspException {
		TestContext ctx = TestContext.create("0");

		PagerTag pager = new PagerTag();
		pager.setPageContext(ctx.pageContext);
		pager.setUrl("newsList.do");
		pager.setItems(5);
		pager.setMaxPageItems(2);
		pager.setScope("request");
		pager.doStartTag();

		TestPageTagSupport tag = new TestPageTagSupport();
		tag.setPageContext(ctx.pageContext);
		tag.doStartTag();

		tag.callSetPageAttributes(0);
		assertThat(ctx.pageAttributes.containsKey("pageUrl")).isTrue();
		assertThat(ctx.pageAttributes.containsKey("pageNumber")).isTrue();

		tag.callRemoveAttributes();
		assertThat(ctx.pageAttributes.containsKey("pageUrl")).isFalse();
		assertThat(ctx.pageAttributes.containsKey("pageNumber")).isFalse();
	}

	@Test
	void removeAttributesCustomOnlyNumber() throws JspException {
		TestContext ctx = TestContext.create("0");
		ctx.pageAttributes.put("num", Integer.valueOf(5));

		PagerTag pager = new PagerTag();
		pager.setPageContext(ctx.pageContext);
		pager.setUrl("newsList.do");
		pager.setItems(5);
		pager.setMaxPageItems(2);
		pager.setScope("request");
		pager.doStartTag();

		TestPageTagSupport tag = new TestPageTagSupport();
		tag.setPageContext(ctx.pageContext);
		tag.setExport("num=pageNumber");
		tag.doStartTag();

		tag.callRemoveAttributes();
		assertThat(ctx.pageAttributes.containsKey("num")).isFalse();
	}
	@Test
	void customExportRestoresOnlyDeclaredVariables() throws JspException {
		TestContext ctx = TestContext.create("0");
		ctx.pageAttributes.put("pageUrl", "keep");
		ctx.pageAttributes.put("pageNumber", Integer.valueOf(9));

		PagerTag pager = new PagerTag();
		pager.setPageContext(ctx.pageContext);
		pager.setUrl("newsList.do");
		pager.setItems(5);
		pager.setMaxPageItems(2);
		pager.setScope("request");
		pager.doStartTag();

		TestPageTagSupport tag = new TestPageTagSupport();
		tag.setPageContext(ctx.pageContext);
		tag.setExport("num=pageNumber");
		tag.doStartTag();

		tag.callSetPageAttributes(0);
		assertThat(ctx.pageAttributes.get("num")).isEqualTo(Integer.valueOf(1));

		tag.doEndTag();
		assertThat(ctx.pageAttributes.containsKey("num")).isFalse();
		assertThat(ctx.pageAttributes.get("pageUrl")).isEqualTo("keep");
	}

	@Test
	void removeAttributesOnlyRemovesDeclaredNames() throws JspException {
		TestContext ctx = TestContext.create("0");
		ctx.pageAttributes.put("pageUrl", "keep");
		ctx.pageAttributes.put("pageNumber", Integer.valueOf(2));

		PagerTag pager = new PagerTag();
		pager.setPageContext(ctx.pageContext);
		pager.setUrl("newsList.do");
		pager.setItems(5);
		pager.setMaxPageItems(2);
		pager.setScope("request");
		pager.doStartTag();

		TestPageTagSupport tag = new TestPageTagSupport();
		tag.setPageContext(ctx.pageContext);
		tag.setExport("num=pageNumber");
		tag.doStartTag();

		tag.callSetPageAttributes(0);
		tag.callRemoveAttributes();
		assertThat(ctx.pageAttributes.containsKey("num")).isFalse();
		assertThat(ctx.pageAttributes.get("pageUrl")).isEqualTo("keep");
	}

	@Test
	void setExportKeepsSameInstanceWhenSameValue() throws JspException {
		TestContext ctx = TestContext.create("0");
		PagerTag pager = new PagerTag();
		pager.setPageContext(ctx.pageContext);
		pager.setUrl("newsList.do");
		pager.setItems(5);
		pager.setMaxPageItems(2);
		pager.setScope("request");
		pager.doStartTag();

		TestPageTagSupport tag = new TestPageTagSupport();
		tag.setPageContext(ctx.pageContext);
		String expr = "pageUrl";
		tag.setExport(expr);
		tag.setExport(expr);
		assertThat(tag.getExport()).isEqualTo(expr);
	}
 
	@Test
	void setExportRejectsInvalidExpression() {
		TestPageTagSupport tag = new TestPageTagSupport();
		assertThatThrownBy(() -> tag.setExport("="))
				.isInstanceOf(JspException.class);
	}
}
