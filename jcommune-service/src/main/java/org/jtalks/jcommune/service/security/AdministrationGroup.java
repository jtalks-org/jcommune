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


public enum AdministrationGroup {
    ADMIN("ROLE_ADMIN", 13L),
    USER("ROLE_USER", 11L),
    BANNED_USER("ROLE_BANNED_USER", 12L),
    ANONYMOUS("ROLE_ANONYMOUS", 0L);

    private String name;
    private Long id;

    private AdministrationGroup(String name, Long id) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public Long getId() {
        return id;
    }

    public static AdministrationGroup getAdministrationGroupByName(String name) {
        for (AdministrationGroup administrationGroup : values()) {
            if (administrationGroup.getName().equals(name)) {
                return administrationGroup;
            }
        }
        throw new IllegalArgumentException("AdministrationGroup with name=" + name + " is not exist.");
    }
}
