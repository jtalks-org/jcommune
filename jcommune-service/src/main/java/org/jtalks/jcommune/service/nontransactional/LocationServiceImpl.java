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

import org.jtalks.jcommune.model.entity.User;
import org.jtalks.jcommune.service.LocationService;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * class to store user map.
 *
 * @author Andrey Kluev
 */
@Component
public class LocationServiceImpl implements LocationService {
    private Map<User, String> registerUserMap = new HashMap<User, String>();

    /**
     * {@inheritDoc}
     */
    public Map<User, String> getRegisterUserMap() {
        return registerUserMap;
    }

    /**
     * {@inheritDoc}
     */
    public void setRegisterUserMap(Map<User, String> registerUserMap) {
        this.registerUserMap = registerUserMap;
    }
}
