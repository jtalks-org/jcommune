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

import org.jtalks.jcommune.web.rememberme.RememberMeCheckService;
import org.jtalks.jcommune.web.rememberme.RememberMeCookieDecoder;
import org.mockito.Mock;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * 
 * @author Anuar_Nurmakanov
 *
 */
public class UsernamePasswordAuthenticationFilterTest {
    @Mock
    private RememberMeCookieDecoder rememberMeCookieDecoder;
    @Mock
    private RememberMeCheckService rememberMeCheckService;
    //
    private UsernamePasswordAuthenticationFilter filter;
    
    @BeforeMethod
    public void init() {
        initMocks(this);
        filter = new UsernamePasswordAuthenticationFilter(rememberMeCookieDecoder, rememberMeCheckService);
    }
    
    @Test
    public void passedRememberMeTokenInCookieShouldBeChecked() throws IOException, ServletException {
        HttpServletRequest request = new MockHttpServletRequest();
        FilterChain filterChain = new MockFilterChain();
        String rememberMeCookieValue = "cookie value";
        String series = "series";
        String token = "token";
        String[] seriesAndToken = new String[] { series, token };
        when(rememberMeCookieDecoder.exctractRememberMeCookieValue(request))
            .thenReturn(rememberMeCookieValue);
        when(rememberMeCookieDecoder.extractSeriesAndToken(rememberMeCookieValue))
            .thenReturn(seriesAndToken);

        filter.doFilter(request, new MockHttpServletResponse(), filterChain);

        verify(rememberMeCheckService).equalWithPersistentToken(series, token);
    }
    
    @Test
    public void passedIncorrectRememberMeTokenInCookieShouldNotBeChecked() throws IOException, ServletException {
        HttpServletRequest request = new MockHttpServletRequest();
        FilterChain filterChain = new MockFilterChain();
        String rememberMeCookieValue = "cookie value";
        String[] seriesAndToken = {"it contains only series"};
        when(rememberMeCookieDecoder.exctractRememberMeCookieValue(request))
            .thenReturn(rememberMeCookieValue);
        when(rememberMeCookieDecoder.extractSeriesAndToken(rememberMeCookieValue))
            .thenReturn(seriesAndToken);

        filter.doFilter(request, new MockHttpServletResponse(), filterChain);

        verify(rememberMeCheckService, never()).equalWithPersistentToken(anyString(), anyString());
    }
    
    @Test
    public void nullRememberMeCookieShouldNotBeChecked() throws IOException, ServletException {
        testIfRememberMeTokenHaveBeenCheckedWithCookie(null);
    }

    @Test
    public void emptyRememberMeCookieShouldNotBeChecked() throws IOException, ServletException {
        testIfRememberMeTokenHaveBeenCheckedWithCookie("");
    }

    @Test
    public void spaceRememberMeCookieShouldNotBeChecked() throws IOException, ServletException {
        testIfRememberMeTokenHaveBeenCheckedWithCookie(" ");
    }

    private void testIfRememberMeTokenHaveBeenCheckedWithCookie(String cookie) throws IOException, ServletException {
        HttpServletRequest request = new MockHttpServletRequest();
        FilterChain filterChain = new MockFilterChain();
        when(rememberMeCookieDecoder.exctractRememberMeCookieValue(request))
                .thenReturn(cookie);

        filter.doFilter(request, new MockHttpServletResponse(), filterChain);

        verify(rememberMeCookieDecoder, never()).extractSeriesAndToken(anyString());
        verify(rememberMeCheckService, never())
                .equalWithPersistentToken(anyString(), anyString());
    }
}
