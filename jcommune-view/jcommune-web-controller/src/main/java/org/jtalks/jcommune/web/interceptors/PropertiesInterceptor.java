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

import org.jtalks.jcommune.model.entity.JCommuneProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @autor masyan
 */
public class PropertiesInterceptor extends HandlerInterceptorAdapter {
    JCommuneProperty componentNameProperty;
    JCommuneProperty componentDescriptionProperty;

    private static final Logger LOGGER = LoggerFactory.getLogger(PropertiesInterceptor.class);

    public void setComponentNameProperty(JCommuneProperty componentNameProperty) {
        this.componentNameProperty = componentNameProperty;
    }

    public void setComponentDescriptionProperty(JCommuneProperty componentDescriptionProperty) {
        this.componentDescriptionProperty = componentDescriptionProperty;
    }

    /**
     * Set properties of component to request parameters.
     *
     * @param request      current HTTP request
     * @param response     current HTTP response
     * @param handler      chosen handler to execute, for type and/or instance examination
     * @param modelAndView the {@code ModelAndView} that the handler returned
     *                     (can also be {@code null})
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) {
        try {
            request.setAttribute("cmpName", componentNameProperty.getValueOfComponent());
            request.setAttribute("cmpDescription", componentDescriptionProperty.getValueOfComponent());
        }
        catch (Exception e) {
            LOGGER.error("Failed to get component properties", e);
        }
    }
}
