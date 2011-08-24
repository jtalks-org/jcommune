/**
 * Copyright (C) 2011  jtalks.org Team
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
 * Also add information on how to contact you by electronic and paper mail.
 * Creation date: Apr 12, 2011 / 8:05:19 PM
 * The jtalks.org Project
 */
package org.jtalks.jcommune.web.controller;

import org.jtalks.jcommune.model.entity.User;
import org.jtalks.jcommune.service.SecurityService;
import org.jtalks.jcommune.service.UserService;
import org.jtalks.jcommune.service.exceptions.DuplicateEmailException;
import org.jtalks.jcommune.service.exceptions.DuplicateUserException;
import org.jtalks.jcommune.service.exceptions.NotFoundException;
import org.jtalks.jcommune.service.exceptions.WrongPasswordException;
import org.jtalks.jcommune.web.dto.Breadcrumb;
import org.jtalks.jcommune.web.dto.BreadcrumbBuilder;
import org.jtalks.jcommune.web.dto.EditUserProfileDto;
import org.jtalks.jcommune.web.dto.RegisterUserDto;
import org.jtalks.jcommune.web.validation.ImageFormats;
import org.mockito.Matchers;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.ModelAndViewAssert.assertAndReturnModelAttributeOfType;
import static org.springframework.test.web.ModelAndViewAssert.assertModelAttributeAvailable;
import static org.springframework.test.web.ModelAndViewAssert.assertViewName;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

/**
 * @author Kirill Afonin
 * @author Osadchuck Eugeny
 */
public class UserControllerTest {
    private UserService userService;
    private SecurityService securityService;
    private UserController controller;

    private final String USER_NAME = "username";
    private final String ENCODED_USER_NAME = "encodeUsername";
    private final String FIRST_NAME = "first name";
    private final String LAST_NAME = "last name";
    private final String EMAIL = "mail@mail.com";
    private final String PASSWORD = "password";
    private final String NEW_PASSWORD = "newPassword";
    private MultipartFile avatar;
    private BreadcrumbBuilder breadcrumbBuilder;

    @BeforeClass
    public void mockAvatar() throws IOException {
        avatar = new MockMultipartFile("test_avatar.jpg", "test_avatar.jpg", "image/jpeg",
                new byte[10]);
    }

    @BeforeMethod
    public void setUp() throws IOException {
        userService = mock(UserService.class);
        securityService = mock(SecurityService.class);
        controller = new UserController(userService, securityService);
        breadcrumbBuilder = mock(BreadcrumbBuilder.class);
        controller.setBreadcrumbBuilder(breadcrumbBuilder);
    }

    @Test
    public void testRegistrationPage() throws Exception {
        ModelAndView mav = controller.registrationPage();

        assertViewName(mav, "registration");
        RegisterUserDto dto = assertAndReturnModelAttributeOfType(mav, "newUser", RegisterUserDto.class);
        assertNullFields(dto);
    }

    private void assertNullFields(RegisterUserDto dto) {
        assertNull(dto.getEmail());
        assertNull(dto.getUsername());
        assertNull(dto.getPassword());
        assertNull(dto.getPasswordConfirm());
        assertNull(dto.getLastName());
        assertNull(dto.getFirstName());
    }

    @Test
    public void testRegisterUser() throws Exception {
        RegisterUserDto dto = getRegisterUserDto();
        BindingResult bindingResult = new BeanPropertyBindingResult(dto, "newUser");

        ModelAndView mav = controller.registerUser(dto, bindingResult);

        assertViewName(mav, "redirect:/");
        verify(userService).registerUser(any(User.class));
    }

    @Test
    public void testRegisterDuplicateUser() throws Exception {
        RegisterUserDto dto = getRegisterUserDto();
        BindingResult bindingResult = new BeanPropertyBindingResult(dto, "newUser");
        doThrow(new DuplicateUserException("User username already exists!"))
                .when(userService).registerUser(any(User.class));

        ModelAndView mav = controller.registerUser(dto, bindingResult);

        assertViewName(mav, "registration");
        assertEquals(bindingResult.getErrorCount(), 1, "Result without errors");
        verify(userService).registerUser(any(User.class));
    }

    @Test
    public void testRegisterUserWithDuplicateEmail() throws Exception {
        RegisterUserDto dto = getRegisterUserDto();
        BindingResult bindingResult = new BeanPropertyBindingResult(dto, "newUser");
        doThrow(new DuplicateEmailException("E-mail mail@mail.com already exists!"))
                .when(userService).registerUser(any(User.class));

        ModelAndView mav = controller.registerUser(dto, bindingResult);

        assertViewName(mav, "registration");
        assertEquals(bindingResult.getErrorCount(), 1, "Result without errors");
        verify(userService).registerUser(any(User.class));
    }

