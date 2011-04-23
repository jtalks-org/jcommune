/*
 * JTalks for uniting people
 * Copyright (C) 2011  JavaTalks Team
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 * Also add information on how to contact you by electronic and paper mail.
 *
 * This file creation date: Apr 12, 2011 / 8:05:19 PM
 * The JTalks Project
 * http://www.jtalks.org
 */
package org.jtalks.jcommune.model.dao;

import java.util.List;
import org.jtalks.jcommune.model.entity.Persistent;

/**
 * Basic Data Access Object interface.
 * @author Pavel Vervenko
 */
public interface Dao<T extends Persistent> {

    /**
     * Save or update the persistent object.
     * @param persistent object to save
     */
    void saveOrUpdate(T persistent);

    /**
     * Delete the object by it's id.
     * @param id the id
     */
    void delete(Long id);

    /**
     * Delete the object from data storage.
     * @param persistent 
     */
    void delete(T persistent);

    /**
     * Get the object by id.
     * @param id
     * @return 
     */
    T get(Long id);

    /**
     * Get the list of objects.
     * @return list of objects
     */
    List<T> getAll();
}
