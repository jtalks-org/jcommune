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
package org.jtalks.jcommune.model.dao;

import org.jtalks.jcommune.model.entity.User;

/**
 * This interface provides persistence operations for
 * {@link User} objects. Now it has no specific methods, it has only methods
 * inherited from {@link ChildRepository} interface.
 *
 * @author Pavel Vervenko
 * @author Kirill Afonin
 * @author Evgeniy Naumenko
 * @see org.jtalks.jcommune.model.dao.hibernate.UserHibernateDao
 */
public interface UserDao extends ParentRepository<User> {

    /**
     * Get {@link User} with corresponding username.
     *
     * @param username name of requested user.
     * @return {@link User} with given username.
     * @see User
     */
    User getByUsername(String username);

    /**
     * Get {@link User} with corresponding encodedUsername.
     *
     * @param encodedUsername encoded name of requested user.
     * @return {@link User} with given encodedUsername.
     * @see User
     */
    User getByEncodedUsername(String encodedUsername);

    /**
     * Get {@link User} with e-mail given.
     *
     * @param email e-mail address set in user profile.
     * @return {@link User} with given encodedUsername.
     * @see User
     */
    User getByEmail(String email);

    /**
     * Check if {@link User} with given username exist.
     *
     * @param username username
     * @return <code>true</code> if {@link User} with given username exist or
     *         <code>false</code>
     */
    boolean isUserWithUsernameExist(String username);

    /**
     * Check if {@link User} with given email exist.
     *
     * @param email email
     * @return <code>true</code> if {@link User} with given email exist or
     *         <code>false</code>
     */
    boolean isUserWithEmailExist(String email);
}
