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