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
 * A step to set from what SIDs the permissions will be removed. This class only affects the removal of permissions, for
 * granting or restricting you had to choose the appropriate method in {@link AclAction} which would lead you to the
 * {@link AclTo}.
 *
 * @author stanislav bashkirtsev
 * @see AclAction
 * @see AclTo
 * @see AclFrom
 * @see AclOn
 * @see AclFlush
 * @since 0.13
 */
public interface AclFrom<T extends Entity> {
    /**
     * Defines the SIDs (the object that had permissions) to remove their permissions from the ACL records. The
     * permission record will be removed from database at all.
     *
     * @param sids the objects like users or user groups to remove the permissions from them
     * @return the next step - setting on what object identity the permission being removing was previously set
     */
    AclOn from(@Nonnull T... sids);
}
