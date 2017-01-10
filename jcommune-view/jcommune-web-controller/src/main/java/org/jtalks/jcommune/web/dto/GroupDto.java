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
package org.jtalks.jcommune.web.dto;

import org.jtalks.common.model.entity.Group;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.jtalks.jcommune.model.dto.UserDto;

/**
 * @author Andrei Alikov
 * DTO class for user group containing only ID and name of the group
 *
 */
public class GroupDto {
    private long id;
    private String name;
    private List<UserDto> users;

    public GroupDto(Group group) {
        this.id = group.getId();
        this.name = group.getName();
    }

    public GroupDto(long id, String name, List<UserDto> users) {
        this.id = id;
        this.name = name;
        this.users = users;
    }

    public GroupDto() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<UserDto> getUsers() {
        return users;
    }

    public void setUsers(List<UserDto> users) {
        this.users = users;
    }

    public static List<GroupDto> convertToGroupDtoList(List<Group> groups, Comparator<GroupDto> comparator) {
        List<GroupDto> groupDtoList = new ArrayList<>();
        if (groups == null) {
            return groupDtoList;
        }
        for (Group group: groups) {
            groupDtoList.add(new GroupDto(group));
        }
        if (comparator != null) {
            Collections.sort(groupDtoList, comparator);
        }
        return groupDtoList;
    }

    public static Comparator<GroupDto> BY_NAME_COMPARATOR = new Comparator<GroupDto>() {
        @Override
        public int compare(GroupDto o1, GroupDto o2) {
            return o1.getName().compareTo(o2.getName());
        }
    };
}
