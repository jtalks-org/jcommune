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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import org.apache.commons.lang.Validate;
//import org.jtalks.common.model.dao.GroupDao;
//import org.jtalks.common.model.entity.Group;
//import org.jtalks.common.model.entity.User;
import org.jtalks.common.model.dao.GroupDao;
import org.jtalks.common.model.entity.Group;
import org.jtalks.common.model.permissions.BranchPermission;
import org.jtalks.common.model.permissions.GeneralPermission;
import org.jtalks.common.model.permissions.ProfilePermission;
import org.jtalks.common.security.acl.AclManager;
import org.jtalks.common.security.acl.AclUtil;
import org.jtalks.common.security.acl.ExtendedMutableAcl;
import org.jtalks.common.security.acl.GroupAce;
import org.jtalks.common.security.acl.sids.JtalksSidFactory;

import org.jtalks.common.security.acl.sids.UniversalSid;
import org.jtalks.jcommune.model.dao.UserDao;
import org.jtalks.jcommune.model.entity.JCUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.acls.jdbc.JdbcMutableAclService;
import org.springframework.security.acls.model.AccessControlEntry;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.security.acls.model.ObjectIdentity;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.acls.model.Sid;
import org.springframework.security.core.Authentication;

/**
 * This evaluator is used to process the annotations of the Spring Security like {@link
 * org.springframework.security.access.prepost.PreAuthorize}. In order to be able to use it, you need to specify the id
 * of object identity, the class of object identity and one of implementation of {@link
 * org.jtalks.common.model.permissions.JtalksPermission}. So it should look precisely like this:<br/> <code>
 * \@PreAuthorize("hasPermission(#topicId, 'TOPIC', 'GeneralPermission.WRITE')")</code>
 *
 * @author Elena Lepaeva
 * @author stanislav bashkirtsev
 */
public class AclGroupPermissionEvaluator implements PermissionEvaluator {
    private static final Logger LOGGER = LoggerFactory.getLogger(AclGroupPermissionEvaluator.class);

    private final AclManager aclManager;
    private final AclUtil aclUtil;
    private final GroupDao groupDao;
    private final JtalksSidFactory sidFactory;
    private final JdbcMutableAclService mutableAclService;
    private final UserDao userDao;

    /**
     * @param aclManager        for getting permissions on object indentity
     * @param aclUtil           utilities to work with Spring ACL
     * @param groupDao          dao for user group getting
     * @param sidFactory        factory to work with principals
     * @param mutableAclService for checking existing of sids
     */
    public AclGroupPermissionEvaluator(@Nonnull org.jtalks.common.security.acl.AclManager aclManager,
                                       @Nonnull AclUtil aclUtil,
                                       @Nonnull GroupDao groupDao,
                                       @Nonnull JtalksSidFactory sidFactory,
                                       @Nonnull JdbcMutableAclService mutableAclService,
                                       @Nonnull UserDao userDao) {
        this.aclManager = aclManager;
        this.aclUtil = aclUtil;
        this.sidFactory = sidFactory;
        this.mutableAclService = mutableAclService;
        this.userDao = userDao;
        this.groupDao = groupDao;
    }

    /**
     * {@inheritDoc}
     * NOTE: Method with current arguments is not supported.
     */
    @Override
    @Deprecated
    public boolean hasPermission(Authentication authentication, Object targetId, Object permission) {
        throw new UnsupportedOperationException("Current implementation does not support this method");
    }

