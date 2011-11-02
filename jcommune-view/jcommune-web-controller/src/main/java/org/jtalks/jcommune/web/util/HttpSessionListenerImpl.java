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
package org.jtalks.jcommune.web.util;

import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

/**
 * Custom session listener implementation to track active user sessions
 *
 * @author Elena Lepaeva
 */
public class HttpSessionListenerImpl implements HttpSessionListener {

    private static long totalActiveSessions;

    /**
     * @return active sessions count
     */
    public static long getTotalActiveSessions() {
        return totalActiveSessions;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sessionCreated(HttpSessionEvent se) {
        totalActiveSessions++;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        WebApplicationContext wac = WebApplicationContextUtils.
                getRequiredWebApplicationContext(se.getSession().getServletContext());
        SessionRegistryImpl sessionRegistry = (SessionRegistryImpl) wac.getBean("sessionRegistry");
        sessionRegistry.removeSessionInformation(se.getSession().getId());
        totalActiveSessions--;
    }
}
