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

package org.jtalks.jcommune.model.plugins;

import org.jtalks.jcommune.model.dto.UserDto;
import org.jtalks.jcommune.model.plugins.exceptions.NoConnectionException;
import org.jtalks.jcommune.model.plugins.exceptions.UnexpectedErrorException;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;
import java.util.Map;

/**
 * Provides registration capabilities: validation and registration.
 * Also supports getting any html for registration form.
 *
 * @author Andrey Pogorelov
 */
public interface RegistrationPlugin extends Plugin {

    /**
     * Performs registration attempt based on user details
     *
     * @param userDto user
     * @param pluginId plugin id
     * @return validation errors as pairs field - error message
     * @throws UnexpectedErrorException if external service returns unexpected result
     * @throws NoConnectionException    if we can't connect for any reason to external authentication service
     */
    Map<String, String> registerUser(UserDto userDto, Long pluginId) throws NoConnectionException, UnexpectedErrorException;

    /**
     * Performs validation based on user details
     *
     * @param userDto user information
     * @param pluginId plugin id
     * @return validation errors as pairs field - error message
     * @throws UnexpectedErrorException if external service returns unexpected result
     * @throws NoConnectionException    if we can't connect for any reason to external authentication service
     */
    Map<String, String> validateUser(UserDto userDto, Long pluginId) throws NoConnectionException, UnexpectedErrorException;

    /**
     * Get provided by plugin any html for the registration form.
     *
     * @param request http request
     * @param pluginId plugin id
     * @param locale user locale
     * @return html or null if plugin doesn't want to provide any html
     */
    String getHtml(HttpServletRequest request, String pluginId, Locale locale);
}
