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
package org.jtalks.jcommune.plugin.api.service;

import org.jtalks.common.model.entity.Entity;
import org.jtalks.jcommune.model.entity.JCUser;

import java.util.List;

/**
 *
 * @author Mikhail Stryzhonok
 */
public interface PluginLocationService {

    /**
     * Returns list of user viewing a page with the entity specified.
     * This method will automatically add the current user to the viewing list
     * for entity passed
     *
     * @param entity to get users viewing a page with this entity
     * @return Users, who're viewing the page for entity passed. Will return empty list if
     *         there are no viewers or view tracking is not supported for this entity type
     */
    List<JCUser> getUsersViewing(Entity entity);
}
