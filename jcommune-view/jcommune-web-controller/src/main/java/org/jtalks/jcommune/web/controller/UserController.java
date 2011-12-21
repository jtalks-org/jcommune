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

import org.jtalks.jcommune.service.UserService;
import org.jtalks.jcommune.service.exceptions.DuplicateEmailException;
import org.jtalks.jcommune.service.exceptions.DuplicateUserException;
import org.jtalks.jcommune.service.exceptions.MailingFailedException;
import org.jtalks.jcommune.service.exceptions.NotFoundException;
import org.jtalks.jcommune.web.dto.RegisterUserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;

/**
 *  This controller handles custom authentication actions
 *  like user registration or password restore.
 *
 *  Basic actions like username/password verification are
 *  to be performed by Spring Security
 *
 *  @author Evgeniy Naumenko
 */
public class UserController {

    public static final String REGISTRATION = "registration";

    private UserService userService;

    /**
     *
     * @param userService to delegate business logic invocation
     */
    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
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
     * @param email address to identify the user
     * @return view with a parameters bound
     */
    @RequestMapping(value = "/password/restore", method = RequestMethod.POST)
    public ModelAndView restorePassword(String email) {
        ModelAndView mav = new ModelAndView("restorePassword");
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

    /**
     * Render registration page with bind objects to form.
     *
     * @return {@code ModelAndView} with "registration" view and empty
     *         {@link org.jtalks.jcommune.web.dto.RegisterUserDto} with name "newUser
     */
    @RequestMapping(value = "/users/new", method = RequestMethod.GET)
    public ModelAndView registrationPage() {
        return new ModelAndView(REGISTRATION)
                .addObject("newUser", new RegisterUserDto());
    }

    /**
     * Register {@link org.jtalks.jcommune.model.entity.User} from populated in form {@link RegisterUserDto}.
     *
     * todo: redirect to the latest url we came from instead of root
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
}
