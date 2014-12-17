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

import org.jtalks.jcommune.model.entity.Poll;
import org.jtalks.jcommune.model.entity.Topic;
import org.jtalks.jcommune.plugin.api.exceptions.NotFoundException;

/**
 * @author Mikhail Stryzhonok
 */
public interface PluginTopicModificationService {

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
     * @param poll  poll of the updated topic, if any (added for compatibility)
     */
    void updateTopic(Topic topic, Poll poll) throws NotFoundException;

    /**
     * Closes topic so no one can add new post until it's not open again.
     * For the topic already closed does nothing.
     *
     * @param topic topic we want to close, hibernate session-bound
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
