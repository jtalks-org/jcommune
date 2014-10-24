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

import org.jtalks.jcommune.web.filters.wrapper.TaggedResponseWrapper;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.mock.web.MockHttpServletResponse;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.servlet.http.HttpServletResponse;
import java.io.StringReader;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * @author Mikhail Stryzhonok
 */
public class MessageTagParserTest {

    private static final String EN_PROP = "code1=message1\n" +
            "code.2=message2\n" +
            "code3=message3\n" +
            "code4=message4";
    private static final String RU_PROP = "code1=сообщение1\n";

    @Spy
    private MessageTagParser parser = new MessageTagParser();
    private ResourceBundle enBundle;
    private ResourceBundle ruBundle;

    @BeforeClass
    public void loadBundles() throws Exception{
        enBundle = new PropertyResourceBundle(new StringReader(EN_PROP));
        ruBundle = new PropertyResourceBundle(new StringReader(RU_PROP));
    }

    @BeforeMethod
    public void init() {
        MockitoAnnotations.initMocks(this);
        doReturn(enBundle).when(parser).getBundle(eq(Locale.forLanguageTag("en")));
        doReturn(ruBundle).when(parser).getBundle(eq(Locale.forLanguageTag("ru")));
    }


//    @Test
//    public void testParse() {
//        StringBuffer incoming = new StringBuffer("test <jcommune:message locale=\"en\"></jcommune:message>");
//        doReturn("parsed").when(parser).getStringByKey(anyString(), any(Locale.class));
//
//        boolean result = parser.replaceTagByContent(incoming);
//
//        assertTrue(result);
//        assertEquals(incoming.toString(), "test parsed");
//    }
//
//    @Test
//    public void testParseDifferentRegister() {
//        StringBuffer incoming = new StringBuffer("test < JcoMMune:MessAGe LOCALE=\"en\"></jCommUNE:mESSagE>");
//        doReturn("parsed").when(parser).getStringByKey(anyString(), any(Locale.class));
//
//        boolean result = parser.replaceTagByContent(incoming);
//
//        assertTrue(result);
//        assertEquals(incoming.toString(), "test parsed");
//    }
//
//    @Test
//    public void  replaceTagByContentShouldReplaceContentByCorrectMessage() {
//        StringBuffer incoming = new StringBuffer("test <jcommune:message locale=\"en\">code1</jcommune:message>");
//
//        boolean result = parser.replaceTagByContent(incoming);
//
//        assertTrue(result);
//        assertEquals(incoming.toString(), "test message1");
//    }
//
//    @Test
//    public void replaceTagByContentShouldCorrectlyProcessCodesWithDots() {
//        StringBuffer incoming = new StringBuffer("test <jcommune:message locale=\"en\">code.2</jcommune:message>");
//
//        boolean result = parser.replaceTagByContent(incoming);
//
//        assertTrue(result);
//        assertEquals(incoming.toString(), "test message2");
//    }
//
//    @Test
//    public void replaceTagByContentShouldCorrectlyProcessCodesWithIllegalCharacters() {
//        String code = "code.2/&?";
//        StringBuffer incoming = new StringBuffer("test <jcommune:message locale=\"en\">" + code + "</jcommune:message>");
//
//        boolean result = parser.replaceTagByContent(incoming);
//
//        assertTrue(result);
//        assertEquals(incoming.toString(),"test " + code);
//    }
//
//    @Test
//    public void replaceTagByContentShouldUseEnglishAsDefaultIfTranslateNotFound() {
//        StringBuffer incoming = new StringBuffer("test <jcommune:message locale=\"ru\">code3</jcommune:message>");
//
//        boolean result = parser.replaceTagByContent(incoming);
//
//        assertTrue(result);
//        assertEquals(incoming.toString(), "test message3");
//    }
//
//    @Test
//    public void replaceTagByContentShouldUseCodeIfTranslateAnsDefaultsNotFound() {
//        StringBuffer incoming = new StringBuffer("test <jcommune:message locale=\"en\">code.000</jcommune:message>");
//
//        boolean result = parser.replaceTagByContent(incoming);
//
//        assertTrue(result);
//        assertEquals(incoming.toString(), "test code.000");
//    }
//
//    @Test
//    public void testParseShouldNotModifyIncomingBufferAndReturnFalseIfNoMatchesFound() {
//        StringBuffer incoming = new StringBuffer("test string");
//        String expected = incoming.toString();
//
//        boolean result = parser.replaceTagByContent(incoming);
//
//        assertFalse(result);
//        assertEquals(incoming.toString(), expected);
//    }
}
