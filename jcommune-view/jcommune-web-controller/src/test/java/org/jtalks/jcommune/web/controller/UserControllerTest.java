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

import org.jtalks.common.service.security.SecurityContextHolderFacade;
import org.jtalks.jcommune.model.entity.AnonymousUser;
import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.service.UserService;
import org.jtalks.jcommune.service.exceptions.MailingFailedException;
import org.jtalks.jcommune.service.exceptions.NotFoundException;
import org.jtalks.jcommune.web.dto.JsonResponse;
import org.jtalks.jcommune.web.dto.RegisterUserDto;
import org.jtalks.jcommune.web.dto.RestorePasswordDto;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.servlet.ModelAndView;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
    private AuthenticationManager authenticationManager;
    private SecurityContextHolderFacade securityFacade;
    private RememberMeServices rememberMeServices;
    private SecurityContext securityContext;
    private SessionAuthenticationStrategy sessionStrategy;
    
    @BeforeMethod
    public void setUp() throws IOException {
        userService = mock(UserService.class);
        authenticationManager = mock(AuthenticationManager.class);
        securityFacade = mock(SecurityContextHolderFacade.class);
        securityContext = mock(SecurityContext.class);
        when(securityFacade.getContext()).thenReturn(securityContext);
        rememberMeServices = mock(RememberMeServices.class);
        sessionStrategy = mock(SessionAuthenticationStrategy.class);

        userController = new UserController(userService, authenticationManager, 
                securityFacade, rememberMeServices, sessionStrategy);
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

        ModelAndView mav = userController.registerUser(dto, bindingResult, new Locale("ru"));

        assertViewName(mav, "redirect:/");
        verify(userService).registerUser(any(JCUser.class));
    }

    @Test
    public void testRegisterValidationFail() {
        RegisterUserDto dto = getRegisterUserDto();
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(true);

        ModelAndView mav = userController.registerUser(dto, bindingResult, Locale.ENGLISH);

        assertViewName(mav, "registration");
    }
    
    @Test 
    void testRegisterAjax() {
    	RegisterUserDto dto = getRegisterUserDto();
        BindingResult bindingResult = new BeanPropertyBindingResult(dto, "newUser");

        JsonResponse jsonResponse = userController.registerUserAjax(dto, bindingResult, new Locale("ru"));
        
        assertEquals(jsonResponse.getStatus(), "success");
        assertNull(jsonResponse.getResult());
        verify(userService).registerUser(any(JCUser.class));
    }

    @Test 
    void testRegisterAjaxFail() {
        RegisterUserDto dto = getRegisterUserDto();
        List<ObjectError> errors = new ArrayList<ObjectError>();
        ObjectError error = new ObjectError("username", "bullshit");
		errors.add(error);
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(true);
        when(bindingResult.getAllErrors()).thenReturn(errors);
        JsonResponse jsonResponse = userController.registerUserAjax(dto, bindingResult, new Locale("ru"));
        
        assertEquals(jsonResponse.getStatus(), "fail");
        ObjectError objectError = ((List<ObjectError>) jsonResponse.getResult()).get(0);
		assertEquals(objectError, error);
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
    
    @Test
    public void testAjaxLoginSuccess() throws Exception {
    	String username = "username";
    	HttpServletRequest httpRequest = new MockHttpServletRequest();
		HttpServletResponse httpResponse = new MockHttpServletResponse();
		UsernamePasswordAuthenticationToken expectedToken = mock(UsernamePasswordAuthenticationToken.class);
		when(expectedToken.isAuthenticated()).thenReturn(true);
		when(userService.getByUsername(username)).thenReturn(new JCUser(username, null, null));
		when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(expectedToken);
		
		JsonResponse response = userController.loginAjax(username, 
    			PASSWORD, "off", httpRequest, httpResponse);
		
		assertEquals(response.getStatus(), "success");
		verify(userService).getByUsername(username);
		verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
		verify(securityContext).setAuthentication(expectedToken);
		verify(rememberMeServices, never()).loginSuccess(any(HttpServletRequest.class), any(HttpServletResponse.class), any(Authentication.class));
		verify(sessionStrategy).onAuthentication(any(Authentication.class), 
		        any(HttpServletRequest.class), any(HttpServletResponse.class));
    }
    
    @Test
    public void testAjaxLoginSuccessWithRememberMe() throws Exception {
    	String username = "username";
    	HttpServletRequest httpRequest = new MockHttpServletRequest();
		HttpServletResponse httpResponse = new MockHttpServletResponse();
		UsernamePasswordAuthenticationToken expectedToken = mock(UsernamePasswordAuthenticationToken.class);
		when(expectedToken.isAuthenticated()).thenReturn(true);
		when(userService.getByUsername(username)).thenReturn(new JCUser(username, null, null));
		when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(expectedToken);
		
		JsonResponse response = userController.loginAjax(username, 
    			PASSWORD, "on", httpRequest, httpResponse);
		
		assertEquals(response.getStatus(), "success");
		verify(userService).getByUsername(username);
		verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
		verify(securityContext).setAuthentication(expectedToken);
		verify(rememberMeServices).loginSuccess(eq(httpRequest), eq(httpResponse), any(UsernamePasswordAuthenticationToken.class));
		verify(sessionStrategy).onAuthentication(any(Authentication.class), 
		        any(HttpServletRequest.class), any(HttpServletResponse.class));
    }
    
    @Test
    public void testAjaxLoginFailUserNotFound() throws Exception {
    	String username = "username";
    	HttpServletRequest httpRequest = new MockHttpServletRequest();
		HttpServletResponse httpResponse = new MockHttpServletResponse();
		when(userService.getByUsername(username)).thenReturn(new JCUser(username, null, null));
		UsernamePasswordAuthenticationToken expectedToken = mock(UsernamePasswordAuthenticationToken.class);
		when(expectedToken.isAuthenticated()).thenReturn(false);
		when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(expectedToken);
		
		JsonResponse response = userController.loginAjax(username, 
    			PASSWORD, "on", httpRequest, httpResponse);
		
    	assertEquals(response.getStatus(), "fail");
    	verify(userService).getByUsername(username);
    	verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    	verify(rememberMeServices, never()).loginSuccess(any(HttpServletRequest.class), any(HttpServletResponse.class), any(Authentication.class));
    }
    
    @Test
    public void testAjaxLoginFailIncorrectPassword() throws Exception {
    	String username = "username";
    	HttpServletRequest httpRequest = new MockHttpServletRequest();
		HttpServletResponse httpResponse = new MockHttpServletResponse();
    	when(userService.getByUsername(username)).thenThrow(new NotFoundException());
    	
		JsonResponse response = userController.loginAjax(username, 
    			PASSWORD, "on", httpRequest, httpResponse);
		
    	assertEquals(response.getStatus(), "fail");
    	verify(userService).getByUsername(username);
    	verify(rememberMeServices, never()).loginSuccess(any(HttpServletRequest.class), any(HttpServletResponse.class), any(Authentication.class));
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
