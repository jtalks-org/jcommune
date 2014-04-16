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

import java.util.Comparator;

/**
 * @author Andrei Alikov
 * DTO class for user group containing only ID and name of the group
 *
 */
public class GroupDto {
    private long id;
    private String name;

    /**
     * Instanciates new GroupDto object
     * @param id id of the group
     * @param name name of the group
     */
    public GroupDto(long id, String name) {
        this.id = id;
        this.name = name;
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
     * Comparator comparing two objects by their names
     */
    public static Comparator<GroupDto> BY_NAME_COMPARATOR = new Comparator<GroupDto>() {
        @Override
        public int compare(GroupDto o1, GroupDto o2) {
            return o1.getName().compareTo(o2.getName());
        }
    };
}
