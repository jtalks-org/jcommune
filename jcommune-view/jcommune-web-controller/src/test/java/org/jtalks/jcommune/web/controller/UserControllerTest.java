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
package org.jtalks.jcommune.web.controller;

import com.google.common.collect.Lists;
import org.jtalks.common.service.security.SecurityContextHolderFacade;
import org.jtalks.jcommune.model.dto.RegisterUserDto;
import org.jtalks.jcommune.model.dto.UserDto;
import org.jtalks.jcommune.model.entity.AnonymousUser;
import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.model.plugins.exceptions.NoConnectionException;
import org.jtalks.jcommune.model.plugins.exceptions.UnexpectedErrorException;
import org.jtalks.jcommune.service.Authenticator;
import org.jtalks.jcommune.service.UserService;
import org.jtalks.jcommune.service.exceptions.MailingFailedException;
import org.jtalks.jcommune.service.exceptions.NotFoundException;
import org.jtalks.jcommune.web.dto.RestorePasswordDto;
import org.jtalks.jcommune.web.dto.json.JsonResponse;
import org.jtalks.jcommune.web.dto.json.JsonResponseStatus;
import org.jtalks.jcommune.web.validation.editors.DefaultStringEditor;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.servlet.ModelAndView;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.ModelAndViewAssert.*;
import static org.testng.Assert.*;

/**
 * @author Evgeniy Naumenko
 * @author Andrey Pogorelov
 */
public class UserControllerTest {
    private final String USER_NAME = "username";
    private final String EMAIL = "mail@mail.com";
    private UserController userController;
    private UserService userService;
    private Authenticator authenticator;

    @BeforeMethod
    public void setUp() throws IOException {
        userService = mock(UserService.class);
        authenticator = mock(Authenticator.class);
        SecurityContextHolderFacade securityFacade = mock(SecurityContextHolderFacade.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityFacade.getContext()).thenReturn(securityContext);

        userController = new UserController(userService, authenticator);
    }

    @Test
    public void testInitBinderStringTrimmerEditor() {
        WebDataBinder binder = mock(WebDataBinder.class);

        userController.initBinder(binder);

        verify(binder).registerCustomEditor(eq(String.class), any(StringTrimmerEditor.class));
    }

    @Test
    public void testInitBinderDefaultStringEditor() {
        WebDataBinder binder = mock(WebDataBinder.class);

        userController.initBinder(binder);

        verify(binder).registerCustomEditor(eq(String.class), any(DefaultStringEditor.class));
    }

    @Test
    public void testRegistrationPage() throws Exception {
        ModelAndView mav = userController.registrationPage();

        assertViewName(mav, "registration");
        RegisterUserDto dto = assertAndReturnModelAttributeOfType(mav, "newUser", RegisterUserDto.class);
        assertNullFields(dto);
    }

    @Test
    public void testRegisterUserShouldBeSuccessful() throws Exception {
        RegisterUserDto dto = createRegisterUserDto();
        BindingResult bindingResult = new BeanPropertyBindingResult(dto, "newUser");
        when(authenticator.register(dto)).thenReturn(bindingResult);

        ModelAndView mav = userController.registerUser(dto, Locale.ENGLISH);

        assertViewName(mav, "afterRegistration");
        verify(authenticator).register(dto);
    }

    @Test
    public void testRegisterValidationFail() throws UnexpectedErrorException, NotFoundException, NoConnectionException {
        RegisterUserDto dto = createRegisterUserDto();
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(true);
        when(authenticator.register(dto)).thenReturn(bindingResult);

        ModelAndView mav = userController.registerUser(dto, Locale.ENGLISH);

        assertViewName(mav, "registration");
    }

    @Test
    public void testRegisterFailIfUnexpectedErrorOccurred()
            throws UnexpectedErrorException, NotFoundException, NoConnectionException {
        RegisterUserDto dto = createRegisterUserDto();
        doThrow(new UnexpectedErrorException()).when(authenticator)
                .register(dto);

        ModelAndView mav = userController.registerUser(dto, Locale.ENGLISH);

        assertViewName(mav, UserController.REG_SERVICE_UNEXPECTED_ERROR_URL);
    }

    @Test
    public void testRegisterFailIfConnectionErrorOccurred()
            throws UnexpectedErrorException, NotFoundException, NoConnectionException {
        RegisterUserDto dto = createRegisterUserDto();
        doThrow(new NoConnectionException()).when(authenticator)
                .register(dto);

        ModelAndView mav = userController.registerUser(dto, Locale.ENGLISH);

        assertViewName(mav, UserController.REG_SERVICE_CONNECTION_ERROR_URL);
    }

    @Test
    public void testRegisterUserAjaxShouldBeSuccessful() throws Exception {
        RegisterUserDto dto = createRegisterUserDto();
        BindingResult bindingResult = new BeanPropertyBindingResult(dto, "newUser");
        when(authenticator.register(dto)).thenReturn(bindingResult);

        JsonResponse response = userController.registerUserAjax(dto, Locale.ENGLISH);

        assertEquals(response.getStatus(), JsonResponseStatus.SUCCESS,
                "User without validation errors should pass registration.");
    }

