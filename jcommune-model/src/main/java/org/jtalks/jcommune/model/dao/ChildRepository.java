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
 * @author Pavel Vervenko
 * @author Kirill Afonin
 */
public interface ChildRepository<T extends Entity> {

    /**
     * Update entity.
     * You should not try to save entity using this method.
     *
     * @param entity object to save
     */
    void update(T entity);

    /**
     * Get entity by id.
     *
     * @param id the id
     * @return loaded Persistence instance
     */
    T get(Long id);

    /**
     * Check entity existance by id.
     *
     * @param id entity id
     * @return {@code true} if entity exist
     */
    boolean isExist(Long id);
}
