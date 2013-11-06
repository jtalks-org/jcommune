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
package org.jtalks.jcommune.web.interceptors;

import org.mockito.Mock;
import org.springframework.web.HttpRequestHandler;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.jgroups.util.Util.assertTrue;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * 
 * @author Andrey Ivanov
 *
 */
public class LocalInterceptorTest {
    @Mock
    private LocaleChangeInterceptor springInterceptor;
    @Mock
    private HttpRequestHandler handler;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    private LocaleInterceptor localeInterceptor;
    
    @BeforeMethod
    public void init() {
        initMocks(this);
        localeInterceptor = new LocaleInterceptor(springInterceptor);
    }
    
    @Test
    public void preHandleWithIncorrectLanguage() throws Exception {
        when(springInterceptor.getParamName()).thenReturn("lang");
        when(request.getParameter("lang")).thenReturn("<>");
        assertTrue(localeInterceptor.preHandle(request, response, handler));
        verify(springInterceptor, never()).preHandle(request, response, handler);
    }

    @Test
    public void preHandleWithCorrectLanguage() throws Exception {
        when(springInterceptor.getParamName()).thenReturn("lang");
        when(request.getParameter("lang")).thenReturn("en");
        localeInterceptor.preHandle(request, response, handler);
        verify(springInterceptor, times(1)).preHandle(request, response, handler);
    }
}
