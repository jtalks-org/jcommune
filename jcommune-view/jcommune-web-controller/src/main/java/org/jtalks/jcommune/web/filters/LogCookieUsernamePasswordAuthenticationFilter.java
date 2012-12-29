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

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.jtalks.jcommune.web.rememberme.RememberMeCookieExtractor;
import org.jtalks.jcommune.web.rememberme.RememberMeCheckService;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Authentication filter that also logs user's cookies that contain 
 * remember me data.
 * 
 * @author Anuar_Nurmakanov
 *
 */
public class LogCookieUsernamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private RememberMeCookieExtractor extractor;
    private RememberMeCheckService rememberMeCheckService;
    
    /**
     * Default constructor.
     */
    protected LogCookieUsernamePasswordAuthenticationFilter() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) 
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        String rememberMeCookieValue = extractor.exctractRememberMeCookieValue(request);
        if (rememberMeCookieValue != null) {
            String[] seriesAndToken = extractor.extractSeriesAndToken(rememberMeCookieValue);
            String series = seriesAndToken[0];
            String token = seriesAndToken[1];
            rememberMeCheckService.checkWithPersistentRememberMeToken(series, token);
        }
        super.doFilter(req, res, chain);
    }

    /**
     * Set extractor of remember me data from cookie.
     * 
     * @param extractor extractor of remember me data from cookie
     */
    public void setExtractor(RememberMeCookieExtractor extractor) {
        this.extractor = extractor;
    }

    /**
     * Set remember me check service.
     * 
     * @param rememberMeCheckService remember me check service
     */
    public void setRememberMeCheckService(RememberMeCheckService rememberMeCheckService) {
        this.rememberMeCheckService = rememberMeCheckService;
    }
}
