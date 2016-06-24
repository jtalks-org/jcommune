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

/**
 * DTO class for user group containing name and number of users in the group
 *
 * @author Oleg Tkachenko
 */

public class GroupAdministrationDto {

    private String name;
    private long numberOfUsers;

    public GroupAdministrationDto(String name, int count) {
            this.name = name;
            this.numberOfUsers = count;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getNumberOfUsers() {
        return numberOfUsers;
    }

    public void setNumberOfUsers(long numberOfUsers) {
        this.numberOfUsers = numberOfUsers;
    }

}
