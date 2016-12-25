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

package org.jtalks.jcommune.service.security.acl.builders;

import org.jtalks.common.model.entity.Entity;
import org.jtalks.common.model.permissions.JtalksPermission;

import javax.annotation.Nonnull;

/**
 * Represents a set of actions that can be made with permissions like grant, delete and restrict. This interface should
 * be used along with other interfaces in the package in order to create a finished chain of methods to finish granting
 * the permissions.
 *
 * @author stanislav bashkirtsev
 * @see AclTo
 * @see AclFrom
 * @see AclOn
 * @see AclFlush
 * @since 0.13
 */
public interface AclAction<T extends Entity> {
    /**
     * Gives the access for the action represented by the specified permission.
     *
     * @param permissions the permissions to be given to some Sid
     * @return next interface to continue the chain of ACL methods to be able to specify the Sid you're going to grant
     *         access to
     */
    AclTo<T> grant(@Nonnull JtalksPermission... permissions);

    /**
     * Restricts the access for the action represented by the specified permission.
     *
     * @param permissions the permissions to be restricted to some Sid provided further in the chain of methods
     * @return next interface to continue the chain of acl to be able to specify the Sid to restrict the permission to
     */
    AclTo<T> restrict(@Nonnull JtalksPermission... permissions);

    /**
     * Removes the access for the action represented by the specified permission. No matter whether this permission was
     * granted before or it was restricted, the removal of the permission will treat them both as just ACL permissions.
     *
     * @param permissions the permissions to be removed from some Sid
     * @return next interface to continue the chain of acl related methods to be able to specify the Sid to remove its
     *         permissions
     */
    AclFrom<T> delete(@Nonnull JtalksPermission... permissions);

    /**
     * Represents all the actions that can be undertaken with the permissions (effectively the are the same as the
     * methods in this interface). This might be useful to the implementations of the interface to make their
     * code/performance more effective.
     */
    enum Actions {
        /**
         * @see AclAction#grant(JtalksPermission...)
         */
        GRANT,
        /**
         * @see AclAction#delete(JtalksPermission...)
         */
        DELETE,
        /**
         * @see AclAction#restrict(JtalksPermission...)
         */
        RESTRICT
    }
}
