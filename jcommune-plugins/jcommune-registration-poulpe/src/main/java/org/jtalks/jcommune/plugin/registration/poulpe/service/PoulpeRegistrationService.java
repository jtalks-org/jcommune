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

import com.google.common.annotations.VisibleForTesting;
import org.jtalks.jcommune.plugin.registration.poulpe.exceptions.NoConnectionException;
import org.jtalks.jcommune.plugin.registration.poulpe.pojo.Errors;
import org.jtalks.jcommune.plugin.registration.poulpe.pojo.User;
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

/**
 * This class contains method needed for communicate with Poulpe rest service.
 *
 * @author Andrey Pogorelov
 */
public class PoulpeRegistrationService implements RegistrationService {

    private static final int CONNECTION_TIMEOUT = 5000;

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private String url;
    private String login;
    private String password;

    public PoulpeRegistrationService(String url, String login, String password) {
        this.url = url;
        this.login = login;
        this.password = password;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Errors registerUser(String username, String password, String email)
            throws IOException, NoConnectionException, JAXBException {
        User user = createUser(username, password, email);
        ClientResource clientResource = sendRegistrationRequest(user);
        return getResult(clientResource);
    }

    /**
     * Gets errors from response if request wasn't successful, otherwise return null.
     *
     * @param clientResource response container
     * @return errors
     * @throws NoConnectionException
     * @throws IOException
     */
    private Errors getResult(ClientResource clientResource) throws NoConnectionException, IOException, JAXBException {
        if (clientResource.getStatus() == Status.SUCCESS_OK) {
            return null;
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
     * @throws IOException
     */
    private Errors parseErrors(Representation repr) throws IOException, JAXBException {
        JAXBContext context = JAXBContext.newInstance(Errors.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        return (Errors) unmarshaller.unmarshal(repr.getStream());
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
     *
     * @param user user entity for registration
     * @return ClientResource result
     */
    @VisibleForTesting
    protected ClientResource sendRegistrationRequest(User user) {
        ClientResource clientResource = createClientResource(url);
        clientResource.setChallengeResponse(ChallengeScheme.HTTP_BASIC, login, password);
        try {
            clientResource.post(user);
        } catch (ResourceException e) {
            logger.error("Poulpe registration request error: {}", e.getStatus());
        }
        return clientResource;
    }

    private ClientResource createClientResource(String url) {
        ClientResource clientResource = new ClientResource(new Context(), url);
        clientResource.getContext().getParameters().add("socketConnectTimeoutMs", String.valueOf(CONNECTION_TIMEOUT));
        clientResource.getContext().getParameters().add("maxIoIdleTimeMs", String.valueOf(CONNECTION_TIMEOUT));
        clientResource.setEntityBuffering(true);
        return clientResource;
    }
}
