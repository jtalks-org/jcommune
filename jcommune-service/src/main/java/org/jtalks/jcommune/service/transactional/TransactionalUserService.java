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
import org.jtalks.common.model.dao.GroupDao;
import org.jtalks.common.model.entity.Group;
import org.jtalks.common.model.entity.User;
import org.jtalks.common.security.SecurityService;
import org.jtalks.jcommune.model.dao.PostDao;
import org.jtalks.jcommune.model.dao.UserDao;
import org.jtalks.jcommune.model.entity.AnonymousUser;
import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.model.entity.Post;
import org.jtalks.jcommune.model.plugins.exceptions.NoConnectionException;
import org.jtalks.jcommune.model.plugins.exceptions.UnexpectedErrorException;
import org.jtalks.jcommune.service.Authenticator;
import org.jtalks.jcommune.service.UserService;
import org.jtalks.jcommune.service.dto.UserInfoContainer;
import org.jtalks.jcommune.service.exceptions.MailingFailedException;
import org.jtalks.jcommune.service.exceptions.NotFoundException;
import org.jtalks.jcommune.service.exceptions.UserActivationException;
import org.jtalks.jcommune.service.nontransactional.Base64Wrapper;
import org.jtalks.jcommune.service.nontransactional.EncryptionService;
import org.jtalks.jcommune.service.nontransactional.MailService;
import org.jtalks.jcommune.service.nontransactional.MentionedUsers;
import org.jtalks.jcommune.service.security.AdministrationGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.prepost.PreAuthorize;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;
import org.jtalks.jcommune.model.entity.Language;

/**
 * User service class. This class contains method needed to manipulate with User persistent entity.
 * Note that this class also encrypts passwords during account creation, password changing, generating
 * a random password.
 *
 * @author Osadchuck Eugeny
 * @author Kirill Afonin
 * @author Alexandre Teterin
 * @author Evgeniy Naumenko
 * @author Mikhail Zaitsev
 * @author Andrei Alikov
 * @author Andrey Pogorelov
 */
