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

import javasape.Sape;
import javasape.SapePageLinks;
import org.jtalks.jcommune.model.entity.JCommuneProperty;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Initializes {@link javasape.Sape} object on application start.
 * JavaSapeInterceptor sent request to Javasape provider with specific account ID and
 * sets sape content to parameters of each request.
 *
 * @author elepaeva
 */
public class JavaSapeInterceptor extends HandlerInterceptorAdapter {

    private JCommuneProperty componentSapeAccountProperty;
    private JCommuneProperty componentSapeOnMainPageEnableProperty;

    private static Sape sape;

    /**
     * Initializes {@link javasape.Sape} object.
     *
     * @param componentSapeAccountProperty JavaSape account ID property
     * @param componentSapeOnMainPageEnableProperty
     *                                     Show javasape content on main page property
     */
    public JavaSapeInterceptor(JCommuneProperty componentSapeAccountProperty,
                               JCommuneProperty componentSapeOnMainPageEnableProperty) {
        this.componentSapeAccountProperty = componentSapeAccountProperty;
        this.componentSapeOnMainPageEnableProperty = componentSapeOnMainPageEnableProperty;
    }

    private void initSape() {
        sape = new Sape(componentSapeAccountProperty.getValue(), "javatalks.ru", 1000, 10);
    }

    /**
     * Set sape content to request parameters.
     *
     * @param request      current HTTP request
     * @param response     current HTTP response
     * @param handler      chosen handler to execute, for type and/or instance examination
     * @param modelAndView the {@code ModelAndView} that the handler returned
     *                     (can also be {@code null})
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        if (sape == null) {
            initSape();
        }
        boolean sapeOnMainPageEnable = false;
        String sapeOnMainPageEnableValue = componentSapeOnMainPageEnableProperty.getValue();
        if (sapeOnMainPageEnableValue != null && !sapeOnMainPageEnableValue.trim().isEmpty()) {
            sapeOnMainPageEnable = Boolean.valueOf(sapeOnMainPageEnableValue);
        }
        //do not apply to the redirected requests: it's unnecessary and may cause error pages to work incorrectly
        if ((sapeOnMainPageEnable || !modelAndView.getViewName().equals("/")) &&
                !modelAndView.getViewName().contains("redirect:")) {
            SapePageLinks pageLinks = sape.getPageLinks(request.getRequestURI(), request.getCookies());
            modelAndView.addObject("sapeContent", pageLinks.render(1));
        }
    }
}
