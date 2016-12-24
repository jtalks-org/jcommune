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

package org.jtalks.jcommune.service.security;


import org.jtalks.common.model.entity.Entity;
import org.jtalks.common.model.entity.User;
import org.jtalks.common.security.SecurityConstants;
import org.jtalks.common.security.acl.AclManager;
import org.jtalks.common.security.acl.builders.AclAction;
import org.jtalks.common.security.acl.builders.AclBuilders;
import org.jtalks.common.service.security.SecurityContextFacade;
import org.jtalks.jcommune.model.dao.UserDao;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;


/**
 * Abstract layer for Spring Security. Contains methods for authentication and authorization. This service
 *
 * @author Kirill Afonin
 * @author Max Malakhov
 * @author Dmitry Sokolov
 */
public class SecurityService implements UserDetailsService {
    private final UserDao userDao;
    private final AclManager aclManager;
    private final AclBuilders aclBuilders = new AclBuilders();
    private final SecurityContextFacade securityContextFacade;

    /**
     * Constructor creates an instance of service.
     * @param userDao    {@link UserDao} to be injected
     * @param aclManager manager for actions with ACLs
     * @param securityContextFacade
     */
    public SecurityService(UserDao userDao, AclManager aclManager, SecurityContextFacade securityContextFacade) {
        this.userDao = userDao;
        this.aclManager = aclManager;
        this.securityContextFacade = securityContextFacade;
    }

    /**
     * Get current authenticated {@link User} username.
     *
     * @return current authenticated {@link User} username or {@code null} if there is no {@link User} authenticated
     * or if no authentication information is available (request not went through spring security filters). 
     */
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
     * Creates the builder to work with the permissions.
     *
     * @param <T> entity that should be the receiver of the permission (SID)
     * @return the builder to work with the permissions
     */
    public <T extends Entity> AclAction<T> createAclBuilder() {
        return aclBuilders.newBuilder(aclManager);
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
     * Delete object from acl. All permissions will be removed.
     *
     * @param securedObject a removed secured object.
     */
    public void deleteFromAcl(Entity securedObject) {
        deleteFromAcl(securedObject.getClass(), securedObject.getId());
    }

    /**
     * Delete object from acl. All permissions will be removed.
     *
     * @param clazz object {@code Class}
     * @param id    object id
     */
    public void deleteFromAcl(Class clazz, long id) {
        aclManager.deleteFromAcl(clazz, id);
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
