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
package org.jtalks.jcommune.service.nontransactional;

import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

/**
 * Operations above user data cache.
 *
 * @author Kirill Afonin
 */
public class UserDataCacheService {

    private final Ehcache userDataCache;

    /**
     * Instantiate bean.
     *
     * @param userDataCache cache
     */
    public UserDataCacheService(Ehcache userDataCache) {
        this.userDataCache = userDataCache;
    }

    /**
     * Get new messages count for {@code username} in cache.
     *
     * @param username username
     * @return new messages count or {@code null} if user not in cache
     */
    public Integer getNewPmCountFor(String username) {
        Element cacheElementForUser = userDataCache.get(username);
        if (cacheElementForUser == null) {
            return null;
        }
        return (Integer) cacheElementForUser.getValue();
    }

    /**
     * Put new messages count for {@code username} to cache.
     *
     * @param username username
     * @param count    new messages count
     */
    public void putNewPmCount(String username, int count) {
        userDataCache.put(new Element(username, count));
    }

    /**
     * Increment new messages count for {@code username} in cache.
     *
     * @param username username
     */
    public void incrementNewMessageCountFor(String username) {
        Element cacheElementForUser = userDataCache.get(username);
        if (cacheElementForUser != null) {
            int count = (Integer) cacheElementForUser.getValue();
            userDataCache.put(new Element(username, ++count));
        }
    }

    /**
     * Decrement new messages count for {@code username} in cache.
     *
     * @param username username
     */
    public void decrementNewMessageCountFor(String username) {
        Element cacheElementForUser = userDataCache.get(username);
        if (cacheElementForUser != null) {
            int count = (Integer) cacheElementForUser.getValue();
            userDataCache.put(new Element(username, --count));
        }
    }
}
