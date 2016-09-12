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
package org.jtalks.jcommune.model.dto;

import org.hibernate.validator.constraints.Length;
import org.jtalks.common.model.entity.Group;
import org.jtalks.jcommune.model.validation.annotations.Unique;

/**
 * DTO class for user group containing name and number of users in the group
 *
 * @author Oleg Tkachenko
 */

public class GroupAdministrationDto {
    public static final int GROUP_NAME_MIN_LENGTH = 1;

    @Length(min = GROUP_NAME_MIN_LENGTH, max = Group.GROUP_NAME_MAX_LENGTH, message = "{group.name.illegal_length}")
    @Unique(entity = Group.class, field = "name", message = "{group.already_exists}", ignoreCase = true)
    private String name;

    @Length(max = Group.GROUP_DESCRIPTION_MAX_LENGTH, message = "{group.description.illegal_length}")
    private String description;

    private long numberOfUsers;

    public GroupAdministrationDto(){}

    public GroupAdministrationDto(String name, int count) {
        setName(name);
        setNumberOfUsers(count);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name.trim();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getNumberOfUsers() {
        return numberOfUsers;
    }

    public void setNumberOfUsers(long numberOfUsers) {
        this.numberOfUsers = numberOfUsers;
    }

}
