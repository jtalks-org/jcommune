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

import java.util.HashMap;
import java.util.Map;

/**
 * Represents one comment to one line in code review.
 *
 * @author Vyacheslav Mishcheryakov
 */
public class PostComment extends Entity {

    /**
     * Minimal allowed length of comment message
     */
    public static final int BODY_MIN_LENGTH = 1;
    /**
     * Maximum allowed length of comment message
     */
    public static final int BODY_MAX_LENGTH = 5000;

    private JCUser author;
    private DateTime creationDate;
    private String body;
    private Post post;
    private Map<String, String> attributes = new HashMap<>();
    private DateTime deletionDate;
    private DateTime modificationDate;
    private JCUser userChanged;

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
        return this.getAuthor() != null && ObjectUtils.nullSafeEquals(this.getAuthor(), user);
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
        return post;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    /**
     * Gets attributes of the comment
     *
     * @return attributes of  the comment
     */
    public Map<String, String> getAttributes() {
        return attributes;
    }

    /**
     * Sets specified attributes to the comment.
     * For hibernate usage. Use PostComment#putAttribute or PostComment#putAllAttributes
     *
     * @param attributes attributes to set
     */
    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }

    /**
     * Adds new attribute or overrides existent attribute of the comment
     *
     * @param attributeName name of the attribute
     * @param attributeValue value of the attribute
     */
    public void putAttribute(String attributeName, String attributeValue) {
        this.attributes.put(attributeName, attributeValue);
    }

    /**
     * Adds new and overrides existent attributes of the comment
     *
     * @param attributes map of attributes to add
     */
    public void putAllAttributes(Map<String, String> attributes) {
        this.attributes.putAll(attributes);
    }

    /**
     * Get a deletion date of the comment. We use {@link #deletionDate} to mark comment as deleted. So, if
     * {@link #deletionDate} is not null, it means that comment was deleted.
     * We need this functional because deletion records from database leads to errors with concurrency.
     * @see <a href="http://jira.jtalks.org/browse/JC-1757">JC-1757</a>
     *
     * @return true if the comment has been deleted
     */
    public DateTime getDeletionDate() {
        return deletionDate;
    }

    /**
     * Set a deletion date of the comment
     *
     * @param deletionDate
     * @see #getDeletionDate()
     */
    public void setDeletionDate(DateTime deletionDate) {
        this.deletionDate = deletionDate;
    }

    public DateTime getModificationDate() {
        return modificationDate;
    }

    public void setModificationDate(DateTime modificationDate) {
        this.modificationDate = modificationDate;
    }

    public JCUser getUserChanged() {
        return userChanged;
    }

    public void setUserChanged(JCUser userChanged) {
        this.userChanged = userChanged;
    }
}
