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
import org.jtalks.jcommune.model.entity.Post;
import org.jtalks.jcommune.service.PostService;
import org.jtalks.jcommune.service.UserService;
import org.jtalks.jcommune.service.exceptions.NotFoundException;
import org.jtalks.jcommune.service.nontransactional.ImageUtils;
import org.jtalks.jcommune.service.nontransactional.SecurityService;
import org.jtalks.jcommune.web.dto.EditUserProfileDto;
import org.jtalks.jcommune.web.util.BreadcrumbBuilder;
import org.jtalks.jcommune.web.util.Pagination;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;

/**
 * Controller for User related actions: registration, user profile operations and so on.
 *
 * @author Kirill Afonin
 * @author Alexandre Teterin
 * @author Max Malakhov
 * @author Eugeny Batov
 * @author Evgeniy Naumenko
 */
@Controller
public class UserProfileController {
    public static final String EDIT_PROFILE = "editProfile";
    public static final String EDITED_USER = "editedUser";
    public static final String BREADCRUMB_LIST = "breadcrumbList";


    private SecurityService securityService;
    private UserService userService;
    private BreadcrumbBuilder breadcrumbBuilder;
    private ImageUtils imageUtils;
    private PostService postService;

    /**
     * This method turns the trim binder on. Trim binder
     * removes leading and trailing spaces from the submitted fields.
     * So, it ensures, that all validations will be applied to
     * trimmed field values only.
     *
     * @param binder Binder object to be injected
     */
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
    }

    /**
     * @param userService       {@link UserService} to be injected
     * @param securityService   {@link SecurityService} used for accessing to current logged in user
     * @param breadcrumbBuilder the object which provides actions on {@link BreadcrumbBuilder} entity
     * @param imageUtils        {@link ImageUtils} used
     * @param postService       {@link PostService} used
     */
    @Autowired
    public UserProfileController(UserService userService,
                                 SecurityService securityService,
                                 BreadcrumbBuilder breadcrumbBuilder,
                                 ImageUtils imageUtils,
                                 PostService postService) {
        this.userService = userService;
        this.securityService = securityService;
        this.breadcrumbBuilder = breadcrumbBuilder;
        this.imageUtils = imageUtils;
        this.postService = postService;
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
    public ModelAndView showProfilePage() throws NotFoundException {
        JCUser user = securityService.getCurrentUser();
        ModelAndView userProfileModelAndView = getModelAndViewUserProfile(user);
        return userProfileModelAndView;
    }

    /**
     * Show edit user profile page for current logged in user.
     *
     * @return edit user profile page
     * @throws NotFoundException throws if current logged in user was not found
     */
    @RequestMapping(value = "/users/edit", method = RequestMethod.GET)
    public ModelAndView editProfilePage() throws NotFoundException {
        JCUser user = securityService.getCurrentUser();
        EditUserProfileDto editedUser = new EditUserProfileDto(user);
        byte[] avatar = user.getAvatar();
        editedUser.setAvatar(imageUtils.prepareHtmlImgSrc(avatar));
        return new ModelAndView(EDIT_PROFILE, EDITED_USER, editedUser);
    }

    /**
     * Update user profile info. Check if the user enter valid data and update profile in database.
     * In error case return into the edit profile page and draw the error.
     * <p/>
     *
     * @param userDto  dto populated by user
     * @param result   binding result which contains the validation result
     * @param response http servlet response
     * @return in case of errors return back to edit profile page, in another case return to user details page
     */
    @RequestMapping(value = "/users/edit", method = RequestMethod.POST)
    public ModelAndView editProfile(@Valid @ModelAttribute(EDITED_USER) EditUserProfileDto userDto,
                                    BindingResult result, HttpServletResponse response) {
        if (result.hasErrors()) {
            return new ModelAndView(EDIT_PROFILE, EDITED_USER, userDto);
        }
        JCUser user = userService.editUserProfile(userDto.getUserInfoContainer());
        // apply language changes immediately
        String code = userDto.getLanguage().getLanguageCode();
        Cookie cookie = new Cookie(CookieLocaleResolver.DEFAULT_COOKIE_NAME, code);
        cookie.setPath("/");
        response.addCookie(cookie);
        //redirect to the view profile page
        return new ModelAndView("redirect:/users/" + user.getEncodedUsername());
    }

    /**
     * Show page with post of user.
     * SpEL pattern in a var name indicates we want to consume all the symbols in a var,
     * even dots, which Spring MVC uses as file extension delimiters by default.
     *
     * @param page            number current page
     * @param pagingEnabled   flag on/OffScreenImage paging
     * @param encodedUsername encodedUsername
     * @return post list of user
     * @throws NotFoundException if user with given id not found.
     */
    @RequestMapping(value = "/users/{encodedUsername:.+}/postList", method = RequestMethod.GET)
    public ModelAndView showUserPostList(@PathVariable("encodedUsername") String encodedUsername,
                                         @RequestParam(value = "page", defaultValue = "1",
                                                 required = false) Integer page,
                                         @RequestParam(value = "pagingEnabled", defaultValue = "true", required = false
                                         ) Boolean pagingEnabled
    ) throws NotFoundException {
        JCUser user = userService.getByUsername(encodedUsername);
        List<Post> posts = postService.getPostsOfUser(user);
        Pagination pag = new Pagination(page, user, posts.size(), pagingEnabled);
        return new ModelAndView("userPostList")
                .addObject("user", user)
                .addObject("pag", pag)
                .addObject("posts", posts)
                .addObject(BREADCRUMB_LIST, breadcrumbBuilder.getForumBreadcrumb());
    }

     /**
     * Show page with user info.
     * SpEL pattern in a var name indicates we want to consume all the symbols in a var,
     * even dots, which Spring MVC uses as file extension delimiters by default.
     *
     * @param username the decoded encodedUsername from the JSP view.
     * @return user details view with {@link JCUser} object.
     * @throws NotFoundException if user with given id not found.
     */
     
    private ModelAndView getModelAndViewUserProfile(JCUser user) throws NotFoundException {
        return new ModelAndView("userDetails")
                .addObject("user", user)
                        // bind separately to get localized value
                .addObject("language", user.getLanguage())
                .addObject("pageSize", Pagination.getPageSizeFor(user));
    }
}
