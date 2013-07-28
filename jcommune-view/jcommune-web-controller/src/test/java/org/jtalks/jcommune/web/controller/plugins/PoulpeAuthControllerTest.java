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

package org.jtalks.jcommune.web.controller.plugins;

import com.google.common.collect.ImmutableMap;
import org.jtalks.jcommune.model.plugins.Plugin;
import org.jtalks.jcommune.model.plugins.SimpleAuthenticationPlugin;
import org.jtalks.jcommune.model.plugins.exceptions.NoConnectionException;
import org.jtalks.jcommune.model.plugins.exceptions.UnexpectedErrorException;
import org.jtalks.jcommune.service.UserService;
import org.jtalks.jcommune.service.nontransactional.EncryptionService;
import org.jtalks.jcommune.service.plugins.PluginLoader;
import org.jtalks.jcommune.service.plugins.TypeFilter;
import org.jtalks.jcommune.web.dto.RegisterUserDto;
import org.jtalks.jcommune.web.dto.json.JsonResponse;
import org.jtalks.jcommune.web.dto.json.JsonResponseStatus;
import org.mockito.Mock;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.*;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.testng.Assert.assertEquals;

/**
 * @author Andrey Pogorelov
 */
public class PoulpeAuthControllerTest {

    private static final String USERNAME = "username";
    private static final String EMAIL = "username@mail.com";
    private static final String PASSWORD = "password";
    private static final String PASSWORD_MD5_HASH = "5f4dcc3b5aa765d61d8327deb882cf99";

    @Mock
    private PluginLoader pluginLoader;
    @Mock
    private EncryptionService encryptionService;
    @Mock
    private UserService userService;
    @Mock
    private BindingResult bindingResult;
    @Mock
    SimpleAuthenticationPlugin plugin;

    private PoulpeAuthController controller;

    @BeforeMethod
    public void setUp() throws Exception {
        initMocks(this);
        controller = new PoulpeAuthController(pluginLoader, encryptionService, userService);
    }

    private RegisterUserDto createUserDto(String username, String password, String email) {
        RegisterUserDto dto = new RegisterUserDto();
        dto.setUsername(username);
        dto.setEmail(email);
        dto.setPassword(password);
        dto.setPasswordConfirm(password);
        return dto;
    }

    private void prepareMocks()
            throws UnexpectedErrorException, NoConnectionException {
        when(pluginLoader.getPlugins(any(TypeFilter.class))).thenReturn(Arrays.<Plugin>asList(plugin));
        when(plugin.getState()).thenReturn(Plugin.State.ENABLED);
        when(encryptionService.encryptPassword(PASSWORD)).thenReturn(PASSWORD_MD5_HASH);
    }

    @Test
    public void registerUserFail() throws UnexpectedErrorException, NoConnectionException {
        List<Map<String, String>> errors = new ArrayList<>();
        errors.add(new ImmutableMap.Builder<String, String>().put("user.email.illegal_length", "").build());
        errors.add(new ImmutableMap.Builder<String, String>().put("user.username.already_exists", "").build());
        errors.add(new ImmutableMap.Builder<String, String>()
                .put("user.password.length_constraint_violation", "").build());
        prepareMocks();
        when(plugin.registerUser(USERNAME, "", EMAIL)).thenReturn(errors);
        when(bindingResult.hasErrors()).thenReturn(true);
        RegisterUserDto userDto = createUserDto(USERNAME, "", EMAIL);

        ModelAndView result = controller.registerUser(userDto, bindingResult, new Locale("en"));

        assertEquals(result.getViewName(), PoulpeAuthController.REGISTRATION,
                "User with validation errors should fail registration.");
    }

    @Test
    public void registerUserSuccess() throws UnexpectedErrorException, NoConnectionException {
        List<Map<String, String>> errors = new ArrayList<>();
        prepareMocks();
        when(plugin.registerUser(USERNAME, PASSWORD, EMAIL)).thenReturn(errors);
        when(bindingResult.hasErrors()).thenReturn(false);
        RegisterUserDto userDto = createUserDto(USERNAME, PASSWORD, EMAIL);

        ModelAndView result = controller.registerUser(userDto, bindingResult, new Locale("en"));

        assertEquals(result.getViewName(), PoulpeAuthController.AFTER_REGISTRATION,
                "User without validation errors should pass registration.");
    }

