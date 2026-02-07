package com.jsptags.navigation.pager.parser;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

class TagExportParserTokenManagerTest {

	@Test
	void tokenizesPagerStateKeywordsAndSymbols() {
		TagExportParserTokenManager tm = new TagExportParserTokenManager(
				new JavaCharStream(new StringReader("pageOffset,pageNumber;")));
		tm.SwitchTo(TagExportParserConstants.PAGER_STATE);

		List<Integer> kinds = collectKinds(tm);
		assertThat(kinds).containsExactly(
				TagExportParserConstants.PAGER_PAGEOFFSET,
				TagExportParserConstants.COMMA,
				TagExportParserConstants.PAGER_PAGENUMBER,
				TagExportParserConstants.SEMICOLON,
				TagExportParserConstants.EOF);
	}

	@Test
	void tokenizesPagerStateAlternateKeywords() {
		TagExportParserTokenManager tm = new TagExportParserTokenManager(
				new JavaCharStream(new StringReader("offset,page,number,pageOffset,pageNumber")));
		tm.SwitchTo(TagExportParserConstants.PAGER_STATE);

		List<Integer> kinds = collectKinds(tm);
		assertThat(kinds).containsExactly(
				TagExportParserConstants.PAGER_OFFSET,
				TagExportParserConstants.COMMA,
				TagExportParserConstants.PAGER_PAGE,
				TagExportParserConstants.COMMA,
				TagExportParserConstants.PAGER_NUMBER,
				TagExportParserConstants.COMMA,
				TagExportParserConstants.PAGER_PAGEOFFSET,
				TagExportParserConstants.COMMA,
				TagExportParserConstants.PAGER_PAGENUMBER,
				TagExportParserConstants.EOF);
	}

	@Test
	void tokenizesPagerStateIdentifierAssignment() {
		TagExportParserTokenManager tm = new TagExportParserTokenManager(
				new JavaCharStream(new StringReader("foo=pageNumber")));
		tm.SwitchTo(TagExportParserConstants.PAGER_STATE);

		List<Integer> kinds = collectKinds(tm);
		assertThat(kinds).containsExactly(
				TagExportParserConstants.IDENTIFIER,
				TagExportParserConstants.EQUALS,
				TagExportParserConstants.PAGER_PAGENUMBER,
				TagExportParserConstants.EOF);
	}

	@Test
	void tokenizesIndexAndPageStates() {
		TagExportParserTokenManager indexTm = new TagExportParserTokenManager(
				new JavaCharStream(new StringReader("itemCount,pageCount")));
		indexTm.SwitchTo(TagExportParserConstants.INDEX_STATE);

		List<Integer> indexKinds = collectKinds(indexTm);
		assertThat(indexKinds).containsExactly(
				TagExportParserConstants.INDEX_ITEMCOUNT,
				TagExportParserConstants.COMMA,
				TagExportParserConstants.INDEX_PAGECOUNT,
				TagExportParserConstants.EOF);

		TagExportParserTokenManager pageTm = new TagExportParserTokenManager(
				new JavaCharStream(new StringReader("pageUrl,pageNumber,firstItem,lastItem")));
		pageTm.SwitchTo(TagExportParserConstants.PAGE_STATE);

		List<Integer> pageKinds = collectKinds(pageTm);
		assertThat(pageKinds).containsExactly(
				TagExportParserConstants.PAGE_PAGEURL,
				TagExportParserConstants.COMMA,
				TagExportParserConstants.PAGE_PAGENUMBER,
				TagExportParserConstants.COMMA,
				TagExportParserConstants.PAGE_FIRSTITEM,
				TagExportParserConstants.COMMA,
				TagExportParserConstants.PAGE_LASTITEM,
				TagExportParserConstants.EOF);
	}

	@Test
	void tokenizesIndexStateAlternateKeywords() {
		TagExportParserTokenManager tm = new TagExportParserTokenManager(
				new JavaCharStream(new StringReader("items,pages")));
		tm.SwitchTo(TagExportParserConstants.INDEX_STATE);

		List<Integer> kinds = collectKinds(tm);
		assertThat(kinds).containsExactly(
				TagExportParserConstants.INDEX_ITEMS,
				TagExportParserConstants.COMMA,
				TagExportParserConstants.INDEX_PAGES,
				TagExportParserConstants.EOF);
	}

	@Test
	void tokenizesPageStateAlternateKeywords() {
		TagExportParserTokenManager tm = new TagExportParserTokenManager(
				new JavaCharStream(new StringReader("url,pageUrl,first,last")));
		tm.SwitchTo(TagExportParserConstants.PAGE_STATE);

		List<Integer> kinds = collectKinds(tm);
		assertThat(kinds).containsExactly(
				TagExportParserConstants.PAGE_URL,
				TagExportParserConstants.COMMA,
				TagExportParserConstants.PAGE_PAGEURL,
				TagExportParserConstants.COMMA,
				TagExportParserConstants.PAGE_FIRST,
				TagExportParserConstants.COMMA,
				TagExportParserConstants.PAGE_LAST,
				TagExportParserConstants.EOF);
	}

	@Test
	void skipsWhitespaceAcrossTokens() {
		TagExportParserTokenManager tm = new TagExportParserTokenManager(
				new JavaCharStream(new StringReader("  pageOffset \n ,\t pageNumber ")));
		tm.SwitchTo(TagExportParserConstants.PAGER_STATE);

		List<Integer> kinds = collectKinds(tm);
		assertThat(kinds).containsExactly(
				TagExportParserConstants.PAGER_PAGEOFFSET,
				TagExportParserConstants.COMMA,
				TagExportParserConstants.PAGER_PAGENUMBER,
				TagExportParserConstants.EOF);
	}

