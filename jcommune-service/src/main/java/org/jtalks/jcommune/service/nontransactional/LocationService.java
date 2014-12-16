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
import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.plugin.api.service.PluginLocationService;
import org.jtalks.jcommune.service.UserService;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Stores and tracks user's location on the forum.
 * As for now  is mostly used to show who's browsing the topic/branch/etc.
 *
 * @author Andrey Kluev
 */
@Component
public class LocationService implements PluginLocationService {
    private UserService userService;
    private SessionRegistry sessionRegistry;
    private Map<JCUser, String> registerUserMap = new ConcurrentHashMap<>();

    /**
     * @param userService     to figure out the current user
     * @param sessionRegistry session registry to get all the users logged in
     */
    public LocationService(UserService userService, SessionRegistry sessionRegistry) {
        this.userService = userService;
        this.sessionRegistry = sessionRegistry;
    }

    /**
     * Returns list of user viewing a page with the entity specified.
     * This method will automatically add the current user to the viewing list
     * for entity passed
     *
     * @param entity to get users viewing a page with this entity
     * @return Users, who're viewing the page for entity passed. Will return empty list if
     *         there are no viewers or view tracking is not supported for this entity type
     */
    public List<JCUser> getUsersViewing(Entity entity) {
        List<JCUser> viewList = new ArrayList<>();
        JCUser currentUser = userService.getCurrentUser();
        /**
         * This condition does not allow Anonymous add to the map of active users.
         */
        if (!currentUser.isAnonymous()) {
            registerUserMap.put(currentUser, entity.getUuid());
        }

        for (Object o : sessionRegistry.getAllPrincipals()) {
            JCUser user = (JCUser) o;
            if (entity.getUuid().equals(registerUserMap.get(user))) {
                viewList.add(user);
            }
        }
        return viewList;
    }

    /**
     * Clears forum location for the current user.
     * After the call current user will be excluded from all the
     * topic/branch viewer's list until explicitly added
     */
    public void clearUserLocation() {
        registerUserMap.remove(userService.getCurrentUser());
    }
}
