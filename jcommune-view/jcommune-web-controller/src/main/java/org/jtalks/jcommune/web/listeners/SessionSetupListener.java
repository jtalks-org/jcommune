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

import org.jtalks.jcommune.model.entity.JCommuneProperty;
import org.jtalks.jcommune.service.nontransactional.LocationService;
import org.jtalks.jcommune.web.controller.AdministrationController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import java.util.concurrent.TimeUnit;

/**
 * Performs initial session setup.
 * Any general session settings are to be set here.
 *
 * @author Evgeniy Naumenko
 * @author Oleg Tkachenko
 */
public class SessionSetupListener implements HttpSessionListener {
    private static volatile JCommuneProperty sessionTimeoutProperty = null;
    private static LocationService locationService = null;
    private static int TIME_OUT_SECONDS;
    private Logger logger = LoggerFactory.getLogger(SessionSetupListener.class);

    /**
     * Sets session timeout for any crated session based on a database-located property.
     *
     * @param se session event to get new session from
     */
    @Override
    public void sessionCreated(HttpSessionEvent se) {
        try {
            if (sessionTimeoutProperty == null) {
                synchronized (this) {
                    if (sessionTimeoutProperty == null){
                        ServletContext context = se.getSession().getServletContext();
                        sessionTimeoutProperty = getBeanFormApplicationContext(context, JCommuneProperty.class, "sessionTimeoutProperty");
                        locationService = getBeanFormApplicationContext(context, LocationService.class, "locationService");
                        int seconds = extractValueFromProperty(sessionTimeoutProperty);
                        /* Zero property value should mean infinitive session.
                        To disable session expiration we should pass negative value here.
                        Setting zero as is results in weird Tomcat behavior.*/
                        TIME_OUT_SECONDS = seconds > 0 ? seconds : -1;
                    }
                }
            }
            se.getSession().setMaxInactiveInterval(TIME_OUT_SECONDS);
        } catch (Exception ex) {
            logger.warn("Bean instantiation error: " + ex);
            throw ex;
        }
    }

    /**
     * If session contain user when destroyed, then we need to clean location info of this user.
     *
     * @param se session event to get session and check for user in security context.
     */
    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        Object principal = getPrincipalFromSession(se.getSession());
        if (principal != null) {
            try {
                locationService.clearUserLocation(principal);
            } catch (Exception ex) {
                logger.warn("Error clearing user location when session being destroyed: " + ex.getMessage());
            }
        }
    }

    /**
     * Returns principal from SecurityContext
     *
     * @param currentSession current session
     * @return {@link Object} authenticated principal.
     */
    private Object getPrincipalFromSession(HttpSession currentSession) {
        Object securityContext = currentSession.getAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY);
        if (securityContext == null) return null;
        Authentication authentication = ((SecurityContext) securityContext).getAuthentication();
        return authentication != null ? authentication.getPrincipal() : null;
    }

    /**
     * Returns bean from application context.
     *
     * @param servletContext to find the web application context for
     * @param tClass         interface or superclass of the actual class.
     * @param beanName       the name of the bean to retrieve
     * @return an instance of the bean
     * @throws IllegalStateException if the root WebApplicationContext could not be found
     * @throws ClassCastException    if the bean found in context is not assignable to the type of tClass
     */
    private static <T> T getBeanFormApplicationContext(ServletContext servletContext, Class<T> tClass, String beanName) {
        WebApplicationContext context = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
        return tClass.cast(context.getBean(beanName));
    }

    /**
     * @param sessionTimeoutProperty property to read
     * @return time in seconds
     */
    private int extractValueFromProperty(JCommuneProperty sessionTimeoutProperty) {
        int value = sessionTimeoutProperty != null ? sessionTimeoutProperty.intValue() : 0;
        return (int) TimeUnit.SECONDS.convert(value, TimeUnit.MINUTES);
    }

    /**
     * Needed in tests and {@link AdministrationController} when sessionTimeoutProperty is updated.
     */
    public static void resetSessionTimeoutProperty(){
        sessionTimeoutProperty = null;
    }
}
