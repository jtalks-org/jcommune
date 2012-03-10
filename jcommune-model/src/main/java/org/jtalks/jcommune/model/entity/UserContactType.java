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
package org.jtalks.jcommune.model.entity;

import org.jtalks.common.model.entity.Entity;

/**
 * Stores information about the type contacts of user.
 * Used as {@code UserDetails}
 */
public class UserContactType extends Entity {

    private String typeName;
    private String icon;

    /**
     * Only for hibernate usage.
     */
    public UserContactType() {
    }

    /**
     *
     * @return address of icon
     */
    public String getIcon() {
        return icon;
    }

    /**
     *
     * @param icon address of icon
     */
    public void setIcon(String icon) {
        this.icon = icon;
    }

    /**
     *
     * @return name type of contact
     */
    public String getTypeName() {
        return typeName;
    }

    /**
     *
     * @param typeName name type of contact
     */
    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }


}
