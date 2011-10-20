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
package org.jtalks.jcommune.model.dao;

import org.joda.time.DateTime;
import org.jtalks.jcommune.model.entity.Topic;

import java.util.List;

/**
 * DAO for the {@link Topic} objects.
 * Besides the basic CRUD methods it provides a method to load any Topics with associated Posts.
 *
 * @author Pavel Vervenko
 * @author Kirill Afonin
 * @author Vitaliy Kravchenko
 * @see org.jtalks.jcommune.model.dao.hibernate.TopicHibernateDao
 */
public interface TopicDao extends ChildRepository<Topic> {

    /**
     * Get posts range from branch.
     *
     * @param branchId branch id from which we obtain topics
     * @param start    start index of topic
     * @param max      number of topics
     * @return list of {@code Topic} objects with size {@code max}
     */
    List<Topic> getTopicRangeInBranch(Long branchId, int start, int max);

    /**
     * Get number of topics in branch.
     *
     * @param branchId branch id where you have to count topics
     * @return number of topics in branch
     */
    int getTopicsInBranchCount(long branchId);

    /**
     * Get count of topics past last 24 hour.
     *
     * @param lastLogin user's last login date and time
     * @return count of topics
     */
    int getTopicsPastLastDayCount(DateTime lastLogin);

    /**
     * Get all topics past last 24 hour.
     *
     * @param start start index of topic
     * @param max   number of topics
     * @param lastLogin user's last login date and time
     * @return list of topics
     */
    List<Topic> getAllTopicsPastLastDay(int start, int max, DateTime lastLogin);
}
