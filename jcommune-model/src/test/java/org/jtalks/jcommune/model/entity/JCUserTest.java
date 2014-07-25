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

import org.joda.time.DateTime;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static junit.framework.Assert.assertFalse;
import static org.testng.Assert.assertNotSame;
import static org.testng.Assert.assertTrue;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;

/**
 * @author Kirill Afonin
 */
public class JCUserTest {

    JCUser user;

    @BeforeMethod
    public void setUp(){
        user = new JCUser("username", "email@mail.com", "pass");
    }

    @Test
    public void testSpringSecurityDefaults() {
        assertTrue(user.isAccountNonExpired());
        assertTrue(user.isAccountNonLocked());
        assertTrue(user.isCredentialsNonExpired());
        assertFalse(user.isEnabled());
    }

    @Test
    public void testUserDefaultAuthority() {
        GrantedAuthority expectedAuthority = new GrantedAuthorityImpl("ROLE_USER");
        assertTrue(user.getAuthorities().contains(expectedAuthority));
    }

    @Test
    public void testUpdateLastLogin() throws InterruptedException {
        DateTime current = new DateTime();
        Thread.sleep(25);

        user.updateLastLoginTime();

        assertTrue(user.getLastLogin().isAfter(current));
    }
    
    @Test
    public void testIsAnonymous() {
        assertFalse(user.isAnonymous());
    }

    @Test
    public void copyUserShouldReturnCopyOfUser() {
        JCUser copy = JCUser.copyUser(user);

        assertNotSame(copy, user);
        assertReflectionEquals(user, copy);
    }

}
