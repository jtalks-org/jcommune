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

import com.xebialabs.restito.server.StubServer;
import org.glassfish.grizzly.http.util.HttpStatus;
import org.jtalks.jcommune.plugin.auth.poulpe.dto.User;
import org.restlet.resource.ClientResource;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static com.xebialabs.restito.builder.stub.StubHttp.whenHttp;
import static com.xebialabs.restito.semantics.Action.status;
import static com.xebialabs.restito.semantics.Condition.get;
import static com.xebialabs.restito.semantics.Condition.parameter;
import static com.xebialabs.restito.semantics.Condition.post;
import static org.testng.Assert.assertEquals;

/**
 * @author Andrey Pogorelov
 */
public class PoulpeAuthServiceIntegrationTest {

    private StubServer server;

    private PoulpeAuthService service;
    private final String POULPE_URL = "http://localhost";
    private String regUrl = "/rest/private/user";
    private String authUrl = "/rest/authenticate";

    /**
     * Start Poulpe server mock (Restito framework).
     */
    @BeforeClass
    private void beforeTestCase() {
        server = new StubServer().run();
        String requestUrl = POULPE_URL + ":" + String.valueOf(server.getPort());
        service = new PoulpeAuthService(requestUrl, "user", "1234");
    }

    /**
     * Shutdown server mock.
     */
    @AfterClass
    public void afterTest() {
        server.stop();
    }

    @Test
    public void testSendRegistrationRequestShouldBeSuccessful() throws Exception {
        User user = createUser("username", "passwordHash", "email@email.ru");
        whenHttp(server).match(post(regUrl)).then(status(HttpStatus.OK_200));

        ClientResource clientResource = service.sendRegistrationRequest(user, true);

        assertEquals(clientResource.getStatus().getCode(), HttpStatus.OK_200.getStatusCode());
    }

    @Test
    public void testSendRegistrationRequestWithBadResponseShouldFail() throws Exception {
        User user = createUser("username", "passwordHash", "email.ru");
        whenHttp(server).match(post(regUrl)).then(status(HttpStatus.INTERNAL_SERVER_ERROR_500));

        ClientResource clientResource = service.sendRegistrationRequest(user, true);

        assertEquals(clientResource.getStatus().getCode(), HttpStatus.INTERNAL_SERVER_ERROR_500.getStatusCode());
    }

    @Test
    public void testSendRegistrationRequestWithValidationErrorResponseShouldFail() throws Exception {
        User user = createUser("username", "passwordHash", "email.ru");
        whenHttp(server).match(post(regUrl)).then(status(HttpStatus.BAD_REQUEST_400));

        ClientResource clientResource = service.sendRegistrationRequest(user, true);

        assertEquals(clientResource.getStatus().getCode(), HttpStatus.BAD_REQUEST_400.getStatusCode());
    }

    @Test
    public void testSendAuthRequestWithValidCredentialsShouldBeSuccessful() throws Exception {
        whenHttp(server).match(get(authUrl),
                parameter("username", "username"),
                parameter("passwordHash", "passwordHash"))
                .then(status(HttpStatus.OK_200));

        ClientResource clientResource = service.sendAuthRequest("username", "passwordHash");

        assertEquals(clientResource.getStatus().getCode(), HttpStatus.OK_200.getStatusCode());
    }

    @Test
    public void testSendAuthRequestWithInvalidCredentialsShouldFail() throws Exception {
        whenHttp(server).match(get(authUrl),
                parameter("username", "username"),
                parameter("passwordHash", "passwordHash")).then(status(HttpStatus.NOT_FOUND_404));

        ClientResource clientResource = service.sendAuthRequest("username", "12345");

        assertEquals(clientResource.getStatus().getCode(), HttpStatus.NOT_FOUND_404.getStatusCode());
    }

    private User createUser(String username, String password, String email) {
        User user = new User();
        user.setUsername(username);
        user.setPasswordHash(password);
        user.setEmail(email);
        return user;
    }
}
