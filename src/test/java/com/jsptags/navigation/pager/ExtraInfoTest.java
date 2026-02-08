package com.jsptags.navigation.pager;

import static org.assertj.core.api.Assertions.assertThat;

import javax.servlet.jsp.tagext.TagData;
import javax.servlet.jsp.tagext.VariableInfo;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class ExtraInfoTest {

	@Test
	void pagerTagExtraInfoParsesExports() {
		TagData tagData = Mockito.mock(TagData.class);
		Mockito.when(tagData.getAttributeString("export"))
				.thenReturn("pageOffset,currentPageNumber=pageNumber");

		PagerTagExtraInfo info = new PagerTagExtraInfo();
		VariableInfo[] vars = info.getVariableInfo(tagData);
		assertThat(vars).hasSize(2);
		assertThat(vars[0].getVarName()).isEqualTo("pageOffset");
		assertThat(vars[1].getVarName()).isEqualTo("currentPageNumber");

		assertThat(info.isValid(tagData)).isTrue();
	}

	@Test
	void pagerTagExtraInfoHandlesInvalidExport() {
		TagData tagData = Mockito.mock(TagData.class);
		Mockito.when(tagData.getAttributeString("export")).thenReturn("=");

		PagerTagExtraInfo info = new PagerTagExtraInfo();
		VariableInfo[] vars = info.getVariableInfo(tagData);
		assertThat(vars).isEmpty();
		assertThat(info.isValid(tagData)).isFalse();
	}

	@Test
	void pagerTagExtraInfoHandlesNullExport() {
		TagData tagData = Mockito.mock(TagData.class);
		Mockito.when(tagData.getAttributeString("export")).thenReturn(null);

		PagerTagExtraInfo info = new PagerTagExtraInfo();
		VariableInfo[] vars = info.getVariableInfo(tagData);
		assertThat(vars).isEmpty();
		assertThat(info.isValid(tagData)).isTrue();
	}

	@Test
	void pagerTagExtraInfoSingleVariable() {
		TagData tagData = Mockito.mock(TagData.class);
		Mockito.when(tagData.getAttributeString("export"))
				.thenReturn("pageOffset");

		PagerTagExtraInfo info = new PagerTagExtraInfo();
		VariableInfo[] vars = info.getVariableInfo(tagData);
		assertThat(vars).hasSize(1);
		assertThat(vars[0].getVarName()).isEqualTo("pageOffset");
	}

	@Test
	void pageTagExtraInfoProvidesDefaultsWhenNoExport() {
		TagData tagData = Mockito.mock(TagData.class);
		Mockito.when(tagData.getAttributeString("export")).thenReturn(null);

		PageTagExtraInfo info = new PageTagExtraInfo();
		VariableInfo[] vars = info.getVariableInfo(tagData);
		assertThat(vars).hasSize(2);
		assertThat(vars[0].getVarName()).isEqualTo("pageUrl");
		assertThat(vars[1].getVarName()).isEqualTo("pageNumber");
		assertThat(info.isValid(tagData)).isTrue();
	}

	@Test
	void pageTagExtraInfoParsesAllExports() {
		TagData tagData = Mockito.mock(TagData.class);
		Mockito.when(tagData.getAttributeString("export"))
				.thenReturn("pageUrl,pageNumber,firstItem,lastItem");

		PageTagExtraInfo info = new PageTagExtraInfo();
		VariableInfo[] vars = info.getVariableInfo(tagData);
		assertThat(vars).hasSize(4);
		assertThat(info.isValid(tagData)).isTrue();
	}

	@Test
	void pageTagExtraInfoInvalidExport() {
		TagData tagData = Mockito.mock(TagData.class);
		Mockito.when(tagData.getAttributeString("export")).thenReturn("=");

		PageTagExtraInfo info = new PageTagExtraInfo();
		VariableInfo[] vars = info.getVariableInfo(tagData);
		assertThat(vars).isEmpty();
		assertThat(info.isValid(tagData)).isFalse();
	}

	@Test
	void pageTagExtraInfoPartialExports() {
		TagData tagData = Mockito.mock(TagData.class);
		Mockito.when(tagData.getAttributeString("export"))
				.thenReturn("pageNumber");

		PageTagExtraInfo info = new PageTagExtraInfo();
		VariableInfo[] vars = info.getVariableInfo(tagData);
		assertThat(vars).hasSize(1);
		assertThat(vars[0].getVarName()).isEqualTo("pageNumber");
	}

	@Test
	void indexTagExtraInfoParsesExports() {
		TagData tagData = Mockito.mock(TagData.class);
		Mockito.when(tagData.getAttributeString("export"))
				.thenReturn("itemCount,pageCount");

		IndexTagExtraInfo info = new IndexTagExtraInfo();
		VariableInfo[] vars = info.getVariableInfo(tagData);
		assertThat(vars).hasSize(2);
		assertThat(info.isValid(tagData)).isTrue();
	}

	@Test
	void indexTagExtraInfoHandlesNullExport() {
		TagData tagData = Mockito.mock(TagData.class);
		Mockito.when(tagData.getAttributeString("export")).thenReturn(null);

		IndexTagExtraInfo info = new IndexTagExtraInfo();
		VariableInfo[] vars = info.getVariableInfo(tagData);
		assertThat(vars).isEmpty();
		assertThat(info.isValid(tagData)).isTrue();
	}

	@Test
	void indexTagExtraInfoSingleVariable() {
		TagData tagData = Mockito.mock(TagData.class);
		Mockito.when(tagData.getAttributeString("export"))
				.thenReturn("itemCount");

		IndexTagExtraInfo info = new IndexTagExtraInfo();
		VariableInfo[] vars = info.getVariableInfo(tagData);
		assertThat(vars).hasSize(1);
		assertThat(vars[0].getVarName()).isEqualTo("itemCount");
	}

	@Test
	void indexTagExtraInfoInvalidExport() {
		TagData tagData = Mockito.mock(TagData.class);
		Mockito.when(tagData.getAttributeString("export")).thenReturn("=");

		IndexTagExtraInfo info = new IndexTagExtraInfo();
		VariableInfo[] vars = info.getVariableInfo(tagData);
		assertThat(vars).isEmpty();
		assertThat(info.isValid(tagData)).isFalse();
	}

	@Test
	void jumpTagExtraInfoValidatesUnless() {
		TagData tagData = Mockito.mock(TagData.class);
		Mockito.when(tagData.getAttributeString("export")).thenReturn(null);
		Mockito.when(tagData.getAttribute("unless")).thenReturn("indexed");

		JumpTagExtraInfo info = new JumpTagExtraInfo();
		assertThat(info.isValid(tagData)).isTrue();

		Mockito.when(tagData.getAttribute("unless")).thenReturn("bad");
		assertThat(info.isValid(tagData)).isFalse();

		Mockito.when(tagData.getAttribute("unless"))
				.thenReturn(TagData.REQUEST_TIME_VALUE);
		assertThat(info.isValid(tagData)).isTrue();
	}

	@Test
	void jumpTagExtraInfoAllowsNonStringUnless() {
		TagData tagData = Mockito.mock(TagData.class);
		Mockito.when(tagData.getAttributeString("export")).thenReturn(null);
		Mockito.when(tagData.getAttribute("unless")).thenReturn(Integer.valueOf(1));

		JumpTagExtraInfo info = new JumpTagExtraInfo();
		assertThat(info.isValid(tagData)).isTrue();
	}

	@Test
	void jumpTagExtraInfoAcceptsCurrentUnless() {
		TagData tagData = Mockito.mock(TagData.class);
		Mockito.when(tagData.getAttributeString("export")).thenReturn(null);
		Mockito.when(tagData.getAttribute("unless")).thenReturn("current");

		JumpTagExtraInfo info = new JumpTagExtraInfo();
		assertThat(info.isValid(tagData)).isTrue();
	}
}
