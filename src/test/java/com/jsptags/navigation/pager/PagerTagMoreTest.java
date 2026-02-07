package com.jsptags.navigation.pager;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Field;

import javax.servlet.jsp.JspException;

import org.junit.jupiter.api.Test;

class PagerTagMoreTest {

	@Test
	void negativeOffsetIsClampedToZero() throws JspException {
		TestContext ctx = TestContext.create("-5");

		PagerTag tag = new PagerTag();
		tag.setPageContext(ctx.pageContext);
		tag.setUrl("newsList.do");
		tag.doStartTag();

		assertThat(tag.getOffset()).isEqualTo(0);
		assertThat(tag.getPageNumber()).isEqualTo(0);
	}

	@Test
	void getItemCountUsesItemCounterWhenItemsUnset() throws JspException {
		TestContext ctx = TestContext.create("0");

		PagerTag tag = new PagerTag();
		tag.setPageContext(ctx.pageContext);
		tag.setUrl("newsList.do");
		tag.setMaxItems(2);
		tag.setMaxPageItems(1);
		tag.doStartTag();

		assertThat(tag.getItemCount()).isEqualTo(0);
		assertThat(tag.nextItem()).isTrue();
		assertThat(tag.getItemCount()).isEqualTo(1);
	}

	@Test
	void getPageNumberReturnsCachedInstanceForCurrentPage() throws Exception {
		TestContext ctx = TestContext.create("10");
		PagerTag tag = new PagerTag();
		tag.setPageContext(ctx.pageContext);
		tag.setUrl("newsList.do");
		tag.setMaxPageItems(10);
		tag.doStartTag();

		Field field = PagerTag.class.getDeclaredField("pageNumberInteger");
		field.setAccessible(true);
		Integer cached = (Integer) field.get(tag);

		Integer fromGetter = tag.getPageNumber(tag.getPageNumber());
		assertThat(fromGetter).isSameAs(cached);
	}

	@Test
	void halfFullIndexWhenBeyondHalfUsesElseBranch() throws JspException {
		TestContext ctx = TestContext.create("40");

		PagerTag tag = new PagerTag();
		tag.setPageContext(ctx.pageContext);
		tag.setUrl("newsList.do");
		tag.setIndex("half-full");
		tag.setItems(120);
		tag.setMaxPageItems(10);
		tag.setMaxIndexPages(6);
		tag.doStartTag();

		int firstPage = tag.getFirstIndexPage();
		assertThat(firstPage).isEqualTo(1);
		assertThat(tag.getLastIndexPage(firstPage)).isEqualTo(6);
	}

	@Test
	void getLastIndexPageHalfFullWhenBeforeHalf() throws JspException {
		TestContext ctx = TestContext.create("0");

		PagerTag tag = new PagerTag();
		tag.setPageContext(ctx.pageContext);
		tag.setUrl("newsList.do");
		tag.setIndex("half-full");
		tag.setItems(120);
		tag.setMaxPageItems(10);
		tag.setMaxIndexPages(6);
		tag.doStartTag();

		int firstPage = tag.getFirstIndexPage();
		assertThat(firstPage).isEqualTo(0);
		assertThat(tag.getLastIndexPage(firstPage)).isEqualTo(2);
	}

	@Test
	void getLastIndexPageWhenMaxPagesLessThanTotal() throws JspException {
		TestContext ctx = TestContext.create("30");

		PagerTag tag = new PagerTag();
		tag.setPageContext(ctx.pageContext);
		tag.setUrl("newsList.do");
		tag.setIndex("center");
		tag.setItems(200);
		tag.setMaxPageItems(10);
		tag.setMaxIndexPages(5);
		tag.doStartTag();

		int firstPage = tag.getFirstIndexPage();
		assertThat(firstPage).isEqualTo(1);
		assertThat(tag.getLastIndexPage(firstPage)).isEqualTo(5);
	}

