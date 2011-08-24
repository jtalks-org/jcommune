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
import org.jtalks.jcommune.model.entity.Post;

import javax.validation.constraints.Size;
/**
 * DTO for {@link Post} objects. Used for validation and binding to form.
 */
public class PostDto {
    private static final int MAX_POST_LENGTH = 2000;

    @NotBlank
    @Size(min = 5, max = MAX_POST_LENGTH)
    private String bodyText;
    private long id;

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
        return dto;
    }    
}

