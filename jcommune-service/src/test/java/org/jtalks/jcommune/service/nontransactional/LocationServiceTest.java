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

import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.model.entity.Topic;
import org.jtalks.jcommune.model.entity.UserInfo;
import org.jtalks.jcommune.service.security.SecurityService;
import org.mockito.Mock;
import org.springframework.security.core.session.SessionRegistry;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * @author Andrey Kluev
 */
public class LocationServiceTest {
    @Mock private SessionRegistry sessionRegistry;
    @Mock private SecurityService securityService;
    private JCUser user;
    private Topic topic;
    private LocationService locationService;
    private UserInfo userInfo;

    @BeforeMethod
    protected void setUp() {
        initMocks(this);
        locationService = new LocationService(securityService, sessionRegistry);
        user = randomUser();
        topic = new Topic(user, "Unit test!");
    }

    @Test
    public void shouldContainUsersViewingTheTopic() {
        userInfo = new UserInfo(user);
        when(securityService.getCurrentUserBasicInfo()).thenReturn(userInfo); //returns current user.
        when(sessionRegistry.getAllPrincipals()).thenReturn(Collections.<Object>singletonList(userInfo)); // returns all principals from Session registry
        List<UserInfo> usersViewing = locationService.getUsersViewing(topic);
        assertEquals(usersViewing.size(), 1);
        assertTrue(usersViewing.contains(userInfo));
    }

    @Test
    public void shouldNotContainAnonymousUsers() {
        when(securityService.getCurrentUserBasicInfo()).thenReturn(null); // returns null coz current user is anonymous.
        when(sessionRegistry.getAllPrincipals()).thenReturn(Collections.<Object>singletonList(new UserInfo(user))); // returns all principals from Session registry
        List<UserInfo> usersViewing = locationService.getUsersViewing(topic);
        assertEquals(usersViewing.size(), 0);
    }

    private JCUser randomUser() {
        return new JCUser("username", "email@jtalk.org", "password");
    }
}
