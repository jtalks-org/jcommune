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
package org.jtalks.jcommune.web.dto;

import org.apache.commons.lang.StringUtils;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.jtalks.common.model.entity.User;
import org.jtalks.common.validation.annotations.Email;
import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.model.entity.Language;
import org.jtalks.jcommune.model.validation.annotations.Matches;
import org.jtalks.jcommune.service.dto.UserInfoContainer;
import org.jtalks.jcommune.web.validation.annotations.*;

import javax.validation.constraints.Size;

/**
 * This dto used for transferring data in edit {@link org.jtalks.jcommune.model.entity.JCUser} profile operation.
 * To get more info see
 * {@link org.jtalks.jcommune.web.controller.UserProfileController#saveEditedProfile(EditUserProfileDto,
 * org.springframework.validation.BindingResult, javax.servlet.http.HttpServletResponse)}.
 *
 * @author Osadchuck Eugeny
 */
@Matches(field = "newUserPassword", verifyField = "newUserPasswordConfirm", message = "{password_not_matches}")
@ChangedEmail
@ChangedPassword
public class EditUserProfileDto {
    private long userId;
    private String username;

    @NotBlank(message = "{validation.not_null}")
    @Size(max = User.EMAIL_MAX_LENGTH, message = "{user.email.illegal_length}")
    @Email(message = "{validation.invalid_email_format}")
    private String email;

    @Size(max = User.USERNAME_FIRSTNAME_MAX_LENGTH, message = "{user.last_name.illegal_length}")
    private String firstName;

    @Size(max = User.USERNAME_LASTNAME_MAX_LENGTH, message = "{user.first_name.illegal_length}")
    private String lastName;
    @Size(max = JCUser.MAX_SIGNATURE_SIZE, message = "{validation.signature.length}")
    @BbCodeNesting
    private String signature;
    private String currentUserPassword;
    @NullableNotBlank(message = "{validation.not_null}")
    @Size(min = User.PASSWORD_MIN_LENGTH, max = User.PASSWORD_MAX_LENGTH)
    private String newUserPassword;
    private String newUserPasswordConfirm;
    private Language language;
    @PageSize(message = "{validation.profile.page.size}")
    private int pageSize;
    private boolean autosubscribe;
    private boolean mentioningNotificationsEnabled;
    private boolean sendPmNotification;
    private String avatar;
    @Length(max = JCUser.MAX_LOCATION_SIZE)
    private String location;

    /**
     * Returns all the page size values available for the user
     * to choose from.
     *
     * @return array of page sizes available
     */
    public int[] getPageSizesAvailable() {
        return JCUser.PAGE_SIZES_AVAILABLE;
    }

    /**
     * Form info population only, please do not call it explicitly
     */
    public EditUserProfileDto() {
    }

    /**
     * Constructor which fills dto fields from user.
     *
     * @param user copying source
     */
    public EditUserProfileDto(JCUser user) {
        this.userId = user.getId();
        this.username = user.getUsername();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.email = user.getEmail();
        this.signature = user.getSignature();
        this.language = user.getLanguage();
        this.pageSize = user.getPageSize();
        this.autosubscribe = user.isAutosubscribe();
        this.mentioningNotificationsEnabled = user.isMentioningNotificationsEnabled();
        this.location = user.getLocation();
        this.sendPmNotification = user.isSendPmNotification();
    }

    /**
     * Transforms DTO into container object - convenience implementation to
     * be passed to the service layer.
     *
     * @return user profile modification info for the service tier
     */
    public UserInfoContainer getUserInfoContainer() {
        return new UserInfoContainer(this.getFirstName(), this.getLastName(), this.getEmail(),
                this.getCurrentUserPassword(), this.getNewUserPassword(), this.getSignature(),
                this.getAvatar(), this.getLanguage(), this.getPageSize(), this.isAutosubscribe(),
                this.isMentioningNotificationsEnabled(), this.getLocation(), this.isSendPmNotification());
    }

