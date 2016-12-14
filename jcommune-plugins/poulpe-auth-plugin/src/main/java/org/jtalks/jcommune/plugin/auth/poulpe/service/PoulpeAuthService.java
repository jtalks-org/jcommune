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

package org.jtalks.jcommune.plugin.auth.poulpe.service;

import org.apache.commons.lang3.StringUtils;
import org.jtalks.jcommune.model.dto.UserDto;
import org.jtalks.jcommune.plugin.api.exceptions.NoConnectionException;
import org.jtalks.jcommune.plugin.api.exceptions.UnexpectedErrorException;
import org.jtalks.jcommune.plugin.auth.poulpe.dto.Authentication;
import org.jtalks.jcommune.plugin.auth.poulpe.dto.Errors;
import org.jtalks.jcommune.plugin.auth.poulpe.dto.User;
import org.restlet.Context;
import org.restlet.data.ChallengeScheme;
import org.restlet.data.Status;
import org.restlet.engine.header.Header;
import org.restlet.engine.header.HeaderConstants;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;
import org.restlet.util.Series;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentMap;

/**
 * This class contains method needed for communicate with Poulpe rest service.
 *
 * @author Andrey Pogorelov
 */
public class PoulpeAuthService {

    private static final int CONNECTION_TIMEOUT = 5000;

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private String regUrl;
    private String authUrl;
    private String activationUrl;
    private String login;
    private String password;

    public PoulpeAuthService(String url, String login, String password) {
        this.regUrl = url + "/rest/private/user";
        this.activationUrl = url + "/rest/private/activate";
        this.authUrl = url + "/rest/authenticate";
        this.login = login;
        this.password = password;
    }

    /**
     * Register user with specified data via Poulpe.
     * Returns errors if request failed, otherwise return null.
     *
     * @param userDto user
     * @param dryRun  do not register the user, just check if it is possible
     * @return errors
     */
    public Map<String, String> registerUser(UserDto userDto, Boolean dryRun)
            throws IOException, NoConnectionException, JAXBException, UnexpectedErrorException {
        User user = createUser(userDto.getUsername(), userDto.getPassword(), userDto.getEmail());
        ClientResource clientResource = sendRegistrationRequest(user, dryRun);
        Map<String, String> result = getRegistrationResult(clientResource, userDto.getLanguage().getLocale());
        closeRestletConnection(clientResource);
        return result;
    }

    /**
     * Authenticate user with specified data via Poulpe.
     * Returns true if auth success, otherwise return false.
     *
     * @param username     username
     * @param passwordHash password hash
     * @return map with user details
     */
    public Map<String, String> authenticate(String username, String passwordHash)
            throws JAXBException, IOException, NoConnectionException {
        ClientResource clientResource = sendAuthRequest(username, passwordHash);
        Map<String, String> result = getAuthResult(clientResource);
        closeRestletConnection(clientResource);
        return result;
    }

    /**
     * Activate user with specified username in Poulpe.
     * @param username username
     */
    public void activate(String username) {
        ClientResource clientResource = null;
        try {
            clientResource = sendActivationRequest(username);
        } finally {
            if (clientResource != null) closeRestletConnection(clientResource);
        }
    }

    private void closeRestletConnection(ClientResource clientResource) {
        try {
            clientResource.getResponseEntity().exhaust();
        } catch (IOException e) {
            logger.warn("Error closing connection: {}", e.getMessage() );
        }
    }

    /**
     * Gets authentication result from response entity.
     *
     * @param clientResource response container
     * @return map with user details
     * @throws org.jtalks.jcommune.plugin.api.exceptions.NoConnectionException
     *
     */
    private Map<String, String> getAuthResult(ClientResource clientResource)
            throws NoConnectionException, JAXBException, IOException {
        if (clientResource.getStatus().getCode() == Status.SUCCESS_OK.getCode()
                && clientResource.getResponseEntity() != null) {
            return parseUserDetails(clientResource.getResponseEntity());
        } else if (clientResource.getStatus().getCode() == Status.CLIENT_ERROR_NOT_FOUND.getCode()) {
            return Collections.emptyMap();
        } else {
            throw new NoConnectionException(clientResource.getStatus().toString());
        }
    }

