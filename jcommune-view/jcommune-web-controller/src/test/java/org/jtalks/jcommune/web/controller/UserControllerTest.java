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

import org.jtalks.jcommune.model.entity.User;
import org.jtalks.jcommune.service.PostService;
import org.jtalks.jcommune.service.SecurityService;
import org.jtalks.jcommune.service.UserService;
import org.jtalks.jcommune.service.exceptions.*;
import org.jtalks.jcommune.web.dto.Breadcrumb;
import org.jtalks.jcommune.web.dto.BreadcrumbBuilder;
import org.jtalks.jcommune.web.dto.EditUserProfileDto;
import org.jtalks.jcommune.web.dto.RegisterUserDto;
import org.jtalks.jcommune.web.util.ImagePreprocessor;
import org.jtalks.jcommune.web.util.Language;
import org.mockito.Matchers;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.ModelAndViewAssert.*;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

/**
 * @author Kirill Afonin
 * @author Osadchuck Eugeny
 *         <p/>
 *         TODO: Split it
 */
public class UserControllerTest {
    private UserService userService;
    private SecurityService securityService;
    private UserController controller;

    private final String USER_NAME = "username";
    private final String FIRST_NAME = "first name";
    private final String LAST_NAME = "last name";
    private final String EMAIL = "mail@mail.com";
    private final String PASSWORD = "password";
    private final String SIGNATURE = "signature";
    private final String NEW_PASSWORD = "newPassword";
    private final String LANGUAGE = "ENGLISH";
    private final String PAGE_SIZE = "FIFTY";
    private String avatar;
    private BreadcrumbBuilder breadcrumbBuilder;
    private ImagePreprocessor imagePreprocessor;
    private PostService postService;

    @BeforeClass
    public void mockAvatar() throws IOException {
        avatar = new ImagePreprocessor().base64Coder(avatarByteArray);
    }

    @BeforeMethod
    public void setUp() throws IOException {
        userService = mock(UserService.class);
        securityService = mock(SecurityService.class);
        breadcrumbBuilder = mock(BreadcrumbBuilder.class);
        imagePreprocessor = mock(ImagePreprocessor.class);
        postService = mock(PostService.class);
        controller = new UserController(userService, securityService, breadcrumbBuilder, imagePreprocessor, postService);
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
        User user = new User("username", "email", "password");
        user.setLanguage("ENGLISH");
        //set expectations
        when(userService.getByUsername(USER_NAME))
                .thenReturn(user);
        when(breadcrumbBuilder.getForumBreadcrumb()).thenReturn(new ArrayList<Breadcrumb>());


        //invoke the object under test
        ModelAndView mav = controller.showProfilePage(USER_NAME);

        //check expectations
        verify(userService).getByUsername(USER_NAME);
        verify(breadcrumbBuilder).getForumBreadcrumb();

        //check result
        assertViewName(mav, "userDetails");
        assertModelAttributeAvailable(mav, "user");
        assertModelAttributeAvailable(mav, "breadcrumbList");

    }

    @Test
    public void testEditProfilePage() throws NotFoundException, IOException {
        User user = getUser();
        //set expectations
        when(securityService.getCurrentUser()).thenReturn(user);
        when(breadcrumbBuilder.getForumBreadcrumb()).thenReturn(new ArrayList<Breadcrumb>());

        //invoke the object under test
        ModelAndView mav = controller.editProfilePage();

        //check expectations
        verify(securityService).getCurrentUser();
        verify(breadcrumbBuilder).getForumBreadcrumb();

        //check result
        assertViewName(mav, "editProfile");
        assertModelAttributeAvailable(mav, "languages");
        assertModelAttributeAvailable(mav, "breadcrumbList");
        EditUserProfileDto dto = assertAndReturnModelAttributeOfType(mav, "editedUser", EditUserProfileDto.class);
        assertEquals(dto.getFirstName(), user.getFirstName(), "First name is not equal");
        assertEquals(dto.getLastName(), user.getLastName(), "Last name is not equal");
        assertEquals(dto.getEmail(), user.getEmail(), "Last name is not equal");
    }


    @Test
    public void testEditProfile() throws Exception {
        User user = getUser();
        EditUserProfileDto userDto = getEditUserProfileDto();
        MockHttpServletResponse response = new MockHttpServletResponse();

        when(userService.editUserProfile(userDto.getEmail(), userDto.getFirstName(),
                userDto.getLastName(), userDto.getCurrentUserPassword(),
                userDto.getNewUserPassword(),
                imagePreprocessor.base64Decoder(userDto.getAvatar()),
                SIGNATURE, LANGUAGE, PAGE_SIZE)).thenReturn(user);

        BindingResult bindingResult = new BeanPropertyBindingResult(userDto, "editedUser");

        ModelAndView mav = controller.editProfile(userDto, bindingResult, response);

        String expectedUrl = "redirect:/users/" + user.getEncodedUsername();
        assertViewName(mav, expectedUrl);
        assertEquals(response.getCookies()[0].getValue(), Language.ENGLISH.getLanguageCode());
        assertEquals(response.getCookies()[0].getName(), CookieLocaleResolver.DEFAULT_COOKIE_NAME);
        verify(userService).editUserProfile(userDto.getEmail(), userDto.getFirstName(),
                userDto.getLastName(), userDto.getCurrentUserPassword(),
                userDto.getNewUserPassword(), imagePreprocessor.base64Decoder(userDto.getAvatar()),
                SIGNATURE, LANGUAGE, PAGE_SIZE);
    }

