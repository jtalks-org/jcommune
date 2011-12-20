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

import org.jtalks.jcommune.model.entity.User;
import org.jtalks.jcommune.service.SecurityService;
import org.jtalks.jcommune.service.nontransactional.LocationServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Global interceptor works for all pages.
 *
 * @author Andrey Kluev
 */
public class ClearInterceptor extends HandlerInterceptorAdapter {

    private final SecurityService securityService;
    private final LocationServiceImpl locationServiceImpl;

    /**
     * Constructor clearInterceptor
     *
     * @param locationServiceImpl autowired object from Spring Context
     * @param securityService autowired object from Spring Context
     */
    @Autowired
    public ClearInterceptor(LocationServiceImpl locationServiceImpl, SecurityService securityService) {
        this.locationServiceImpl = locationServiceImpl;
        this.securityService = securityService;
    }

    /**
     * Drops location current user in forum
     *
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @param handler  handler
     * @return true
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response, Object handler) throws IOException, ServletException {

        User currentUser = securityService.getCurrentUser();
        LocationServiceImpl locationService = new LocationServiceImpl();
        locationService.clear(currentUser);

        return true;
    }
}
