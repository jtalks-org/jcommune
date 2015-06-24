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

import org.jtalks.jcommune.model.dto.RegisterUserDto;
import org.jtalks.jcommune.plugin.api.exceptions.NoConnectionException;
import org.jtalks.jcommune.plugin.api.exceptions.UnexpectedErrorException;
import org.jtalks.jcommune.service.util.AuthenticationStatus;
import org.springframework.validation.BindingResult;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jtalks.jcommune.model.dto.LoginUserDto;

/**
 * Serves for authentication and registration user.
 *
 * @author Andrey Pogorelov
 */
public interface Authenticator {

    /**
     * Authenticate user with specified credentials.
     *
     * @param loginUserDto DTO which represent information about user
     * @param request    HTTP request
     * @param response   HTTP response
     * @return AAUTHENTICATED if user was logged in. AUTHENTICATION_FAIL if there were any errors during
     *         logging in.
     * @throws UnexpectedErrorException if external service returns unexpected result
     * @throws NoConnectionException    if we can't connect for any reason to external authentication service
     */
    public AuthenticationStatus authenticate(LoginUserDto loginUserDto, HttpServletRequest request, HttpServletResponse response)
            throws UnexpectedErrorException, NoConnectionException;

    /**
     * Register user with given details.
     *
     * @param registerUserDto user details
     * @return errors occurred during registration
     * @throws UnexpectedErrorException if external service returns unexpected result
     * @throws NoConnectionException    if we can't connect for any reason to external authentication service
     */
    public BindingResult register(RegisterUserDto registerUserDto)
            throws UnexpectedErrorException, NoConnectionException;


}
