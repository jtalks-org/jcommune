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
import org.jtalks.jcommune.service.ComponentService;
import org.jtalks.jcommune.service.PluginService;
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

    private PluginService pluginService;
    private ComponentService componentService;

    public AuthInterceptor(PluginService pluginService, ComponentService componentService) {
        this.pluginService = pluginService;
        this.componentService = componentService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws IOException, ServletException {
        SimpleAuthenticationPlugin authPlugin = getAuthPlugin();
        if (request.getMethod().equalsIgnoreCase("POST") && authPlugin != null) {
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
        long componentId = componentService.getComponentOfForum().getId();
        List<Plugin> plugins = pluginService.getPlugins(componentId);
        for (Plugin plugin : plugins) {
            if (plugin instanceof SimpleAuthenticationPlugin) {
                return (SimpleAuthenticationPlugin) plugin;
            }
        }
        return null;
    }
}
