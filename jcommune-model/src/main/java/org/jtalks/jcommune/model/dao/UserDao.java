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

import org.hibernate.ObjectNotFoundException;
import org.jtalks.common.model.entity.User;
import org.jtalks.jcommune.model.entity.JCUser;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * This interface provides persistence operations for {@link org.jtalks.jcommune.model.entity.JCUser} objects.
 *
 * @author Pavel Vervenko
 * @author Kirill Afonin
 * @author Evgeniy Naumenko
 * @see org.jtalks.jcommune.model.dao.hibernate.UserHibernateDao
 */
public interface UserDao extends org.jtalks.common.model.dao.UserDao<JCUser> {

    /**
     * Get {@link JCUser} with corresponding username ignoring case. If there are several identical usernames that
     * differ only by letter case, we check exact match among them. This is done due to historical reasons - in the
     * early releases users with the same username were allowed if they had letters in different cases,
     * but afterwards it was decided that if user `Vasia` is registered, then another new user shouldn't be allowed
     * to register with `vAsia` username. This decision was made too late and by that time we had such users on the
     * production already, thus this check for exact match was introduced.
     *
     * @return {@link JCUser} with given username or null if not found
     * @see <a href="http://jira.jtalks.org/browse/JC-1163">JC-1163</a>
     */
    JCUser getByUsername(String username);

    /**
     * Get {@link JCUser} with e-mail given.
     *
     * @param email e-mail address set in user profile.
     * @return {@link JCUser} with given email or null if not found
     * @see JCUser
     */
    JCUser getByEmail(String email);

    /**
     * Get {@link JCUser} with UUID given.
     *
     * @param uuid unique entity identifier
     * @return {@link JCUser} with given UUID or null if not found
     * @see JCUser
     */
    JCUser getByUuid(String uuid);

    /**
     * Returns all users, whose accounts are not enables. At the moment registration creates disabled accounts and user
     * should activate them manually following the link in an e-mail.
     *
     * @return list of non-activated user accounts
     */
    Collection<JCUser> getNonActivatedUsers();

    /**
     * Gets a common (not JCommune one) user from the database. This is done because there might be some users not
     * registered within JCommune, but via some other component and we might need accessing them.
     *
     * @param username a full username to search the common user for, note that depending on the column/table collation
     *                 specified during table creation the search might be either case sensitive or not.
     * @return a user with the specified username, or {@code null} if such user wasn't found
     */
    User getCommonUserByUsername(String username);

    /**
     * Get users by their user's names.
     * 
     * @param usernames the collections of user's names
     * @return the list of found users
     */
    List<JCUser> getByUsernames(Set<String> usernames);

    /**
     * Get user names by part of user name.
     * Also does some formatting to pattern.
     *
     * @param pattern part of user name
     * @param count max count of user names
     * @return the list of found user names
     */
    List<String> getUsernames(String pattern, int count);

    /**
     * Gets list of users by part of username or email
     *
     * @param pattern part of username (case insensitive)
     * @param count max count of users in result
     * @return list of found users
     */
    List<JCUser> findByUsernameOrEmail(String pattern, int count);

    /**
     * May return a proxy (without hitting DB). If no row found the object is returned but it
     * throws {@link ObjectNotFoundException} when properties are first accessed.
     * @param id stored user identifier.
     * @return proxy of JCUser object.
     */
    JCUser loadById(Long id);
}
