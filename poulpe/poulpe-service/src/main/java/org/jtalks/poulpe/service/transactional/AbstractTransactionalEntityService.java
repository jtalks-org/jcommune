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
package org.jtalks.poulpe.service.transactional;

import org.jtalks.poulpe.model.dao.Dao;
import org.jtalks.poulpe.model.entity.Persistent;
import org.jtalks.poulpe.service.EntityService;
import org.jtalks.poulpe.service.exceptions.NotFoundException;

/**
 * Generic implementation of all entity based services.
 * Most of the implementations of the methods are basing on straightforward calls
 * of the same named method from DAO interface.
 *
 * @author Osadchuck Eugeny
 * @author Kirill Afonin
 */
public abstract class AbstractTransactionalEntityService<T extends Persistent, Y extends Dao<T>>
        implements EntityService<T> {
    /**
     * Dao object implementation.
     */
    protected Y dao;

    /**
     * {@inheritDoc}
     */
    @Override
    public T get(Long id) throws NotFoundException {
        if (!dao.isExist(id)) {
            throw new NotFoundException("Entity with id: " + id + " not found");
        }
        return (T) dao.get(id);
    }
    
    /**
     * Set the DAO.
     * 
     * @param dao Data Access Object
     */
    public void setDao(Y dao) {
		this.dao = dao;
	}
    
}
