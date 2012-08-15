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
import org.jtalks.jcommune.model.entity.Post;
import org.jtalks.jcommune.model.entity.Topic;
import org.jtalks.jcommune.service.exceptions.NotFoundException;
import org.springframework.data.domain.Page;

/**
 * This interface should have methods which give us more abilities in manipulating Topic persistent entity.
 *
 * @author Osadchuck Eugeny
 * @author Vervenko Pavel
 * @author Kirill Afonin
 * @author Vitaliy Kravchenko
 * @author Eugeny Batov
 */
public interface TopicService extends EntityService<Topic> {

    /**
     * Add the answer to the topic. Add the specified message to the target topic and save.
     * User should be authorized to answer to the topic. Otherwise {@link IllegalStateException} will be thrown.
     *
     * @param topicId    target topic primary id.
     * @param answerBody the text of the answer
     * @param branchId   target branch primary id.
     * @return created {@link Post}
     * @throws org.jtalks.jcommune.service.exceptions.NotFoundException
     *          when topic not found
     */
    Post replyToTopic(long topicId, String answerBody, long branchId) throws NotFoundException;

    /**
     * Add new topic with given title and body.
     * Author is current user.
     *
     * @param topic           topic that used as dto
     * @param bodyText        body of topic
     * @param notifyOnAnswers user notification on answers flag
     * @return created topic
     * @throws org.jtalks.jcommune.service.exceptions.NotFoundException
     *          when branch not found
     */
    Topic createTopic(Topic topic, String bodyText, boolean notifyOnAnswers)
            throws NotFoundException;

    /**
     * Get topics that have been updated in the last 24 hours.
     *
     * @param page page page number, for which we will find topics
     * @return object that contains topics(that have been updated in the last 24 hours)
     *         for one page and information for pagination
     */
    Page<Topic> getRecentTopics(int page);

    /**
     * Get unanswered topics(topics which has only 1 post added during topic creation).
     *
     * @param page page number, for which we will find topics
     * @return object that contains unanswered topics for one page and information for
     *         pagination
     */
    Page<Topic> getUnansweredTopics(int page);

    /**
     * Update current topic with given title and body.
     *
     * @param topicDto {@link Topic} object used as DTO between layers
     * @param bodyText body of topic
     * @throws org.jtalks.jcommune.service.exceptions.NotFoundException
     *          when topic not found
     */
    void updateTopic(Topic topicDto, String bodyText) throws NotFoundException;

    /**
     * Update current topic with given title and body.
     *
     * @param topicDto        {@link Topic} object used as DTO between layers
     * @param bodyText        body of topic
     * @param notifyOnAnswers user notification on answers flag
     * @throws NotFoundException when topic not found
     */
    void updateTopic(Topic topicDto, String bodyText,
                     boolean notifyOnAnswers) throws NotFoundException;

    /**
     * Delete topic by id. Sends notifications to subscribers and performs logging.
     *
     * @param topicId topic id
     * @return branch from which topic deleted
     * @throws NotFoundException when topic not found
     */
    Branch deleteTopic(long topicId) throws NotFoundException;
    
    /**
     * Delete topic by id. Does not send any notification or log messages. 
     * Intended to be used mostly by other services.
     *
     * @param topicId topic id
     * @return branch from which topic deleted
     * @throws NotFoundException when topic not found
     */
    Branch deleteTopicSilent(long topicId) throws NotFoundException;

    /**
     * Moves topic to another branch.
     *
     * @param topicId  id of moving topic
     * @param branchId id of target branch
     * @throws NotFoundException when topic or branch with given id not found
     */
    void moveTopic(Long topicId, Long branchId) throws NotFoundException;

    /**
     * Get topics in the branch.
     *
     * @param branch        for this branch we will find topics
     * @param page          page number, for which we will find topics
     * @param pagingEnabled if true, then it returns topics for one page, otherwise it
     *                      return all topics in the branch
     * @return object that contains topics for one page(note, that one page may contain
     *         all topics) and information for pagination
     */
    Page<Topic> getTopics(Branch branch, int page, boolean pagingEnabled);
}