	@Test
	void tokenizesIdentifierWithDollarAndDigits() {
		TagExportParserTokenManager tm = new TagExportParserTokenManager(
				new JavaCharStream(new StringReader("$id123=pageOffset")));
		tm.SwitchTo(TagExportParserConstants.PAGER_STATE);

		List<Integer> kinds = collectKinds(tm);
		assertThat(kinds).containsExactly(
				TagExportParserConstants.IDENTIFIER,
				TagExportParserConstants.EQUALS,
				TagExportParserConstants.PAGER_PAGEOFFSET,
				TagExportParserConstants.EOF);
	}

	@Test
	void tokenizesDefaultStateIdentifier() {
		TagExportParserTokenManager tm = new TagExportParserTokenManager(
				new JavaCharStream(new StringReader("foo")));

		List<Integer> kinds = collectKinds(tm);
		assertThat(kinds).containsExactly(
				TagExportParserConstants.IDENTIFIER,
				TagExportParserConstants.EOF);
	}

	@Test
	void tokenizesDefaultStateSymbols() {
		TagExportParserTokenManager tm = new TagExportParserTokenManager(
				new JavaCharStream(new StringReader("=,;")));

		List<Integer> kinds = collectKinds(tm);
		assertThat(kinds).containsExactly(
				TagExportParserConstants.EQUALS,
				TagExportParserConstants.COMMA,
				TagExportParserConstants.SEMICOLON,
				TagExportParserConstants.EOF);
	}

	@Test
	void tokenizesUnicodeIdentifierInDefaultState() {
		TagExportParserTokenManager tm = new TagExportParserTokenManager(
				new JavaCharStream(new StringReader("あ")));

		List<Integer> kinds = collectKinds(tm);
		assertThat(kinds).containsExactly(
				TagExportParserConstants.IDENTIFIER,
				TagExportParserConstants.EOF);
	}

	@Test
	void tokenizesUnicodeIdentifierInPagerState() {
		TagExportParserTokenManager tm = new TagExportParserTokenManager(
				new JavaCharStream(new StringReader("あ=pageOffset")));
		tm.SwitchTo(TagExportParserConstants.PAGER_STATE);

		List<Integer> kinds = collectKinds(tm);
		assertThat(kinds).containsExactly(
				TagExportParserConstants.IDENTIFIER,
				TagExportParserConstants.EQUALS,
				TagExportParserConstants.PAGER_PAGEOFFSET,
				TagExportParserConstants.EOF);
	}

	@Test
	void returnsEofForEmptyInput() {
		TagExportParserTokenManager tm = new TagExportParserTokenManager(
				new JavaCharStream(new StringReader("")));

		List<Integer> kinds = collectKinds(tm);
		assertThat(kinds).containsExactly(TagExportParserConstants.EOF);
	}

	@Test
	void reinitAndSwitchToResetState() {
		TagExportParserTokenManager tm = new TagExportParserTokenManager(
				new JavaCharStream(new StringReader("itemCount")));
		tm.SwitchTo(TagExportParserConstants.INDEX_STATE);
		assertThat(tm.getNextToken().kind)
				.isEqualTo(TagExportParserConstants.INDEX_ITEMCOUNT);

		tm.ReInit(new JavaCharStream(new StringReader("pageOffset")),
				TagExportParserConstants.PAGER_STATE);
		assertThat(tm.getNextToken().kind)
				.isEqualTo(TagExportParserConstants.PAGER_PAGEOFFSET);
	}

	@Test
	void switchToRejectsInvalidState() {
		TagExportParserTokenManager tm = new TagExportParserTokenManager(
				new JavaCharStream(new StringReader("pageOffset")));

		assertThatThrownBy(() -> tm.SwitchTo(99))
				.isInstanceOf(TokenMgrError.class);
	}

	@Test
	void throwsTokenMgrErrorOnInvalidCharacter() {
		TagExportParserTokenManager tm = new TagExportParserTokenManager(
				new JavaCharStream(new StringReader("#")));
		tm.SwitchTo(TagExportParserConstants.PAGE_STATE);

		assertThatThrownBy(() -> tm.getNextToken())
				.isInstanceOf(TokenMgrError.class);
	}

	@Test
	void throwsTokenMgrErrorOnInvalidCharacterInIndexState() {
		TagExportParserTokenManager tm = new TagExportParserTokenManager(
				new JavaCharStream(new StringReader("#")));
		tm.SwitchTo(TagExportParserConstants.INDEX_STATE);

		assertThatThrownBy(() -> tm.getNextToken())
				.isInstanceOf(TokenMgrError.class);
	}

	@Test
	void setDebugStreamReplacesStream() {
		TagExportParserTokenManager tm = new TagExportParserTokenManager(
				new JavaCharStream(new StringReader("pageOffset")));
		PrintStream stream = new PrintStream(new ByteArrayOutputStream());
		tm.setDebugStream(stream);
		assertThat(tm.debugStream).isSameAs(stream);
	}

	private static List<Integer> collectKinds(TagExportParserTokenManager tm) {
		List<Integer> kinds = new ArrayList<Integer>();
		while (true) {
			Token token = tm.getNextToken();
			kinds.add(token.kind);
			if (token.kind == TagExportParserConstants.EOF) {
				break;
			}
		}
		return kinds;
	}
}
