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

package org.jtalks.antarcticle.model.entity;

import org.joda.time.DateTime;
import org.jtalks.jcommune.model.entity.Persistent;
import org.jtalks.jcommune.model.entity.User;

/**
 * Represent a comment for a concrete article
 * Contains a link to an article {@link Article}
 * 
 * @author Dmitry Sokolov
 */
public class Comment extends Persistent {
    
    private User userCommented;
    private DateTime creationDate;
    private String commentContent;
    private Article article;

    /**
     * Constructor for a comment.
     * All instance variables get default value
     */
    public Comment() {
    }
    
    /**
     * Constructor for comment 
     * where creation date is set
     * 
     * @param creationDate  date when comment was created
     */
    public Comment(DateTime creationDate) {
        this.creationDate = creationDate;
    }

    /**
     * Constructor for a comment
     * where all instance variable are initialized
     * 
     * @param userCommented     user created comment
     * @param creationDate      date when comment is created
     * @param commentContent    text of comment
     * @param article           article for which comment is created
     */
    public Comment(User userCommented, DateTime creationDate, String commentContent, Article article) {
        this.userCommented = userCommented;
        this.creationDate = creationDate;
        this.commentContent = commentContent;
        this.article = article;
    }

    /**
     * Get the content of comment
     * @return the content of comment
     */
    public String getCommentContent() {
        return commentContent;
    }

    /** Set the content of comment
     * @param commentContent The text of content 
     */
    public void setCommentContent(String commentContent) {
        this.commentContent = commentContent;
    }

    /**
     * Get date when comment is created
     * @return creation date of comment
     */
    public DateTime getCreationDate() {
        return creationDate;
    }

    /**
     * Set creation date of comment
     * @param creationDate Date when post is created
     */
    public void setCreationDate(DateTime creationDate) {
        this.creationDate = creationDate;
    }
    
    /**
     * Get {@link User} who creates comment
     * @return user created a comment
     */
    public User getUserCommented() {
        return userCommented;
    }

    /**
     * Set {@link User} who creates comment
     * @param userCommented user created a comment
     */
    public void setUserCommented(User userCommented) {
        this.userCommented = userCommented;
    }

    /**
     * Get {@link Article} for which comment is created
     * @return article
     */
    public Article getArticle() {
        return article;
    }
    
    /**
     * Set {@link Article} for which comment is created
     * @param article   article
     */
    public void setArticle(Article article) {
        this.article = article;
    }
    
}
