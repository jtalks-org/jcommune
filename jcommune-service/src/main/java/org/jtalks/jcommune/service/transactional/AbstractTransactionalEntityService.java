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
package org.jtalks.jcommune.service.transactional;

import org.jtalks.common.model.dao.Crud;
import org.jtalks.common.model.entity.Entity;
import org.jtalks.jcommune.service.EntityService;
import org.jtalks.jcommune.plugin.api.exceptions.NotFoundException;

import java.lang.reflect.ParameterizedType;

/**
 * Generic implementation of all entity based services.
 * Most of the implementations of the methods are basing on straightforward calls
 * of the same named method from DAO interface.
 *
 * @author Osadchuck Eugeny
 * @author Kirill Afonin
 */
public abstract class AbstractTransactionalEntityService<T extends Entity, Y extends Crud<T>>
        implements EntityService<T> {
    /**
     * ChildRepository object implementation.
     */
    private Y dao;

    /**
     * Subclass may use this constructor to store entity DAO or parent
     * entity DAO if necessary
     *
     * @param dao subclass-provided dao object
     */
    AbstractTransactionalEntityService(Y dao) {
        this.dao = dao;
    }

    /**
     * Returns the dao set in constructor
     *
     * @return dao set in the constructor
     */
    Y getDao() {
        return dao;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T get(Long id) throws NotFoundException {
        if (!dao.isExist(id)) {
            throw new NotFoundException(String.format("Entity [%s] with id: %d not found",
                    getEntityClass().getSimpleName(), id));
        }
        return dao.get(id);
    }

    /**
     * Returns entity class with which service implementation works
     * @return entity class for service implementation
     */
    private Class<?> getEntityClass() {
        ParameterizedType parameterizedType = (ParameterizedType) getClass().getGenericSuperclass();
        return (Class<?>) parameterizedType.getActualTypeArguments()[0];
    }
}