    @Test
    public void registerUserThrowsUnexpectedError() throws UnexpectedErrorException, NoConnectionException {
        prepareMocks();
        when(plugin.registerUser(USERNAME, PASSWORD_MD5_HASH, EMAIL)).thenThrow(new UnexpectedErrorException());
        RegisterUserDto userDto = createUserDto(USERNAME, PASSWORD, EMAIL);

        ModelAndView result = controller.registerUser(userDto, bindingResult, new Locale("en"));

        assertEquals(result.getViewName(), PoulpeAuthController.UNEXPECTED_ERROR_URL,
                "Unexpected error should fail registration.");
    }

    @Test
    public void registerUserThrowsConnectionError() throws UnexpectedErrorException, NoConnectionException {
        prepareMocks();
        when(plugin.registerUser(USERNAME, PASSWORD_MD5_HASH, EMAIL)).thenThrow(new NoConnectionException());
        RegisterUserDto userDto = createUserDto(USERNAME, PASSWORD, EMAIL);

        ModelAndView result = controller.registerUser(userDto, bindingResult, new Locale("en"));

        assertEquals(result.getViewName(), PoulpeAuthController.CONNECTION_ERROR_URL,
                "Connection error should fail registration.");
    }

    @Test
    public void registerUserAjaxFail() throws UnexpectedErrorException, NoConnectionException {
        List<Map<String, String>> errors = new ArrayList<>();
        errors.add(new ImmutableMap.Builder<String, String>().put("user.email.illegal_length", "").build());
        errors.add(new ImmutableMap.Builder<String, String>().put("user.username.already_exists", "").build());
        errors.add(new ImmutableMap.Builder<String, String>()
                .put("user.password.length_constraint_violation", "").build());
        prepareMocks();
        when(plugin.registerUser(USERNAME, "", EMAIL)).thenReturn(errors);
        when(bindingResult.hasErrors()).thenReturn(true);
        RegisterUserDto userDto = createUserDto(USERNAME, "", EMAIL);

        JsonResponse result = controller.registerUserAjax(userDto, bindingResult, new Locale("en"));

        assertEquals(result.getStatus(), JsonResponseStatus.FAIL,
                "User with validation errors should fail registration.");
    }

    @Test
    public void registerUserAjaxSuccess() throws UnexpectedErrorException, NoConnectionException {
        List<Map<String, String>> errors = new ArrayList<>();
        prepareMocks();
        when(plugin.registerUser(USERNAME, PASSWORD, EMAIL)).thenReturn(errors);
        when(bindingResult.hasErrors()).thenReturn(false);
        RegisterUserDto userDto = createUserDto(USERNAME, PASSWORD, EMAIL);

        JsonResponse result = controller.registerUserAjax(userDto, bindingResult, new Locale("en"));

        assertEquals(result.getStatus(), JsonResponseStatus.SUCCESS,
                "User without validation errors should pass registration.");
    }

    @Test
    public void registerUserAjaxThrowsUnexpectedError() throws UnexpectedErrorException, NoConnectionException {
        prepareMocks();
        when(plugin.registerUser(USERNAME, PASSWORD_MD5_HASH, EMAIL)).thenThrow(new UnexpectedErrorException());
        RegisterUserDto userDto = createUserDto(USERNAME, PASSWORD, EMAIL);

        JsonResponse result = controller.registerUserAjax(userDto, bindingResult, new Locale("en"));

        assertEquals(result.getStatus(), JsonResponseStatus.FAIL, "Unexpected error should fail registration.");
    }

    @Test
    public void registerUserAjaxThrowsConnectionError() throws UnexpectedErrorException, NoConnectionException {
        prepareMocks();
        when(plugin.registerUser(USERNAME, PASSWORD_MD5_HASH, EMAIL)).thenThrow(new NoConnectionException());
        RegisterUserDto userDto = createUserDto(USERNAME, PASSWORD, EMAIL);

        JsonResponse result = controller.registerUserAjax(userDto, bindingResult, new Locale("en"));

        assertEquals(result.getStatus(), JsonResponseStatus.FAIL, "Connection error should fail registration.");
    }


}
