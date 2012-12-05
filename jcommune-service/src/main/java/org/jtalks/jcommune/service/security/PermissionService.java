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
import org.springframework.security.access.AccessDeniedException;

/**
 * Service for checking permission like @PreAuthorize('hasPermission(...)') 
 * annotation does. Intended to be used when we can't pass required IDs to 
 * methods (e.g. branch ID in code review addComment method)
 * 
 * @author Vyacheslav Mishcheryakov
 *
 * @see {@link AclGroupPermissionEvaluator}
 */
public interface PermissionService {

    /**
     * Checks if current user is granted with permission
     * @param targetId the identifier for the object instance
     * @param targetType a String representing the target's type (e.g. 'BRANCH' or 'USER'). Not null.
     * @param permission permission to check. Not null.
     * @return true if the permission is granted, false otherwise
     */
    boolean hasPermission(long targetId, String targetType, JtalksPermission permission);
    
    /**
     * Checks if current user is granted with permission
     * @param targetId the identifier for the object instance
     * @param targetType a String representing the target's type (e.g. 'BRANCH' or 'USER'). Not null.
     * @param permission a representation of the permission object as supplied by the expression system. Not null.
     * @return true if the permission is granted, false otherwise
     */
    boolean hasPermission(long targetId, String targetType, String permission);
    
    /**
     * Emulates @PreAuthorize('hasPermission(...)')
     * @param targetId the identifier for the object instance
     * @param targetType a String representing the target's type (e.g. 'BRANCH' or 'USER'). Not null.
     * @param permission permission to check. Not null.
     * 
     * @throws AccessDeniedException if current user is not granted with permission
     */
    void checkPermission(long targetId, String targetType, JtalksPermission permission);
}
