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

import org.jtalks.jcommune.model.entity.Persistent;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Vitaliy Kravchenko
 */

public class ArticleCollection extends Persistent {

    private List<Article> articles = new ArrayList<Article>();
    private String description;
    private String title;

    /**
     * Creates the ArticleCollection instance. All fields values are null.
     */
    public ArticleCollection() {
    }

    /**
     * Creates the ArticleCollection instance with a list of articles
     *
     * @param articles  the list of articles
     */
    public ArticleCollection(List<Article> articles) {
        this.articles = articles;
    }

    /**
     * Returns the description of this article collection
     *
     * @return the value of description field
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of this article collection
     *
     * @param description  the new value for description field
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Returns the title of this article collection
     *
     * @return the value of title field
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title of this article collection
     *
     * @param title  the new value for title field
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Adds a new <code>Article</code> to this article collection
     *
     * @param article  the article to be added
     */
    public void addArticle(Article article) {
        articles.add(article);
    }

    /**
     * Removes existing <code>Article</code> from this article collection
     *
     * @param article  the article to be removed
     */
    public void removeArticle(Article article) {
        articles.remove(article);
    }

    /**
     * Returns the list of articles of this article collection
     *
     * @return the value of articles field
     */
    public List<Article> getArticles() {
        return articles;
    }

    /**
     * Sets the list of articles of this article collection
     *
     * @param articles  the new value for articles field
     */
    public void setArticles(List<Article> articles) {
        this.articles = articles;
    }


}
