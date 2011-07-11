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
 *
 * @author Dmitry Sokolov
 */
public class Comment extends Persistent {
    
    private User userCommented;
    private DateTime creationDate;
    private String commentContent;
    private Article article;

    public Comment() {
    }

    public Comment(DateTime creationDate) {
        this.creationDate = creationDate;
    }

    public Comment(User userCommented, DateTime creationDate, String commentContent, Article article) {
        this.userCommented = userCommented;
        this.creationDate = creationDate;
        this.commentContent = commentContent;
        this.article = article;
    }

    public String getCommentContent() {
        return commentContent;
    }

    public void setCommentContent(String commentContent) {
        this.commentContent = commentContent;
    }

    public DateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(DateTime creationDate) {
        this.creationDate = creationDate;
    }

    public User getUserCommented() {
        return userCommented;
    }

    public void setUserCommented(User userCommented) {
        this.userCommented = userCommented;
    }

    public Article getArticle() {
        return article;
    }

    public void setArticle(Article article) {
        this.article = article;
    }
    
}
