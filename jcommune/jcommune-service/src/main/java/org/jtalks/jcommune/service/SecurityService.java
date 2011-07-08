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
package org.jtalks.jcommune.service;

import org.jtalks.jcommune.model.entity.Persistent;
import org.jtalks.jcommune.model.entity.User;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.acls.model.Sid;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * This interface declare methods for authentication and authorization.
 *
 * @author Kirill Afonin
 * @author Max Malakhov
 */
public interface SecurityService extends UserDetailsService {

    /**
     * Get current authenticated {@link User}.
     *
     * @return current authenticated {@link User} or {@code null} if there is
     *         no authenticated {@link User}.
     * @see User
     */
    User getCurrentUser();

    /**
     * Get current authenticated {@link User} username.
     *
     * @return current authenticated {@link User} username or {@code null} if there is
     *         no authenticated {@link User}.
     */
    String getCurrentUserUsername();

    /**
     * Add administrator permissions to current user and administrators.
     *
     * @param securedObject a new secured object.
     */
    void grantAdminPermissionToCurrentUserAndAdmins(Persistent securedObject);

    /**
     * Delete object from acl. All permissions will be removed.
     *
     * @param securedObject a removed secured object.
     */
    void deleteFromAcl(Persistent securedObject);

    /**
     * Delete object from acl. All permissions will be removed.
     *
     * @param clazz object {@code Class}
     * @param id    object id
     */
    void deleteFromAcl(Class clazz, long id);

    /**
     * Delete {@code permission} from {@code recipient} on {@code securedObject}
     *
     * @param securedObject object for authorization with existent Acl
     * @param recipient     sid from which will permission be removed
     * @param permission    granted permission
     */
    void deletePermission(Persistent securedObject, Sid recipient,
                          Permission permission);
}
