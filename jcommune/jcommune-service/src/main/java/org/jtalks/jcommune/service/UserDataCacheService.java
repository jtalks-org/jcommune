/**
 * Copyright (C) 2011  jtalks.org Team
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
 * Also add information on how to contact you by electronic and paper mail.
 * Creation date: Apr 12, 2011 / 8:05:19 PM
 * The jtalks.org Project
 */
package org.jtalks.jcommune.service;

/**
 * Contains operations above user data cache.
 *
 * @author Kirill Afonin
 */
public interface UserDataCacheService {
    /**
     * Get new messages count for {@code username} in cache.
     *
     * @param username username
     * @return new messages count or {@code null} if user not in cache
     */
    Integer getNewPmCountFor(String username);

    /**
     * Put new messages count for {@code username} to cache.
     *
     * @param username username
     * @param count    new messages count
     */
    void putNewPmCount(String username, int count);

    /**
     * Increment new messages count for {@code username} in cache.
     *
     * @param username username
     */
    void incrementNewMessageCountFor(String username);

    /**
     * Decrement new messages count for {@code username} in cache.
     *
     * @param username username
     */
    void decrementNewMessageCountFor(String username);
}