    @Test
    public void testRegisterAjaxValidationFail() throws UnexpectedErrorException, NotFoundException, NoConnectionException {
        RegisterUserDto dto = createRegisterUserDto();
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(true);
        when(authenticator.register(dto)).thenReturn(bindingResult);

        JsonResponse response = userController.registerUserAjax(dto, Locale.ENGLISH);

        assertEquals(response.getStatus(), JsonResponseStatus.FAIL,
                "User with validation errors should fail registration.");
    }

    @Test
    public void testRegisterAjaxFailIfUnexpectedErrorOccurred()
            throws UnexpectedErrorException, NotFoundException, NoConnectionException {
        RegisterUserDto dto = createRegisterUserDto();
        doThrow(new UnexpectedErrorException()).when(authenticator).register(dto);

        JsonResponse response = userController.registerUserAjax(dto, Locale.ENGLISH);

        assertEquals(response.getStatus(), JsonResponseStatus.FAIL, "Unexpected error should fail registration.");
    }

    @Test
    public void testRegisterAjaxFailIfConnectionErrorOccurred()
            throws UnexpectedErrorException, NotFoundException, NoConnectionException {
        RegisterUserDto dto = createRegisterUserDto();
        doThrow(new NoConnectionException()).when(authenticator).register(dto);

        JsonResponse response = userController.registerUserAjax(dto, Locale.ENGLISH);

        assertEquals(response.getStatus(), JsonResponseStatus.FAIL, "Connection error should fail registration.");
    }

    @Test
    public void testRestorePasswordPage() {
        assertViewName(userController.showRestorePasswordPage(), "restorePassword");
    }

    @Test
    public void testRestorePassword() throws IOException, NotFoundException, MailingFailedException {
        RestorePasswordDto dto = new RestorePasswordDto();
        dto.setUserEmail(EMAIL);
        BindingResult bindingResult = new BeanPropertyBindingResult(dto, "email");
        ModelAndView mav = userController.restorePassword(dto, bindingResult);
        verify(userService, times(1)).restorePassword(EMAIL);
        assertModelAttributeValue(mav, "message", "label.restorePassword.completed");
    }

    @Test
    public void testRestorePasswordWrongMail() throws IOException, NotFoundException, MailingFailedException {
        RestorePasswordDto dto = new RestorePasswordDto();
        dto.setUserEmail(EMAIL);
        BindingResult bindingResult = new BeanPropertyBindingResult(dto, "email");
        bindingResult.addError(new FieldError("", "", ""));
        ModelAndView mav = userController.restorePassword(dto, bindingResult);
        verifyZeroInteractions(userService);
        assertViewName(mav, "restorePassword");
    }

    @Test
    public void testRestorePasswordFail() throws NotFoundException, MailingFailedException {
        Exception fail = new MailingFailedException(new RuntimeException());
        doThrow(fail).when(userService).restorePassword(anyString());
        RestorePasswordDto dto = new RestorePasswordDto();
        dto.setUserEmail(EMAIL);
        BindingResult bindingResult = new BeanPropertyBindingResult(dto, "email");
        userController.restorePassword(dto, bindingResult);
        verify(userService, times(1)).restorePassword(EMAIL);
        assertTrue(bindingResult.hasErrors());
    }

    @Test
    public void testActivateAccount() throws NotFoundException {
        String viewName = userController.activateAccount(USER_NAME);

        verify(userService, times(1)).activateAccount(USER_NAME);
        assertEquals("redirect:/login", viewName);
    }

    @Test
    public void testActivateAccountFail() throws NotFoundException {
        doThrow(new NotFoundException()).when(userService).activateAccount(anyString());

        String viewName = userController.activateAccount(USER_NAME);

        assertEquals("errors/activationExpired", viewName);
    }

    @Test
    public void testLoginUserLogged() {
        when(userService.getCurrentUser()).thenReturn(new JCUser("username", null, null));

        String result = userController.loginPage();

        assertEquals(result, "redirect:/");
        verify(userService).getCurrentUser();
    }

    @Test
    public void testLoginUserNotLogged() {
        when(userService.getCurrentUser()).thenReturn(new AnonymousUser());

        String result = userController.loginPage();

        assertEquals(result, UserController.LOGIN);
        verify(userService).getCurrentUser();
    }

    @Test
    public void testAjaxLoginSuccess() throws Exception {
        when(userService.loginUser(anyString(), anyString(), anyBoolean(),
                any(HttpServletRequest.class), any(HttpServletResponse.class)))
                .thenReturn(true);

        JsonResponse response = userController.loginAjax(null, null, "on", null, null);
        assertEquals(response.getStatus(), JsonResponseStatus.SUCCESS);
        verify(userService).loginUser(anyString(), anyString(), eq(true),
                any(HttpServletRequest.class), any(HttpServletResponse.class));
    }

