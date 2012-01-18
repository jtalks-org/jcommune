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

import org.jtalks.jcommune.model.entity.Language;
import org.jtalks.jcommune.model.entity.Post;
import org.jtalks.jcommune.model.entity.Topic;
import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.service.PostService;
import org.jtalks.jcommune.service.nontransactional.SecurityService;
import org.jtalks.jcommune.service.UserService;
import org.jtalks.jcommune.service.dto.UserInfoContainer;
import org.jtalks.jcommune.service.exceptions.DuplicateEmailException;
import org.jtalks.jcommune.service.exceptions.NotFoundException;
import org.jtalks.jcommune.service.exceptions.WrongPasswordException;
import org.jtalks.jcommune.service.nontransactional.ImageUtils;
import org.jtalks.jcommune.web.dto.Breadcrumb;
import org.jtalks.jcommune.web.util.BreadcrumbBuilder;
import org.jtalks.jcommune.web.dto.EditUserProfileDto;
import org.mockito.Matchers;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.ModelAndViewAssert.*;
import static org.testng.Assert.assertEquals;

/**
 * @author Kirill Afonin
 * @author Osadchuck Eugeny
 */
public class UserProfileControllerTest {
    private UserService userService;
    private SecurityService securityService;
    private UserProfileController profileController;

    private final String USER_NAME = "username";
    private final String FIRST_NAME = "first name";
    private final String LAST_NAME = "last name";
    private final String EMAIL = "mail@mail.com";
    private final String PASSWORD = "password";
    private final String SIGNATURE = "signature";
    private final Language LANGUAGE = Language.ENGLISH;
    private final int PAGE_SIZE = 50;
    private final String LOCATION = "location";
    private String avatar;
    private BreadcrumbBuilder breadcrumbBuilder;
    private ImageUtils imageUtils;
    private PostService postService;

    @BeforeClass
    public void mockAvatar() throws IOException {
        avatar = new ImageUtils().encodeB64(avatarByteArray);
    }

    @BeforeMethod
    public void setUp() throws IOException {
        userService = mock(UserService.class);
        securityService = mock(SecurityService.class);
        breadcrumbBuilder = mock(BreadcrumbBuilder.class);
        imageUtils = mock(ImageUtils.class);
        postService = mock(PostService.class);
        profileController = new UserProfileController(userService, securityService, breadcrumbBuilder, imageUtils, postService);
    }



    @Test
    public void testShow() throws Exception {
        JCUser user = new JCUser("username", "email", "password");
        user.setLanguage(LANGUAGE);
        //set expectations
        when(userService.getByUsername(USER_NAME)).thenReturn(user);

        //invoke the object under test
        ModelAndView mav = profileController.showProfilePage(USER_NAME);

        //check expectations
        verify(userService).getByUsername(USER_NAME);

        //check result
        assertViewName(mav, "userDetails");
        assertModelAttributeAvailable(mav, "user");
    }

    @Test
    public void testEditProfilePage() throws NotFoundException, IOException {
        JCUser user = getUser();
        //set expectations
        when(securityService.getCurrentUser()).thenReturn(user);

        //invoke the object under test
        ModelAndView mav = profileController.editProfilePage();

        //check expectations
        verify(securityService).getCurrentUser();

        //check result
        assertViewName(mav, "editProfile");
        EditUserProfileDto dto = assertAndReturnModelAttributeOfType(mav, "editedUser", EditUserProfileDto.class);
        assertEquals(dto.getFirstName(), user.getFirstName(), "First name is not equal");
        assertEquals(dto.getLastName(), user.getLastName(), "Last name is not equal");
        assertEquals(dto.getEmail(), user.getEmail(), "Last name is not equal");
    }


    @Test
    public void testEditProfile() throws Exception {
        JCUser user = getUser();
        EditUserProfileDto userDto = getEditUserProfileDto();
        MockHttpServletResponse response = new MockHttpServletResponse();

        when(userService.editUserProfile(Matchers.<UserInfoContainer>any())).thenReturn(user);

        BindingResult bindingResult = new BeanPropertyBindingResult(userDto, "editedUser");

        ModelAndView mav = profileController.editProfile(userDto, bindingResult, response);

        String expectedUrl = "redirect:/users/" + user.getEncodedUsername();
        assertViewName(mav, expectedUrl);
        assertEquals(response.getCookies()[0].getValue(), Language.ENGLISH.getLanguageCode());
        assertEquals(response.getCookies()[0].getName(), CookieLocaleResolver.DEFAULT_COOKIE_NAME);
        verify(userService).editUserProfile(new UserInfoContainer(userDto.getEmail(), userDto.getFirstName(),
                userDto.getLastName(), userDto.getCurrentUserPassword(),
                userDto.getNewUserPassword(), anyString(),
                SIGNATURE, LANGUAGE, PAGE_SIZE, LOCATION));
    }

