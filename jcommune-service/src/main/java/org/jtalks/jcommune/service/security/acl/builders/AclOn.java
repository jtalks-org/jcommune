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
 * A step while creating/modifying ACL structure to assign the permissions on some object identity (like branch, topic,
 * post).
 *
 * @author stanislav bashkirtsev
 * @see AclTo
 * @see AclFrom
 * @see AclAction
 * @see AclFlush
 */
public interface AclOn {
    /**
     * This method states for what object the SID will get a permission. Object Identity (or secured object) is always
     * some object SIDs can do something with, e.g. it can be a branch, or a topic, or a post, or anything else.
     *
     * @param objectIdentity the secured object to set permissions to make actions on it
     * @return the next step of the chain - flushing the changes to the database
     */
    AclFlush on(@Nonnull Entity objectIdentity);
}
