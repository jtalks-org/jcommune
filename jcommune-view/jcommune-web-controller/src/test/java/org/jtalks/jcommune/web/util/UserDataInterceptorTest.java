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

import org.jtalks.jcommune.model.entity.AnonymousUser;
import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.service.PrivateMessageService;
import org.jtalks.jcommune.service.UserService;
import org.jtalks.jcommune.web.interceptors.UserDataInterceptor;
import org.mockito.Mock;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.ModelAndView;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.mockito.Mockito.*;
import static org.testng.Assert.assertEquals;

/**
 * @author Kirill Afonin
 */
public class UserDataInterceptorTest {

    private UserDataInterceptor interceptor;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private PrivateMessageService service;

    @BeforeMethod
    public void setUp() throws Exception {
        service = mock(PrivateMessageService.class);
        interceptor = new UserDataInterceptor(service);
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
    }

    @Test
    public void testUserDataIsSetAfterController() throws Exception {
        int pmCount = 2;
        when(service.currentUserNewPmCount()).thenReturn(pmCount);

        interceptor.postHandle(request, response, null, new ModelAndView("view"));

        assertEquals(request.getAttribute("newPmCount"), pmCount);
        verify(service).currentUserNewPmCount();
    }

    @Test
    public void testPostHandleWithoutCurrentUser() throws Exception {
        when(service.currentUserNewPmCount()).thenReturn(0);

        interceptor.postHandle(request, response, null, new ModelAndView("view"));

        assertEquals(request.getAttribute("newPmCount"), 0);
        verify(service).currentUserNewPmCount();
    }

}
