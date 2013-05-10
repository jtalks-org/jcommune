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
package org.jtalks.jcommune.web.util;

import org.jtalks.jcommune.model.entity.JCUser;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.RedirectStrategy;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.testng.Assert.assertEquals;

/**
 * @author Andrei Alikov
 */
public class UserAuthenticationFailureHandlerTest {
    private UserAuthenticationFailureHandler handler;

    @BeforeMethod
    public void setUp() throws Exception {
        handler = new UserAuthenticationFailureHandler();
    }

    @Test
    public void testOnAuthenticationFailureShouldRedirectToLoginPage() throws Exception {
        JCUser user = new JCUser("username", "email", "password");
        Authentication auth = new TestingAuthenticationToken(user, null);

        AuthenticationException exception = new BadCredentialsException("Password doesn't match!");
        exception.setAuthentication(auth);

        RedirectStrategy redirectStrategy = mock(RedirectStrategy.class);
        handler.setRedirectStrategy(redirectStrategy);
        handler.setDefaultFailureUrl("/badlogin?login_error=1");
        handler.setUsernameSessionAttribute("j_user_name");

        HttpServletRequest request = new MockHttpServletRequest();
        HttpServletResponse response = new MockHttpServletResponse();
        handler.onAuthenticationFailure(request, response, exception);

        verify(redirectStrategy).sendRedirect(request, response, "/badlogin?login_error=1");
        assertEquals(request.getSession().getAttribute(handler.getUsernameSessionAttribute()), "username");
    }
}
