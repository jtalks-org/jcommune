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
import org.jtalks.jcommune.model.dao.ForumStatisticsDAO;

/**
 * The implementation of ForumStatisticsDAO based on Hibernate.
 * The class is responsible for getting forum statistic information from database.
 *
 * @author Elena Lepaeva
 */
public class ForumStatisticsHibernateDAO implements ForumStatisticsDAO {

    /**
     * Hibernate SessionFactory
     */
    private SessionFactory sessionFactory;

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
    public int getPostsOnForumCount() {
        return ((Number) getSession().createQuery("select count(*) from Post p")
                .setCacheable(true).uniqueResult()).intValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getUsersCount() {
        return ((Number) getSession().getNamedQuery("getCountOfUsers")
                .setCacheable(true)
                .uniqueResult())
                .intValue();
    }
}
