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

import java.util.List;

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
     * @param branchId  target branch primary id.
     * @return created {@link Post}
     * @throws org.jtalks.jcommune.service.exceptions.NotFoundException
     *          when topic not found
     */
    Post replyToTopic(long topicId, String answerBody, long branchId) throws NotFoundException;

    /**
     * Add new topic with given title and body.
     * Author is current user.
     *
     * @param topicName         name of topic
     * @param bodyText          body of topic
     * @param branchId          branch containing topic
     * @param notifyOnAnswers flag that indicates notifications state(enabled or disabled)
     * @return created topic
     * @throws org.jtalks.jcommune.service.exceptions.NotFoundException
     *          when branch not found
     */
    Topic createTopic(String topicName, String bodyText, long branchId, boolean notifyOnAnswers)
            throws NotFoundException;

    /**
     * @return list of {@code Topic}  objectsupdated since last 24 hours.
     */
    List<Topic> getRecentTopics();

    /**
     * Get unanswered topics(topics which has only 1 post added during topic creation).
     *
     * @return list of {@code Topic} objects without answers
     */
    List<Topic> getUnansweredTopics();

    /**
     * Update current topic with given title and body.
     *
     * @param topicId   topic id
     * @param topicName name of topic
     * @param bodyText  body of topic
     * @throws org.jtalks.jcommune.service.exceptions.NotFoundException
     *          when topic not found
     */
    void updateTopic(long topicId, String topicName, String bodyText) throws NotFoundException;

    /**
     * Update current topic with given title and body.
     *
     * @param topicId           topic id
     * @param topicName         name of topic
     * @param bodyText          body of topic
     * @param topicWeight       priority for sticked topic
     * @param sticked           flag for sticking a topic
     * @param announcement      flag, which set topic as announcement
     * @param notifyOnAnswers flag that indicates notifications state(enabled or disabled)
     * @throws NotFoundException when topic not found
     */
    void updateTopic(long topicId, String topicName, String bodyText, int topicWeight,
                     boolean sticked, boolean announcement, boolean notifyOnAnswers) throws NotFoundException;

    /**
     * Delete topic by id.
     *
     * @param topicId topic id
     * @param branchId branch Id
     * @return branch from which topic deleted
     * @throws NotFoundException when topic not found
     */
    Branch deleteTopic(long topicId, long branchId) throws NotFoundException;

    /**
     * Moves topic to another branch.
     *
     * @param topicId  id of moving topic
     * @param branchId id of target branch
     * @throws NotFoundException when topic or branch with given id not found
     */
    void moveTopic(Long topicId, Long branchId) throws NotFoundException;
}
