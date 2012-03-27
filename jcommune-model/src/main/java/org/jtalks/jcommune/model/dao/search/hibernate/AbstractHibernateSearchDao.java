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
package org.jtalks.jcommune.model.dao.search.hibernate;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.jtalks.jcommune.model.dao.search.SearchDao;
import org.jtalks.jcommune.model.entity.IndexedEntity;

/**
 * The base class for full-text search.
 * The implementation is based on the Hibernate Search.
 * 
 * @author Anuar Nurmakanov
 * 
 * @param <E> indexed entity
 */
public abstract class AbstractHibernateSearchDao<E extends IndexedEntity>
        implements SearchDao<E> {
    /**
     * Hibernate SessionFactory
     */
    private SessionFactory sessionFactory;
    
    /**
     * Setter for Hibernate SessionFactory.
     *
     * @param sessionFactory the sessionFactory to set
     */
    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
    
    /**
     * Get Hibernate Search session.
     * 
     * @return full text session
     */
    protected FullTextSession getFullTextSession() {
        Session session = sessionFactory.getCurrentSession();
        return Search.getFullTextSession(session);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void rebuildIndex(Class<E> entityClass) {
        getFullTextSession().createIndexer(entityClass).start();
    }
}
