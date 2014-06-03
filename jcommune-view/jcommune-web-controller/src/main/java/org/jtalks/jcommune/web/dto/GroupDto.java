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

/**
 * @author Andrei Alikov
 * DTO class for user group containing only ID and name of the group
 *
 */
public class GroupDto {
    private long id;
    private String name;

    /**
     * Instantiates new GroupDto object based on the Group object
     * @param group source data for the DTO object
     */
    public GroupDto(Group group) {
        this.id = group.getId();
        this.name = group.getName();
    }

    /**
     * Instantiates new GroupDto object
     * @param id id of the group
     * @param name name of the group
     */
    public GroupDto(long id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * Default constructor. Needed for initialization from request body.
     */
    public GroupDto() {
    }

    /**
     * Gets id of the group
     * @return id of the group
     */
    public long getId() {
        return id;
    }

    /**
     * Sets id of the group
     * @param id id of the group
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Gets name of the group
     * @return name of the group
     */
    public String getName() {
        return name;
    }

    /**
     * Sets name of the group
     * @param name name of the group
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Converts list of the Group objects to the list of the GroupDto objects
     * @param groups source information about the Groups
     * @param sortByName if true than result list will be sorted by the group names
     * @return result list with GroupDto based on the source list of the Group objects
     */
    public static List<GroupDto> convertGroupList(List<Group> groups, boolean sortByName) {
        List<GroupDto> groupDtoList = new ArrayList<GroupDto>();
        for (Group group: groups) {
            groupDtoList.add(new GroupDto(group));
        }

        if (sortByName) {
            Collections.sort(groupDtoList, BY_NAME_COMPARATOR);
        }

        return groupDtoList;
    }


    /**
     * Comparator comparing two objects by their names
     */
    public static Comparator<GroupDto> BY_NAME_COMPARATOR = new Comparator<GroupDto>() {
        @Override
        public int compare(GroupDto o1, GroupDto o2) {
            return o1.getName().compareTo(o2.getName());
        }
    };
}
