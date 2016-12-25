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

import javax.annotation.Nonnull;

/**
 * A step while creating/modifying ACL structure to assign permissions to some sid entity. Sid is an object that can
 * undertake some actions, like user or group of users.
 *
 * @author stanislav bashkirtsev
 * @see AclAction
 * @see AclFrom
 * @see AclOn
 * @see AclFlush
 * @since 0.13
 */
public interface AclTo<T extends Entity> {
    /**
     * Assigns (or restrict) the permission to the specified SIDs.
     *
     * @param sids the objects (users, groups of users) to get permissions to undertake some action on object identity
     * @return the next action to be performed - choosing the object identity to assign permissions for
     */
    AclOn to(@Nonnull T... sids);
}
