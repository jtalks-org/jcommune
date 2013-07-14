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

package org.jtalks.jcommune.plugin.registration.poulpe.service;

import org.jtalks.jcommune.plugin.registration.poulpe.exceptions.NoConnectionException;
import org.jtalks.poulpe.web.controller.rest.pojo.Errors;

import javax.xml.bind.JAXBException;
import java.io.IOException;

/**
 * Provides user registration via 3rd party system.
 *
 * @author Andrey Pogorelov
 */
public interface RegistrationService {

    /**
     * Register user with specified data via Poulpe.
     * Returns errors if request failed, otherwise return null.
     *
     * @param username username
     * @param password password
     * @param email email
     * @return errors
     */
    Errors registerUser(String username, String password, String email)
            throws IOException, NoConnectionException, JAXBException;

}