    @Test
    public void testEditProfileDuplicatedEmail() throws Exception {
        JCUser user = getUser();
        when(securityService.getCurrentUser()).thenReturn(user);
        EditUserProfileDto userDto = getEditUserProfileDto();
        BindingResult bindingResult = new BeanPropertyBindingResult(userDto, "editedUser");

        when(userService.editUserProfile(Matchers.<UserInfoContainer>any())).thenThrow(new DuplicateEmailException());

        ModelAndView mav = profileController.editProfile(userDto, bindingResult, new MockHttpServletResponse());

        assertViewName(mav, "editProfile");
        assertEquals(bindingResult.getErrorCount(), 1, "Result without errors");
        verify(userService).editUserProfile(Matchers.<UserInfoContainer>any());

        assertContainsError(bindingResult, "email");
    }

    @Test
    public void testEditProfileWrongPassword() throws Exception {
        JCUser user = getUser();
        when(securityService.getCurrentUser()).thenReturn(user);
        EditUserProfileDto userDto = getEditUserProfileDto();
        BindingResult bindingResult = new BeanPropertyBindingResult(userDto, "editedUser");

        when(userService.editUserProfile(new UserInfoContainer(userDto.getEmail(), userDto.getFirstName(),
                userDto.getLastName(), userDto.getCurrentUserPassword(),
                userDto.getNewUserPassword(),
                anyString(),
                SIGNATURE, LANGUAGE, PAGE_SIZE, LOCATION))).thenThrow(new WrongPasswordException());

        ModelAndView mav = profileController.editProfile(userDto, bindingResult, new MockHttpServletResponse());

        assertViewName(mav, "editProfile");
        assertEquals(bindingResult.getErrorCount(), 1, "Result without errors");
        verify(userService).editUserProfile(new UserInfoContainer(userDto.getEmail(), userDto.getFirstName(),
                userDto.getLastName(), userDto.getCurrentUserPassword(),
                userDto.getNewUserPassword(), anyString(),
                SIGNATURE, LANGUAGE, PAGE_SIZE, LOCATION));
        assertContainsError(bindingResult, "currentUserPassword");
    }

    @Test
    public void testEditProfileValidationFail() throws Exception {
        JCUser user = getUser();
        when(securityService.getCurrentUser()).thenReturn(user);

        EditUserProfileDto dto = getEditUserProfileDto();
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(true);

        ModelAndView mav = profileController.editProfile(dto, bindingResult, new MockHttpServletResponse());

        assertViewName(mav, "editProfile");
        verify(userService, never()).editUserProfile(Matchers.<UserInfoContainer>any());
    }

    @Test
    public void testInitBinder() {
        WebDataBinder binder = mock(WebDataBinder.class);
        profileController.initBinder(binder);
        verify(binder).registerCustomEditor(eq(String.class), any(StringTrimmerEditor.class));
    }

    @Test
    public void testShowUserPostList() throws NotFoundException {
        JCUser user = new JCUser("username", "email", "password");
        user.setPageSize(5);
        Post post = mock(Post.class);
        Topic topic = mock(Topic.class);
        List<Post> posts = new ArrayList<Post>();
        posts.add(post);

        //set expectations
        when(userService.getByUsername("username")).thenReturn(user);
        when(breadcrumbBuilder.getForumBreadcrumb()).thenReturn(new ArrayList<Breadcrumb>());
        when(postService.getPostsOfUser(user)).thenReturn(new ArrayList<Post>());
        when(securityService.getCurrentUser()).thenReturn(user);
        when(postService.getPostsOfUser(user)).thenReturn(posts);
        when(post.getTopic()).thenReturn(topic);


        //invoke the object under test
        ModelAndView mav = profileController.showUserPostList("username", 1, true);

        //check expectations
        verify(userService).getByUsername("username");
        verify(breadcrumbBuilder).getForumBreadcrumb();

        //check result
        assertModelAttributeAvailable(mav, "user");
        assertModelAttributeAvailable(mav, "breadcrumbList");
        assertModelAttributeAvailable(mav, "user");
    }

    private void assertContainsError(BindingResult bindingResult, String errorName) {
        for (ObjectError error : bindingResult.getAllErrors()) {
            if (error != null && error instanceof FieldError) {
                assertEquals(((FieldError) error).getField(), errorName);
            }
        }
    }

    /**
     * @return {@link EditUserProfileDto} with default values
     */
    private EditUserProfileDto getEditUserProfileDto() {
        String NEW_PASSWORD = "newPassword";

        JCUser user = new JCUser("username", EMAIL, PASSWORD);
        user.setFirstName(FIRST_NAME);
        user.setLastName(LAST_NAME);
        user.setSignature(SIGNATURE);
        user.setLanguage(LANGUAGE);
        user.setPageSize(PAGE_SIZE);

        EditUserProfileDto dto = new EditUserProfileDto(user);
        dto.setCurrentUserPassword(PASSWORD);
        dto.setNewUserPassword(NEW_PASSWORD);
        dto.setNewUserPasswordConfirm(NEW_PASSWORD);
        dto.setAvatar(avatar);
        return dto;
    }

    private JCUser getUser() throws IOException {
        JCUser newUser = new JCUser(USER_NAME, EMAIL, PASSWORD);
        newUser.setFirstName(FIRST_NAME);
        newUser.setLastName(LAST_NAME);
        newUser.setAvatar(avatarByteArray);
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
