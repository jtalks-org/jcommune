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
import org.jtalks.jcommune.model.entity.Post;
import org.jtalks.jcommune.web.validation.annotations.BbCodeAwareSize;
import org.jtalks.jcommune.web.validation.annotations.BbCodeNesting;

/**
 * DTO for {@link Post} objects. Used for validation and binding to form.
 */
public class PostDto {

    @NotBlank
    @BbCodeAwareSize(min = Post.MIN_LENGTH, max = Post.MAX_LENGTH)
    @BbCodeNesting
    private String bodyText;
    private long id;
    private long topicId;

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
        return dto;
    }
}

