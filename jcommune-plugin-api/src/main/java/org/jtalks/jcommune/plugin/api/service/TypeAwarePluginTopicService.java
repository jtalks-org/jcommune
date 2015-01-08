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
package org.jtalks.jcommune.plugin.api.service;

import org.jtalks.jcommune.model.entity.Post;
import org.jtalks.jcommune.model.entity.Topic;
import org.jtalks.jcommune.plugin.api.exceptions.NotFoundException;

/**
 * @author Mikhail Stryzhonok
 */
public interface TypeAwarePluginTopicService {
    /**
     * Gets topic with specified type and id
     *
     * @param id id of interested topic
     * @param type type of interested topic
     *
     * @return topic with specified id and type
     *
     * @throws NotFoundException if no topic with specified id found in database or topic has different type
     */
    Topic get(Long id, String type) throws NotFoundException;

    /**
     * Add new topic with given title and body.
     * Author is current user.
     *
     * @param topicDto    topic that used as dto
     * @param bodyText body of topic
     * @return created topic
     * @throws org.jtalks.jcommune.plugin.api.exceptions.NotFoundException
     *          when branch not found
     */
    Topic createTopic(Topic topicDto, String bodyText) throws NotFoundException;

    /**
     * Update current topic with given title and body.
     *
     * @param topic topic to be updated
     */
    void updateTopic(Topic topic) throws NotFoundException;

    /**
     * Check if user has given permission. Throws
     * {@link org.springframework.security.access.AccessDeniedException} if user
     * don't have specified permission.
     *
     * @param branchId ID of the branch which holds permissions
     */
    void checkViewTopicPermission(Long branchId);

    /**
     * Closes topic so no one can add new post until it's not open again.
     * For the topic already closed does nothing.
     *
     * We can't close plugin topic as core topic because we need to redirect to different page
     * after closing plugin topic
     *
     * @param topic topic we want to close, hibernate session-bound
     */
    void closeTopic(Topic topic);

    /**
     * Opens topic so granted users may add posts again.
     * For the topic already open does nothing.
     *
     * We can't open plugin topic as core topic because we need to redirect to different page
     * after opening plugin topic
     *
     * @param topic topic we want to open, hibernate session-bound
     */
    void openTopic(Topic topic);

    /**
     * Add the answer to the topic. Add the specified message to the target topic and save.
     * User should be authorized to answer to the topic. Otherwise {@link IllegalStateException} will be thrown.
     *
     * @param topicId    target topic primary id.
     * @param answerBody the text of the answer
     * @param branchId   target branch primary id.
     * @return created {@link org.jtalks.jcommune.model.entity.Post}
     * @throws org.jtalks.jcommune.plugin.api.exceptions.NotFoundException
     *          when topic not found
     */
    Post replyToTopic(long topicId, String answerBody, long branchId) throws NotFoundException;

}
