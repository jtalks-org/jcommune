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

import org.jtalks.jcommune.model.dao.ForumStatisticsDAO;
import org.jtalks.jcommune.service.ForumStatisticsService;
import org.jtalks.jcommune.service.listeners.HttpSessionStatisticListener;
import org.springframework.security.core.session.SessionRegistry;

import java.util.List;

/**
 * This class contains methods for getting forum statistic information.
 *
 * @author Elena Lepaeva
 */
public class ForumStatisticsServiceImpl implements ForumStatisticsService {

    private ForumStatisticsDAO statisticsDAO;
    private SessionRegistry sessionRegistry;
    private HttpSessionStatisticListener sessionStatisticListener;

    /**
     * Create an instance of transactional forum statistics service
     *
     * @param statisticsDAO            for operations with data storage
     * @param sessionStatisticListener for getting active users count
     * @param sessionRegistry          for getting active users information
     */
    public ForumStatisticsServiceImpl(ForumStatisticsDAO statisticsDAO,
                                      HttpSessionStatisticListener sessionStatisticListener,
                                      SessionRegistry sessionRegistry) {
        this.statisticsDAO = statisticsDAO;
        this.sessionStatisticListener = sessionStatisticListener;
        this.sessionRegistry = sessionRegistry;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getPostsOnForumCount() {
        return statisticsDAO.getPostsOnForumCount();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getUsersCount() {
        return statisticsDAO.getUsersCount();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Object> getOnlineRegisteredUsers() {
        return sessionRegistry.getAllPrincipals();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getOnlineUsersCount() {
        return sessionStatisticListener.getTotalActiveSessions();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getOnlineRegisteredUsersCount() {
        return sessionRegistry.getAllPrincipals().size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getOnlineAnonymoustUsersCount() {
        return sessionStatisticListener.getTotalActiveSessions()
                - sessionRegistry.getAllPrincipals().size();
    }
}
