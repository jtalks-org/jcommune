package org.jtalks.jcommune.service.nontransactional;

import org.jtalks.jcommune.model.dao.UserDao;
import org.jtalks.jcommune.model.entity.Post;
import org.jtalks.jcommune.model.entity.User;
import org.jtalks.jcommune.service.SecurityService;
import org.jtalks.jcommune.service.security.AclBuilder;
import org.jtalks.jcommune.service.security.AclManager;
import org.jtalks.jcommune.service.security.SecurityContextFacade;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import sun.security.acl.PrincipalImpl;

import java.security.Principal;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

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
    private AclManager aclManager;

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
        aclManager = mock(AclManager.class);
        securityService = new SecurityServiceImpl(userDao, securityContextFacade, aclManager);
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
        Principal user = new PrincipalImpl(SecurityConstants.ANONYMOUS_USERNAME);
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
    public void testDeleteFromAcl() throws Exception {
        Post object = Post.createNewPost();
        object.setId(1L);

        securityService.deleteFromAcl(object);

        verify(aclManager).deleteFromAcl(Post.class, 1L);
    }

    @Test
    public void testDeleteFromAclByClassAndId() throws Exception {
        securityService.deleteFromAcl(Post.class, 1L);

        verify(aclManager).deleteFromAcl(Post.class, 1L);
    }

    @Test
    public void testGrantToCurrentUser() throws Exception {
        mockCurrentUserPrincipal();

        AclBuilder builder = securityService.grantToCurrentUser();

        assertTrue(builder.containsSid(USERNAME));
    }

    private void mockCurrentUserPrincipal() {
        Principal user = new PrincipalImpl(USERNAME);
        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(user);
        when(securityContext.getAuthentication()).thenReturn(auth);
    }

}
