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
import org.jtalks.jcommune.model.entity.User;
import org.jtalks.jcommune.model.entity.Persistent;

/**
 *
 * @author Vitaliy Kravchenko
 * @author Dmitry Sokolov
 */

public class Article extends Persistent {
    
    private DateTime creationDate;
    private User userCreated;
    private String articleTitle;
    private String articleContent;
    private ArticleCollection articleCollection;

    public Article() {
    }

    public Article(String articleContent) {
        this.articleContent = articleContent;
    }

    /**
     * Constructs the Article instance with the specified creation date and time
     *
     * @param creationDate  the article creation date and time
     */
    public Article(DateTime creationDate) {
        this.creationDate = creationDate;
    }

    /**
     * Creates the Article instance with the creationDate initialized with current date and time
     *
     * @return new Article instance
     */
    public static Article createNewArticle() {
        return new Article(new DateTime());
    }

    public String getArticleContent() {
        return articleContent;
    }

    public void setArticleContent(String articleContent) {
        this.articleContent = articleContent;
    }

    public String getArticleTitle() {
        return articleTitle;
    }

    public void setArticleTitle(String articleTopic) {
        this.articleTitle = articleTopic;
    }

    public DateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(DateTime creationDate) {
        this.creationDate = creationDate;
    }


    public User getUserCreated() {
        return userCreated;
    }

    public void setUserCreated(User userCreated) {
        this.userCreated = userCreated;
	}
	
    public ArticleCollection getArticleCollection() {
        return articleCollection;
    }

    public void setArticleCollection(ArticleCollection articleCollection) {
        this.articleCollection = articleCollection;
    }
}
