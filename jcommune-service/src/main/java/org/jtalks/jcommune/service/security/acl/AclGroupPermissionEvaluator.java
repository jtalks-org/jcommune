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

package org.jtalks.jcommune.service.security.acl;

import org.apache.commons.lang.Validate;
import org.jtalks.common.model.entity.Group;
import org.jtalks.common.model.permissions.BranchPermission;
import org.jtalks.common.model.permissions.GeneralPermission;
import org.jtalks.common.model.permissions.JtalksPermission;
import org.jtalks.common.model.permissions.ProfilePermission;
import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.model.entity.UserInfo;
import org.jtalks.jcommune.plugin.api.PluginPermissionManager;
import org.jtalks.jcommune.service.security.SecurityService;
import org.jtalks.jcommune.service.security.acl.sids.JtalksSidFactory;
import org.jtalks.jcommune.service.security.acl.sids.UniversalSid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.acls.jdbc.JdbcMutableAclService;
import org.springframework.security.acls.model.*;
import org.springframework.security.core.Authentication;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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
    private final JtalksSidFactory sidFactory;
    private final JdbcMutableAclService mutableAclService;
    private final PluginPermissionManager pluginPermissionManager;
    private final SecurityService securityService;

    /**
     * @param aclManager        for getting permissions on object indentity
     * @param aclUtil           utilities to work with Spring ACL
     * @param sidFactory        factory to work with principals
     * @param mutableAclService for checking existing of sids
     * @param securityService   to get current user from SecurityContext
     */
    public AclGroupPermissionEvaluator(@Nonnull AclManager aclManager,
                                       @Nonnull AclUtil aclUtil,
                                       @Nonnull JtalksSidFactory sidFactory,
                                       @Nonnull JdbcMutableAclService mutableAclService,
                                       @Nonnull PluginPermissionManager pluginPermissionManager,
                                       @Nonnull SecurityService securityService) {
        this.aclManager = aclManager;
        this.aclUtil = aclUtil;
        this.sidFactory = sidFactory;
        this.mutableAclService = mutableAclService;
        this.pluginPermissionManager = pluginPermissionManager;
        this.securityService = securityService;
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
     * {@inheritDoc}
     */
    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId,
                                 String targetType, Object permission) {
        Long id = parseTargetId(targetId);
        JtalksPermission jtalksPermission = parseJtalksPermissionFrom(permission);
        if (jtalksPermission == ProfilePermission.EDIT_OWN_PROFILE &&
                ((UserInfo) authentication.getPrincipal()).getId() != id) {
            return false;
        }
        ObjectIdentity objectIdentity = aclUtil.createIdentity(id, targetType);
        Sid sid = sidFactory.createPrincipal(authentication);
        List<AccessControlEntry> aces;
        List<GroupAce> controlEntries;

        try {
            aces = ExtendedMutableAcl.castAndCreate(mutableAclService.readAclById(objectIdentity)).getEntries();
            controlEntries = aclManager.getGroupPermissionsFilteredByPermissionOn(objectIdentity, jtalksPermission);
        } catch (NotFoundException nfe) {
            aces = new ArrayList<>();
            controlEntries = new ArrayList<>();
        }
        if (jtalksPermission instanceof ProfilePermission && authentication.getPrincipal() instanceof UserInfo){
            if (isRestrictedPersonalPermission(authentication, jtalksPermission)) return false;
            else if (isAllowedPersonalPermission(authentication, jtalksPermission)) return true;
        }
        if (isRestrictedForSid(sid, aces, jtalksPermission) ||
                isRestrictedForGroup(controlEntries, authentication, jtalksPermission)) {
            return false;
        } else if (isAllowedForSid(sid, aces, jtalksPermission) ||
                isAllowedForGroup(controlEntries, authentication, jtalksPermission)) {
            return true;
        }
        return false;
    }

    /**
     * Parses targetId parameter
     *
     * @param targetId parameter value to parse.
     * @return targetId as Long.
     */
    private Long parseTargetId(Serializable targetId) {
        Validate.isTrue(targetId instanceof String || targetId instanceof Long, "Can't parse targetId, value=[" + targetId + "]");
        if (targetId instanceof String) return Long.parseLong((String) targetId);
        return (Long) targetId;
    }

    /**
     * Check if this <tt>personal permission</tt> is allowed for groups of user from authentication
     *
     * @return <code>true</code> if this permission is allowed
     */
    private boolean isAllowedPersonalPermission(Authentication authentication, Permission permission) {
        return isGrantedPersonalPermission(authentication, permission, true);
    }

    /**
     * Check if this <tt>personal permission</tt> is restricted for groups of user from authentication
     *
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
        return ace.isGranting() == isCheckAllowedGrant
                && permission.equals(ace.getPermission())
                && ((UniversalSid)sid).getSidId().equals(((UniversalSid)ace.getSid()).getSidId());
    }

    /**
     * Check if this <tt>permission</tt> is granted for groups of user from authentication
     *
     * @param permission          permission to check
     * @param isCheckAllowedGrant flag that indicates what type of grant need to
     *                            be checked  - 'allowed' (true) or 'restricted' (false)
     * @return <code>true</code> if this permission was found with specified
     *         type of grant.
     */
    private boolean isGrantedPersonalPermission(Authentication authentication, Permission permission,
                                                boolean isCheckAllowedGrant) {
        JCUser jcUser = securityService.getFullUserInfoFrom(authentication);
        if (jcUser == null) return false;
        List<Group> groups = jcUser.getGroups();
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
        return false;
    }

    /**
     * Check if this <tt>permission</tt> is allowed for any <tt>authority's</tt>
     * group.
     *
     * @param controlEntries list of entries with security information for groups
     *                       to loop through
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
     * @param permission          permission to check
     * @param isCheckAllowedGrant flag that indicates what type of grant need to
     *                            be checked  - 'allowed' (true) or 'restricted' (false)
     * @return <code>true</code> if this permission was found with specified
     *         type of grant.
     */
    private boolean isGrantedForGroup(List<GroupAce> controlEntries,
                                      Authentication authentication, Permission permission,
                                      boolean isCheckAllowedGrant) {
        JCUser jcUser = securityService.getFullUserInfoFrom(authentication);
        if (jcUser == null) return false;
        List<Long> groupsIDs = jcUser.getGroupsIDs();
        for (GroupAce ace : controlEntries) {
            if (groupsIDs.contains(ace.getGroupId())) {
                if (isGrantedForGroup(ace, permission, isCheckAllowedGrant)) return true;
            }
        }
        return false;
    }

    /**
     * Check if this <tt>permission</tt> is granted for any <tt>authority's</tt>
     * group.
     *
     * @param ace                 entry with security information (for groups)
     * @param permission          permission to check
     * @param isCheckAllowedGrant flag that indicates what type of grant need to
     *                            be checked  - 'allowed' (true) or 'restricted' (false)
     * @return <code>true</code> if this entry has specified <tt>permission</tt>
     *         and type of grant.
     */
    private boolean isGrantedForGroup(GroupAce ace, Permission permission, boolean isCheckAllowedGrant) {
        Permission permissionToComapare = ace.getPermission();
        if (permissionToComapare == null) {
            permissionToComapare = pluginPermissionManager.findPluginsBranchPermissionByMask(ace.getPermissionMask());
        }
        return ace.isGranting() == isCheckAllowedGrant
                && permission.equals(permissionToComapare);
    }

    private JtalksPermission parseJtalksPermissionFrom(Object permission) {
        if (permission instanceof Permission) return (JtalksPermission) permission;
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