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
package org.jtalks.jcommune.web.util;

import org.jtalks.jcommune.service.nontransactional.ForumStatisticsService;
import org.jtalks.jcommune.web.listeners.SessionStatisticListener;
import org.springframework.security.core.session.SessionRegistry;

import java.util.List;

/**
 * This class contains methods for getting and calculate forum statistic information.
 */
public class ForumStatisticsProvider {

    private SessionRegistry sessionRegistry;
    private SessionStatisticListener sessionStatisticListener;
    private ForumStatisticsService statisticsService;

    /**
     * Create an instance of transactional forum statistics provider
     *
     * @param sessionRegistry          for operations with data storage
     * @param sessionStatisticListener for getting active users count
     * @param statisticsService        for getting active users information
     */
    public ForumStatisticsProvider(SessionRegistry sessionRegistry,
                                   SessionStatisticListener sessionStatisticListener,
                                   ForumStatisticsService statisticsService) {
        this.sessionRegistry = sessionRegistry;
        this.sessionStatisticListener = sessionStatisticListener;
        this.statisticsService = statisticsService;
    }

    /**
     * Get total count of messages on the forum
     *
     * @return number of posts on the forum.
     */
    public int getPostsOnForumCount() {
        return statisticsService.getPostsOnForumCount();
    }


    /**
     * Return total count of registered user's accounts
     *
     * @return count of registered user's accounts
     */
    public int getUsersCount() {
        return statisticsService.getUsersCount();
    }

    /**
     * Return list of registered users who is online now
     *
     * @return list of users
     */
    public List<Object> getOnlineRegisteredUsers() {
        return sessionRegistry.getAllPrincipals();
    }

    /**
     * Return total number of online users
     *
     * @return total number of online users
     */
    public long getOnlineUsersCount() {
        return sessionStatisticListener.getTotalActiveSessions();
    }

    /**
     * Return number of online registered users
     *
     * @return number of users
     */
    public long getOnlineRegisteredUsersCount() {
        return sessionRegistry.getAllPrincipals().size();
    }

    /**
     * Return number of online anonymous users
     *
     * @return number of users
     */
    public long getOnlineAnonymousUsersCount() {
        return sessionStatisticListener.getTotalActiveSessions()
                - sessionRegistry.getAllPrincipals().size();
    }
}
