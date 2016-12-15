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
package org.jtalks.jcommune.service.security;

import java.util.ArrayList;
import java.util.List;

/**
 * Elements contains IDs predefined user groups.
 * More information see in V21__Add_predefined_groups.sql migration.
 *
 * @author Elena Lepaeva
 */
public enum AdministrationGroup {
    /**
     * Administrators
     */
    ADMIN("Administrators"),
    /**
     * Registered users
     */
    USER("Registered Users"),
    /**
     * Banned users
     */
    BANNED_USER("Banned Users");

    private String name;

    public static final List<String> PREDEFINED_GROUP_NAMES = new ArrayList<>();

    static {
        for (AdministrationGroup group : values()) {
            PREDEFINED_GROUP_NAMES.add(group.getName());
        }
    }

    /**
     * @param name group database name, hardcoded in initial SQL migrations
     */
    private AdministrationGroup(String name) {
        this.name = name;
    }

    /**
     * @return group database name, hardcoded in initial SQL migrations
     */
    public String getName() {
        return name;
    }

    /**
     * @return true if provided name is the one from pre-defined groups
     */
    public static boolean isPredefinedGroup(String groupName) {
        return PREDEFINED_GROUP_NAMES.contains(groupName);
    }
}
