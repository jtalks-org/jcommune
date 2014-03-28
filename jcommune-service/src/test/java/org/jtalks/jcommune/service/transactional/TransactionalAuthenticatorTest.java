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

import com.google.common.collect.ImmutableMap;
import org.jtalks.common.model.dao.GroupDao;
import org.jtalks.common.model.entity.Group;
import org.jtalks.common.model.entity.User;
import org.jtalks.common.service.security.SecurityContextHolderFacade;
import org.jtalks.jcommune.model.dao.UserDao;
import org.jtalks.jcommune.model.dto.RegisterUserDto;
import org.jtalks.jcommune.model.dto.UserDto;
import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.model.plugins.AuthenticationPlugin;
import org.jtalks.jcommune.model.plugins.Plugin;
import org.jtalks.jcommune.model.plugins.RegistrationPlugin;
import org.jtalks.jcommune.model.plugins.exceptions.NoConnectionException;
import org.jtalks.jcommune.model.plugins.exceptions.UnexpectedErrorException;
import org.jtalks.jcommune.service.Authenticator;
import org.jtalks.jcommune.service.PluginService;
import org.jtalks.jcommune.service.exceptions.NotFoundException;
import org.jtalks.jcommune.service.nontransactional.EncryptionService;
import org.jtalks.jcommune.service.nontransactional.ImageService;
import org.jtalks.jcommune.service.nontransactional.MailService;
import org.jtalks.jcommune.service.plugins.PluginLoader;
import org.jtalks.jcommune.service.plugins.TypeFilter;
import org.jtalks.jcommune.service.security.AdministrationGroup;
import org.mockito.Mock;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.jtalks.jcommune.model.dto.LoginUserDto;

import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import static org.testng.Assert.*;

/**
 * @author Andrey Pogorelov
 */
public class TransactionalAuthenticatorTest {

    @Mock
    private PluginLoader pluginLoader;
    @Mock
    private AuthenticationPlugin authPlugin;
    @Mock
    private RegistrationPlugin registrationPlugin;
    @Mock
    private EncryptionService encryptionService;
    @Mock
    private UserDao userDao;
    @Mock
    private GroupDao groupDao;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private SecurityContextHolderFacade securityFacade;
    @Mock
    private SecurityContext securityContext;
    @Mock
    private RememberMeServices rememberMeServices;
    @Mock
    private SessionAuthenticationStrategy sessionStrategy;
    @Mock
    private BindingResult bindingResult;
    @Mock
    private HttpServletRequest httpRequest;
    @Mock
    private HttpServletResponse httpResponse;
    @Mock
    MailService mailService;
    @Mock
    ImageService avatarService;
    @Mock
    PluginService pluginService;
    @Mock
    private Validator validator;

    private Authenticator authenticator;

    @BeforeMethod
    public void setUp() throws Exception {
        initMocks(this);
        authenticator = new TransactionalAuthenticator(pluginLoader, userDao, groupDao,
                encryptionService, mailService, avatarService, pluginService,
                securityFacade, rememberMeServices, sessionStrategy, validator, authenticationManager);
    }

    private JCUser prepareOldUser(String username) {
        JCUser oldUser = new JCUser(username, "oldEmail@email.em", "14a88b9d2f52c55b5fbcf9c5d9c11875");
        when(userDao.getByUsername(username)).thenReturn(oldUser);
        return oldUser;
    }

    private Map<String, String> createAuthInfo(String username, String email) {
        Map<String, String> authInfo = new HashMap<>();
        authInfo.put("username", username);
        authInfo.put("email", email);
        authInfo.put("firstName", "firstName");
        authInfo.put("lastName", "lastName");
        return authInfo;
    }

    private void preparePlugin(String username, String passwordHash, Map<String, String> authInfo) throws Exception {
        Class authPluginClass = AuthenticationPlugin.class;
        when(pluginLoader.getPluginByClassName(authPluginClass)).thenReturn(authPlugin);
        when(authPlugin.authenticate(username, passwordHash)).thenReturn(authInfo);
        when(authPlugin.getState()).thenReturn(Plugin.State.ENABLED);
        Class cl = RegistrationPlugin.class;
        when(pluginLoader.getPluginByClassName(cl)).thenReturn(registrationPlugin);
        when(registrationPlugin.getState()).thenReturn(Plugin.State.ENABLED);
    }

