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

import org.jtalks.jcommune.model.plugins.SimpleAuthenticationPlugin;
import org.jtalks.jcommune.service.plugins.PluginFilter;
import org.jtalks.jcommune.service.plugins.PluginLoader;
import org.jtalks.jcommune.service.plugins.TypeFilter;
import org.mockito.Mock;
import org.springframework.web.HttpRequestHandler;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;

import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.testng.Assert.assertTrue;

public class AuthInterceptorTest {

    private AuthInterceptor interceptor;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private HttpRequestHandler handler;
    @Mock
    private PluginLoader pluginLoader;


    @BeforeMethod
    public void setUp() throws Exception {
        initMocks(this);
        interceptor = new AuthInterceptor(pluginLoader);
    }

    private PluginFilter createFilter(){
        Class cl = SimpleAuthenticationPlugin.class;
        return new TypeFilter(cl);
    }

    @Test
    public void testPreHandlePluginUnavailable() throws Exception {
        when(request.getMethod()).thenReturn("POST");
        when(pluginLoader.getPlugins(createFilter())).thenReturn(Collections.EMPTY_LIST);
        boolean result = interceptor.preHandle(request, response, handler);
        assertTrue(result, "Interceptor should not forward if there are no available plugins.");
    }

}
