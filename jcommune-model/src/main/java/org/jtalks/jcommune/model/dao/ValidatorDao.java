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
package org.jtalks.jcommune.model.dao;

import org.jtalks.common.model.entity.Entity;

/**
 * Simple DAO to check if certain objects are already present in a database.
 * To be used mostly for duplication/existence verifications.
 *
 * Type parameter stands for type of the query argument.
 *
 * @author Evgeniy Naumenko
 */
public interface ValidatorDao<T> {

    /**
     * Checks if the query passed returns nothing.
     * Supports one parameter for a certain type.
     *
     * @param entity entity we want to check
     * @param field  entity field we want to check
     * @param param parameter for the query
     * @param ignoreCase ignore case or not when checking
     * @return true if result set returned have at least one row
     */
    boolean isResultSetEmpty(Class<? extends Entity> entity, String field, 
            T param, boolean ignoreCase);

    /**
     * Checks if entity with specified value of field exists. If <code>
     * ignoreCase</code> is true and multiple entities with same 'ignore case' 
     * data exists, they will be checked in case sensitive mode.
     * 
     * @param entity entity we want to check
     * @param field entity field we want to check
     * @param value value of the field
     * @param ignoreCase ignore case or not when checking
     * @return true if there is at least one entity with specified value of field 
     */
    boolean isExists(Class<? extends Entity> entity, String field,
            T value, boolean ignoreCase);
}
