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


import org.jtalks.jcommune.model.entity.UserContact;
import org.jtalks.jcommune.web.validation.annotations.ValidUserContact;

/**
 * Dto for transferring user contacts to client side.
 *
 * @author Michael Gamov
 */

@ValidUserContact(field="value", storedTypeId="typeId")
public class UserContactDto {

    private Long id;
    
    private Long ownerId;
    
    private String value;
    
    private long typeId;
    
    /**
     * Create dto from {@link UserContact)
     *
     * @param contact user contact for conversion
     */
    public UserContactDto(UserContact contact) {
        id = contact.getId();
        ownerId = contact.getOwner().getId();
        value = contact.getValue();
        typeId = contact.getType().getId();
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
     * @return owner id
     */
    public Long getOwnerId() {
        return ownerId;
    }

    /**
     * Set owner id of contact
     *
     * @param ownerId owner id
     */
    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
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

    /**
     * @return id of user contact type
     */
    public long getTypeId() {
        return typeId;
    }

    /**
     * Set id of user contact type
     *
     * @param typeId of user contact type
     */
    public void setTypeId(long typeId) {
        this.typeId = typeId;
    }

}
