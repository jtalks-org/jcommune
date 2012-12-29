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
package org.jtalks.jcommune.web.filters;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jtalks.jcommune.web.rememberme.RememberMeCheckService;
import org.jtalks.jcommune.web.rememberme.RememberMeCookieExtractor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

/**
 * 
 * @author Anuar_Nurmakanov
 *
 */
public class LogCookieUsernamePasswordAuthenticationFilterTest {
    @Mock
    private RememberMeCookieExtractor extractor;
    @Mock
    private RememberMeCheckService rememberMeCheckService;
    private LogCookieUsernamePasswordAuthenticationFilter filter;

    @BeforeTest
    public void init() {
        MockitoAnnotations.initMocks(this);
        filter = new LogCookieUsernamePasswordAuthenticationFilter();
        filter.setRememberMeCheckService(rememberMeCheckService);
        filter.setExtractor(extractor);
    }

    @Test
    public void doFilterWithPassedCookieShouldLogRememberMeData() throws IOException, ServletException {
        HttpServletRequest request = new MockHttpServletRequest();
        HttpServletResponse response = new MockHttpServletResponse();
        FilterChain filterChain = new MockFilterChain();
        String rememberMeCookieValue = "cookie value";
        String series = "series";
        String token = "token";
        String[] seriesAndToken = new String[] { series, token };
        when(extractor.exctractRememberMeCookieValue(request))
            .thenReturn(rememberMeCookieValue);
        when(extractor.extractSeriesAndToken(rememberMeCookieValue))
                .thenReturn(seriesAndToken);

        filter.doFilter(request, response, filterChain);

        verify(rememberMeCheckService).checkWithPersistentRememberMeToken(series, token);
    }
    
    @Test
    public void doFilterWithNotPassedCookieShouldNotCheckRememberMeData() throws IOException, ServletException {
        HttpServletRequest request = new MockHttpServletRequest();
        HttpServletResponse response = new MockHttpServletResponse();
        FilterChain filterChain = new MockFilterChain();
        when(extractor.exctractRememberMeCookieValue(request))
            .thenReturn(null);

        filter.doFilter(request, response, filterChain);

        verify(extractor, never()).extractSeriesAndToken(anyString());
        verify(rememberMeCheckService, never())
            .checkWithPersistentRememberMeToken(anyString(), anyString());
    }
}
