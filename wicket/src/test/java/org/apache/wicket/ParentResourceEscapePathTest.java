/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.wicket;

import java.io.InputStream;

import org.apache.wicket.ng.request.Url;
import org.apache.wicket.ng.resource.PackageResourceReference;
import org.apache.wicket.ng.resource.ResourceReference;

public class ParentResourceEscapePathTest extends WicketTestCase
{
	public void testParentEscapeSequenceInRenderedHtml() throws Exception
	{
		tester.getApplication().getResourceSettings().setParentFolderPlaceholder("-updir-");
		parentEscapeSequenceInRenderedHtml();

		tester.getApplication().getResourceSettings().setParentFolderPlaceholder("$up$");
		parentEscapeSequenceInRenderedHtml();
	}

	private void parentEscapeSequenceInRenderedHtml()
	{

		tester.startPage(ParentResourceEscapePathTestPage.class);
		tester.assertRenderedPage(ParentResourceEscapePathTestPage.class);
		tester.assertNoErrorMessage();

		System.out.println(tester.getLastResponseAsString());

		String html = tester.getLastResponseAsString();
		assertContains(html, "<html><head><wicket:link><script ");
		assertContains(html, " type=\"text/javascript\"");
		assertContains(html, expectedResourceUrl() + "\"");
		assertContains(html, "\"></script></wicket:link></head></html>");
	}

	private void assertContains(String html, String expected)
	{
		assertTrue(html, html.contains(expected));
	}

	public void testResourceUrlGeneratedByResourceReference()
	{
		tester.getApplication().getResourceSettings().setParentFolderPlaceholder("-updir-");
		resourceUrlGeneratedByResourceReference();

		tester.getApplication().getResourceSettings().setParentFolderPlaceholder("$up$");
		resourceUrlGeneratedByResourceReference();
	}

	private void resourceUrlGeneratedByResourceReference()
	{
		final ResourceReference ref = new PackageResourceReference(
			ParentResourceEscapePathTestPage.class, "../../../ParentResourceTest.js");

		assertContains(tester.getRequestCycle().urlFor(ref).toString(), expectedResourceUrl());
	}

	public void testRequestHandlingOfResourceUrlWithEscapeStringInside()
	{
		tester.getApplication().getResourceSettings().setParentFolderPlaceholder("-updir-");
		requestHandlingOfResourceUrlWithEscapeStringInside();

		tester.getApplication().getResourceSettings().setParentFolderPlaceholder("$up$");
		requestHandlingOfResourceUrlWithEscapeStringInside();
	}

	private void requestHandlingOfResourceUrlWithEscapeStringInside()
	{
		tester.getRequest().setUrl(Url.parse("wicket/" + expectedResourceUrl()));
		InputStream i = getClass().getClassLoader().getResourceAsStream("ParentResourceTest.js");
		i = getClass().getClassLoader().getResourceAsStream("/ParentResourceTest.js");

		tester.processRequest();
		tester.assertNoErrorMessage();

		String res = new String(tester.getLastResponse().getBinaryResponse());
		assertEquals("// ParentResourceTest.js", res);
	}

	private String expectedResourceUrl()
	{
		final CharSequence escapeSequence = tester.getApplication()
			.getResourceSettings()
			.getParentFolderPlaceholder();

		final StringBuilder url = new StringBuilder();
		url.append("resource/org.apache.wicket.ParentResourceEscapePathTestPage/");

		for (int i = 0; i < 3; i++)
		{
			url.append(escapeSequence);
			url.append('/');
		}
		url.append("ParentResourceTest.js");

		return url.toString();
	}
}
