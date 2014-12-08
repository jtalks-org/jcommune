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
import org.mockito.Mock;
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
import static org.mockito.Mockito.when;
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

    @Mock
    TaggedResponseWrapper wrapper;

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
        when(wrapper.getCharacterEncoding()).thenReturn("UTF-8");
    }


    @Test
    public void testParse() throws Exception {
        when(wrapper.getByteArray()).thenReturn("test <jcommune:message></jcommune:message>".getBytes());
        doReturn("parsed").when(parser).getStringByKey(anyString(), any(Locale.class));

        byte[] result = parser.replaceTagByContent(wrapper);

        assertEquals(new String(result), "test parsed");
    }

    @Test
    public void testParseDifferentRegister() throws Exception{
        when(wrapper.getByteArray()).thenReturn("test < JcoMMune:MessAGe></jCommUNE:mESSagE>".getBytes());
        doReturn("parsed").when(parser).getStringByKey(anyString(), any(Locale.class));

        byte[] result = parser.replaceTagByContent(wrapper);

        assertEquals(new String(result), "test parsed");
    }

    @Test
    public void  replaceTagByContentShouldReplaceContentByCorrectMessage() throws Exception {
        when(wrapper.getByteArray()).thenReturn("test <jcommune:message>code1</jcommune:message>".getBytes());
        when(wrapper.getLocale()).thenReturn(Locale.ENGLISH);

        byte[] result = parser.replaceTagByContent(wrapper);

        assertEquals(new String(result), "test message1");
    }

    @Test
    public void replaceTagByContentShouldCorrectlyProcessCodesWithDots() throws Exception {
        when(wrapper.getByteArray()).thenReturn("test <jcommune:message>code.2</jcommune:message>".getBytes());
        when(wrapper.getLocale()).thenReturn(Locale.ENGLISH);

        byte[] result = parser.replaceTagByContent(wrapper);

        assertEquals(new String(result), "test message2");
    }

    @Test
    public void replaceTagByContentShouldCorrectlyProcessCodesWithIllegalCharacters() throws Exception {
        String code = "code.2/&?";
        when(wrapper.getByteArray()).thenReturn(("test <jcommune:message>" + code + "</jcommune:message>").getBytes());
        when(wrapper.getLocale()).thenReturn(Locale.ENGLISH);

        byte[] result = parser.replaceTagByContent(wrapper);

        assertEquals(new String(result), "test " + code);
    }

    @Test
    public void replaceTagByContentShouldUseEnglishAsDefaultIfTranslateNotFound() throws Exception {
        when(wrapper.getByteArray()).thenReturn("test <jcommune:message>code3</jcommune:message>".getBytes());
        when(wrapper.getLocale()).thenReturn(Locale.forLanguageTag("ru"));

        byte[] result = parser.replaceTagByContent(wrapper);

        assertEquals(new String(result), "test message3");
    }

    @Test
    public void replaceTagByContentShouldUseCodeIfTranslateAnsDefaultsNotFound() throws Exception {
        when(wrapper.getByteArray()).thenReturn("test <jcommune:message>code.000</jcommune:message>".getBytes());
        when(wrapper.getLocale()).thenReturn(Locale.ENGLISH);

        byte[] result = parser.replaceTagByContent(wrapper);

        assertEquals(new String(result), "test code.000");
    }

    @Test
    public void testParseShouldNotModifyIncomingBufferAndReturnFalseIfNoMatchesFound() throws Exception {
        String response = "test string";
        when(wrapper.getByteArray()).thenReturn(response.getBytes());
        when(wrapper.getLocale()).thenReturn(Locale.ENGLISH);

        byte[] result = parser.replaceTagByContent(wrapper);

        assertEquals(new String(result), response);
    }
}
