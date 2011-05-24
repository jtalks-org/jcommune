package org.jtalks.jcommune.service.nontransactional;

import org.jtalks.jcommune.model.entity.User;
import org.jtalks.jcommune.service.SecurityContextFacade;
import org.jtalks.jcommune.service.SecurityService;
import org.jtalks.jcommune.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import sun.security.acl.PrincipalImpl;

import java.security.Principal;

import static org.mockito.Mockito.*;

/**
 * Test for {@link SecurityServiceImpl}.
 *
 * @author Kirill Afonin
 */
public class SecurityServiceImplTest {

    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";

    private UserService userService;
    private SecurityService securityService;
    private SecurityContextFacade securityContextFacade;
    private SecurityContext securityContext;

    private User getUser() {
        User user = new User();
        user.setUsername(USERNAME);
        user.setPassword(PASSWORD);
        return user;
    }

    @BeforeMethod
    public void setUp() throws Exception {
        userService = mock(UserService.class);
        securityContextFacade = mock(SecurityContextFacade.class);
        securityContext = mock(SecurityContext.class);
        securityService = new SecurityServiceImpl(userService, securityContextFacade);
        when(securityContextFacade.getContext()).thenReturn(securityContext);
    }

    @Test
    public void testGetCurrentUser() throws Exception {
        User user = getUser();
        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(user);
        when(securityContext.getAuthentication()).thenReturn(auth);
        when(userService.getByUsername(USERNAME)).thenReturn(user);

        User result = securityService.getCurrentUser();

        Assert.assertEquals(result.getUsername(), USERNAME, "Username not equals");
        verify(userService, times(1)).getByUsername(USERNAME);
        verify(auth, times(1)).getPrincipal();
        verify(securityContext, times(1)).getAuthentication();
    }

    @Test
    public void testGetCurrentUserNotFound() throws Exception {
        when(securityContext.getAuthentication()).thenReturn(null);

        User result = securityService.getCurrentUser();

        Assert.assertNull(result, "User not null");
        verify(securityContext, times(1)).getAuthentication();
        verify(userService, never()).getByUsername(USERNAME);
    }

    @Test
    public void testGetCurrentUserUsername() throws Exception {
        User user = getUser();
        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(user);
        when(securityContext.getAuthentication()).thenReturn(auth);

        String username = securityService.getCurrentUserUsername();

        Assert.assertEquals(username, USERNAME, "Username not equals");
        verify(auth, times(1)).getPrincipal();
        verify(securityContext, times(1)).getAuthentication();
    }

    @Test
    public void testGetCurrentUserUsernamePrincipal() throws Exception {
        Principal user = new PrincipalImpl(USERNAME);
        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(user);
        when(securityContext.getAuthentication()).thenReturn(auth);

        String username = securityService.getCurrentUserUsername();

        Assert.assertEquals(username, USERNAME, "Username not equals");
        verify(auth, times(1)).getPrincipal();
        verify(securityContext, times(1)).getAuthentication();
    }

    @Test
    public void testGetCurrentUserUsernameNotAuthenticated() throws Exception {
        when(securityContext.getAuthentication()).thenReturn(null);

        String username = securityService.getCurrentUserUsername();

        Assert.assertNull(username, "Username not null");
        verify(securityContext, times(1)).getAuthentication();
    }

    @Test
    public void testLoadUserByUsername() throws Exception {
        User user = getUser();

        when(userService.getByUsername(USERNAME)).thenReturn(user);

        UserDetails result = securityService.loadUserByUsername(USERNAME);

        Assert.assertEquals(result.getUsername(), USERNAME, "Username not equals");
        verify(userService, times(1)).getByUsername(USERNAME);
    }

    @Test(expectedExceptions = UsernameNotFoundException.class)
    public void testLoadUserByUsername_NotFound() throws Exception {
        when(userService.getByUsername(USERNAME)).thenThrow(new UsernameNotFoundException(""));

        securityService.loadUserByUsername(USERNAME);
    }
}
