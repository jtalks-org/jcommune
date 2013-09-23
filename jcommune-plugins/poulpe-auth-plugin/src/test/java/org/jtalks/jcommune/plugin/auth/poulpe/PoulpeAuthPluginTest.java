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

package org.jtalks.jcommune.plugin.auth.poulpe;

import org.jtalks.jcommune.model.dto.UserDto;
import org.jtalks.jcommune.model.entity.PluginConfiguration;
import org.jtalks.jcommune.model.entity.PluginProperty;
import org.jtalks.jcommune.model.plugins.Plugin;
import org.jtalks.jcommune.model.plugins.exceptions.NoConnectionException;
import org.jtalks.jcommune.model.plugins.exceptions.UnexpectedErrorException;
import org.jtalks.jcommune.plugin.auth.poulpe.service.PoulpeAuthService;
import org.mockito.Mock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.jtalks.jcommune.model.entity.PluginProperty.Type.STRING;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * @author Andrey Pogorelov
 */
public class PoulpeAuthPluginTest {

    @Mock
    PoulpeAuthService service;

    PoulpeAuthPlugin plugin;

    @BeforeMethod
    public void setUp() throws Exception {
        initMocks(this);
        plugin = new PoulpeAuthPlugin();
        plugin.setPluginService(service);
    }

    @Test
    public void testConfigure() throws Exception {
        PluginConfiguration configuration = createConfiguration("http://localhost", "user", "1234");
        plugin.configure(configuration);

        assertTrue(plugin.getState() == Plugin.State.ENABLED,
                "Plugin with correct parameters should be configured properly.");
    }

    @Test
    public void pluginWithIncorrectParametersShouldNotBeConfigured() throws Exception {
        PluginConfiguration configuration = createConfiguration(null, "user", "1234");
        plugin.configure(configuration);

        assertTrue(plugin.getState() == Plugin.State.IN_ERROR,
                "Plugin with incorrect parameters shouldn't be configured.");
    }

    @Test
    public void pluginWithIncorrectUrlShouldNotBeConfigured() throws Exception {
        PluginConfiguration configuration = createConfiguration("http:/jtalks.org", "user", "1234");
        plugin.configure(configuration);

        assertTrue(plugin.getState() == Plugin.State.IN_ERROR,
                "Plugin with incorrect Url shouldn't be configured.");
    }

    @Test
    public void userShouldNotBeRegisteredIfSomeErrorOccurred()
            throws JAXBException, IOException, NoConnectionException, UnexpectedErrorException {
        UserDto userDto = createUserDto("user", "", "");
        Map<String, String> errors = new HashMap<>();
        errors.put("email", "Invalid email");
        errors.put("password", "Invalid password");

        when(service.registerUser(userDto, false)).thenReturn(errors);

        Map<String, String> result = plugin.registerUser(userDto);

        assertEquals(result.size(), 2, "User with incorrect parameters shouldn't be registered.");
    }

    @Test
    public void userWithCorrectParametersShouldBeRegistered()
            throws UnexpectedErrorException, NoConnectionException, IOException, JAXBException {
        UserDto userDto = createUserDto("user", "1234", "email@email.em");

        when(service.registerUser(userDto, true)).thenReturn(Collections.EMPTY_MAP);

        Map<String, String> result = plugin.registerUser(userDto);

        assertEquals(result.size(), 0, "User with correct parameters should be registered.");
    }

    @Test(expectedExceptions = NoConnectionException.class)
    public void registerUserShouldThrowNoConnectionExceptionIfPoulpeUnavailable()
            throws UnexpectedErrorException, NoConnectionException, IOException, JAXBException {
        UserDto userDto = createUserDto("user", "1234", "email@email.em");

        when(service.registerUser(userDto, false)).thenThrow(new NoConnectionException());

        plugin.registerUser(userDto);
    }

    @Test(expectedExceptions = UnexpectedErrorException.class)
    public void registerUserShouldThrowUnexpectedErrorExceptionIfSomeErrorOccurred()
            throws UnexpectedErrorException, NoConnectionException, IOException, JAXBException {
        UserDto userDto = createUserDto("user", "1234", "email@email.em");

        when(service.registerUser(userDto, false)).thenThrow(new JAXBException(""));

        plugin.registerUser(userDto);
    }

    @Test
    public void userShouldNotBeAuthenticatedIfSomeErrorOccurred()
            throws JAXBException, IOException, NoConnectionException, UnexpectedErrorException {
        String username = "user";
        String password = "";

        when(service.authenticate(username, password)).thenReturn(Collections.<String, String>emptyMap());

        Map<String, String> result = plugin.authenticate(username, password);

        assertEquals(result.size(), 0, "User with incorrect parameters shouldn't be authenticated.");
    }

    @Test
    public void userWithCorrectParametersShouldBeAuthenticated()
            throws UnexpectedErrorException, NoConnectionException, IOException, JAXBException {
        String username = "user";
        String password = "1234";
        Map<String, String> authInfo = new HashMap<>();
        authInfo.put("username", "user");
        authInfo.put("password", "1234");
        authInfo.put("email", "email@email.em");

        when(service.authenticate(username, password)).thenReturn(authInfo);

        Map<String, String> result = plugin.authenticate(username, password);

        assertEquals(result.size(), 3, "User with correct parameters should be authenticated.");
    }

    @Test(expectedExceptions = NoConnectionException.class)
    public void authenticateShouldThrowNoConnectionExceptionIfPoulpeUnavailable()
            throws UnexpectedErrorException, NoConnectionException, IOException, JAXBException {
        String username = "user";
        String password = "1234";

        when(service.authenticate(username, password)).thenThrow(new NoConnectionException());

        plugin.authenticate(username, password);
    }

    @Test(expectedExceptions = UnexpectedErrorException.class)
    public void authenticateShouldThrowUnexpectedErrorExceptionIfSomeErrorOccurred()
            throws UnexpectedErrorException, NoConnectionException, IOException, JAXBException {
        String username = "user";
        String password = "1234";

        when(service.authenticate(username, password)).thenThrow(new JAXBException(""));

        plugin.authenticate(username, password);
    }

    private UserDto createUserDto(String username, String password, String email) {
        UserDto userDto = new UserDto();
        userDto.setUsername(username);
        userDto.setEmail(email);
        userDto.setPassword(password);
        return userDto;
    }

    private PluginConfiguration createConfiguration(String url, String login, String password) {

        PluginProperty urlProperty =
                new PluginProperty("URL", STRING, url);
        urlProperty.setName("Url");
        PluginProperty loginProperty = new PluginProperty("LOGIN", STRING, login);
        loginProperty.setName("Login");
        PluginProperty passwordProperty = new PluginProperty("PASSWORD", STRING, password);
        passwordProperty.setName("Password");
        return new PluginConfiguration("Poulpe Auth Plugin", true,
                Arrays.asList(urlProperty, loginProperty, passwordProperty));
    }
}
