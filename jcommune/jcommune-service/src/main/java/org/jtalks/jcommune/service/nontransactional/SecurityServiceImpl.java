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
package org.jtalks.jcommune.service.nontransactional;

import org.jtalks.jcommune.model.dao.UserDao;
import org.jtalks.jcommune.model.entity.Persistent;
import org.jtalks.jcommune.model.entity.User;
import org.jtalks.jcommune.service.SecurityService;
import org.jtalks.jcommune.service.security.AclBuilder;
import org.jtalks.jcommune.service.security.AclBuilderImpl;
import org.jtalks.jcommune.service.security.AclManager;
import org.jtalks.jcommune.service.security.SecurityContextFacade;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;


/**
 * Abstract layer for Spring Security.
 * Contains methods for authentication and authorization.
 *
 * @author Kirill Afonin
 * @author Max Malakhov
 */
public class SecurityServiceImpl implements SecurityService {

    private UserDao userDao;
    private AclManager aclManager;
    private SecurityContextFacade securityContextFacade;

    /**
     * Constructor creates an instance of service.
     *
     * @param userDao               {@link org.jtalks.jcommune.model.dao.UserDao} to be injected
     * @param securityContextFacade {@link org.jtalks.jcommune.service.security.SecurityContextFacade} to be injected
     * @param aclManager            manager for actions with ACLs
     */
    public SecurityServiceImpl(UserDao userDao, SecurityContextFacade securityContextFacade,
                               AclManager aclManager) {
        this.userDao = userDao;
        this.securityContextFacade = securityContextFacade;
        this.aclManager = aclManager;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User getCurrentUser() {
        return userDao.getByUsername(getCurrentUserUsername());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCurrentUserUsername() {
        Authentication auth = securityContextFacade.getContext().getAuthentication();
        if (auth == null) {
            return null;
        }
        Object principal = auth.getPrincipal();
        String username = extractUsername(principal);

        if (isAnonymous(username)) {
            return null;
        }
        return username;
    }

    /**
     * Get username from principal.
     *
     * @param principal principal
     * @return username
     */
    private String extractUsername(Object principal) {
        // if principal is spring security user, cast it and get username
        // else it is javax.security principal with toString() that return username
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        }
        return principal.toString();
    }

    /**
     * @param username username
     * @return {@code true} if user is anonymous
     */
    private boolean isAnonymous(String username) {
        return username.equals(SecurityConstants.ANONYMOUS_USERNAME);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteFromAcl(Persistent securedObject) {
        deleteFromAcl(securedObject.getClass(), securedObject.getId());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteFromAcl(Class clazz, long id) {
        aclManager.deleteFromAcl(clazz, id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AclBuilder grant() {
        return new AclBuilderImpl(aclManager, AclBuilderImpl.Action.GRANT);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AclBuilder grantToCurrentUser() {
        return new AclBuilderImpl(aclManager, AclBuilderImpl.Action.GRANT).user(getCurrentUserUsername());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AclBuilder delete() {
        return new AclBuilderImpl(aclManager, AclBuilderImpl.Action.DELETE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserDetails loadUserByUsername(String username) {
        User user = userDao.getByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found: " + username);
        }
        return user;
    }

}
