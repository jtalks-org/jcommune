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
package org.jtalks.jcommune.web.tags;


import org.hibernate.ObjectNotFoundException;
import org.jtalks.jcommune.model.entity.JCUser;
import org.testng.annotations.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.assertFalse;

public class UsersUtilTest {

    @Test
    public void testIsExists() {
        JCUser user = new JCUser("user1", "user1@mail.org", "pass1");
        assertTrue(UsersUtil.isExists(user));
    }

    @Test
    public void testIfUserNull() {
        assertFalse(UsersUtil.isExists(null));
    }

    @Test
    public void testIfObjectNotFound() {
        JCUser user = mock(JCUser.class);
        when(user.getUsername()).thenThrow(new ObjectNotFoundException(0L, JCUser.class.getName()));
        assertFalse(UsersUtil.isExists(user));
    }
}
