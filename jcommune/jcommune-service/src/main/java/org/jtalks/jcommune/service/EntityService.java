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
package org.jtalks.jcommune.service;

import java.util.List;

import org.jtalks.jcommune.model.entity.Persistent;

/**
 * This is generic interface for services which would interact with database entities via DAO object.
 * This interface include all base method declaration which straightly based on database CRUD operations.         
 * 
 * @author Osadchuck Eugeny
 */
public interface EntityService<T extends Persistent> {
	
	/**
	 * Save new row in database table or update if row with current primary id has already existed in database.
	 * @param persistent - persistent object T which would be saved or updated in database 
	 */
    void saveOrUpdate(T persistent);

    /**
     * Delete row in database table corresponded to current primary id.
     * @param id - primary id of database table row to delete
     */
    void delete(Long id);

    /**
     * Delete row in database table corresponded to current persistent object primary id.
     * @param persistent - persistent object T to delete.
     */
    void delete(T persistent);

    /**
     * Get persistent object by id. Method is trying to find persistent object with current primary id and return it. 
     * @param id - primary id of persistent object to find.
     * @return - persistent object T or null if row with primary id = id is absent.
     */
    T get(Long id);

    /**
     * Get list of all persistence objects T currently present in database.
     * @return - list of persistence objects T.
     */
    List<T> getAll();
}
