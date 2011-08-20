/**
 * Copyright (C) 2011  jtalks.org Team
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
 * Also add information on how to contact you by electronic and paper mail.
 * Creation date: Apr 12, 2011 / 8:05:19 PM
 * The jtalks.org Project
 */

package org.jtalks.jcommune.web.dto;

import org.hibernate.validator.constraints.NotBlank;
import org.jtalks.jcommune.model.entity.Topic;

import javax.validation.constraints.Size;

/**
 * DTO for {@link Topic} objects. Used for validation and binding to form.
 *
 * @author Vitaliy Kravchenko
 * @author Max Malakhov
 */
public class TopicDto {

    private static final int MAX_POST_LENGTH = 2000;

    @NotBlank
    @Size(min = 5, max = 255)
    private String topicName;

    @NotBlank
    @Size(min = 5, max = MAX_POST_LENGTH)
    private String bodyText;

    private int topicWeight;

    private boolean sticked;
    private boolean announcement;

    /**
     * @return topic id
     */
    public long getId() {
        return id;
    }

    /**
     * Set topic id.
     *
     * @param id id
     */
    public void setId(long id) {
        this.id = id;
    }

    private long id;

    /**
     * Get topic title.
     *
     * @return topic title
     */
    public String getTopicName() {
        return topicName;
    }

    /**
     * Set topic title.
     *
     * @param topicName name of topic
     */
    public void setTopicName(String topicName) {
        this.topicName = topicName;
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

    public int getTopicWeight() {
        return this.topicWeight;
    }

    public void setTopicWeight(int topicWeight) {
        this.topicWeight = topicWeight;
    }

    public boolean isSticked() {
        return this.sticked;
    }

    public void setSticked(boolean sticked) {
        this.sticked = sticked;
    }

    public boolean isAnnouncement() {
        return this.announcement;
    }

    public void setAnnouncement(boolean announcement) {
        this.announcement = announcement;
    }

    /**
     * Create dto from {@link Topic}
     *
     * @param topic topic for conversion
     * @return dto for topic
     */
    public static TopicDto getDtoFor(Topic topic) {
        TopicDto dto = new TopicDto();
        dto.setTopicName(topic.getTitle());
        dto.setBodyText(topic.getFirstPost().getPostContent());
        dto.setId(topic.getId());
        dto.setTopicWeight(topic.getTopicWeight());
        dto.setSticked(topic.isSticked());
        dto.setAnnouncement(topic.isAnnouncement());
        return dto;
    }
}