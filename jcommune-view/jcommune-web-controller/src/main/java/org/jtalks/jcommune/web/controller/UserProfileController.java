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

import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.model.entity.Language;
import org.jtalks.jcommune.model.entity.Post;
import org.jtalks.jcommune.service.PostService;
import org.jtalks.jcommune.service.UserContactsService;
import org.jtalks.jcommune.service.UserService;
import org.jtalks.jcommune.service.exceptions.NotFoundException;
import org.jtalks.jcommune.service.nontransactional.ImageConverter;
import org.jtalks.jcommune.web.dto.*;
import org.jtalks.jcommune.web.util.BreadcrumbBuilder;
import org.jtalks.jcommune.web.validation.editors.DefaultStringEditor;
import org.jtalks.jcommune.web.validation.editors.PageSizeEditor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.data.domain.Page;
import org.springframework.orm.hibernate3.HibernateOptimisticLockingFailureException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Locale;

/**
 * Controller for User related actions: registration, user profile operations and so on.
 *
 * @author Kirill Afonin
 * @author Alexandre Teterin
 * @author Max Malakhov
 * @author Eugeny Batov
 * @author Evgeniy Naumenko
 * @author Anuar_Nurmakanov
 * @author Andrey Pogorelov
 */
@Controller
public class UserProfileController {

    /**
     * We need this properties for determining
     * the desired operation while saving user
     */
    public static final String SECURITY = "security";
    public static final String PROFILE = "profile";
    public static final String NOTIFICATIONS = "notifications";
    public static final String CONTACTS = "contacts";
    
