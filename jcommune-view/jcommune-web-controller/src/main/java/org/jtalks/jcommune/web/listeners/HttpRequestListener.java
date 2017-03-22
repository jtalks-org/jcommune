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

import org.jtalks.jcommune.service.security.SecurityService;

import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;

import static org.jtalks.jcommune.web.util.AppContextUtils.getBeanFormApplicationContext;

/**
 * Since SecurityService contain thread-local storage for JCUser objects and we are using
 * Tomcat's thread pool some threads may contain thread-local variables and Tomcat can't
 * clean them after application stop. So we should clean thread-local storage after each Http request.
 *
 * @author Oleg Tkachenko
 */
public class HttpRequestListener implements ServletRequestListener {
    @Override
    public void requestDestroyed(ServletRequestEvent sre) {
        SecurityService securityService = getBeanFormApplicationContext(sre.getServletContext(), SecurityService.class);
        securityService.cleanThreadLocalStorage();
    }

    @Override
    public void requestInitialized(ServletRequestEvent sre) {
        // DO NOTHING
    }
}
