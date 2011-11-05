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
import org.jtalks.jcommune.service.SecurityService;
import org.jtalks.jcommune.service.UserService;
import org.jtalks.jcommune.service.exceptions.DuplicateEmailException;
import org.jtalks.jcommune.service.exceptions.DuplicateUserException;
import org.jtalks.jcommune.service.exceptions.InvalidImageException;
import org.jtalks.jcommune.service.exceptions.MailingFailedException;
import org.jtalks.jcommune.service.exceptions.NotFoundException;
import org.jtalks.jcommune.service.exceptions.WrongPasswordException;
import org.jtalks.jcommune.web.dto.BreadcrumbBuilder;
import org.jtalks.jcommune.web.dto.EditUserProfileDto;
import org.jtalks.jcommune.web.dto.RegisterUserDto;
import org.jtalks.jcommune.web.util.ImagePreprocessor;
import org.jtalks.jcommune.web.util.Language;
import org.jtalks.jcommune.web.util.PageSize;
import org.jtalks.jcommune.web.validation.ImageFormats;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;

/**
 * Controller for User related actions: registration.
 *
 * @author Kirill Afonin
 * @author Alexandre Teterin
 * @author Max Malakhov
 * @author Eugeny Batov
 * @author Evgeniy Naumenko
 */
@Controller
public class UserController {
    public static final String EDIT_PROFILE = "editProfile";
    public static final String REGISTRATION = "registration";
    public static final String EDITED_USER = "editedUser";

    public static final int AVATAR_MAX_HEIGHT = 100;
    public static final int AVATAR_MAX_WIDTH = 100;


    private final SecurityService securityService;
    private final UserService userService;
    private BreadcrumbBuilder breadcrumbBuilder;
    private ImagePreprocessor imagePreprocessor;

