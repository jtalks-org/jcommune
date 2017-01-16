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

import org.jtalks.common.model.entity.Property;
import org.jtalks.jcommune.model.dao.PropertyDao;
import org.jtalks.jcommune.model.entity.JCommuneProperty;
import org.mockito.Mock;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.web.context.WebApplicationContext;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.servlet.http.HttpSessionEvent;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.testng.Assert.assertEquals;

/**
 * @author Evgeniy Naumenko
 */
public class SessionSetupListenerTest {

    private static final String PROPERTY_NAME = "property";


    private SessionSetupListener listener;

    @Mock
    private PropertyDao propertyDao;

    @Mock
    private WebApplicationContext context;

    private MockHttpSession session;

    private HttpSessionEvent event;

    private JCommuneProperty sessionTimeoutProperty = JCommuneProperty.SESSION_TIMEOUT;

    @BeforeMethod
    public void setUp() throws Exception {
        initMocks(this);

        listener = new SessionSetupListener();

        sessionTimeoutProperty.setPropertyDao(propertyDao);
        sessionTimeoutProperty.setName(PROPERTY_NAME);

        when(context.getBean("sessionTimeoutProperty")).thenReturn(sessionTimeoutProperty);

        session = new MockHttpSession();
        session.getServletContext().setAttribute(
                WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE,
                context);

        event = new HttpSessionEvent(session);
        SessionSetupListener.resetSessionTimeoutProperty();
    }

    @Test
    public void testSessionTimeoutSet() throws Exception {
        Property oneHourTimeout = new Property(PROPERTY_NAME, String.valueOf(1));
        when(propertyDao.getByName(PROPERTY_NAME)).thenReturn(oneHourTimeout);
        
        listener.sessionCreated(event);

        int expireTime = (int) TimeUnit.SECONDS.convert(sessionTimeoutProperty.intValue(), TimeUnit.MINUTES);
        assertEquals(session.getMaxInactiveInterval(), expireTime);
    }

    @Test
    public void testSessionZeroTimeoutConversion() throws Exception {
        Property oneHourTimeout = new Property(PROPERTY_NAME, String.valueOf(0));
        when(propertyDao.getByName(PROPERTY_NAME)).thenReturn(oneHourTimeout);
        
        listener.sessionCreated(event);

        assertEquals(session.getMaxInactiveInterval(), -1);
    }

}
