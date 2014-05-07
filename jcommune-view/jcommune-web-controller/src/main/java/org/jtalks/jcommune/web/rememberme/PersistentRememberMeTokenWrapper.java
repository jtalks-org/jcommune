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

import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;

/**
 * We need this class to store token and caching time in cache
 *
 * @author  Mikhail Stryzhonok
 */
public class PersistentRememberMeTokenWrapper {

    private PersistentRememberMeToken token;
    long cachingTime;

    public PersistentRememberMeTokenWrapper(PersistentRememberMeToken token, long cachingTime) {
        this.token = token;
        this.cachingTime = cachingTime;
    }

    public PersistentRememberMeToken getToken() {
        return token;
    }

    public void setToken(PersistentRememberMeToken token) {
        this.token = token;
    }

    public long getCachingTime() {
        return cachingTime;
    }

    public void setCachingTime(long cachingTime) {
        this.cachingTime = cachingTime;
    }

    public String getTokenSeries() {
        return token.getSeries();
    }

    public String getTokenValue() {
        return token.getTokenValue();
    }
}
