package org.jtalks.jcommune.service.nontransactional;

import org.jtalks.jcommune.model.dao.UserDao;
import org.jtalks.jcommune.model.entity.Post;
import org.jtalks.jcommune.model.entity.User;
import org.jtalks.jcommune.service.SecurityContextFacade;
import org.jtalks.jcommune.service.SecurityService;
import org.springframework.security.acls.domain.*;
import org.springframework.security.acls.model.*;
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

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

/**
 * Test for {@link SecurityServiceImpl}.
 *
 * @author Kirill Afonin
 * @author Max Malakhov
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
    public void testGetCurrentUserUsernameNotAuthenticated() throws Exception {
        when(securityContext.getAuthentication()).thenReturn(null);

        String username = securityService.getCurrentUserUsername();

        assertNull(username, "Username not null");
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

    @Test
    public void testGrantAdminPermissionsToCurrentUser() throws Exception {
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

        securityService.grantAdminPermissionToCurrentUser(object);

        assertGranted(objectAcl, new PrincipalSid(USERNAME),
                BasePermission.ADMINISTRATION, "Permission to current user not granted");
        verify(mutableAclService).readAclById(objectIdentity);
        verify(mutableAclService).createAcl(objectIdentity);
        verify(mutableAclService).updateAcl(objectAcl);
    }

    @Test
    public void testGrantReadPermissionsToCurrentUser() throws Exception {
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

        securityService.grantReadPermissionToCurrentUser(object);

        assertGranted(objectAcl, new PrincipalSid(USERNAME),
                BasePermission.READ, "Permission to current user not granted");
        verify(mutableAclService).readAclById(objectIdentity);
        verify(mutableAclService).createAcl(objectIdentity);
        verify(mutableAclService).updateAcl(objectAcl);
    }

    @Test
    public void testGrantReadPermissionsToUser() throws Exception {
        Post object = Post.createNewPost();
        object.setId(1L);
        ObjectIdentity objectIdentity = new ObjectIdentityImpl(Post.class.getCanonicalName(), object.getId());
        MutableAcl objectAcl = new AclImpl(objectIdentity, 2L, mock(AclAuthorizationStrategy.class),
                mock(AuditLogger.class));
        when(mutableAclService.readAclById(objectIdentity))
                .thenThrow(new NotFoundException(""))
                .thenReturn(objectAcl);
        when(mutableAclService.createAcl(objectIdentity)).thenReturn(objectAcl);

        securityService.grantReadPermissionToUser(object, USERNAME);

        assertGranted(objectAcl, new PrincipalSid(USERNAME),
                BasePermission.READ, "Permission to current user not granted");
        verify(mutableAclService).readAclById(objectIdentity);
        verify(mutableAclService).createAcl(objectIdentity);
        verify(mutableAclService).updateAcl(objectAcl);
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
