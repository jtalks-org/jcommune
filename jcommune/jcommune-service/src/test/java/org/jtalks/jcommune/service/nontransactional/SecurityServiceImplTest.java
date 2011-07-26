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
package org.jtalks.jcommune.service.nontransactional;

import org.jtalks.jcommune.model.dao.UserDao;
import org.jtalks.jcommune.model.entity.Post;
import org.jtalks.jcommune.model.entity.User;
import org.jtalks.jcommune.service.SecurityContextFacade;
import org.jtalks.jcommune.service.SecurityService;
import org.springframework.security.acls.domain.AclAuthorizationStrategy;
import org.springframework.security.acls.domain.AclImpl;
import org.springframework.security.acls.domain.AuditLogger;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.domain.GrantedAuthoritySid;
import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.model.MutableAcl;
import org.springframework.security.acls.model.MutableAclService;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.security.acls.model.ObjectIdentity;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.acls.model.Sid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import sun.security.acl.PrincipalImpl;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

/**
 * Test for {@link SecurityServiceImpl}.
 *
 * @author Kirill Afonin
 */
public class SecurityServiceImplTest {

    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";

    private UserDao userDao;
    private SecurityService securityService;
    private SecurityContextFacade securityContextFacade;
    private SecurityContext securityContext;
    private MutableAclService mutableAclService;

    private User getUser() {
        User user = new User();
        user.setUsername(USERNAME);
        user.setPassword(PASSWORD);
        return user;
    }

    @BeforeMethod
    public void setUp() throws Exception {
        userDao = mock(UserDao.class);
        securityContextFacade = mock(SecurityContextFacade.class);
        securityContext = mock(SecurityContext.class);
        mutableAclService = mock(MutableAclService.class);
        securityService = new SecurityServiceImpl(userDao, securityContextFacade, mutableAclService);
        when(securityContextFacade.getContext()).thenReturn(securityContext);
    }

    @Test
    public void testGetCurrentUser() throws Exception {
        User user = getUser();
        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(user);
        when(securityContext.getAuthentication()).thenReturn(auth);
        when(userDao.getByUsername(USERNAME)).thenReturn(user);

        User result = securityService.getCurrentUser();

        assertEquals(result.getUsername(), USERNAME, "Username not equals");
        assertEquals(result.getAuthorities().iterator().next().getAuthority(), "ROLE_USER");
        assertTrue(result.isAccountNonExpired());
        assertTrue(result.isAccountNonLocked());
        assertTrue(result.isEnabled());
        assertTrue(result.isCredentialsNonExpired());
        verify(userDao).getByUsername(USERNAME);
        verify(auth).getPrincipal();
        verify(securityContext).getAuthentication();
    }

    @Test
    public void testGetCurrentUserNotFound() throws Exception {
        when(securityContext.getAuthentication()).thenReturn(null);

        User result = securityService.getCurrentUser();

        assertNull(result, "User not null");
        verify(securityContext).getAuthentication();
        verify(userDao, never()).getByUsername(USERNAME);
    }

    @Test
    public void testGetCurrentUserUsername() throws Exception {
        User user = getUser();
        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(user);
        when(securityContext.getAuthentication()).thenReturn(auth);

        String username = securityService.getCurrentUserUsername();

        assertEquals(username, USERNAME, "Username not equals");
        verify(auth).getPrincipal();
        verify(securityContext).getAuthentication();
    }

    @Test
    public void testGetCurrentUserUsernamePrincipal() throws Exception {
        Principal user = new PrincipalImpl(USERNAME);
        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(user);
        when(securityContext.getAuthentication()).thenReturn(auth);

        String username = securityService.getCurrentUserUsername();

        assertEquals(username, USERNAME, "Username not equals");
        verify(auth).getPrincipal();
        verify(securityContext).getAuthentication();
    }

    @Test
    public void testGetCurrentUserUsernameWithoutAuthentication() throws Exception {
        when(securityContext.getAuthentication()).thenReturn(null);

        String username = securityService.getCurrentUserUsername();

        assertNull(username, "Username not null");
        verify(securityContext).getAuthentication();
    }

    @Test
    public void testGetCurrentUserUsernameAnonymousUser() throws Exception {
        Principal user = new PrincipalImpl(SecurityServiceImpl.ANONYMOUS_USER);
        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(user);
        when(securityContext.getAuthentication()).thenReturn(auth);

        String username = securityService.getCurrentUserUsername();

        assertNull(username, "Username not null");
        verify(auth).getPrincipal();
        verify(securityContext).getAuthentication();
    }

    @Test
    public void testLoadUserByUsername() throws Exception {
        User user = getUser();

        when(userDao.getByUsername(USERNAME)).thenReturn(user);

        UserDetails result = securityService.loadUserByUsername(USERNAME);

        assertEquals(result.getUsername(), USERNAME, "Username not equals");
        verify(userDao).getByUsername(USERNAME);
    }

