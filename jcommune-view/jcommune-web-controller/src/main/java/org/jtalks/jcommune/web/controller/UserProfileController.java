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
import org.jtalks.jcommune.service.UserService;
import org.jtalks.jcommune.service.exceptions.NotFoundException;
import org.jtalks.jcommune.service.nontransactional.ImageConverter;
import org.jtalks.jcommune.web.dto.EditUserProfileDto;
import org.jtalks.jcommune.web.util.BreadcrumbBuilder;
import org.jtalks.jcommune.web.validation.editors.DefaultStringEditor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
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
 */
@Controller
public class UserProfileController {
    public static final String EDIT_PROFILE = "editProfile";
    public static final String EDITED_USER = "editedUser";
    public static final String BREADCRUMB_LIST = "breadcrumbList";


    private UserService userService;
    private BreadcrumbBuilder breadcrumbBuilder;
    private ImageConverter imageConverter;
    private PostService postService;

    /**
     * This method turns the trim binder on. Trim binder
     * removes leading and trailing spaces from the submitted fields.
     * So, it ensures, that all validations will be applied to
     * trimmed field values only.
     * <p/> There is no need for trim edit password fields,
     * so they are processed with {@link DefaultStringEditor}
     * @param binder Binder object to be injected
     */
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(String.class, "newUserPassword", new DefaultStringEditor(true));
        binder.registerCustomEditor(String.class, "newUserPasswordConfirm", new DefaultStringEditor(true));
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
    }

    /**
     * @param userService       to get current user and user by id
     * @param breadcrumbBuilder the object which provides actions on {@link BreadcrumbBuilder} entity
     * @param imageConverter        to prepare user avatar for view
     * @param postService       to get all user's posts
     */
    @Autowired
    public UserProfileController(UserService userService,
                                 BreadcrumbBuilder breadcrumbBuilder,
                                 @Qualifier("avatarPreprocessor")
                                 ImageConverter imageConverter,
                                 PostService postService) {
        this.userService = userService;
        this.breadcrumbBuilder = breadcrumbBuilder;
        this.imageConverter = imageConverter;
        this.postService = postService;
    }

    /**
     * Show user profile page with user info.
     *
     * @param id user identifier
     * @return user details view with {@link JCUser} object.
     * @throws NotFoundException if user with given id not found.
     */
    @RequestMapping(value = "/users/{id}", method = RequestMethod.GET)
    public ModelAndView showProfilePage(@PathVariable Long id) throws NotFoundException {
        JCUser user = userService.get(id);
        return getUserProfileModelAndView(user);
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
        return getUserProfileModelAndView(user);
    }
    
    /**
     * Formats model and view for representing user's details
     *
     * @param user  user
     * @return user's details
     */
    private ModelAndView getUserProfileModelAndView(JCUser user){
        return new ModelAndView("userDetails")
                .addObject("user", user)
                // bind separately to get localized value
                .addObject("language", user.getLanguage());
    }

    /**
     * Show edit user profile page for current logged in user.
     *
     * @return edit user profile page
     * @throws NotFoundException throws if current logged in user was not found
     */
    @RequestMapping(value = "/users/edit/{editedUserId}", method = RequestMethod.GET)
    public ModelAndView startEditUserProfile(@PathVariable Long editedUserId) throws NotFoundException {
        checkPermissionsToEditProfile(editedUserId);
        JCUser editedUser = userService.get(editedUserId);
        EditUserProfileDto editedUserDto = convertUserForView(editedUser);
        ModelAndView mav = new ModelAndView(EDIT_PROFILE, EDITED_USER, editedUserDto);
        mav.addObject("contacts", editedUser.getUserContacts());
        return mav;
    }
    
    /**
     * Converts passed user to data transfer object for view. 
     * 
     * @param user passed user
     * @return data transfer object for view
     */
    private EditUserProfileDto convertUserForView(JCUser user) {
        EditUserProfileDto editUserProfileDto = new EditUserProfileDto(user);
        byte[] avatar = user.getAvatar();
        editUserProfileDto.setAvatar(imageConverter.prepareHtmlImgSrc(avatar));
        return editUserProfileDto;
    }

    /**
     * Update user profile info. Check if the user enter valid data and update profile in database.
     * In error case return into the edit profile page and draw the error.
     * <p/>
     *
     * @param editedProfileDto  dto populated by user
     * @param result   binding result which contains the validation result
     * @param response http servlet response
     * @return in case of errors return back to edit profile page, in another case return to user details page
     * @throws NotFoundException if edited user doesn't exist in system
     */
    @RequestMapping(value = "/users/edit/**", method = RequestMethod.POST)
    public ModelAndView saveEditedProfile(@Valid @ModelAttribute(EDITED_USER) EditUserProfileDto editedProfileDto,
                                    BindingResult result, HttpServletResponse response) throws NotFoundException {
        if (result.hasErrors()) {
            JCUser editedUser = userService.get(editedProfileDto.getUserId());
            ModelAndView mav = new ModelAndView(EDIT_PROFILE, EDITED_USER, editedProfileDto);
            mav.addObject("contacts", editedUser.getUserContacts());
            return mav;
        }
        long editedUserId = editedProfileDto.getUserId();
        checkPermissionsToEditProfile(editedUserId);
        JCUser user = userService.saveEditedUserProfile(editedUserId, editedProfileDto.getUserInfoContainer());
        // apply language changes immediately
        String code = editedProfileDto.getLanguage().getLanguageCode();
        Cookie cookie = new Cookie(CookieLocaleResolver.DEFAULT_COOKIE_NAME, code);
        cookie.setPath("/");
        response.addCookie(cookie);
        //redirect to the view profile page
        return new ModelAndView("redirect:/users/" + user.getId());
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
     * @param page            number current page
     * @param id database user identifier
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

    @RequestMapping(value = "**/lang={id}", method = RequestMethod.GET)
    public String saveUserLanguage(@PathVariable("id") String id, HttpServletResponse response, HttpServletRequest request) throws ServletException {
        JCUser jcuser = userService.getCurrentUser();
        if(!jcuser.isAnonymous()){
            Language languageFromRequest = Language.byLocale(new Locale(id));
            try{
                jcuser.setLanguage(languageFromRequest);
                userService.saveEditedUserProfile(jcuser.getId(), new EditUserProfileDto(jcuser).getUserInfoContainer());
            }catch (Exception e){
                throw new ServletException("Language save failed.");
            }
        }
        LocaleResolver localeResolver = RequestContextUtils.getLocaleResolver(request);
        localeResolver.setLocale(request, response, jcuser.getLanguage().getLocale());

        return "redirect:" + request.getHeader("Referer");
//        return new JsonResponse(JsonResponseStatus.SUCCESS, null);
    }

}
