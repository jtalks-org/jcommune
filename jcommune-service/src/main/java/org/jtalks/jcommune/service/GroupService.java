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
package org.jtalks.jcommune.service;

import org.jtalks.common.model.entity.Group;
import org.jtalks.common.service.EntityService;
import org.jtalks.common.service.exceptions.NotFoundException;
import org.jtalks.jcommune.model.dto.GroupAdministrationDto;
import org.jtalks.jcommune.model.dto.PageRequest;
import org.jtalks.jcommune.model.dto.SecurityGroupList;
import org.jtalks.jcommune.model.dto.UserDto;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Service for dealing with {@link Group} objects
 *
 * @author unascribed
 */
public interface GroupService extends EntityService<Group> {

    /**
     * @return list of all {@link Group} objects
     */
    List<Group> getAll();

    /**
     * @return {@link SecurityGroupList} with all {@link Group} objects
     */
    SecurityGroupList getSecurityGroups();

    /**
     * @param name to look up
     * @return list of groups which names contains given name
     */
    List<Group> getByNameContains(String name);

    /**
     * Returns list of group which name is equal ignoring case with given name
     * @param name to look up
     * @return list of groups which names exactly match the given name
     */
    List<Group> getByName(String name);

    Page<UserDto> getPagedGroupUsers(long id, PageRequest pageRequest);

    /**
     * Delete group
     *
     * @param group to be delete
     * @throws IllegalArgumentException if group is null
     * @throws NotFoundException if current user have no sid(not activated)
     */
    void deleteGroup(Group group) throws NotFoundException;

    /**
     * Save or update group.
     *
     * @param selectedGroup instance to save
     * @throws IllegalArgumentException if group is null
     */
    void saveGroup(Group selectedGroup);

    /**
     * Save new group if id is null.
     * Update group if id is not null.
     *
     * @param dto
     * @throws NotFoundException
     * @throws org.jtalks.common.validation.ValidationException in case of duplicate group name
     */
    void saveOrUpdate(GroupAdministrationDto dto) throws NotFoundException;

    /**
     * @return list of GroupAdministrationDto
     */
    List<GroupAdministrationDto> getGroupNamesWithCountOfUsers();
}