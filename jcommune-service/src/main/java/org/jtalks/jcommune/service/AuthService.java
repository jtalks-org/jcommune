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
package org.jtalks.jcommune.service;

import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.model.plugins.exceptions.NoConnectionException;
import org.jtalks.jcommune.model.plugins.exceptions.UnexpectedErrorException;

/**
 * Serves to authenticate users with some available authentication plugin.
 * todo It is temporary solution. We need some uniform solution for registration and authentication.
 *
 * @author Andrey Pogorelov
 */
public interface AuthService  extends EntityService<JCUser> {

    /**
     * Authenticate user with specified parameters by some available plugin.
     *
     * @param username username
     * @param passwordHash user password hash
     * @param newUser if user is new for JCommune (not exist in internal database)
     * @return authenticated user
     *
     * @throws UnexpectedErrorException if some unexpected error occurred
     * @throws NoConnectionException if some connection error occurred
     */
    JCUser pluginAuthenticate(String username, String passwordHash, boolean newUser)
            throws UnexpectedErrorException, NoConnectionException;
}
