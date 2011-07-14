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
import org.jtalks.jcommune.service.exceptions.DuplicateEmailException;
import org.jtalks.jcommune.service.exceptions.DuplicateException;
import org.jtalks.jcommune.service.exceptions.DuplicateUserException;
import org.jtalks.jcommune.service.exceptions.NotFoundException;
import org.jtalks.jcommune.service.security.SecurityConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;

/**
 * User service class. This class contains method needed to manipulate with User persistent entity.
 *
 * @author Osadchuck Eugeny
 * @author Kirill Afonin
 * @author Alexandre Teterin
 */
public class TransactionalUserService extends AbstractTransactionalEntityService<User, UserDao> implements UserService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * Create an instance of User entity based service
     *
     * @param dao - data access object, which should be able do all CRUD operations with user entity
     */
    public TransactionalUserService(UserDao dao) {
        this.dao = dao;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User getByUsername(String username) throws NotFoundException {
        User user = dao.getByUsername(username);
        if (user == null) {
            String msg = "User " + username + " not found.";
            logger.info(msg);
            throw new NotFoundException(msg);
        }
        return user;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User registerUser(User user) throws DuplicateUserException, DuplicateEmailException, UnsupportedEncodingException {
        if (isUserExist(user)) {
            String msg = "User " + user.getUsername() + " already exists!";
            logger.warn(msg);
            throw new DuplicateUserException(msg);
        }
        if (isEmailExist(user.getEmail())) {
            String msg = "E-mail " + user.getEmail() + " already exists!";
            logger.warn(msg);
            throw new DuplicateEmailException(msg);
        }

        user.setEncodedUsername(user.getUsername());

        dao.saveOrUpdate(user);

        logger.info("User registered: {}", user.getUsername());
        return user;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateLastLoginTime(User user) {
        user.updateLastLoginTime();
        dao.saveOrUpdate(user);
    }

    /**
     * Check user for existance.
     *
     * @param user user for check existance
     * @return {@code true} if user with given username exist
     */
    private boolean isUserExist(User user) {
        return user.getUsername().equals(SecurityConstants.ANONYMOUS_USERNAME)
                || dao.isUserWithUsernameExist(user.getUsername());
    }

    /**
     * Check email for existance.
     *
     * @param email email for check existance
     * @return {@code true} if email exist
     */
    private boolean isEmailExist(String email) {
        return dao.isUserWithEmailExist(email);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void editUserProfile(User user) {
        dao.saveOrUpdate(user);
    }
}
