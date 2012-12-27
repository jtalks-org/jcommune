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

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.web.authentication.rememberme.InvalidCookieException;

/**
 * Provides an ability to extract remember me data from cookie.
 * 
 * @author Anuar_Nurmanakov
 * 
 */
public class RememberMeCookieExtracter {
    private RememberMeCookieDecoder rememberMeCookieDecoder = new RememberMeCookieDecoderImpl();

    /**
     * Locates the Spring Security remember me cookie in the request and returns its value.
     * The cookie is searched for by name and also by matching the context path to the cookie path.
     *
     * @param request the submitted request which is to be authenticated
     * @return the cookie value (if present), null otherwise.
     */
    public String exctractRememberMeCookieValue(HttpServletRequest request) {
        return rememberMeCookieDecoder.extractRememberMeCookie(request);
    }
    
    /**
     * Extracts remember me data from cookie value.
     * 
     * @param cookieValue contains remember me data as series and token
     * @return extracted series and token
     */
    public String[] extractSeriesAndToken(String cookieValue) {
        RememberMeCookieDecoder rememberMeCookieDecoder = new RememberMeCookieDecoderImpl();
        try {
            return rememberMeCookieDecoder.decodeCookie(cookieValue);
        } catch (InvalidCookieException e) {
            return new String[] {};
        }
    }
}