    @Test
    public void testEditProfileDuplicatedEmail() throws Exception {
        User user = getUser();
        when(securityService.getCurrentUser()).thenReturn(user);
        EditUserProfileDto userDto = getEditUserProfileDto();
        BindingResult bindingResult = new BeanPropertyBindingResult(userDto, "editedUser");

        when(userService.editUserProfile(userDto.getEmail(), userDto.getFirstName(),
                userDto.getLastName(), userDto.getCurrentUserPassword(),
                userDto.getNewUserPassword(),
                imagePreprocessor.base64Decoder(userDto.getAvatar()),
                SIGNATURE, LANGUAGE, PAGE_SIZE)).thenThrow(new DuplicateEmailException());

        ModelAndView mav = controller.editProfile(userDto, bindingResult, new MockHttpServletResponse());

        assertViewName(mav, "editProfile");
        assertEquals(bindingResult.getErrorCount(), 1, "Result without errors");
        assertModelAttributeAvailable(mav, "languages");
        verify(userService).editUserProfile(userDto.getEmail(), userDto.getFirstName(),
                userDto.getLastName(), userDto.getCurrentUserPassword(),
                userDto.getNewUserPassword(), imagePreprocessor.base64Decoder(userDto.getAvatar()),
                SIGNATURE, LANGUAGE, PAGE_SIZE);

        assertContainsError(bindingResult, "email");
    }

    @Test
    public void testEditProfileWrongPassword() throws Exception {
        User user = getUser();
        when(securityService.getCurrentUser()).thenReturn(user);
        EditUserProfileDto userDto = getEditUserProfileDto();
        BindingResult bindingResult = new BeanPropertyBindingResult(userDto, "editedUser");

        when(userService.editUserProfile(userDto.getEmail(), userDto.getFirstName(),
                userDto.getLastName(), userDto.getCurrentUserPassword(),
                userDto.getNewUserPassword(),
                imagePreprocessor.base64Decoder(userDto.getAvatar()),
                SIGNATURE, LANGUAGE, PAGE_SIZE)).thenThrow(new WrongPasswordException());

        ModelAndView mav = controller.editProfile(userDto, bindingResult, new MockHttpServletResponse());

        assertViewName(mav, "editProfile");
        assertModelAttributeAvailable(mav, "languages");
        assertEquals(bindingResult.getErrorCount(), 1, "Result without errors");
        verify(userService).editUserProfile(userDto.getEmail(), userDto.getFirstName(),
                userDto.getLastName(), userDto.getCurrentUserPassword(),
                userDto.getNewUserPassword(), imagePreprocessor.base64Decoder(userDto.getAvatar()),
                SIGNATURE, LANGUAGE, PAGE_SIZE);
        assertContainsError(bindingResult, "currentUserPassword");
    }

    @Test
    public void testEditProfileValidationFail() throws Exception {
        User user = getUser();
        when(securityService.getCurrentUser()).thenReturn(user);

        EditUserProfileDto dto = getEditUserProfileDto();
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(true);

        ModelAndView mav = controller.editProfile(dto, bindingResult, new MockHttpServletResponse());

        assertViewName(mav, "editProfile");
        assertModelAttributeAvailable(mav, "languages");
        verify(userService, never()).editUserProfile(anyString(), anyString(), anyString(),
                anyString(), anyString(), Matchers.<byte[]>anyObject(), anyString(), anyString(), anyString());
    }

    @Test
    public void testRemoveAvatar() throws IOException {
        User user = getUser();
        when(securityService.getCurrentUser()).thenReturn(user);

        ModelAndView mav = controller.removeAvatarFromCurrentUser();

        assertViewName(mav, "editProfile");
        verify(securityService).getCurrentUser();
        verify(userService).removeAvatarFromCurrentUser();
    }

    @Test
    public void testRenderAvatar() throws Exception {
        String ENCODED_USER_NAME = "encodeUsername";
        when(userService.getByEncodedUsername(ENCODED_USER_NAME))
                .thenReturn(getUser());
        HttpServletResponse response = mock(HttpServletResponse.class);
        ServletOutputStream servletOutputStream = mock(ServletOutputStream.class);
        when(response.getOutputStream()).thenReturn(servletOutputStream);

        controller.renderAvatar(response, ENCODED_USER_NAME);

        verify(response).setContentType("image/jpeg");
        verify(response).setContentLength(avatarByteArray.length);
        verify(response).getOutputStream();
        verify(servletOutputStream).write(avatarByteArray);
    }

