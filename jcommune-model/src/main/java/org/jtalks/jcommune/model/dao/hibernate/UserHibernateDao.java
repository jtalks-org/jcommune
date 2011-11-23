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

import org.jtalks.jcommune.model.dao.UserDao;
import org.jtalks.jcommune.model.entity.User;

/**
 * Hibernate implementation of UserDao.
 * Mainly intended for quering users from DB based on different criteria.
 *
 * @author Pavel Vervenko
 * @author Evgeniy Naumenko
 * @author Kirill Afonin
 */
public class UserHibernateDao extends ParentRepositoryImpl<User> implements UserDao {

    /**
     * {@inheritDoc}
     */
    @Override
    public User getByUsername(String username) {
        User user = (User) getSession()
                .createQuery("from User u where u.username = ?")
                .setCacheable(true).setString(0, username).uniqueResult();
        if (user != null) {
            user.setUserPostCount(getCountPostOfUser(user));
        }
        return user;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User getByEncodedUsername(String encodedUsername) {
        return (User) getSession()
                .createQuery("from User u where u.encodedUsername = ?")
                .setCacheable(true).setString(0, encodedUsername)
                .uniqueResult();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User getByEmail(String email) {
        return (User) getSession().createQuery("from User u where u.email = ?")
                .setCacheable(true).setString(0, email).uniqueResult();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isUserWithUsernameExist(String username) {
        return ((Number) getSession()
                .createQuery("select count(*) from User u where u.username = ?")
                .setCacheable(true).setString(0, username).uniqueResult())
                .intValue() != 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isUserWithEmailExist(String email) {
        return ((Number) getSession()
                .createQuery("select count(*) from User u where u.email = ?")
                .setCacheable(true).setString(0, email).uniqueResult())
                .intValue() != 0;
    }

    /**
     * Counts post for the user passed.
     *
     * We've tried to apply formula property instead of that, but
     * it is affected by l2cahce showing old results
     *
     * @param userCreated user created of post
     * @return count posts of user
     */
    private int getCountPostOfUser(User userCreated) {
        return ((Number) getSession().getNamedQuery("getCountPostOfUser")
                .setCacheable(true).setEntity("userCreated", userCreated)
                .uniqueResult()).intValue();
    }
}
