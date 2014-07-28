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

import com.google.common.collect.Sets;
import org.joda.time.DateTime;
import org.jtalks.common.model.entity.Group;
import org.jtalks.common.model.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotSame;
import static org.testng.Assert.*;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;

/**
 * @author Kirill Afonin
 */
public class JCUserTest {

    @Test
    public void testSpringSecurityDefaults() {
        JCUser user =  new JCUser("username", "email@mail.com", "pass");

        assertTrue(user.isAccountNonExpired());
        assertTrue(user.isAccountNonLocked());
        assertTrue(user.isCredentialsNonExpired());
        assertFalse(user.isEnabled());
    }

    @Test
    public void testUserDefaultAuthority() {
        JCUser user =  new JCUser("username", "email@mail.com", "pass");

        GrantedAuthority expectedAuthority = new GrantedAuthorityImpl("ROLE_USER");
        assertTrue(user.getAuthorities().contains(expectedAuthority));
    }

    @Test
    public void testUpdateLastLogin() throws InterruptedException {
        JCUser user =  new JCUser("username", "email@mail.com", "pass");

        DateTime current = new DateTime();
        Thread.sleep(25);

        user.updateLastLoginTime();

        assertTrue(user.getLastLogin().isAfter(current));
    }
    
    @Test
    public void testIsAnonymous() {
        JCUser user =  new JCUser("username", "email@mail.com", "pass");

        assertFalse(user.isAnonymous());
    }

    @Test
    public void copyUserShouldCopyCorrectlyIfOnlyRequiredFieldsFilled() {
        JCUser user =  new JCUser("username", "email@mail.com", "pass");
        JCUser copy = JCUser.copyUser(user);

        assertNotSame(copy, user);
        assertReflectionEquals(user, copy);
    }

    @Test
    public void copyUserShouldCopyCorrectlyIfAllFieldsFilled() {
        JCUser user =  new JCUser("username", "email@mail.com", "pass");
        user.setId(1);
        user.setFirstName("firstname");
        user.setLastName("lastname");
        user.setBanReason("spam");
        user.setRole("ROLE_USER");
        user.setAvatar(new byte[]{1, 2, 3});
        user.setVersion(1L);
        Group group = new Group("users");
        group.getUsers().add(user);
        user.setGroups(Arrays.asList(group));
        user.setSalt("salt");
        user.setPostCount(1);
        user.setLanguage(Language.ENGLISH);
        user.setPageSize(15);
        user.setLocation("world");
        user.setSignature("signature");
        user.setRegistrationDate(new DateTime());
        user.setEnabled(true);
        user.setAutosubscribe(true);
        user.setMentioningNotificationsEnabled(true);
        user.setSendPmNotification(true);
        UserContact contact = new UserContact("contact1", new UserContactType());
        contact.setOwner(user);
        user.setContacts(Sets.newHashSet(contact));
        user.setAvatarLastModificationTime(new DateTime());
        user.setAllForumMarkedAsReadTime(new DateTime());
        user.setUuid("uuid");

        JCUser copy = JCUser.copyUser(user);

        assertNotSame(copy, user);
        assertReflectionEquals(user, copy);
    }

    @Test
    public void copyUserContactShouldCopyCorrectlyIfOnlyRequiredFieldsFilled() {
        UserContactType contactType = new UserContactType();
        contactType.setDisplayPattern("*");
        UserContact contact = new UserContact("valuse", contactType);
        JCUser owner =  new JCUser("username", "email@mail.com", "pass");

        UserContact copy = JCUser.copyUserContact(contact, owner);

        assertNotSame(copy, contact);
        assertUserContactEqualsIgnoreOwner(contact, copy);
        assertSame(copy.getOwner(), owner);
    }

    @Test
    public void copyUserContactShouldCopyCorrectlyIfAllFieldsFilled() {
        UserContactType contactType = new UserContactType();
        contactType.setDisplayPattern("*");
        UserContact contact = new UserContact("valuse", contactType);
        JCUser oldOwner =  new JCUser("username", "email@mail.com", "pass");
        JCUser newOwner =  new JCUser("username", "email@mail.com", "pass");
        contact.setOwner(oldOwner);
        contact.setId(1);
        contact.setUuid("uuid");

        UserContact copy = JCUser.copyUserContact(contact, newOwner);

        assertNotSame(copy, contact);
        assertUserContactEqualsIgnoreOwner(contact, copy);
        assertSame(copy.getOwner(), newOwner);
    }

    @Test
    public void copyUserGroupShouldCopyCorrectlyIfOnlyRequiredFieldsFilled() {
        Group group = new Group("name");
        JCUser user =  new JCUser("username", "email@mail.com", "pass");
        Group copy = JCUser.copyUserGroup(group, user);

        assertNotSame(group, copy);
        assertUserGroupEqualsIgnoreUsers(copy, group);
        assertEquals(copy.getUsers().size(), 1);
        assertSame(copy.getUsers().get(0), user);
    }

    @Test
    public void copyUserGroupShouldCopyCorrectlyIfAllFieldsFilled() {
        Group group = new Group("name", "description");
        group.setId(1);
        group.setUuid("uuid");
        List<User> users = Arrays.asList(new User("username", "email@mail.com", "pass"),
                new User("username1", "email1@mail.com", "pass1"));
        group.setUsers(users);
        JCUser newUser =  new JCUser("username", "email@mail.com", "pass");

        Group copy = JCUser.copyUserGroup(group, newUser);

        assertNotSame(copy, group);
        assertUserGroupEqualsIgnoreUsers(copy, group);
        assertEquals(copy.getUsers().size(), 1);
        assertSame(copy.getUsers().get(0), newUser);
    }

    /**
     * Asserts {@link UserContact} ignore owner field. Needed because new owner can be set when copied.
     * @param actual actual contact
     * @param expected expected contact
     */
    private void assertUserContactEqualsIgnoreOwner(UserContact actual, UserContact expected) {
        assertEquals(actual.getId(), expected.getId());
        assertEquals(actual.getUuid(), expected.getUuid());
        assertEquals(actual.getDisplayValue(), expected.getDisplayValue());
        assertEquals(actual.getType(), expected.getType());
        assertEquals(actual.getValue(), expected.getValue());
    }

    /**
     * Asserts {@link Group} fields ignore users. Needed because group will contain only one user after coping.
     * @param actual actual group
     * @param expected expected group
     */
    private void assertUserGroupEqualsIgnoreUsers(Group actual, Group expected) {
        assertEquals(actual.getId(), expected.getId());
        assertEquals(actual.getUuid(), expected.getUuid());
        assertEquals(actual.getName(), expected.getName());
        assertEquals(actual.getDescription(), expected.getDescription());
    }



}
