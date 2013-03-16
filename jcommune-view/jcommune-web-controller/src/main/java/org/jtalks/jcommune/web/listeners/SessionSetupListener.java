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
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import java.util.concurrent.TimeUnit;

/**
 * Performs initial session setup.
 * Any general session settings are to be set here.
 *
 * @author Evgeniy Naumenko
 */
public class SessionSetupListener implements HttpSessionListener {

    /**
     * Sets session timeout for any crated session based on a database-located property.
     * As for now it affects registered user only.
     *
     * @param se session event to get new session from
     */
    @Override
    public void sessionCreated(HttpSessionEvent se) {
        ServletContext servletContext = se.getSession().getServletContext();
        ApplicationContext ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
        JCommuneProperty property = (JCommuneProperty) ctx.getBean("sessionTimeoutProperty");
        int timeoutInSeconds = (int) TimeUnit.SECONDS.convert(property.intValue(), TimeUnit.MINUTES);
        if (timeoutInSeconds > 0) {
            se.getSession().setMaxInactiveInterval(timeoutInSeconds);
        } else {
            /* Zero property value should mean infinitive session.
             To disable session expiration we should pass negative value here.
             Setting zero as is results in weird Tomcat behavior.*/
            se.getSession().setMaxInactiveInterval(-1);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        //noop
    }
}
