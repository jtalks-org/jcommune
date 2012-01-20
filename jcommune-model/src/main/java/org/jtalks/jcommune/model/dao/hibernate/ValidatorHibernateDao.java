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
import org.jtalks.common.model.entity.Entity;
import org.jtalks.jcommune.model.dao.ValidatorDao;

/**
 * Performs simple checks in a database for the sake of validation.
 * This implementation supports only string params for simplicity
 *
 * @author Evgeniy Naumenko
 */
public class ValidatorHibernateDao implements ValidatorDao<String> {

    private static final String QUERY_TEMPLATE = "from %s p where p.%s = ?";

    private SessionFactory sessionFactory;

    /**
     * @param sessionFactory to obtain current hibernate session
     */
    public ValidatorHibernateDao(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isResultSetEmpty(Class<? extends Entity> entity, String field, String param) {
        String hql = String.format(QUERY_TEMPLATE, entity.getSimpleName(), field);
        return sessionFactory
                .getCurrentSession()
                .createQuery(hql)
                .setString(0, param)
                .setCacheable(true)
                .list()
                .isEmpty();
    }
}
