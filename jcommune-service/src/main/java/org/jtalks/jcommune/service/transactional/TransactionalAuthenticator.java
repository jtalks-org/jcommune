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

package org.jtalks.jcommune.service.transactional;

import org.hibernate.Session;
import org.joda.time.DateTime;
import org.jtalks.common.model.dao.GroupDao;
import org.jtalks.common.model.dao.hibernate.GenericDao;
import org.jtalks.common.model.entity.Group;
import org.jtalks.common.model.entity.User;
import org.jtalks.common.service.security.SecurityContextHolderFacade;
import org.jtalks.jcommune.model.dao.UserDao;
import org.jtalks.jcommune.model.dto.RegisterUserDto;
import org.jtalks.jcommune.model.dto.UserDto;
import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.plugin.api.core.AuthenticationPlugin;
import org.jtalks.jcommune.plugin.api.core.Plugin;
import org.jtalks.jcommune.plugin.api.core.RegistrationPlugin;
import org.jtalks.jcommune.plugin.api.exceptions.NoConnectionException;
import org.jtalks.jcommune.plugin.api.exceptions.UnexpectedErrorException;
import org.jtalks.jcommune.service.Authenticator;
import org.jtalks.jcommune.service.PluginService;
import org.jtalks.jcommune.service.exceptions.NotFoundException;
import org.jtalks.jcommune.service.nontransactional.EncryptionService;
import org.jtalks.jcommune.service.nontransactional.ImageService;
import org.jtalks.jcommune.service.nontransactional.MailService;
import org.jtalks.jcommune.plugin.api.PluginLoader;
import org.jtalks.jcommune.service.security.AdministrationGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.Advised;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.Validator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import org.jtalks.jcommune.model.dto.LoginUserDto;

/**
 * Serves for authentication and registration user.
 * Authentication:
 * Firstly tries to authenticate by default behavior (with JCommune authentication).
 * If default authentication was failed tries to authenticate by any available plugin.
 * <p/>
 * Registration:
 * Register user by any available plugin and save in JCommune internal database.
 *
 * @author Andrey Pogorelov
 */