    private void prepareAuth() {
        UsernamePasswordAuthenticationToken expectedToken = mock(UsernamePasswordAuthenticationToken.class);
        when(securityFacade.getContext()).thenReturn(securityContext);
        when(expectedToken.isAuthenticated()).thenReturn(true);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(expectedToken);
    }

    @Test
    public void authenticateExistingUserShouldBeSuccessful() throws Exception {
        String passwordHash = "5f4dcc3b5aa765d61d8327deb882cf99";
        JCUser user = getDefaultUser();
        LoginUserDto loginUserDto = createDefaultLoginUserDto();
        Map<String, String> authInfo = createAuthInfo(user.getUsername(), user.getEmail());
        when(userDao.getByUsername(user.getUsername())).thenReturn(user);
        when(encryptionService.encryptPassword(user.getPassword())).thenReturn(passwordHash);
        prepareAuth();
        preparePlugin(user.getUsername(), passwordHash, authInfo);

        boolean result = authenticator.authenticate(loginUserDto, httpRequest, httpResponse);

        assertTrue(result, "Authentication existing user with correct credentials should be successful.");
    }

    @Test
    public void authenticateNotExistingUserShouldBeSuccessful() throws Exception {
        String passwordHash = "5f4dcc3b5aa765d61d8327deb882cf99";
        LoginUserDto loginUserDto = createDefaultLoginUserDto();
        JCUser user = getDefaultUser();
        Map<String, String> authInfo = createAuthInfo(user.getUsername(), user.getEmail());
        when(userDao.getByUsername(user.getUsername())).thenReturn(null).thenReturn(null).thenReturn(user);
        when(encryptionService.encryptPassword(user.getPassword())).thenReturn(passwordHash);
        prepareAuth();
        preparePlugin(user.getUsername(), passwordHash, authInfo);

        boolean result = authenticator.authenticate(loginUserDto, httpRequest, httpResponse);

        assertTrue(result, "Authentication not existing user with correct credentials should be successful.");
    }

    @Test
    public void authenticateUserWithNewCredentialsShouldBeSuccessful() throws Exception {
        String passwordHash = "5f4dcc3b5aa765d61d8327deb882cf99";
        String email = "email@email.em";
        LoginUserDto loginUserDto = createDefaultLoginUserDto();
        JCUser oldUser = prepareOldUser(loginUserDto.getUserName());
        Map<String, String> authInfo = createAuthInfo(oldUser.getUsername(), email);
        authInfo.put("enabled", "true");
        Group group = new Group(AdministrationGroup.USER.getName());
        when(groupDao.getGroupByName(AdministrationGroup.USER.getName())).thenReturn(group);
        when(userDao.getByUsername(oldUser.getUsername())).thenReturn(oldUser);
        when(encryptionService.encryptPassword(loginUserDto.getPassword())).thenReturn(passwordHash);
        UsernamePasswordAuthenticationToken expectedToken = mock(UsernamePasswordAuthenticationToken.class);
        when(securityFacade.getContext()).thenReturn(securityContext);
        when(expectedToken.isAuthenticated()).thenReturn(true);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException(null)).thenReturn(expectedToken);
        preparePlugin(oldUser.getUsername(), passwordHash, authInfo);

        boolean result = authenticator.authenticate(loginUserDto, httpRequest, httpResponse);

        verify(userDao).saveOrUpdate(oldUser);

