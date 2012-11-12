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

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.model.entity.Language;
import org.jtalks.jcommune.service.UserService;
import org.jtalks.jcommune.service.exceptions.MailingFailedException;
import org.jtalks.jcommune.service.exceptions.NotFoundException;
import org.jtalks.jcommune.web.dto.JsonResponse;
import org.jtalks.jcommune.web.dto.RegisterUserDto;
import org.jtalks.jcommune.web.dto.RestorePasswordDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

/**
 * This controller handles custom authentication actions
 * like user registration or password restore.
 * <p/>
 * Basic actions like username/password verification are
 * to be performed by Spring Security
 *
 * @author Evgeniy Naumenko
 */
@Controller
public class UserController {

    public static final String REGISTRATION = "registration";
    
    public static final String LOGIN = "login";
    
    private static final String REMEMBER_ME_ON = "on";
    
    private static final String JSON_RESPONSE_SUCCESS = "success";
    
    private static final String JSON_RESPONSE_FAIL = "fail";

    private UserService userService;
    
    /**
     * @param userService to delegate business logic invocation
     * @param sessionStratedy used to call after authentication to store users 
     *      online list
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
        return new ModelAndView("restorePassword")
                .addObject("dto", new RestorePasswordDto());
    }

    /**
     * Tries to restore a password by email.
     * If e-mail given has not been registered
     * before view with an error will be returned.
     *
     * @param dto    with email address to identify the user
     * @param result email validation result
     * @return view with a parameters bound
     */
    @RequestMapping(value = "/password/restore", method = RequestMethod.POST)
    public ModelAndView restorePassword(@Valid @ModelAttribute("dto") RestorePasswordDto dto, BindingResult result) {
        ModelAndView mav = new ModelAndView("restorePassword");
        if (result.hasErrors()) {
            return mav;
        }
        try {
            userService.restorePassword(dto.getUserEmail());
            mav.addObject("message", "label.restorePassword.completed");
        } catch (MailingFailedException e) {
            result.addError(new FieldError("dto", "email", "email.failed"));
        }
        return mav;
    }

    /**
     * Render registration page with bind objects to form.
     *
     * @return {@code ModelAndView} with "registration" view and empty
     *         {@link org.jtalks.jcommune.web.dto.RegisterUserDto} with name "newUser
     */
    @RequestMapping(value = "/user/new", method = RequestMethod.GET)
    public ModelAndView registrationPage() {
        return new ModelAndView(REGISTRATION)
                .addObject("newUser", new RegisterUserDto());
    }

    /**
     * Register {@link org.jtalks.jcommune.model.entity.JCUser} from populated in form {@link RegisterUserDto}.
     * <p/>
     * todo: redirect to the latest url we came from instead of root
     *
     * @param userDto {@link RegisterUserDto} populated in form
     * @param result  result of {@link RegisterUserDto} validation
     * @param locale  to set currently selected language as user's default
     * @return redirect to / if registration successful or back to "/registration" if failed
     */
    @RequestMapping(value = "/user/new", method = RequestMethod.POST)
    public ModelAndView registerUser(@Valid @ModelAttribute("newUser") RegisterUserDto userDto,
                                     BindingResult result, Locale locale) {
        if (result.hasErrors()) {
            return new ModelAndView(REGISTRATION);
        }
        JCUser user = userDto.createUser();
        user.setLanguage(Language.byLocale(locale));
        userService.registerUser(user);
        return new ModelAndView("redirect:/");
    }
    
    /**
     * Register {@link org.jtalks.jcommune.model.entity.JCUser} from populated {@link RegisterUserDto}.
     * <p/>
     *
     * @param userDto {@link RegisterUserDto} populated in form
     * @param result  result of {@link RegisterUserDto} validation
     * @param locale  to set currently selected language as user's default
     * @return redirect validation result in JSON format
     */
    @RequestMapping(value = "/user/new_ajax", method = RequestMethod.POST)
    @ResponseBody 
    public JsonResponse registerUserAjax(@Valid @ModelAttribute("newUser") RegisterUserDto userDto,
                                     BindingResult result, Locale locale) {
    	 if (result.hasErrors()) {
             return new JsonResponse("fail", result.getAllErrors());
         }
         JCUser user = userDto.createUser();
         user.setLanguage(Language.byLocale(locale));
         userService.registerUser(user);
         return new JsonResponse("success");
    }



	/**
     * Activates user account with UUID-based URL
     * We use UUID's to be sure activation link cannot be generated from username
     * by script or any other tool.
     *
     * @param uuid unique entity identifier
     * @return redirect to the login page
     */
    @RequestMapping(value = "user/activate/{uuid}")
    public String activateAccount(@PathVariable String uuid) {
        try {
            userService.activateAccount(uuid);
            return "redirect:/login";
        } catch (NotFoundException e) {
            return "errors/activationExpired";
        }
    }
    
    /**
     * Shows login page. Also checks if user is already logged in. 
     * If so he is redirected to main page.
     * 
     * @return login view name or redirect to main page
     */
    @RequestMapping(value="/login", method=RequestMethod.GET)
    public String loginPage()
    {
        JCUser currentUser = userService.getCurrentUser();
        if (currentUser.isAnonymous()) {
            return LOGIN;
        } else {
            return "redirect:/";
        }
    }
    
    /**
     * Handles login action for ajax clients.
     * 
     * @param username username
     * @param password password
     * @param rememberMe set remember me token if equal to "on"
     * @param request servlet request
     * @param response servlet response 
     * @return "success" or "fail" response status
     */
    @RequestMapping(value="/login_ajax", method=RequestMethod.POST)
    @ResponseBody 
    public JsonResponse loginAjax(@RequestParam("j_username") String username,
            					  				@RequestParam("j_password") String password,
            					  				@RequestParam(value="_spring_security_remember_me", defaultValue="off") String rememberMe,
            					  				HttpServletRequest request, HttpServletResponse response) {
	    boolean rememberMeBoolean = rememberMe.equals(REMEMBER_ME_ON);
	    boolean isAuthenticated = userService.loginUser(username, password, 
	            rememberMeBoolean, request, response);
		return new JsonResponse(isAuthenticated ? JSON_RESPONSE_SUCCESS : JSON_RESPONSE_FAIL);
    }
}
