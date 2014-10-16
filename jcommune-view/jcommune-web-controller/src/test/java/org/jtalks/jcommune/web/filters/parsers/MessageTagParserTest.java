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
package org.jtalks.jcommune.web.filters.parsers;

import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.Locale;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * @author Mikhail Stryzhonok
 */
public class MessageTagParserTest {
    @Spy
    MessageTagParser parser = new MessageTagParser();

    @BeforeTest
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testParse() {
        StringBuffer incoming = new StringBuffer("test <jcommune:message locale=\"en\"></jcommune:message>");
        doReturn("parsed").when(parser).getStringByKey(anyString(), any(Locale.class));

        boolean result = parser.replaceTagByContent(incoming);

        assertTrue(result);
        assertEquals(incoming.toString(), "test parsed");
    }

    @Test
    public void testParseShouldNotModifyIncomingBufferAndReturnFalseIfNoMatchesFound() {
        StringBuffer incoming = new StringBuffer("test string");
        String expected = incoming.toString();

        boolean result = parser.replaceTagByContent(incoming);

        assertFalse(result);
        assertEquals(incoming.toString(), expected);
    }
}
