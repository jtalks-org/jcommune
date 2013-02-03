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

import org.springframework.mock.web.MockHttpSession;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.servlet.http.HttpSessionEvent;

import static org.testng.Assert.assertEquals;

/**
 * Test for {@link SessionStatisticListener}.
 *
 * @author Elena Lepaeva
 */
public class SessionStatisticListenerTest {
    private SessionStatisticListener listener;
    private HttpSessionEvent event;

    @BeforeMethod
    public void initMethod() {
        event = new HttpSessionEvent(new MockHttpSession());
        listener = new SessionStatisticListener();
    }

    @Test
    public void confirmIncrementSessionCountTest() {
        listener.sessionCreated(event);

        assertEquals(listener.getTotalActiveSessions(), 1);
    }

    @Test
    public void confirmDeletingSessionInformationTest() {
        listener.sessionCreated(event);
        assertEquals(listener.getTotalActiveSessions(), 1);
        listener.sessionDestroyed(event);
        assertEquals(listener.getTotalActiveSessions(), 0);
    }

    @Test
    public void confirmDeletingSessionWithNoSessions() {
        assertEquals(listener.getTotalActiveSessions(), 0);
        listener.sessionDestroyed(event);
        assertEquals(listener.getTotalActiveSessions(), 0);
    }
}