    public static final String EDIT_PROFILE = "editProfile";
    public static final String EDITED_USER = "editedUser";
    public static final String BREADCRUMB_LIST = "breadcrumbList";
    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);


    private UserService userService;
    private BreadcrumbBuilder breadcrumbBuilder;
    private ImageConverter imageConverter;
    private PostService postService;
    private UserContactsService contactsService;

    /**
     * This method turns the trim binder on. Trim binder
     * removes leading and trailing spaces from the submitted fields.
     * So, it ensures, that all validations will be applied to
     * trimmed field values only.
     * <p/> There is no need for trim edit password fields,
     * so they are processed with {@link DefaultStringEditor}
     *
     * @param binder Binder object to be injected
     */
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(String.class, "userSecurityDto.currentUserPassword", new DefaultStringEditor(true));
        binder.registerCustomEditor(String.class, "userSecurityDto.newUserPassword", new DefaultStringEditor(true));
        binder.registerCustomEditor(String.class, "userSecurityDto.newUserPasswordConfirm",
                new DefaultStringEditor(true));
        binder.registerCustomEditor(Integer.class, "userProfileDto.pageSize", new PageSizeEditor());
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
    }

    /**
     * @param userService       to get current user and user by id
     * @param breadcrumbBuilder the object which provides actions on {@link BreadcrumbBuilder} entity
     * @param imageConverter    to prepare user avatar for view
     * @param postService       to get all user's posts
     * @param contactsService   for edit user contacts
     */
    @Autowired
    public UserProfileController(UserService userService,
                                 BreadcrumbBuilder breadcrumbBuilder,
                                 @Qualifier("avatarPreprocessor")
                                 ImageConverter imageConverter,
                                 PostService postService,
                                 UserContactsService contactsService) {
        this.userService = userService;
        this.breadcrumbBuilder = breadcrumbBuilder;
        this.imageConverter = imageConverter;
        this.postService = postService;
        this.contactsService = contactsService;
    }

    /**
     * This method is a shortcut for user profile access. It may be usefull when we haven't got
     * the specific id, but simply want to access current user's profile.
     * <p/>
     * Requires user to be authorized.
     *
     * @return user details view with {@link JCUser} object.
     */
    @RequestMapping(value = "/user", method = RequestMethod.GET)
    public ModelAndView showCurrentUserProfilePage() {
        JCUser user = userService.getCurrentUser();
        return getUserProfileModelAndView(user, PROFILE);
    }

    /**
     * Formats model and view for representing user's details
     *
     * @param user user
     * @param settingsType type of user settings (profile, contacts, security or notifications)
     * @return user's details
     */
    private ModelAndView getUserProfileModelAndView(JCUser user, String settingsType) {
        EditUserProfileDto editedUserDto;
        switch(settingsType) {
            case CONTACTS:
                editedUserDto = new EditUserProfileDto(new UserContactsDto(user), user);
                editedUserDto.getUserContactsDto().setContactTypes(contactsService.getAvailableContactTypes());
                break;
            case NOTIFICATIONS:
                editedUserDto = new EditUserProfileDto(new UserNotificationsDto(user), user);
                break;
            case SECURITY:
                editedUserDto = new EditUserProfileDto(new UserSecurityDto(user), user);
                break;
            default:
                editedUserDto = new EditUserProfileDto(new UserProfileDto(user), user);
        }
        setAvatarToUserProfileView(editedUserDto, user);
        return new ModelAndView(EDIT_PROFILE, EDITED_USER, editedUserDto);
    }

    /**
     * Show user profile page for specified user.
     *
     * @return edit user profile page
     * @throws NotFoundException throws if current logged in user was not found
     */
    @RequestMapping(value = {"/users/{editedUserId}/profile", "/users/{editedUserId}"}, method = RequestMethod.GET)
    public ModelAndView showUserProfile(@PathVariable Long editedUserId) throws NotFoundException {
        JCUser editedUser = userService.get(editedUserId);
        return getUserProfileModelAndView(editedUser, PROFILE);
    }

    /**
     * Show user contacts page for specified user.
     *
     * @return edit user contacts page
     * @throws NotFoundException throws if current logged in user was not found
     */
    @RequestMapping(value = "/users/{editedUserId}/contacts", method = RequestMethod.GET)
    public ModelAndView showUserContacts(@PathVariable Long editedUserId) throws NotFoundException {
        JCUser editedUser = userService.get(editedUserId);
        return getUserProfileModelAndView(editedUser, CONTACTS);
    }

    /**
     * Show user notifications page for specified user.
     *
     * @return edit user notifications page
     * @throws NotFoundException throws if current logged in user was not found
     */
    @RequestMapping(value = "/users/{editedUserId}/notifications", method = RequestMethod.GET)
    public ModelAndView showUserNotificationSettings(@PathVariable Long editedUserId) throws NotFoundException {
        checkPermissionForEditNotificationsOrSecurity(editedUserId);
        JCUser editedUser = userService.get(editedUserId);
        return getUserProfileModelAndView(editedUser, NOTIFICATIONS);
    }

    /**
     * Show user security page for specified user.
     *
     * @return edit user security page
     * @throws NotFoundException throws if current logged in user was not found
     */
    @RequestMapping(value = "/users/{editedUserId}/security", method = RequestMethod.GET)
    public ModelAndView showUserSecuritySettings(@PathVariable Long editedUserId) throws NotFoundException {
        checkPermissionForEditNotificationsOrSecurity(editedUserId);
        JCUser editedUser = userService.get(editedUserId);
        return getUserProfileModelAndView(editedUser, SECURITY);
    }

    /**
     * Set avatar to data transfer object for view.
     *
     * @param user passed user
     */
    private void setAvatarToUserProfileView(EditUserProfileDto editUserProfileDto, JCUser user) {
        byte[] avatar = user.getAvatar();
        editUserProfileDto.setAvatar(imageConverter.prepareHtmlImgSrc(avatar));
    }

    /**
     * Update user profile info. Check if the user enter valid data and update profile in database.
     * In error case return into the edit profile page and draw the error.
     * <p/>
     *
     * @param editedProfileDto dto populated by user
     * @param result           binding result which contains the validation result
     * @param response         http servlet response
     * @return return to user profile page
     * @throws NotFoundException if edited user doesn't exist in system
     */
    @RequestMapping(value = "/users/*/profile", method = RequestMethod.POST)
    public ModelAndView saveEditedProfile(@Valid @ModelAttribute(EDITED_USER) EditUserProfileDto editedProfileDto,
                                          BindingResult result, HttpServletResponse response) throws NotFoundException {
        if (result.hasErrors()) {
            return new ModelAndView(EDIT_PROFILE, EDITED_USER, editedProfileDto);
        }
        long editedUserId = editedProfileDto.getUserProfileDto().getUserId();
        checkPermissionsToEditProfile(editedUserId);
        JCUser user = saveEditedProfileWithLockHandling(editedUserId, editedProfileDto, PROFILE);
        //redirect to the view profile page
        return new ModelAndView("redirect:/users/" + user.getId() +"/" + PROFILE);
    }

    /**
     * Update user notification settings. Check if the user enter valid data and update settings in database.
     * In error case return into the edit profile page and draw the error.
     * <p/>
     *
     * @param editedProfileDto dto populated by user
     * @param result           binding result which contains the validation result
     * @param response         http servlet response
     * @return in case of errors return back to edit notifications page, in another case return to user profile page
     * @throws NotFoundException if edited user doesn't exist in system
     */
    @RequestMapping(value = "/users/*/notifications", method = RequestMethod.POST)
    public ModelAndView saveEditedNotifications(@Valid @ModelAttribute(EDITED_USER) EditUserProfileDto editedProfileDto,
                                          BindingResult result, HttpServletResponse response) throws NotFoundException {
        if (result.hasErrors()) {
            return new ModelAndView(EDIT_PROFILE, EDITED_USER, editedProfileDto);
        }
        long editedUserId = editedProfileDto.getUserNotificationsDto().getUserId();
        checkPermissionForEditNotificationsOrSecurity(editedUserId);
        JCUser user = saveEditedProfileWithLockHandling(editedUserId, editedProfileDto, NOTIFICATIONS);
        //redirect to the view profile page
        return new ModelAndView("redirect:/users/" + user.getId() +"/" + NOTIFICATIONS);
    }

    /**
     * Update user security info. Check if the user enter valid data and update user security info in database.
     * In error case return into the edit profile page and draw the error.
     * <p/>
     *
     * @param editedProfileDto dto populated by user
     * @param result           binding result which contains the validation result
     * @param response         http servlet response
     * @return in case of errors return back to edit security page, in another case return to user profile page
     * @throws NotFoundException if edited user doesn't exist in system
     */
    @RequestMapping(value = "/users/*/security", method = RequestMethod.POST)
    public ModelAndView saveEditedSecurity(@Valid @ModelAttribute(EDITED_USER) EditUserProfileDto editedProfileDto,
                                          BindingResult result, HttpServletResponse response) throws NotFoundException {
        if (result.hasErrors()) {
            return new ModelAndView(EDIT_PROFILE, EDITED_USER, editedProfileDto);
        }
        long editedUserId = editedProfileDto.getUserSecurityDto().getUserId();
        checkPermissionForEditNotificationsOrSecurity(editedUserId);
        JCUser user = saveEditedProfileWithLockHandling(editedUserId, editedProfileDto, SECURITY);
        //redirect to the view profile page
        return new ModelAndView("redirect:/users/" + user.getId() +"/" + SECURITY);
    }

    /**
     * Update user contacts. Check if the user enter valid data and update contacts in database.
     * In error case return into the edit profile page and draw the error.
     * <p/>
     *
     * @param editedProfileDto dto populated by user
     * @param result           binding result which contains the validation result
     * @param response         http servlet response
     * @return in case of errors return back to edit contacts page, in another case return to user profile page
     * @throws NotFoundException if edited user doesn't exist in system
     */
    @RequestMapping(value = "/users/*/contacts", method = RequestMethod.POST)
    public ModelAndView saveEditedContacts(@Valid @ModelAttribute(EDITED_USER) EditUserProfileDto editedProfileDto,
                                           BindingResult result, HttpServletResponse response) throws NotFoundException {
        if (result.hasErrors()) {
            editedProfileDto.getUserContactsDto().setContactTypes(contactsService.getAvailableContactTypes());
            return new ModelAndView(EDIT_PROFILE, EDITED_USER, editedProfileDto);
        }
        long editedUserId = editedProfileDto.getUserId();
        checkPermissionsToEditProfile(editedUserId);
        JCUser user = saveEditedProfileWithLockHandling(editedUserId, editedProfileDto, CONTACTS);
        //redirect to the view profile page
        return new ModelAndView("redirect:/users/" + user.getId() +"/" + CONTACTS);
    }

    /**
     * User doesn't need to have permission to edit his password and notifications.
     * For other users we have to check permission to edit other profiles.
     *
     * @param editedUserId an identifier of edited user
     * @see <a href="http://jira.jtalks.org/browse/JC-1740">JC-1740</a>
     */
    private void checkPermissionForEditNotificationsOrSecurity(long editedUserId) {
        JCUser editorUser = userService.getCurrentUser();
        if (editorUser.getId() != editedUserId) {
            userService.checkPermissionToEditOtherProfiles(editorUser.getId());
        }
    }

    /**
     * User must have permissions to edit own or other profiles.
     * So we must check them for users, who try to edit profiles.
     *
     * @param editedUserId an identifier of edited user
     */
    private void checkPermissionsToEditProfile(long editedUserId) {
        JCUser editorUser = userService.getCurrentUser();
        if (editorUser.getId() == editedUserId) {
            userService.checkPermissionToEditOwnProfile(editorUser.getId());
        } else {
            userService.checkPermissionToEditOtherProfiles(editorUser.getId());
        }
    }

    /**
     * Show page with post of user.
     * SpEL pattern in a var name indicates we want to consume all the symbols in a var,
     * even dots, which Spring MVC uses as file extension delimiters by default.
     *
     * @param page number current page
     * @param id   database user identifier
     * @return post list of user
     * @throws NotFoundException if user with given id not found.
     */
    @RequestMapping(value = "/users/{id}/postList", method = RequestMethod.GET)
    public ModelAndView showUserPostList(@PathVariable Long id,
                                         @RequestParam(value = "page", defaultValue = "1",
                                                 required = false) String page) throws NotFoundException {
        JCUser user = userService.get(id);
        Page<Post> postsPage = postService.getPostsOfUser(user, page);
        return new ModelAndView("userPostList")
                .addObject("user", user)
                .addObject("postsPage", postsPage)
                .addObject(BREADCRUMB_LIST, breadcrumbBuilder.getForumBreadcrumb());
    }

    @RequestMapping(value = "**/language", method = RequestMethod.GET)
    public String saveUserLanguage(@RequestParam(value = "lang", defaultValue = "en") String lang,
                                   HttpServletResponse response, HttpServletRequest request) throws ServletException {
        JCUser jcuser = userService.getCurrentUser();
        Language languageFromRequest = Language.byLocale(new Locale(lang));
        if (!jcuser.isAnonymous()) {
            changeLanguageWithLockHandling(jcuser, languageFromRequest);
        }
        LocaleResolver localeResolver = RequestContextUtils.getLocaleResolver(request);
        localeResolver.setLocale(request, response, languageFromRequest.getLocale());
        return "redirect:" + request.getHeader("Referer");
    }

    private void changeLanguageWithLockHandling(JCUser user, Language language) {
        for (int i = 0; i < UserController.LOGIN_TRIES_AFTER_LOCK; i++) {
            try {
                userService.changeLanguage(user, language);
                return;
            } catch (HibernateOptimisticLockingFailureException ignored) {
            }
        }
        try {
            userService.changeLanguage(user, language);
        } catch (HibernateOptimisticLockingFailureException e) {
            LOGGER.error("User has been optimistically locked and can't be reread {} times. Username: {}",
                    UserController.LOGIN_TRIES_AFTER_LOCK, user.getUsername());
            throw e;
        }
    }

    /**
     * Save user profile settings depending on settings type.
     *
     * @param userId user Id
     * @param userProfileDto dto with user settings
     * @param settingsType user settings type
     * @return updated user
     * @throws NotFoundException
     */
    private JCUser saveUserData(long userId, EditUserProfileDto userProfileDto, String settingsType)
            throws NotFoundException {
        switch(settingsType) {
            case SECURITY:
                return userService.saveEditedUserSecurity(userId, userProfileDto.getUserSecurityContainer());
            case NOTIFICATIONS:
                return userService.saveEditedUserNotifications(userId, userProfileDto.getUserNotificationsContainer());
            case CONTACTS:
                return contactsService.saveEditedUserContacts(userId, userProfileDto.getUserContacts());
            default:
                return userService.saveEditedUserProfile(userId, userProfileDto.getUserInfoContainer());
        }
    }

    private JCUser saveEditedProfileWithLockHandling(long editedUserId, EditUserProfileDto editedProfileDto,
                                                     String settingsType)
            throws NotFoundException {
        for (int i = 0; i < UserController.LOGIN_TRIES_AFTER_LOCK; i++) {
            try {
                return saveUserData(editedUserId, editedProfileDto, settingsType);
            } catch (HibernateOptimisticLockingFailureException ignored) {
            }
        }
        try {
            return saveUserData(editedUserId, editedProfileDto, settingsType);
        } catch (HibernateOptimisticLockingFailureException e) {
            LOGGER.error("User has been optimistically locked and can't be reread {} times. Username: {}",
                    UserController.LOGIN_TRIES_AFTER_LOCK, editedProfileDto.getUsername());
            throw e;
        }
    }
}
