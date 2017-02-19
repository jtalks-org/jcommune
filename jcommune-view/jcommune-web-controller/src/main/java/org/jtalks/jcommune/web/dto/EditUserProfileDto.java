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

    private long userId;
    private String username;
    private String avatar;
    private boolean defaultAvatarFlag;
    private Long userProfileVersion;

    @Valid
    private UserProfileDto userProfileDto;

    @Valid
    private UserNotificationsDto userNotificationsDto;

    @Valid
    private UserSecurityDto userSecurityDto;

    @Valid
    private UserContactsDto userContactsDto;

    public boolean isDefaultAvatarFlag() {
        return defaultAvatarFlag;
    }

    public void setDefaultAvatarFlag(Boolean defaultAvatarFlag) {
        this.defaultAvatarFlag = defaultAvatarFlag;
    }

    /**
     * Create instance with UserProfileDto. All required fields retrieved from JCUser.
     * @param userProfileDto dto for user profile fields
     * @param user edited user
     */
    public EditUserProfileDto(UserProfileDto userProfileDto, JCUser user) {
        this(user);
        this.userProfileDto = userProfileDto;
    }

    /**
     * Create instance with UserSecurityDto. All required fields retrieved from JCUser.
     * @param userSecurityDto dto for user security settings
     * @param user edited user
     */
    public EditUserProfileDto(UserSecurityDto userSecurityDto, JCUser user) {
        this(user);
        this.userSecurityDto = userSecurityDto;
    }

    /**
     * Create instance with UserNotificationsDto. All required fields retrieved from JCUser.
     * @param userNotificationsDto dto for user notification settings
     * @param user edited user
     */
    public EditUserProfileDto(UserNotificationsDto userNotificationsDto, JCUser user) {
        this(user);
        this.userNotificationsDto = userNotificationsDto;
    }

    /**
     * Create instance with UserContactsDto. All required fields retrieved from JCUser.
     * @param userContactsDto dto for user contacts settings
     * @param user edited user
     */
    public EditUserProfileDto(UserContactsDto userContactsDto, JCUser user) {
        this(user);
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
        this.userProfileVersion = user.getVersion();
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
                this.getAvatar(), dto.getPageSize(), dto.getLocation());
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
        return new UserNotificationsContainer(dto.isAutosubscribe(),
                dto.isMentioningNotificationsEnabled(), dto.isSendPmNotification());
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
        if (dto == null) {
            return contacts;
        }
        for (UserContactDto contact : dto.getContacts()) {
            contacts.add(new UserContactContainer(contact.getId(),
                    contact.getValue(), contact.getType().getId()));
        }
        return contacts;
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

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public void setUserProfileVersion(Long version) {
        this.userProfileVersion = version;
    }

    public Long getUserProfileVersion() {
        return userProfileVersion;
    }

    /**
     * @return dto with user profile fields
     */
    public UserProfileDto getUserProfileDto() {
        return userProfileDto;
    }

    /**
     * Set UserProfileDto.
     *
     * @param userProfileDto - user dto with profile fields
     */
    public void setUserProfileDto(UserProfileDto userProfileDto) {
        this.userProfileDto = userProfileDto;
    }

    /**
     * @return user dto with notification settings
     */
    public UserNotificationsDto getUserNotificationsDto() {
        return userNotificationsDto;
    }

    /**
     * Set UserNotificationsDto.
     *
     * @param userNotificationsDto - user dto with notification settings
     */
    public void setUserNotificationsDto(UserNotificationsDto userNotificationsDto) {
        this.userNotificationsDto = userNotificationsDto;
    }

    /**
     * @return user dto with security settings
     */
    public UserSecurityDto getUserSecurityDto() {
        return userSecurityDto;
    }

    /**
     * Set UserSecurityDto.
     *
     * @param userSecurityDto - user dto with security settings
     */
    public void setUserSecurityDto(UserSecurityDto userSecurityDto) {
        this.userSecurityDto = userSecurityDto;
    }

    /**
     * @return dto with user contacts
     */
    public UserContactsDto getUserContactsDto() {
        return userContactsDto;
    }

    /**
     * Set UserContactsDto.
     *
     * @param userContactsDto - dto with user contacts
     */
    public void setUserContactsDto(UserContactsDto userContactsDto) {
        this.userContactsDto = userContactsDto;
    }

}
