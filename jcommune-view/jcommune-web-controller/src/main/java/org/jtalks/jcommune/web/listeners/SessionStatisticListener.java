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

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Custom session listener implementation to track active user sessions
 *
 * @author Elena Lepaeva
 */
public class SessionStatisticListener implements HttpSessionListener {
    /**
     * It's static because 2 listeners are created - one by web server, one by Spring. We'll need to get rid
     * of this hack.
     */
    private static volatile AtomicLong totalActiveSessions = new AtomicLong(0);

    /**
     * @return active sessions count
     */
    public long getTotalActiveSessions() {
        return totalActiveSessions.get();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sessionCreated(HttpSessionEvent se) {
        totalActiveSessions.incrementAndGet();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void sessionDestroyed(HttpSessionEvent se) {
        /**
         * Tomcat may not invalidate HTTP session on server restart while counter variable
         * will be set to 0 on class reload. So we can quickly get our session count negative when
         * persisted sessions will expire. This check provides us with a self-correcting facility
         * to overcome this problem
         **/
        if (totalActiveSessions.get() > 0) {
            totalActiveSessions.decrementAndGet();
        }
    }
}
