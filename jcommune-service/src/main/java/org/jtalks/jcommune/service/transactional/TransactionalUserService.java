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
import org.jtalks.jcommune.service.dto.UserInfoContainer;
import org.jtalks.jcommune.service.exceptions.*;
import org.jtalks.jcommune.service.security.SecurityConstants;
import org.jtalks.jcommune.service.util.ImagePreprocessor;
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
    private ImagePreprocessor imagePreprocessor;

    /**
     * Create an instance of User entity based service
     *
     * @param dao             for operations with data storage
     * @param securityService for security
     * @param mailService     to send e-mails
     * @param imagePreprocessor  for avatar image-related operations
     */
    public TransactionalUserService(UserDao dao, SecurityService securityService,
                                    MailService mailService, ImagePreprocessor imagePreprocessor) {
        super(dao);
        this.securityService = securityService;
        this.mailService = mailService;
        this.imagePreprocessor = imagePreprocessor;
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
        return user;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User registerUser(User user) throws DuplicateUserException, DuplicateEmailException {
        if (this.isUserExist(user.getUsername())) {
            String msg = "Failed to register user. User " + user.getUsername() + " already exists!";
            logger.info(msg);
            throw new DuplicateUserException(msg);
        }
        if (this.isEmailExist(user.getEmail())) {
            String msg = "Failed to register user. E-mail " + user.getEmail() + " already exists!";
            logger.info(msg);
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
    @Override
    public User editUserProfile(UserInfoContainer info) throws DuplicateEmailException, WrongPasswordException {

        User currentUser = securityService.getCurrentUser();
        byte[] decodedAvatar = imagePreprocessor.decodeB64(info.getB64EncodedAvatar());

        this.changePassword(info.getCurrentPassword(), info.getNewPassword(), currentUser);
        this.changeEmail(info.getEmail(), currentUser);
        this.changeAvatar(decodedAvatar, currentUser);
        currentUser.setSignature(info.getSignature());
        currentUser.setFirstName(info.getFirstName());
        currentUser.setLastName(info.getLastName());
        currentUser.setLanguage(info.getLanguage());
        currentUser.setPageSize(info.getPageSize());

        this.getDao().saveOrUpdate(currentUser);
        logger.info("Updated user profile. Username: {}", currentUser.getUsername());
        return currentUser;
    }

    /**
     * Checks if e-mail passed differs from the one set in the User object
     * and new email is valid, i. e. has not been used by any other user.
     * <p/>
     * If no violations found, this method then wiil set new email value
     * to the User object passed.
     *
     * @param email       new address to be validated and set
     * @param currentUser user object to be checked
     * @throws org.jtalks.jcommune.service.exceptions.DuplicateEmailException
     *          if email set is already in use
     */
    private void changeEmail(String email, User currentUser) throws DuplicateEmailException {
        if (!currentUser.getEmail().equals(email)) {
            if (this.isEmailExist(email)) {
                throw new DuplicateEmailException();
            } else {
                currentUser.setEmail(email);
            }
        }
    }

    /**
     * Checks if current password was filled up correctly and if so,
     * alters the current password to the new one
     *
     * @param currentPass existing password from the form to verify identity
     * @param newPass     new password to be set
     * @param current     user object from a database
     * @throws WrongPasswordException if current password doesn't match the one stored in database
     */
    private void changePassword(String currentPass, String newPass, User current) throws WrongPasswordException {
        if (newPass != null) {
            if (current.getPassword().equals(currentPass)) {
                current.setPassword(newPass);
            } else {
                throw new WrongPasswordException();
            }
        }
    }

    /**
     * Checks if byte[] passed is not empty, that means user has uploaded
     * a new avatar image. If so, user passed will be assigned this new image.
     *
     * @param avatar      avatar image representation
     * @param currentUser user to be set with new avatar image
     */
    private void changeAvatar(byte[] avatar, User currentUser) {
        if (avatar != null && avatar.length > 0) {
            currentUser.setAvatar(avatar);
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
    public void restorePassword(String email) throws NotFoundException, MailingFailedException {
        User user = this.getDao().getByEmail(email);
        if (user == null) {
            String message = "No user matches the email " + email;
            logger.info(message);
            throw new NotFoundException(message);
        }
        String randomPassword = Long.toString(new Random().nextInt(100000000), 36); // 5-6 chars
        // first - mail attempt, then - database changes
        mailService.sendPasswordRecoveryMail(user.getUsername(), email, randomPassword);
        user.setPassword(randomPassword);
        this.getDao().update(user);

        logger.info("New random password was set for user {}", user.getUsername());
    }

    /**
     * Checks whether username passed is associated with any known user.
     *
     * @param userName user login to check
     * @return true is login given matches some user in a database
     */
    private boolean isUserExist(String userName) {
        return SecurityConstants.ANONYMOUS_USERNAME.equals(userName) || this.getDao().isUserWithUsernameExist(userName);
    }

    /**
     * {@inheritDoc}
     */
    public boolean isEmailExist(String email) {
        return this.getDao().isUserWithEmailExist(email);
    }
}
