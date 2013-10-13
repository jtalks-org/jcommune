package org.jtalks.jcommune.model.plugins;

import org.jtalks.jcommune.model.dto.UserDto;
import org.jtalks.jcommune.model.plugins.exceptions.NoConnectionException;
import org.jtalks.jcommune.model.plugins.exceptions.UnexpectedErrorException;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

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


    String getHtml(HttpServletRequest request, String pluginId);
}
