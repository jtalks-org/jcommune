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

package org.jtalks.jcommune.service.security;

/**
 * Contains constants specific to security.
 *
 * @author Kirill Afonin
 */
public final class SecurityConstants {
    /**
     * Username of anonymous user (not logged in user).
     * If you want to change it you should change it here and in security-context.xml
     */
    public static final String ANONYMOUS_USERNAME = "anonymousUser";
    /**
     * Role name of administrators.
     */
    public static final String ROLE_ADMIN = "ROLE_ADMIN";
    /**
     * Role name of user. Every registered user have this role by default.
     */
    public static final String ROLE_USER = "ROLE_USER";

    /**
     * You can't create instance of this class.
     */
    private SecurityConstants() {
    }
}
