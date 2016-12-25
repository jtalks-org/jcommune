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

/**
 * Classes within this package relate to Spring ACL and granting/restricting/deleting permissions with handy classes
 * and methods. The main class here is {@link AclBuilders} which can create ACL builders to be used in order to
 * construct granting or any other operation on the permissions. Note, that there are several small interfaces like
 * {@link AclAction} or {@link AclTo} that contain methods related to some particular step in creating full-blown ACL.
 * You should deal with these interfaces again through {@link AclBuilders}.
 */
package org.jtalks.jcommune.service.security.acl.builders;