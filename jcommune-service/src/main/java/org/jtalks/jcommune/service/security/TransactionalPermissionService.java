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

import org.jtalks.common.model.permissions.JtalksPermission;
import org.jtalks.common.service.security.SecurityContextHolderFacade;
import org.jtalks.jcommune.service.security.AclGroupPermissionEvaluator;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;

public class TransactionalPermissionService implements PermissionService {

    private static final String CLASS_CANONICAL_NAME_PATTERN = "%s.%s";
    
    private SecurityContextHolderFacade contextFacade;
    
    private AclGroupPermissionEvaluator aclEvaluator;
    
    public TransactionalPermissionService(SecurityContextHolderFacade contextFacade,
            AclGroupPermissionEvaluator aclEvaluator) {
        super();
        this.contextFacade = contextFacade;
        this.aclEvaluator = aclEvaluator;
    }

    @Override
    public boolean hasPermission(long targetId, String targetType,
            JtalksPermission permission) {
        String stringPermission = String.format(CLASS_CANONICAL_NAME_PATTERN,
                permission.getClass().getSimpleName(), permission.getName()); 
        return hasPermission(targetId, targetType, stringPermission);
    }

    @Override
    public boolean hasPermission(long targetId, String targetType,
            String permission) {
        Authentication authentication = contextFacade.getContext().getAuthentication();
        return aclEvaluator.hasPermission(authentication, targetId, targetType, permission);
    }
    
    @Override
    public void checkPermission(long targetId, String targetType,
            JtalksPermission permission) throws AccessDeniedException {
        if (!hasPermission(targetId, targetType, permission)) {
            Authentication authentication = contextFacade.getContext().getAuthentication();
            throw new AccessDeniedException(
                    "Access denied for " + authentication.getName() + ". " 
                    + targetType + ": " + targetId 
                    + ", permission - " + permission.getName());
        }
    }

    
}
