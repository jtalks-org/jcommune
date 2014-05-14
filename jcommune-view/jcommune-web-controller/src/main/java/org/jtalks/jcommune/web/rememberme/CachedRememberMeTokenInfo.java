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
 * Tokens used for linking information about user with information which stored in user's remember-me cookies.
 * All this information stores in database. Token value updates every time when be accessed.
 * We need this class to store "remember-me" token value and caching time in cache.
 * @see PersistentRememberMeToken
 * @see ThrottlingRememberMeService
 *
 * @author  Mikhail Stryzhonok
 */
public class CachedRememberMeTokenInfo {

    private String value;
    long cachingTime;

    public CachedRememberMeTokenInfo(String tokenValue, long cachingTime) {
        this.value = tokenValue;
        this.cachingTime = cachingTime;
    }

    /**
     * Gets token date and time of token caching as milliseconds
     * @return Date and time of token caching
     */
    public long getCachingTime() {
        return cachingTime;
    }

    public String getValue() {
        return value;
    }
}
