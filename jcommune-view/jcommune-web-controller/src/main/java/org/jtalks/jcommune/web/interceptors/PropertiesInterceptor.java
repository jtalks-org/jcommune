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
 * Sets forum name and descriptions to be shown on all the pages.
 * These properties are set in Poulpe and are stored in a database.
 *
 * @author masyan
 * @author Evgeniy Naumenko
 */
public class PropertiesInterceptor extends HandlerInterceptorAdapter {
    private JCommuneProperty componentNameProperty;
    private JCommuneProperty componentDescriptionProperty;

    /**
     * @param componentDescriptionProperty component description property
     * @param componentNameProperty        component name property
     */
    public PropertiesInterceptor(JCommuneProperty componentNameProperty,
                                 JCommuneProperty componentDescriptionProperty) {
        this.componentDescriptionProperty = componentDescriptionProperty;
        this.componentNameProperty = componentNameProperty;
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
        modelAndView.addObject("cmpName", componentNameProperty.getValueOfComponent());
        modelAndView.addObject("cmpDescription", componentDescriptionProperty.getValueOfComponent());
    }
}
