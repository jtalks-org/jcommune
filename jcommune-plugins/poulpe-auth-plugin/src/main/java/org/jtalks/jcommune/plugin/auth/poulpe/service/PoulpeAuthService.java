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

import com.google.common.annotations.VisibleForTesting;
import org.jtalks.jcommune.model.plugins.exceptions.NoConnectionException;
import org.jtalks.jcommune.plugin.auth.poulpe.dto.Authentication;
import org.jtalks.jcommune.plugin.auth.poulpe.dto.Errors;
import org.jtalks.jcommune.plugin.auth.poulpe.dto.User;
import org.restlet.Context;
import org.restlet.data.ChallengeScheme;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.IOException;
import java.util.*;

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
    private String login;
    private String password;

    public PoulpeAuthService(String url, String login, String password) {
        this.regUrl = url + "/rest/private/user";
        this.authUrl = url + "/rest/authenticate";
        this.login = login;
        this.password = password;
    }

    /**
     * Register user with specified data via Poulpe.
     * Returns errors if request failed, otherwise return null.
     *
     * @param username username
     * @param password password
     * @param email    email
     * @return errors
     */
    public Map<String, String> registerUser(String username, String password, String email)
            throws IOException, NoConnectionException, JAXBException {
        User user = createUser(username, password, email);
        ClientResource clientResource = sendRegistrationRequest(user);
        return getRegistrationResult(clientResource);
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
        return getAuthResult(clientResource);
    }

    /**
     * Gets authentication result from response entity.
     *
     * @param clientResource response container
     * @return map with user details
     * @throws org.jtalks.jcommune.model.plugins.exceptions.NoConnectionException
     */
    private Map<String, String> getAuthResult(ClientResource clientResource)
            throws NoConnectionException, JAXBException, IOException {
        if (clientResource.getStatus().getCode() == Status.SUCCESS_OK.getCode()) {
            return parseUserDetails(clientResource.getResponseEntity());
        } else if (clientResource.getStatus().getCode() == Status.CLIENT_ERROR_NOT_FOUND.getCode()) {
            return Collections.emptyMap();
        } else {
            throw new NoConnectionException();
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
        return authInfo;
    }

    /**
     * Gets errors from response if request wasn't successful, otherwise return null.
     *
     * @param clientResource response container
     * @return errors
     * @throws org.jtalks.jcommune.model.plugins.exceptions.NoConnectionException
     *
     * @throws java.io.IOException
     */
    private Map<String, String> getRegistrationResult(ClientResource clientResource)
            throws NoConnectionException, IOException, JAXBException {
        if (clientResource.getStatus().getCode() == Status.SUCCESS_OK.getCode()) {
            return Collections.emptyMap();
        } else if (clientResource.getStatus().getCode() == Status.CLIENT_ERROR_BAD_REQUEST.getCode()
                || clientResource.getStatus().getCode() == Status.SERVER_ERROR_INTERNAL.getCode()) {
            return parseErrors(clientResource.getResponseEntity());
        } else {
            throw new NoConnectionException();
        }
    }

    /**
     * Parse bad response representation for errors.
     *
     * @param repr response representation
     * @return errors
     * @throws java.io.IOException
     */
    private Map<String, String> parseErrors(Representation repr) throws IOException, JAXBException {
        JAXBContext context = JAXBContext.newInstance(Errors.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        Errors errorsRepr = (Errors) unmarshaller.unmarshal(repr.getStream());

        ResourceBundle resourceBundle = ResourceBundle.getBundle("ValidationMessages", new Locale("en"));
        Map<String, String> errors = new HashMap<>();
        for (org.jtalks.jcommune.plugin.auth.poulpe.dto.Error error : errorsRepr.getErrorList()) {
            if (error.getCode() != null && !error.getCode().isEmpty() && resourceBundle.containsKey(error.getCode())) {
                String errorCode = resourceBundle.getString(error.getCode());
                if (error.getCode().contains("email")) {
                    errors.put("email", errorCode);
                } else if (error.getCode().contains("username")) {
                    errors.put("username", errorCode);
                } else if (error.getCode().contains("password")) {
                    errors.put("password", errorCode);
                }
            } else {
                errors.put("common", error.getMessage());
            }
        }
        return errors;
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
        user.setUsername(username);
        user.setPasswordHash(passwordHash);
        user.setEmail(email);
        return user;
    }

    /**
     * Sends registration post request.
     *
     * @param user user entity for registration
     * @return ClientResource result
     */
    @VisibleForTesting
    protected ClientResource sendRegistrationRequest(User user) {
        ClientResource clientResource = createClientResource(regUrl, true);
        clientResource.setChallengeResponse(ChallengeScheme.HTTP_BASIC, login, password);
        try {
            clientResource.post(user);
        } catch (ResourceException e) {
            logger.error("Poulpe registration request error: {}", e.getStatus());
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
    @VisibleForTesting
    protected ClientResource sendAuthRequest(String username, String passwordHash) {
        String url = authUrl + "?username=" + username + "&passwordHash=" + passwordHash;
        ClientResource clientResource = createClientResource(url, false);
        clientResource.setChallengeResponse(ChallengeScheme.HTTP_BASIC, login, password);
        try {
            clientResource.get();
        } catch (ResourceException e) {
            logger.error("Poulpe registration request error: {}", e.getStatus());
        }
        return clientResource;
    }

    private ClientResource createClientResource(String url, boolean buffering) {
        ClientResource clientResource = new ClientResource(new Context(), url);
        clientResource.getContext().getParameters().add("socketConnectTimeoutMs", String.valueOf(CONNECTION_TIMEOUT));
        clientResource.getContext().getParameters().add("maxIoIdleTimeMs", String.valueOf(CONNECTION_TIMEOUT));
        clientResource.setEntityBuffering(buffering);
        return clientResource;
    }
}
