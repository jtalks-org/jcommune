package org.jtalks.jcommune.service;

import org.jtalks.jcommune.model.entity.User;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * This interface declare methods for authentication and authorization.
 *
 * @author Kirill Afonin
 */
public interface SecurityService extends UserDetailsService {

    /**
     * Get current authenticated {@link User}.
     *
     * @return current authenticated {@link User} or <code>null</code> if there is no authenticated {@link User}.
     * @see User
     */
    User getCurrentUser();

    /**
     * Get current authenticated {@link User} username.
     *
     * @return current authenticated {@link User} username or <code>null</code> if there is no authenticated {@link User}.
     */
    String getCurrentUserUsername();

    /**
     * Authenticate {@link User}.
     *
     * @param user {@link User} which must be authenticated.
     */
    void authenticateUser(User user);

    /**
     * {@link UserService} setter for DI.
     *
     * @param userService {@link UserService} to be injected.
     * @see UserService
     */
    void setUserService(UserService userService);

    /**
     * {@link SecurityContextFacade} setter for DI.
     *
     * @param securityContextFacade {@link SecurityContextFacade} to be injected.
     */
    void setSecurityContextFacade(SecurityContextFacade securityContextFacade);
}
