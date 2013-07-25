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
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Class serves to intercepting authentication and registration requests and forwarding them to Poulpe auth plugin.
 *
 * @author Andrey Pogorelov
 */
public class AuthInterceptor extends HandlerInterceptorAdapter {
    private static final String POULPE_NEW_USER_URL = "/user/new/poulpe";
    private static final String POULPE_NEW_USER__AJAX_URL = "/user/new_ajax/poulpe";
    private static final String JC_NEW_USER_URL = "/user/new";
    private static final String JC_NEW_USER_AJAX_URL = "/user/new_ajax";

    private PluginLoader pluginLoader;

    public AuthInterceptor(PluginLoader pluginLoader) {
        this.pluginLoader = pluginLoader;
    }

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws IOException, ServletException {
        SimpleAuthenticationPlugin authPlugin = getAuthPlugin();
        if (request.getMethod().equalsIgnoreCase("POST") && authPlugin != null
                && authPlugin.getState() == Plugin.State.ENABLED) {
            if (request.getRequestURI().equals(JC_NEW_USER_URL)) {
                request.getRequestDispatcher(POULPE_NEW_USER_URL).forward(request, response);
            } else {
                request.getRequestDispatcher(POULPE_NEW_USER__AJAX_URL).forward(request, response);
            }
            return false;
        } else {
            return true;
        }
    }

    private SimpleAuthenticationPlugin getAuthPlugin() {
        Class cl = SimpleAuthenticationPlugin.class;
        PluginFilter pluginFilter = new TypeFilter(cl);
        List<Plugin> plugins = pluginLoader.getPlugins(pluginFilter);
        return !plugins.isEmpty() ? (SimpleAuthenticationPlugin) plugins.get(0) : null;
    }
}
