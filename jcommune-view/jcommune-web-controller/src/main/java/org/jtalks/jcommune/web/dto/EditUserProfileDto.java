/**
 * Copyright (C) 2011  jtalks.org Team
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
 * Also add information on how to contact you by electronic and paper mail.
 * Creation date: Apr 12, 2011 / 8:05:19 PM
 * The jtalks.org Project
 */
package org.jtalks.jcommune.web.dto;

import org.hibernate.validator.constraints.Length;
import org.jtalks.jcommune.model.entity.User;
import org.jtalks.jcommune.web.validation.Avatar;
import org.jtalks.jcommune.web.validation.Matches;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

/**
 * This dto used for transferring data in edit {@link User} profile operation.
 * To get more info see {@link UserController#editProfile}.
 *
 * @author Osadchuck Eugeny
 */
@Matches(field = "newUserPassword", verifyField = "newUserPasswordConfirm", message = "{password_not_matches}")
public class EditUserProfileDto extends UserDto {

    private String currentUserPassword;

    @Length(min = 4, max = 20)
    private String newUserPassword;
    private String newUserPasswordConfirm;
    @Avatar
    private MultipartFile avatar;

    /**
     * Default constructor
     */
    public EditUserProfileDto() {
        super();
    }

    /**
     * Constructor which fills dto fields from user.
     * Fields {@link User#getFirstName()}, {@link User#getLastName()}, {@link User#getEmail(), {@link User#getAvatar()} } will be copied.
     *
     * @param user - copying source
     */
    public EditUserProfileDto(User user) {
        this.setFirstName(user.getFirstName());
        this.setLastName(user.getLastName());
        this.setEmail(user.getEmail());
        this.setAvatar(new MockMultipartFile("avatar", user.getAvatar()));
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

    public MultipartFile getAvatar() {
        return avatar;
    }

    public void setAvatar(MultipartFile avatar) {
        this.avatar = avatar;
    }

}
