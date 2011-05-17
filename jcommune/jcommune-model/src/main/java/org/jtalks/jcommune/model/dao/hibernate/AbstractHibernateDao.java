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
package org.jtalks.jcommune.model.dao.hibernate;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.classic.Session;
import org.jtalks.jcommune.model.dao.Dao;
import org.jtalks.jcommune.model.entity.Persistent;

import java.lang.reflect.ParameterizedType;
import java.util.List;

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
    private Class<T> type;

    /**
     * Default constructor.
     * Retrieves parameterized type of entity using reflection.
     */
    protected AbstractHibernateDao() {
        this.type = (Class<T>) ((ParameterizedType) getClass()
                .getGenericSuperclass()).getActualTypeArguments()[0];
    }

    /**
     * Get the current Hibernate session.
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
    public void delete(T persistent) {
        getSession().delete(persistent);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveOrUpdate(T entity) {
        getSession().save(entity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(Long id) {
        Query query = getSession()
                .createQuery("delete " + type.getSimpleName() + " where id= :id");
        query.setLong("id", id);
        query.executeUpdate();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T get(Long id) {
        return (T) getSession().load(type, id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<T> getAll() {
        return getSession()
                .createQuery("from " + type.getSimpleName()).list();
    }

}
