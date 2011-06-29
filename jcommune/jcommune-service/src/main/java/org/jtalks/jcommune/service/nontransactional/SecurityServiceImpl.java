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
import org.jtalks.jcommune.service.SecurityContextFacade;
import org.jtalks.jcommune.service.SecurityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.domain.GrantedAuthoritySid;
import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.model.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;


/**
 * Abstract layer for Spring Security.
 * Contains methods for authentication and authorization.
 *
 * @author Kirill Afonin
 * @author Max Malakhov
 */
public class SecurityServiceImpl implements SecurityService {

    private UserDao userDao;
    private MutableAclService mutableAclService;
    private SecurityContextFacade securityContextFacade;
    private final Logger logger = LoggerFactory.getLogger(SecurityServiceImpl.class);

    /**
     * Constructor creates an instance of service.
     *
     * @param userDao               {@link org.jtalks.jcommune.model.dao.UserDao} to be injected
     * @param securityContextFacade {@link org.jtalks.jcommune.service.SecurityContextFacade} to be injected
     * @param mutableAclService     service for actions with ACLs
     */
    public SecurityServiceImpl(UserDao userDao, SecurityContextFacade securityContextFacade,
                               MutableAclService mutableAclService) {
        this.userDao = userDao;
        this.securityContextFacade = securityContextFacade;
        this.mutableAclService = mutableAclService;
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

        if (null == auth) {
            return null;
        }

        Object principal = auth.getPrincipal();
        String username = "";

        // if principal is spring security user, cast it and get username
        // else it is javax.security principal with toString() that return username
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }

        return username;
    }

    /**
     * Grant {@code permisison} to current user on {@code securedObject}
     *
     * @param securedObject object for authorization
     * @param permission    granted permission
     */
    private void addPermissionToCurrentUser(Persistent securedObject,
                                            Permission permission) {
        addPermission(securedObject, new PrincipalSid(getCurrentUserUsername()), permission);
    }

    /**
     * Grant permission  {@code permision} to {@code recipient} on {@code securedObject}
     *
     * @param securedObject object for authorization
     * @param recipient     sid to whom  the permission granted
     * @param permission    granted permission
     */
    private void addPermission(Persistent securedObject, Sid recipient,
                               Permission permission) {
        MutableAcl acl;
        // create identity for securedObject
        ObjectIdentity oid = new ObjectIdentityImpl(securedObject.getClass().getCanonicalName(),
                securedObject.getId());

        try {
            acl = (MutableAcl) mutableAclService.readAclById(oid);
        } catch (NotFoundException nfe) {
            // create new Acl if not exist
            acl = mutableAclService.createAcl(oid);
        }
        // add permission to acl for recipient
        acl.insertAce(acl.getEntries().size(), permission, recipient, true);
        mutableAclService.updateAcl(acl);
        logger.debug("Added permission {} for Sid {} securedObject {}", new Object[]{permission, recipient, securedObject});
    }

    /**
     * Delete {@code permission} from {@code recipient} on {@code securedObject}
     *
     * @param securedObject object for authorization with existent Acl
     * @param recipient     sid from which will permission be removed
     * @param permission    granted permission
     */
    private void deletePermission(Persistent securedObject, Sid recipient,
                                  Permission permission) {
        // create identity for securedObject
        ObjectIdentity oid = new ObjectIdentityImpl(securedObject.getClass().getCanonicalName(),
                securedObject.getId());
        MutableAcl acl = (MutableAcl) mutableAclService.readAclById(oid);

        // Remove all permissions associated with this particular recipient
        // (string equality used to keep things simple)
        List<AccessControlEntry> entries = acl.getEntries();

        for (int i = 0; i < entries.size(); i++) {
            if (entries.get(i).getSid().equals(recipient) &&
                    entries.get(i).getPermission().equals(permission)) {
                acl.deleteAce(i);
            }
        }
        mutableAclService.updateAcl(acl);        
        logger.debug("Deleted securedObject {} ACL permissions for recipient {}" ,securedObject, recipient);
    }

    /**
     * Grant administration permission to admins.
     * Currently admins include ROLE_ADMIN.
     *
     * @param securedObject object for authorization
     */
    private void addPermissionsForAdmins(Persistent securedObject) {
        addPermission(securedObject, new GrantedAuthoritySid("ROLE_ADMIN"),
                BasePermission.ADMINISTRATION);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void grantAdminPermissionsToCreatorAndAdmins(Persistent securedObject) {
        addPermissionsForAdmins(securedObject);
        addPermissionToCurrentUser(securedObject, BasePermission.ADMINISTRATION);
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
        ObjectIdentity oid = new ObjectIdentityImpl(clazz.getCanonicalName(), id);
        mutableAclService.deleteAcl(oid, true);
        logger.debug("Deleted securedObject {} with id: {}", clazz, id);
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
