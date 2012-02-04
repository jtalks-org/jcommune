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

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.jtalks.jcommune.model.dao.UserDao;
import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.service.UserService;
import org.jtalks.jcommune.service.dto.UserInfoContainer;
import org.jtalks.jcommune.service.exceptions.DuplicateEmailException;
import org.jtalks.jcommune.service.exceptions.MailingFailedException;
import org.jtalks.jcommune.service.exceptions.NotFoundException;
import org.jtalks.jcommune.service.exceptions.WrongPasswordException;
import org.jtalks.jcommune.service.nontransactional.AvatarService;
import org.jtalks.jcommune.service.nontransactional.Base64Wrapper;
import org.jtalks.jcommune.service.nontransactional.MailService;
import org.jtalks.jcommune.service.nontransactional.SecurityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * User service class. This class contains method needed to manipulate with User persistent entity.
 *
 * @author Osadchuck Eugeny
 * @author Kirill Afonin
 * @author Alexandre Teterin
 * @author Evgeniy Naumenko
 */
public class TransactionalUserService extends AbstractTransactionalEntityService<JCUser, UserDao>
        implements UserService {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private SecurityService securityService;
    private MailService mailService;
    private Base64Wrapper base64Wrapper;
    private AvatarService avatarService;

    /**
     * Create an instance of User entity based service
     *
     * @param dao             for operations with data storage
     * @param securityService for security
     * @param mailService     to send e-mails
     * @param base64Wrapper   for avatar image-related operations
     * @param avatarService   some more avatar operations)
     */
    public TransactionalUserService(UserDao dao, SecurityService securityService,
                                    MailService mailService, Base64Wrapper base64Wrapper, AvatarService avatarService) {
        super(dao);
        this.securityService = securityService;
        this.mailService = mailService;
        this.base64Wrapper = base64Wrapper;
        this.avatarService = avatarService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JCUser getByUsername(String username) throws NotFoundException {
        JCUser user = this.getDao().getByUsername(username);
        if (user == null) {
            String msg = "JCUser " + username + " not found.";
            logger.info(msg);
            throw new NotFoundException(msg);
        }
        return user;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public JCUser registerUser(JCUser user) {
        user.setRegistrationDate(new DateTime());
        user.setAvatar(avatarService.getDefaultAvatar());
        this.getDao().saveOrUpdate(user);
        mailService.sendAccountActivationMail(user);
        logger.info("JCUser registered: {}", user.getUsername());
        return user;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateLastLoginTime(JCUser user) {
        user.updateLastLoginTime();
        this.getDao().saveOrUpdate(user);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JCUser editUserProfile(UserInfoContainer info) throws DuplicateEmailException, WrongPasswordException {

        JCUser currentUser = securityService.getCurrentUser();
        byte[] decodedAvatar = base64Wrapper.decodeB64Bytes(info.getB64EncodedAvatar());

        this.changePassword(info.getCurrentPassword(), info.getNewPassword(), currentUser);
        this.changeEmail(info.getEmail(), currentUser);
        this.changeAvatar(decodedAvatar, currentUser);
        currentUser.setSignature(info.getSignature());
        currentUser.setFirstName(info.getFirstName());
        currentUser.setLastName(info.getLastName());
        currentUser.setLanguage(info.getLanguage());
        currentUser.setPageSize(info.getPageSize());
        currentUser.setLocation(info.getLocation());

        this.getDao().saveOrUpdate(currentUser);
        logger.info("Updated user profile. Username: {}", currentUser.getUsername());
        return currentUser;
    }

    /**
     * Checks if e-mail passed differs from the one set in the User object
     * and new email is valid, i. e. has not been used by any other user.
     * <p/>
     * If no violations found, this method then will set new email value
     * to the User object passed.
     *
     * @param email       new address to be validated and set
     * @param currentUser user object to be checked
     * @throws DuplicateEmailException if email set is already in use
     */
    private void changeEmail(String email, JCUser currentUser) throws DuplicateEmailException {
        if (!currentUser.getEmail().equals(email)) {
            JCUser user = this.getDao().getByEmail(email);
            if (user != null) {
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
     * @param currentUser user object from a database
     * @throws WrongPasswordException if current password doesn't match the one stored in database
     */
    private void changePassword(String currentPass, String newPass, JCUser currentUser) throws WrongPasswordException {
        if (newPass != null) {
            if (currentUser.getPassword().equals(currentPass)) {
                currentUser.setPassword(newPass);
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
    private void changeAvatar(byte[] avatar, JCUser currentUser) {
        if (avatar != null && avatar.length > 0) {
            currentUser.setAvatar(avatar);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void restorePassword(String email) throws MailingFailedException {
        JCUser user = this.getDao().getByEmail(email);
        String randomPassword = RandomStringUtils.randomAlphanumeric(6);
        // first - mail attempt, then - database changes
        mailService.sendPasswordRecoveryMail(user.getUsername(), email, randomPassword);
        user.setPassword(randomPassword);
        this.getDao().update(user);

        logger.info("New random password was set for user {}", user.getUsername());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void activateAccount(String uuid) throws NotFoundException {
        JCUser user = this.getDao().getByUuid(uuid);
        if (user == null){
            throw new NotFoundException();
        }
        user.setEnabled(true);
        this.getDao().saveOrUpdate(user);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Scheduled(cron="0 * * * * *") // cron expression: invoke every hour at :00 min, e.g. 11:00, 12:00 and so on
    public void deleteUnactivatedAccountsByTimer(){
        DateTime today = new DateTime();
        for (JCUser user : this.getDao().getNonActivatedUsers()){
            Period period = new Period(user.getRegistrationDate(), today);
            if (period.getDays() > 0){
                 this.getDao().delete(user);
            }
        }
    }
}
