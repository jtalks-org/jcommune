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
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.jtalks.jcommune.model.entity.JCommuneProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * <b>Objectives:</b> put some links to every page user views.<br/>
 * <b>Details: </b> initializes {@link javasape.Sape} object on application start. JavaSapeInterceptor sends request
 * to SAPE.ru  provider with specific account ID and sets sape content to parameters of each request.
 *
 * @author elepaeva
 * @see <a href="http://jira.jtalks.org/browse/JC-1254">Related JIRA ticket</a>
 */
public class SapeInterceptor extends HandlerInterceptorAdapter {
    private final Logger logger = LoggerFactory.getLogger(SapeInterceptor.class);
    private JCommuneProperty componentSapeAccountProperty;
    private JCommuneProperty componentSapeOnMainPageEnableProperty;
    private JCommuneProperty componentSapeLinksCountProperty;
    private JCommuneProperty componentSapeHostProperty;
    private JCommuneProperty componentSapeTimeoutProperty;
    private JCommuneProperty componentSapeShowDummyLinksProperty;
    private JCommuneProperty componentSapeEnableServiceProperty;

    private volatile Sape sape;

    private String dummyLinks = "";

    public SapeInterceptor() {
        initDummyLinks();
    }

    /**
     * Set sape content to request parameters.
     *
     * @param request      current HTTP request
     * @param response     current HTTP response
     * @param handler      chosen handler to execute, for type and/or instance examination
     * @param modelAndView the {@code ModelAndView} that the handler returned (can also be {@code null})
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response,
                           Object handler, ModelAndView modelAndView) throws Exception {
        if (!componentSapeEnableServiceProperty.booleanValue() || modelAndView == null ||
                modelAndView.getViewName() == null ||
                //do not apply to the redirected requests: it's unnecessary and may cause error pages to work
                // incorrectly
                (!componentSapeOnMainPageEnableProperty.booleanValue() &&
                        modelAndView.getViewName().equals("sectionList")) ||
                modelAndView.getViewName().contains("redirect:")) {
            return;
        }

        String sapeLinksAsString = "";
        if (componentSapeShowDummyLinksProperty.booleanValue()) {
            sapeLinksAsString = dummyLinks;
        } else if (initSape()) {
            SapePageLinks pageLinks = sape.getPageLinks(request.getRequestURI(), request.getCookies());
            sapeLinksAsString = pageLinks.render();
        }
        modelAndView.addObject("sapeLinks", sapeLinksAsString);
    }

    /**
     * Initializes {@link javasape.Sape} object.
     */
    private boolean initSape() {
        if (sape != null) {
            return true;
        }
        String accountId = componentSapeAccountProperty.getValue();
        String host = componentSapeHostProperty.getValue();
        if (StringUtils.isBlank(accountId) || StringUtils.isBlank(host)) {
            return false;
        }
        sape = new Sape(componentSapeAccountProperty.getValue(),
                componentSapeHostProperty.getValue(),
                Integer.parseInt(componentSapeTimeoutProperty.getValue()),
                Integer.parseInt(componentSapeLinksCountProperty.getValue()));
        return true;
    }

    private void initDummyLinks() {
        String dummyLinksLocation = "/org/jtalks/jcommune/web/interceptors/DummySapeLinks.txt";
        try {
            dummyLinks = IOUtils.toString(new ClassPathResource(dummyLinksLocation).getInputStream());
        } catch (IOException e) {
            logger.error("Could not find resource [{}] in classpath. This is clearly a bug", dummyLinksLocation);
        }
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
     * Sets show javasape content on main page property
     *
     * @param componentSapeOnMainPageEnableProperty
     *         flag value
     */
    public void setComponentSapeOnMainPageEnableProperty(JCommuneProperty componentSapeOnMainPageEnableProperty) {
        this.componentSapeOnMainPageEnableProperty = componentSapeOnMainPageEnableProperty;
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
     * Sets current instance host
     *
     * @param componentSapeHostProperty host name
     */
    public void setComponentSapeHostProperty(JCommuneProperty componentSapeHostProperty) {
        this.componentSapeHostProperty = componentSapeHostProperty;
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
     * Sets flag whether show dummy links for SAPE
     *
     * @param componentSapeShowDummyLinksProperty
     *         timeout
     */
    public void setComponentSapeShowDummyLinksProperty(JCommuneProperty componentSapeShowDummyLinksProperty) {
        this.componentSapeShowDummyLinksProperty = componentSapeShowDummyLinksProperty;
    }

    /**
     * Sets flag whether enable SAPE service
     *
     * @param componentSapeEnableServiceProperty
     *         enable SAPE service flag
     */
    public void setComponentSapeEnableServiceProperty(JCommuneProperty componentSapeEnableServiceProperty) {
        this.componentSapeEnableServiceProperty = componentSapeEnableServiceProperty;
    }
}
