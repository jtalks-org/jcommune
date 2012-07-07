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

import org.jtalks.jcommune.model.entity.AnonymousUser;
import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.service.UserService;
import org.jtalks.jcommune.service.exceptions.MailingFailedException;
import org.jtalks.jcommune.service.exceptions.NotFoundException;
import org.jtalks.jcommune.web.dto.RegisterUserDto;
import org.jtalks.jcommune.web.dto.RestorePasswordDto;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.servlet.ModelAndView;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.ModelAndViewAssert.*;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

/**
 * @author Evgeniy Naumenko
 */
public class UserControllerTest {

    private final String USER_NAME = "username";
    private final String FIRST_NAME = "first name";
    private final String LAST_NAME = "last name";
    private final String EMAIL = "mail@mail.com";
    private final String PASSWORD = "password";


    private UserController userController;
    private UserService userService;

    @BeforeMethod
    public void setUp() throws IOException {
        userService = mock(UserService.class);
        userController = new UserController(userService);
    }

    @Test
    public void testRegistrationPage() throws Exception {
        ModelAndView mav = userController.registrationPage();

        assertViewName(mav, "registration");
        RegisterUserDto dto = assertAndReturnModelAttributeOfType(mav, "newUser", RegisterUserDto.class);
        assertNullFields(dto);
    }

    @Test
    public void testRegisterUser() throws Exception {
        RegisterUserDto dto = getRegisterUserDto();
        BindingResult bindingResult = new BeanPropertyBindingResult(dto, "newUser");

        ModelAndView mav = userController.registerUser(dto, bindingResult);

        assertViewName(mav, "redirect:/");
        verify(userService).registerUser(any(JCUser.class));
    }

    @Test
    public void testRegisterValidationFail() {
        RegisterUserDto dto = getRegisterUserDto();
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(true);

        ModelAndView mav = userController.registerUser(dto, bindingResult);

        assertViewName(mav, "registration");
    }

    @Test
    public void testRestorePasswordPage() {
        assertViewName(userController.showRestorePasswordPage(), "restorePassword");
    }

    @Test
    public void testRestorePassword() throws IOException, NotFoundException, MailingFailedException {
        RestorePasswordDto dto = new RestorePasswordDto();
        dto.setEmail(EMAIL);
        BindingResult bindingResult = new BeanPropertyBindingResult(dto, "email");
        ModelAndView mav = userController.restorePassword(dto, bindingResult);
        verify(userService, times(1)).restorePassword(EMAIL);
        assertModelAttributeValue(mav, "message", "label.restorePassword.completed");
    }

    @Test
    public void testRestorePasswordWrongMail() throws IOException, NotFoundException, MailingFailedException {
        RestorePasswordDto dto = new RestorePasswordDto();
        dto.setEmail(EMAIL);
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
        dto.setEmail(EMAIL);
        BindingResult bindingResult = new BeanPropertyBindingResult(dto, "email");
        ModelAndView mav = userController.restorePassword(dto, bindingResult);
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

    private void assertNullFields(RegisterUserDto dto) {
        assertNull(dto.getEmail());
        assertNull(dto.getUsername());
        assertNull(dto.getPassword());
        assertNull(dto.getPasswordConfirm());
    }

    private RegisterUserDto getRegisterUserDto() {
        RegisterUserDto dto = new RegisterUserDto();
        dto.setUsername(USER_NAME);
        dto.setEmail(EMAIL);
        dto.setPassword(PASSWORD);
        dto.setPasswordConfirm(PASSWORD);
        return dto;
    }
}
