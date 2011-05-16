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

import org.hibernate.validator.constraints.Email;
//import org.hibernate.validator.constraints.NotBlank;
import org.jtalks.jcommune.web.validation.Matches;

import javax.validation.constraints.Size;

/**
 * DTO for {@link User} object. Required for validation and binding
 * errors to form.
 *
 * @author Kirill Afonin
 * @see User
 */
@Matches(field = "password", verifyField = "passwordConfirm", message = "Password not matches!")
public class UserDTO {
    //@NotBlank
    @Size(min = 3, max = 20)
    private String username;
   // @NotBlank
    @Email
    private String email;
    private String firstName;
    private String lastName;
    //@NotBlank
    @Size(min = 4)
    private String password;
   // @NotBlank
    private String passwordConfirm;

    /**
     *
     * @return username
     */
    public String getUsername() {
        return username;
    }

    /**
     *
     * @param username username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     *
     * @return email
     */
    public String getEmail() {
        return email;
    }

    /**
     *
     * @param email email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     *
     * @return first name
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     *
     * @param firstName  first name
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     *
     * @return last name
     */
    public String getLastName() {
        return lastName;
    }

    /**
     *
     * @param lastName last name
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     *
     * @return password
     */
    public String getPassword() {
        return password;
    }

    /**
     *
     * @param password password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     *
     * @return password confirmation
     */
    public String getPasswordConfirm() {
        return passwordConfirm;
    }

    /**
     *
     * @param passwordConfirm  password confirmation
     */
    public void setPasswordConfirm(String passwordConfirm) {
        this.passwordConfirm = passwordConfirm;
    }
}
