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

import static org.mockito.Mockito.mock;
import static org.testng.Assert.assertEquals;

import javax.servlet.http.HttpSessionEvent;

import org.testng.annotations.Test;

/**
 * @author Elena Lepaeva
 */
public class HttpSessionListenerTest {

    private static HttpSessionListenerImpl listener;
    private static HttpSessionEvent event = mock(HttpSessionEvent.class);

    @Test
    public void confirmIncrementSessionCountTest() {
        listener = new HttpSessionListenerImpl();
        long sessionCount = 3;
        for (int i = 1; i <= sessionCount; i++)
            listener.sessionCreated(event);
        assertEquals(HttpSessionListenerImpl.getTotalActiveSessions(), sessionCount);
    }

    //todo confirmDeletingSessionInformationTest
}
