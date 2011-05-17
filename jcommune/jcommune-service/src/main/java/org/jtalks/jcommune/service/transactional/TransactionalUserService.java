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
package org.jtalks.jcommune.service.transactional;

import org.jtalks.jcommune.model.dao.UserDao;
import org.jtalks.jcommune.model.entity.User;
import org.jtalks.jcommune.service.UserService;
import org.jtalks.jcommune.service.exceptions.DuplicateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * User service class. This class contains method needed to manipulate with User persistent entity.
 *
 * @author Osadchuck Eugeny
 */
public class TransactionalUserService extends AbstractTransactionlaEntityService<User> implements UserService {

    private final Logger logger = LoggerFactory.getLogger(TransactionalUserService.class);
    private UserDao userDao;

    /**
     * Create an instance of User entity based service
     *
     * @param dao - data access object, which should be able do all CRUD operations with user entity.
     */
    public TransactionalUserService(UserDao dao) {
        super(dao);
        this.userDao = dao;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User getByUsername(String username) {
        User user = userDao.getByUsername(username);
        if (user == null) {
            final String msg = "User " + username + " not found.";
            logger.info(msg);
            throw new UsernameNotFoundException(msg);
        }
        return user;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void registerUser(String username, String email, String firstName,
                             String lastName, String password) throws DuplicateException {

        if (isUserExist(username, email)) {
            final String msg = "User " + username + " already exist!";
            logger.info(msg);
            throw new DuplicateException(msg);
        }

        User user = populateUser(username, email, firstName, lastName, password);
        userDao.saveOrUpdate(user);

        logger.info("User registered: " + username);
    }

    /**
     * Populate {@link User} object from strings.
     *
     * @param username  username
     * @param email     email
     * @param firstName first name
     * @param lastName  last name
     * @param password  password
     * @return populated {@link User} object
     */
    private User populateUser(String username, String email, String firstName,
                              String lastName, String password) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPassword(password);
        return user;
    }

    /**
     * Check user with given username and email existance.
     *
     * @param username username
     * @param email    email
     * @return true if user with given username or email exist.
     */
    private boolean isUserExist(String username, String email) {
        return userDao.isUserWithUsernameExist(username) ||
                userDao.isUserWithEmailExist(email);
    }
}
