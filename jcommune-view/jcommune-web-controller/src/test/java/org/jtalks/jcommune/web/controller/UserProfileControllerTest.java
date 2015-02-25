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

import org.apache.commons.lang.StringUtils;
import org.jtalks.jcommune.model.entity.*;
import org.jtalks.jcommune.plugin.api.web.dto.Breadcrumb;
import org.jtalks.jcommune.service.PostService;
import org.jtalks.jcommune.service.UserContactsService;
import org.jtalks.jcommune.service.UserService;
import org.jtalks.jcommune.service.dto.EntityToDtoConverter;
import org.jtalks.jcommune.service.dto.UserInfoContainer;
import org.jtalks.jcommune.service.dto.UserNotificationsContainer;
import org.jtalks.jcommune.service.dto.UserSecurityContainer;
import org.jtalks.jcommune.plugin.api.exceptions.NotFoundException;
import org.jtalks.jcommune.service.nontransactional.Base64Wrapper;
import org.jtalks.jcommune.service.nontransactional.ImageConverter;
import org.jtalks.jcommune.service.nontransactional.ImageService;
import org.jtalks.jcommune.web.dto.*;
import org.jtalks.jcommune.plugin.api.web.util.BreadcrumbBuilder;
import org.mockito.Mock;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.servlet.ServletException;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.test.web.ModelAndViewAssert.assertModelAttributeAvailable;
import static org.springframework.test.web.ModelAndViewAssert.assertViewName;
import static org.springframework.web.servlet.DispatcherServlet.LOCALE_RESOLVER_ATTRIBUTE;
import static org.testng.Assert.assertEquals;

/**
 * @author Kirill Afonin
 * @author Osadchuck Eugeny
 * @author Anuar_Nurmakanov
 * @author Andrey Pogorelov
 * @author Andrey Ivanov
 */
public class UserProfileControllerTest {
    private static final long USER_ID = 1l;
    private static final String USER_NAME = "username";
    private static final String FIRST_NAME = "first name";
    private static final String LAST_NAME = "last name";
    private static final String EMAIL = "mail@mail.com";
    private static final String PASSWORD = "password";
    private static final String SIGNATURE = "signature";
    private static final Language LANGUAGE = Language.ENGLISH;
    private static final int PAGE_SIZE = 50;
    private static final boolean AUTOSUBSCRIBE = true;
    private String avatar;
    //
    @Mock
    private BreadcrumbBuilder breadcrumbBuilder;
    @Mock
    private ImageConverter imageConverter;
    @Mock
    private PostService postService;
    @Mock
    private UserService userService;
    @Mock
    private UserContactsService userContactsService;
    //
    private UserProfileController profileController;
    @Mock
    private ImageService imageService;
    @Mock
    private RedirectAttributes redirectAttributes;
    @Mock
    private EntityToDtoConverter converter;

    @BeforeClass
    public void mockAvatar() {
        avatar = new Base64Wrapper().encodeB64Bytes(avatarByteArray);
    }

    @BeforeMethod
    public void setUp() {
        initMocks(this);
        profileController = new UserProfileController(
                userService,
                breadcrumbBuilder,
                imageConverter,
                postService,
                userContactsService,
                imageService,
                converter);
    }

    @Test
    public void showCurrentUserProfilePageShouldMoveToCurrentUserProfile() throws NotFoundException {
        JCUser user = new JCUser(USER_NAME, EMAIL, PASSWORD);
        when(userService.getCurrentUser()).thenReturn(user);

        ModelAndView mav = profileController.showCurrentUserProfilePage();

        assertViewName(mav, "editProfile");
    }

