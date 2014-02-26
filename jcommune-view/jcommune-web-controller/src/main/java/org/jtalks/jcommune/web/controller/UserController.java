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
import org.jtalks.jcommune.model.dto.RegisterUserDto;
import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.model.entity.Language;
import org.jtalks.jcommune.model.plugins.ExtendedPlugin;
import org.jtalks.jcommune.model.plugins.Plugin;
import org.jtalks.jcommune.model.plugins.RegistrationPlugin;
import org.jtalks.jcommune.model.plugins.exceptions.NoConnectionException;
import org.jtalks.jcommune.model.plugins.exceptions.UnexpectedErrorException;
import org.jtalks.jcommune.service.Authenticator;
import org.jtalks.jcommune.service.PluginService;
import org.jtalks.jcommune.service.UserService;
import org.jtalks.jcommune.service.exceptions.MailingFailedException;
import org.jtalks.jcommune.service.exceptions.NotFoundException;
import org.jtalks.jcommune.service.exceptions.UserTriesActivatingAccountAgainException;
import org.jtalks.jcommune.service.plugins.TypeFilter;
import org.jtalks.jcommune.web.dto.RestorePasswordDto;
import org.jtalks.jcommune.web.dto.json.JsonResponse;
import org.jtalks.jcommune.web.dto.json.JsonResponseStatus;
import org.jtalks.jcommune.web.interceptors.RefererKeepInterceptor;
import org.jtalks.jcommune.web.util.MutableHttpRequest;
import org.jtalks.jcommune.web.validation.editors.DefaultStringEditor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.orm.hibernate3.HibernateOptimisticLockingFailureException;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.rememberme.AbstractRememberMeServices;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.jtalks.jcommune.model.dto.UserDto;
import org.jtalks.jcommune.model.plugins.exceptions.HoneypotCaptchaException;

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
    public static final String REFERER_ATTR = "referer";
    public static final String AUTH_FAIL_URL = "redirect:/login?login_error=1";
    public static final String AUTH_SERVICE_FAIL_URL = "redirect:/login?login_error=3";
    public static final String REG_SERVICE_CONNECTION_ERROR_URL = "redirect:/user/new?reg_error=1";
    public static final String REG_SERVICE_UNEXPECTED_ERROR_URL = "redirect:/user/new?reg_error=2";
    public static final String REG_SERVICE_HONEYPOT_FILLED_ERROR_URL = "redirect:/user/new?reg_error=3";
    public static final String NULL_REPRESENTATION = "null";
    public static final String MAIN_PAGE_REFERER = "/";
    public static final int LOGIN_TRIES_AFTER_LOCK = 3;
    public static final int SLEEP_MILLISECONDS_AFTER_LOCK = 500;
    protected static final String ATTR_USERNAME = "username";
    protected static final String ATTR_LOGIN_ERROR = "login_error";
    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);
    private static final String REMEMBER_ME_ON = "on";
    private final UserService userService;
    private final Authenticator authenticator;
    private final PluginService pluginService;
    private final UserService plainPasswordUserService;

    /**
     * @param userService              to delegate business logic invocation
     * @param authenticator            default authenticator
     * @param pluginService            for communication with available registration or authentication plugins
     * @param plainPasswordUserService strategy for authenticating by password without hashing
     */
    @Autowired
    public UserController(UserService userService, Authenticator authenticator, PluginService pluginService,
                          UserService plainPasswordUserService) {
        this.userService = userService;
        this.authenticator = authenticator;
        this.pluginService = pluginService;
        this.plainPasswordUserService = plainPasswordUserService;
    }

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
     * @return {@code ModelAndView} with "registration" view, any additional html from registration plugins and
     *         {@link org.jtalks.jcommune.model.dto.RegisterUserDto} with name "newUser
     */
    @RequestMapping(value = "/user/new", method = RequestMethod.GET)
    public ModelAndView registrationPage(HttpServletRequest request, Locale locale) {
        Map<String, String> registrationPlugins = getRegistrationPluginsHtml(request, locale);
        return new ModelAndView(REGISTRATION)
                .addObject("newUser", new RegisterUserDto())
                .addObject("registrationPlugins", registrationPlugins);
    }

    /**
     * Register {@link org.jtalks.jcommune.model.entity.JCUser} from populated in form {@link RegisterUserDto}.
     * <p/>
     * todo: redirect to the latest url we came from instead of root
     *
     * @param registerUserDto {@link RegisterUserDto} populated in form
     * @param locale          to set currently selected language as user's default
     * @return redirect to / if registration successful or back to "/registration" if failed
     */
    @RequestMapping(value = "/user/new", method = RequestMethod.POST)
    public ModelAndView registerUser(@ModelAttribute("newUser") RegisterUserDto registerUserDto,
                                     HttpServletRequest request,
                                     Locale locale) {
        Map<String, String> registrationPlugins = getRegistrationPluginsHtml(request, locale);
        BindingResult errors;
        try {
            registerUserDto.getUserDto().setLanguage(Language.byLocale(locale));
            errors = authenticator.register(registerUserDto, request);
        } catch (NoConnectionException e) {
            return new ModelAndView(REG_SERVICE_CONNECTION_ERROR_URL);
        } catch (UnexpectedErrorException e) {
            return new ModelAndView(REG_SERVICE_UNEXPECTED_ERROR_URL);
        } catch (HoneypotCaptchaException e) {
            return new ModelAndView(REG_SERVICE_HONEYPOT_FILLED_ERROR_URL);
        }
        if (errors.hasErrors()) {
            ModelAndView mav = new ModelAndView(REGISTRATION);
            mav.addObject("registrationPlugins", registrationPlugins);
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
     * @param locale          to set currently selected language as user's default
     * @return redirect validation result in JSON format
     */
    @RequestMapping(value = "/user/new_ajax", method = RequestMethod.POST)
    @ResponseBody
    public JsonResponse registerUserAjax(@ModelAttribute("newUser") RegisterUserDto registerUserDto,
                                         HttpServletRequest request,
                                         Locale locale) {
        BindingResult errors;
        try {
            registerUserDto.getUserDto().setLanguage(Language.byLocale(locale));
            errors = authenticator.register(registerUserDto, request);
        } catch (NoConnectionException e) {
            return new JsonResponse(JsonResponseStatus.FAIL,
                    new ImmutableMap.Builder<String, String>().put("customError", "connectionError").build());
        } catch (UnexpectedErrorException e) {
            return new JsonResponse(JsonResponseStatus.FAIL,
                    new ImmutableMap.Builder<String, String>().put("customError", "unexpectedError").build());
        } catch (HoneypotCaptchaException e) {
            return new JsonResponse(JsonResponseStatus.FAIL,
                    new ImmutableMap.Builder<String, String>().put("customError", "honeypotCaptchaNotNull").build());
        }
        if (errors.hasErrors()) {
            return new JsonResponse(JsonResponseStatus.FAIL, errors.getAllErrors());
        }

        return new JsonResponse(JsonResponseStatus.SUCCESS);
    }
        
    /**
     * Get html from available registration plugins.
     *
     * @param request request
     * @param locale  user locale
     * @return map as pairs pluginId - html
     */
    private Map<String, String> getRegistrationPluginsHtml(HttpServletRequest request, Locale locale) {
        Map<String, String> registrationPlugins = new HashMap<>();
        for (Map.Entry<Long, RegistrationPlugin> entry : pluginService.getRegistrationPlugins().entrySet()) {
            String pluginId = String.valueOf(entry.getKey());
            String html = entry.getValue().getHtml(request, pluginId, locale);
            if (html != null) {
                registrationPlugins.put(pluginId, html);
            }
        }
        return registrationPlugins;
    }

    @RequestMapping(value = "/user/new_ajax", method = RequestMethod.GET)
    @ResponseBody
    public JsonResponse registrationForm(HttpServletRequest request, Locale locale) {
        Map<String, String> registrationPlugins = getRegistrationPluginsHtml(request, locale);
        return new JsonResponse(JsonResponseStatus.SUCCESS, registrationPlugins);
    }

    @RequestMapping(value = "/plugin/{pluginId}/{action}")
    public void pluginAction(@PathVariable String pluginId, @PathVariable String action,
                             HttpServletRequest request, HttpServletResponse response) {
        try {
            Plugin plugin = pluginService.getPluginById(pluginId, new TypeFilter(ExtendedPlugin.class));
            ((ExtendedPlugin) plugin).doAction(pluginId, action, request, response);
        } catch (org.jtalks.common.service.exceptions.NotFoundException ex) {
            LOGGER.error("Can't perform action {}: plugin with id {} not found", action, pluginId);
        }
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
    public String activateAccount(@PathVariable String uuid, HttpServletRequest request, HttpServletResponse response)
            throws UnexpectedErrorException, NoConnectionException {
        try {
            userService.activateAccount(uuid);
            JCUser user = userService.getByUuid(uuid);
            MutableHttpRequest wrappedRequest = new MutableHttpRequest(request);
            wrappedRequest.addParameter(AbstractRememberMeServices.DEFAULT_PARAMETER, "true");
            loginWithLockHandling(user.getUsername(), user.getPassword(), true, wrappedRequest, response,
                    plainPasswordUserService);
            return "redirect:/";
        } catch (NotFoundException e) {
            return "errors/activationExpired";
        } catch (UserTriesActivatingAccountAgainException e) {
            return "redirect:/";
        }
    }

    /**
     * Shows login page. Also checks if user is already logged in.
     * If so he is redirected to main page.
     *
     * @param request Current servlet request
     * @return login view name or redirect to main page
     */
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public ModelAndView loginPage(HttpServletRequest request) {
        JCUser currentUser = userService.getCurrentUser();

        String referer = getReferer(request);
        if (currentUser.isAnonymous()) {
            ModelAndView mav = new ModelAndView(LOGIN);
            mav.addObject(REFERER_ATTR, referer);
            return mav;
        } else {
            return new ModelAndView("redirect:" + referer);
        }
    }

    /**
     * Gets request referrer - a page user was directed from e.g. when user followed a link or there was a redirect. In
     * most cases when user browses our forum we put the referer on our own - the page user previously was at. This is
     * done so that we can sign in and sign out user and redirect him back to original page.
     */
    private String getReferer(HttpServletRequest request) {
        String referer = request.getHeader("referer");
        HttpSession session = request.getSession(false);
        if (session != null) {
            SavedRequest savedRequest = (SavedRequest) session.getAttribute(WebAttributes.SAVED_REQUEST);
            if (savedRequest != null) {
                referer = savedRequest.getRedirectUrl();
            } else {
                String customReferer =
                        String.valueOf(session.getAttribute(RefererKeepInterceptor.CUSTOM_REFERER));
                /** We need check this !NULL_REPRESENTATION.equals(referer) strange condition 
                 *  because after CookieTheftException customReferer equals "null" (not null)
                 */
                if (customReferer != null && !NULL_REPRESENTATION.equals(customReferer)) {
                    referer = customReferer;
                }
            }
        }

        return referer;
    }

    @RequestMapping(value = "/login_ajax", method = RequestMethod.POST)
    @ResponseBody
    public JsonResponse loginAjax(@RequestParam("j_username") String username,
                                  @RequestParam("j_password") String password,
                                  @RequestParam(value = "_spring_security_remember_me", defaultValue = "off")
                                  String rememberMe,
                                  HttpServletRequest request, HttpServletResponse response) {
        boolean rememberMeBoolean = rememberMe.equals(REMEMBER_ME_ON);
        boolean isAuthenticated;
        try {
            isAuthenticated = loginWithLockHandling(username, password, rememberMeBoolean, request, response,
                    userService);
        } catch (NoConnectionException e) {
            return new JsonResponse(JsonResponseStatus.FAIL,
                    new ImmutableMap.Builder<String, String>().put("customError", "connectionError").build());
        } catch (UnexpectedErrorException e) {
            return new JsonResponse(JsonResponseStatus.FAIL,
                    new ImmutableMap.Builder<String, String>().put("customError", "unexpectedError").build());
        }
        if (isAuthenticated) {
            LocaleResolver localeResolver = RequestContextUtils.getLocaleResolver(request);
            localeResolver.setLocale(request, response, userService.getCurrentUser().getLanguage().getLocale());
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
     * @param referer    referer url
     * @param rememberMe set remember me token if equal to "on"
     * @param request    servlet request
     * @param response   servlet response
     * @return "success" or "fail" response status
     */
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ModelAndView login(@RequestParam("j_username") String username,
                              @RequestParam("j_password") String password,
                              @RequestParam(REFERER_ATTR) String referer,
                              @RequestParam(value = "_spring_security_remember_me", defaultValue = "off")
                              String rememberMe,
                              HttpServletRequest request, HttpServletResponse response) {
        boolean rememberMeBoolean = rememberMe.equals(REMEMBER_ME_ON);
        boolean isAuthenticated;
        if (referer == null || referer.contains(LOGIN)) {
            referer = MAIN_PAGE_REFERER;
        }
        try {
            isAuthenticated = loginWithLockHandling(username, password, rememberMeBoolean, request, response,
                    userService);
        } catch (NoConnectionException e) {
            return new ModelAndView(AUTH_SERVICE_FAIL_URL);
        } catch (UnexpectedErrorException e) {
            return new ModelAndView(AUTH_SERVICE_FAIL_URL);
        }
        if (isAuthenticated) {
            LocaleResolver localeResolver = RequestContextUtils.getLocaleResolver(request);
            localeResolver.setLocale(request, response, userService.getCurrentUser().getLanguage().getLocale());
            return new ModelAndView("redirect:" + referer);
        } else {
            ModelAndView modelAndView = new ModelAndView(AUTH_FAIL_URL);
            modelAndView.addObject(ATTR_USERNAME, username);
            modelAndView.addObject(REFERER_ATTR, referer);
            return modelAndView;
        }
    }

    private boolean loginWithLockHandling(String username, String password, boolean rememberMeBoolean,
                                          HttpServletRequest request, HttpServletResponse response,
                                          UserService userService)
            throws UnexpectedErrorException, NoConnectionException {
        for (int i = 0; i < LOGIN_TRIES_AFTER_LOCK; i++) {
            try {
                return userService.loginUser(username, password, rememberMeBoolean, request, response);
            } catch (HibernateOptimisticLockingFailureException e) {
                //we don't handle the exception for several times, just re-reading the content and trying again
                //after the max times exceeds, only then we give up.
            }
        }
        try {
            return userService.loginUser(username, password, rememberMeBoolean, request, response);
        } catch (HibernateOptimisticLockingFailureException e) {
            LOGGER.error("User have been locked {} times. Username: {}", LOGIN_TRIES_AFTER_LOCK, username);
            throw e;
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
    public JsonResponse usernameList(@RequestParam("pattern") String pattern) {
        return new JsonResponse(JsonResponseStatus.SUCCESS, userService.getUsernames(pattern));
    }

}
