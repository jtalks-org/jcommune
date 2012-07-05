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
package org.jtalks.jcommune.model.entity;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;
import static org.testng.Assert.assertEquals;

/**
 * @author Vyacheslav Mishcheryakov
 */
public class AnonymousUserTest {

    private AnonymousUser user;

    @BeforeMethod
    public void setUp(){
        user = new AnonymousUser();
    }

    @Test
    public void testDefaults() {
        assertEquals(user.getPageSize(), JCUser.DEFAULT_PAGE_SIZE);
        assertEquals(user.getLanguage(), Language.ENGLISH);
    }
    
    @Test
    public void testIsAnonymous() {
        assertTrue(user.isAnonymous());
    }

    @Test(expectedExceptions=UnsupportedOperationException.class)
    public void testGetPassword() {
        user.getPassword();
    }
    
    @Test(expectedExceptions=UnsupportedOperationException.class)
    public void testGetEmail() {
        user.getEmail();
    }
    
    @Test(expectedExceptions=UnsupportedOperationException.class)
    public void testGetAuthorities() {
        user.getAuthorities();
    }
    
    @Test(expectedExceptions=UnsupportedOperationException.class)
    public void testGetGroups() {
        user.getGroups();
    }
    
    @Test(expectedExceptions=UnsupportedOperationException.class)
    public void testGetId() {
        user.getId();
    }
    
    @Test(expectedExceptions=UnsupportedOperationException.class)
    public void testGetUsername() {
        user.getUsername();
    }
    
    @Test(expectedExceptions=UnsupportedOperationException.class)
    public void testGetEncodedUsername() {
        user.getEncodedUsername();
    }
}