    @Test(enabled = false)
    public void saveEditedProfileWithCorrectEnteredDataShouldMoveUserInUpdatedProfile() throws NotFoundException {
        JCUser user = getUser();
        EditUserProfileDto userDto = getEditUserProfileDto();
        MockHttpServletResponse response = new MockHttpServletResponse();
        //
        when(userService.saveEditedUserProfile(anyLong(), any(UserInfoContainer.class))).thenReturn(user);
        when(userService.getCurrentUser()).thenReturn(user);

        BindingResult bindingResult = new BeanPropertyBindingResult(userDto, "editedUser");

        ModelAndView mav = profileController.saveEditedProfile(userDto, bindingResult, response);

        String expectedUrl = "redirect:/users/" + user.getId();
        assertViewName(mav, expectedUrl);
        assertEquals(response.getLocale().getLanguage(), user.getLanguage().getLocale().getLanguage());
        verify(userService).saveEditedUserProfile(anyLong(), any(UserInfoContainer.class));
    }

    @Test(enabled = false, expectedExceptions = {NotFoundException.class})
    public void saveEditedProfileShouldShowErrorWhenUserWasNotFound() throws NotFoundException {
        EditUserProfileDto userDto = getEditUserProfileDto();
        MockHttpServletResponse response = new MockHttpServletResponse();
        BindingResult bindingResult = new BeanPropertyBindingResult(userDto, "editedUser");
        when(userService.getCurrentUser()).thenReturn(getUser());
        when(userService.saveEditedUserProfile(anyLong(), any(UserInfoContainer.class)))
            .thenThrow(new NotFoundException());
        
        profileController.saveEditedProfile(userDto, bindingResult, response);
    }

    @Test
    public void showUserProfileShouldReturnUserView() throws NotFoundException {
        JCUser user = getUser();
        user.setId(USER_ID);
        when(userService.getCurrentUser()).thenReturn(user);
        when(userService.get(USER_ID)).thenReturn(user);

        ModelAndView mav = profileController.showUserProfile(USER_ID);

        assertViewName(mav, "editProfile");
    }

    @Test
    public void showUserSecuritySettingsShouldReturnUserView() throws NotFoundException {
        JCUser user = getUser();
        user.setId(USER_ID);
        when(userService.getCurrentUser()).thenReturn(user);
        when(userService.get(USER_ID)).thenReturn(user);

        ModelAndView mav = profileController.showUserSecuritySettings(USER_ID);

        assertViewName(mav, "editProfile");
    }

    @Test
    public void showUserNotificationSettingsShouldReturnUserView() throws NotFoundException {
        JCUser user = getUser();
        user.setId(USER_ID);
        when(userService.getCurrentUser()).thenReturn(user);
        when(userService.get(USER_ID)).thenReturn(user);

        ModelAndView mav = profileController.showUserNotificationSettings(USER_ID);

        assertViewName(mav, "editProfile");
    }

    @Test
    public void showUserContactsShouldReturnUserView() throws NotFoundException {
        JCUser user = getUser();
        user.setId(USER_ID);
        when(userService.getCurrentUser()).thenReturn(user);
        when(userService.get(USER_ID)).thenReturn(user);

        ModelAndView mav = profileController.showUserContacts(USER_ID);

        assertViewName(mav, "editProfile");
    }

    @Test(expectedExceptions = AccessDeniedException.class)
    public void showUserNotificationSettingsShouldThrowAccessExceptionIfUserDoesNotHavePermissionToEditOtherProfiles()
            throws NotFoundException {
        JCUser user = getUser();
        JCUser editorUser = getUser();
        user.setId(USER_ID);
        editorUser.setId(USER_ID + 1);

        when(userService.getCurrentUser()).thenReturn(editorUser);
        doThrow(new AccessDeniedException(StringUtils.EMPTY))
                .when(userService).checkPermissionToEditOtherProfiles(editorUser.getId());

        ModelAndView mav = profileController.showUserNotificationSettings(USER_ID);

        assertViewName(mav, "editProfile");
    }

    @Test(expectedExceptions = AccessDeniedException.class)
    public void showUserSecuritySettingsShouldThrowAccessExceptionIfUserDoesNotHavePermissionToEditOtherProfiles()
            throws NotFoundException {
        JCUser user = getUser();
        JCUser editorUser = getUser();
        user.setId(USER_ID);
        editorUser.setId(USER_ID + 1);

        when(userService.getCurrentUser()).thenReturn(editorUser);
        doThrow(new AccessDeniedException(StringUtils.EMPTY))
                .when(userService).checkPermissionToEditOtherProfiles(editorUser.getId());

        ModelAndView mav = profileController.showUserSecuritySettings(USER_ID);

        assertViewName(mav, "editProfile");
    }