    @Test
    public void testRegisterValidationFail() {
        RegisterUserDto dto = getRegisterUserDto();
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(true);

        ModelAndView mav = controller.registerUser(dto, bindingResult);

        assertViewName(mav, "registration");
    }

    @Test
    public void testShow() throws Exception {
        //set expectations
        when(userService.getByEncodedUsername(ENCODED_USER_NAME))
                .thenReturn(new User("username", "email", "password"));
        when(breadcrumbBuilder.getForumBreadcrumb()).thenReturn(new ArrayList<Breadcrumb>());


        //invoke the object under test
        ModelAndView mav = controller.show(ENCODED_USER_NAME);

        //check expectations
        verify(userService).getByEncodedUsername(ENCODED_USER_NAME);
        verify(breadcrumbBuilder).getForumBreadcrumb();

        //check result
        assertViewName(mav, "userDetails");
        assertModelAttributeAvailable(mav, "user");
        assertModelAttributeAvailable(mav, "breadcrumbList");

    }

    @Test
    public void testEditProfilePage() throws NotFoundException, IOException {
        User user = getUser();
        when(securityService.getCurrentUser()).thenReturn(user);

        ModelAndView mav = controller.editProfilePage();

        assertViewName(mav, "editProfile");
        EditUserProfileDto dto = assertAndReturnModelAttributeOfType(mav, "editedUser", EditUserProfileDto.class);
        assertEquals(dto.getFirstName(), user.getFirstName(), "First name is not equal");
        assertEquals(dto.getLastName(), user.getLastName(), "Last name is not equal");
        assertEquals(dto.getEmail(), user.getEmail(), "Last name is not equal");
        verify(securityService).getCurrentUser();
    }

    @Test
    public void testEditProfile() throws Exception {
        User user = getUser();
        EditUserProfileDto userDto = getEditUserProfileDto();
        when(userService.editUserProfile(userDto.getEmail(), userDto.getFirstName(),
                userDto.getLastName(), userDto.getCurrentUserPassword(),
                userDto.getNewUserPassword(), userDto.getAvatar().getBytes())).thenReturn(user);
        BindingResult bindingResult = new BeanPropertyBindingResult(userDto, "editedUser");

        ModelAndView mav = controller.editProfile(userDto, bindingResult);

        String expectedUrl = "redirect:/user/" + user.getEncodedUsername() + ".html";
        assertViewName(mav, expectedUrl);
        verify(userService).editUserProfile(userDto.getEmail(), userDto.getFirstName(),
                userDto.getLastName(), userDto.getCurrentUserPassword(),
                userDto.getNewUserPassword(), userDto.getAvatar().getBytes());
    }

    @Test
    public void testEditProfileWithAvatarFailedValidation() throws Exception {
        User user = getUserWithoutAvatar();
        when(securityService.getCurrentUser()).thenReturn(user);
        EditUserProfileDto userDto = mock(EditUserProfileDto.class);
        when(userDto.getEmail()).thenReturn(EMAIL);
        when(userDto.getFirstName()).thenReturn(FIRST_NAME);
        when(userDto.getLastName()).thenReturn(LAST_NAME);
        when(userDto.getCurrentUserPassword()).thenReturn(PASSWORD);
        when(userDto.getNewUserPassword()).thenReturn(NEW_PASSWORD);
        when(userDto.getAvatar()).thenReturn(avatar);
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(true);

        ModelAndView mav = controller.editProfile(userDto, bindingResult);

        assertViewName(mav, "editProfile");
        verify(userDto).setAvatar(Matchers.<MultipartFile>anyObject());
        verify(userService, never()).editUserProfile(userDto.getEmail(), userDto.getFirstName(),
                userDto.getLastName(), userDto.getCurrentUserPassword(),
                userDto.getNewUserPassword(), userDto.getAvatar().getBytes());
    }

    @Test
    public void testEditProfileDuplicatedEmail() throws Exception {
        EditUserProfileDto userDto = getEditUserProfileDto();
        BindingResult bindingResult = new BeanPropertyBindingResult(userDto, "editedUser");

        doThrow(new DuplicateEmailException()).when(userService)
                .editUserProfile(userDto.getEmail(), userDto.getFirstName(),
                        userDto.getLastName(), userDto.getCurrentUserPassword(),
                        userDto.getNewUserPassword(), userDto.getAvatar().getBytes());

        ModelAndView mav = controller.editProfile(userDto, bindingResult);

        assertViewName(mav, "editProfile");
        assertEquals(bindingResult.getErrorCount(), 1, "Result without errors");
        verify(userService).editUserProfile(userDto.getEmail(), userDto.getFirstName(),
                userDto.getLastName(), userDto.getCurrentUserPassword(),
                userDto.getNewUserPassword(), userDto.getAvatar().getBytes());

        assertContainsError(bindingResult, "email");
    }

