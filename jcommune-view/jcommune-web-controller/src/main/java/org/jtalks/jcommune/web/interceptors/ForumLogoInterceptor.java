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

import org.jtalks.jcommune.service.exceptions.ImageProcessException;
import org.jtalks.jcommune.web.controller.AdministrationController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Global interceptor which injects forum logo image data in to the page
 * @author Andrei Alikov
 */
public class ForumLogoInterceptor extends HandlerInterceptorAdapter {
    private final static String LOGO_PROPERTY = "forumLogo";
    private final AdministrationController administrationController;
    private static final Logger LOGGER = LoggerFactory.getLogger(ForumLogoInterceptor.class);

    /**
     * Creates new interceptor
     * @param administrationController controller for getting forum logo image
     */
    public ForumLogoInterceptor(AdministrationController administrationController) {
        this.administrationController = administrationController;
    }

    /**
     * Set forum logo image data to request parameters.
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
        //do not apply to the redirected requests: it's unnecessary and may cause error pages to work incorrectly
        if (modelAndView != null && !modelAndView.getViewName().contains("redirect:")) {
            try {
                modelAndView.addObject(LOGO_PROPERTY, administrationController.getForumLogo());
            } catch (ImageProcessException e) {
                LOGGER.error("Couldn't get forum logo image. " + e.getMessage());
            }
        }
    }

}
