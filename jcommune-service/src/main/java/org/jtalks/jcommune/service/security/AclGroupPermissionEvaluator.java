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

import org.jtalks.common.model.dao.GroupDao;
import org.jtalks.common.model.entity.Group;
import org.jtalks.common.model.permissions.BranchPermission;
import org.jtalks.common.model.permissions.GeneralPermission;
import org.jtalks.common.model.permissions.ProfilePermission;
import org.jtalks.common.security.acl.AclUtil;
import org.jtalks.common.security.acl.GroupAce;
import org.jtalks.common.security.acl.sids.JtalksSidFactory;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.acls.model.AccessControlEntry;
import org.springframework.security.acls.model.ObjectIdentity;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.acls.model.Sid;
import org.springframework.security.core.Authentication;

import javax.annotation.Nonnull;
import java.io.Serializable;
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
    private final org.jtalks.common.security.acl.AclManager aclManager;
    private final AclUtil aclUtil;
    private final GroupDao groupDao;
    private final JtalksSidFactory sidFactory;

    public AclGroupPermissionEvaluator(@Nonnull org.jtalks.common.security.acl.AclManager aclManager,
                                       @Nonnull AclUtil aclUtil, @Nonnull GroupDao groupDao,
                                       @Nonnull JtalksSidFactory sidFactory) {
        this.aclManager = aclManager;
        this.aclUtil = aclUtil;
        this.groupDao = groupDao;
        this.sidFactory = sidFactory;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasPermission(Authentication authentication, Object groupId, Object permission) {
        if (authentication.getPrincipal() instanceof String) {
            return false;
        }
        Group group = groupDao.get((Long) groupId);
        return group.getUsers().contains(authentication.getPrincipal());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId,
                                 String targetType, Object permission) {
        boolean result = false;
        Long id = (targetId instanceof String ? Long.parseLong((String) targetId) : (Long) targetId);

        ObjectIdentity objectIdentity = aclUtil.createIdentity(id, targetType);
        Permission jtalksPermission = getPermission(permission);
        Sid sid = sidFactory.createPrincipal(authentication);

        List<AccessControlEntry> aces = aclUtil.getAclFor(objectIdentity).getEntries();
        List<GroupAce> controlEntries = aclManager.getGroupPermissionsOn(objectIdentity);

        if (isRestricted(sid, aces, jtalksPermission) ||
                isRestrictedForGroup(controlEntries, authentication)) {
            return false;
        } else if (isAllowed(sid, aces, jtalksPermission) ||
                isAllowedForGroup(controlEntries, authentication)) {
            return true;
        }
        return result;
    }

    private boolean isAllowed(Sid sid, List<AccessControlEntry> controlEntries, Permission permission) {
        for (AccessControlEntry ace : controlEntries) {
            if (sid.getSidId().equals(ace.getSid().getSidId()) &&
                    permission.equals(ace.getPermission()) && ace.isGranting()) {
                return true;
            }
        }
        return false;
    }

    private boolean isRestricted(Sid sid, List<AccessControlEntry> controlEntries, Permission permission) {
        for (AccessControlEntry ace : controlEntries) {
            if (sid.getSidId().equals(ace.getSid().getSidId()) &&
                    permission.equals(ace.getPermission()) &&
                    !ace.isGranting()) {
                return true;
            }
        }
        return false;
    }

    private boolean isAllowedForGroup(List<GroupAce> controlEntries, Authentication authentication) {
        for (GroupAce ace : controlEntries) {
            if (ace.isGranting() && ace.getGroup(groupDao).getUsers().
                    contains(authentication.getPrincipal())) {
                return true;
            }
        }
        return false;
    }

    private boolean isRestrictedForGroup(List<GroupAce> controlEntries, Authentication authentication) {
        for (GroupAce ace : controlEntries) {
            if (!ace.isGranting() && ace.getGroup(groupDao).getUsers().
                    contains(authentication.getPrincipal())) {
                return true;
            }
        }
        return false;
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