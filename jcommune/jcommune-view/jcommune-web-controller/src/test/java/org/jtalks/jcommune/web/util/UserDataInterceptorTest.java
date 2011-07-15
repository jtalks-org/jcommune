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

import org.jtalks.jcommune.service.PrivateMessageService;
import org.jtalks.jcommune.service.SecurityService;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

/**
 * @author Kirill Afonin
 */
public class UserDataInterceptorTest {
    private UserDataInterceptor interceptor;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private PrivateMessageService service;
    private SecurityService securityService;
    
    private final int userNewPmCount = 2;
    private final String curUserEncodedName = "curUserEncodedName";
    
    @BeforeMethod
    public void setUp() throws Exception {
        service = mock(PrivateMessageService.class);
        securityService = mock(SecurityService.class);
        interceptor = new UserDataInterceptor(service, securityService);
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
    }

    @Test
    public void testPostHandle() throws Exception {
        when(service.currentUserNewPmCount()).thenReturn(userNewPmCount);
        when(securityService.getCurrentUserEncodedName()).thenReturn(curUserEncodedName);
        
        interceptor.postHandle(request, response, null, null);

        assertEquals(request.getAttribute("newPmCount"), userNewPmCount);
        assertEquals(request.getAttribute("encodedUserName"), curUserEncodedName);
        verify(service).currentUserNewPmCount();
        verify(securityService).getCurrentUserEncodedName();
    }
}
