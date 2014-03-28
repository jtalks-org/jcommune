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
 * This class is used when transferring user security updates
 * from web tier to the service layer. For various reasons
 * we can't use domain model class and MVC command object.
 * Dto's marked with validation annotations located in controller layer,
 * and in some cases we need another set of fields.
 *
 * @author Andrey Pogorelov
 */
public class UserSecurityContainer {

    private String currentPassword;
    private String newPassword;

    /**
     * Create instance with required fields.
     *
     * @param currentPassword current user password to verify identity, may be null is we're not changing password
     * @param newPassword     new password to be set, may be null is we're not changing password
     */
    public UserSecurityContainer(String currentPassword, String newPassword) {
        this.currentPassword = currentPassword;
        this.newPassword = newPassword;
    }

    /**
     * @return password
     */
    public String getCurrentPassword() {
        return currentPassword;
    }

    /**
     * @return new password set during profile updates
     */
    public String getNewPassword() {
        return newPassword;
    }

}
