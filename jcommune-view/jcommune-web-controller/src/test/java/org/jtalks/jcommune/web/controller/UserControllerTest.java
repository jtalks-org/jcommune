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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import org.jtalks.common.model.entity.Component;
import org.jtalks.common.service.security.SecurityContextHolderFacade;
import org.jtalks.jcommune.model.dto.LoginUserDto;
import org.jtalks.jcommune.model.dto.RegisterUserDto;
import org.jtalks.jcommune.model.dto.UserDto;
import org.jtalks.jcommune.model.entity.AnonymousUser;
import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.plugin.api.core.ExtendedPlugin;
import org.jtalks.jcommune.plugin.api.core.RegistrationPlugin;
import org.jtalks.jcommune.plugin.api.exceptions.NoConnectionException;
import org.jtalks.jcommune.plugin.api.exceptions.NotFoundException;
import org.jtalks.jcommune.plugin.api.exceptions.UnexpectedErrorException;
import org.jtalks.jcommune.plugin.api.filters.TypeFilter;
import org.jtalks.jcommune.plugin.api.web.dto.json.JsonResponse;
import org.jtalks.jcommune.plugin.api.web.dto.json.JsonResponseStatus;
import org.jtalks.jcommune.service.Authenticator;
import org.jtalks.jcommune.service.ComponentService;
import org.jtalks.jcommune.service.PluginService;
import org.jtalks.jcommune.service.UserService;
import org.jtalks.jcommune.service.exceptions.MailingFailedException;
import org.jtalks.jcommune.service.exceptions.UserTriesActivatingAccountAgainException;
import org.jtalks.jcommune.service.nontransactional.MailService;
import org.jtalks.jcommune.service.util.AuthenticationStatus;
import org.jtalks.jcommune.web.dto.RestorePasswordDto;
import org.jtalks.jcommune.web.util.MutableHttpRequest;
import org.jtalks.jcommune.web.validation.editors.DefaultStringEditor;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.retry.policy.NeverRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.servlet.ModelAndView;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
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
    private MailService mailService;
    private PluginService pluginService;
    private Authenticator authenticator;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private RetryTemplate retryTemplate;
    private ComponentService componentService;

    @BeforeMethod
    public void setUp() throws IOException {
        userService = mock(UserService.class);
        mailService = mock(MailService.class);
        pluginService = mock(PluginService.class);
        authenticator = mock(Authenticator.class);
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        componentService = mock(ComponentService.class);
        retryTemplate = new RetryTemplate();
        retryTemplate.setRetryPolicy(new NeverRetryPolicy());
        SecurityContextHolderFacade securityFacade = mock(SecurityContextHolderFacade.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityFacade.getContext()).thenReturn(securityContext);
        when(request.getHeader("X-FORWARDED-FOR")).thenReturn("192.168.1.1");
        userController = new UserController(userService, authenticator, pluginService, userService,
                mailService, retryTemplate, componentService);
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
    public void userMustBeAbleToOpenRegistrationPage_ifHeIsNotLoggedIn() throws Exception {
        when(userService.getCurrentUser()).thenReturn(new AnonymousUser());
        ModelAndView mav = userController.registrationPage(null, null);

        assertViewName(mav, "registration");
        RegisterUserDto dto = assertAndReturnModelAttributeOfType(mav, "newUser", RegisterUserDto.class);
        assertNullFields(dto);
    }

    @Test
    public void whenOpeningRegistrationPage_userMustBeRedirectedToMainPage_ifHeIsLoggedIn() throws Exception {
        when(userService.getCurrentUser()).thenReturn(new JCUser("username", null, null));
        ModelAndView mav = userController.registrationPage(null, null);

        assertViewName(mav, "redirect:/");
    }

    @Test
    public void testRegistrationFormWithoutAnyPluginShouldBeSuccessful() {
        JsonResponse response = userController.registrationForm(request, Locale.ENGLISH);

        assertEquals(response.getStatus(), JsonResponseStatus.SUCCESS);
    }

    @Test
    public void testRegistrationFormWithAvailablePluginShouldBeSuccessful() {
        RegistrationPlugin plugin = mock(RegistrationPlugin.class);
        when(pluginService.getRegistrationPlugins()).thenReturn(
                new ImmutableMap.Builder<Long, RegistrationPlugin>().put(1L, plugin).build());

        JsonResponse response = userController.registrationForm(request, Locale.ENGLISH);

        assertEquals(response.getStatus(), JsonResponseStatus.SUCCESS);
    }

    @Test
    public void testPluginActionForAvailablePluginShouldBeSuccessful()
            throws org.jtalks.common.service.exceptions.NotFoundException {
        ExtendedPlugin plugin = mock(ExtendedPlugin.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpServletRequest request = mock(HttpServletRequest.class);
        String pluginId = "1";
        when(pluginService.getPluginById(eq(pluginId), any(TypeFilter.class))).thenReturn(plugin);

        userController.pluginAction(pluginId, "someAction", request, response);
    }

    @Test
    public void testPluginActionIfPluginNotFound() throws org.jtalks.common.service.exceptions.NotFoundException {
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpServletRequest request = mock(HttpServletRequest.class);
        String pluginId = "1";
        when(pluginService.getPluginById(eq(pluginId), any(TypeFilter.class)))
                .thenThrow(new org.jtalks.common.service.exceptions.NotFoundException());

        userController.pluginAction(pluginId, "someAction", request, response);
    }

    @Test
    public void testRegisterUserShouldBeSuccessful() throws Exception {
        RegisterUserDto dto = createRegisterUserDto(null);
        BindingResult bindingResult = new BeanPropertyBindingResult(dto, "newUser");
        when(authenticator.register(dto)).thenReturn(bindingResult);

        ModelAndView mav = userController.registerUser(dto, request, Locale.ENGLISH);

        assertViewName(mav, "afterRegistration");
        verify(authenticator).register(dto);
    }

    @Test
    public void testRegisterValidationFail() throws Exception {
        RegisterUserDto dto = createRegisterUserDto(null);
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(true);
        when(authenticator.register(dto)).thenReturn(bindingResult);

        ModelAndView mav = userController.registerUser(dto, request, Locale.ENGLISH);

        assertViewName(mav, "registration");
    }

    @Test
    public void testRegisterFailIfUnexpectedErrorOccurred() throws Exception {
        RegisterUserDto dto = createRegisterUserDto(null);
        doThrow(new UnexpectedErrorException()).when(authenticator).register(dto);

        ModelAndView mav = userController.registerUser(dto, request, Locale.ENGLISH);

        assertViewName(mav, UserController.REG_SERVICE_UNEXPECTED_ERROR_URL);
    }

    @Test
    public void testRegisterFailIfConnectionErrorOccurred() throws Exception {
        RegisterUserDto dto = createRegisterUserDto(null);
        doThrow(new NoConnectionException()).when(authenticator).register(dto);

        ModelAndView mav = userController.registerUser(dto, request, Locale.ENGLISH);

        assertViewName(mav, UserController.REG_SERVICE_CONNECTION_ERROR_URL);
    }
    
    @Test
    public void testRegisterFailIfHoneypotCaptchaNotNull() throws Exception {
        RegisterUserDto dto = createRegisterUserDto("anyString");
        
        ModelAndView mav = userController.registerUser(dto, request, Locale.ENGLISH);
        
        assertViewName(mav, UserController.REG_SERVICE_HONEYPOT_FILLED_ERROR_URL);
    }

    @Test
    public void testRegisterUserAjaxShouldBeSuccessful() throws Exception {
        RegisterUserDto dto = createRegisterUserDto(null);
        BindingResult bindingResult = new BeanPropertyBindingResult(dto, "newUser");
        when(authenticator.register(dto)).thenReturn(bindingResult);

        JsonResponse response = userController.registerUserAjax(dto, request, Locale.ENGLISH);

        assertEquals(response.getStatus(), JsonResponseStatus.SUCCESS,
                "User without validation errors should pass registration.");
    }

    @Test
    public void testRegisterAjaxValidationFail() throws Exception {
        RegisterUserDto dto = createRegisterUserDto(null);
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(true);
        when(authenticator.register(dto)).thenReturn(bindingResult);

        JsonResponse response = userController.registerUserAjax(dto, request, Locale.ENGLISH);

        assertEquals(response.getStatus(), JsonResponseStatus.FAIL,
                "User with validation errors should fail registration.");
    }

    @Test
    public void testRegisterAjaxFailIfUnexpectedErrorOccurred() throws Exception {
        RegisterUserDto dto = createRegisterUserDto(null);
        doThrow(new UnexpectedErrorException()).when(authenticator).register(dto);

        JsonResponse response = userController.registerUserAjax(dto, request, Locale.ENGLISH);

        assertEquals(response.getStatus(), JsonResponseStatus.FAIL, "Unexpected error should fail registration.");
    }

    @Test
    public void testRegisterAjaxFailIfConnectionErrorOccurred() throws Exception {
        RegisterUserDto dto = createRegisterUserDto(null);
        doThrow(new NoConnectionException()).when(authenticator).register(dto);

        JsonResponse response = userController.registerUserAjax(dto, request, Locale.ENGLISH);

        assertEquals(response.getStatus(), JsonResponseStatus.FAIL, "Connection error should fail registration.");
    }
    
    @Test
    public void testRegisterAjaxFailIfHoneypotCaptchaNotNull() throws Exception {
        RegisterUserDto dto = createRegisterUserDto("anyString");

        JsonResponse response = userController.registerUserAjax(dto, request, Locale.ENGLISH);
        
        assertEquals(response.getStatus(), JsonResponseStatus.FAIL, "HoneypotException should fail registration.");
    }

    @Test
    public void testRestorePasswordPage() {
        assertViewName(userController.showRestorePasswordPage(), "restorePassword");
    }

    @Test
    public void testRestorePassword() throws Exception {
        RestorePasswordDto dto = new RestorePasswordDto();
        dto.setUserEmail(EMAIL);
        BindingResult bindingResult = new BeanPropertyBindingResult(dto, "email");
        ModelAndView mav = userController.restorePassword(dto, bindingResult);
        verify(userService, times(1)).restorePassword(EMAIL);
        assertModelAttributeValue(mav, "message", "label.restorePassword.completed");
    }

    @Test
    public void testRestorePasswordWrongMail() throws Exception {
        RestorePasswordDto dto = new RestorePasswordDto();
        dto.setUserEmail(EMAIL);
        BindingResult bindingResult = new BeanPropertyBindingResult(dto, "email");
        bindingResult.addError(new FieldError("", "", ""));
        ModelAndView mav = userController.restorePassword(dto, bindingResult);
        verifyZeroInteractions(userService);
        assertViewName(mav, "restorePassword");
    }

    @Test
    public void testRestorePasswordFail() throws Exception {
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
    public void testActivateAccount() throws Exception {

        JCUser user = new JCUser("username", "password", null);
        user.setPassword("password");
        when(userService.getByUuid(USER_NAME)).thenReturn(user);
        String viewName = userController.activateAccount(USER_NAME, request, response);
        verify(userService, times(1)).activateAccount(USER_NAME);
        verify(userService, times(1)).loginUser(any(LoginUserDto.class), any(MutableHttpRequest.class), eq(response));
        assertEquals("redirect:/", viewName);
    }

    @Test
    public void testActivateAccountFail() throws Exception {
        doThrow(new NotFoundException()).when(userService).activateAccount(anyString());

        String viewName = userController.activateAccount(USER_NAME, request, response);

        assertEquals("errors/activationExpired", viewName);
    }

    @Test
    public void testActivateAccountAgain() throws Exception {

        JCUser user = new JCUser("username", "password", null);
        user.setEnabled(true);
        when(userService.getByUuid(USER_NAME)).thenReturn(user);
        doThrow(new UserTriesActivatingAccountAgainException()).when(userService).activateAccount(anyString());

        String viewName = userController.activateAccount(USER_NAME, request, response);
        assertEquals("redirect:/", viewName);
    }

    @Test(dataProvider = "referers")
    public void testLoginUserLogged(String referer) {
        when(userService.getCurrentUser()).thenReturn(new JCUser("username", null, null));
        when(request.getHeader("referer")).thenReturn(referer);

        ModelAndView mav = userController.loginPage(request);

        assertEquals(mav.getViewName(), "redirect:" + referer);
        verify(userService).getCurrentUser();
    }

    @Test
    public void testLoginUserNotLogged() {
        when(userService.getCurrentUser()).thenReturn(new AnonymousUser());

        ModelAndView mav = userController.loginPage(request);

        assertEquals(mav.getViewName(), UserController.LOGIN);
        verify(userService).getCurrentUser();
    }

    @DataProvider
    public Object[][] referers() {
        return new Object[][]{
                {"/"},
                {"/referer1"},
                {"/referer/url1"},
        };
    }

    @Test(enabled = false)
    public void testAjaxLoginSuccess() throws Exception {
        when(userService.loginUser(any(LoginUserDto.class), any(HttpServletRequest.class), 
                any(HttpServletResponse.class))).thenReturn(AuthenticationStatus.AUTHENTICATED);

        JsonResponse response = userController.loginAjax(null, null, "on", null, null);
        assertEquals(response.getStatus(), JsonResponseStatus.SUCCESS);
        LoginUserDto loginUserDto = new LoginUserDto("userName", "password", true, "192.168.1.1");
        verify(userService).loginUser(loginUserDto,
                any(HttpServletRequest.class), any(HttpServletResponse.class));
    }

    @Test
    public void testAjaxLoginFailure() throws Exception {
        when(userService.loginUser(any(LoginUserDto.class),any(HttpServletRequest.class), 
                any(HttpServletResponse.class))).thenReturn(AuthenticationStatus.AUTHENTICATION_FAIL);
        JsonResponse response = userController.loginAjax(null, null, "on", request, null);
        assertEquals(response.getStatus(), JsonResponseStatus.FAIL);
        verify(userService).loginUser(any(LoginUserDto.class), any(HttpServletRequest.class), any(HttpServletResponse.class));
    }

    @Test
    public void testLoginAjaxUserShouldFailIfConnectionErrorOccurred() throws Exception {
        when(userService.loginUser(any(LoginUserDto.class), any(HttpServletRequest.class), any(HttpServletResponse.class)))
                .thenThrow(new NoConnectionException());
        
        JsonResponse response = userController.loginAjax(null, null, "on", request, null);
        assertEquals(response.getStatus(), JsonResponseStatus.FAIL);
        verify(userService).loginUser(any(LoginUserDto.class), any(HttpServletRequest.class), any(HttpServletResponse.class));
    }

    @Test
    public void testLoginAjaxUserShouldFailIfUnexpectedErrorOccurred() throws Exception {
        when(userService.loginUser(any(LoginUserDto.class), any(HttpServletRequest.class), any(HttpServletResponse.class)))
                .thenThrow(new UnexpectedErrorException());
        
        JsonResponse response = userController.loginAjax(null, null, "on", request, null);
        assertEquals(response.getStatus(), JsonResponseStatus.FAIL);
        verify(userService).loginUser(any(LoginUserDto.class), any(HttpServletRequest.class), any(HttpServletResponse.class));
    }

    @Test(enabled = false)
    public void testLoginWithCorrectParametersShouldBeSuccessful() throws Exception {
        LoginUserDto loginUserDto = new LoginUserDto("userName", "password", true, "192.168.1.1");
        when(userService.loginUser(loginUserDto, any(HttpServletRequest.class), any(HttpServletResponse.class)))
                .thenReturn(AuthenticationStatus.AUTHENTICATED);
        
        ModelAndView view = userController.login(loginUserDto, "on", null, request, null);

        assertEquals(view.getViewName(), "redirect:/");
        verify(userService).loginUser(loginUserDto, any(HttpServletRequest.class), any(HttpServletResponse.class));
    }

    @Test
    public void testLoginWithIncorrectParametersShouldFail() throws Exception {
        when(userService.loginUser(any(LoginUserDto.class), any(HttpServletRequest.class), any(HttpServletResponse.class)))
                .thenReturn(AuthenticationStatus.AUTHENTICATION_FAIL);
        
        LoginUserDto loginUserDto = new LoginUserDto();
        userController.login(loginUserDto, null,  "on", request, null);

        verify(userService).loginUser(any(LoginUserDto.class), any(HttpServletRequest.class), any(HttpServletResponse.class));
    }

    @Test
    public void testLoginUserShouldFailIfConnectionErrorOccurred() throws Exception {
        when(userService.loginUser(any(LoginUserDto.class),
                any(HttpServletRequest.class), any(HttpServletResponse.class)))
                .thenThrow(new NoConnectionException());
        
        LoginUserDto loginUserDto = new LoginUserDto();
        ModelAndView view = userController.login(loginUserDto, null,  "on", request, null);

        assertEquals(view.getViewName(), UserController.AUTH_SERVICE_FAIL_URL);
        verify(userService).loginUser(eq(loginUserDto), any(HttpServletRequest.class), any(HttpServletResponse.class));
    }

    @Test
    public void testLoginUserShouldFailIfUnexpectedErrorOccurred() throws Exception {
        when(userService.loginUser(any(LoginUserDto.class), any(HttpServletRequest.class), any(HttpServletResponse.class)))
                .thenThrow(new UnexpectedErrorException());
        
        LoginUserDto loginUserDto = new LoginUserDto();
        ModelAndView view = userController.login(loginUserDto, null,  "on", request, null);

        assertEquals(view.getViewName(), UserController.AUTH_SERVICE_FAIL_URL);
        verify(userService).loginUser(eq(loginUserDto),
                any(HttpServletRequest.class), any(HttpServletResponse.class));
    }

    @Test
    public void testGetUsernameListSuccess() {
        String pattern = "us";
        List<String> usernames = Lists.newArrayList("User1", "User2", "User3");
        when(userService.getUsernames(pattern)).thenReturn(usernames);

        JsonResponse response = userController.usernameList(pattern);
        assertEquals(response.getStatus(), JsonResponseStatus.SUCCESS);
    }

    @Test
    public void testSearchUsersNullSearchKey() {
        Component component = new Component();
        component.setId(1);

        when(componentService.getComponentOfForum()).thenReturn(component);

        ModelAndView mav = userController.searchUsers(null);

        verify(componentService).checkPermissionsForComponent(component.getId());
        assertEquals(mav.getViewName(), UserController.USER_SEARCH);
        assertFalse(mav.getModel().containsKey(UserController.USERS_ATTR_NAME));
    }

    @Test
    public void testSearchUsersEmptySearchKey() {
        Component component = new Component();
        component.setId(1);

        when(componentService.getComponentOfForum()).thenReturn(component);

        ModelAndView mav = userController.searchUsers("");

        verify(componentService).checkPermissionsForComponent(component.getId());
        assertEquals(mav.getViewName(), UserController.USER_SEARCH);
        assertFalse(mav.getModel().containsKey(UserController.USERS_ATTR_NAME));
    }

    @Test
    public void testSearchUsers() {
        Component component = new Component();
        component.setId(1);
        String searchKey = "key";
        List<JCUser> users = Lists.asList(new JCUser("user", "email@email.com", "pwd"), new JCUser[0]);

        when(componentService.getComponentOfForum()).thenReturn(component);
        when(userService.findByUsernameOrEmail(component.getId(), searchKey)).thenReturn(users);

        ModelAndView mav = userController.searchUsers(searchKey);

        assertEquals(mav.getViewName(), UserController.USER_SEARCH);
        assertEquals(mav.getModel().get(UserController.USERS_ATTR_NAME), users);
    }

    @Test
    public void searchusersShouldTrimSearchKey()
    {
        Component component = new Component();
        component.setId(1);
        String searchKey = "  key  ";

        when(componentService.getComponentOfForum()).thenReturn(component);

        userController.searchUsers(searchKey);

        verify(userService).findByUsernameOrEmail(component.getId(), searchKey.trim());
    }

    private void assertNullFields(RegisterUserDto dto) {
        assertNull(dto.getUserDto());
        assertNull(dto.getPasswordConfirm());
    }

    private RegisterUserDto createRegisterUserDto(String honeypotCaptcha) {
        UserDto userDto = new UserDto();
        userDto.setUsername(USER_NAME);
        userDto.setEmail(EMAIL);
        userDto.setPassword("password");
        RegisterUserDto dto = new RegisterUserDto();
        dto.setPasswordConfirm("password");
        dto.setUserDto(userDto);
        dto.setHoneypotCaptcha(honeypotCaptcha);
        return dto;
    }
}
