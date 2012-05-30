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

import org.jtalks.jcommune.service.nontransactional.LocationService;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.HttpRequestHandler;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.testng.Assert.assertTrue;

/**
 * @author Andrey Kluev
 */
public class ClearInterceptorTest {

    private ClearInterceptor interceptor;
    private HttpRequestHandler handler;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private LocationService service;

    @BeforeMethod
    public void setUp() throws Exception {
        handler = mock(HttpRequestHandler.class);
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        service = mock(LocationService.class);
        interceptor = new ClearInterceptor(service);
    }

    @Test
    public void testUserLocationWasCleared() {
        assertTrue(interceptor.preHandle(request, response, handler));
        verify(service).clearUserLocation();
    }
}