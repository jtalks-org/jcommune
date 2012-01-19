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
 * Stores information about the contacts of user.
 * Used as {@code UserDetails}
 */
public class UserContact extends Entity {

    private String value;
    private JCUser contactOfUser;
    private String type;
    private String icon;

    /**
     * Only for hibernate usage.
     */
    public UserContact() {
    }

    /**
     * @return path icon this contact
     */
    public String getIcon() {
        return icon;
    }

    /**
     * @param icon path icon this contact
     */
    public void setIcon(String icon) {
        this.icon = icon;
    }

    /**
     * @return type contact
     */
    public String getType() {
        return type;
    }

    /**
     * @param type type contact
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return user who owns the contact
     */
    public JCUser getContactOfUser() {
        return contactOfUser;
    }

    /**
     *
     * @param contactOfUser  user who owns the contact
     */
    public void setContactOfUser(JCUser contactOfUser) {
        this.contactOfUser = contactOfUser;
    }

    /**
     * @return address or number
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value address or number
     */
    public void setValue(String value) {
        this.value = value;
    }
}
