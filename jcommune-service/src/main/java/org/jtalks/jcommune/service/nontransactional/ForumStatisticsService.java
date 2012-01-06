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

/**
 * This class contains methods for getting forum statistic information.
 *
 * @author Elena Lepaeva
 */
public class ForumStatisticsService  {

    private ForumStatisticsDAO statisticsDAO;

    /**
     * Create an instance of transactional forum statistics service
     *
     * @param statisticsDAO for operations with data storage
     */
    public ForumStatisticsService(ForumStatisticsDAO statisticsDAO) {
        this.statisticsDAO = statisticsDAO;
    }

    /**
     * Get total count of messages on the forum
     *
     * @return number of posts on the forum.
     */

    public int getPostsOnForumCount() {
        return statisticsDAO.getPostsOnForumCount();
    }

    /**
     * Return total count of registred user's accounts
     *
     * @return count of registred user's accounts
     */

    public int getUsersCount() {
        return statisticsDAO.getUsersCount();
    }
}
