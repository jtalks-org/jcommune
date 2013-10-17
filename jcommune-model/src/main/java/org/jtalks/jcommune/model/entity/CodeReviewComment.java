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
package org.jtalks.jcommune.model.entity;

import org.joda.time.DateTime;
import org.jtalks.common.model.entity.Entity;
import org.springframework.util.ObjectUtils;

/**
 * Represents one comment to one line in code review.
 *
 * @author Vyacheslav Mishcheryakov
 */
public class CodeReviewComment extends Entity {

    /**
     * Minimal allowed length of comment message
     */
    public static final int BODY_MIN_LENGTH = 1;
    /**
     * Maximum allowed length of comment message
     */
    public static final int BODY_MAX_LENGTH = 5000;

    /**
     * Number of commented line of code
     */
    private int lineNumber;

    private JCUser author;

    private DateTime creationDate;

    private String body;

    private CodeReview codeReview;

    /**
     * @return {@link CodeReview} that this comment belong to.
     */
    public CodeReview getCodeReview() {
        return codeReview;
    }

    /**
     * For Hibernate use only. For adding comment to code review use
     * the {@link CodeReview#addComment(CodeReviewComment)}
     *
     * @param codeReview the code review.
     */
    void setCodeReview(CodeReview codeReview) {
        this.codeReview = codeReview;
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
     * @return the author
     */
    public JCUser getAuthor() {
        return author;
    }

    /**
     * @param author the author to set
     */
    public void setAuthor(JCUser author) {
        this.author = author;
    }

    /**
     * Determines whether specified user is the author of this comment.
     *
     * @param user a user to define whether she is the author, can be null
     * @return true if the specified user is the author, or false if the author is null or the specified user is null or
     *         the specified user is simply not the author
     */
    public boolean isCreatedBy(JCUser user) {
        if (this.getAuthor() == null) {
            return false;
        }
        return ObjectUtils.nullSafeEquals(this.getAuthor(), user);
    }

    /**
     * The time when comment was added
     *
     * @return the creationDate
     */
    public DateTime getCreationDate() {
        return creationDate;
    }

    /**
     * @param creationDate the creation date of this comment
     */
    public void setCreationDate(DateTime creationDate) {
        this.creationDate = creationDate;
    }

    /**
     * @return the comment body
     */
    public String getBody() {
        return body;
    }

    /**
     * @param body the comment body to set
     */
    public void setBody(String body) {
        this.body = body;
    }

    /**
     * Get post where code review is placed.
     *
     * @return post where code review is placed
     */
    public Post getOwnerPost() {
        return getCodeReview().getOwnerPost();
    }
}
