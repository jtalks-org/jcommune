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
package org.jtalks.jcommune.web.tags;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.chrono.ISOChronology;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockPageContext;
import org.springframework.mock.web.MockServletContext;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.FixedLocaleResolver;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import java.io.UnsupportedEncodingException;
import java.util.Locale;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

/**
 * @author Evgeniy Naumenko
 */
public class FormattedDateTagTest {

    private PageContext context;
    private HttpServletRequest request;
    private Cookie[] cookies;
    private DateTime localDate = new DateTime(2011, 10, 30, 20, 15, 10, 5);
    private DateTime utcDate = new DateTime(localDate.getZone().convertLocalToUTC(localDate.getMillis(), true));
    private DateTimeFormatter formatter = DateTimeFormat.forPattern(FormattedDate.DEFAULT_DATE_FORMAT_PATTERN);
    private Locale locale = Locale.ENGLISH;

    private FormattedDate tag;

    @BeforeMethod
    public void setUp() {
        request = mock(HttpServletRequest.class);
        context = new MockPageContext(new MockServletContext(), request);
        LocaleResolver resolver = new FixedLocaleResolver(locale);
        when(request.getAttribute(DispatcherServlet.LOCALE_RESOLVER_ATTRIBUTE)).thenReturn(resolver);
        tag = new FormattedDate();
    }

    @Test
    public void testRenderDateWithPositiveOffset() throws JspException, UnsupportedEncodingException {
        cookies = new Cookie[]{new Cookie("foo", "bar"), new Cookie(FormattedDate.GMT_COOKIE_NAME, "60")};
        when(request.getCookies()).thenReturn(cookies);
        String output = this.render();
        assertEquals(output, formatter.withLocale(locale).print(utcDate.minusHours(1)));
    }

    @Test
    public void testRenderDateWithNegativeOffset() throws JspException, UnsupportedEncodingException {
        cookies = new Cookie[]{new Cookie(FormattedDate.GMT_COOKIE_NAME, "-60")};
        when(request.getCookies()).thenReturn(cookies);
        String output = this.render();
        assertEquals(output, formatter.withLocale(locale).print(utcDate.plusHours(1)));
    }

    @Test
    public void testRenderDateWithZeroOffset() throws JspException, UnsupportedEncodingException {
        cookies = new Cookie[]{new Cookie(FormattedDate.GMT_COOKIE_NAME, "0")};
        when(request.getCookies()).thenReturn(cookies);
        String output = this.render();
        assertEquals(output, formatter.withLocale(locale).print(utcDate));
    }

    @Test
    public void testRenderDateWithoutCookies() throws JspException, UnsupportedEncodingException {
        cookies = new Cookie[]{};
        when(request.getCookies()).thenReturn(cookies);
        String output = this.render();
        assertEquals(output, formatter.withLocale(locale).print(utcDate));
    }

    @Test
    public void testRenderDateWithNullCookies() throws JspException, UnsupportedEncodingException {
        when(request.getCookies()).thenReturn(null);
        String output = this.render();
        assertEquals(output, formatter.withLocale(locale).print(utcDate));
    }

    @Test
    public void testRenderDateWithWrongCookiesSet() throws JspException, UnsupportedEncodingException {
        cookies = new Cookie[]{new Cookie(FormattedDate.GMT_COOKIE_NAME, "lol")};
        when(request.getCookies()).thenReturn(cookies);
        String output = this.render();
        assertEquals(output, formatter.withLocale(locale).print(utcDate));
    }

    @Test
    public void testRenderNullDate() throws JspException, UnsupportedEncodingException {
        cookies = new Cookie[]{};
        when(request.getCookies()).thenReturn(cookies);
        tag.setPageContext(context);
        tag.setValue(null);
        tag.doStartTag();
        tag.doEndTag();
        String output = ((MockHttpServletResponse) context.getResponse()).getContentAsString();
        assertEquals(output, "");
    }

    @Test
    public void testSetValueCorrectlyConvertDstDatetime() throws JspException {
        DateTime date = new DateTime(2011, 3, 27, 4, 34, 30, 0, ISOChronology.getInstance(DateTimeZone.forID("Europe/Berlin")));
        tag.setValue(date);
    }
    
    private String render() throws JspException, UnsupportedEncodingException {
        //cannot move it to @Before as cookies should be set first
        tag.setPageContext(context);
        tag.setValue(localDate);
        tag.doStartTag();
        tag.doEndTag();
        return ((MockHttpServletResponse) context.getResponse()).getContentAsString();
    }
}
