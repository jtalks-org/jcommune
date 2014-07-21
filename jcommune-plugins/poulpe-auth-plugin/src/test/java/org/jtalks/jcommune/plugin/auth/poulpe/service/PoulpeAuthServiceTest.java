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

import org.jtalks.jcommune.model.dto.UserDto;
import org.jtalks.jcommune.plugin.api.exceptions.NoConnectionException;
import org.jtalks.jcommune.plugin.api.exceptions.UnexpectedErrorException;
import org.jtalks.jcommune.plugin.auth.poulpe.dto.*;
import org.jtalks.jcommune.plugin.auth.poulpe.dto.Error;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Method;
import org.restlet.data.Status;
import org.restlet.ext.jaxb.JaxbRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.ClientResource;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.testng.Assert.*;

/**
 * @author Andrey Pogorelov
 */
public class PoulpeAuthServiceTest {

    private PoulpeAuthService service;
    private String url = "http://localhost:8080";

    @BeforeMethod
    public void setUp() throws Exception {
        initMocks(this);
        service = spy(new PoulpeAuthService(url, "user", "1234"));
    }

    @Test
    public void testRegisterUserWithInvalidCredentialsShouldFail() throws Exception {
        Errors errors = new Errors();
        List<Error> errorList = new ArrayList<>();
        errorList.add(createError("user.username.length_constraint_violation", null));
        errorList.add(createError("user.email.illegal_length", null));
        errorList.add(createError("user.password.length_constraint_violation", null));

        errors.setErrorList(errorList);
        JaxbRepresentation<Errors> errorsRepr = new JaxbRepresentation<>(errors);
        ClientResource clientResource = createClientResource(Status.CLIENT_ERROR_BAD_REQUEST, errorsRepr);

        doReturn(clientResource).when(service).sendRegistrationRequest(any(User.class), eq(true));

        Map<String, String> result = service.registerUser(createUserDto("", "password", "email@email.ou"), true);

        assertEquals(result.size(), 3, "User with invalid credentials shouldn't pass registration.");
    }

    @Test(expectedExceptions = NoConnectionException.class)
    public void testRegisterUserShouldFailIfConnectionErrorOccurred() throws Exception {
        ClientResource clientResource = createClientResource(Status.CLIENT_ERROR_REQUEST_TIMEOUT, null);

        doReturn(clientResource).when(service).sendRegistrationRequest(any(User.class), eq(true));

        service.registerUser(createUserDto("username", "password", "email@email.ru"), true);
    }

    @Test(expectedExceptions = UnexpectedErrorException.class)
    public void testRegisterUserShouldFailIfInternalServerErrorOccurred() throws Exception {
        ClientResource clientResource = createClientResource(Status.SERVER_ERROR_INTERNAL, null);

        doReturn(clientResource).when(service).sendRegistrationRequest(any(User.class), eq(true));

        service.registerUser(createUserDto("username", "password", "email@email.ru"), true);
    }

    @Test
    public void testRegisterUserWithCorrectParametersShouldBeSuccessful() throws Exception {
        ClientResource clientResource = createClientResource(Status.SUCCESS_OK, new StringRepresentation(""));

        doReturn(clientResource).when(service).sendRegistrationRequest(any(User.class), eq(true));

        Map<String, String> result = service.registerUser(createUserDto("username", "password", "email@email.ru"), true);

        assertTrue(result.size() == 0, "User with valid credentials should pass registration.");
    }

    @Test
    public void testAuthUserWithCorrectCredentialsShouldBeSuccessful() throws Exception {
        Authentication auth = createAuth("username", "password", "email");
        auth.setStatus("success");
        JaxbRepresentation<Authentication> authRepr = new JaxbRepresentation<>(auth);
        ClientResource clientResource = createClientResource(Status.SUCCESS_OK, authRepr);

        doReturn(clientResource).when(service).sendAuthRequest("username", "password");

        Map<String, String> result = service.authenticate("username", "password");

        assertTrue(result.size() >= 3,
                "Authentication user with valid credentials should return user details (username, password, email).");
    }

    @Test
    public void testAuthUserWithInvalidCredentialsShouldFail() throws Exception {
        Authentication auth = createAuth("", "password", "email");
        auth.setStatus("fail");
        JaxbRepresentation<Authentication> authRepr = new JaxbRepresentation<>(auth);
        ClientResource clientResource = createClientResource(Status.CLIENT_ERROR_NOT_FOUND, authRepr);

        doReturn(clientResource).when(service).sendAuthRequest("", "password");

        Map<String, String> result = service.authenticate("", "password");

        assertEquals(result.size(), 0, "User with invalid credentials shouldn't pass authentication.");
    }

    @Test(expectedExceptions = NoConnectionException.class)
    public void testAuthUserShouldFailIfConnectionErrorOccurred() throws Exception {
        ClientResource clientResource = createClientResource(Status.CLIENT_ERROR_REQUEST_TIMEOUT, null);

        doReturn(clientResource).when(service).sendAuthRequest("user", "password");

        service.authenticate("user", "password");
    }

    private Authentication createAuth(String username, String password, String email) {
        Authentication auth = new Authentication();
        auth.setProfile(new Profile(new PoulpeUser(username, email, password, null)));
        auth.setCredintals(new Credentials("username"));
        return auth;
    }

    private Error createError(String code, String message) {
        Error error = new Error();
        if(code != null) {
            error.setCode(code);
        }
        if(message != null) {
            error.setMessage(message);
        }
        return error;
    }

    private UserDto createUserDto(String username, String password, String email) {
        UserDto userDto = new UserDto();
        userDto.setUsername(username);
        userDto.setEmail(email);
        userDto.setPassword(password);
        return userDto;
    }

    private ClientResource createClientResource(Status status, Representation repr) {
        ClientResource clientResource = new ClientResource(url);
        clientResource.setMethod(Method.POST);
        clientResource.setEntityBuffering(true);
        Response response = new Response(new Request());
        response.setStatus(status);
        if (repr != null) {
            response.setEntity(repr);
        }
        clientResource.setResponse(response);
        return clientResource;
    }
}
