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

import org.apache.commons.lang.StringUtils;
import org.jtalks.common.model.entity.Entity;

/**
 * Stores information about the contacts of user. Each contact can have its type {@link #getType()} which is either one
 * of predefined types like Email, Skype and has its own validation rule to check its value when user edits its
 * contacts.
 * Used in {@code UserDetails} rather than {@link org.jtalks.common.model.entity.User} itself.
 */
public class UserContact extends Entity {
    private String value;
    private JCUser owner;
    private UserContactType type;

    /**
     * Only for hibernate usage.
     */
    protected UserContact() {
    }

    /**
     * @param value actual value, like cell number, mail address or ICQ UIN
     * @param type contact type, like "Skype", "Jabber" or "Pigeon Mail"
     */
    public UserContact(String value, UserContactType type) {
        this.value = value;
        this.type = type;
    }

    /**
     * @return type contact
     */
    public UserContactType getType() {
        return type;
    }

    /**
     * @param type type contact
     */
    public void setType(UserContactType type) {
        this.type = type;
    }

    /**
     * @return user who owns the contact
     */
    public JCUser getOwner() {
        return owner;
    }

    /**
     * @param owner user who owns the contact
     */
    public void setOwner(JCUser owner) {
        this.owner = owner;
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
    
    /**
     * Replaced stubs in display pattern for this contact type by actual
     * contact value
     * @return actual ready-to-display contact
     */
    public String getDisplayValue() {
        String replacement = StringUtils.defaultIfBlank(value, "");
        return type.getDisplayValue(replacement);
    }
}
