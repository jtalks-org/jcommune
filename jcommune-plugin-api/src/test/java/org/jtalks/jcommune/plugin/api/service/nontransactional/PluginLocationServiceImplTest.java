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
import org.jtalks.jcommune.model.entity.UserInfo;
import org.jtalks.jcommune.plugin.api.service.PluginLocationService;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.List;

import static org.testng.Assert.assertEquals;

/**
 * @author Mikhail Stryzhonok
 */
public class PluginLocationServiceImplTest {
    @Mock
    private PluginLocationService locationService;

    @Test
    public void userShouldBeInViewersList() {
        locationService = Mockito.mock(PluginLocationService.class);
        UserInfo user = new UserInfo(new JCUser("name", "mail@example.com", "password"));
        Topic topic = new Topic();
        Mockito.when(locationService.getUsersViewing(topic)).thenReturn(Collections.singletonList(user));
        ((PluginLocationServiceImpl)PluginLocationServiceImpl.getInstance()).setLocationService(locationService);
        List<UserInfo> users = PluginLocationServiceImpl.getInstance().getUsersViewing(topic);
        assertEquals(users.size(), 1);
        assertEquals(users.get(0), user);
    }
}
