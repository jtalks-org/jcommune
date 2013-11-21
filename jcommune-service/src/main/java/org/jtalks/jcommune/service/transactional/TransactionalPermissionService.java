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

import org.jtalks.common.model.permissions.BranchPermission;
import org.jtalks.common.model.permissions.JtalksPermission;
import org.jtalks.common.service.security.SecurityContextHolderFacade;
import org.jtalks.jcommune.service.security.AclClassName;
import org.jtalks.jcommune.service.security.AclGroupPermissionEvaluator;
import org.jtalks.jcommune.service.security.PermissionService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;

/**
 * Implementation of {@link org.jtalks.jcommune.service.security.PermissionService} interface
 *
 * @author Vyacheslav Mishcheryakov
 */
public class TransactionalPermissionService implements PermissionService {
    /**
     * Pattern to build permission full name, here is an example of eventual string:
     * {@code BranchPermissions.EDIT_OWN_POSTS}
     */
    private static final String PERMISSION_FULLNAME_PATTERN = "%s.%s";
    private SecurityContextHolderFacade contextFacade;
    private AclGroupPermissionEvaluator aclEvaluator;

    /**
     * @param contextFacade to get {@link Authentication} object from security context
     * @param aclEvaluator  to evaluate permissions
     */
    public TransactionalPermissionService(SecurityContextHolderFacade contextFacade,
                                          AclGroupPermissionEvaluator aclEvaluator) {
        this.contextFacade = contextFacade;
        this.aclEvaluator = aclEvaluator;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasPermission(long targetId, AclClassName targetClass, JtalksPermission permission) {
        String stringPermission = String.format(PERMISSION_FULLNAME_PATTERN,
                permission.getClass().getSimpleName(), permission.getName());
        return hasPermission(targetId, targetClass.toString(), stringPermission);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasPermission(long targetId, String targetType, String permission) {
        Authentication authentication = contextFacade.getContext().getAuthentication();
        return aclEvaluator.hasPermission(authentication, targetId, targetType, permission);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasBranchPermission(long branchId, BranchPermission permission) {
        return hasPermission(branchId, AclClassName.BRANCH, permission);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void checkPermission(long targetId, AclClassName targetClass,
                                JtalksPermission permission) {
        if (!hasPermission(targetId, targetClass, permission)) {
            Authentication authentication = contextFacade.getContext().getAuthentication();
            throw new AccessDeniedException(
                    "Access denied for " + authentication.getName() + ". "
                            + targetClass + ": " + targetId
                            + ", permission - " + permission.getName());
        }
    }


}
