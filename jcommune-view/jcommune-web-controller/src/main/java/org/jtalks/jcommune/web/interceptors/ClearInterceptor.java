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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Global interceptor works for all pages, clears location of
 * the current user. Location is a certain page user is viewing at the moment
 *
 * @author Andrey Kluev
 */
public class ClearInterceptor extends HandlerInterceptorAdapter {

    private LocationService locationService;

    private static final Logger LOGGER = LoggerFactory.getLogger(ClearInterceptor.class);

    /**
     * @param locationService to operate with current user location on forum
     */
    @Autowired
    public ClearInterceptor(LocationService locationService) {
        this.locationService = locationService;
    }

    /**
     * Clears location current user in forum
     *
     * @param request  HttpServletRequest
     * @param response HttpServletResponse
     * @param handler  handler
     * @return true, as processing should be continued anyway
     */
    @Override
    public boolean preHandle(HttpServletRequest request,
            HttpServletResponse response, Object handler) {
        try {
            locationService.clearUserLocation();
        } catch (Exception e) {
            // failure here should not cause all the web processing chain to be broken
            LOGGER.error("Failed to clear current user's location", e);
        }
        return true;
    }
}