    @Test(expectedExceptions = UsernameNotFoundException.class)
    public void testLoadUserByUsernameNotFound() throws Exception {
        when(userDao.getByUsername(USERNAME)).thenReturn(null);

        securityService.loadUserByUsername(USERNAME);
    }

    @Test
    public void testGrantAdminPermissionsToCurrentUserAndAdmins() throws Exception {
        mockCurrentUserPrincipal();
        Post object = Post.createNewPost();
        object.setId(1L);
        ObjectIdentity objectIdentity = new ObjectIdentityImpl(Post.class.getCanonicalName(), object.getId());
        MutableAcl objectAcl = new AclImpl(objectIdentity, 2L, mock(AclAuthorizationStrategy.class),
                mock(AuditLogger.class));
        when(mutableAclService.readAclById(objectIdentity))
                .thenThrow(new NotFoundException(""))
                .thenReturn(objectAcl);
        when(mutableAclService.createAcl(objectIdentity)).thenReturn(objectAcl);

        securityService.grantAdminPermissionToCurrentUserAndAdmins(object);

        assertGranted(objectAcl, new PrincipalSid(USERNAME),
                BasePermission.ADMINISTRATION, "Permission to current user not granted");
        assertGranted(objectAcl, new GrantedAuthoritySid("ROLE_ADMIN"),
                BasePermission.ADMINISTRATION, "Permission to ROLE_ADMIN not granted");
        verify(mutableAclService, times(2)).readAclById(objectIdentity);
        verify(mutableAclService).createAcl(objectIdentity);
        verify(mutableAclService, times(2)).updateAcl(objectAcl);
    }

    @Test(expectedExceptions = {IllegalStateException.class})
    public void testGrantAdminPermissionsToCurrentUserAndAdminsWithZeroId() {
        mockCurrentUserPrincipal();
        Post object = Post.createNewPost();

        securityService.grantAdminPermissionToCurrentUserAndAdmins(object);
    }


    @Test
    public void testDeleteFromAcl() throws Exception {
        Post object = Post.createNewPost();
        object.setId(1L);
        ObjectIdentity objectIdentity = new ObjectIdentityImpl(Post.class.getCanonicalName(), object.getId());

        securityService.deleteFromAcl(object);

        verify(mutableAclService).deleteAcl(objectIdentity, true);
    }

    @Test(expectedExceptions = {IllegalStateException.class})
    public void testDeleteFromAclWithZeroId() throws Exception {
        Post object = Post.createNewPost();

        securityService.deleteFromAcl(object);
    }

    @Test
    public void testDeletePermission() {
        mockCurrentUserPrincipal();
        Post object = Post.createNewPost();
        object.setId(1L);
        ObjectIdentity objectIdentity = new ObjectIdentityImpl(Post.class.getCanonicalName(), object.getId());
        MutableAcl objectAcl = new AclImpl(objectIdentity, 2L, mock(AclAuthorizationStrategy.class),
                mock(AuditLogger.class));
        objectAcl.insertAce(objectAcl.getEntries().size(), BasePermission.ADMINISTRATION,
                new PrincipalSid(USERNAME), true);
        objectAcl.insertAce(objectAcl.getEntries().size(), BasePermission.READ,
                new PrincipalSid(USERNAME), true);
        when(mutableAclService.readAclById(objectIdentity)).thenReturn(objectAcl);


        securityService.deletePermission(object, new PrincipalSid(USERNAME),
                BasePermission.ADMINISTRATION);

        assertNotGranted(objectAcl, new PrincipalSid(USERNAME),
                BasePermission.ADMINISTRATION, "Permission from current user wasn't taken away");
        verify(mutableAclService, times(1)).readAclById(objectIdentity);
        verify(mutableAclService, times(1)).updateAcl(objectAcl);
    }

    private void assertNotGranted(MutableAcl acl, Sid sid, Permission permission, String message) {
        List<Permission> expectedPermission = new ArrayList<Permission>();
        expectedPermission.add(permission);
        List<Sid> expectedSid = new ArrayList<Sid>();
        expectedSid.add(sid);
        try {
            acl.isGranted(expectedPermission, expectedSid, true);
            fail(message);
        } catch (NotFoundException e) {
        }
    }


    private void assertGranted(MutableAcl acl, Sid sid, Permission permission, String message) {
        List<Permission> expectedPermission = new ArrayList<Permission>();
        expectedPermission.add(permission);
        List<Sid> expectedSid = new ArrayList<Sid>();
        expectedSid.add(sid);
        try {
            assertTrue(acl.isGranted(expectedPermission, expectedSid, true), message);
        } catch (NotFoundException e) {
            fail(message);
        }
    }

    private void mockCurrentUserPrincipal() {
        Principal user = new PrincipalImpl(USERNAME);
        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(user);
        when(securityContext.getAuthentication()).thenReturn(auth);
    }

}
