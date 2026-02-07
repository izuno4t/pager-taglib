package com.jsptags.navigation.pager;

import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;

final class TestContext {
	final PageContext pageContext;
	final HttpServletRequest request;
	final Map<String, Object> pageAttributes;
	final Map<String, Object> requestAttributes;

	private TestContext(PageContext pageContext, HttpServletRequest request,
			Map<String, Object> pageAttributes, Map<String, Object> requestAttributes) {
		this.pageContext = pageContext;
		this.request = request;
		this.pageAttributes = pageAttributes;
		this.requestAttributes = requestAttributes;
	}

	static TestContext create(String offsetParam) {
		PageContext pageContext = mock(PageContext.class);
		HttpServletRequest request = mock(HttpServletRequest.class);

		Map<String, Object> pageAttributes = new HashMap<String, Object>();
		Map<String, Object> requestAttributes = new HashMap<String, Object>();

		when(pageContext.getRequest()).thenReturn(request);
		when(request.getParameter(PagerTag.DEFAULT_ID + PagerTag.OFFSET_PARAM))
				.thenReturn(offsetParam);
		when(request.getCharacterEncoding()).thenReturn("UTF-8");

		doAnswer(invocation -> {
			String name = (String) invocation.getArgument(0);
			Object value = invocation.getArgument(1);
			requestAttributes.put(name, value);
			return null;
		}).when(request).setAttribute(org.mockito.ArgumentMatchers.anyString(),
				org.mockito.ArgumentMatchers.any());

		doAnswer(invocation -> {
			String name = (String) invocation.getArgument(0);
			requestAttributes.remove(name);
			return null;
		}).when(request).removeAttribute(org.mockito.ArgumentMatchers.anyString());

		when(request.getAttribute(org.mockito.ArgumentMatchers.anyString()))
				.thenAnswer(invocation -> requestAttributes.get(invocation.getArgument(0)));

		doAnswer(invocation -> {
			String name = (String) invocation.getArgument(0);
			Object value = invocation.getArgument(1);
			pageAttributes.put(name, value);
			return null;
		}).when(pageContext).setAttribute(org.mockito.ArgumentMatchers.anyString(),
				org.mockito.ArgumentMatchers.any());

		doAnswer(invocation -> {
			String name = (String) invocation.getArgument(0);
			pageAttributes.remove(name);
			return null;
		}).when(pageContext).removeAttribute(org.mockito.ArgumentMatchers.anyString());

		when(pageContext.getAttribute(org.mockito.ArgumentMatchers.anyString()))
				.thenAnswer(invocation -> pageAttributes.get(invocation.getArgument(0)));

		return new TestContext(pageContext, request, pageAttributes, requestAttributes);
	}
}
