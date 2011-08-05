/**
 * Copyright (C) 2011  jtalks.org Team
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
 * Also add information on how to contact you by electronic and paper mail.
 * Creation date: Apr 12, 2011 / 8:05:19 PM
 * The jtalks.org Project
 */
package org.jtalks.poulpe.model.entity;

/**
 * Basic class for persistent objects.
 * 
 * @author Pavel Vervenko
 */
public abstract class Persistent {

    private long id;

    private String uuid = java.util.UUID.randomUUID().toString();
    
    /**
     * Get the primary id of the persistent object.
     * @return the id
     */
    public long getId() {
        return id;
    }

    /**
     * Set the id for the persistent object.
     * @param id id to set
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Get the unique id. 
     * @return the uuid
     */
    public String getUuid() {
        return this.uuid;
    }

    /**
     * Set the unique id for the persistent object.
     * @param uuid uuid to set
     */
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }    
    
    /**
     * {@inheritDoc }
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Persistent other = (Persistent) obj;
        return uuid.equals(other.uuid);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int hashCode() {
        return uuid.hashCode();
    }
}
