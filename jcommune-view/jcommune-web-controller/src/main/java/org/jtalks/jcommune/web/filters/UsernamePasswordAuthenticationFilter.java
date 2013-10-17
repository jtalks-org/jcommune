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
package org.jtalks.jcommune.web.filters;

import org.jtalks.jcommune.web.rememberme.RememberMeCheckService;
import org.jtalks.jcommune.web.rememberme.RememberMeCookieDecoder;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * This filter performs the same operations as {@link UsernamePasswordAuthenticationFilter}
 * but usage of this filter provides an ability to check passed in request "remember me" token
 * with token in database. We log a detailed message about what were the persistent tokens if they don't match with
 * the ones from user's cookies so that we can investigate this problem in details (that means that cookies were
 * stolen).
 *
 * @author Anuar_Nurmakanov
 */
public class UsernamePasswordAuthenticationFilter
        extends org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter {

    private final RememberMeCookieDecoder rememberMeCookieDecoder;
    private final RememberMeCheckService rememberMeCheckService;

    /**
     * Constructs an instance with required fields.
     *
     * @param rememberMeCookieDecoder decoder of remember me data from cookie
     * @param rememberMeCheckService  remember me check service
     */
    public UsernamePasswordAuthenticationFilter(
            RememberMeCookieDecoder rememberMeCookieDecoder,
            RememberMeCheckService rememberMeCheckService) {
        this.rememberMeCookieDecoder = rememberMeCookieDecoder;
        this.rememberMeCheckService = rememberMeCheckService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        extractAndCheckRememberMeToken(request);
        super.doFilter(req, res, chain);
    }

    /**
     * Extract "remember me" token from request and if it exists check it with
     * token from database.
     *
     * @param request incoming http request
     */
    private void extractAndCheckRememberMeToken(HttpServletRequest request) {
        String rememberMeCookieValue = rememberMeCookieDecoder.exctractRememberMeCookieValue(request);
        if (rememberMeCookieValue != null) {
            String[] seriesAndToken = rememberMeCookieDecoder.extractSeriesAndToken(rememberMeCookieValue);
            int seriesAndTokenMinLength = 2;
            if (seriesAndToken.length >= seriesAndTokenMinLength) {
                String series = seriesAndToken[0];
                String token = seriesAndToken[1];
                rememberMeCheckService.equalWithPersistentToken(series, token);
            }
        }
    }
}
