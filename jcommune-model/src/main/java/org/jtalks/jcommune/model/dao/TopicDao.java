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
import org.jtalks.common.model.dao.Crud;
import org.jtalks.common.model.entity.Branch;
import org.jtalks.jcommune.model.dto.PageRequest;
import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.model.entity.SubscriptionAwareEntity;
import org.jtalks.jcommune.model.entity.Topic;
import org.springframework.data.domain.Page;

import java.util.Collection;
import java.util.List;

/**
 * DAO for the {@link Topic} objects.
 * Besides the basic CRUD methods it provides a method to load any Topics with associated Posts.
 *
 * @author Pavel Vervenko
 * @author Kirill Afonin
 * @author Vitaliy Kravchenko
 * @author Eugeny Batov
 * @author Anuar Nurmakanov
 * @see org.jtalks.jcommune.model.dao.hibernate.TopicHibernateDao
 */
public interface TopicDao extends Crud<Topic> {


    /**
     * Get all topics past last 24 hour.
     *
     * @param lastLogin   user's last login date and time
     * @param pageRequest contains information for pagination: page number, page size
     * @param user        current user
     * @return object that contains topics for one page and information for pagination
     */
    Page<Topic> getTopicsUpdatedSince(DateTime lastLogin, PageRequest pageRequest, JCUser user);


    /**
     * Get unanswered topics(topics which has only 1 post added during topic creation).
     *
     * @param pageRequest contains information for pagination: page number, page size
     * @param user        current user
     * @return object that contains unanswered topics for one page and information
     *         for pagination
     */
    Page<Topic> getUnansweredTopics(PageRequest pageRequest, JCUser user);

    /**
     * Find the last updated topic in the branch.
     *
     * @param branch the branch, in which we try to find
     * @return the last updated topic in the branch
     */
    Topic getLastUpdatedTopicInBranch(Branch branch);

    /**
     * Get topics in the branch.
     *
     * @param branch      for this branch we will find topics
     * @param pageRequest contains information for pagination: page number, page size
     * @return object that contains topics for one page(note, that one page may contain
     *         all topics) and information for pagination
     */
    Page<Topic> getTopics(Branch branch, PageRequest pageRequest);

    /**
     * Get count of topics in the branch.
     *
     * @param branch the branch
     * @return count of topics in the branch
     */
    int countTopics(Branch branch);

    /**
     * Get subscribers for specified topic with allowed permission to read this topic.
     *
     * @param entity the topic
     * @return subscribers with allowed permission
     */
    Collection<JCUser> getAllowedSubscribers(SubscriptionAwareEntity entity);

    /**
     * Get forbidden branches id for permission VIEW_TOPICS only
     *
     * @param user Current user
     * @return
     */
    List<Long> getForbiddenBranchesIds(JCUser user);

    /**
     * Get allowed branches id for permission VIEW_TOPICS only
     *
     * @param user Current user
     * @return
     */
    List<Long> getAllowedBranchesIds(JCUser user);
}
