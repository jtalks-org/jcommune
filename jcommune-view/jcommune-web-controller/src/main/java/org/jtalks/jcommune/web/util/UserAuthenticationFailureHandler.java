package org.jtalks.jcommune.web.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * <tt>AuthenticationFailureHandler</tt> which performs a redirect back to the login page
 * @author Andrei Alikov
 */
public class UserAuthenticationFailureHandler implements AuthenticationFailureHandler {
    private final Log logger = LogFactory.getLog(getClass());
    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
    private static final String FAILURE_REDIRECT_URL = "/login?login_error=1&username=";

    /**
     * {@inheritDoc}
     */
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        request.getSession().setAttribute(WebAttributes.AUTHENTICATION_EXCEPTION, exception);

        String fullUrl = FAILURE_REDIRECT_URL + exception.getAuthentication().getName();

        logger.debug("Redirecting to " + fullUrl);
        redirectStrategy.sendRedirect(request, response, fullUrl);
    }

    /**
     * Allows overriding of the behaviour when redirecting to a target URL.
     * @param redirectStrategy new behaviour when redirecting to a target URL.
     */
    public void setRedirectStrategy(RedirectStrategy redirectStrategy) {
        this.redirectStrategy = redirectStrategy;
    }

    /**
     * Gets current behaviour when redirecting to a target URL.
     * @return current behaviour when redirecting to a target URL.
     */
    public RedirectStrategy getRedirectStrategy() {
        return redirectStrategy;
    }

}
