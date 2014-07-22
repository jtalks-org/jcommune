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
package org.jtalks.jcommune.plugin.api.core;

import org.jtalks.jcommune.plugin.api.exceptions.NoConnectionException;
import org.jtalks.jcommune.plugin.api.exceptions.UnexpectedErrorException;

import java.util.Map;

/**
 * Interface for plugins providing basic authentication capabilities
 * based on the login/password pair.
 * <p/>
 * todo: create more general interface for full featured authentication:
 * kerberos, x.509, OTP, etc. Spring Security API may serve as an example. Robust
 * authentication interface should also provide some means to return different
 * operation outcomes and messages/bundle codes.
 *
 * @author Evgeny Naumenko
 * @author Andrey Pogorelov
 */
public interface AuthenticationPlugin extends Plugin {

    /**
     * Performs authentication attempt based on login/password pair
     *
     * @param login    user login
     * @param password user password
     * @return user details
     * @throws UnexpectedErrorException if external service returns unexpected result
     * @throws NoConnectionException    if we can't connect for any reason to external authentication service
     */
    Map<String, String> authenticate(String login, String password)
            throws UnexpectedErrorException, NoConnectionException;
}
