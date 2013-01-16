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

/**
 * Provides an ability to extract, decode values from cookies that holds "remember me" data. The interface was
 * introduced because implementation extends Spring Security classes and we don't want to inject object with dozens of
 * not-actually-needed methods, that's why we separated out only needed methods and moved them into this interface.
 * 
 * @author Anuar_Nurmakanov
 *
 */
public interface RememberMeCookieDecoder {

    /**
     * Locates the Spring Security remember me cookie in the request and returns its value.
     * The cookie is searched for by name and also by matching the context path to the cookie path.
     *
     * @param request the submitted request which is to be authenticated
     * @return the cookie value (if present), null otherwise.
     */
    String exctractRememberMeCookieValue(HttpServletRequest request);
    
    /**
     * Extracts remember me data from cookie value.
     * 
     * @param cookieValue contains remember me data as series and token
     * @return extracted series(0 index in returned array) and token (1 index in returned array)
     */
    String[] extractSeriesAndToken(String cookieValue);
}