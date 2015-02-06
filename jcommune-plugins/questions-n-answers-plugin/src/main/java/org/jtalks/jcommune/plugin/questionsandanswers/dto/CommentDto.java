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
package org.jtalks.jcommune.plugin.questionsandanswers.dto;

import org.hibernate.validator.constraints.NotBlank;
import org.jtalks.jcommune.model.entity.PostComment;
import org.jtalks.jcommune.plugin.api.web.velocity.tool.JodaDateTimeTool;

import javax.validation.constraints.Size;

/**
 * @author Mikhail Stryzhonok
 */
public class CommentDto {

    private long id;

    private long postId;

    @NotBlank
    @Size(min = PostComment.BODY_MIN_LENGTH, max = PostComment.BODY_MAX_LENGTH)
    private String body;

    private String authorUsername;

    private long authorId;

    private String formattedCreationDate;

    public CommentDto() {
    }

    public CommentDto(PostComment postComment, JodaDateTimeTool dateTimeTool) {
        this.id = postComment.getId();
        this.authorUsername = postComment.getAuthor().getUsername();
        this.body = postComment.getBody();
        this.formattedCreationDate = dateTimeTool.format(postComment.getCreationDate());
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getPostId() {
        return postId;
    }

    public void setPostId(long postId) {
        this.postId = postId;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getAuthorUsername() {
        return authorUsername;
    }

    public void setAuthorUsername(String authorUsername) {
        this.authorUsername = authorUsername;
    }

    public long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(long authorId) {
        this.authorId = authorId;
    }

    public String getFormattedCreationDate() {
        return formattedCreationDate;
    }

    public void setFormattedCreationDate(String formattedCreationDate) {
        this.formattedCreationDate = formattedCreationDate;
    }
}