	@Test
	void getOffsetPageNumberUsesPageNumberCalculation() throws JspException {
		TestContext ctx = TestContext.create("0");

		PagerTag tag = new PagerTag();
		tag.setPageContext(ctx.pageContext);
		tag.setUrl("newsList.do");
		tag.setMaxPageItems(10);
		tag.doStartTag();

		assertThat(tag.getOffsetPageNumber(0)).isEqualTo(Integer.valueOf(1));
		assertThat(tag.getOffsetPageNumber(10)).isEqualTo(Integer.valueOf(2));
		assertThat(tag.getOffsetPageNumber(11)).isEqualTo(Integer.valueOf(3));
	}

	@Test
	void getPageCountReflectsItemsWhenSet() throws JspException {
		TestContext ctx = TestContext.create("0");
		PagerTag tag = new PagerTag();
		tag.setPageContext(ctx.pageContext);
		tag.setUrl("newsList.do");
		tag.setItems(21);
		tag.setMaxPageItems(10);
		tag.doStartTag();

		assertThat(tag.getPageCount()).isEqualTo(3);
	}

	@Test
	void getPageNumberCreatesNewInstanceForOtherPage() throws Exception {
		TestContext ctx = TestContext.create("0");
		PagerTag tag = new PagerTag();
		tag.setPageContext(ctx.pageContext);
		tag.setUrl("newsList.do");
		tag.setMaxPageItems(10);
		tag.doStartTag();

		Field field = PagerTag.class.getDeclaredField("pageNumberInteger");
		field.setAccessible(true);
		Integer cached = (Integer) field.get(tag);

		Integer other = tag.getPageNumber(tag.getPageNumber() + 1);
		assertThat(other).isNotSameAs(cached);
		assertThat(other).isEqualTo(Integer.valueOf(2));
	}

	@Test
	void hasNextPageFalseWhenAtEnd() throws JspException {
		TestContext ctx = TestContext.create("0");
		PagerTag tag = new PagerTag();
		tag.setPageContext(ctx.pageContext);
		tag.setUrl("newsList.do");
		tag.setItems(10);
		tag.setMaxPageItems(10);
		tag.doStartTag();

		assertThat(tag.hasNextPage()).isFalse();
		assertThat(tag.isIndexNeeded()).isFalse();
	}

	@Test
	void pageScopeWithoutExportLeavesAttributesUnchanged() throws JspException {
		TestContext ctx = TestContext.create("0");
		ctx.pageAttributes.put("pageOffset", Integer.valueOf(7));
		ctx.pageAttributes.put("pageNumber", Integer.valueOf(8));

		PagerTag tag = new PagerTag();
		tag.setPageContext(ctx.pageContext);
		tag.setUrl("newsList.do");
		tag.setScope("page");
		tag.doStartTag();
		tag.doEndTag();

		assertThat(ctx.pageAttributes.get("pageOffset")).isEqualTo(Integer.valueOf(7));
		assertThat(ctx.pageAttributes.get("pageNumber")).isEqualTo(Integer.valueOf(8));
	}

	@Test
	void getLastIndexPageWhenPagesWithinMax() throws JspException {
		TestContext ctx = TestContext.create("0");
		PagerTag tag = new PagerTag();
		tag.setPageContext(ctx.pageContext);
		tag.setUrl("newsList.do");
		tag.setItems(25);
		tag.setMaxPageItems(10);
		tag.setMaxIndexPages(10);
		tag.doStartTag();

		int first = tag.getFirstIndexPage();
		assertThat(first).isEqualTo(0);
		assertThat(tag.getLastIndexPage(first)).isEqualTo(2);
	}

	@Test
	void setIdUpdatesOffsetParameterName() throws JspException {
		TestContext ctx = TestContext.create("0");
		org.mockito.Mockito.when(ctx.request.getParameter("custom.offset"))
				.thenReturn("0");

		PagerTag tag = new PagerTag();
		tag.setPageContext(ctx.pageContext);
		tag.setUrl("newsList.do");
		tag.setId("custom");
		tag.doStartTag();

		assertThat(tag.getPageUrl(0))
				.isEqualTo("newsList.do?custom.offset=0");
	}

