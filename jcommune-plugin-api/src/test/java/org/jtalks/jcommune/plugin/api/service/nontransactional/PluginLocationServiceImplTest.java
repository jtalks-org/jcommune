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
package org.jtalks.jcommune.plugin.api.service.nontransactional;

import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.model.entity.Topic;
import org.jtalks.jcommune.plugin.api.service.PluginLocationService;
import org.mockito.Mock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.testng.Assert.assertEquals;

/**
 * @author Mikhail Stryzhonok
 */
public class PluginLocationServiceImplTest {
    @Mock
    private PluginLocationService locationService;

    @BeforeMethod
    public void init() {
        initMocks(this);
        PluginLocationServiceImpl service = (PluginLocationServiceImpl)PluginLocationServiceImpl.getInstance();
        service.setLocationService(locationService);
    }

    @Test
    public void testGetUsersViewing() {
        JCUser user = new JCUser("name", "mail@example.com", "password");
        Topic topic = new Topic();
        when(locationService.getUsersViewing(topic)).thenReturn(Arrays.asList(user));

        List<JCUser> users = PluginLocationServiceImpl.getInstance().getUsersViewing(topic);

        assertEquals(users.size(), 1);
        assertEquals(users.get(0), user);
    }
}

