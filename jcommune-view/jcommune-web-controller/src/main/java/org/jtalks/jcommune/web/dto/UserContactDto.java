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


import org.apache.commons.lang.StringUtils;
import org.jtalks.jcommune.model.entity.UserContact;
import org.jtalks.jcommune.model.entity.UserContactType;
import org.jtalks.jcommune.web.validation.annotations.ValidUserContact;

/**
 * Dto for transferring user contacts to client side.
 *
 * @author Michael Gamov
 */

@ValidUserContact(field="value", storedTypeId="type.id")
public class UserContactDto implements Comparable<UserContactDto> {

    private Long id;
    
    private String value;

    private String displayValue;

    private UserContactType type;

    /**
     * Create dto from {@link UserContact)
     *
     * @param contact user contact for conversion
     */
    public UserContactDto(UserContact contact) {
        id = contact.getId();
        value = contact.getValue();
        type = contact.getType();
    }

    /**
     * Default constructor. Creates objects with all fields set to null
     */
    public UserContactDto() {
    }

    /**
     * @return id
     */
    public Long getId() {
        return id;
    }

    /**
     * Set contact id
     *
     * @param id of contact
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return contact value
     */
    public String getValue() {
        return value;
    }

    /**
     * Set contact value
     *
     * @param value of contact
     */
    public void setValue(String value) {
        this.value = value;
    }

    public UserContactType getType() {
        return type;
    }

    public void setType(UserContactType type) {
        this.type = type;
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

    @Override
    public int compareTo(UserContactDto o) {
        return o == null ? -1 : Long.compare(this.type.getId(), o.getType().getId());
    }
}
