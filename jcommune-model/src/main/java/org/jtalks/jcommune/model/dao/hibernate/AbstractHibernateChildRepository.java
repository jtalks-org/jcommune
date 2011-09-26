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
package org.jtalks.jcommune.model.dao.hibernate;

import org.hibernate.SessionFactory;
import org.hibernate.classic.Session;
import org.jtalks.common.model.entity.Entity;
import org.jtalks.jcommune.model.dao.ChildRepository;

import java.lang.reflect.ParameterizedType;

/**
 * Basic class for access to the {@link org.jtalks.jcommune.model.entity.Entity} objects.
 * Uses to load objects from database, save, update or delete them.
 * The implementation is based on the Hibernate.
 * Has the implementation of some commonly used methods.
 *
 * @author Pavel Vervenko
 * @author Kirill Afonin
 */
public abstract class AbstractHibernateChildRepository<T extends Entity> implements ChildRepository<T> {

    /**
     * Hibernate SessionFactory
     */
    private SessionFactory sessionFactory;

    /**
     * Type of entity
     */
    private final Class<T> type = getType();

    /**
     * Retrieves parametrized type of entity using reflection.
     *
     * @return type of entity
     */
    @SuppressWarnings("unchecked")
    protected Class<T> getType() {
        return (Class<T>) ((ParameterizedType) getClass()
                .getGenericSuperclass()).getActualTypeArguments()[0];
    }


    /**
     * Get current Hibernate session.
     *
     * @return current Session
     */
    protected Session getSession() {
        return sessionFactory.getCurrentSession();
    }

    /**
     * Setter for Hibernate SessionFactory.
     *
     * @param sessionFactory the sessionFactory to set
     */
    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update(T entity) {
        Session session = getSession();
        session.saveOrUpdate(entity);
        session.flush();   //TODO: WOW, this shouldn't be here, it's related only to tests,
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public T get(Long id) {
        return (T) getSession().get(type, id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isExist(Long id) {
        return get(id) != null;
    }
}
