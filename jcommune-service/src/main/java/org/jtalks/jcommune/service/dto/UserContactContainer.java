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
package org.jtalks.jcommune.service.dto;

/**
 * This class is used when transferring user contacts updates
 * from web tier to the service layer. For various reasons
 * we can't use domain model class and MVC command object.
 *
 * @author Andrey Pogorelov
 */
public class UserContactContainer {

    private Long id;

    private String value;

    private long typeId;

    public UserContactContainer(Long id, String value, long typeId) {
        this.id = id;
        this.value = value;
        this.typeId = typeId;
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
