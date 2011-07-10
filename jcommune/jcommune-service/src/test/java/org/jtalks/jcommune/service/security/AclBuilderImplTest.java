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
package org.jtalks.jcommune.service.security;

import org.jtalks.jcommune.model.entity.Persistent;
import org.springframework.security.acls.domain.BasePermission;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * @author Kirill Afonin
 */
public class AclBuilderImplTest {
    public static final String USERNAME = "username";
    public static final String ROLE = "ROLE_USER";
    private AclBuilder builder;
    private AclManager manager;
    private Persistent target = new Persistent() {
    };

    @BeforeMethod
    public void setUp() throws Exception {
        manager = mock(AclManager.class);
        builder = new AclBuilderImpl(manager, AclBuilderImpl.Action.GRANT);
    }

    @Test
    public void testUser() throws Exception {
        builder.user(USERNAME);

        assertTrue(builder.containsSid(USERNAME));
    }

    @Test
    public void testRole() throws Exception {
        builder.role(ROLE);

        assertTrue(builder.containsSid(ROLE));
    }

    @Test
    public void testNotContainsSid() {
        builder.user(USERNAME);

        assertFalse(builder.containsSid(ROLE));
    }

    @Test
    public void testAdmin() throws Exception {
        builder.admin();

        assertTrue(builder.hasPermission(BasePermission.ADMINISTRATION));
    }

    @Test
    public void testRead() throws Exception {
        builder.read();

        assertTrue(builder.hasPermission(BasePermission.READ));
    }

    @Test
    public void testWrite() throws Exception {
        builder.write();

        assertTrue(builder.hasPermission(BasePermission.WRITE));
    }

    @Test
    public void testDelete() throws Exception {
        builder.delete();

        assertTrue(builder.hasPermission(BasePermission.DELETE));
    }

    @Test
    public void testCreate() throws Exception {
        builder.create();

        assertTrue(builder.hasPermission(BasePermission.CREATE));
    }

    @Test
    public void testGrantOn() throws Exception {
        builder.user(USERNAME).admin().on(target);

        assertFalse(builder.containsSid(USERNAME));
        assertFalse(builder.hasPermission(BasePermission.ADMINISTRATION));
        verify(manager).grant(builder.getSids(), builder.getPermissions(), target);
    }

    @Test
    public void testRemoveOn() throws Exception {
        builder = new AclBuilderImpl(manager, AclBuilderImpl.Action.DELETE);

        builder.user(USERNAME).admin().on(target);

        assertFalse(builder.containsSid(USERNAME));
        assertFalse(builder.hasPermission(BasePermission.ADMINISTRATION));
        verify(manager).delete(builder.getSids(), builder.getPermissions(), target);
    }

    @Test
    public void testGrantToTwoSids() {
        builder.user(USERNAME).role(ROLE);

        assertTrue(builder.containsSid(ROLE));
        assertTrue(builder.containsSid(USERNAME));
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void testOnWithoutSids() {
        builder.admin().on(target);
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void testOnWithoutPermissions() {
        builder.user(USERNAME).on(target);
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void testOnWithoutSidsAndPermissions() {
        builder.on(target);
    }
}
