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

import javax.servlet.http.HttpServletRequest;

import org.jtalks.jcommune.web.rememberme.RememberMeCheckService;
import org.jtalks.jcommune.web.rememberme.RememberMeCookieDecoder;

/**
 * Provides an ability to check series and token from cookie and series and
 * token from database. We need this functionality to find more information
 * about cases when user cant' login in application by using "remember me"
 * service.
 * 
 * @author Anuar_Nurmakanov
 *
 */
public class CheckRememberMeDataFilterPreHandler implements FilterPreHandler {
    
    private RememberMeCookieDecoder rememberMeCookieDecoder;
    private RememberMeCheckService rememberMeCheckService;
    
    /**
     * Constructs an instance with required fields.
     * 
     * @param rememberMeCookieDecoder decoder of remember me data from cookie
     * @param rememberMeCheckService remember me check service
     */ 
    public CheckRememberMeDataFilterPreHandler(
            RememberMeCookieDecoder rememberMeCookieDecoder,
            RememberMeCheckService rememberMeCheckService) {
        this.rememberMeCookieDecoder = rememberMeCookieDecoder;
        this.rememberMeCheckService = rememberMeCheckService;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void handle(HttpServletRequest request) {
        String rememberMeCookieValue = rememberMeCookieDecoder.exctractRememberMeCookieValue(request);
        if (rememberMeCookieValue != null) {
            String[] seriesAndToken = rememberMeCookieDecoder.extractSeriesAndToken(rememberMeCookieValue);
            String series = seriesAndToken[0];
            String token = seriesAndToken[1];
            rememberMeCheckService.equalWithPersistentToken(series, token);
        }
    }
}
