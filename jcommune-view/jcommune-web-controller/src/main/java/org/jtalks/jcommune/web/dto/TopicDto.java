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
package org.jtalks.jcommune.web.dto;

import org.hibernate.validator.constraints.NotBlank;
import org.joda.time.DateTime;
import org.jtalks.jcommune.model.entity.Post;
import org.jtalks.jcommune.model.entity.Topic;
import org.jtalks.jcommune.web.validation.annotations.BbCodeAwareSize;

import javax.validation.constraints.Size;

/**
 * DTO for {@link Topic} objects. Used for validation and binding to form.
 *
 * @author Vitaliy Kravchenko
 * @author Max Malakhov
 */
public class TopicDto {

    @NotBlank
    @Size(min = Topic.MIN_NAME_SIZE, max = Topic.MAX_NAME_SIZE)
    private String topicName;

    @NotBlank
    @BbCodeAwareSize(min = Post.MIN_LENGTH, max = Post.MAX_LENGTH)
    private String bodyText;

    private int topicWeight;

    private boolean sticked;
    private boolean announcement;

    private long id;

    private String pollTitle;
    private String pollOptions;
    private boolean single;
    private DateTime endingDate;

    /**
     * Plain object for topic creation
     */
    public TopicDto() {
    }

    /**
     * Create dto from {@link Topic}
     *
     * @param topic topic for conversion
     */
    public TopicDto(Topic topic) {
        topicName = topic.getTitle();
        bodyText = topic.getFirstPost().getPostContent();
        id = topic.getId();
        topicWeight = topic.getTopicWeight();
        sticked = topic.isSticked();
        announcement = topic.isAnnouncement();
        pollTitle = topic.getPoll().getTitle();
        pollOptions = topic.getPoll().getPollOptions().toString();
        single = topic.getPoll().isSingle();
        endingDate = topic.getPoll().getEndingDate();
    }

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

    /**
     * @return priority of sticked topic
     */
    public int getTopicWeight() {
        return this.topicWeight;
    }

    /**
     * Set priority for a sticked topic.
     *
     * @param topicWeight priority(weight) of sticked topic
     */
    public void setTopicWeight(int topicWeight) {
        this.topicWeight = topicWeight;
    }

    /**
     * @return stickedness flag of topic
     */
    public boolean isSticked() {
        return this.sticked;
    }

    /**
     * Set flag of stickedness.
     *
     * @param sticked flag of stickedness
     */
    public void setSticked(boolean sticked) {
        this.sticked = sticked;
    }

    /**
     * @return announcement flag of topic
     */
    public boolean isAnnouncement() {
        return this.announcement;
    }

    /**
     * Set flag of announcement for a topic
     *
     * @param announcement flag of announcement
     */
    public void setAnnouncement(boolean announcement) {
        this.announcement = announcement;
    }
}