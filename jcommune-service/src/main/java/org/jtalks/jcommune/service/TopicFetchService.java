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
package org.jtalks.jcommune.service;

import org.jtalks.jcommune.model.entity.Branch;
import org.jtalks.jcommune.model.entity.Topic;
import org.jtalks.jcommune.plugin.api.exceptions.NotFoundException;
import org.springframework.data.domain.Page;

/**
 * Serves all kinds of fetch topic requests
 */
public interface TopicFetchService extends EntityService<Topic> {

    /**
     * Gets topic with specified id without increasing views counter
     *
     * @param id id of interested topic
     *
     * @return topic with specified id
     *
     * @throws NotFoundException if topic with specified id not found
     */
    Topic getTopicSilently(Long id) throws NotFoundException;

    /**
     * Get topics in the branch.
     *
     * @param branch        for this branch we will find topics
     * @param page          page number, for which we will find topics.
     *                      Page number provided as user input string.
     *                      The final validation of provided input will be at DAO level.
     *                      That allow as to exclude additional DAO call,
     *                      as for input validation we need the total count of page elements.
     * @return object that contains topics for one page(note, that one page may contain
     *         all topics) and information for pagination
     */
    Page<Topic> getTopics(Branch branch, String page);

    /**
     * Get topics that have been updated in the last 24 hours.
     *
     * @param page page page number, for which we will find topics
     *                      Page number provided as user input string.
     *                      The final validation of provided input will be at DAO level.
     *                      That allow as to exclude additional DAO call,
     *                      as for input validation we need the total count of page elements.
     * @return object that contains topics(that have been updated in the last 24 hours)
     *         for one page and information for pagination
     */
    Page<Topic> getRecentTopics(String page);

    /**
     * Get unanswered topics(topics which has only 1 post added during topic creation).
     *
     * @param page page number, for which we will find topics
     *                      Page number provided as user input string.
     *                      The final validation of provided input will be at DAO level.
     *                      That allow as to exclude additional DAO call,
     *                      as for input validation we need the total count of page elements.
     * @return object that contains unanswered topics for one page and information for
     *         pagination
     */
    Page<Topic> getUnansweredTopics(String page);

    /**
     * Search by topics, title and content of which corresponds to the text of search.
     *
     * @param phrase search request from the user
     * @param page requested page number, page size is calculated based on user's preferences
     *                      Page number provided as user input string.
     *                      The final validation of provided input will be at DAO level.
     *                      That allow as to exclude additional DAO call,
     *                      as for input validation we need the total count of page elements.
     * @return search results page
     */
    Page<Topic> searchByTitleAndContent(String phrase, String page);

    /**
     * Indexing topics from the database.
     * This functionality is required either when data exists in the database,
     * but the index doesn't contain this data or the index is re-created.
     */
    void rebuildSearchIndex();
    
    /**
     * Check if user has given permission. Throws 
     * {@link org.springframework.security.access.AccessDeniedException} if user
     * don't have specified permission.
     *
     * @param branchId ID of the branch which holds permissions
     */
    void checkViewTopicPermission(Long branchId);
}
