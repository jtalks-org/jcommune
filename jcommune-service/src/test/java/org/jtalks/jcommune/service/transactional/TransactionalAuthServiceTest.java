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

package org.jtalks.jcommune.service.transactional;

import org.jtalks.jcommune.model.dao.UserDao;
import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.model.plugins.Plugin;
import org.jtalks.jcommune.model.plugins.SimpleAuthenticationPlugin;
import org.jtalks.jcommune.model.plugins.exceptions.NoConnectionException;
import org.jtalks.jcommune.model.plugins.exceptions.UnexpectedErrorException;
import org.jtalks.jcommune.service.plugins.PluginLoader;
import org.jtalks.jcommune.service.plugins.TypeFilter;
import org.mockito.Mock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.testng.Assert.*;

public class TransactionalAuthServiceTest {

    private TransactionalAuthService authService;

    @Mock
    private UserDao userDao;
    @Mock
    private PluginLoader pluginLoader;

    @BeforeMethod
    public void setUp() throws Exception {
        initMocks(this);
        authService = new TransactionalAuthService(userDao, pluginLoader);
    }

    private Map<String, String> createAuthInfo(String username, String email) {
        Map<String, String> authInfo = new HashMap<>();
        authInfo.put("username", username);
        authInfo.put("email", email);
        authInfo.put("firstName", "firstName");
        authInfo.put("lastName", "lastName");
        return authInfo;
    }

    private void preparePlugin(String username, String passwordHash, Map<String, String> authInfo)
            throws UnexpectedErrorException, NoConnectionException {
        SimpleAuthenticationPlugin plugin = mock(SimpleAuthenticationPlugin.class);
        when(pluginLoader.getPlugins(any(TypeFilter.class))).thenReturn(Arrays.<Plugin>asList(plugin));
        when(plugin.authenticate(username, passwordHash)).thenReturn(authInfo);
        when(plugin.getState()).thenReturn(Plugin.State.ENABLED);
    }

    private void prepareOldUser(String username) {
        JCUser oldUser = new JCUser(username, "oldEmail@email.em", "oldPasswordHash");
        when(userDao.getByUsername(username)).thenReturn(oldUser);
    }

    @Test
    public void authenticateNotExistingUserShouldReturnNewUser() throws Exception {
        String username = "user";
        String passwordHash = "passwordHash";
        String email = "email@email.em";

        Map<String, String> authInfo = createAuthInfo(username, email);
        preparePlugin(username, passwordHash, authInfo);

        JCUser user = authService.pluginAuthenticate(username, passwordHash, true);

        verify(userDao).saveOrUpdate(user);

        assertNotNull(user, "Authentication not existing user with correct parameters should return new user.");
    }

    @Test
    public void authenticateShouldNotReturnAnyUserIfThereAreNoConnectionToPoulpe() throws Exception {
        String username = "user";
        String passwordHash = "passwordHash";

        SimpleAuthenticationPlugin plugin = mock(SimpleAuthenticationPlugin.class);
        when(pluginLoader.getPlugins(any(TypeFilter.class))).thenReturn(Arrays.<Plugin>asList(plugin));
        when(plugin.authenticate(username, passwordHash)).thenThrow(new NoConnectionException());

        JCUser user = authService.pluginAuthenticate(username, passwordHash, true);

        verifyZeroInteractions(userDao);

        assertNull(user, "Authentication should not save and return any user if plugin throws NoConnectionException.");
    }

    @Test
    public void authenticateShouldNotReturnAnyUserIfPluginThrowsAnUnexpectedException() throws Exception {
        String username = "user";
        String passwordHash = "passwordHash";

        SimpleAuthenticationPlugin plugin = mock(SimpleAuthenticationPlugin.class);
        when(pluginLoader.getPlugins(any(TypeFilter.class))).thenReturn(Arrays.<Plugin>asList(plugin));
        when(plugin.authenticate(username, passwordHash)).thenThrow(new UnexpectedErrorException());

        JCUser user = authService.pluginAuthenticate(username, passwordHash, false);

        verifyZeroInteractions(userDao);

        assertNull(user, "Authentication should not save and return any user if plugin throws UnexpectedErrorException.");
    }

    @Test
    public void authenticateExistingUserShouldReturnUserWithUpdatedEmail() throws Exception {
        String username = "user";
        String passwordHash = "passwordHash";
        String email = "email@email.em";
        Map<String, String> authInfo = createAuthInfo(username, email);
        preparePlugin(username, passwordHash, authInfo);
        prepareOldUser(username);

        JCUser user = authService.pluginAuthenticate(username, passwordHash, false);

        assertEquals(user.getEmail(), email,
                "Authenticate existing user with correct parameters should return updated user.");
    }

    @Test
    public void authenticateExistingUserShouldReturnUserWithUpdatedFirstName() throws Exception {
        String username = "user";
        String passwordHash = "passwordHash";
        String email = "email@email.em";
        Map<String, String> authInfo = createAuthInfo(username, email);
        preparePlugin(username, passwordHash, authInfo);
        prepareOldUser(username);

        JCUser user = authService.pluginAuthenticate(username, passwordHash, false);

        assertEquals(user.getFirstName(), authInfo.get("firstName"),
                "Authenticate existing user with correct parameters should return user with updated first name.");
    }

    @Test
    public void authenticateExistingUserShouldReturnUserWithUpdatedLastName() throws Exception {
        String username = "user";
        String passwordHash = "passwordHash";
        String email = "email@email.em";
        Map<String, String> authInfo = createAuthInfo(username, email);
        preparePlugin(username, passwordHash, authInfo);
        prepareOldUser(username);

        JCUser user = authService.pluginAuthenticate(username, passwordHash, false);

        assertEquals(user.getLastName(), authInfo.get("lastName"),
                "Authenticate existing user with correct parameters should return user with updated last name.");
    }

}
