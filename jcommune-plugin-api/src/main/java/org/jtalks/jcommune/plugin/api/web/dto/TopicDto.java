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
package org.jtalks.jcommune.plugin.api.web.dto;

import org.jtalks.jcommune.model.entity.Poll;
import org.jtalks.jcommune.model.entity.Topic;



/**
 * Now it copy of TopicDto which placed in controller module.
 * TODO: move TopicDto to plugin-api and fix validation problems
 *
 */
public class TopicDto {

    private Topic topic;


    private String bodyText;

    /**
     * Plain object for topic creation
     */
    public TopicDto() {
    }

    /**
     * Create dto from {@link org.jtalks.jcommune.model.entity.Topic}
     *
     * @param topic topic for conversion
     */
    public TopicDto(Topic topic) {
        this.topic = topic;
        if (!topic.getPosts().isEmpty()) {
            bodyText = topic.getBodyText();
        }
    }

    /**
     * @return topic that used as dto between controllers and services
     */
    public Topic getTopic() {
        return topic;
    }

    /**
     * Set topic in dto. Used in tests.
     *
     * @param topic topic
     */
    public void setTopic(Topic topic) {
        this.topic = topic;
    }

    /**
     * Get first post content.
     *
     * @return first post content
     */
    public String getBodyText() {
        return bodyText;
    }

    /**
     * Set first post content.
     *
     * @param bodyText content of first post in topic
     */
    public void setBodyText(String bodyText) {
        this.bodyText = bodyText;
    }

    /**
     * @return poll in topic
     */
    public Poll getPoll() {
        return topic.getPoll();
    }

    /**
     * Set poll in topic.
     *
     * @param poll poll
     */
    public void setPoll(Poll poll) {
        topic.setPoll(poll);
    }

    /**
     * Fills persistent topic object with data from the current dto
     *
     * @param persistentTopic persistent topic
     * @return the same topic with fields set from dto
     */
    public Topic fillTopic(Topic persistentTopic) {
        persistentTopic.setTitle(topic.getTitle());
        persistentTopic.getFirstPost().setPostContent(bodyText);
        persistentTopic.setAnnouncement(topic.isAnnouncement());
        persistentTopic.setSticked(topic.isSticked());
        return topic;
    }
}