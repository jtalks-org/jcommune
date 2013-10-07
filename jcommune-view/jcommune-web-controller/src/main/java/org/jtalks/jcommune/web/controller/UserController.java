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

import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang.StringUtils;
import org.jtalks.jcommune.model.dto.RegisterUserDto;
import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.model.entity.Language;
import org.jtalks.jcommune.model.plugins.exceptions.NoConnectionException;
import org.jtalks.jcommune.model.plugins.exceptions.UnexpectedErrorException;
import org.jtalks.jcommune.service.Authenticator;
import org.jtalks.jcommune.service.UserService;
import org.jtalks.jcommune.service.exceptions.MailingFailedException;
import org.jtalks.jcommune.service.exceptions.NotFoundException;
import org.jtalks.jcommune.web.dto.RestorePasswordDto;
import org.jtalks.jcommune.web.dto.json.JsonResponse;
import org.jtalks.jcommune.web.dto.json.JsonResponseStatus;
import org.jtalks.jcommune.web.validation.editors.DefaultStringEditor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.Validator;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Locale;

/**
 * This controller handles custom authentication actions
 * like user registration or password restore.
 * <p/>
 * Basic actions like username/password verification are
 * to be performed by Spring Security
 *
 * @author Evgeniy Naumenko
 * @author Andrey Pogorelov
 */
@Controller
public class UserController {

    public static final String REGISTRATION = "registration";
    public static final String LOGIN = "login";
    public static final String AFTER_REGISTRATION = "afterRegistration";
    public static final String AUTH_FAIL_URL = "redirect:/login?login_error=1";
    public static final String AUTH_SERVICE_FAIL_URL = "redirect:/login?login_error=3";
    public static final String REG_SERVICE_CONNECTION_ERROR_URL = "redirect:/user/new?reg_error=1";
    public static final String REG_SERVICE_UNEXPECTED_ERROR_URL = "redirect:/user/new?reg_error=2";

    private static final String REMEMBER_ME_ON = "on";
    protected static final String ATTR_USERNAME = "username";
    protected static final String ATTR_LOGIN_ERROR = "username";

    private UserService userService;
    private Authenticator authenticator;

    /**
     * @param userService to delegate business logic invocation
     */
    @Autowired
    public UserController(UserService userService, Authenticator authenticator) {
        this.userService = userService;
        this.authenticator = authenticator;
    }

    /**
     * This method turns the trim binder on. Trim binder
     * removes leading and trailing spaces from the submitted fields.
     * So, it ensures, that all validations will be applied to
     * trimmed field values only.
     * <p/> There is no need for trim edit password fields,
     * so they are processed with {@link DefaultStringEditor}
     * @param binder Binder object to be injected
     */
    @InitBinder({"dto", "newUser"})
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
        binder.registerCustomEditor(String.class, "password", new DefaultStringEditor(true));
        binder.registerCustomEditor(String.class, "passwordConfirm", new DefaultStringEditor(true));
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
     *         {@link org.jtalks.jcommune.model.dto.RegisterUserDto} with name "newUser
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
     * @param registerUserDto {@link RegisterUserDto} populated in form
     * @param locale  to set currently selected language as user's default
     * @return redirect to / if registration successful or back to "/registration" if failed
     */
    @RequestMapping(value = "/user/new", method = RequestMethod.POST)
    public ModelAndView registerUser(@ModelAttribute("newUser") RegisterUserDto registerUserDto,
                                     Locale locale) {
        BindingResult errors;
        try {
            registerUserDto.getUserDto().setLanguage(Language.byLocale(locale));
            errors = authenticator.register(registerUserDto);
        } catch (NoConnectionException e) {
            return new ModelAndView(REG_SERVICE_CONNECTION_ERROR_URL);
        } catch (UnexpectedErrorException e) {
            return new ModelAndView(REG_SERVICE_UNEXPECTED_ERROR_URL);
        }
        if (errors.hasErrors()) {
            ModelAndView mav = new ModelAndView(REGISTRATION);
            mav.addAllObjects(errors.getModel());
            return mav;
        }

        return new ModelAndView(AFTER_REGISTRATION);
    }


