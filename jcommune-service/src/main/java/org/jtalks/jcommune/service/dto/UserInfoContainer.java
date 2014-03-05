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
package org.jtalks.jcommune.service.dto;

/**
 * This class is used when transferring user profile updates
 * from web tier to the service layer. For various reasons
 * we can't use domain model class and MVC command object.
 *
 * @author Evgeniy Naumenko
 */
public class UserInfoContainer {

    private String firstName;
    private String lastName;
    private String email;
    private String signature;
    private String b64EncodedAvatar;
    private int pageSize;
    private String location;

    /**
     * Create instance with required fields.
     *
     * @param firstName       user's first name
     * @param lastName        user's last name
     * @param email           email set for user
     * @param signature       user's signature
     * @param avatar          B64 encoded avatar
     * @param pageSize        page size chosen
     * @param location        geographic user location
     */
    public UserInfoContainer(String firstName, String lastName, String email, String signature, String avatar,
                             int pageSize, String location) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.signature = signature;
        this.b64EncodedAvatar = avatar;
        this.pageSize = pageSize;
        this.location = location;
    }

    /**
     * Get the user's Last Name.
     *
     * @return the lastName
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * @return the firstName
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @return user b64EncodedAvatar
     */
    public String getB64EncodedAvatar() {
        return b64EncodedAvatar;
    }

    /**
     * @return user signature
     */
    public String getSignature() {
        return signature;
    }

    /**
     * @return user page size
     */
    public int getPageSize() {
        return pageSize;
    }

    /**
     * @return user location
     */
    public String getLocation() {
        return location;
    }
}
