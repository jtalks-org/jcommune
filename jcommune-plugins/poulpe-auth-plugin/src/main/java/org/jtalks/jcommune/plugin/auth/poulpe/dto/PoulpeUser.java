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
package org.jtalks.jcommune.plugin.auth.poulpe.dto;

import org.hibernate.validator.constraints.Length;
import org.jtalks.common.model.entity.Group;


/**
 * Stores information about the user.
 */
public class PoulpeUser extends org.jtalks.common.model.entity.User {

    /**
     * Creates an empty and <i>not valid</i> instance without required fields, use {@link #PoulpeUser(String, String,
     * String, String)} instead. This constructor is usually used by Hibernate.
     */
    public PoulpeUser() {
        super();
    }

    /**
     * Create instance with all the mandatory fields fields.
     *
     * @param username username
     * @param email    email
     * @param password password
     * @param salt     a security salt that is used for encrypting the passwords to be less vulnerable for decryption of
     *                 password from its hash, more info can be found
     *                 <a href="http://en.wikipedia.org/wiki/Salt_(cryptography)">here</a>.
     */
    public PoulpeUser(String username, String email, String password, String salt) {
        super(username, email, password, salt);
    }

    /**
     * Defines whether user resides in a group with the specified ID.
     *
     * @param groupId an ID of the group to find from the list of group user is in
     * @return true if user is a member of the group with specified id
     */
    public boolean isInGroupWithId(long groupId) {
        for (Group userGroup : getGroups()) {
            if (userGroup.getId() == groupId) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns an empty user with invalid mail, username and password with ID being set.
     *
     * @param id an id to set to the user
     * @return an empty user with invalid mail, username and password with ID being set
     */
    public static PoulpeUser withId(long id) {
        PoulpeUser user = new PoulpeUser("", "", "", "");
        user.setId(id);
        return user;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Length(max = EMAIL_MAX_LENGTH, message = "{user.email.length_constraint_violation}")
    public String getEmail() {
        return super.getEmail();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "PoulpeUser [id=" + getId() + ", email=" + getEmail() + ", username=" + getUsername() + "]";
    }

}
