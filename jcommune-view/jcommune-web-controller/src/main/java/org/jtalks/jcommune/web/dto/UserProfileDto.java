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
import org.joda.time.DateTime;
import org.jtalks.common.model.entity.User;
import org.jtalks.common.validation.annotations.Email;
import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.model.entity.Language;
import org.jtalks.jcommune.web.validation.annotations.BbCodeNesting;
import org.jtalks.jcommune.web.validation.annotations.ChangedEmail;
import org.jtalks.jcommune.web.validation.annotations.PageSize;

import javax.validation.constraints.Size;

/**
 * This dto used for transferring data in edit {@link org.jtalks.jcommune.model.entity.JCUser} profile operation.
 * To get more info see
 * {@link org.jtalks.jcommune.web.controller.UserProfileController#saveEditedProfile(EditUserProfileDto,
 * org.springframework.validation.BindingResult, javax.servlet.http.HttpServletResponse)}.
 *
 * @author Osadchuck Eugeny
 * @author Andrey Pogorelov
 */
@ChangedEmail
public class UserProfileDto {
    private long userId;

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
    @PageSize(message = "{validation.profile.page.size}")
    private int pageSize;
    private boolean autosubscribe;

    @Length(max = JCUser.MAX_LOCATION_SIZE)
    private String location;
    private int postCount;
    private DateTime lastLogin;
    private DateTime registrationDate;

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
    public UserProfileDto() {
    }

    /**
     * Constructor which fills dto fields from user.
     *
     * @param user copying source
     */
    public UserProfileDto(JCUser user) {
        this.userId = user.getId();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.email = user.getEmail();
        this.signature = user.getSignature();
        this.pageSize = user.getPageSize();
        this.autosubscribe = user.isAutosubscribe();
        this.location = user.getLocation();
        this.postCount = user.getPostCount();
        this.registrationDate = user.getRegistrationDate();
        this.lastLogin = user.getLastLogin();
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
     * @see org.jtalks.jcommune.model.entity.JCUser#isAutosubscribe()
     */
    public boolean isAutosubscribe() {
        return autosubscribe;
    }

    /**
     * @see org.jtalks.jcommune.model.entity.JCUser#setAutosubscribe(boolean)
     */
    public void setAutosubscribe(boolean autosubscribe) {
        this.autosubscribe = autosubscribe;
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

    public void setPostCount(int postCount) {
        this.postCount = postCount;
    }

    public int getPostCount() {
        return postCount;
    }

    public void setLastLogin(DateTime lastLogin) {
        this.lastLogin = lastLogin;
    }

    public DateTime getLastLogin() {
        return lastLogin;
    }

    public void setRegistrationDate(DateTime registrationDate) {
        this.registrationDate = registrationDate;
    }

    public DateTime getRegistrationDate() {
        return registrationDate;
    }
}