    /**
     * This method turns the trim binder on. Trim bilder
     * removes leading and trailing spaces from the submitted fields.
     *
     * @param binder Binder object to be injected
     */
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
    }

    /**
     * Assign {@link UserService} to field.
     *
     * @param userService       {@link org.jtalks.jcommune.service.UserService} to be injected
     * @param securityService   {@link org.jtalks.jcommune.service.SecurityService} used for
     *                          accessing to current logged in user
     * @param breadcrumbBuilder the object which provides actions on
     *                          {@link org.jtalks.jcommune.web.dto.BreadcrumbBuilder} entity
     * @param imagePreprocessor {@link org.jtalks.jcommune.web.util.ImagePreprocessor} used
     */
    @Autowired
    public UserController(UserService userService,
                          SecurityService securityService,
                          BreadcrumbBuilder breadcrumbBuilder,
                          ImagePreprocessor imagePreprocessor) {
        this.userService = userService;
        this.securityService = securityService;
        this.breadcrumbBuilder = breadcrumbBuilder;
        this.imagePreprocessor = imagePreprocessor;
    }

    /**
     * Render registration page with binded object to form.
     *
     * @return {@code ModelAndView} with "registration" view and empty
     *         {@link RegisterUserDto} with name "newUser
     */
    @RequestMapping(value = "/users/new", method = RequestMethod.GET)
    public ModelAndView registrationPage() {
        return new ModelAndView(REGISTRATION).addObject("newUser", new RegisterUserDto());
    }

    /**
     * Register {@link User} from populated in form {@link RegisterUserDto}.
     *
     * @param userDto {@link RegisterUserDto} populated in form
     * @param result  result of {@link RegisterUserDto} validation
     * @return redirect to / if registration successful or back to "/registration" if failed
     */
    @RequestMapping(value = "/users", method = RequestMethod.POST)
    public ModelAndView registerUser(@Valid @ModelAttribute("newUser") RegisterUserDto userDto, BindingResult result) {
        if (result.hasErrors()) {
            return new ModelAndView(REGISTRATION);
        }

        try {
            userService.registerUser(userDto.createUser());
            return new ModelAndView("redirect:/");
        } catch (DuplicateUserException e) {
            result.rejectValue("username", "validation.duplicateuser");
        } catch (DuplicateEmailException e) {
            result.rejectValue("email", "validation.duplicateemail");
        }
        return new ModelAndView(REGISTRATION);
    }

    /**
     * Show page with user info.
     *
     * @param username the decoded encodedUsername from the JSP view.
     * @return user details view with {@link User} object.
     * @throws NotFoundException if user with given id not found.
     */
    @RequestMapping(value = "/users/{encodedUsername}", method = RequestMethod.GET)
    //The {encodedUsername} from the JSP view automatically converted to username.
    // That's why the getByUsername() method is used instead of getByEncodedUsername().
    public ModelAndView show(@PathVariable("encodedUsername") String username) throws NotFoundException {
        User user = userService.getByUsername(username);
        return new ModelAndView("userDetails")
                .addObject("user", user)
                .addObject("breadcrumbList", breadcrumbBuilder.getForumBreadcrumb())
                        // bind separately to get localized value
                .addObject("language", Language.valueOf(user.getLanguage()))
                .addObject("pageSize",PageSize.valueOf(user.getPageSize()));                
    }

    /**
     * Show edit user profile page for current logged in user.
     *
     * @return edit user profile page
     * @throws NotFoundException throws if current logged in user was not found
     */
    @RequestMapping(value = "/users/edit", method = RequestMethod.GET)
    public ModelAndView editProfilePage() throws NotFoundException {
        User user = securityService.getCurrentUser();
        EditUserProfileDto editedUser = new EditUserProfileDto(user);
        editedUser.setAvatar(new MockMultipartFile("avatar", "", ImageFormats.JPG.getContentType(), user.getAvatar()));
        return editMaV(editedUser)
                .addObject("breadcrumbList", breadcrumbBuilder.getForumBreadcrumb())
                .addObject("languages", Language.values())
                .addObject("pageSizes", PageSize.values());
    }

    /**
     * Update user profile info. Check if the user enter valid data and update profile in database.
     * In error case return into the edit profile page and draw the error.
     * <p/>
     *
     * @param userDto  dto populated by user
     * @param result   binding result which contains the validation result
     * @param response http servlet response
     * @return in case of errors return back to edit profile page, in another case return to user detalis page
     * @throws NotFoundException throws if current logged in user was not found
     * @throws IOException       throws in case of access errors (if the temporary store fails)
     */
    @RequestMapping(value = "/users/edit", method = RequestMethod.POST)
    public ModelAndView editProfile(
            @Valid @ModelAttribute(EDITED_USER) EditUserProfileDto userDto,
            BindingResult result, HttpServletResponse response)
        throws NotFoundException, IOException {

        // apply language changes immediately
        applyLanguage(Language.valueOf(userDto.getLanguage()), response);
        // validate other fields
        if (result.hasErrors()) {
            return applyAvatarRemoval(userDto);
        }
        User editedUser = editUserProfile(userDto, result);
        // error occured
        if (editedUser == null) {
            return applyAvatarRemoval(userDto);
        }
        return new ModelAndView(new StringBuilder().append("redirect:/users/")
                .append(editedUser.getEncodedUsername()).toString());
    }

    /**
     * Convenience method to handle possible errors
     *
     * @param userDto form submission result
     * @param result form validation result, will be filled up with additional errors, if any
     * @return Edited domain object if no error occure, null otherwise
     * @throws IOException image stream processing error
     */
    private User editUserProfile(EditUserProfileDto userDto, BindingResult result) throws IOException {
        try {
            return performEditUserProfile(userDto);
        } catch (DuplicateEmailException e) {
            result.rejectValue("email", "validation.duplicateemail");
        } catch (WrongPasswordException e) {
            result.rejectValue("currentUserPassword", "label.incorrectCurrentPassword",
                    "Password does not match to the current password");
        } catch (InvalidImageException e) {
            result.rejectValue("avatar", "avatar.wrong.format");
        }
        return null;
    }

    /**
     * This method applies language to the response as cookie for CookieLocaleResolver
     *
     * @param language language to be applied
     * @param response response to be filled with new cookie
     */
    private void applyLanguage(Language language, HttpServletResponse response) {
        String code =  language.getLanguageCode();
        Cookie cookie = new Cookie(CookieLocaleResolver.DEFAULT_COOKIE_NAME, code);
        response.addCookie(cookie);
    }

    /**
     * todo: refactor it to pass dto to the service layer
     * Convenience method to pass a dto content toa  service layer
     *
     * @param userDto form submission result
     * @return updated user object
     * @throws DuplicateEmailException e-maim already registered
     * @throws WrongPasswordException current password doesn't match with the passed one
     * @throws IOException avatar upload problems
     * @throws InvalidImageException avatar image is invalid
     */
    private User performEditUserProfile(EditUserProfileDto userDto) throws DuplicateEmailException,
            WrongPasswordException, IOException, InvalidImageException {
        return userService.editUserProfile(userDto.getEmail(), userDto.getFirstName(),
                userDto.getLastName(), userDto.getCurrentUserPassword(), userDto.getNewUserPassword(),
                imagePreprocessor.preprocessImage(userDto.getAvatar(), AVATAR_MAX_WIDTH, AVATAR_MAX_HEIGHT),
                userDto.getSignature(), userDto.getLanguage(), userDto.getPageSize());
    }

    /**
     *  todo: looks realy odd, we need to somehow refactor all the chain
     *
     * Substitues fake avatar value if there was no avatar passed
     *
     * @param userDto for submission result
     * @return  updated model and view containing avatar in any case
     */
    private ModelAndView applyAvatarRemoval(EditUserProfileDto userDto) {
        User user = securityService.getCurrentUser();
        if (user.getAvatar() == null) {
            userDto.setAvatar(new MockMultipartFile("avatar", "", ImageFormats.JPG.getContentType(), new byte[0]));
        }
        return editMaV(userDto);
    }

    /**
     * {@code ModelAndView} with dto and languages.
     *
     * @param dto edit user dto
     * @return {@code ModelAndView} for edit profile page
     */
    private ModelAndView editMaV(EditUserProfileDto dto) {
        return new ModelAndView(EDIT_PROFILE, EDITED_USER, dto).addObject("languages", Language.values());
    }

    /**
     * Remove avatar from user profile.
     *
     * @return edit user profile page
     */
    @RequestMapping(value = "/users/edit/avatar", method = RequestMethod.POST)
    public ModelAndView removeAvatarFromCurrentUser() {
        User user = securityService.getCurrentUser();
        userService.removeAvatarFromCurrentUser();
        EditUserProfileDto editedUser = new EditUserProfileDto(user);
        return editMaV(editedUser);
    }

    /**
     * Write user avatar  in response for rendering it on html pages.
     *
     * @param response        servlet response
     * @param encodedUsername {@link User#getEncodedUsername()}
     * @throws NotFoundException - throws if user with given encodedUsername not found
     * @throws IOException       - throws if an output exception occurred
     */
    @RequestMapping(value = "/{encodedUsername}/avatar", method = RequestMethod.GET)
    public void renderAvatar(HttpServletResponse response,
                             @PathVariable("encodedUsername") String encodedUsername) throws NotFoundException,
            IOException {
        User user = userService.getByEncodedUsername(encodedUsername);
        byte[] avatar = user.getAvatar();
        response.setContentType("image/jpeg");
        response.setContentLength(avatar.length);
        response.getOutputStream().write(avatar);
    }

    /**
     * Renders a page to restore user's password.
     * Registration e-mail is required.
     *
     * @return view page name
     */
    @RequestMapping(value = "/password/restore", method = RequestMethod.GET)
    public ModelAndView showRestorePasswordPage() {
        return new ModelAndView("restorePassword");
    }

    /**
     * Tries to restore a password by email.
     * If e-mail given has not been registered
     * before view with an error will be returned.
     *
     * @param email address ro identify the user
     * @return view with a parameters bound
     */
    @RequestMapping(value = "/password/restore", method = RequestMethod.POST)
    public ModelAndView restorePassword(String email) {
        ModelAndView mav = new ModelAndView("restorePassword");
        mav.addObject("breadcrumbList", breadcrumbBuilder.getForumBreadcrumb());
        try {
            userService.restorePassword(email);
            mav.addObject("message", "label.restorePassword.completed");
        } catch (NotFoundException e) {
            mav.addObject("error", "email.unknown");
        } catch (MailingFailedException e) {
            mav.addObject("error", "email.failed");
        }
        return mav;
    }
}
