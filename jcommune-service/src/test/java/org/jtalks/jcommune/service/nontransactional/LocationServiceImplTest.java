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

import org.jtalks.common.model.entity.Entity;
import org.jtalks.jcommune.model.entity.User;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 *
 * @author Andrey Kluev
 */
public class LocationServiceImplTest {
    private Entity entity;
    private LocationServiceImpl locationServiceImpl;
    private User user;


    @BeforeMethod
    protected void setUp() {
        entity = mock(Entity.class);
        locationServiceImpl = new LocationServiceImpl();
        user = new User("", "", "");
    }

    @Test
    public void testActiveRegistryUserList() {
        List<Object> list = new ArrayList<Object>();
        list.add(user);
        Map<User, String> map = new HashMap<User, String>();
        map.put(user, "");
        locationServiceImpl.setRegisterUserMap(map);

        when(entity.getUuid()).thenReturn("");

        locationServiceImpl.getRegisterUserMap().put(user, "1");
        locationServiceImpl.activeRegistryUserList(user, entity, list);

        locationServiceImpl.getRegisterUserMap().put(user, entity.getUuid());
        locationServiceImpl.activeRegistryUserList(user, entity, list);

        locationServiceImpl.activeRegistryUserList(user, entity, list);
    }
}