    @Test
    public void testAjaxLoginFailure() throws Exception {
        when(userService.loginUser(anyString(), anyString(), anyBoolean(),
                any(HttpServletRequest.class), any(HttpServletResponse.class)))
                .thenReturn(false);

        JsonResponse response = userController.loginAjax(null, null, "off", null, null);
        assertEquals(response.getStatus(), JsonResponseStatus.FAIL);
        verify(userService).loginUser(anyString(), anyString(), eq(false),
                any(HttpServletRequest.class), any(HttpServletResponse.class));
    }

    @Test
    public void testLoginAjaxUserShouldFailIfConnectionErrorOccurred() throws Exception {
        when(userService.loginUser(anyString(), anyString(), anyBoolean(),
                any(HttpServletRequest.class), any(HttpServletResponse.class)))
                .thenThrow(new NoConnectionException());

        JsonResponse response = userController.loginAjax(null, null, "off", null, null);
        assertEquals(response.getStatus(), JsonResponseStatus.FAIL);
        verify(userService).loginUser(anyString(), anyString(), eq(false),
                any(HttpServletRequest.class), any(HttpServletResponse.class));
    }

    @Test
    public void testLoginAjaxUserShouldFailIfUnexpectedErrorOccurred() throws Exception {
        when(userService.loginUser(anyString(), anyString(), anyBoolean(),
                any(HttpServletRequest.class), any(HttpServletResponse.class)))
                .thenThrow(new UnexpectedErrorException());

        JsonResponse response = userController.loginAjax(null, null, "off", null, null);
        assertEquals(response.getStatus(), JsonResponseStatus.FAIL);
        verify(userService).loginUser(anyString(), anyString(), eq(false),
                any(HttpServletRequest.class), any(HttpServletResponse.class));
    }

    @Test
    public void testLoginWithCorrectParametersShouldBeSuccessful() throws Exception {
        when(userService.loginUser(eq("user1"), eq("password"), anyBoolean(),
                any(HttpServletRequest.class), any(HttpServletResponse.class)))
                .thenReturn(true);

        ModelAndView view = userController.login("user1", "password", "off", null, null);

        assertEquals(view.getViewName(), "redirect:/");
        verify(userService).loginUser(eq("user1"), eq("password"), eq(false),
                any(HttpServletRequest.class), any(HttpServletResponse.class));
    }

    @Test
    public void testLoginWithIncorrectParametersShouldFail() throws Exception {
        when(userService.loginUser(anyString(), anyString(), anyBoolean(),
                any(HttpServletRequest.class), any(HttpServletResponse.class)))
                .thenReturn(false);

        ModelAndView view = userController.login(null, null, "off", null, null);

        assertEquals(view.getViewName(), UserController.AUTH_FAIL_URL);
        verify(userService).loginUser(anyString(), anyString(), eq(false),
                any(HttpServletRequest.class), any(HttpServletResponse.class));
    }

    @Test
    public void testLoginUserShouldFailIfConnectionErrorOccurred() throws Exception {
        when(userService.loginUser(anyString(), anyString(), anyBoolean(),
                any(HttpServletRequest.class), any(HttpServletResponse.class)))
                .thenThrow(new NoConnectionException());

        ModelAndView view = userController.login(null, null, "off", null, null);

        assertEquals(view.getViewName(), UserController.AUTH_SERVICE_FAIL_URL);
        verify(userService).loginUser(anyString(), anyString(), eq(false),
                any(HttpServletRequest.class), any(HttpServletResponse.class));
    }

    @Test
    public void testLoginUserShouldFailIfUnexpectedErrorOccurred() throws Exception {
        when(userService.loginUser(anyString(), anyString(), anyBoolean(),
                any(HttpServletRequest.class), any(HttpServletResponse.class)))
                .thenThrow(new UnexpectedErrorException());

        ModelAndView view = userController.login(null, null, "off", null, null);

        assertEquals(view.getViewName(), UserController.AUTH_SERVICE_FAIL_URL);
        verify(userService).loginUser(anyString(), anyString(), eq(false),
                any(HttpServletRequest.class), any(HttpServletResponse.class));
    }

    @Test
    public void testGetUsernameListSuccess(){
        String pattern = "us";
        List<String> usernames = Lists.newArrayList("User1", "User2", "User3");
        when(userService.getUsernames(pattern)).thenReturn(usernames);

        JsonResponse response = userController.usernameList(pattern);
        assertEquals(response.getStatus(), JsonResponseStatus.SUCCESS);
    }

    private void assertNullFields(RegisterUserDto dto) {
        assertNull(dto.getUserDto());
        assertNull(dto.getPasswordConfirm());
    }

    private RegisterUserDto createRegisterUserDto() {
        UserDto userDto = new UserDto();
        userDto.setUsername(USER_NAME);
        userDto.setEmail(EMAIL);
        userDto.setPassword("password");
        RegisterUserDto dto = new RegisterUserDto();
        dto.setPasswordConfirm("password");
        dto.setUserDto(userDto);
        return dto;
    }
}
