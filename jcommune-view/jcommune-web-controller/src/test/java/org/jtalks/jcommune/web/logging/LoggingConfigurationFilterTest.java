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
package org.jtalks.jcommune.web.logging;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.jtalks.common.security.SecurityService;
import org.jtalks.jcommune.web.filters.LoggingConfigurationFilter;
import org.mockito.Mock;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestWrapper;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author Anuar_Nurmakanov
 */
public class LoggingConfigurationFilterTest {
    @Mock
    private SecurityService securityService;
    @Mock
    private LoggerMdc loggerMdc;
    //
    private LoggingConfigurationFilter loggingConfigurationFilter;

    @BeforeMethod
    public void init() {
        initMocks(this);
        this.loggingConfigurationFilter = new LoggingConfigurationFilter(
                securityService, loggerMdc);
    }

    @Test
    public void userShouldBeRegisteredAndUnregisteredWhenChainEnded() throws IOException, ServletException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        MockFilterChain filterChain = new MockFilterChain();
        String userName = "Shogun";
        when(securityService.getCurrentUserUsername()).thenReturn(userName);

        loggingConfigurationFilter.doFilter(request, response, filterChain);

        verify(loggerMdc).registerUser(userName);
        verify(loggerMdc).unregisterUser();
    }

    @Test
    public void anonymousUserRegisteredAndUnregistered() throws IOException, ServletException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockHttpSession httpSession = spy(new MockHttpSession());
        request.setSession(httpSession);
        MockFilterChain filterChain = new MockFilterChain();

        String userName = StringUtils.EMPTY;
        when(securityService.getCurrentUserUsername()).thenReturn(userName);
        when(httpSession.getId()).thenReturn("AF7823");

        loggingConfigurationFilter.doFilter(request, response, filterChain);

        verify(loggerMdc).registerUser("anonymous-7823");
        verify(loggerMdc).unregisterUser();
    }

    @Test
    public void anonymousUserRegisteredAndUnregisteredWithNullSession() throws IOException, ServletException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.setSession(null);
        MockFilterChain filterChain = new MockFilterChain();

        String userName = StringUtils.EMPTY;
        when(securityService.getCurrentUserUsername()).thenReturn(userName);

        loggingConfigurationFilter.doFilter(request, response, filterChain);

        verify(loggerMdc, never()).registerUser(userName);
        verify(loggerMdc, never()).unregisterUser();
    }
}
