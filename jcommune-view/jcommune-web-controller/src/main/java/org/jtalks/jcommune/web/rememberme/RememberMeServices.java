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
package org.jtalks.jcommune.web.rememberme;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Implements our custom Remember Me service to replace the Spring default one. This implementation removes Remember Me
 * token only for a single session.
 * <p><b>Justification:</b> Spring's * {@link PersistentTokenBasedRememberMeServices} removes all the tokens from DB
 * for a user whose session expired - even the sessions started on a different machine or device. Thus users were
 * frustrated when their sessions expired on the machines where the Remember Me checkbox was checked.
 * </p>
 */
public class RememberMeServices extends PersistentTokenBasedRememberMeServices {
    private final static String REMOVE_TOKEN_QUERY = "DELETE FROM persistent_logins WHERE series = ? AND token = ?";
    private final RememberMeCookieDecoder rememberMeCookieDecoder;
    private final JdbcTemplate jdbcTemplate;

    /**
     * @param rememberMeCookieDecoder needed for extracting rememberme cookies
     * @param jdbcTemplate            needed to execute the sql queries
     * @throws Exception - see why {@link PersistentTokenBasedRememberMeServices} throws it
     */
    public RememberMeServices(RememberMeCookieDecoder rememberMeCookieDecoder, JdbcTemplate jdbcTemplate)
            throws Exception {
        super();
        this.rememberMeCookieDecoder = rememberMeCookieDecoder;
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Causes a logout to be completed. The method must complete successfully.
     * Removes client's token which is extracted from the HTTP request.
     * {@inheritDoc}
     */
    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        String cookie = rememberMeCookieDecoder.exctractRememberMeCookieValue(request);
        if (cookie != null) {
            String[] seriesAndToken = rememberMeCookieDecoder.extractSeriesAndToken(cookie);
            if (logger.isDebugEnabled()) {
                logger.debug("Logout of user " + (authentication == null ? "Unknown" : authentication.getName()));
            }
            cancelCookie(request, response);
            jdbcTemplate.update(REMOVE_TOKEN_QUERY, seriesAndToken);
        }
    }
}