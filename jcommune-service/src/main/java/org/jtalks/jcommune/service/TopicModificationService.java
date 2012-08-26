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
public interface TopicModificationService {

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
    Topic createTopic(Topic topic, String bodyText, boolean notifyOnAnswers) throws NotFoundException;

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
     * @param topic topic to be deleted
     * @throws NotFoundException when topic not found
     */
    void deleteTopic(Topic topic) throws NotFoundException;
    
    /**
     * Delete topic by id. Does not send any notification or log messages. 
     * Intended to be used mostly by other services.
     *
     * @param topicId topic id
     * @throws NotFoundException when topic not found
     */
    void deleteTopicSilent(long topicId) throws NotFoundException;

    /**
     * Moves topic to another branch.
     *
     * @param topic  topic we're about to move
     * @param branchId id of target branch
     * @throws NotFoundException when topic or branch with given id not found
     */
    void moveTopic(Topic topic, Long branchId) throws NotFoundException;

    /**
     * Closes topic so no one can add new post until it's not open again.
     * For the topic already closed does nothing.
     *
     * @param topic  topic we want to close, hibernate session-bound
     */
    void closeTopic(Topic topic);

    /**
     * Opens topic so granted users may add posts again.
     * For the topic already open does nothing.
     *
     * @param topic topic we want to open, hibernate session-bound
     */
    void openTopic(Topic topic);
}
