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

import static org.testng.AssertJUnit.assertEquals;

/**
 * @author Alexandre Teterin
 *         Date: 04.03.13
 */


public class ExternalLinkTest {

    @Test
    public void testThreeArgsConstructorAndGetters() throws Exception {
        String url = "http://javatalks.ru";
        String title = "title";
        String hint = "hint";
        ExternalLink externalLink = new ExternalLink(url, title, hint);
        assertEquals(url, externalLink.getUrl());
        assertEquals(title, externalLink.getTitle());
        assertEquals(hint, externalLink.getHint());
    }

    @Test
    public void testLinkWithNoProtocol() {
        String url = "javatalks.ru";
        String title = "title";
        String hint = "hint";
        ExternalLink externalLink = new ExternalLink(url, title, hint);
        assertEquals("http://" + url, externalLink.getUrl());
        assertEquals(title, externalLink.getTitle());
        assertEquals(hint, externalLink.getHint());
    }

    @Test
    public void testEmptyLink() {
        String url = "";
        String title = "title";
        String hint = "hint";
        ExternalLink externalLink = new ExternalLink(url, title, hint);
        assertEquals("", externalLink.getUrl());
        assertEquals(title, externalLink.getTitle());
        assertEquals(hint, externalLink.getHint());
    }
}