	@Test
	void requestScopeExportsPageOffsetAndPageNumber() throws JspException {
		TestContext ctx = TestContext.create("10");

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
		assertThat(ctx.requestAttributes.containsKey("pageOffset")).isFalse();
		assertThat(ctx.requestAttributes.containsKey("pageNumber")).isFalse();
	}

	@Test
	void requestScopeExportOnlyPageNumberSkipsOffset() throws JspException {
		TestContext ctx = TestContext.create("10");

		PagerTag tag = new PagerTag();
		tag.setPageContext(ctx.pageContext);
		tag.setUrl("newsList.do");
		tag.setExport("pageNumber");
		tag.setScope("request");
		tag.doStartTag();

		assertThat(ctx.requestAttributes.containsKey("pageOffset")).isFalse();
		assertThat(ctx.requestAttributes.get("pageNumber"))
				.isEqualTo(Integer.valueOf(2));

		tag.doEndTag();
		assertThat(ctx.requestAttributes.containsKey("pageOffset")).isFalse();
		assertThat(ctx.requestAttributes.containsKey("pageNumber")).isFalse();
	}

	@Test
	void requestScopeExportOnlyPageOffsetSkipsNumber() throws JspException {
		TestContext ctx = TestContext.create("10");

		PagerTag tag = new PagerTag();
		tag.setPageContext(ctx.pageContext);
		tag.setUrl("newsList.do");
		tag.setExport("pageOffset");
		tag.setScope("request");
		tag.doStartTag();

		assertThat(ctx.requestAttributes.get("pageOffset"))
				.isEqualTo(Integer.valueOf(10));
		assertThat(ctx.requestAttributes.containsKey("pageNumber")).isFalse();

		tag.doEndTag();
		assertThat(ctx.requestAttributes.containsKey("pageOffset")).isFalse();
		assertThat(ctx.requestAttributes.containsKey("pageNumber")).isFalse();
	}

	@Test
	void pageScopeExportsPageOffsetAndPageNumber() throws JspException {
		TestContext ctx = TestContext.create("10");
		ctx.pageAttributes.put("pageOffset", Integer.valueOf(1));
		ctx.pageAttributes.put("pageNumber", Integer.valueOf(1));

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
				.isEqualTo(Integer.valueOf(1));
	}

	@Test
	void pageScopeExportOnlyPageNumberSkipsOffset() throws JspException {
		TestContext ctx = TestContext.create("10");

		PagerTag tag = new PagerTag();
		tag.setPageContext(ctx.pageContext);
		tag.setUrl("newsList.do");
		tag.setExport("pageNumber");
		tag.setScope("page");
		tag.doStartTag();

		assertThat(ctx.pageAttributes.containsKey("pageOffset")).isFalse();
		assertThat(ctx.pageAttributes.get("pageNumber"))
				.isEqualTo(Integer.valueOf(2));

		tag.doEndTag();
		assertThat(ctx.pageAttributes.containsKey("pageOffset")).isFalse();
		assertThat(ctx.pageAttributes.containsKey("pageNumber")).isFalse();
	}

	@Test
	void pageScopeExportOnlyPageOffsetSkipsNumber() throws JspException {
		TestContext ctx = TestContext.create("10");

		PagerTag tag = new PagerTag();
		tag.setPageContext(ctx.pageContext);
		tag.setUrl("newsList.do");
		tag.setExport("pageOffset");
		tag.setScope("page");
		tag.doStartTag();

		assertThat(ctx.pageAttributes.get("pageOffset"))
				.isEqualTo(Integer.valueOf(10));
		assertThat(ctx.pageAttributes.containsKey("pageNumber")).isFalse();

		tag.doEndTag();
		assertThat(ctx.pageAttributes.containsKey("pageOffset")).isFalse();
		assertThat(ctx.pageAttributes.containsKey("pageNumber")).isFalse();
	}
}