public class TransactionalUserService extends AbstractTransactionalEntityService<JCUser, UserDao>
        implements UserService {

    private GroupDao groupDao;
    private SecurityService securityService;
    private MailService mailService;
    private Base64Wrapper base64Wrapper;
    //Important, use for every password creation.
    private EncryptionService encryptionService;
    private final PostDao postDao;

    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionalUserService.class);

    /**
     * Create an instance of User entity based service
     *
     * @param dao               for operations with data storage
     * @param groupDao          for user group operations with data storage
     * @param securityService   for security
     * @param mailService       to send e-mails
     * @param base64Wrapper     for avatar image-related operations
     * @param encryptionService encodes user password before store
     * @param postDao           for operations with posts
     */
    public TransactionalUserService(UserDao dao,
                                    GroupDao groupDao,
                                    SecurityService securityService,
                                    MailService mailService,
                                    Base64Wrapper base64Wrapper,
                                    EncryptionService encryptionService,
                                    PostDao postDao) {
        super(dao);
        this.groupDao = groupDao;
        this.securityService = securityService;
        this.mailService = mailService;
        this.base64Wrapper = base64Wrapper;
        this.encryptionService = encryptionService;
        this.postDao = postDao;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JCUser getByUsername(String username) throws NotFoundException {
        JCUser user = this.getDao().getByUsername(username);
        if (user == null) {
            String msg = "JCUser [" + username + "] not found.";
            LOGGER.info(msg);
            throw new NotFoundException(msg);
        }
        return user;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User getCommonUserByUsername(String username) throws NotFoundException {
        User user = this.getDao().getCommonUserByUsername(username);
        if (user == null) {
            String msg = "Common User [" + username + "] not found.";
            LOGGER.info(msg);
            throw new NotFoundException(msg);
        }
        return user;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getUsernames(String pattern) {
        int usernameCount = 10;
        return getDao().getUsernames(pattern, usernameCount);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JCUser getCurrentUser() {
        String name = securityService.getCurrentUserUsername();
        if (name == null) {
            return new AnonymousUser();
        } else {
            return this.getDao().getByUsername(name);
        }

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
    public JCUser saveEditedUserProfile(
            long editedUserId, UserInfoContainer editedUserProfileInfo) throws NotFoundException {

        JCUser editedUser = this.get(editedUserId);
        byte[] decodedAvatar = base64Wrapper.decodeB64Bytes(editedUserProfileInfo.getB64EncodedAvatar());

        String newPassword = editedUserProfileInfo.getNewPassword();
        if (newPassword != null) {
            String encryptedPassword = encryptionService.encryptPassword(newPassword);
            editedUser.setPassword(encryptedPassword);
        }
        editedUser.setEmail(editedUserProfileInfo.getEmail());

        if (!Arrays.equals(editedUser.getAvatar(), decodedAvatar)) {
            editedUser.setAvatarLastModificationTime(new DateTime());
        }
        editedUser.setAvatar(decodedAvatar);
        editedUser.setSignature(editedUserProfileInfo.getSignature());
        editedUser.setFirstName(editedUserProfileInfo.getFirstName());
        editedUser.setLastName(editedUserProfileInfo.getLastName());
        editedUser.setPageSize(editedUserProfileInfo.getPageSize());
        editedUser.setLocation(editedUserProfileInfo.getLocation());
        editedUser.setAutosubscribe(editedUserProfileInfo.isAutosubscribe());
        editedUser.setMentioningNotificationsEnabled(editedUserProfileInfo.isMentioningNotificationsEnabled());
        editedUser.setSendPmNotification(editedUserProfileInfo.isSendPmNotification());

        this.getDao().saveOrUpdate(editedUser);
        LOGGER.info("Updated user profile. Username: {}", editedUser.getUsername());
        return editedUser;
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
        String encryptedRandomPassword = encryptionService.encryptPassword(randomPassword);
        user.setPassword(encryptedRandomPassword);
        this.getDao().saveOrUpdate(user);

        LOGGER.info("New random password was set for user {}", user.getUsername());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void activateAccount(String uuid) throws NotFoundException, UserActivationException {
        JCUser user = this.getDao().getByUuid(uuid);
        if (user == null) {
            throw new NotFoundException();
        } else if (!user.isEnabled()) {
            Group group = groupDao.getGroupByName(AdministrationGroup.USER.getName());
            user.addGroup(group);
            user.setEnabled(true);
            this.getDao().saveOrUpdate(user);
        } else {
            throw new UserActivationException();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JCUser getByUuid(String uuid) throws NotFoundException {
        JCUser user = this.getDao().getByUuid(uuid);
        if (user == null) {
            throw new NotFoundException();
        } else {
            return user;
        }
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

    /**
     * {@inheritDoc}
     */
    @Override
    @PreAuthorize("hasPermission(#userId, 'USER', 'ProfilePermission.EDIT_OTHERS_PROFILE')")
    public void checkPermissionToEditOtherProfiles(Long userId) {
        LOGGER.debug("Check permission to edit other profiles for user - " + userId);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    @PreAuthorize("hasPermission(#userId, 'USER', 'ProfilePermission.EDIT_OWN_PROFILE')")
    public void checkPermissionToEditOwnProfile(Long userId) {
        LOGGER.debug("Check permission to edit own profile for user - " + userId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @PreAuthorize("hasPermission(#userId, 'USER', 'ProfilePermission.CREATE_FORUM_FAQ')")
    public void checkPermissionToCreateAndEditSimplePage(Long userId) {
        LOGGER.debug("Check permission to create or edit simple(static) pages - " + userId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean loginUser(String username, String password, boolean rememberMe,
                             HttpServletRequest request, HttpServletResponse response, Authenticator authenticator)
            throws UnexpectedErrorException, NoConnectionException {
        return authenticator.authenticate(username, password, rememberMe, request, response);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String processUserBbCodesInPost(String postContent) {
        MentionedUsers mentionedUsers = MentionedUsers.parse(postContent);
        return mentionedUsers.getTextWithProcessedUserTags(getDao());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void notifyAndMarkNewlyMentionedUsers(Post post) {
        MentionedUsers mentionedUsers = MentionedUsers.parse(post);
        List<JCUser> usersToNotify = mentionedUsers.getNewUsersToNotify(getDao());

        for (JCUser user : usersToNotify) {
            mailService.sendUserMentionedNotification(user, post.getId());
        }

        mentionedUsers.markUsersAsAlreadyNotified(postDao);
    }

    /**
     * {@inheritDoc}
     */    
    @Override
    public void changeLanguage(JCUser jcUser, Language newLang) {
        jcUser.setLanguage(newLang);
        this.getDao().saveOrUpdate(jcUser);
    }
}
