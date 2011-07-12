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

package org.jtalks.antarcticle.service;

import org.jtalks.antarcticle.model.entity.Article;
import org.jtalks.antarcticle.model.entity.ArticleCollection;
import org.jtalks.jcommune.model.entity.User;
import org.jtalks.jcommune.service.EntityService;
import org.jtalks.jcommune.service.exceptions.NotFoundException;


/**
 * @author Vitaliy Kravchenko
 * @authoe Dmitry Sokolov
 */

public interface ArticleService extends EntityService<Article> {
    
    /**
     * Persists {@link Article}
     * @param article article which should to persist
     */
    void addArticle(Article article);
    
    /**
     * Creates new article with defined {@link User} and {@link ArticleCollection}
     * 
     * @param articleCollection article collection
     * @param user user who creates article
     * @return article
     */
    Article createArticle(ArticleCollection articleCollection, User user);
    
    /**
     * Delete {@link Article} from persistence
     * @param article article which need to delete
     * @throws NotFoundException if article is not persisted
     */
    void deleteArticle(Article article) throws NotFoundException;
    
    /**
     * Get {@link Article} by article's identifier
     * @param id article's identifier
     * @return article
     */
    Article getFirstArticleFromCollection(long id);

}
