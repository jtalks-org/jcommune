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

import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.model.entity.Language;
import org.jtalks.jcommune.service.dto.UserContactContainer;
import org.jtalks.jcommune.service.dto.UserInfoContainer;
import org.jtalks.jcommune.service.dto.UserNotificationsContainer;
import org.jtalks.jcommune.service.dto.UserSecurityContainer;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * This dto used for transferring data in edit {@link org.jtalks.jcommune.model.entity.JCUser} profile operation.
 * To get more info see
 * {@link org.jtalks.jcommune.web.controller.UserProfileController#saveEditedProfile(EditUserProfileDto,
 * org.springframework.validation.BindingResult, javax.servlet.http.HttpServletResponse)}.
 *
 * @author Osadchuck Eugeny
 * @author Andrey Pogorelov
 */
public class EditUserProfileDto {

    public static final String SECURITY = "security";
    public static final String PROFILE = "profile";
    public static final String NOTIFICATIONS = "notifications";
    public static final String CONTACTS = "contacts";

    private long userId;
    private String username;
    private String avatar;

    @Valid
    UserProfileDto userProfileDto;

    @Valid
    UserNotificationsDto userNotificationsDto;

    @Valid
    UserSecurityDto userSecurityDto;

    @Valid
    UserContactsDto userContactsDto;

    public EditUserProfileDto(UserProfileDto userProfileDto, JCUser user) {
        this.userId = user.getId();
        this.username = user.getUsername();
        this.userProfileDto = userProfileDto;
    }

    public EditUserProfileDto(UserSecurityDto userSecurityDto, JCUser user) {
        this.userId = user.getId();
        this.username = user.getUsername();
        this.userSecurityDto = userSecurityDto;
    }

    public EditUserProfileDto(UserNotificationsDto userNotificationsDto, JCUser user) {
        this.userId = user.getId();
        this.username = user.getUsername();
        this.userNotificationsDto = userNotificationsDto;
    }

    public EditUserProfileDto(UserContactsDto userContactsDto, JCUser user) {
        this.userId = user.getId();
        this.username = user.getUsername();
        this.userContactsDto = userContactsDto;
    }

    public UserContactsDto getUserContactsDto() {
        return userContactsDto;
    }

    public void setUserContactsDto(UserContactsDto userContactsDto) {
        this.userContactsDto = userContactsDto;
    }

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
    }

    /**
     * Transforms DTO into container object - convenience implementation to
     * be passed to the service layer.
     *
     * @return user profile modification info for the service tier
     */
    public UserInfoContainer getUserInfoContainer() {
        UserProfileDto dto = this.getUserProfileDto();
        return new UserInfoContainer(dto.getFirstName(), dto.getLastName(), dto.getEmail(), dto.getSignature(),
                this.getAvatar(), dto.getPageSize(), dto.isAutosubscribe(), dto.getLocation());
    }

    /**
     * Transforms DTO security info container object - convenience implementation to
     * be passed to the service layer.
     *
     * @return user profile modification info for the service tier
     */
    public UserSecurityContainer getUserSecurityContainer() {
        UserSecurityDto dto = this.getUserSecurityDto();
        return new UserSecurityContainer(dto.getCurrentUserPassword(), dto.getNewUserPassword());
    }

    /**
     * Transforms DTO notification settings container object - convenience implementation to
     * be passed to the service layer.
     *
     * @return user profile modification info for the service tier
     */
    public UserNotificationsContainer getUserNotificationsContainer() {
        UserNotificationsDto dto = this.getUserNotificationsDto();
        return new UserNotificationsContainer(dto.isMentioningNotificationsEnabled(), dto.isSendPmNotification());
    }

    /**
     * Transforms DTO user contacts container object - convenience implementation to
     * be passed to the service layer.
     *
     * @return user profile modification info for the service tier
     */
    public List<UserContactContainer> getUserContacts() {
        UserContactsDto dto = this.getUserContactsDto();
        List<UserContactContainer> contacts = new ArrayList<>();
        for (UserContactDto contact : dto.getContacts()) {
            contacts.add(new UserContactContainer(contact.getId(),
                    contact.getValue(), contact.getType().getId()));
        }
        return contacts;
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

    public UserProfileDto getUserProfileDto() {
        return userProfileDto;
    }

    public void setUserProfileDto(UserProfileDto userProfileDto) {
        this.userProfileDto = userProfileDto;
    }

    public UserNotificationsDto getUserNotificationsDto() {
        return userNotificationsDto;
    }

    public void setUserNotificationsDto(UserNotificationsDto userNotificationsDto) {
        this.userNotificationsDto = userNotificationsDto;
    }

    public UserSecurityDto getUserSecurityDto() {
        return userSecurityDto;
    }

    public void setUserSecurityDto(UserSecurityDto userSecurityDto) {
        this.userSecurityDto = userSecurityDto;
    }

}
