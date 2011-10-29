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
import org.jtalks.jcommune.service.MailService;
import org.jtalks.jcommune.service.SecurityService;
import org.jtalks.jcommune.service.UserService;
import org.jtalks.jcommune.service.exceptions.DuplicateEmailException;
import org.jtalks.jcommune.service.exceptions.DuplicateUserException;
import org.jtalks.jcommune.service.exceptions.NotFoundException;
import org.jtalks.jcommune.service.exceptions.WrongPasswordException;
import org.jtalks.jcommune.service.security.SecurityConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

/**
 * User service class. This class contains method needed to manipulate with User persistent entity.
 *
 * @author Osadchuck Eugeny
 * @author Kirill Afonin
 * @author Alexandre Teterin
 * @author Evgeniy Naumenko
 */
public class TransactionalUserService extends AbstractTransactionalEntityService<User, UserDao>
        implements UserService {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private SecurityService securityService;
    private MailService mailService;

    /**
     * Create an instance of User entity based service
     *
     * @param dao             for operations with data storage
     * @param securityService for security
     * @param mailService     to send e-mails
     */
    public TransactionalUserService(UserDao dao, SecurityService securityService, MailService mailService) {
        super(dao);
        this.securityService = securityService;
        this.mailService = mailService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User getByUsername(String username) throws NotFoundException {
        User user = this.getDao().getByUsername(username);
        if (user == null) {
            String msg = "User " + username + " not found.";
            logger.warn(msg);
            throw new NotFoundException(msg);
        }
        int postCount = this.getDao().getCountPostOfUser(user);
        user.setUserPostCount(postCount);
        return user;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User getByEncodedUsername(String encodedUsername) throws NotFoundException {
        User user = this.getDao().getByEncodedUsername(encodedUsername);
        if (user == null) {
            String msg = "User " + encodedUsername + " not found.";
            logger.info(msg);
            throw new NotFoundException(msg);
        }
        user.setUserPostCount(this.getDao().getCountPostOfUser(user));
        return user;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User registerUser(User user) throws DuplicateUserException, DuplicateEmailException {
        if (isUserExist(user.getUsername())) {
            String msg = "User " + user.getUsername() + " already exists!";
            logger.warn(msg);
            throw new DuplicateUserException(msg);
        }
        if (isEmailExist(user.getEmail())) {
            String msg = "E-mail " + user.getEmail() + " already exists!";
            logger.warn(msg);
            throw new DuplicateEmailException(msg);
        }

        this.getDao().saveOrUpdate(user);

        logger.info("User registered: {}", user.getUsername());
        return user;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateLastLoginTime(User user) {
        user.updateLastLoginTime();
        this.getDao().saveOrUpdate(user);
    }

    /**
     * {@inheritDoc}
     */
    public boolean isEmailExist(String email) {
        return this.getDao().isUserWithEmailExist(email);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User editUserProfile(String email, String firstName, String lastName, String currentPassword,
                                String newPassword, byte[] avatar, String signature, String language, String pageSize)
        throws DuplicateEmailException, WrongPasswordException {

        User currentUser = securityService.getCurrentUser();

        boolean changePassword = newPassword != null;
        if (changePassword) {
            changePassword(currentPassword, newPassword, currentUser);
        }

        if (isChangeEmail(email, currentUser)) {
            throw new DuplicateEmailException();
        }

        currentUser.setEmail(email);
        currentUser.setSignature(getSignature(signature));
        currentUser.setFirstName(firstName);
        currentUser.setLastName(lastName);
        currentUser.setLanguage(language);
        currentUser.setPageSize(pageSize);
        if (isChangeAvatar(avatar)) {
            currentUser.setAvatar(avatar);
        }

        this.getDao().saveOrUpdate(currentUser);
        return currentUser;
    }

    /**
     * Checks if e-mail passed differs from the one set in the User object
     * and new email is valid, i. e. has not been used by any other user
     *
     * @param email new address
     * @param currentUser user object to be checked
     * @return true, if e-mail has been changed to a valid one
     */
    private boolean isChangeEmail(String email, User currentUser) {
        return (!currentUser.getEmail().equals(email)) && isEmailExist(email);
    }

    /**
     * Checks if byte[] passed is not empty, that means user has uploaded
     * a new avatar image
     *
     * @param avatar avatar image representation
     * @return true if avatar image has been set
     */
    private boolean isChangeAvatar(byte[] avatar) {
        return avatar != null && avatar.length > 0;
    }

    /**
     * Checks if current password was filled up correctly and if so,
     * alters the current password to the new one
     *
     * @param currentPassword existing password to verify identity
     * @param newPassword new password to be set
     * @param currentUser user object from a database
     * @throws WrongPasswordException if current password doesn't match the one stored in database
     */
    private void changePassword(String currentPassword, String newPassword, User currentUser)
        throws WrongPasswordException {
        if (currentPassword == null ||
                !currentUser.getPassword().equals(currentPassword)) {
            throw new WrongPasswordException();
        } else {
            currentUser.setPassword(newPassword);
        }
    }

    /**
     * Returns parameter as is if string passed is not empty
     *
     * @param signature  string to be checked
     * @return parameter value or null, if parameter is null or blank
     */
    private String getSignature(String signature) {
        if (signature != null && signature.trim().equals("")) {
            return null;
        } else {
            return signature;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeAvatarFromCurrentUser() {
        User user = securityService.getCurrentUser();
        user.setAvatar(null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getCountPostOfUser(User userCreated) {
        return this.getDao().getCountPostOfUser(userCreated);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void restorePassword(String email) throws NotFoundException {
        User user = this.getDao().getByEmail(email);
        if (user == null) {
            String message = "No user matches the email " + email;
            logger.error(message);
            throw new NotFoundException(message);
        }
        String randomPassword = Long.toString(new Random().nextInt(100000000), 36); // 5-6 chars
        user.setPassword(randomPassword);
        this.getDao().update(user);
        logger.info("New random password was set for user {}", new Object[]{user.getUsername()});
        mailService.sendPasswordRecoveryMail(user.getUsername(), email, randomPassword);
    }

    /**
     * Checks whether username passed is associated with any known user.
     *
     * @param userName user login to check
     * @return true is login given matches some user in a database
     */
    private boolean isUserExist(String userName) {
        return SecurityConstants.ANONYMOUS_USERNAME.equals(userName)
                || this.getDao().isUserWithUsernameExist(userName);
    }
}
