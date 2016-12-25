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

/**
 * Represents an action of flushing changes to the ACL structure to the database.
 *
 * @author stanislav bashkirtsev
 * @see AclAction
 * @see AclTo
 * @see AclFrom
 * @see AclOn
 */
public interface AclFlush {
    /**
     * Flushes the changes made during the construction of ACL to the database and cache. This is the last action in the
     * building ACL structure.
     */
    void flush();
}
