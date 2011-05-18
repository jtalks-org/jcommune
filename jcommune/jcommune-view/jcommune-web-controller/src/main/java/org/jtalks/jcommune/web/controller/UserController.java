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

import org.jtalks.jcommune.service.UserService;
import org.jtalks.jcommune.service.exceptions.DuplicateException;
import org.jtalks.jcommune.web.dto.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;

/**
 * Controller for User related actions.
 *
 * @author Kirill Afonin
 */
@Controller
public class UserController {

    private UserService userService;

    /**
     * @param userService {@link UserService} to be injected.
     * @see UserService
     */
    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Render registration page with binded object to form.
     *
     * @return <code>ModelAndView</code> with "registration" view and empty
     *         {@link UserDto} with name "newUser.
     */
    @RequestMapping(value = "/registration", method = RequestMethod.GET)
    public ModelAndView registrationPage() {
        return new ModelAndView("registration").addObject("newUser", new org.jtalks.jcommune.web.dto.UserDto());
    }

    /**
     * Register {@link User} from populated in form {@link UserDto}.
     *
     * @param userDto {@link UserDto} populated in form.
     * @param result  result of validation.
     * @return redirect to /
     */
    @RequestMapping(value = "/user", method = RequestMethod.POST)
    public ModelAndView registerUser(@Valid @ModelAttribute("newUser") UserDto userDto,
                                     BindingResult result) {

        if (result.hasErrors()) {
            return new ModelAndView("registration");
        }

        try {
            userService.registerUser(userDto.getUsername(), userDto.getEmail(),
                    userDto.getFirstName(), userDto.getLastName(), userDto.getPassword());
        } catch (DuplicateException e) {
            result.rejectValue("username", "validation.duplicateuser",
                    "User already exist!");
            return new ModelAndView("registration");
        }

        return new ModelAndView("redirect:/");
    }
}
