package org.jtalks.jcommune.web.rememberme;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

    /**
     * {@inheritDoc}
     */
    public class RememberMeServices extends PersistentTokenBasedRememberMeServices {

    private RememberMeCookieDecoder rememberMeCookieDecoder;
    private JdbcTemplate jdbcTemplate;

    public RememberMeServices() throws Exception {
        super();
    }

    public void setRememberMeCookieDecoder(RememberMeCookieDecoder rememberMeCookieDecoder) {
        this.rememberMeCookieDecoder = rememberMeCookieDecoder;
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        String cookie = rememberMeCookieDecoder.exctractRememberMeCookieValue(request);
        String[] seriesAndToken = rememberMeCookieDecoder.extractSeriesAndToken(cookie);
        if (logger.isDebugEnabled()) {
            logger.debug( "Logout of user " + (authentication == null ? "Unknown" : authentication.getName()));
        }
        cancelCookie(request, response);
        jdbcTemplate.update("delete from persistent_logins where series = ? and token = ?", seriesAndToken);
    }
}