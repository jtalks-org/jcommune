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
package org.jtalks.jcommune.web.listeners;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSessionEvent;

import org.jtalks.jcommune.model.entity.JCommuneProperty;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import java.util.concurrent.TimeUnit;

/**
 * Custom session listener implementation to track active user sessions
 *
 * @author Elena Lepaeva
 */
public class HttpSessionStatisticListenerImpl implements HttpSessionStatisticListener {

    private static final String SESSION_TIMEOUT_PROPERTY_NAME = "sessionTimeoutProperty";
    
    private static volatile long totalActiveSessions;
    
    /** 
     * We need to inject bean but this listener is not in Spring context.
     * So we will get required beans directly from the Spring context 
     */
    private WebApplicationContext webApplicationContext;
    
    /**
     * Returns Spring context. If context already was set it is returned. If
     * context is null it is set using Spring utility class and servletContext.
     * @param servletContext - servlet context used to get current Spring 
     *      web application context
     * @return Spring context
     */
    private WebApplicationContext getWebApplicationContext(ServletContext servletContext) {
        if (webApplicationContext == null) {
            webApplicationContext = WebApplicationContextUtils
                .getRequiredWebApplicationContext(servletContext);
        }
        return webApplicationContext;
    }

    /**
     * @param webApplicationContext the webApplicationContext to set
     */
    public void setWebApplicationContext(WebApplicationContext webApplicationContext) {
        this.webApplicationContext = webApplicationContext;
    }

    /**
     * @return active sessions count
     */
    public long getTotalActiveSessions() {
        return totalActiveSessions;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void sessionCreated(HttpSessionEvent se) {
        JCommuneProperty sessionTimeoutProperty = (JCommuneProperty) getSpringBean(
                SESSION_TIMEOUT_PROPERTY_NAME,
                se.getSession().getServletContext());
        
        int timeoutInSeconds = (int) TimeUnit.SECONDS.convert(
                sessionTimeoutProperty.intValue(), TimeUnit.HOURS);
        se.getSession().setMaxInactiveInterval(timeoutInSeconds);
        totalActiveSessions++;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void sessionDestroyed(HttpSessionEvent se) {
        /*
        Tomcat may not invalidate HTTP session on server restart while counter variable
        will be set to 0 on class reload. So we can quickly get our session count negative when
        persisted sessions will expire. This check provides us with a self-correcting facility
        to overcome this problem
         */
        if (totalActiveSessions > 0) {
            totalActiveSessions--;
        }
    }
    
    /**
     * Returns Spring manager bean from Spring context.
     * @param name - name of bean
     * @param servletContext - servlet context used to get current Spring 
     *      web application context
     * @return Spring managed bean with specified name or null if it was not 
     *      found
     */
    private Object getSpringBean(String name, ServletContext servletContext) {
        return getWebApplicationContext(servletContext).getBean(name);
    }
}
