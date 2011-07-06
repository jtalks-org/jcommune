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
package org.jtalks.jcommune.web;

import org.jtalks.jcommune.model.entity.User;
import org.jtalks.jcommune.service.UserService;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Kirill Afonin
 */
public class SuccessfulAuthenticationHandlerTest {
    private UserService userService;
    private SuccessfulAuthenticationHandler handler;

    @BeforeMethod
    public void setUp() throws Exception {
        userService = mock(UserService.class);
        handler = new SuccessfulAuthenticationHandler();
        handler.setUserService(userService);
    }

    @Test
    public void testOnAuthenticationSuccess() throws Exception {
        User user = new User();
        user.setUsername("username");
        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(user);

        handler.onAuthenticationSuccess(new MockHttpServletRequest(), new MockHttpServletResponse(), auth);

        verify(userService).updateLastLoginTime(user);
    }
}
