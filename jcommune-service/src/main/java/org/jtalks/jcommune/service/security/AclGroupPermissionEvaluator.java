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

import org.jtalks.common.model.permissions.BranchPermission;
import org.jtalks.common.security.acl.ExtendedMutableAcl;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.acls.AclPermissionEvaluator;
import org.springframework.security.acls.domain.ObjectIdentityRetrievalStrategyImpl;
import org.springframework.security.acls.jdbc.JdbcMutableAclService;
import org.springframework.security.acls.model.AccessControlEntry;
import org.springframework.security.acls.model.ObjectIdentity;
import org.springframework.security.acls.model.ObjectIdentityGenerator;
import org.springframework.security.core.Authentication;

import javax.annotation.Nonnull;
import java.io.Serializable;

/**
 *
 */
public class AclGroupPermissionEvaluator implements PermissionEvaluator {
    private final AclPermissionEvaluator basicPermissionEvaluator;
    private final org.jtalks.common.security.acl.AclManager aclManager;
    private ObjectIdentityGenerator objectIdentityGenerator = new ObjectIdentityRetrievalStrategyImpl();
    private JdbcMutableAclService jdbcAclService;

    public AclGroupPermissionEvaluator(@Nonnull AclPermissionEvaluator basicPermissionEvaluator,
                                       @Nonnull org.jtalks.common.security.acl.AclManager aclManager) {
        this.basicPermissionEvaluator = basicPermissionEvaluator;
        this.aclManager = aclManager;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        return false;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        ObjectIdentity objectIdentity = objectIdentityGenerator.createObjectIdentity(targetId, targetType);
        boolean hasPermission = false;
        BranchPermission branchPermission = BranchPermission.CREATE_TOPICS;
        ExtendedMutableAcl extendedMutableAcl = ExtendedMutableAcl.castAndCreate(jdbcAclService.readAclById(objectIdentity));
        for (AccessControlEntry controlEntry : extendedMutableAcl.getEntries()) {
            if (controlEntry.getPermission().equals(branchPermission)) {//todo getting permission
                hasPermission = controlEntry.isGranting();
                if (!hasPermission)
                    break;
            }
        }
        return hasPermission;
    }

    public void setJdbcAclService(JdbcMutableAclService jdbcAclService) {
        this.jdbcAclService = jdbcAclService;
    }
}