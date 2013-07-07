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
import org.jtalks.common.model.dao.hibernate.GenericDao;
import org.jtalks.common.model.entity.Component;
import org.jtalks.jcommune.model.dao.ComponentDao;

/**
 * The implementation of {@link org.jtalks.jcommune.model.dao.ComponentDao} based on Hibernate.
 * The class is responsible for loading {@link org.jtalks.common.model.entity.Component} objects from database,
 * but another CRUD operations aren't available, because only and only administrative panel may perform
 * creating, updating, deleting {@link org.jtalks.common.model.entity.Component} in database.
 *
 * @author masyan
 */
public class ComponentHibernateDao extends GenericDao<Component> implements ComponentDao {

    /**
     * @param sessionFactory The SessionFactory.
     */
    public ComponentHibernateDao(SessionFactory sessionFactory) {
        super(sessionFactory, Component.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Component getComponent() {
        return (Component) session().getNamedQuery("getForumComponent").uniqueResult();
    }
}
