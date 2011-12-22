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
import org.jtalks.jcommune.service.LocationService;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class for storing and tracking of users on the forum.
 *
 * @author Andrey Kluev
 */
@Component
public class LocationServiceImpl implements LocationService {
    private Map<User, String> registerUserMap = new HashMap<User, String>();

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<User, String> getRegisterUserMap() {
        return registerUserMap;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setRegisterUserMap(Map<User, String> registerUserMap) {
        this.registerUserMap = registerUserMap;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> activeRegistryUserList(User currentUser, Entity entity,
                                               List<Object> onlineRegisteredUsers) {
        registerUserMap.put(currentUser, entity.getUuid());

        List<String> viewList = new ArrayList<String>();
        for (Object o : onlineRegisteredUsers) {
            User user = (User) o;
            if (registerUserMap.containsKey(user) && registerUserMap.get(user).equals(entity.getUuid())) {
                viewList.add(user.getEncodedUsername());
            }
        }
        return viewList;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clear(User user) {
        getRegisterUserMap().put(user, "");
    }
}
