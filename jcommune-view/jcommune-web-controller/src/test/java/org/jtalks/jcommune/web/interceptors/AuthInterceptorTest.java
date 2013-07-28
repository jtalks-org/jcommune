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

import org.jtalks.jcommune.model.plugins.Plugin;
import org.jtalks.jcommune.model.plugins.SimpleAuthenticationPlugin;
import org.jtalks.jcommune.service.plugins.PluginFilter;
import org.jtalks.jcommune.service.plugins.PluginLoader;
import org.jtalks.jcommune.service.plugins.TypeFilter;
import org.mockito.Mock;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.HttpRequestHandler;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Collections;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * @author Andrey Pogorelov
 */
public class AuthInterceptorTest {

    private static final String JC_NEW_USER_URL = "/user/new";
    private static final String JC_NEW_USER_AJAX_URL = "/user/new_ajax";

    private AuthInterceptor interceptor;
    private HttpServletRequest request;
    private HttpServletResponse response;
    @Mock
    private HttpRequestHandler handler;
    @Mock
    private PluginLoader pluginLoader;
    @Mock
    private SimpleAuthenticationPlugin plugin;

    @BeforeMethod
    public void setUp() throws Exception {
        initMocks(this);
        interceptor = new AuthInterceptor(pluginLoader);
        response = new MockHttpServletResponse();
    }

    private PluginFilter createFilter(){
        Class cl = SimpleAuthenticationPlugin.class;
        return new TypeFilter(cl);
    }

    @Test
    public void testPreHandlePluginUnavailable() throws Exception {
        request = new MockHttpServletRequest("POST", JC_NEW_USER_URL);
        when(pluginLoader.getPlugins(createFilter())).thenReturn(Collections.EMPTY_LIST);
        boolean result = interceptor.preHandle(request, response, handler);

        assertTrue(result, "Interceptor should not forward if there are no available plugins.");
    }

    @Test
    public void testPreHandlePluginAvailable() throws Exception {
        request = new MockHttpServletRequest("POST", JC_NEW_USER_URL);
        when(plugin.getState()).thenReturn(Plugin.State.ENABLED);
        when(pluginLoader.getPlugins(any(TypeFilter.class))).thenReturn(Arrays.<Plugin>asList(plugin));

        boolean result = interceptor.preHandle(request, response, handler);

        assertFalse(result, "Interceptor should forward request to plugin if there are available plugins.");
    }

    @Test
    public void testPreHandlePluginAvailableLoginAjax() throws Exception {
        request = new MockHttpServletRequest("POST", JC_NEW_USER_AJAX_URL);
        when(plugin.getState()).thenReturn(Plugin.State.ENABLED);
        when(pluginLoader.getPlugins(any(TypeFilter.class))).thenReturn(Arrays.<Plugin>asList(plugin));

        boolean result = interceptor.preHandle(request, response, handler);

        assertFalse(result, "Interceptor should forward request to plugin if there are available plugins.");
    }

}