    @Test
    public void saveEditedProfileWithCorrectDataShouldSaveProfileSettings() throws NotFoundException {
        JCUser user = getUser();
        user.setId(USER_ID);
        when(userService.getCurrentUser()).thenReturn(user);
        when(userService.get(user.getId())).thenReturn(user);

        EditUserProfileDto dto = getEditUserProfileDto(user);
        dto.setUserProfileDto(new UserProfileDto(user));
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);
        when(userService.saveEditedUserProfile(eq(user.getId()), any(UserInfoContainer.class))).thenReturn(user);

        ModelAndView mav = profileController.saveEditedProfile(dto, bindingResult, new MockHttpServletResponse());

        String expectedUrl = "redirect:/users/" + user.getId() + "/" + UserProfileController.PROFILE;
        assertViewName(mav, expectedUrl);
        verify(userService, times(1)).saveEditedUserProfile(eq(user.getId()), any(UserInfoContainer.class));
    }

    @Test
    public void saveEditedSecurityWithCorrectDataShouldSaveSecuritySettings() throws NotFoundException {
        JCUser user = getUser();
        user.setId(USER_ID);
        when(userService.getCurrentUser()).thenReturn(user);
        when(userService.get(user.getId())).thenReturn(user);

        EditUserProfileDto dto = getEditUserProfileDto(user);
        UserSecurityDto userSecurityDto = new UserSecurityDto(user);
        userSecurityDto.setNewUserPassword("new_password");
        dto.setUserSecurityDto(userSecurityDto);
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);
        when(userService.saveEditedUserSecurity(eq(user.getId()), any(UserSecurityContainer.class))).thenReturn(user);

        ModelAndView mav = profileController.saveEditedSecurity(dto, bindingResult, new MockHttpServletResponse(),
                redirectAttributes);

        String expectedUrl = "redirect:/users/" + user.getId() + "/" + UserProfileController.SECURITY;
        assertViewName(mav, expectedUrl);
        verify(userService, times(1)).saveEditedUserSecurity(eq(user.getId()), any(UserSecurityContainer.class));
        verify(redirectAttributes, times(1)).addFlashAttribute(profileController.IS_PASSWORD_CHANGED_ATTRIB, true);
    }

    @Test
    public void saveEditedNotificationsWithCorrectDataShouldSaveNotificationsSettings() throws NotFoundException {
        JCUser user = getUser();
        user.setId(USER_ID);
        when(userService.getCurrentUser()).thenReturn(user);
        when(userService.get(user.getId())).thenReturn(user);

        EditUserProfileDto dto = getEditUserProfileDto(user);
        dto.setUserNotificationsDto(new UserNotificationsDto(user));
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);
        when(userService.saveEditedUserNotifications(eq(user.getId()), any(UserNotificationsContainer.class)))
                .thenReturn(user);

        ModelAndView mav = profileController.saveEditedNotifications(dto, bindingResult, new MockHttpServletResponse());

        String expectedUrl = "redirect:/users/" + user.getId() + "/" + UserProfileController.NOTIFICATIONS;
        assertViewName(mav, expectedUrl);
        verify(userService, times(1)).saveEditedUserNotifications(eq(user.getId()),
                any(UserNotificationsContainer.class));
    }

    @Test
    public void saveEditedContactsWithCorrectDataShouldSaveUserContacts() throws NotFoundException {
        JCUser user = getUser();
        user.setId(USER_ID);
        when(userService.getCurrentUser()).thenReturn(user);
        when(userService.get(user.getId())).thenReturn(user);

        EditUserProfileDto dto = getEditUserProfileDto(user);
        dto.setUserContactsDto(new UserContactsDto(user));
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);
        when(userContactsService.saveEditedUserContacts(eq(user.getId()), any(List.class)))
                .thenReturn(user);

        ModelAndView mav = profileController.saveEditedContacts(dto, bindingResult, new MockHttpServletResponse());

        String expectedUrl = "redirect:/users/" + user.getId() + "/" + UserProfileController.CONTACTS;
        assertViewName(mav, expectedUrl);
        verify(userContactsService, times(1)).saveEditedUserContacts(eq(user.getId()), any(List.class));
    }

    @Test
    public void saveEditedProfileWithValidationErrorsShouldShowThemToUser() throws NotFoundException {
        JCUser user = getUser();
        when(userService.getCurrentUser()).thenReturn(user);
        when(userService.get(user.getId())).thenReturn(user);

        EditUserProfileDto dto = getEditUserProfileDto(user);
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(true);

        ModelAndView mav = profileController.saveEditedProfile(dto, bindingResult, new MockHttpServletResponse());

        assertViewName(mav, "editProfile");
        verify(userService, never()).saveEditedUserProfile(anyLong() , any(UserInfoContainer.class));
    }

    @Test
    public void saveEditedNotificationsWithValidationErrorsShouldShowThemToUser() throws NotFoundException {
        JCUser user = getUser();
        when(userService.getCurrentUser()).thenReturn(user);
        when(userService.get(user.getId())).thenReturn(user);

        EditUserProfileDto dto = getEditUserProfileDto(user);
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(true);

        ModelAndView mav = profileController.saveEditedNotifications(dto, bindingResult, new MockHttpServletResponse());

        assertViewName(mav, "editProfile");
        verify(userService, never()).saveEditedUserNotifications(anyLong(), any(UserNotificationsContainer.class));
    }

    @Test
    public void saveEditedSecurityWithValidationErrorsShouldShowThemToUser() throws NotFoundException {
        JCUser user = getUser();
        when(userService.getCurrentUser()).thenReturn(user);
        when(userService.get(user.getId())).thenReturn(user);

        EditUserProfileDto dto = getEditUserProfileDto(user);
        UserSecurityDto userSecurityDto = new UserSecurityDto(user);
        userSecurityDto.setNewUserPassword("new password");
        dto.setUserSecurityDto(userSecurityDto);
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(true);

        ModelAndView mav = profileController.saveEditedSecurity(dto, bindingResult, new MockHttpServletResponse(),
                redirectAttributes);

        assertViewName(mav, "editProfile");
        verify(userService, never()).saveEditedUserSecurity(anyLong(), any(UserSecurityContainer.class));
        verify(redirectAttributes, never()).addFlashAttribute(profileController.IS_PASSWORD_CHANGED_ATTRIB, true);
    }

    @Test
    public void saveEditedContactsWithValidationErrorsShouldShowThemToUser() throws NotFoundException {
        JCUser user = getUser();
        when(userService.getCurrentUser()).thenReturn(user);
        when(userService.get(user.getId())).thenReturn(user);

        EditUserProfileDto dto = getEditUserProfileDto(user);
        dto.setUserContactsDto(new UserContactsDto(user));

        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(true);

        ModelAndView mav = profileController.saveEditedContacts(dto, bindingResult, new MockHttpServletResponse());

        assertViewName(mav, "editProfile");
        verify(userContactsService, never()).saveEditedUserContacts(anyLong(), any(List.class));
    }

    @Test(expectedExceptions = AccessDeniedException.class)
    public void saveEditedProfileShouldShowErrorIfUserDoesNotHavePermissionToEditOwnProfile() throws NotFoundException {
        JCUser user = getUser();
        user.setId(USER_ID);
        EditUserProfileDto userDto = getEditUserProfileDto();
        userDto.setUserProfileDto(new UserProfileDto(user));
        MockHttpServletResponse response = new MockHttpServletResponse();
        when(userService.getCurrentUser()).thenReturn(user);
        doThrow(new AccessDeniedException(StringUtils.EMPTY)).when(userService)
                .checkPermissionToEditOwnProfile(user.getId());
        BindingResult bindingResult = new BeanPropertyBindingResult(userDto, "editedUser");

        profileController.saveEditedProfile(userDto, bindingResult, response);
    }

    @Test(expectedExceptions = AccessDeniedException.class)
    public void saveEditedContactsShouldShowErrorIfUserDoesNotHavePermissionToEditOwnProfile() throws NotFoundException {
        JCUser user = getUser();
        user.setId(USER_ID);
        EditUserProfileDto userDto = getEditUserProfileDto(user);
        userDto.setUserContactsDto(new UserContactsDto(user));

        MockHttpServletResponse response = new MockHttpServletResponse();
        when(userService.getCurrentUser()).thenReturn(user);
        doThrow(new AccessDeniedException(StringUtils.EMPTY)).when(userService)
                .checkPermissionToEditOwnProfile(user.getId());

        BindingResult bindingResult = new BeanPropertyBindingResult(userDto, "editedUser");

        profileController.saveEditedContacts(userDto, bindingResult, response);
    }
    
    @Test(expectedExceptions = AccessDeniedException.class)
    public void saveEditedProfileShouldShowErrorIfUserDoesNotHavePermissionToEditOtherProfile() throws NotFoundException {
        JCUser editedUser = getUser();
        editedUser.setId(USER_ID);
        JCUser editorUser = getUser();
        editorUser.setId(USER_ID + 1);
        EditUserProfileDto userDto = getEditUserProfileDto(editedUser);
        userDto.setUserProfileDto(new UserProfileDto(editedUser));

        when(userService.getCurrentUser()).thenReturn(editorUser);
        doThrow(new AccessDeniedException(StringUtils.EMPTY)).when(userService)
                .checkPermissionToEditOtherProfiles(editorUser.getId());

        BindingResult bindingResult = new BeanPropertyBindingResult(userDto, "editedUser");

        profileController.saveEditedProfile(userDto, bindingResult, null);
    }

    @Test(expectedExceptions = AccessDeniedException.class)
    public void saveEditedNotificationsShouldShowErrorIfUserDoesNotHavePermissionToEditOtherProfile()
            throws NotFoundException {
        JCUser editedUser = getUser();
        editedUser.setId(USER_ID);
        JCUser editorUser = getUser();
        editorUser.setId(USER_ID + 1);
        EditUserProfileDto userDto = getEditUserProfileDto(editedUser);
        userDto.setUserNotificationsDto(new UserNotificationsDto(editedUser));

        when(userService.getCurrentUser()).thenReturn(editorUser);
        doThrow(new AccessDeniedException(StringUtils.EMPTY))
                .when(userService).checkPermissionToEditOtherProfiles(editorUser.getId());

        BindingResult bindingResult = new BeanPropertyBindingResult(userDto, "editedUser");

        profileController.saveEditedNotifications(userDto, bindingResult, null);
    }

    @Test(expectedExceptions = AccessDeniedException.class)
    public void saveEditedSecurityShouldShowErrorIfUserDoesNotHavePermissionToEditOtherProfile()
            throws NotFoundException {
        JCUser editedUser = getUser();
        editedUser.setId(USER_ID);
        JCUser editorUser = getUser();
        editorUser.setId(USER_ID + 1);
        EditUserProfileDto userDto = getEditUserProfileDto(editedUser);
        userDto.setUserSecurityDto(new UserSecurityDto(editedUser));

        when(userService.getCurrentUser()).thenReturn(editorUser);
        doThrow(new AccessDeniedException(StringUtils.EMPTY))
                .when(userService).checkPermissionToEditOtherProfiles(editorUser.getId());

        BindingResult bindingResult = new BeanPropertyBindingResult(userDto, "editedUser");

        profileController.saveEditedSecurity(userDto, bindingResult, null, redirectAttributes);
    }

    @Test(expectedExceptions = AccessDeniedException.class)
    public void saveEditedContactsShouldShowErrorIfUserDoesNotHavePermissionToEditOtherProfile()
            throws NotFoundException {
        JCUser editedUser = getUser();
        editedUser.setId(USER_ID);
        JCUser editorUser = getUser();
        editorUser.setId(USER_ID + 1);
        EditUserProfileDto userDto = getEditUserProfileDto(editedUser);
        userDto.setUserContactsDto(new UserContactsDto(editedUser));

        when(userService.getCurrentUser()).thenReturn(editorUser);
        doThrow(new AccessDeniedException(StringUtils.EMPTY))
                .when(userService).checkPermissionToEditOtherProfiles(editorUser.getId());

        BindingResult bindingResult = new BeanPropertyBindingResult(userDto, "editedUser");

        profileController.saveEditedContacts(userDto, bindingResult, null);
    }

    @Test
    public void testInitBinder() {
        when(userService.getCurrentUser()).thenReturn(getUser());
        WebDataBinder binder = mock(WebDataBinder.class);
        
        profileController.initBinder(binder);
        
        verify(binder).registerCustomEditor(eq(String.class), any(StringTrimmerEditor.class));
    }

    @Test
    public void showUserPostListShouldShowThemToUser() throws NotFoundException {
        JCUser user = new JCUser("username", "email", "password");
        user.setPageSize(5);
        Post post = mock(Post.class);
        Topic topic = mock(Topic.class);
        List<Post> posts = new ArrayList<>();
        posts.add(post);
        Page<Post> postsPage = new PageImpl<>(posts);
        //
        when(userService.getByUsername("username")).thenReturn(user);
        when(breadcrumbBuilder.getForumBreadcrumb()).thenReturn(new ArrayList<Breadcrumb>());
        when(postService.getPostsOfUser(any(JCUser.class), anyString()))
            .thenReturn(postsPage);
        when(userService.getCurrentUser()).thenReturn(user);
        when(post.getTopic()).thenReturn(topic);

        ModelAndView mav = profileController.showUserPostList(user.getId(), "1");

        verify(userService).get(user.getId());
        verify(converter).convertPostPageToPostDtoPage(any(Page.class));
        assertViewName(mav, "userPostList");
        assertModelAttributeAvailable(mav, "user");
        assertModelAttributeAvailable(mav, "breadcrumbList");
        assertModelAttributeAvailable(mav, "user");
        assertModelAttributeAvailable(mav, "postsPage");
    }

    @Test
    public void testSaveUserLanguage() throws ServletException {
        LocaleResolver localeResolver = mock(LocaleResolver.class);
        JCUser user = getUser();
        MockHttpServletRequest reuqest = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        
        reuqest.setAttribute(LOCALE_RESOLVER_ATTRIBUTE, localeResolver);
        when(userService.getCurrentUser()).thenReturn(user);
        
        profileController.saveUserLanguage("ru", response, reuqest);
        
        verify(userService).changeLanguage(user, Language.RUSSIAN);
        verify(localeResolver).setLocale(reuqest, response, Language.RUSSIAN.getLocale());
    }
    
    /**
     * @return {@link EditUserProfileDto} with default values
     */
    private EditUserProfileDto getEditUserProfileDto() {
        JCUser user = new JCUser("username", EMAIL, PASSWORD);
        user.setId(USER_ID);
        user.setFirstName(FIRST_NAME);
        user.setLastName(LAST_NAME);
        user.setSignature(SIGNATURE);
        user.setLanguage(LANGUAGE);
        user.setPageSize(PAGE_SIZE);
        user.setAutosubscribe(AUTOSUBSCRIBE);

        return new EditUserProfileDto(user);
    }

    /**
     * @return {@link EditUserProfileDto} retrieved from user
     */
    private EditUserProfileDto getEditUserProfileDto(JCUser user) {
        return new EditUserProfileDto(user);
    }

    private JCUser getUser() {
        JCUser newUser = new JCUser(USER_NAME, EMAIL, PASSWORD);
        newUser.setFirstName(FIRST_NAME);
        newUser.setLastName(LAST_NAME);
        newUser.setSignature(SIGNATURE);
        newUser.setLanguage(LANGUAGE);
        newUser.setPageSize(PAGE_SIZE);
        newUser.setAutosubscribe(AUTOSUBSCRIBE);
        newUser.setAvatar(avatarByteArray);
        UserContact contact = new UserContact("test contact", new UserContactType());
        newUser.addContact(contact);
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
