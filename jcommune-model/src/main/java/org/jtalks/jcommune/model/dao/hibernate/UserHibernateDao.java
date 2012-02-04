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

import org.jtalks.common.model.dao.hibernate.AbstractHibernateParentRepository;
import org.jtalks.jcommune.model.dao.UserDao;
import org.jtalks.jcommune.model.entity.JCUser;

import java.util.Collection;

/**
 * Hibernate implementation of UserDao.
 * Mainly intended for queering users from DB based on different criteria.
 *
 * @author Pavel Vervenko
 * @author Evgeniy Naumenko
 * @author Kirill Afonin
 */
public class UserHibernateDao extends AbstractHibernateParentRepository<JCUser> implements UserDao {

    /**
     * {@inheritDoc}
     */
    @Override
    public JCUser getByUsername(String username) {
        JCUser user = (JCUser) getSession()
                .createQuery("from JCUser u where u.username = ?")
                .setCacheable(true).setString(0, username)
                .uniqueResult();
        return user;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JCUser getByEmail(String email) {
        return (JCUser) getSession().createQuery("from JCUser u where u.email = ?")
                .setCacheable(true)
                .setString(0, email)
                .uniqueResult();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<JCUser> getNonActivatedUsers() {
        return getSession().createQuery("from JCUser u where u.enabled = false")
                .setCacheable(false)
                .list();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JCUser getByUuid(String uuid) {
        return (JCUser) getSession().createQuery("from JCUser u where u.uuid = ?")
                .setCacheable(true)
                .setString(0, uuid)
                .uniqueResult();
    }
}
