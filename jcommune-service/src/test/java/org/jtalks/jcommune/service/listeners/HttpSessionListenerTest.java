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
package org.jtalks.jcommune.service.listeners;

import org.springframework.security.core.session.SessionRegistry;
import org.testng.annotations.*;

import static org.mockito.Mockito.*;
import static org.testng.Assert.assertEquals;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;

/**
 * @author Elena Lepaeva
 */
public class HttpSessionListenerTest {

    private HttpSessionStatisticListenerImpl listener;
    private HttpSessionEvent event;
    private long sessionCount = 3;

    @BeforeClass
    public void init() {
        String sessionId = "123";
        event = mock(HttpSessionEvent.class);
        HttpSession session = mock(HttpSession.class);
        when(session.getId()).thenReturn(sessionId);
        when(event.getSession()).thenReturn(session);
        listener = new HttpSessionStatisticListenerImpl();
        SessionRegistry sessionRegistry = mock(SessionRegistry.class);
        listener.setSessionRegistry(sessionRegistry);
    }

    @BeforeMethod
    public void initMethod() {
        for (int i = 1; i <= sessionCount; i++)
            listener.sessionCreated(event);
    }

    @AfterMethod
    public void destroyMethod() {
        long count = listener.getTotalActiveSessions();
        for (int i = 1; i <= count; i++)
            listener.sessionDestroyed(event);
    }

    @Test
    public void confirmIncrementSessionCountTest() {
        assertEquals(listener.getTotalActiveSessions(), sessionCount);
    }

    @Test
    public void confirmDeletingSessionInformationTest() {
        listener.sessionDestroyed(event);
        assertEquals(listener.getTotalActiveSessions(), sessionCount - 1);
    }
}
