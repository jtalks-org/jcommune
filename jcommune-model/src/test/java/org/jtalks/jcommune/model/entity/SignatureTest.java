/**
 * Copyright (C) 2011  JTalks.org Team
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package org.jtalks.jcommune.model.entity;

import org.testng.annotations.Test;

import static org.jtalks.jcommune.model.entity.Signature.RENDERING_TEMPLATE;
import static org.testng.Assert.assertEquals;

/**
 * @author Evgeniy Naumenko
 */
public class SignatureTest {

    private static final String CONTENT = "content";
    private static final String HTML_CONTENT = "<content/>";
    private static final String HTML_CONTENT_ESCAPED = "&lt;content/&gt;";
    private static final String HYPERLINK_CONTENT = "http://localhost";
    private static final String HYPERLINK_CONTENT_REPLACED = "<a href=\"http://localhost\">http://localhost</a>";

    private Signature signature;

    @Test
    public void testGetContent() {
        signature = new Signature(CONTENT);

        assertEquals(CONTENT, signature.getContent());
    }

    @Test
    public void testRender() {
        signature = new Signature(CONTENT);
        String expected = String.format(RENDERING_TEMPLATE, CONTENT);
        
        String result = signature.render();

        assertEquals(expected, result);
    }

    @Test
    public void testRenderHtml() {
        signature = new Signature(HTML_CONTENT);
        String expected = String.format(RENDERING_TEMPLATE, HTML_CONTENT_ESCAPED);

        String result = signature.render();

        assertEquals(expected, result);
    }

    @Test
    public void testRenderHyperlink() {
        signature = new Signature(HYPERLINK_CONTENT);
        String expected = String.format(RENDERING_TEMPLATE, HYPERLINK_CONTENT_REPLACED);

        String result = signature.render();

        assertEquals(expected, result);
    }

    @Test
    public void testRenderNull() {
        signature = new Signature(null);

        assertEquals("", signature.render());
    }
}
