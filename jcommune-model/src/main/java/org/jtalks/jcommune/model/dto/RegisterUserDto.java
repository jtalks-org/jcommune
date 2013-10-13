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
package org.jtalks.jcommune.model.dto;

import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.model.validation.annotations.Matches;

import javax.validation.Valid;

/**
 * DTO for {@link org.jtalks.jcommune.model.entity.JCUser} object. Required for validation and binding
 * errors to form. This dto used for register user operation
 *
 * @author Osadchuck Eugeny
 */
@Matches(field = "userDto.password", verifyField = "passwordConfirm", message = "{password_not_matches}")
public class RegisterUserDto {

    @Valid
    private UserDto userDto;

    private String passwordConfirm;

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
     * Populate {@link JCUser} from fields.
     *
     * @return populated {@link JCUser} object
     */
    public JCUser createUser() {
        return new JCUser(userDto.getUsername(), userDto.getEmail(), userDto.getPassword());
    }

    /**
     * Get UserDto.
     *
     * @return userDto
     */
    public UserDto getUserDto() {
        return userDto;
    }

    /**
     * Set UserDto.
     *
     * @param userDto userDto
     */
    public void setUserDto(UserDto userDto) {
        this.userDto = userDto;
    }
}
