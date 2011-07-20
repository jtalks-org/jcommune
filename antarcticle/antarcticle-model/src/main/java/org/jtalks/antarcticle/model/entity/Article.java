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
import org.jtalks.common.model.entity.User;
import org.jtalks.common.model.entity.Persistent;

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

    /**
     * Constructor for article. All instance variables get default value 
     */
    public Article() {
    }

    /**
     * Constructor for article with initializing of content
     * @param articleContent Text for article
     */
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

//    /**
//     * Creates the Article instance with the creationDate initialized with current date and time
//     *
//     * @return new Article instance
//     */
//    public static Article createNewArticle() {
//        return new Article(new DateTime());
//    }

    /**
     * Get content of an article
     * @return text of article
     */
    public String getArticleContent() {
        return articleContent;
    }

    /**
     * Set content of article
     * @param articleContent    Text for article
     */
    public void setArticleContent(String articleContent) {
        this.articleContent = articleContent;
    }

    /**
     * Get topic of article
     * @return topic of article
     */
    public String getArticleTitle() {
        return articleTitle;
    }

    /**
     * Set topic for an article
     * @param articleTitle topic of article
     */
    public void setArticleTitle(String articleTitle) {
        this.articleTitle = articleTitle;
    }

    /**
     * Get date when article is created
     * @return creation date of article
     */
    public DateTime getCreationDate() {
        return creationDate;
    }

    /**
     * Set date when article is created
     * @param creationDate date of article creation
     */
    public void setCreationDate(DateTime creationDate) {
        this.creationDate = creationDate;
    }
    
    /**
     * Get {@link User} who creates an article
     * @return user created an article
     */
    public User getUserCreated() {
        return userCreated;
    }

    /**
     * Set {@link User} who creates an article
     * @param userCreated user created an article
     */
    public void setUserCreated(User userCreated) {
        this.userCreated = userCreated;
    }

    /**
     * Get {@link ArticleCollection} which article is belong
     * @return article collection
     */
    public ArticleCollection getArticleCollection() {
        return articleCollection;
    }

    /**
     * Set {@link ArticleCollection} which article is belong
     * @param articleCollection article collection
     */
    public void setArticleCollection(ArticleCollection articleCollection) {
        this.articleCollection = articleCollection;
    }
}
