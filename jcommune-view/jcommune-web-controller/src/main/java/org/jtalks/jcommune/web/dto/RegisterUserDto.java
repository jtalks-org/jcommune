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

import org.hibernate.validator.constraints.NotBlank;
import org.jtalks.common.model.entity.User;
import org.jtalks.common.validation.annotations.Email;
import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.web.validation.annotations.Captcha;
import org.jtalks.jcommune.web.validation.annotations.Matches;
import org.jtalks.jcommune.web.validation.annotations.Unique;

import javax.validation.constraints.Size;

/**
 * DTO for {@link org.jtalks.jcommune.model.entity.JCUser} object. Required for validation and binding
 * errors to form. This dto used for register user operation
 * {@link org.jtalks.jcommune.web.controller.UserController#registerUser}.
 *
 * @author Osadchuck Eugeny
 */
@Matches(field = "password", verifyField = "passwordConfirm", message = "{password_not_matches}")
public class RegisterUserDto {

    @NotBlank(message = "{validation.not_null}")
    @Size(min = User.USERNAME_MIN_LENGTH, max = User.USERNAME_MAX_LENGTH, message = "{validation.username.length}")
    @Unique(entity = JCUser.class, field = "username", message = "{validation.duplicateuser}", ignoreCase = true)
    private String username;

    @NotBlank(message = "{validation.not_null}")
    @Size(max = User.EMAIL_MAX_LENGTH, message = "{validation.email.length}")
    @Email(message = "{validation.email.wrong.format}")
    @Unique(entity = JCUser.class, field = "email", message = "{validation.duplicateemail}", ignoreCase = true)
    private String email;

    @NotBlank(message = "{validation.not_null}")
    @Size(min = User.PASSWORD_MIN_LENGTH, max = User.PASSWORD_MAX_LENGTH)
    private String password;

    private String passwordConfirm;

    @Captcha(message = "{validation.captcha.wrong}")
    private String captcha;

    /**
     * @return username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Set username.
     *
     * @param username username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Get password.
     *
     * @return password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Set password.
     *
     * @param password password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Get password confirmation.
     *
     * @return password confirmation
     */
    public String getPasswordConfirm() {
        return passwordConfirm;
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
     * Set password confirmation.
     *
     * @param passwordConfirm password confirmation
     */
    public void setPasswordConfirm(String passwordConfirm) {
        this.passwordConfirm = passwordConfirm;
    }

    /**
     * @return Captcha value entered by user on a registration form
     */
    public String getCaptcha() {
        return captcha;
    }

    /**
     * @param captcha captcha string from user input during registration
     */
    public void setCaptcha(String captcha) {
        this.captcha = captcha;
    }

    /**
     * Populate {@link JCUser} from fields.
     *
     * @return populated {@link JCUser} object
     */
    public JCUser createUser() {
        return new JCUser(username, email, password);
    }
}
