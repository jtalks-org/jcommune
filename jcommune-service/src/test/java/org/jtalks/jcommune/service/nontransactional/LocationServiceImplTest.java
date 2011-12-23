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
package org.jtalks.jcommune.service.nontransactional;

import org.jtalks.jcommune.model.entity.Topic;
import org.jtalks.jcommune.model.entity.User;
import org.jtalks.jcommune.service.LocationService;
import org.jtalks.jcommune.service.SecurityService;
import org.springframework.security.core.session.SessionRegistry;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

/**
 *
 * @author Andrey Kluev
 */
public class LocationServiceImplTest {
    private Topic topic;
    private LocationService locationService;
    private SecurityService securityService;
    private SessionRegistry sessionRegistry;
    private User user;


    @BeforeMethod
    protected void setUp() {
        securityService = mock(SecurityService.class);
        sessionRegistry = mock(SessionRegistry.class);
        locationService = new LocationServiceImpl(securityService, sessionRegistry);
        user = new User("", "", "");
        topic = new Topic(user, "");
    }

    @Test
    public void testUsersViewing() {
        when(securityService.getCurrentUser()).thenReturn(user);
        List<Object> list = new ArrayList<Object>();
        list.add(user);
        Map<User, String> map = new HashMap<User, String>();
        map.put(user, "");
        locationService.getRegisterUserMap().put(user, "");

        topic.setUuid("");

        locationService.getRegisterUserMap().put(user, "1");
        locationService.getUsersViewing(topic);

        locationService.getRegisterUserMap().put(user, topic.getUuid());
        locationService.getUsersViewing(topic);

        locationService.getUsersViewing(topic);
    }

    @Test
    public void testClearUserLocation(){
        when(securityService.getCurrentUser()).thenReturn(user);
        locationService.getRegisterUserMap().put(user,"");
        when(securityService.getCurrentUser()).thenReturn(user);
        
        locationService.clearUserLocation();

        assertEquals(locationService.getRegisterUserMap(),new HashMap<User, String>());
    }

    @Test
        public void testClearUserLocationAnonymous(){
            locationService.getRegisterUserMap().put(user,"");
            when(securityService.getCurrentUser()).thenReturn(user);

            locationService.clearUserLocation();

            assertEquals(locationService.getRegisterUserMap(),new HashMap<User, String>());
        }
}
