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

import org.jtalks.common.model.entity.Entity;
import org.jtalks.jcommune.model.entity.UserInfo;
import org.jtalks.jcommune.plugin.api.service.PluginLocationService;

import java.util.List;

/**
 * Tracks user's location on the forum.
 * As for now  is mostly used to show who's browsing the topic/branch/etc.
 *
 * For tracking user location jcommune use classes from service module
 *
 * This class is  singleton because we can't use spring dependency injection mechanism in plugins due plugins can be
 * added or removed in runtime.
 *
 * @author Mikhail Stryzhonok
 */
public class PluginLocationServiceImpl implements PluginLocationService {

    private static final PluginLocationServiceImpl INSTANCE = new PluginLocationServiceImpl();

    private PluginLocationService locationService;

    /** Use {@link #getInstance()}, this class is singleton. */
    private PluginLocationServiceImpl() {

    }

    public static PluginLocationService getInstance() {
        return INSTANCE;
    }

    @Override
    public List<UserInfo> getUsersViewing(Entity entity) {
        return locationService.getUsersViewing(entity);
    }

    public void setLocationService(PluginLocationService locationService) {
        this.locationService = locationService;
    }
}
