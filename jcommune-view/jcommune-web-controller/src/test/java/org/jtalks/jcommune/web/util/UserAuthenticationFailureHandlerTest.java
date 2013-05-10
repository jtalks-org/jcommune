package org.jtalks.jcommune.web.util;

import org.jtalks.jcommune.model.entity.JCUser;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.RedirectStrategy;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.testng.Assert.assertEquals;

/**
 * @author Andrei Alikov
 */
public class UserAuthenticationFailureHandlerTest {
    private UserAuthenticationFailureHandler handler;

    @BeforeMethod
    public void setUp() throws Exception {
        handler = new UserAuthenticationFailureHandler();
    }

    @Test
    public void testOnAuthenticationFailureShouldRedirectToLoginPage() throws Exception {
        JCUser user = new JCUser("username", "email", "password");
        Authentication auth = new TestingAuthenticationToken(user, null);

        AuthenticationException exception = new BadCredentialsException("Password doesn't match!");
        exception.setAuthentication(auth);

        RedirectStrategy redirectStrategy = mock(RedirectStrategy.class);
        handler.setRedirectStrategy(redirectStrategy);
        handler.setDefaultFailureUrl("/badlogin?login_error=1");
        handler.setUsernameSessionAttribute("j_user_name");

        HttpServletRequest request = new MockHttpServletRequest();
        HttpServletResponse response = new MockHttpServletResponse();
        handler.onAuthenticationFailure(request, response, exception);

        verify(redirectStrategy).sendRedirect(request, response, "/badlogin?login_error=1");
        assertEquals(request.getSession().getAttribute(handler.getUsernameSessionAttribute()), "username");
    }
}
