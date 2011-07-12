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
package org.jtalks.common.model.dao.hibernate;

import org.jtalks.common.model.dao.UserDao;
import org.jtalks.common.model.entity.User;

/**
 * Hibernate implementation of UserDao.
 *
 * @author Pavel Vervenko
 * @author Kirill Afonin
 */
public class UserHibernateDao extends AbstractHibernateDao<User> implements UserDao {
    /**
     * {@inheritDoc}
     */
    @Override
    public User getByUsername(String username) {
        return (User) getSession()
                .createQuery("from User u where u.username = ?")
                .setString(0, username)
                .uniqueResult();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isUserWithUsernameExist(String username) {
        return ((Number) getSession()
                .createQuery("select count(*) from User u where u.username = ?")
                .setString(0, username)
                .uniqueResult()).intValue() != 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isUserWithEmailExist(String email) {
        return ((Number) getSession()
                .createQuery("select count(*) from User u where u.email = ?")
                .setString(0, email)
                .uniqueResult()).intValue() != 0;
    }
}
