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
package org.jtalks.jcommune.model.dao;

import org.jtalks.common.model.entity.Group;
import org.jtalks.jcommune.model.dto.GroupAdministrationDto;
import org.jtalks.jcommune.model.dto.UserDto;

import java.util.List;

/**
 * Data access object for manipulating groups
 *
 * @author Mikhail Stryzhonok
 */
public interface GroupDao extends org.jtalks.common.model.dao.GroupDao {

    /**
     * Get groups by identificators
     * @param ids the collection of identificators
     * @return the list of found groups
     */
    List<Group> getGroupsByIds(List<Long> ids);

    /**
     * Get the list of all groups.
     *
     * @return list of groups
     */
    List<Group> getAll();

    /**
     * Get the list of all groups which names contains the specified name.
     *
     * @param name group name
     * @return list of groups
     * @throws IllegalArgumentException if name is null
     */
    List<Group> getByNameContains(String name);

    /**
     * Get the list of all groups which name matches ignoring case the specified name.
     *
     * @param name group name
     * @return list of groups
     * @throws IllegalArgumentException if name is null
     */
    List<Group> getByName(String name);

    /**
     * @return list of GroupAdministrationDto
     */
    List<GroupAdministrationDto> getGroupNamesWithCountOfUsers();

    List<UserDto> getGroupUsers(long id, int count);
}
