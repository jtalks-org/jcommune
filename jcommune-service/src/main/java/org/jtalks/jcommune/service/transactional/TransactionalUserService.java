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
package org.jtalks.jcommune.service.transactional;

import org.jtalks.jcommune.model.dao.UserDao;
import org.jtalks.jcommune.model.entity.User;
import org.jtalks.jcommune.service.SecurityService;
import org.jtalks.jcommune.service.UserService;
import org.jtalks.jcommune.service.exceptions.DuplicateEmailException;
import org.jtalks.jcommune.service.exceptions.DuplicateUserException;
import org.jtalks.jcommune.service.exceptions.NotFoundException;
import org.jtalks.jcommune.service.exceptions.WrongPasswordException;
import org.jtalks.jcommune.service.security.SecurityConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * User service class. This class contains method needed to manipulate with User persistent entity.
 *
 * @author Osadchuck Eugeny
 * @author Kirill Afonin
 * @author Alexandre Teterin
 */
public class TransactionalUserService extends AbstractTransactionalEntityService<User, UserDao>
        implements UserService {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private SecurityService securityService;

    /**
     * Create an instance of User entity based service
     *
     * @param dao             for operations with data storage
     * @param securityService for security
     */
    public TransactionalUserService(UserDao dao, SecurityService securityService) {
        this.dao = dao;
        this.securityService = securityService;
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
    public User getByEncodedUsername(String encodedUsername) throws NotFoundException {
        User user = dao.getByEncodedUsername(encodedUsername);
        if (user == null) {
            String msg = "User " + encodedUsername + " not found.";
            logger.info(msg);
            throw new NotFoundException(msg);
        }
        return user;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User registerUser(User user) throws DuplicateUserException, DuplicateEmailException {
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
    public User editUserProfile(String email, String firstName, String lastName, String currentPassword,
                                String newPassword, byte[] avatar)
            throws DuplicateEmailException, WrongPasswordException {

        User currentUser = securityService.getCurrentUser();
        boolean changePassword = newPassword != null;
        if (changePassword) {
            if (currentPassword == null ||
                    !currentUser.getPassword().equals(currentPassword)) {
                throw new WrongPasswordException();
            } else {
                currentUser.setPassword(newPassword);
            }
        }

        boolean changeEmail = !currentUser.getEmail().equals(email);
        if (changeEmail && isEmailExist(email)) {
            throw new DuplicateEmailException();
        }

        currentUser.setEmail(email);
        currentUser.setFirstName(firstName);
        currentUser.setLastName(lastName);
        if (avatar != null && avatar.length > 0) {
            currentUser.setAvatar(avatar);
        }


        dao.saveOrUpdate(currentUser);
        return currentUser;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeAvatarFromCurrentUser() {
        User user = securityService.getCurrentUser();
        user.setAvatar(null);
    }

}
