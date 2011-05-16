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
import org.jtalks.jcommune.web.dto.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

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
     *         {@link UserDTO} with name "newUser.
     */
    @RequestMapping(value = "/registration", method = RequestMethod.GET)
    public ModelAndView registrationPage() {
        return new ModelAndView("registration").addObject("newUser", new UserDTO());
    }

    /**
     * Register {@link User} from populated in form {@link UserDTO}.
     *
     * @param userDto {@link UserDTO} populated in form.
     * @return redirect to /
     * @throws DuplicateException !!will be removed from throws!!
     */
    @RequestMapping(value = "/user", method = RequestMethod.POST)
    public String registerUser(@ModelAttribute("newUser") UserDTO userDto)
            throws DuplicateException {

        userService.registerUser(userDto.getUsername(), userDto.getEmail(),
                userDto.getFirstName(), userDto.getLastName(), userDto.getPassword());

        return "redirect:/";
    }
}
