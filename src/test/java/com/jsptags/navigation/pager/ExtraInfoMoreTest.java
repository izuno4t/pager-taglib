package com.jsptags.navigation.pager;

import static org.assertj.core.api.Assertions.assertThat;

import javax.servlet.jsp.tagext.TagData;
import javax.servlet.jsp.tagext.VariableInfo;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class ExtraInfoMoreTest {

	@Test
	void pagerTagExtraInfoOnlyPageNumber() {
		TagData tagData = Mockito.mock(TagData.class);
		Mockito.when(tagData.getAttributeString("export"))
				.thenReturn("pageNumber");

		PagerTagExtraInfo info = new PagerTagExtraInfo();
		VariableInfo[] vars = info.getVariableInfo(tagData);
		assertThat(vars).hasSize(1);
		assertThat(vars[0].getVarName()).isEqualTo("pageNumber");
	}

	@Test
	void indexTagExtraInfoOnlyPageCount() {
		TagData tagData = Mockito.mock(TagData.class);
		Mockito.when(tagData.getAttributeString("export"))
				.thenReturn("pageCount");

		IndexTagExtraInfo info = new IndexTagExtraInfo();
		VariableInfo[] vars = info.getVariableInfo(tagData);
		assertThat(vars).hasSize(1);
		assertThat(vars[0].getVarName()).isEqualTo("pageCount");
	}

	@Test
	void pageTagExtraInfoOnlyUrl() {
		TagData tagData = Mockito.mock(TagData.class);
		Mockito.when(tagData.getAttributeString("export"))
				.thenReturn("pageUrl");

		PageTagExtraInfo info = new PageTagExtraInfo();
		VariableInfo[] vars = info.getVariableInfo(tagData);
		assertThat(vars).hasSize(1);
		assertThat(vars[0].getVarName()).isEqualTo("pageUrl");
	}

	@Test
	void jumpTagExtraInfoInvalidExportFails() {
		TagData tagData = Mockito.mock(TagData.class);
		Mockito.when(tagData.getAttributeString("export")).thenReturn("=");
		Mockito.when(tagData.getAttribute("unless")).thenReturn(null);

		JumpTagExtraInfo info = new JumpTagExtraInfo();
		assertThat(info.isValid(tagData)).isFalse();
	}

	@Test
	void jumpTagExtraInfoNullExportAndNullUnlessIsValid() {
		TagData tagData = Mockito.mock(TagData.class);
		Mockito.when(tagData.getAttributeString("export")).thenReturn(null);
		Mockito.when(tagData.getAttribute("unless")).thenReturn(null);

		JumpTagExtraInfo info = new JumpTagExtraInfo();
		assertThat(info.isValid(tagData)).isTrue();
	}
}
