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
import org.jtalks.common.model.entity.User;
import org.jtalks.jcommune.model.dao.PostDao;
import org.jtalks.jcommune.model.dao.UserDao;
import org.jtalks.jcommune.model.dto.LoginUserDto;
import org.jtalks.jcommune.model.entity.*;
import org.jtalks.jcommune.plugin.api.exceptions.NoConnectionException;
import org.jtalks.jcommune.plugin.api.exceptions.NotFoundException;
import org.jtalks.jcommune.plugin.api.exceptions.UnexpectedErrorException;
import org.jtalks.jcommune.plugin.api.service.UserReader;
import org.jtalks.jcommune.service.Authenticator;
import org.jtalks.jcommune.service.UserService;
import org.jtalks.jcommune.service.dto.UserInfoContainer;
import org.jtalks.jcommune.service.dto.UserNotificationsContainer;
import org.jtalks.jcommune.service.dto.UserSecurityContainer;
import org.jtalks.jcommune.service.exceptions.MailingFailedException;
import org.jtalks.jcommune.service.nontransactional.Base64Wrapper;
import org.jtalks.jcommune.service.nontransactional.EncryptionService;
import org.jtalks.jcommune.service.nontransactional.MailService;
import org.jtalks.jcommune.service.nontransactional.MentionedUsers;
import org.jtalks.jcommune.service.security.SecurityService;
import org.jtalks.jcommune.service.util.AuthenticationStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;

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
        implements UserService, UserReader {

    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionalUserService.class);
    protected static final int MAX_SEARCH_USER_COUNT=20;

    private final PostDao postDao;
    private final Authenticator authenticator;
    private final GroupDao groupDao;
    private final SecurityService securityService;
    private final MailService mailService;
    private final Base64Wrapper base64Wrapper;
    //Important, use for every password creation.
    private final EncryptionService encryptionService;

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
     * @param authenticator     for user authentication
     */
    public TransactionalUserService(UserDao dao,
                                    GroupDao groupDao,
                                    SecurityService securityService,
                                    MailService mailService,
                                    Base64Wrapper base64Wrapper,
                                    EncryptionService encryptionService,
                                    PostDao postDao,
                                    Authenticator authenticator) {
        super(dao);
        this.groupDao = groupDao;
        this.securityService = securityService;
        this.mailService = mailService;
        this.base64Wrapper = base64Wrapper;
        this.encryptionService = encryptionService;
        this.postDao = postDao;
        this.authenticator = authenticator;
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
        UserInfo userInfo = securityService.getCurrentUserBasicInfo();
        return userInfo != null ? this.getDao().loadById(userInfo.getId()) : new AnonymousUser();
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

        this.getDao().saveOrUpdate(editedUser);
        LOGGER.info("Updated user profile. Username: {}", editedUser.getUsername());
        return editedUser;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JCUser saveEditedUserSecurity(long editedUserId, UserSecurityContainer userSecurityInfo)
            throws NotFoundException {
        JCUser editedUser = this.get(editedUserId);

        String newPassword = userSecurityInfo.getNewPassword();
        if (newPassword != null) {
            String encryptedPassword = encryptionService.encryptPassword(newPassword);
            editedUser.setPassword(encryptedPassword);
        }

        this.getDao().saveOrUpdate(editedUser);
        LOGGER.info("Updated user security settings. Username: {}", editedUser.getUsername());
        return editedUser;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JCUser saveEditedUserNotifications(long editedUserId, UserNotificationsContainer userNotificationsInfo)
            throws NotFoundException {
        JCUser editedUser = this.get(editedUserId);

        editedUser.setMentioningNotificationsEnabled(userNotificationsInfo.isMentioningNotificationsEnabled());
        editedUser.setSendPmNotification(userNotificationsInfo.isSendPmNotification());
        editedUser.setAutosubscribe(userNotificationsInfo.isAutosubscribe());

        this.getDao().saveOrUpdate(editedUser);
        LOGGER.info("Updated user notification settings. Username: {}", editedUser.getUsername());
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
//  Temporarily disabled. Until we find the bug due to which activated users become not activated.
//    @Scheduled(cron = "0 * * * * *") // cron expression: invoke every hour at :00 min, e.g. 11:00, 12:00 and so on
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
    public AuthenticationStatus loginUser(LoginUserDto loginUserDto, HttpServletRequest request, HttpServletResponse response)
            throws UnexpectedErrorException, NoConnectionException {
        return authenticator.authenticate(loginUserDto, request, response);
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

    @Override
    @PreAuthorize("hasPermission(#forumComponentId, 'COMPONENT', 'GeneralPermission.ADMIN')")
    public List<JCUser> findByUsernameOrEmail(long forumComponentId, String searchKey) {
        return getDao().findByUsernameOrEmail(searchKey, MAX_SEARCH_USER_COUNT);
    }

    @Override
    @PreAuthorize("hasPermission(#forumComponentId, 'COMPONENT', 'GeneralPermission.ADMIN')")
    public List<Long> getUserGroupIDs(long forumComponentId, long userID) throws NotFoundException {
        JCUser jcUser = getDao().get(userID);
        return jcUser.getGroupsIDs();
    }

    @Override
    @PreAuthorize("hasPermission(#forumComponentId, 'COMPONENT', 'GeneralPermission.ADMIN')")
    public void addUserToGroup(long forumComponentId, long userID, long groupID) throws NotFoundException {
        JCUser jcUser = getDao().get(userID);
        jcUser.addGroup(groupDao.get(groupID));

        this.getDao().saveOrUpdate(jcUser);
    }

    @Override
    @PreAuthorize("hasPermission(#forumComponentId, 'COMPONENT', 'GeneralPermission.ADMIN')")
    public void deleteUserFromGroup(long forumComponentId, long userID, long groupID) throws NotFoundException {
        JCUser jcUser = getDao().get(userID);
        jcUser.deleteGroup(groupDao.get(groupID));

        this.getDao().saveOrUpdate(jcUser);
    }
}
