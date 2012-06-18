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
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.jtalks.common.model.permissions.ProfilePermission;
import org.jtalks.common.security.SecurityService;
import org.jtalks.jcommune.model.dao.UserDao;
import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.service.UserService;
import org.jtalks.jcommune.service.dto.UserInfoContainer;
import org.jtalks.jcommune.service.exceptions.MailingFailedException;
import org.jtalks.jcommune.service.exceptions.NotFoundException;
import org.jtalks.jcommune.service.nontransactional.AvatarService;
import org.jtalks.jcommune.service.nontransactional.Base64Wrapper;
import org.jtalks.jcommune.service.nontransactional.MailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.encoding.PasswordEncoder;

/**
 * User service class. This class contains method needed to manipulate with User persistent entity.
 * Note that this class also encrypts passwords during account creation, password changing, generating
 * a random password.
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
    //Important, use for every password creation.
    private PasswordEncoder passwordEncoder;

    /**
     * Create an instance of User entity based service
     *
     * @param dao             for operations with data storage
     * @param securityService for security
     * @param mailService     to send e-mails
     * @param base64Wrapper   for avatar image-related operations
     * @param avatarService   some more avatar operations)
     * @param passwordEncoder for password encryption
     */
    public TransactionalUserService(UserDao dao,
                                    SecurityService securityService,
                                    MailService mailService,
                                    Base64Wrapper base64Wrapper,
                                    AvatarService avatarService,
                                    PasswordEncoder passwordEncoder) {
        super(dao);
        this.securityService = securityService;
        this.mailService = mailService;
        this.base64Wrapper = base64Wrapper;
        this.avatarService = avatarService;
        this.passwordEncoder = passwordEncoder;
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
        //encrypt password
        String encodedPassword = encryptPassword(user.getPassword());
        user.setPassword(encodedPassword);
        //
        user.setRegistrationDate(new DateTime());
        user.setAvatar(avatarService.getDefaultAvatar());
        this.getDao().saveOrUpdate(user);
        mailService.sendAccountActivationMail(user);
        logger.info("JCUser registered: {}", user.getUsername());
        return user;
    }

    /**
     * Gets a hash of the password with the use of encryption mechanism.
     * If the logic of encryption will become more complicated, we will
     * have to move all this in a separate service. This mechanism is
     * the same  that used in the old forum.
     *
     * @param password password that the user entered
     * @return encrypted password
     */
    private String encryptPassword(String password) {
        return passwordEncoder.encodePassword(
                password,//value that the user entered 
                null); //We do not use salt because it is not used phpbb
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
    public JCUser editUserProfile(UserInfoContainer info) {

        JCUser currentUser = (JCUser) securityService.getCurrentUser();
        byte[] decodedAvatar = base64Wrapper.decodeB64Bytes(info.getB64EncodedAvatar());

        String newPassword = info.getNewPassword();
        if (newPassword != null) {
            String encryptedPassword = encryptPassword(newPassword);
            currentUser.setPassword(encryptedPassword);
        }
        currentUser.setEmail(info.getEmail());

        currentUser.setAvatar(decodedAvatar);

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
     * {@inheritDoc}
     */
    @Override
    public void restorePassword(String email) throws MailingFailedException {
        JCUser user = this.getDao().getByEmail(email);
        String randomPassword = RandomStringUtils.randomAlphanumeric(6);
        // first - mail attempt, then - database changes
        mailService.sendPasswordRecoveryMail(user, randomPassword);
        String encryptedRandomPassword = encryptPassword(randomPassword);
        user.setPassword(encryptedRandomPassword);
        this.getDao().update(user);

        logger.info("New random password was set for user {}", user.getUsername());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void activateAccount(String uuid) throws NotFoundException {
        JCUser user = this.getDao().getByUuid(uuid);
        if (user == null) {
            throw new NotFoundException();
        }
        user.setEnabled(true);
        this.getDao().saveOrUpdate(user);
        securityService.createAclBuilder().grant(ProfilePermission.EDIT_PROFILE).to(user).on(user).flush();
        securityService.createAclBuilder().grant(ProfilePermission.EDIT_PROFILE).to(user).on(user).flush();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Scheduled(cron = "0 * * * * *") // cron expression: invoke every hour at :00 min, e.g. 11:00, 12:00 and so on
    public void deleteUnactivatedAccountsByTimer() {
        DateTime today = new DateTime();
        for (JCUser user : this.getDao().getNonActivatedUsers()) {
            Period period = new Period(user.getRegistrationDate(), today);
            if (period.getDays() > 0) {
                this.getDao().delete(user);
            }
        }
    }
}