    /**
     * TODO In runtime authentication object contains clear user password (not the hashed one).
     * May be potential security issue.
     * <p/>
     * {@inheritDoc}
     */
    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId,
                                 String targetType, Object permission) {
        boolean result = false;
        Long id = parseTargetId(targetId);

        ObjectIdentity objectIdentity = aclUtil.createIdentity(id, targetType);
        Permission jtalksPermission = getPermission(permission);
        Sid sid = sidFactory.createPrincipal(authentication);
        List<AccessControlEntry> aces;
        List<GroupAce> controlEntries;

        try {
            aces = ExtendedMutableAcl.castAndCreate(mutableAclService.readAclById(objectIdentity)).getEntries();
            controlEntries = aclManager.getGroupPermissionsOn(objectIdentity);
        } catch (NotFoundException nfe) {
            aces = new ArrayList<>();
            controlEntries = new ArrayList<>();
        }

        if (permission == ProfilePermission.EDIT_OWN_PROFILE &&
                ((JCUser) authentication.getPrincipal()).getId() != id) {
            return false;
        }

        if (isRestrictedForSid(sid, aces, jtalksPermission) ||
                isRestrictedForGroup(controlEntries, authentication, jtalksPermission) ||
                isRestrictedPersonalPermission(authentication, jtalksPermission)) {
            return false;
        } else if (isAllowedForSid(sid, aces, jtalksPermission) ||
                isAllowedForGroup(controlEntries, authentication, jtalksPermission) ||
                isAllowedPersonalPermission(authentication, jtalksPermission)) {
            return true;
        }
        return result;
    }

    /**
     * Parses targetId parameter
     *
     * @param targetId parameter value to parse.
     * @return targetId as Long.
     */
    private Long parseTargetId(Serializable targetId) {
        Long result = 0L;
        Validate.isTrue(targetId instanceof String || targetId instanceof Long);
        if (targetId instanceof String) {
            result = Long.parseLong((String) targetId);
        } else if (targetId instanceof Long) {
            result = (Long) targetId;
        }
        return result;
    }

    /**
     * Check if this <tt>personal permission</tt> is allowed for groups of user from authentication
     *
     * @param authentication authentication to check permission for it
     * @return <code>true</code> if this permission is allowed
     */
    private boolean isAllowedPersonalPermission(Authentication authentication, Permission permission) {
        return isGrantedPersonalPermission(authentication, permission, true);
    }

    /**
     * Check if this <tt>personal permission</tt> is restricted for groups of user from authentication
     *
     * @param authentication authentication to check permission for it
     * @return <code>true</code> if this permission is restricted
     */
    private boolean isRestrictedPersonalPermission(Authentication authentication, Permission permission) {
        return isGrantedPersonalPermission(authentication, permission, false);
    }


    /**
     * Check if this <tt>permission</tt> is allowed for specified <tt>sid</tt>
     *
     * @param sid            sid to check permission for it
     * @param controlEntries list of records with security information for sids
     * @param permission     permission to check
     * @return <code>true</code> if this permission is allowed
     */
    private boolean isAllowedForSid(Sid sid, List<AccessControlEntry> controlEntries, Permission permission) {
        return isGrantedForSid(sid, controlEntries, permission, true);
    }

    /**
     * Check if this <tt>permission</tt> is restricted for specified <tt>sid</tt>
     *
     * @param sid            sid to check permission for it
     * @param controlEntries list of records with security information for sids
     * @param permission     permission to check
     * @return <code>true</code> if this permission is restricted
     */
    private boolean isRestrictedForSid(Sid sid, List<AccessControlEntry> controlEntries, Permission permission) {
        return isGrantedForSid(sid, controlEntries, permission, false);
    }

    /**
     * Check if this <tt>permission</tt> is granted for specified <tt>sid</tt>
     *
     * @param sid                 sid to check permission for it
     * @param controlEntries      list of records with security information for sids
     * @param permission          permission to check
     * @param isCheckAllowedGrant flag that indicates what type of grant need to
     *                            be checked  - 'allowed' (true) or 'restricted' (false)
     * @return <code>true</code> if this permission was found with specified
     *         type of grant.
     */
    private boolean isGrantedForSid(Sid sid, List<AccessControlEntry> controlEntries,
                                    Permission permission, boolean isCheckAllowedGrant) {
        for (AccessControlEntry ace : controlEntries) {
            if (isGrantedForSid(sid, ace, permission, isCheckAllowedGrant)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if this <tt>permission</tt> is granted for specified <tt>sid</tt>
     *
     * @param sid                 sid to check permission for it
     * @param ace                 entry with security information (for sids)
     * @param permission          permission to check
     * @param isCheckAllowedGrant flag that indicates what type of grant need to
     *                            be checked  - 'allowed' (true) or 'restricted' (false)
     * @return <code>true</code> if this entry has specified <tt>permission</tt>
     *         and type of grant.
     */
    private boolean isGrantedForSid(Sid sid, AccessControlEntry ace,
                                    Permission permission, boolean isCheckAllowedGrant) {
        return ((UniversalSid)sid).getSidId().equals(((UniversalSid)ace.getSid()).getSidId())
                && permission.equals(ace.getPermission())
                && (ace.isGranting() == isCheckAllowedGrant);
    }

    /**
     * Check if this <tt>permission</tt> is granted for groups of user from authentication
     *
     * @param authentication      authentication to check permission for it
     * @param permission          permission to check
     * @param isCheckAllowedGrant flag that indicates what type of grant need to
     *                            be checked  - 'allowed' (true) or 'restricted' (false)
     * @return <code>true</code> if this permission was found with specified
     *         type of grant.
     */
    private boolean isGrantedPersonalPermission(Authentication authentication, Permission permission,
                                                boolean isCheckAllowedGrant) {
        if (authentication.getPrincipal() instanceof JCUser) {
            JCUser storedUser = (JCUser) authentication.getPrincipal();
            // retriev user with replicated groups from EhCache
            JCUser actualUser = userDao.get(storedUser.getId());
            if (actualUser == null) {
                LOGGER.warn("{} : User #{} not found",
                        this.getClass().getCanonicalName(),
                        storedUser.getId());
                return !isCheckAllowedGrant;
            }
            List<Group> groups = actualUser.getGroups();
            for (Group group : groups) {
                ObjectIdentity groupIdentity = aclUtil.createIdentity(group.getId(), "GROUP");
                Sid groupSid = sidFactory.create(group);
                List<AccessControlEntry> groupAces;
                try {
                    groupAces = ExtendedMutableAcl.castAndCreate(
                            mutableAclService.readAclById(groupIdentity)).getEntries();
                } catch (NotFoundException nfe) {
                    groupAces = new ArrayList<>();
                }
                if (isGrantedForSid(groupSid, groupAces, permission, isCheckAllowedGrant)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Check if this <tt>permission</tt> is allowed for any <tt>authority's</tt>
     * group.
     *
     * @param controlEntries list of entries with security information for groups
     *                       to loop through
     * @param authentication authentication to check permission for it
     * @param permission     permission to check
     * @return <code>true</code> if this permission is allowed.
     */
    private boolean isAllowedForGroup(List<GroupAce> controlEntries,
                                      Authentication authentication, Permission permission) {
        return isGrantedForGroup(controlEntries, authentication, permission, true);
    }

    /**
     * Check if this <tt>permission</tt> is restricted for any <tt>authority's</tt>
     * group.
     *
     * @param controlEntries list of entries with security information for groups
     *                       to loop through
     * @param authentication authentication to check permission for it
     * @param permission     permission to check
     * @return <code>true</code> if this permission is restricted.
     */
    private boolean isRestrictedForGroup(List<GroupAce> controlEntries,
                                         Authentication authentication, Permission permission) {
        return isGrantedForGroup(controlEntries, authentication, permission, false);
    }

    /**
     * Check if this <tt>permission</tt> is granted for any <tt>authority's</tt>
     * group.
     *
     * @param controlEntries      list of entries with security information for groups
     *                            to loop through
     * @param authentication      authentication to check permission for it
     * @param permission          permission to check
     * @param isCheckAllowedGrant flag that indicates what type of grant need to
     *                            be checked  - 'allowed' (true) or 'restricted' (false)
     * @return <code>true</code> if this permission was found with specified
     *         type of grant.
     */
    private boolean isGrantedForGroup(List<GroupAce> controlEntries,
                                      Authentication authentication, Permission permission,
                                      boolean isCheckAllowedGrant) {
        if (authentication.getPrincipal() instanceof JCUser) {
            for (GroupAce ace : controlEntries) {
                if (isGrantedForGroup(ace, authentication, permission, isCheckAllowedGrant)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Check if this <tt>permission</tt> is granted for any <tt>authority's</tt>
     * group.
     *
     * @param ace                 entry with security information (for groups)
     * @param authentication      authentication to check permission for it
     * @param permission          permission to check
     * @param isCheckAllowedGrant flag that indicates what type of grant need to
     *                            be checked  - 'allowed' (true) or 'restricted' (false)
     * @return <code>true</code> if this entry has specified <tt>permission</tt>
     *         and type of grant.
     */
    private boolean isGrantedForGroup(GroupAce ace, Authentication authentication,
                                      Permission permission, boolean isCheckAllowedGrant) {
        return ace.isGranting() == isCheckAllowedGrant
                && permission.equals(ace.getPermission())
                && ace.getGroup(groupDao).getUsers().
                contains(authentication.getPrincipal());
    }

    private Permission getPermission(Object permission) {
        String permissionName = (String) permission;

        if ((permissionName).startsWith(GeneralPermission.class.getSimpleName())) {
            String particularPermission = permissionName.replace(GeneralPermission.class.getSimpleName() + ".", "");
            return GeneralPermission.valueOf(particularPermission);

        } else if ((permissionName).startsWith(BranchPermission.class.getSimpleName())) {
            String particularPermission = permissionName.replace(BranchPermission.class.getSimpleName() + ".", "");
            return BranchPermission.valueOf(particularPermission);

        } else if ((permissionName).startsWith(ProfilePermission.class.getSimpleName())) {
            String particularPermission = permissionName.replace(ProfilePermission.class.getSimpleName() + ".", "");
            return ProfilePermission.valueOf(particularPermission);

        } else {
            throw new IllegalArgumentException("No other permissions that GeneralPermission are supported now. " +
                    "Was specified: " + permission);
        }
    }
}