    @Test
    public void testInitBinder() {
        WebDataBinder binder = mock(WebDataBinder.class);
        controller.initBinder(binder);
        verify(binder).registerCustomEditor(eq(String.class), any(StringTrimmerEditor.class));
    }

    @Test
    public void testRestorePasswordPage() {
        assertViewName(controller.showRestorePasswordPage(), "restorePassword");
    }

    @Test
    public void testRestorePassword() throws IOException, NotFoundException, MailingFailedException {
        ModelAndView mav = controller.restorePassword(EMAIL);
        verify(userService, times(1)).restorePassword(EMAIL);
        assertModelAttributeValue(mav, "message", "label.restorePassword.completed");
    }

    @Test
    public void testRestorePasswordWithWrongEmail() throws NotFoundException, MailingFailedException {
        doThrow(new NotFoundException()).when(userService).restorePassword(anyString());
        ModelAndView mav = controller.restorePassword(EMAIL);
        verify(userService, times(1)).restorePassword(EMAIL);
        assertModelAttributeValue(mav, "error", "email.unknown");
    }

    @Test
    public void testRestorePasswordFail() throws NotFoundException, MailingFailedException {
        Exception fail = new MailingFailedException("", new RuntimeException());
        doThrow(fail).when(userService).restorePassword(anyString());
        ModelAndView mav = controller.restorePassword(EMAIL);
        verify(userService, times(1)).restorePassword(EMAIL);
        assertModelAttributeValue(mav, "error", "email.failed");
    }

    @Test
    public void testShowUserPostList() throws NotFoundException {
        User user = new User("username", "email", "password");
        user.setLanguage("ENGLISH");
        user.setPageSize("FIVE");

        //set expectations
        when(securityService.getCurrentUser()).thenReturn(user);
        when(breadcrumbBuilder.getForumBreadcrumb()).thenReturn(new ArrayList<Breadcrumb>());


        //invoke the object under test
        ModelAndView mav = controller.showUserPostList(user.getEncodedUsername(), 1,true);

        //check expectations
        verify(securityService).getCurrentUser();
        verify(breadcrumbBuilder).getForumBreadcrumb();

        //check result
        assertModelAttributeAvailable(mav, "pageSize");
        assertModelAttributeAvailable(mav, "user");
        assertModelAttributeAvailable(mav, "breadcrumbList");
        assertModelAttributeAvailable(mav,"user");
        assertModelAttributeAvailable(mav,"language");
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
        dto.setSignature(SIGNATURE);
        dto.setLanguage(LANGUAGE);
        dto.setPageSize(PAGE_SIZE);
        dto.setNewUserPasswordConfirm(NEW_PASSWORD);
        dto.setAvatar(avatar);
        return dto;
    }

    private User getUser() throws IOException {
        User newUser = new User(USER_NAME, EMAIL, PASSWORD);
        newUser.setFirstName(FIRST_NAME);
        newUser.setLastName(LAST_NAME);
        newUser.setAvatar(avatarByteArray);
        return newUser;
    }

    private User getUserWithoutAvatar() throws IOException {
        User newUser = new User(USER_NAME, EMAIL, PASSWORD);
        newUser.setFirstName(FIRST_NAME);
        newUser.setLastName(LAST_NAME);
        newUser.setAvatar(null);
        return newUser;
    }

    private byte[] avatarByteArray = new byte[]{-119, 80, 78, 71, 13, 10, 26, 10, 0, 0, 0, 13, 73, 72, 68, 82, 0,
            0, 0, 4, 0, 0, 0, 4, 1, 0, 0, 0, 0, -127, -118, -93, -45, 0, 0, 0, 9, 112, 72, 89, 115, 0, 0, 1,
            -118, 0, 0, 1, -118, 1, 51, -105, 48, 88, 0, 0, 0, 32, 99, 72, 82, 77, 0, 0, 122, 37, 0, 0,
            -128, -125, 0, 0, -7, -1, 0, 0, -128, -23, 0, 0, 117, 48, 0, 0, -22, 96, 0, 0, 58, -104, 0, 0,
            23, 111, -110, 95, -59, 70, 0, 0, 0, 22, 73, 68, 65, 84, 120, -38, 98, -40, -49, -60, -64, -92,
            -64, -60, 0, 0, 0, 0, -1, -1, 3, 0, 5, -71, 0, -26, -35, -7, 32, 96, 0, 0, 0, 0, 73, 69, 78, 68,
            -82, 66, 96, -126
    };
}
