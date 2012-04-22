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

/**
 * The base class for full-text search.
 * Provides basic methods for working with the Hibernate Search.
 * 
 * @author Anuar Nurmakanov
 * 
 */
public abstract class AbstractHibernateSearchDao {
    /**
     * Hibernate SessionFactory
     */
    private SessionFactory sessionFactory;
 
    /**
     * @param sessionFactory the Hibernate SessionFactory
     */
    public AbstractHibernateSearchDao(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
    
    /**
     * Gets the Hibernate Search session, that provides functionality
     * for working with indexed entities.
     * 
     * @return the full text session
     * @see org.hibernate.search.FullTextSession
     */
    protected FullTextSession getFullTextSession() {
        Session session = sessionFactory.getCurrentSession();
        return Search.getFullTextSession(session);
    }
}
