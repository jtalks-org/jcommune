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
import org.jtalks.jcommune.web.dto.EditUserProfileDto;
import org.jtalks.jcommune.web.dto.RegisterUserDto;
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
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.io.UnsupportedEncodingException;

/**
 * Controller for User related actions: registration.
 *
 * @author Kirill Afonin
 * @author Alexandre Teterin
 */
@Controller
public class UserController {
    private final SecurityService securityService;
    private final UserService userService;

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
     * @param userService     {@link UserService} to be injected
     * @param securityService {@link SecurityService} used for accessing to current logged in user
     * @see UserService
     * @see SecurityService
     */
    @Autowired
    public UserController(UserService userService, SecurityService securityService) {
        this.userService = userService;
        this.securityService = securityService;
    }

    /**
     * Render registration page with binded object to form.
     *
     * @return {@code ModelAndView} with "registration" view and empty
     *         {@link RegisterUserDto} with name "newUser
     */
    @RequestMapping(value = "/registration", method = RequestMethod.GET)
    public ModelAndView registrationPage() {
        return new ModelAndView("registration").addObject("newUser", new RegisterUserDto());
    }

    /**
     * Register {@link User} from populated in form {@link RegisterUserDto}.
     *
     * @param userDto {@link RegisterUserDto} populated in form
     * @param result  result of {@link RegisterUserDto} validation
     * @return redirect to / if registration successfull or back to "/registration" if failed
     */
    @RequestMapping(value = "/user", method = {RequestMethod.POST, RequestMethod.GET})
    public ModelAndView registerUser(@Valid @ModelAttribute("newUser") RegisterUserDto userDto, BindingResult result) {

        if (result.hasErrors()) {
            return new ModelAndView("registration");
        }

        try {
            userService.registerUser(userDto.createUser());
            return new ModelAndView("redirect:/");
        } catch (DuplicateUserException e) {
            result.rejectValue("username", "validation.duplicateuser");
        } catch (DuplicateEmailException e) {
            result.rejectValue("email", "validation.duplicateemail");
        } catch (UnsupportedEncodingException e) {
            result.rejectValue("username", "validation.invalidusernamechar");
        }
        return new ModelAndView("registration");
    }

    /**
     * Show page with user info.
     *
     * @param userId id
     * @return user details view with {@link User} object
     * @throws NotFoundException if user with given id not found
     */
    @RequestMapping(value = "/user/{encodedUsername}", method = RequestMethod.GET)
    public ModelAndView show(@PathVariable("encodedUsername") String encodedUsername) throws NotFoundException {
        User user = userService.getByEncodedUsername(encodedUsername);
        return new ModelAndView("userDetails", "user", user);
    }

    /**
     * Show edit user profile page for current logged in user.
     *
     * @return edit user profile page
     * @throws NotFoundException - throws if current logged in user was not found
     */
    @RequestMapping(value = "/user/edit", method = RequestMethod.GET)
    public ModelAndView editProfilePage() throws NotFoundException {
        String currentUser = securityService.getCurrentUserUsername();
        User user = userService.getByUsername(currentUser);
        EditUserProfileDto editedUser = new EditUserProfileDto(user);
        return new ModelAndView("editProfile", "editedUser", editedUser);
    }

    /**
     * Update user profile info. Check if the user enter valid data and update profile in database.
     * In error case return into the edit profile page and draw the error.
     *
     * @param userDto - dto populated by user
     * @param result - binding result which contains the validation result
     * @return - in case of errors return back to edit profile page, in another case return to user detalis page
     * @throws NotFoundException - throws if current logged in user was not found
     */
    @RequestMapping(value = "/user/edit", method = RequestMethod.POST)
    public ModelAndView editProfile(@Valid @ModelAttribute("editedUser") EditUserProfileDto userDto,
                                    BindingResult result) throws NotFoundException, UnsupportedEncodingException {
        User currentUser = securityService.getCurrentUser();

        if (result.hasErrors()) {
            return new ModelAndView("editProfile");
        }

        User userData2Edit = userDto.createUser();

        try {
            userService.editUserProfile(userData2Edit, currentUser.getUsername(), userDto.getCurrentUserPassword(), userDto.getNewUserPassword());
        } catch (DuplicateEmailException e) {
            result.rejectValue("email", "validation.duplicateemail");
          return new ModelAndView("editProfile");
        } catch (WrongPasswordException e) {
            result.rejectValue("currentUserPassword", "label.incorrectCurrentPassword",
                  "Password does not match to the current password");
          return new ModelAndView("editProfile");
        }                                    
        return new ModelAndView(new StringBuilder()
                .append("redirect:/user/")
                .append(currentUser.getEncodedUsername())
                .append(".html").toString());
    }
}
