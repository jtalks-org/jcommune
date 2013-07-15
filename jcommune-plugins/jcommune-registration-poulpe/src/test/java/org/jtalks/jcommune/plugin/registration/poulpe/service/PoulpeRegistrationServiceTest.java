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

import org.jtalks.jcommune.model.plugins.exceptions.NoConnectionException;
import org.jtalks.jcommune.plugin.registration.poulpe.pojo.Errors;
import org.jtalks.jcommune.plugin.registration.poulpe.pojo.Error;
import org.jtalks.jcommune.plugin.registration.poulpe.pojo.User;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Method;
import org.restlet.data.Status;
import org.restlet.ext.jaxb.JaxbRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

/**
 * @author Andrey Pogorelov
 */
public class PoulpeRegistrationServiceTest {

    private PoulpeRegistrationService service;
    private String url = "http://localhost:8080/rest/private/user";

    @BeforeMethod
    public void setUp() throws Exception {
        initMocks(this);
        service = spy(new PoulpeRegistrationService(url, "user", "1234"));
    }

    @Test
    public void testRegisterUserWithInvalidCredentials() throws Exception {
        Errors errors = new Errors();
        List<Error> errorList = new ArrayList<Error>();
        errorList.add(createError("user.username.length_constraint_violation", null));
        errors.setErrorList(errorList);
        JaxbRepresentation<Errors> errorsRepr = new JaxbRepresentation<Errors>(errors);
        ClientResource clientResource = createClientResource(Status.CLIENT_ERROR_BAD_REQUEST, errorsRepr);

        doReturn(clientResource).when(service).sendRegistrationRequest(any(User.class));

        Map<String, String> result = service.registerUser("", "password", "email@email.ru");

        assertEquals(result.size(), 1, "User with invalid credentials shouldn't pass registration.");
    }

    @Test(expectedExceptions = NoConnectionException.class)
    public void testRegisterUserFailedWithUnexpectedError() throws Exception {
        ClientResource clientResource = createClientResource(Status.CLIENT_ERROR_NOT_FOUND, null);

        doReturn(clientResource).when(service).sendRegistrationRequest(any(User.class));

        service.registerUser("username", "password", "email@email.ru");
    }

    @Test
    public void testRegisterUser() throws Exception {
        ClientResource clientResource = createClientResource(Status.SUCCESS_OK, null);

        doReturn(clientResource).when(service).sendRegistrationRequest(any(User.class));

        Map<String, String> result = service.registerUser("username", "password", "email@email.ru");

        assertNull(result, "User with valid credentials should pass registration.");
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