    /**
     * Register {@link org.jtalks.jcommune.model.entity.JCUser} from populated {@link RegisterUserDto}.
     * <p/>
     *
     * @param registerUserDto {@link RegisterUserDto} populated in form
     * @param locale  to set currently selected language as user's default
     * @return redirect validation result in JSON format
     */
    @RequestMapping(value = "/user/new_ajax", method = RequestMethod.POST)
    @ResponseBody
    public JsonResponse registerUserAjax(@ModelAttribute("newUser") RegisterUserDto registerUserDto,
                                         Locale locale) {
        BindingResult errors;
        try {
            registerUserDto.getUserDto().setLanguage(Language.byLocale(locale));
            errors = authenticator.register(registerUserDto);
        } catch (NoConnectionException e) {
            return new JsonResponse(JsonResponseStatus.FAIL,
                    new ImmutableMap.Builder<String, String>().put("customError", "connectionError").build());
        } catch (UnexpectedErrorException e) {
            return new JsonResponse(JsonResponseStatus.FAIL,
                    new ImmutableMap.Builder<String, String>().put("customError", "unexpectedError").build());
        }
        if (errors.hasErrors()) {
            return new JsonResponse(JsonResponseStatus.FAIL, errors.getAllErrors());
        }

        return new JsonResponse(JsonResponseStatus.SUCCESS);
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
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String loginPage() {
        JCUser currentUser = userService.getCurrentUser();
        if (currentUser.isAnonymous()) {
            return LOGIN;
        } else {
            return "redirect:/";
        }
    }

    @RequestMapping(value = "/login_ajax", method = RequestMethod.POST)
    @ResponseBody
    public JsonResponse loginAjax(@RequestParam("j_username") String username,
                                  @RequestParam("j_password") String password,
                                  @RequestParam(value = "_spring_security_remember_me", defaultValue = "off") String rememberMe,
                                  HttpServletRequest request, HttpServletResponse response) {
        boolean rememberMeBoolean = rememberMe.equals(REMEMBER_ME_ON);
        boolean isAuthenticated;
        try {
            isAuthenticated = userService.loginUser(username, password, rememberMeBoolean, request, response);
        } catch (NoConnectionException e) {
            return new JsonResponse(JsonResponseStatus.FAIL,
                    new ImmutableMap.Builder<String, String>().put("customError", "connectionError").build());
        } catch (UnexpectedErrorException e) {
            return new JsonResponse(JsonResponseStatus.FAIL,
                    new ImmutableMap.Builder<String, String>().put("customError", "unexpectedError").build());
        }
        if (isAuthenticated) {
            return new JsonResponse(JsonResponseStatus.SUCCESS);
        } else {
            return new JsonResponse(JsonResponseStatus.FAIL);
        }
    }

    /**
     * Handles login action.
     *
     * @param username   username
     * @param password   password
     * @param rememberMe set remember me token if equal to "on"
     * @param request    servlet request
     * @param response   servlet response
     * @return "success" or "fail" response status
     */
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ModelAndView login(@RequestParam("j_username") String username,
                              @RequestParam("j_password") String password,
                              @RequestParam(value = "_spring_security_remember_me", defaultValue = "off") String rememberMe,
                              HttpServletRequest request, HttpServletResponse response) {
        boolean rememberMeBoolean = rememberMe.equals(REMEMBER_ME_ON);
        boolean isAuthenticated;
        try {
            isAuthenticated = userService.loginUser(username, password, rememberMeBoolean, request, response);
        } catch (NoConnectionException e) {
            return new ModelAndView(AUTH_SERVICE_FAIL_URL);
        } catch (UnexpectedErrorException e) {
            return new ModelAndView(AUTH_SERVICE_FAIL_URL);
        }
        if (isAuthenticated) {
            return new ModelAndView("redirect:/");
        } else {
            ModelAndView modelAndView = new ModelAndView(LOGIN);
            modelAndView.addObject(ATTR_USERNAME, username);
            modelAndView.addObject(ATTR_LOGIN_ERROR, 1);
            return modelAndView;
        }
    }

    /**
     * Get usernames by pattern
     *
     * @param pattern some part of username
     * @return list of usernames as json
     */
    @RequestMapping(value = "/usernames", method = RequestMethod.POST)
    @ResponseBody
    public JsonResponse usernameList(@RequestParam("pattern") String pattern){
        return new JsonResponse(JsonResponseStatus.SUCCESS, userService.getUsernames(pattern));
    }

}
