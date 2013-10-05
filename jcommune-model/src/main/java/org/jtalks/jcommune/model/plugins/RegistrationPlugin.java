package org.jtalks.jcommune.model.plugins;

import org.jtalks.jcommune.model.dto.UserDto;
import org.jtalks.jcommune.model.plugins.exceptions.NoConnectionException;
import org.jtalks.jcommune.model.plugins.exceptions.UnexpectedErrorException;

import java.util.Map;

public interface RegistrationPlugin extends Plugin {
    /**
     * Performs registration attempt based on user details
     *
     *
     * @param userDto user
     * @return validation errors as pairs field - error message
     * @throws org.jtalks.jcommune.model.plugins.exceptions.UnexpectedErrorException if external service returns unexpected result
     * @throws org.jtalks.jcommune.model.plugins.exceptions.NoConnectionException    if we can't connect for any reason to external authentication service
     */
    Map<String, String> registerUser(UserDto userDto) throws NoConnectionException, UnexpectedErrorException;

    /**
     * Performs validation based on user details
     * @param userDto user information
     * @return validation errors as pairs field - error message
     * @throws UnexpectedErrorException if external service returns unexpected result
     * @throws NoConnectionException    if we can't connect for any reason to external authentication service
     */
    Map<String, String> validateUser(UserDto userDto) throws NoConnectionException, UnexpectedErrorException;

    String getHtml();
}