    /**
     * Get the primary id of the user.
     *
     * @return the id
     */
    public long getUserId() {
        return userId;
    }

    /**
     * Set the primary id of the user.
     *
     * @param userId the id
     */
    public void setUserId(long userId) {
        this.userId = userId;
    }

    /**
     * @return - current user password
     */
    public String getCurrentUserPassword() {
        return currentUserPassword;
    }

    /**
     * Set current user password
     *
     * @param currentUserPassword - current user password
     */
    public void setCurrentUserPassword(String currentUserPassword) {
        this.currentUserPassword = currentUserPassword;
    }

    /**
     * @return - new user password
     */
    public String getNewUserPassword() {
        return newUserPassword;
    }

    /**
     * Set new user password
     *
     * @param newUserPassword - new user password
     */
    public void setNewUserPassword(String newUserPassword) {
        this.newUserPassword = newUserPassword;
    }

    /**
     * @return - new user password confirmation
     */
    public String getNewUserPasswordConfirm() {
        return newUserPasswordConfirm;
    }

    /**
     * Set new user password confirmation.
     *
     * @param newUserPasswordConfirm - new user password confirmation
     */
    public void setNewUserPasswordConfirm(String newUserPasswordConfirm) {
        this.newUserPasswordConfirm = newUserPasswordConfirm;
    }

    /**
     * @see org.jtalks.jcommune.model.entity.JCUser#isAutosubscribe()
     */
    public boolean isAutosubscribe() {
        return autosubscribe;
    }

    /**
     * @see JCUser#setAutosubscribe(boolean)
     */
    public void setAutosubscribe(boolean autosubscribe) {
        this.autosubscribe = autosubscribe;
    }

    /**
     * @see JCUser#isMentioningNotificationsEnabled()
     */
    public boolean isMentioningNotificationsEnabled() {
        return mentioningNotificationsEnabled;
    }

    /**
     * @see JCUser#setMentioningNotificationsEnabled(boolean)
     */
    public void setMentioningNotificationsEnabled(
            boolean mentioningNotificationsEnabled) {
        this.mentioningNotificationsEnabled = mentioningNotificationsEnabled;
    }

    /**
     * @return - user avatar
     */
    public String getAvatar() {
        return avatar;
    }

    /**
     * Set user avatar.
     *
     * @param avatar - user avatar
     */
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    /**
     * @return user language
     */
    public Language getLanguage() {
        return language;
    }

    /**
     * @param language of user
     */
    public void setLanguage(Language language) {
        this.language = language;
    }

    /**
     * @return user page size
     */
    public int getPageSize() {
        return pageSize;
    }

    /**
     * @param pageSize user page size
     */
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    /**
     * Returns all the languages available for the user
     * to choose from.
     *
     * @return array of languages for user to choose
     */
    public Language[] getLanguagesAvailable() {
        return Language.values();
    }

    /**
     * Get user's name(login).
     *
     * @return user's name(login)
     */
    public String getUsername() {
        return username;
    }

    /**
     * Set user's name(login).
     *
     * @param username user's name
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Get email.
     *
     * @return email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Set email.
     *
     * @param email email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Get first name.
     *
     * @return first name
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Set first name.
     *
     * @param firstName first name
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Get last name.
     *
     * @return last name
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Set last name.
     *
     * @param lastName last name
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * @return signature
     */
    public String getSignature() {
        return StringUtils.trimToNull(signature);
    }

    /**
     * @param signature user signature
     */
    public void setSignature(String signature) {
        this.signature = signature;
    }

    /**
     * @return user location
     */
    public String getLocation() {
        return location;
    }

    /**
     * @param location user location
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * Check - sending notification is allow
     *
     * @return if allow return true else false
     */
    public boolean isSendPmNotification() {
        return sendPmNotification;
    }

    /**
     * Set send notification or not
     *
     * @param sendPmNotification Send notification or not
     */
    public void setSendPmNotification(boolean sendPmNotification) {
        this.sendPmNotification = sendPmNotification;
    }
}