    @Test
    public void testEditProfileWrongPassword() throws Exception {
        EditUserProfileDto userDto = getEditUserProfileDto();
        BindingResult bindingResult = new BeanPropertyBindingResult(userDto, "editedUser");
        doThrow(new WrongPasswordException()).when(userService)
                .editUserProfile(userDto.getEmail(), userDto.getFirstName(),
                        userDto.getLastName(), userDto.getCurrentUserPassword(),
                        userDto.getNewUserPassword(), userDto.getAvatar().getBytes());

        ModelAndView mav = controller.editProfile(userDto, bindingResult);

        assertViewName(mav, "editProfile");
        assertEquals(bindingResult.getErrorCount(), 1, "Result without errors");
        verify(userService).editUserProfile(userDto.getEmail(), userDto.getFirstName(),
                userDto.getLastName(), userDto.getCurrentUserPassword(),
                userDto.getNewUserPassword(), userDto.getAvatar().getBytes());
        assertContainsError(bindingResult, "currentUserPassword");
    }

    @Test
    public void testEditProfileValidationFail() throws Exception {
        User user=getUser();
        when(securityService.getCurrentUser()).thenReturn(user);

        EditUserProfileDto dto = getEditUserProfileDto();
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(true);

        ModelAndView mav = controller.editProfile(dto, bindingResult);

        assertViewName(mav, "editProfile");
        verify(userService, never()).editUserProfile(anyString(), anyString(), anyString(),
                anyString(), anyString(), Matchers.<byte[]>anyObject());
    }

    @Test
    public void testRemoveAvatar() throws IOException {
        User user = getUser();
        when(securityService.getCurrentUser()).thenReturn(user);
        ModelAndView mav = controller.removeAvatarFromCurrentUser();
        assertViewName(mav, "editProfile");
        verify(securityService).getCurrentUser();
        verify(userService).removeAvatarFromCurrentUser(user);
    }

    @Test
    public void testRenderAvatar() throws Exception {
        when(userService.getByEncodedUsername(ENCODED_USER_NAME))
                .thenReturn(getUser());
        HttpServletResponse response = mock(HttpServletResponse.class);
        ServletOutputStream servletOutputStream = mock(ServletOutputStream.class);
        when(response.getOutputStream()).thenReturn(servletOutputStream);
        controller.renderAvatar(response, ENCODED_USER_NAME);
        verify(response).setContentType("image/jpeg");
        verify(response).setContentLength(avatar.getBytes().length);
        verify(response).getOutputStream();
        verify(servletOutputStream).write(avatar.getBytes());
    }

    @Test
    public void testInitBinder() {
        WebDataBinder binder = mock(WebDataBinder.class);

        controller.initBinder(binder);

        verify(binder).registerCustomEditor(eq(String.class), any(StringTrimmerEditor.class));
    }

    private void assertContainsError(BindingResult bindingResult, String errorName) {
        for (ObjectError error : bindingResult.getAllErrors()) {
            if (error != null && error instanceof FieldError) {
                assertEquals(((FieldError) error).getField(), errorName);
            }
        }
    }

    /**
     * @return RegisterUserDto with default field values
     */
    private RegisterUserDto getRegisterUserDto() {
        RegisterUserDto dto = new RegisterUserDto();
        dto.setUsername(USER_NAME);
        dto.setEmail(EMAIL);
        dto.setPassword(PASSWORD);
        dto.setPasswordConfirm(PASSWORD);
        dto.setFirstName(FIRST_NAME);
        dto.setLastName(LAST_NAME);
        return dto;
    }

    /**
     * @return {@link EditUserProfileDto} with default values
     */
    private EditUserProfileDto getEditUserProfileDto() {
        EditUserProfileDto dto = new EditUserProfileDto();
        dto.setEmail(EMAIL);
        dto.setFirstName(FIRST_NAME);
        dto.setLastName(LAST_NAME);
        dto.setCurrentUserPassword(PASSWORD);
        dto.setNewUserPassword(NEW_PASSWORD);
        dto.setNewUserPasswordConfirm(NEW_PASSWORD);
        dto.setAvatar(avatar);
        return dto;
    }

    private User getUser() throws IOException {
        User newUser = new User(USER_NAME, EMAIL, PASSWORD);
        newUser.setFirstName(FIRST_NAME);
        newUser.setLastName(LAST_NAME);
        newUser.setAvatar(avatar.getBytes());
        return newUser;
    }

    private User getUserWithoutAvatar() throws IOException {
        User newUser = new User(USER_NAME, EMAIL, PASSWORD);
        newUser.setFirstName(FIRST_NAME);
        newUser.setLastName(LAST_NAME);
        newUser.setAvatar(null);
        return newUser;
    }
}
