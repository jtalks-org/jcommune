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

import java.util.ArrayList;
import java.util.List;

import org.jtalks.jcommune.model.entity.CodeReview;
import org.jtalks.jcommune.model.entity.Post;
import org.jtalks.jcommune.model.entity.PostComment;

/**
 * DTO for {@link CodeReview}
 * @author Vyacheslav Mishcheryakov
 *
 */
public class CodeReviewDto {

    private long postId;
    
    private List<CodeReviewCommentDto> comments = new ArrayList<>();

    public CodeReviewDto() {
    }
    
    public CodeReviewDto(Post post) {
        this.postId = post.getId();
        for (PostComment comment : post.getComments()) {
            this.comments.add(new CodeReviewCommentDto(comment));
        }
    }

    /**
     * Gets id of post that is comment owner
     *
     * @return id of owner post
     */
    public long getPostId() {
        return postId;
    }

    /**
     * Sets id of the post
     *
     * @param postId id to set
     */
    public void setPostId(long postId) {
        this.postId = postId;
    }

    /**
     * @return the comments
     */
    public List<CodeReviewCommentDto> getComments() {
        return comments;
    }

    /**
     * @param comments the comments to set
     */
    public void setComments(List<CodeReviewCommentDto> comments) {
        this.comments = comments;
    }
    
    
}
