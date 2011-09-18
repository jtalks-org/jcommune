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

import org.jtalks.common.model.entity.Entity;
import org.jtalks.jcommune.service.security.AclBuilder;
import org.mockito.Matchers;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Kirill Afonin
 */
public final class TestUtils {

    /**
     * Create mock for {@code AclBuilder}.
     *
     * @return {@code AclBuilder} mock
     */
    public static AclBuilder mockAclBuilder() {
        AclBuilder newBuilder = mock(AclBuilder.class);
        when(newBuilder.read()).thenReturn(newBuilder);
        when(newBuilder.admin()).thenReturn(newBuilder);
        when(newBuilder.delete()).thenReturn(newBuilder);
        when(newBuilder.create()).thenReturn(newBuilder);
        when(newBuilder.write()).thenReturn(newBuilder);
        when(newBuilder.user(Matchers.anyString())).thenReturn(newBuilder);
        when(newBuilder.role(Matchers.anyString())).thenReturn(newBuilder);
        when(newBuilder.on(Matchers.<Entity>anyObject())).thenReturn(newBuilder);
        return newBuilder;
    }
}
