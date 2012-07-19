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
import org.jtalks.jcommune.web.validation.annotations.Exists;

/**
 * Spring MVC command object for password restore form processing.
 * todo: add capcha support for this operation
 *
 * @author Evgeniy Naumenko
 */
public class RestorePasswordDto {

    /** This name is used to avoid collision with 'email' field at signup popup */
    @Exists(entity = JCUser.class, field = "email", message = "{email.unknown}")
    private String userEmail;

    /**
     * @return email set on the web page
     */
    public String getUserEmail() {
        return userEmail;
    }

    /**
     * @param email email set on the web page
     */
    public void setUserEmail(String email) {
        this.userEmail = email;
    }
}
