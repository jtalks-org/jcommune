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
import org.jtalks.jcommune.service.SecurityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Class for storing and tracking of users on the forum.
 * Realizes the possibility of receiving map stores the user and his location,
 * the list of user names on this page and delete the current user of map
 *
 * @author Andrey Kluev
 */
@Component
public class LocationServiceImpl implements LocationService {
    private SecurityService securityService;
    private SessionRegistry sessionRegistry;
    private Map<User, String> registerUserMap = Collections.synchronizedMap(new HashMap<User, String>());
    private static final Logger LOGGER = LoggerFactory.getLogger(LocationServiceImpl.class);

    /**
     * @param securityService security service
     * @param sessionRegistry session registry
     */
    public LocationServiceImpl(SecurityService securityService, SessionRegistry sessionRegistry) {
        this.securityService = securityService;
        this.sessionRegistry = sessionRegistry;
    }

    /**
     * Modification map to active user, and create list of user name users on the current page
     * {@inheritDoc}
     */
    @Override
    public synchronized List<String> getUsersViewing(Entity entity) {
        List<String> viewList = new ArrayList<String>();
        registerUserMap.put(securityService.getCurrentUser(), entity.getUuid());
        LOGGER.error(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        LOGGER.error("put in registredUserMap current user");
        if (securityService.getCurrentUser() != null) {
            LOGGER.error(securityService.getCurrentUser().getEncodedUsername());
            LOGGER.error(registerUserMap.get(securityService.getCurrentUser()), entity.getUuid());
        } else {
            LOGGER.error(registerUserMap.get(securityService.getCurrentUser()), entity.getUuid());
        }
        LOGGER.error(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");

        LOGGER.error(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        for (Object o : sessionRegistry.getAllPrincipals()) {
            User user = (User) o;

            LOGGER.error(user.getEncodedUsername());
            LOGGER.error("perebor AllPrincipals");
            LOGGER.error(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
            if (registerUserMap.containsKey(user) && registerUserMap.get(user).equals(entity.getUuid())) {
                viewList.add(user.getEncodedUsername());
                LOGGER.error("add in view list");
                LOGGER.error(user.getEncodedUsername());
            }
        }
        LOGGER.error(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        LOGGER.error("Full view list");
        for (String str : viewList) {
            LOGGER.error(str);
        }
        LOGGER.error(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        return viewList;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clearUserLocation() {

        LOGGER.error(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        if (securityService.getCurrentUser() != null) {
            LOGGER.error(securityService.getCurrentUser().getEncodedUsername());
        } else {
            LOGGER.error("anonymous");
        }
        LOGGER.error("delete this user");
        LOGGER.error(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        registerUserMap.remove(securityService.getCurrentUser());
    }
}
