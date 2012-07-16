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

import java.util.concurrent.TimeUnit;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.web.context.WebApplicationContext;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.jtalks.common.model.entity.Property;
import org.jtalks.jcommune.model.dao.PropertyDao;
import org.jtalks.jcommune.model.entity.JCommuneProperty;

import javax.servlet.http.HttpSessionEvent;

import static org.mockito.MockitoAnnotations.initMocks;
import static org.testng.Assert.assertEquals;
import static org.mockito.Mockito.when;

/**
 * Test for {@link HttpSessionStatisticListener}.
 *
 * @author Elena Lepaeva
 */
public class HttpSessionStatisticListenerTest {
    private static final String PROPERTY_NAME = "property";
    private static final int SESSION_TIMEOUT = 1;
    
    private static final Property oneHourTimeout = new Property(
            PROPERTY_NAME, String.valueOf(SESSION_TIMEOUT));
    
    private HttpSessionStatisticListener listener;
    private HttpSessionEvent event;
    private MockHttpSession session;
    
    @Mock
    private PropertyDao propertyDao;
    
    @Mock
    private WebApplicationContext context;
    
    private JCommuneProperty sessionTimeoutProperty = JCommuneProperty.SESSION_TIMEOUT;
    

    @BeforeMethod
    public void initMethod() {
        initMocks(this);
        sessionTimeoutProperty.setPropertyDao(propertyDao);
        sessionTimeoutProperty.setName(PROPERTY_NAME);
        
        when(context.getBean(Mockito.anyString())).thenReturn(sessionTimeoutProperty);
        
        session = new MockHttpSession();
        session.getServletContext().setAttribute(
                WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, 
                context);
        event = new HttpSessionEvent(session);
        
        listener = new HttpSessionStatisticListener();
    }

    @Test
    public void confirmIncrementSessionCountTest() {
        when(propertyDao.getByName(PROPERTY_NAME)).thenReturn(oneHourTimeout);
        
        listener.sessionCreated(event);

        assertEquals(listener.getTotalActiveSessions(), 1);
        assertEquals(session.getMaxInactiveInterval(),  getTimeoutInSeconds(SESSION_TIMEOUT));
    }

    @Test
    public void confirmDeletingSessionInformationTest() {
        when(propertyDao.getByName(Mockito.anyString())).thenReturn(oneHourTimeout);
        
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
    
    private long getTimeoutInSeconds(int timeoutInHours) {
        long result = TimeUnit.SECONDS.convert(
                timeoutInHours, TimeUnit.HOURS);
        return result;
    }
}
