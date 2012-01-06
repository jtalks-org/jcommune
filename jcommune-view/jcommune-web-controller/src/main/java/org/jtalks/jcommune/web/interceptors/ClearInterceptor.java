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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Global interceptor works for all pages,
 * clear location current user
 *
 * @author Andrey Kluev
 */
public class ClearInterceptor extends HandlerInterceptorAdapter {

    private LocationService locationService;

    /**
     * Constructor clearInterceptor
     *
     * @param locationService autowired object from Spring Context
     */
    @Autowired
    public ClearInterceptor(LocationService locationService) {
        this.locationService = locationService;
    }

    /**
     * Drops location current user in forum
     *
     * @param request  HttpServletRequest
     * @param response HttpServletResponse
     * @param handler  handler
     * @return true
     */
    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response, Object handler) {
        /**
         * This condition is necessary because interceptors work designed to challenge a controller,
         * if the page is present avatar, after calling the main controller will call controller
         * avatar that will lead to loss of data on the location of the current user.
         * If you delete or change the terms of the controller mapinga avatar,
         * to display all the pages of browsing users will see only the current user.
         */
        if (!request.getRequestURI().endsWith("/avatar")) {
            locationService.clearUserLocation();
        }

        return true;
    }
}