    private Map<String, String> parseUserDetails(Representation repr) throws JAXBException, IOException {
        JAXBContext context = JAXBContext.newInstance(Authentication.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        Authentication auth = (Authentication) unmarshaller.unmarshal(repr.getStream());

        Map<String, String> authInfo = new HashMap<>();
        authInfo.put("username", auth.getCredintals().getUsername());
        authInfo.put("email", auth.getProfile().getEmail());
        authInfo.put("firstName", auth.getProfile().getFirstName());
        authInfo.put("lastName", auth.getProfile().getLastName());
        authInfo.put("enabled", String.valueOf(auth.getProfile().isEnabled()));
        return authInfo;
    }

    /**
     * Gets errors from response if request wasn't successful, otherwise return null.
     *
     * @param clientResource response container
     * @param locale         locale
     * @return errors
     * @throws org.jtalks.jcommune.plugin.api.exceptions.NoConnectionException
     *
     * @throws java.io.IOException
     */
    private Map<String, String> getRegistrationResult(ClientResource clientResource, Locale locale)
            throws NoConnectionException, IOException, JAXBException, UnexpectedErrorException {
        if (clientResource.getStatus().getCode() == Status.SUCCESS_OK.getCode()
                && clientResource.getResponseEntity() != null) {
            return Collections.emptyMap();
        } else if (clientResource.getStatus().getCode() == Status.CLIENT_ERROR_BAD_REQUEST.getCode()) {
            return parseErrors(clientResource.getResponseEntity(), locale);
        } else if (clientResource.getStatus().getCode() == Status.SERVER_ERROR_INTERNAL.getCode()) {
            throw new UnexpectedErrorException();
        } else {
            throw new NoConnectionException(clientResource.getStatus().toString());
        }
    }

    /**
     * Parse bad response representation for errors.
     *
     * @param repr   response representation
     * @param locale locale
     * @return errors
     * @throws java.io.IOException
     */
    private Map<String, String> parseErrors(Representation repr, Locale locale) throws IOException, JAXBException {
        JAXBContext context = JAXBContext.newInstance(Errors.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        Errors errorsRepr = (Errors) unmarshaller.unmarshal(repr.getStream());

        Map<String, String> errors = new HashMap<>();
        ResourceBundle resourceBundle = ResourceBundle.getBundle("ValidationMessages", locale);
        for (org.jtalks.jcommune.plugin.auth.poulpe.dto.Error error : errorsRepr.getErrorList()) {
            if (error.getCode() != null && !error.getCode().isEmpty()) {
                Map.Entry<String, String> errorEntry = parseErrorCode(error.getCode(), resourceBundle);
                if (errorEntry != null) {
                    errors.put(errorEntry.getKey(), errorEntry.getValue());
                }
            }
        }
        return errors;
    }

    /**
     * Parse error code with specified {@link ResourceBundle}.
     *
     * @param errorCode      error code
     * @param resourceBundle used {@link ResourceBundle}
     * @return parsed error as pair field - error message
     */
    private Map.Entry<String, String> parseErrorCode(String errorCode, ResourceBundle resourceBundle) {
        Map.Entry<String, String> error = null;
        if (resourceBundle.containsKey(errorCode)) {
            String errorMessage = resourceBundle.getString(errorCode);
            if (errorCode.contains("email")) {
                errorMessage = errorMessage
                        .replace("{max}", String.valueOf(org.jtalks.common.model.entity.User.EMAIL_MAX_LENGTH));
                error = new HashMap.SimpleEntry<>("userDto.email", errorMessage);
            } else if (errorCode.contains("username")) {
                errorMessage = errorMessage
                        .replace("{min}", String.valueOf(org.jtalks.common.model.entity.User.USERNAME_MIN_LENGTH))
                        .replace("{max}", String.valueOf(org.jtalks.common.model.entity.User.USERNAME_MAX_LENGTH));
                error = new HashMap.SimpleEntry<>("userDto.username", errorMessage);
            } else if (errorCode.contains("password")) {
                errorMessage = errorMessage
                        .replace("{min}", String.valueOf(org.jtalks.common.model.entity.User.PASSWORD_MIN_LENGTH))
                        .replace("{max}", String.valueOf(org.jtalks.common.model.entity.User.PASSWORD_MAX_LENGTH));
                error = new HashMap.SimpleEntry<>("userDto.password", errorMessage);
            }
        }
        return error;
    }

    /**
     * Creates user entity by specified username, password and email.
     *
     * @param username     username
     * @param passwordHash password hash
     * @param email        user email
     * @return user entity
     */
    private User createUser(String username, String passwordHash, String email) {
        User user = new User();
        user.setUsername(username == null ? "" : username);
        user.setEmail(email == null ? "" : email);
        user.setPasswordHash(passwordHash);
        return user;
    }

    private void addHeaderAttribute(ClientResource clientResource, String attrName, String attrValue) {
        ConcurrentMap<String, Object> attrs = clientResource.getRequest().getAttributes();
        Series<Header> headers = (Series<Header>) attrs.get(HeaderConstants.ATTRIBUTE_HEADERS);
        if (headers == null) {
            headers = new Series<>(Header.class);
            Series<Header> prev = (Series<Header>) attrs.putIfAbsent(HeaderConstants.ATTRIBUTE_HEADERS, headers);
            if (prev != null) {
                headers = prev;
            }
        }
        headers.add(attrName, attrValue);
    }

    /**
     * Sends registration post request.
     *
     * @param user   user entity for registration
     * @param dryRun do not register the user, just check if it is possible
     * @return ClientResource result
     */
    protected ClientResource sendRegistrationRequest(User user, Boolean dryRun) {
        ClientResource clientResource = createClientResource(regUrl, true);
        addCredentialsIfAny(clientResource);
        if (dryRun) {
            addHeaderAttribute(clientResource, "dryRun", "true");
        }
        writeRequestInfoToLog(clientResource);
        try {
            clientResource.post(user);
        } catch (ResourceException e) {
            logger.warn("Poulpe registration request error: {}", e.getStatus());
        }
        return clientResource;
    }

    /**
     * Sends registration post request.
     *
     * @param username     user name
     * @param passwordHash password hash
     * @return ClientResource result
     */
    protected ClientResource sendAuthRequest(String username, String passwordHash) {
        String url = authUrl + "?username=" + username + "&passwordHash=" + passwordHash;
        ClientResource clientResource = createClientResource(url, false);
        addCredentialsIfAny(clientResource);
        writeRequestInfoToLog(clientResource);
        try {
            clientResource.get();
        } catch (ResourceException e) {
            logger.warn("Poulpe authentication request error: {}", e.getStatus());
        }
        return clientResource;
    }

    /**
     * Send user activation request for specified username
     * @param username username
     * @return ClientResource result
     */
    protected ClientResource sendActivationRequest(String username){
        String url = activationUrl + "?username=" + username;
        ClientResource clientResource = createClientResource(url, false);
        addCredentialsIfAny(clientResource);
        writeRequestInfoToLog(clientResource);
        try {
            clientResource.get();
        } catch (ResourceException e) {
            logger.warn("Poulpe activation request error: {}", e.getStatus());
        }
        return clientResource;
    }

    @SuppressWarnings("unchecked")
    private void writeRequestInfoToLog(ClientResource clientResource) {
        ConcurrentMap<String, Object> attrs = clientResource.getRequest().getAttributes();
        Series<Header> headers = (Series<Header>) attrs.get(HeaderConstants.ATTRIBUTE_HEADERS);
        logger.info("Request to Poulpe: requested URI - {}, request headers - {}, request body - {}",
                new Object[]{clientResource.getRequest().getResourceRef(), headers, clientResource.getRequest()});
    }

    private ClientResource createClientResource(String url, boolean buffering) {
        ClientResource clientResource = new ClientResource(new Context(), url);
        clientResource.getContext().getParameters().add("socketConnectTimeoutMs", String.valueOf(CONNECTION_TIMEOUT));
        clientResource.getContext().getParameters().add("maxIoIdleTimeMs", String.valueOf(CONNECTION_TIMEOUT));
        clientResource.setEntityBuffering(buffering);
        return clientResource;
    }

    private void addCredentialsIfAny(ClientResource clientResource){
        if (!StringUtils.isAnyBlank(login, password))
            clientResource.setChallengeResponse(ChallengeScheme.HTTP_BASIC, login, password);
    }
}
