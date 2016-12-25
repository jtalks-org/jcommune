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
package org.jtalks.jcommune.service;

import org.jtalks.common.model.entity.Entity;
import org.jtalks.common.model.entity.User;
import org.jtalks.common.model.permissions.GeneralPermission;
import org.jtalks.jcommune.service.security.acl.builders.CompoundAclBuilder;
import org.mockito.Mockito;

import static org.mockito.Mockito.mock;

/**
 * @author Kirill Afonin
 */
public final class TestUtils {

    /**
     * Create mock for {@code AclBuilder}.
     *
     * @return {@code AclBuilder} mock
     */
    public static CompoundAclBuilder<User> mockAclBuilder() {
        //noinspection unchecked
        CompoundAclBuilder<User> newBuilder = mock(CompoundAclBuilder.class);
        Mockito.when(newBuilder.grant(Mockito.any(GeneralPermission.class))).thenReturn(newBuilder);
        Mockito.when(newBuilder.to(Mockito.any(User.class))).thenReturn(newBuilder);
        Mockito.when(newBuilder.on(Mockito.any(Entity.class))).thenReturn(newBuilder);
        return newBuilder;
    }
}
