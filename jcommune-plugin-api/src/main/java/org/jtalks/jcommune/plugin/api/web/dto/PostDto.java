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

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.jtalks.jcommune.model.entity.Post;
import org.jtalks.jcommune.plugin.api.web.validation.annotations.BbCodeAwareSize;
import org.jtalks.jcommune.plugin.api.web.validation.annotations.BbCodeNesting;

/**
 * DTO for {@link Post} objects. Used for validation and binding to form.
 */
public class PostDto {

    @BbCodeAwareSize(min = Post.MIN_LENGTH, max = Post.MAX_LENGTH)
    @BbCodeNesting
    private String bodyText;
    private long id;
    private long topicId;
    private TopicDto topicDto;
    private DateTime creationDate;
    private DateTime modificationDate;

    /**
     * Get topic id.
     *
     * @return topic id
     */
    public long getTopicId() {
        return topicId;
    }

    /**
     * Set topic id.
     *
     * @param topicId topic id
     */
    public void setTopicId(long topicId) {
        this.topicId = topicId;
    }

    /**
     * Get post id.
     *
     * @return post id
     */
    public long getId() {
        return id;
    }

    /**
     * Set post id.
     *
     * @param id id
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Get post content.
     *
     * @return post content
     */
    public String getBodyText() {
        return bodyText;
    }

    /**
     * Set post content.
     *
     * @param bodyText content of post in topic
     */
    public void setBodyText(String bodyText) {
        this.bodyText = bodyText;
    }

    /**
     * Gets topic dto of the post
     *
     * @return topic dto
     */
    public TopicDto getTopicDto() {
        return topicDto;
    }

    /**
     * Sets specified topic dto to the post
     *
     * @param topicDto topic dto to set
     */
    public void setTopicDto(TopicDto topicDto) {
        this.topicDto = topicDto;
    }

    /**
     * Gets time of post creation represented by this dto
     *
     * @return time of post creation
     */
    public DateTime getCreationDate() {
        return creationDate;
    }

    /**
     * Sets specified time of post  creation
     *
     * @param creationDate time of post  creation
     */
    public void setCreationDate(DateTime creationDate) {
        this.creationDate = creationDate;
    }

    /**
     * Gets time of post modificatiom represented by this dto
     *
     * @return
     */
    public DateTime getModificationDate() {
        return modificationDate;
    }

    /**
     * Sets specified time of post modification
     *
     * @param modificationDate time of post modification
     */
    public void setModificationDate(DateTime modificationDate) {
        this.modificationDate = modificationDate;
    }

    /**
     * Gets millisecond representation of post creation date in UTC timezone
     *
     * We store dates in database in timezone of server. And to display correct time of creation draft
     * we convert UTC representation ot user's timezone in javascript
     *
     * @return millisecond representation of post creation date in UTC timezone
     */
    public long getUtcCreationTime() {
        DateTimeZone zone = creationDate.getZone();
        return zone.convertLocalToUTC(creationDate.getMillis(), false);
    }

    /**
     * Create dto
     *
     * @param post post for conversion
     * @return dto for post
     */
    public static PostDto getDtoFor(Post post) {
        PostDto dto = new PostDto();
        dto.setBodyText(post.getPostContent());
        dto.setId(post.getId());
        dto.setTopicId(post.getTopic().getId());
        dto.setCreationDate(post.getCreationDate());
        dto.setModificationDate(post.getModificationDate());
        return dto;
    }

    public long getDifferenceMillis() {
        DateTime currentDate = new DateTime();
        long differenceTime = currentDate.getMillis() - creationDate.getMillis();
        return differenceTime > 0 ? differenceTime : creationDate.getMillis();
    }
}