        assertTrue(result, "Authentication user with new credentials should be successful.");
    }

    @Test
    public void authenticateUserShouldBeSuccessfulIfPluginAndJCommuneUseTheSameDatabase() throws Exception {
        String username = "user";
        String password = "password";
        String passwordHash = "5f4dcc3b5aa765d61d8327deb882cf99";
        String email = "email@email.em";
        LoginUserDto loginUserDto = createDefaultLoginUserDto();
        Map<String, String> authInfo = createAuthInfo(username, email);
        Group group = new Group(AdministrationGroup.USER.getName());
        User commonUser = new User(username, email, password, null);
        commonUser.getGroups().add(group);
        when(groupDao.getGroupByName(group.getName())).thenReturn(group);
        when(userDao.getByUsername(username)).thenReturn(null);
        when(userDao.getCommonUserByUsername(username)).thenReturn(commonUser);
        when(encryptionService.encryptPassword(password)).thenReturn(passwordHash);
        prepareAuth();
        preparePlugin(username, passwordHash, authInfo);

        boolean result = authenticator.authenticate(loginUserDto, httpRequest, httpResponse);

        assertTrue(result, "Authentication not existing user with correct credentials should be successful " +
                "if case Plugin and JCommune use the same database.");
    }

    @Test
    public void authenticateUserWithNewCredentialsShouldFailIfPluginNotFound() throws Exception {
        String passwordHash = "5f4dcc3b5aa765d61d8327deb882cf99";
        LoginUserDto loginUserDto = createDefaultLoginUserDto();
        JCUser oldUser = prepareOldUser(loginUserDto.getUserName());
        when(userDao.getByUsername(oldUser.getUsername())).thenReturn(oldUser);
        when(encryptionService.encryptPassword(loginUserDto.getPassword())).thenReturn(passwordHash);
        UsernamePasswordAuthenticationToken expectedToken = mock(UsernamePasswordAuthenticationToken.class);
        when(securityFacade.getContext()).thenReturn(securityContext);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(expectedToken);
        when(expectedToken.isAuthenticated()).thenReturn(false);

        when(pluginLoader.getPlugins(any(TypeFilter.class))).thenReturn(Collections.EMPTY_LIST);

        boolean result = authenticator.authenticate(loginUserDto, httpRequest, httpResponse);

        assertFalse(result, "Authenticate user with new credentials should fail if plugin not found.");
    }

    @Test
    public void authenticateUserWithBadCredentialsShouldFail() throws Exception {
        String password = "password";
        String passwordHash = "5f4dcc3b5aa765d61d8327deb882cf99";
        LoginUserDto loginUserDto = createDefaultLoginUserDto();
        JCUser oldUser = prepareOldUser(loginUserDto.getUserName());
        when(userDao.getByUsername(oldUser.getUsername())).thenReturn(oldUser);
        when(encryptionService.encryptPassword(password)).thenReturn(passwordHash);
        when(securityFacade.getContext()).thenReturn(securityContext);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException(null));

        preparePlugin(oldUser.getUsername(), passwordHash, Collections.EMPTY_MAP);

        boolean result = authenticator.authenticate(loginUserDto, httpRequest, httpResponse);

        assertFalse(result, "Authenticate user with bad credentials should fail.");
    }

    @Test(expectedExceptions = NoConnectionException.class)
    public void authenticateShouldFailIfThereAreNoConnectionToAuthService() throws Exception {
        String passwordHash = "5f4dcc3b5aa765d61d8327deb882cf99";
        LoginUserDto loginUserDto = createDefaultLoginUserDto();

        when(encryptionService.encryptPassword(loginUserDto.getPassword())).thenReturn(passwordHash);
        when(authPlugin.getState()).thenReturn(Plugin.State.ENABLED);
        when(userDao.getByUsername(loginUserDto.getUserName())).thenReturn(null);
        Class cl = AuthenticationPlugin.class;
        when(pluginLoader.getPluginByClassName(cl)).thenReturn(authPlugin);
        when(authPlugin.authenticate(loginUserDto.getUserName(), passwordHash)).thenThrow(new NoConnectionException());

        authenticator.authenticate(loginUserDto, httpRequest, httpResponse);
    }

    @Test(expectedExceptions = UnexpectedErrorException.class)
    public void authenticateShouldFailIfPluginThrowsAnUnexpectedException() throws Exception {
        String passwordHash = "5f4dcc3b5aa765d61d8327deb882cf99";
        LoginUserDto loginUserDto = createDefaultLoginUserDto();

        when(encryptionService.encryptPassword(loginUserDto.getPassword())).thenReturn(passwordHash);
        when(authPlugin.getState()).thenReturn(Plugin.State.ENABLED);
        Class cl = AuthenticationPlugin.class;
        when(pluginLoader.getPluginByClassName(cl)).thenReturn(authPlugin);
        when(authPlugin.authenticate(loginUserDto.getUserName(), passwordHash)).thenThrow(new UnexpectedErrorException());

        authenticator.authenticate(loginUserDto, httpRequest, httpResponse);
    }

    @Test
    public void registerUserWithCorrectDetailsShouldBeSuccessful() throws Exception {
        RegisterUserDto userDto = createRegisterUserDto("username", "password", "email@email.em", null);
        User commonUser = new User("username", "email@email.em", "password", null);
        when(registrationPlugin.getState()).thenReturn(Plugin.State.ENABLED);
        when(registrationPlugin.registerUser(userDto.getUserDto(), null)).thenReturn(Collections.EMPTY_MAP);
        when(pluginLoader.getPlugins(any(TypeFilter.class))).thenReturn(Arrays.asList((Plugin) registrationPlugin));
        when(bindingResult.hasErrors()).thenReturn(false);
        when(userDao.getCommonUserByUsername("username")).thenReturn(commonUser);

        authenticator.register(userDto);

        verify(bindingResult, never()).rejectValue(anyString(), anyString(), anyString());
    }

    @Test
    public void registerUserWithIncorrectDetailsShouldFail() throws Exception {
        RegisterUserDto userDto = createRegisterUserDto("", "", "", null);
        Map<String, String> errors = new HashMap<>();
        errors.put("userDto.email", "Invalid email length");
        errors.put("userDto.username", "Invalid username length");
        errors.put("userDto.password", "Invalid password length");

        RegistrationPlugin plugin = mock(RegistrationPlugin.class);
        when(plugin.getState()).thenReturn(Plugin.State.ENABLED);
        when(plugin.registerUser(userDto.getUserDto(), 1L)).thenReturn(errors);

        when(pluginService.getRegistrationPlugins()).thenReturn(
                new ImmutableMap.Builder<Long, RegistrationPlugin>().put(1L, plugin).build());

        when(bindingResult.hasErrors()).thenReturn(true);

        BindingResult result = authenticator.register(userDto);

        assertEquals(result.getFieldErrors().size(), 3);
    }

    @Test(expectedExceptions = NoConnectionException.class)
    public void registerUserShouldFailIfPluginThrowsNoConnectionException() throws Exception {
        RegisterUserDto userDto = createRegisterUserDto("username", "password", "email@email.em", null);
        RegistrationPlugin plugin = mock(RegistrationPlugin.class);
        when(plugin.getState()).thenReturn(Plugin.State.ENABLED);
        when(plugin.registerUser(userDto.getUserDto(), 1L))
                .thenThrow(new NoConnectionException());
        when(pluginService.getRegistrationPlugins()).thenReturn(
                new ImmutableMap.Builder<Long, RegistrationPlugin>().put(1L, plugin).build());

        when(bindingResult.hasErrors()).thenReturn(true);

        authenticator.register(userDto);
    }

    @Test(expectedExceptions = UnexpectedErrorException.class)
    public void registerUserShouldFailIfPluginThrowsUnexpectedErrorException() throws Exception {
        RegisterUserDto userDto = createRegisterUserDto("username", "password", "email@email.em", null);
        RegistrationPlugin plugin = mock(RegistrationPlugin.class);
        when(plugin.getState()).thenReturn(Plugin.State.ENABLED);
        when(plugin.registerUser(userDto.getUserDto(), 1L))
                .thenThrow(new UnexpectedErrorException());
        when(pluginService.getRegistrationPlugins()).thenReturn(
                new ImmutableMap.Builder<Long, RegistrationPlugin>().put(1L, plugin).build());

        authenticator.register(userDto);
    }

    @Test
    public void defaultRegistrationShouldFailIfValidationErrorsOccurred() throws Exception {
        RegisterUserDto userDto = createRegisterUserDto("username", "password", "email@email.em", null);
        when(pluginLoader.getPlugins(any(TypeFilter.class))).thenReturn(Collections.EMPTY_LIST);
        when(bindingResult.hasErrors()).thenReturn(true);

        authenticator.register(userDto);

        verify(bindingResult, never()).rejectValue(anyString(), anyString(), anyString());
    }

    @Test
    public void defaultRegistrationWithCorrectDetailsShouldBeSuccessful() throws Exception {
        RegisterUserDto userDto = createRegisterUserDto("username", "password", "email@email.em", null);
        when(pluginLoader.getPlugins(any(TypeFilter.class))).thenReturn(Collections.EMPTY_LIST);
        when(bindingResult.hasErrors()).thenReturn(false);

        authenticator.register(userDto);

        verify(bindingResult, never()).rejectValue(anyString(), anyString(), anyString());
    }

    private RegisterUserDto createRegisterUserDto(String username, String password, String email, String honeypotCaptcha) {
        RegisterUserDto registerUserDto = new RegisterUserDto();
        UserDto userDto = new UserDto();
        userDto.setUsername(username);
        userDto.setEmail(email);
        userDto.setPassword(password);
        registerUserDto.setUserDto(userDto);
        registerUserDto.setHoneypotCaptcha(honeypotCaptcha);
        return registerUserDto;
    }
    
    private JCUser getDefaultUser() {
        return new JCUser("user", "email@email.em", "password");
    }
    
    private LoginUserDto createDefaultLoginUserDto() {
        return new LoginUserDto("user", "password", true, "192.168.1.1");
    }
}