public class TransactionalAuthenticator extends AbstractTransactionalEntityService<JCUser, UserDao>
        implements Authenticator {

    /**
     * While registering a new user, she gets {@link JCUser#setAutosubscribe(boolean)} set to {@code true} by default.
     * Afterwards user can edit her profile and change this setting.
     */
    public static final boolean DEFAULT_AUTOSUBSCRIBE = true;
    public static final boolean DEFAULT_SEND_PM_NOTIFICATION = true;

    private PluginLoader pluginLoader;
    private EncryptionService encryptionService;
    private AuthenticationManager authenticationManager;
    private SecurityContextHolderFacade securityFacade;
    private RememberMeServices rememberMeServices;
    private SessionAuthenticationStrategy sessionStrategy;
    private Validator validator;
    private MailService mailService;
    private ImageService avatarService;
    private GroupDao groupDao;
    private PluginService pluginService;
    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionalAuthenticator.class);


    /**
     * @param pluginLoader          used for obtain auth plugin
     * @param dao                   for operations with data storage
     * @param encryptionService     encodes user password
     * @param securityFacade        used in login logic
     * @param rememberMeServices    used in login logic to specify remember user or
     *                              not
     * @param sessionStrategy       used in login logic to call onAuthentication hook
     *                              which stored this user to online uses list.
     * @param authenticationManager to authenticate users
     */
    public TransactionalAuthenticator(PluginLoader pluginLoader, UserDao dao, GroupDao groupDao,
                                      EncryptionService encryptionService,
                                      MailService mailService,
                                      ImageService avatarService,
                                      PluginService pluginService,
                                      SecurityContextHolderFacade securityFacade,
                                      RememberMeServices rememberMeServices,
                                      SessionAuthenticationStrategy sessionStrategy,
                                      Validator validator,
                                      AuthenticationManager authenticationManager) {
        super(dao);
        this.groupDao = groupDao;
        this.pluginLoader = pluginLoader;
        this.encryptionService = encryptionService;
        this.mailService = mailService;
        this.avatarService = avatarService;
        this.pluginService = pluginService;
        this.securityFacade = securityFacade;
        this.rememberMeServices = rememberMeServices;
        this.sessionStrategy = sessionStrategy;
        this.validator = validator;
        this.authenticationManager = authenticationManager;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean authenticate(LoginUserDto loginUserDto, HttpServletRequest request,
                                HttpServletResponse response) throws UnexpectedErrorException, NoConnectionException {
        boolean result;
        JCUser user;
        try {
            user = getByUsername(loginUserDto.getUserName());
            result = authenticateDefault(user, loginUserDto.getPassword(), loginUserDto.isRememberMe(), request, response);
        } catch (NotFoundException e) {
            LOGGER.info("User was not found during login process, username = {}, IP={}", 
                    loginUserDto.getUserName(), loginUserDto.getClientIp());
            result = authenticateByPluginAndUpdateUserInfo(loginUserDto, true, request, response);
        } catch (AuthenticationException e) {
            LOGGER.info("AuthenticationException: username = {}, IP={}, message={}",
                    new String[]{loginUserDto.getUserName(), loginUserDto.getClientIp(), e.getMessage()});
            result = authenticateByPluginAndUpdateUserInfo(loginUserDto, false, request, response);
        }
        return result;
    }

    /**
     * Authenticate user by auth plugin and save updated user details to inner database.
     *
     * @param loginUserDto DTO object which represent authentication information
     * @param newUser  is new user or not
     * @return true if authentication was successful, otherwise false
     * @throws UnexpectedErrorException if some unexpected error occurred
     * @throws NoConnectionException    if some connection error occurred
     */
    private boolean authenticateByPluginAndUpdateUserInfo(LoginUserDto loginUserDto, boolean newUser,
                                                          HttpServletRequest request,
                                                          HttpServletResponse response)
            throws UnexpectedErrorException, NoConnectionException {
        String passwordHash = encryptionService.encryptPassword(loginUserDto.getPassword());
        String encodedUsername;
        try {
            encodedUsername = loginUserDto.getUserName() == null ? null : 
                    URLEncoder.encode(loginUserDto.getUserName(), "UTF-8").replace("+", "%20");
        } catch (UnsupportedEncodingException e) {
            LOGGER.error("Could not encode username '{}'", loginUserDto.getUserName());
            throw new UnexpectedErrorException(e);
        }
        Map<String, String> authInfo = authenticateByAvailablePlugin(encodedUsername, passwordHash);
        if (authInfo.isEmpty() || !authInfo.containsKey("email") || !authInfo.containsKey("username")) {
            LOGGER.info("Could not authenticate user '{}' by plugin.", loginUserDto.getUserName());
            return false;
        }
        JCUser user = saveUser(authInfo, passwordHash, newUser);
        try {
            return authenticateDefault(user, loginUserDto.getUserName(), loginUserDto.isRememberMe(), request, response);
        } catch (AuthenticationException e) {
            return false;
        }
    }

    /**
     * Authenticate user by JCommune.
     *
     * @param user       user entity
     * @param password   user password
     * @param rememberMe remember this user or not
     * @param request    HTTP request
     * @param response   HTTP response
     * @return true if authentication was successful, otherwise false
     * @throws AuthenticationException
     */
    private boolean authenticateDefault(JCUser user, String password, boolean rememberMe,
                                        HttpServletRequest request,
                                        HttpServletResponse response) throws AuthenticationException {
        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(user.getUsername(), password);
        token.setDetails(user);
        Authentication auth = authenticationManager.authenticate(token);
        securityFacade.getContext().setAuthentication(auth);
        if (auth.isAuthenticated()) {
            sessionStrategy.onAuthentication(auth, request, response);
            if (rememberMe) {
                rememberMeServices.loginSuccess(request, response, auth);
            }
            user.updateLastLoginTime();
            return true;
        }
        return false;
    }

    /**
     * Process authentication with available plugin.
     *
     * @param username     username
     * @param passwordHash user password hash
     * @return user auth details returned by authentication plugin
     * @throws UnexpectedErrorException if some unexpected error occurred
     * @throws NoConnectionException    if some connection error occurred
     */
    private Map<String, String> authenticateByAvailablePlugin(String username, String passwordHash)
            throws UnexpectedErrorException, NoConnectionException {
        AuthenticationPlugin authPlugin
                = (AuthenticationPlugin) pluginLoader.getPluginByClassName(AuthenticationPlugin.class);
        Map<String, String> authInfo = new HashMap<>();
        if (authPlugin != null && authPlugin.getState() == Plugin.State.ENABLED) {
            authInfo.putAll(authPlugin.authenticate(username, passwordHash));
        }
        return authInfo;
    }

    private JCUser getByUsername(String username) throws NotFoundException {
        JCUser user = this.getDao().getByUsername(username);
        if (user == null) {
            throw new NotFoundException();
        }
        return user;
    }

    private void copyFieldsFromUserToJCUser(User commonUser, JCUser user) {
        user.setRole(commonUser.getRole());
        user.setAvatar(commonUser.getAvatar());
        user.setBanReason(commonUser.getBanReason());
        for (Group group : commonUser.getGroups()) {
            user.addGroup(group);
        }
    }

    /**
     * Save (or update) user with specified details in internal database.
     *
     * @param authInfo     user details
     * @param passwordHash user password hash
     * @param newUser      user is new for JCommune
     * @return saved (or updated) user
     */
    private JCUser saveUser(Map<String, String> authInfo, String passwordHash, boolean newUser) {
        JCUser user;
        if (newUser) {
            user = new JCUser(authInfo.get("username"), authInfo.get("email"), passwordHash);
            user.setRegistrationDate(new DateTime());
            user.setAutosubscribe(DEFAULT_AUTOSUBSCRIBE);
            user.setSendPmNotification(DEFAULT_SEND_PM_NOTIFICATION);
            User commonUser = this.getDao().getCommonUserByUsername(authInfo.get("username"));
            if (commonUser != null) {
                copyFieldsFromUserToJCUser(commonUser, user);
                // user already exist in database (poulpe uses the same database),
                // we need to delete common User and create JCUser
                try {
                    Session session = ((GenericDao) ((Advised) this.getDao()).getTargetSource().getTarget()).session();
                    session.delete(commonUser);
                    this.getDao().flush();
                } catch (Exception e) {
                    LOGGER.warn("Could not delete common user.");
                }
            } else {
                user.setAvatar(avatarService.getDefaultImage());
            }
        } else {
            user = getDao().getByUsername(authInfo.get("username"));
            user.setPassword(passwordHash);
            user.setEmail(authInfo.get("email"));
        }
        if (authInfo.containsKey("firstName")) {
            user.setFirstName(authInfo.get("firstName"));
        }
        if (authInfo.containsKey("lastName")) {
            user.setLastName(authInfo.get("lastName"));
        }
        if (authInfo.containsKey("enabled")) {
            user.setEnabled(Boolean.parseBoolean(authInfo.get("enabled")));
        }
        if (user.isEnabled() && user.getGroups().isEmpty()) {
            Group group = groupDao.getGroupByName(AdministrationGroup.USER.getName());
            user.addGroup(group);
        }
        getDao().saveOrUpdate(user);
        return user;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BindingResult register(RegisterUserDto registerUserDto)
            throws UnexpectedErrorException, NoConnectionException {
        BindingResult result = new BeanPropertyBindingResult(registerUserDto, "newUser");
        BindingResult jcErrors = new BeanPropertyBindingResult(registerUserDto, "newUser");
        validator.validate(registerUserDto, jcErrors);
        UserDto userDto = registerUserDto.getUserDto();
        String encodedPassword = (userDto.getPassword() == null || userDto.getPassword().isEmpty()) ? ""
                : encryptionService.encryptPassword(userDto.getPassword());
        userDto.setPassword(encodedPassword);
        registerByPlugin(userDto, true, result);
        mergeValidationErrors(jcErrors, result);
        if (!result.hasErrors()) {
            registerByPlugin(userDto, false, result);
            // because next http call can fail (in the interim another user was registered)
            // we need to double check it
            if (!result.hasErrors()) {
                storeRegisteredUser(userDto);
            }
        } 
        return result;
    }

    public void registerByPlugin(UserDto userDto, boolean dryRun, BindingResult bindingResult)
            throws UnexpectedErrorException, NoConnectionException {
        Map<Long, RegistrationPlugin> registrationPlugins = pluginService.getRegistrationPlugins();
        for (Map.Entry<Long, RegistrationPlugin> entry : registrationPlugins.entrySet()) {
            RegistrationPlugin registrationPlugin = entry.getValue();
            if (registrationPlugin != null && registrationPlugin.getState() == Plugin.State.ENABLED) {
                Map<String, String> errors = dryRun
                        ? registrationPlugin.validateUser(userDto, entry.getKey())
                        : registrationPlugin.registerUser(userDto, entry.getKey());
                for (Map.Entry<String, String> error : errors.entrySet()) {
                    bindingResult.rejectValue(error.getKey(), null, error.getValue());
                }
            }
        }
    }

    protected void mergeValidationErrors(BindingResult srcErrors, BindingResult dstErrors) {
        for (FieldError error : srcErrors.getFieldErrors()) {
            if (!dstErrors.hasFieldErrors(error.getField())) {
                dstErrors.addError(error);
            }
        }
    }
   
    /**
     * Just saves a new {@link JCUser} or upgrade {@link org.jtalks.common.model.entity.User}
     * to {@link JCUser} without any additional checks
     *
     * @param userDto coming from enclosing methods, this object is built by Spring MVC
     * @return stored user
     */
    public JCUser storeRegisteredUser(UserDto userDto) {
        // check if user already saved by plugin as common user
        User commonUser = this.getDao().getCommonUserByUsername(userDto.getUsername());
        if (commonUser != null) {
            // in this case we must delete old common user and save user as JCUser,
            // because hibernate doesn't allow upgrade common User to JCUser
            try {
                Session session = ((GenericDao) ((Advised) this.getDao()).getTargetSource().getTarget()).session();
                session.delete(commonUser);
                this.getDao().flush();
            } catch (Exception e) {
                LOGGER.warn("Could not delete common user. This is needed if Poulpe still works with JCommune on the " +
                        "same DB and therefore saves User first.");
            }
        }
        JCUser user = new JCUser(userDto.getUsername(), userDto.getEmail(), userDto.getPassword());
        user.setLanguage(userDto.getLanguage());
        user.setAutosubscribe(DEFAULT_AUTOSUBSCRIBE);
        user.setSendPmNotification(DEFAULT_SEND_PM_NOTIFICATION);
        user.setAvatar(avatarService.getDefaultImage());
        user.setRegistrationDate(new DateTime());
        this.getDao().saveOrUpdate(user);
        mailService.sendAccountActivationMail(user);
        LOGGER.info("JCUser registered: {}", user.getUsername());
        return user;
    }
    
}
