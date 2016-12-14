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
package org.jtalks.jcommune.service;

import org.jtalks.common.model.entity.User;
import org.jtalks.jcommune.model.dto.LoginUserDto;
import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.model.entity.Language;
import org.jtalks.jcommune.model.entity.Post;
import org.jtalks.jcommune.plugin.api.exceptions.NoConnectionException;
import org.jtalks.jcommune.plugin.api.exceptions.NotFoundException;
import org.jtalks.jcommune.plugin.api.exceptions.UnexpectedErrorException;
import org.jtalks.jcommune.service.dto.UserInfoContainer;
import org.jtalks.jcommune.service.dto.UserNotificationsContainer;
import org.jtalks.jcommune.service.dto.UserSecurityContainer;
import org.jtalks.jcommune.service.exceptions.MailingFailedException;
import org.jtalks.jcommune.service.exceptions.UserTriesActivatingAccountAgainException;
import org.jtalks.jcommune.service.util.AuthenticationStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * This interface should have methods which give us more abilities in manipulating User persistent entity.
 *
 * @author Osadchuck Eugeny
 * @author Kirill Afonin
 * @author Anuar_Nurmakanov
 */
public interface UserService extends EntityService<JCUser> {
    /**
     * Get {@link JCUser} with corresponding username ignoring case. If there are
     * several 'ignore case' usernames we don't ignore case and check exact match
     * among them. See http://jira.jtalks.org/browse/JC-1163 for details.
     *
     * @param username username of User
     * @return {@link JCUser} with given username
     * @throws NotFoundException if user not found
     * @see JCUser
     */
    JCUser getByUsername(String username) throws NotFoundException;

    /**
     * Gets user currently logged in.
     *
     * @return logged in user or {@link org.jtalks.jcommune.model.entity.AnonymousUser}, if user hasn't yet log in
     */
    JCUser getCurrentUser();

    /**
     * Updates user last login time to current time.
     *
     * @param user user which must be updated
     * @see org.jtalks.jcommune.model.entity.JCUser
     */
    void updateLastLoginTime(JCUser user);

    /**
     * Update user profile.
     *
     * @param editedUserId          an identifier of edited user
     * @param editedUserProfileInfo modified profile info holder
     * @return edited user
     * @throws NotFoundException if edited user doesn't exists in system
     */
    JCUser saveEditedUserProfile(long editedUserId, UserInfoContainer editedUserProfileInfo) throws NotFoundException;

    /**
     * Update user security settings.
     *
     *
     * @param editedUserId          an identifier of edited user
     * @param editedUserSecurityInfo modified security info holder
     * @return edited user
     * @throws NotFoundException if edited user doesn't exists in system
     */
    JCUser saveEditedUserSecurity(long editedUserId, UserSecurityContainer editedUserSecurityInfo)
            throws NotFoundException;

    /**
     * Update user notification settings.
     *
     *
     * @param editedUserId          an identifier of edited user
     * @param editedUserNotificationsInfo modified notification info holder
     * @return edited user
     * @throws NotFoundException if edited user doesn't exists in system
     */
    JCUser saveEditedUserNotifications(long editedUserId, UserNotificationsContainer editedUserNotificationsInfo)
            throws NotFoundException;

    /**
     * Performs the following:
     * 1. Alters the password for this user to the random string
     * 2. Sends an e-mail with new password to this address to notify user
     * <p/>
     * If mailing fails password won't be changed.
     *
     * @param email address to identify user
     * @throws MailingFailedException if mailing failed
     */
    void restorePassword(String email) throws MailingFailedException;

    /**
     * Get user by UUID
     *
     * @param uuid unique entity identifier to locate user account
     * @throws NotFoundException if there is no user matching username given
     */
    JCUser getByUuid(String uuid) throws NotFoundException;

    /**
     * This method will be called automatically every hour to check
     * if there are expired user accounts to be deleted. User account
     * is expired if it's created, but not activated for a day or more.
     */
    void deleteUnactivatedAccountsByTimer();

    /**
     * This methods checks a permissions of user to edit profiles.
     *
     * @param userId an identifier of user, for which we check permission
     */
    void checkPermissionToEditOtherProfiles(Long userId);

    /**
     * This methods checks a permissions of user to edit own profiles.
     *
     * @param userId an identifier of user, for which we check permission
     */
    void checkPermissionToEditOwnProfile(Long userId);

    /**
     * This method checks a permissions of user to create or edit simple(static)
     * pages.
     *
     * @param userId an identifier of user, for which we check permission
     */
    void checkPermissionToCreateAndEditSimplePage(Long userId);

    /**
     * Searches for the common user, meaning that she might or might not be registered in JCommune, she can also be
     * registered by some other JTalks component. This might be required to search through all the users of JTalks.
     *
     * @param username a user's login to find her in the database, depending on the used DB Engine might or might not be
     *                 case-sensitive
     * @return a common user with the specified username
     * @throws NotFoundException if no user was found with the specified username
     */
    User getCommonUserByUsername(String username) throws NotFoundException;

    /**
     * Perform login logic for provided user. After calling this the method
     * this user will be logged in
     *
     * @param loginUserDto DTO object which represent authentication information
     * @param request    HTTP request
     * @param response   HTTP response
     * @return AuthenticationStatus.AUTHENTICATED if user's account
     * is enabled and he was logged in. AuthenticationStatus.NOT_ENABLED if
     * user's account was not activated. AuthenticationStatus.AUTHENTICATION_FAIL
     * if there were any errors during logging in.
     */
    AuthenticationStatus loginUser(LoginUserDto loginUserDto, HttpServletRequest request, HttpServletResponse response)
            throws UnexpectedErrorException, NoConnectionException;

    /**
     * Parses the input of some post which contains [user] bb code,
     * and replace this bb codes with user profile links
     *
     * @return string with BB codes replaced by user profile links
     */
    String processUserBbCodesInPost(String postContent);

    /**
     * Sends email to user that was mentioned in the post
     * and mark BB code as already notified users
     *
     * @param post post in which user was mentioned
     */
    void notifyAndMarkNewlyMentionedUsers(Post post);

    /**
     * Get usernames by pattern
     *
     * @param pattern part of username
     */
    List<String> getUsernames(String pattern);

    /**
     * Update user language.
     */
    void changeLanguage(JCUser jcUser, Language newLang);

    /**
     * Searches users with email or username matching specified key
     *
     * @param forumComponentId id of forum (for security checking)
     * @param searchKey key to search users
     * @return first 20 users with email or username matching key
     */
    List<JCUser> findByUsernameOrEmail(long forumComponentId, String searchKey);

    List<Long> getUserGroupIDs(long forumComponentId, long userID) throws NotFoundException;

    void addUserToGroup(long forumId, long userID, long groupID) throws NotFoundException;

    void deleteUserFromGroup(long forumId, long userID, long groupID) throws NotFoundException;
}
