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
package org.jtalks.poulpe.model.dao.hibernate;

import org.hibernate.SessionFactory;
import org.hibernate.classic.Session;
import org.jtalks.poulpe.model.dao.Dao;
import org.jtalks.poulpe.model.entity.Persistent;

import java.lang.reflect.ParameterizedType;

/**
 * Basic class for access to the {@link Persistent} objects.
 * Uses to load objects from database, save, update or delete them.
 * The implementation is based on the Hibernate.
 * Has the implementation of some commonly used methods.
 *
 * @author Pavel Vervenko
 * @author Kirill Afonin
 */
public abstract class AbstractHibernateDao<T extends Persistent> implements Dao<T> {

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
    private Class<T> getType() {
        return (Class<T>) ((ParameterizedType) getClass()
                .getGenericSuperclass()).getActualTypeArguments()[0];
    }

    private final String deleteQuery = "delete " + type.getSimpleName() + " e where e.id= :id";

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
    public void saveOrUpdate(T entity) {
        Session session = getSession();
        session.saveOrUpdate(entity);
        session.flush();   //TODO: WOW, this shouldn't be here, it's related only to tests,
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean delete(Long id) {
        return getSession().createQuery(deleteQuery)
                .setLong("id", id)
                .executeUpdate() != 0;
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
