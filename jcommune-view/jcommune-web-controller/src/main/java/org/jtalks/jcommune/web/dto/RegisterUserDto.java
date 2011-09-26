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
import org.jtalks.jcommune.model.entity.User;
import org.jtalks.jcommune.web.validation.Matches;

import javax.validation.constraints.Size;

/**
 * DTO for {@link User} object. Required for validation and binding
 * errors to form. This dto used for register user operation {@link UserController#registerUser}.
 *
 * @author Osadchuck Eugeny
 */
@Matches(field = "password", verifyField = "passwordConfirm", message = "{password_not_matches}")
public class RegisterUserDto extends UserDto {

    @NotBlank(message = "{validation.username.notblank}")
    @Size(min = 3, max = 20, message = "{validation.username.length}")
    private String username;
    @NotBlank(message = "{validation.password.notblank}")
    @Size(min = 4, max = 20)
    private String password;
    @NotBlank(message = "{validation.password.confirm.notblank}")
    private String passwordConfirm;

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
     * Set password confirmation.
     *
     * @param passwordConfirm password confirmation
     */
    public void setPasswordConfirm(String passwordConfirm) {
        this.passwordConfirm = passwordConfirm;
    }

    /**
     * Populate {@link User} from fields.
     *
     * @return populated {@link User} object
     */
    public User createUser() {
        User newUser = new User(username, getEmail(), password);
        newUser.setFirstName(getFirstName());
        newUser.setLastName(getLastName());
        return newUser;
    }
}
