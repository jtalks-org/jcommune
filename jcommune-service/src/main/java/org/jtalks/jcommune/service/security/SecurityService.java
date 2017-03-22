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
import org.jtalks.common.service.security.SecurityContextFacade;
import org.jtalks.jcommune.model.dao.UserDao;
import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.model.entity.UserInfo;
import org.jtalks.jcommune.service.security.acl.AclManager;
import org.jtalks.jcommune.service.security.acl.builders.AclAction;
import org.jtalks.jcommune.service.security.acl.builders.AclBuilders;
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
    private final static ThreadLocal<JCUser> CACHED_USER = new ThreadLocal<>();

    /**
     * Constructor creates an instance of service.
     * @param userDao    {@link UserDao} to be injected
     * @param aclManager manager for actions with ACLs
     * @param securityContextFacade for access to security context that contain {@link Authentication} object.
     */
    public SecurityService(UserDao userDao, AclManager aclManager, SecurityContextFacade securityContextFacade) {
        this.userDao = userDao;
        this.aclManager = aclManager;
        this.securityContextFacade = securityContextFacade;
    }

    /**
     * Returns object that contains basic information about authenticated user.
     * @return {@link UserInfo} if {@link Authentication} contain it, otherwise null.
     */
    public UserInfo getCurrentUserBasicInfo() {
        Object principal = extractPrincipalFromAuthentication();
        return principal instanceof UserInfo ? (UserInfo) principal : null;
    }

    /**
     * Returns copy of persistent user.
     *
     * @param authentication to get principal
     * @return copy of persistent user associated with authentication principal, if authentication
     * or principal is null - returns null.
     */
    public JCUser getFullUserInfoFrom(Authentication authentication){
        if (authentication == null) return null;
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof UserInfo)) return null;
        return updateCacheAndGet(((UserInfo) principal).getId());
    }

    /**
     * Get current authenticated {@link User} username.
     *
     * @return current authenticated {@link User} username or {@code null} if there is no {@link User} authenticated
     * or if no authentication information is available (request not went through spring security filters).
     */
    public String getCurrentUserUsername() {
        Object principal = extractPrincipalFromAuthentication();
        if (principal == null) return null;
        String username = extractUsername(principal);
        return isAnonymous(username) ? null : username;
    }

    public Authentication getAuthentication(){
        return securityContextFacade.getContext().getAuthentication();
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
        return new UserInfo(user);
    }

    private JCUser updateCacheAndGet(long principalId){
        JCUser cached = CACHED_USER.get();
        if (cached == null || cached.getId() != principalId){
            cached = JCUser.copyUser(userDao.loadById(principalId));
            CACHED_USER.set(cached);
        }
        return cached;
    }

    /**
     * Returns the principal encapsulated by Authentication.
     *
     * @return <code>Principal</code> if {@link Authentication} contain any, otherwise null.
     */
    private Object extractPrincipalFromAuthentication() {
        Authentication auth = securityContextFacade.getContext().getAuthentication();
        return auth != null ? auth.getPrincipal() : null;
    }

    /**
     * Removes JCUser object from threadlocal storage
     */
    public void cleanThreadLocalStorage() {
        CACHED_USER.remove();
    }
}
