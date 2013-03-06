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
import java.util.ArrayList;
import java.util.List;

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
    private JCommuneProperty componentSapeLinksCountProperty;
    private JCommuneProperty componentSapeHostProperty;
    private JCommuneProperty componentSapeTimeoutProperty;
    private JCommuneProperty componentSapeShowDummyLinksProperty;

    private static Sape sape;
    private static boolean enabledSape;

    /**
     * Initializes {@link javasape.Sape} object.
     */
    private boolean initSape() {
        String accountId = componentSapeAccountProperty.getValue();
        String host = componentSapeHostProperty.getValue();
        if (componentSapeShowDummyLinksProperty.booleanValue() || accountId == null || accountId.trim().isEmpty() ||
                host == null || host.trim().isEmpty()) {
            return false;
        }
        sape = new Sape(componentSapeAccountProperty.getValue(),
                componentSapeHostProperty.getValue(),
                Integer.parseInt(componentSapeTimeoutProperty.getValue()),
                Integer.parseInt(componentSapeLinksCountProperty.getValue()));
        return true;
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
            enabledSape = initSape();
        }
        if (enabledSape) {
            boolean sapeOnMainPageEnable = false;
            String sapeOnMainPageEnableValue = componentSapeOnMainPageEnableProperty.getValue();
            if (sapeOnMainPageEnableValue != null && !sapeOnMainPageEnableValue.trim().isEmpty()) {
                sapeOnMainPageEnable = Boolean.valueOf(sapeOnMainPageEnableValue);
            }
            //do not apply to the redirected requests: it's unnecessary and may cause error pages to work incorrectly
            if ((sapeOnMainPageEnable || !modelAndView.getViewName().equals("/")) &&
                    !modelAndView.getViewName().contains("redirect:")) {
                SapePageLinks pageLinks = sape.getPageLinks(request.getRequestURI(), request.getCookies());
                List<String> sapeLinks = new ArrayList<String>();
                for (int i = 1; i <= componentSapeLinksCountProperty.intValue(); i++) {
                    sapeLinks.add(pageLinks.render(1));
                }
                modelAndView.addObject("sapeLinks", sapeLinks);
            }
        }
    }

    /**
     * Gets JavaSape account ID property
     *
     * @return account ID
     */
    public JCommuneProperty getComponentSapeAccountProperty() {
        return componentSapeAccountProperty;
    }

    /**
     * Sets JavaSape account ID property
     *
     * @param componentSapeAccountProperty account ID
     */
    public void setComponentSapeAccountProperty(JCommuneProperty componentSapeAccountProperty) {
        this.componentSapeAccountProperty = componentSapeAccountProperty;
    }

    /**
     * Gets flag to show javasape content on main page property
     *
     * @return show javasape content on main page property or not
     */
    public JCommuneProperty getComponentSapeOnMainPageEnableProperty() {
        return componentSapeOnMainPageEnableProperty;
    }

    /**
     * Sets show javasape content on main page property
     *
     * @param componentSapeOnMainPageEnableProperty
     *         flag value
     */
    public void setComponentSapeOnMainPageEnableProperty(JCommuneProperty componentSapeOnMainPageEnableProperty) {
        this.componentSapeOnMainPageEnableProperty = componentSapeOnMainPageEnableProperty;
    }

    /**
     * Gets sape links count for one request to Sape service
     *
     * @return links count
     */
    public JCommuneProperty getComponentSapeLinksCountProperty() {
        return componentSapeLinksCountProperty;
    }

    /**
     * Sets ape links count for one request to Sape service
     *
     * @param componentSapeLinksCountProperty
     *         links count
     */
    public void setComponentSapeLinksCountProperty(JCommuneProperty componentSapeLinksCountProperty) {
        this.componentSapeLinksCountProperty = componentSapeLinksCountProperty;
    }

    /**
     * Gets current instance host
     *
     * @return host name
     */
    public JCommuneProperty getComponentSapeHostProperty() {
        return componentSapeHostProperty;
    }

    /**
     * Sets current instance host
     *
     * @param componentSapeHostProperty host name
     */
    public void setComponentSapeHostProperty(JCommuneProperty componentSapeHostProperty) {
        this.componentSapeHostProperty = componentSapeHostProperty;
    }

    /**
     * Gets Sape request timeout
     *
     * @return timeout
     */
    public JCommuneProperty getComponentSapeTimeoutProperty() {
        return componentSapeTimeoutProperty;
    }

    /**
     * Sets Sape request timeout
     *
     * @param componentSapeTimeoutProperty timeout
     */
    public void setComponentSapeTimeoutProperty(JCommuneProperty componentSapeTimeoutProperty) {
        this.componentSapeTimeoutProperty = componentSapeTimeoutProperty;
    }

    /**
     * Gets flag whether show dummy links for SAPE
     *
     * @return show dummy links
     */
    public JCommuneProperty getComponentSapeShowDummyLinksProperty() {
        return componentSapeShowDummyLinksProperty;
    }

    /**
     * Sets flag whether show dummy links for SAPE
     *
     * @param componentSapeShowDummyLinksProperty
     *         timeout
     */
    public void setComponentSapeShowDummyLinksProperty(JCommuneProperty componentSapeShowDummyLinksProperty) {
        this.componentSapeShowDummyLinksProperty = componentSapeShowDummyLinksProperty;
    }
}
