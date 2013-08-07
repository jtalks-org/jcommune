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

import org.jtalks.common.service.security.SecurityContextHolderFacade;
import org.jtalks.jcommune.model.dao.UserDao;
import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.model.plugins.Plugin;
import org.jtalks.jcommune.model.plugins.SimpleAuthenticationPlugin;
import org.jtalks.jcommune.model.plugins.exceptions.NoConnectionException;
import org.jtalks.jcommune.model.plugins.exceptions.UnexpectedErrorException;
import org.jtalks.jcommune.service.Authenticator;
import org.jtalks.jcommune.service.exceptions.NotFoundException;
import org.jtalks.jcommune.service.nontransactional.EncryptionService;
import org.jtalks.jcommune.service.plugins.PluginFilter;
import org.jtalks.jcommune.service.plugins.PluginLoader;
import org.jtalks.jcommune.service.plugins.TypeFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Serves for authentication and registration user.
 * Authentication:
 * Firstly tries to authenticate by default behavior (with JCommune authentication).
 * If default authentication was failed tries to authenticate by any available plugin.
 * <p/>
 * Registration:
 * Register user by any available plugin.
 *
 * @author Andrey Pogorelov
 */
public class TransactionalAuthenticator extends AbstractTransactionalEntityService<JCUser, UserDao>
        implements Authenticator {

    private PluginLoader pluginLoader;
    private EncryptionService encryptionService;
    private AuthenticationManager authenticationManager;
    private SecurityContextHolderFacade securityFacade;
    private RememberMeServices rememberMeServices;
    private SessionAuthenticationStrategy sessionStrategy;

    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionalAuthenticator.class);


    /**
     * @param pluginLoader          used for obtain auth plugin
     * @param dao                   for operations with data storage
     * @param encryptionService     encodes user password
     * @param authenticationManager used in login logic
     * @param securityFacade        used in login logic
     * @param rememberMeServices    used in login logic to specify remember user or
     *                              not
     * @param sessionStrategy       used in login logic to call onAuthentication hook
     *                              which stored this user to online uses list.
     */
    public TransactionalAuthenticator(PluginLoader pluginLoader, UserDao dao,
                                      EncryptionService encryptionService,
                                      AuthenticationManager authenticationManager,
                                      SecurityContextHolderFacade securityFacade,
                                      RememberMeServices rememberMeServices,
                                      SessionAuthenticationStrategy sessionStrategy) {
        super(dao);
        this.pluginLoader = pluginLoader;
        this.encryptionService = encryptionService;
        this.authenticationManager = authenticationManager;
        this.securityFacade = securityFacade;
        this.rememberMeServices = rememberMeServices;
        this.sessionStrategy = sessionStrategy;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean authenticate(String username, String password, boolean rememberMe,
                                HttpServletRequest request, HttpServletResponse response)
            throws UnexpectedErrorException, NoConnectionException {
        boolean result;
        JCUser user;
        try {
            user = getByUsername(username);
            result = authenticateDefault(user, password, rememberMe, request, response);
        } catch (NotFoundException | AuthenticationException e) {
            boolean newUser = false;
            if (e instanceof NotFoundException) {
                String ipAddress = getClientIpAddress(request);
                LOGGER.info("User was not found during login process, username = {}, IP={}", username, ipAddress);
                newUser = true;
            }
            result = authenticateByPluginAndUpdateUserInfo(username, password, newUser, rememberMe, request, response);
        }
        return result;
    }

    /**
     * Authenticate user by auth plugin and save updated user details to inner database.
     *
     *
     * @param username username
     * @param password user password
     * @param newUser  is new user or not
     * @return true if authentication was successful, otherwise false
     * @throws UnexpectedErrorException if some unexpected error occurred
     * @throws NoConnectionException    if some connection error occurred
     */
    private boolean authenticateByPluginAndUpdateUserInfo(String username, String password, boolean newUser,
                                                          boolean rememberMe, HttpServletRequest request,
                                                          HttpServletResponse response)
            throws UnexpectedErrorException, NoConnectionException {
        String passwordHash = encryptionService.encryptPassword(password);
        Map<String, String> authInfo = authenticateByAvailablePlugin(username, passwordHash);
        if (authInfo.isEmpty() || !authInfo.containsKey("email") || !authInfo.containsKey("username")) {
            LOGGER.info("Could not authenticate by plugin user {}.", username);
            return false;
        }
        JCUser user = saveUser(authInfo, passwordHash, newUser);
        try {
            return authenticateDefault(user, password, rememberMe, request, response);
        } catch (AuthenticationException e) {
            return false;
        }
    }

    /**
     * Authenticate user by JCommune.
     *
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
                                        HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        try {
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
        } catch (AuthenticationException e) {
            String ipAddress = getClientIpAddress(request);
            LOGGER.info("AuthenticationException: username = {}, IP={}, message={}",
                    new Object[]{user.getUsername(), ipAddress, e.getMessage()});
            throw e;
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

        SimpleAuthenticationPlugin authPlugin = getPlugin();
        Map<String, String> authInfo = new HashMap<>();
        if (authPlugin != null && authPlugin.getState() == Plugin.State.ENABLED) {
            authInfo.putAll(authPlugin.authenticate(username, passwordHash));
        }
        return authInfo;
    }

    private JCUser getByUsername(String username) throws NotFoundException {
        JCUser user = this.getDao().getByUsername(username);
        if (user == null) {
            LOGGER.info("JCUser [" + username + "] not found.");
            throw new NotFoundException();
        }
        return user;
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-FORWARDED-FOR");
        if (ipAddress == null) {
            ipAddress = request.getRemoteAddr();
        }
        return ipAddress;
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
            user = getDao().getByUsername(authInfo.get("username"));
            if (user != null) {
                // user already exist in database (poulpe uses the same database),
                // no need to create or update update him
                return user;
            } else {
                // user not exist in database (poulpe uses own database)
                user = new JCUser(authInfo.get("username"), authInfo.get("email"), passwordHash);
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
        getDao().saveOrUpdate(user);
        return user;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public List<Map<String, String>> register(String username, String password, String email)
            throws UnexpectedErrorException, NoConnectionException, NotFoundException {
        SimpleAuthenticationPlugin authPlugin = getPlugin();
        if (authPlugin != null && authPlugin.getState() == Plugin.State.ENABLED) {
            username = username == null ? "" : username;
            email = email == null ? "" : email;
            String passwordHash = (password == null || password.isEmpty()) ? ""
                    : encryptionService.encryptPassword(password);
            return authPlugin.registerUser(username, passwordHash, email);
        } else {
            throw new NotFoundException();
        }
    }

    /**
     * Get available plugin by plugin loader.
     *
     * @return authentication plugin
     */
    protected SimpleAuthenticationPlugin getPlugin() {
        Class cl = SimpleAuthenticationPlugin.class;
        PluginFilter pluginFilter = new TypeFilter(cl);
        List<Plugin> plugins = pluginLoader.getPlugins(pluginFilter);
        return !plugins.isEmpty() ? (SimpleAuthenticationPlugin) plugins.get(0) : null;
    }

}
