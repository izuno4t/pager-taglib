package com.jsptags.navigation.pager.parser;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class TagExportParserTest {

	@Test
	void parsePagerTagExportSupportsAliasesAndCaches() throws ParseException {
		PagerTagExport first = TagExportParser
				.parsePagerTagExport("pageOffset,currentPageNumber=pageNumber");

		assertThat(first.getPageOffset()).isEqualTo("pageOffset");
		assertThat(first.getPageNumber()).isEqualTo("currentPageNumber");

		PagerTagExport second = TagExportParser
				.parsePagerTagExport("pageOffset,currentPageNumber=pageNumber");
		assertThat(second).isSameAs(first);
	}

	@Test
	void parseIndexAndPageExports() throws ParseException {
		IndexTagExport index = TagExportParser
				.parseIndexTagExport("itemCount,pageCount");
		assertThat(index.getItemCount()).isEqualTo("itemCount");
		assertThat(index.getPageCount()).isEqualTo("pageCount");

		PageTagExport page = TagExportParser
				.parsePageTagExport("pageUrl,pageNumber,firstItem,lastItem");
		assertThat(page.getPageUrl()).isEqualTo("pageUrl");
		assertThat(page.getPageNumber()).isEqualTo("pageNumber");
		assertThat(page.getFirstItem()).isEqualTo("firstItem");
		assertThat(page.getLastItem()).isEqualTo("lastItem");
	}

	@Test
	void parseRejectsInvalidExpressions() {
		assertThatThrownBy(() -> TagExportParser.parsePagerTagExport("="))
				.isInstanceOf(ParseException.class);
	}
}
