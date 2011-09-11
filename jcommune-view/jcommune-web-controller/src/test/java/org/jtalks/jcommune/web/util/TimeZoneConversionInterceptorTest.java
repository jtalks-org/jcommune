/**
 * Copyright (C) 2011  jtalks.org Team
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
 * Also add information on how to contact you by electronic and paper mail.
 * Creation date: Apr 12, 2011 / 8:05:19 PM
 * The jtalks.org Project
 */
package org.jtalks.jcommune.web.util;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.web.servlet.HandlerInterceptor;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.util.TimeZone;

import static org.jtalks.jcommune.web.util.TimeZoneConversionInterceptor.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

/**
 * @author Evgeniy Naumenko
 */
public class TimeZoneConversionInterceptorTest {

    private HttpServletRequest request;
    private HttpServletResponse response;
    private HandlerInterceptor sut;
    private HttpSession session;
    private Cookie cookie;

    @BeforeMethod
    public void setUp() throws Exception {
        request = spy(new MockHttpServletRequest());
        response = new MockHttpServletResponse();
        session = new MockHttpSession();
        when(request.getSession()).thenReturn(session);
        cookie = mock(Cookie.class);
        when(cookie.getName()).thenReturn(GMT_PARAM_NAME);
        sut = new TimeZoneConversionInterceptor();

    }

    @Test
    public void testNegativeOffsetConversion() throws Exception {
        when(cookie.getValue()).thenReturn("-600"); // client specifies value in minutes
        when(request.getCookies()).thenReturn(new Cookie[]{cookie});
        sut.preHandle(request, response, null);
        assertEquals(
                session.getAttribute(GMT_PARAM_NAME),
                "AET",
                "Timezone was converted incorrectly for negative offset");
    }

    @Test
    public void testPositiveOffsetConversion() throws Exception {
        when(cookie.getValue()).thenReturn("420"); // client specifies value in minutes
        when(request.getCookies()).thenReturn(new Cookie[]{cookie});
        sut.preHandle(request, response, null);
        assertEquals(
                session.getAttribute(GMT_PARAM_NAME),
                "America/Boise",
                "Timezone was converted incorrectly for positive offset");
    }

    @Test
    public void testZeroOffsetConversion() throws Exception {
        when(cookie.getValue()).thenReturn("0"); // client specifies value in minutes
        when(request.getCookies()).thenReturn(new Cookie[]{cookie});
        sut.preHandle(request, response, null);
        assertEquals(
                session.getAttribute(GMT_PARAM_NAME),
                "Africa/Abidjan",
                "Timezone was converted incorrectly for zero offset");
    }

    @Test
    public void testNoCookiesReceived() throws Exception {
        when(request.getCookies()).thenReturn(new Cookie[]{});
        sut.preHandle(request, response, null);
        assertEquals(
                session.getAttribute(GMT_PARAM_NAME),
                TimeZone.getDefault().getID(),
                "Default timezone was not set when no cookies specified");
    }

    @Test
    public void testNoCookiesAtAll() throws Exception {
        when(request.getCookies()).thenReturn(null);
        sut.preHandle(request, response, null);
        assertEquals(
                session.getAttribute(GMT_PARAM_NAME),
                TimeZone.getDefault().getID(),
                "Default timezone was not set when no cookies specified");
    }

    @Test
    public void testIllegalCookieFormat() throws Exception {
        when(cookie.getValue()).thenReturn("wrong timezone offset");
        when(request.getCookies()).thenReturn(new Cookie[]{cookie});
        sut.preHandle(request, response, null);
        assertEquals(
                session.getAttribute(GMT_PARAM_NAME),
                TimeZone.getDefault().getID(),
                "Default timezone was not set when timezone offset has wrong format");
    }
}
