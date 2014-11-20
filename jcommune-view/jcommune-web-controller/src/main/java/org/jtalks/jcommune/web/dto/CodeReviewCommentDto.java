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

import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotBlank;
import org.jtalks.jcommune.model.entity.CommentProperty;
import org.jtalks.jcommune.model.entity.PostComment;
import org.jtalks.jcommune.model.entity.PropertyType;

import java.util.Arrays;
import java.util.List;

/**
 * DTO for {@link org.jtalks.jcommune.model.entity.PostComment}
 * 
 * @author Vyacheslav Mishcheryakov
 *
 */
public class CodeReviewCommentDto {
    public static final String LINE_NUMBER_PROPERTY_NAME = "line_number";

    private long id;
    
    private int lineNumber;
    
    @NotBlank
    @Size(min = PostComment.BODY_MIN_LENGTH, max = PostComment.BODY_MAX_LENGTH)
    private String body;
    
    private long authorId;
    
    private String authorUsername;

    public CodeReviewCommentDto() {
    }
    
    public CodeReviewCommentDto(PostComment comment) {
        this.id = comment.getId();
        this.lineNumber = getLineNumberProperty(comment);
        this.body = comment.getBody();
        this.authorId = comment.getAuthor().getId();
        this.authorUsername = comment.getAuthor().getUsername();
    }

    private int getLineNumberProperty(PostComment comment) {
        for (CommentProperty property : comment.getCustomProperties()) {
            if (LINE_NUMBER_PROPERTY_NAME.equals(property.getName()) && property.getType() == PropertyType.INT) {
                return Integer.parseInt(property.getValue());
            }
        }
        throw new IllegalArgumentException("Code review comment should contain INT line_number property");
    }

    /**
     * @return the id
     */
    public long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * @return the lineNumber
     */
    public int getLineNumber() {
        return lineNumber;
    }

    /**
     * @param lineNumber the lineNumber to set
     */
    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    /**
     * @return the body
     */
    public String getBody() {
        return body;
    }

    /**
     * @param body the body to set
     */
    public void setBody(String body) {
        this.body = body;
    }

    /**
     * @return the authorId
     */
    public long getAuthorId() {
        return authorId;
    }

    /**
     * @param authorId the authorId to set
     */
    public void setAuthorId(long authorId) {
        this.authorId = authorId;
    }

    /**
     * @return the authorUsername
     */
    public String getAuthorUsername() {
        return authorUsername;
    }

    /**
     * @param authorUsername the authorUsername to set
     */
    public void setAuthorUsername(String authorUsername) {
        this.authorUsername = authorUsername;
    }

    /**
     * Gets custom properties list for comment. In this case contains only line_number property
     *
     * @return list of custom properties
     */
    public List<CommentProperty> getCommentProperties() {
        return Arrays.asList( new CommentProperty(LINE_NUMBER_PROPERTY_NAME, PropertyType.INT,
                String.valueOf(lineNumber)));
    }
    
}
