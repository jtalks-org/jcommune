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

package org.jtalks.antarcticle.service.transactional;

import org.joda.time.DateTime;
import org.jtalks.antarcticle.model.dao.ArticleDao;
import org.jtalks.antarcticle.model.entity.Article;
import org.jtalks.antarcticle.model.entity.ArticleCollection;
import org.jtalks.antarcticle.service.ArticleService;
import org.jtalks.common.model.entity.User;
import org.jtalks.common.service.exceptions.NotFoundException;
import org.jtalks.common.service.transactional.AbstractTransactionalEntityService;


/**
 * @author Vitaliy Kravchenko
 * @author Dmitry Sokolov
 */
public class TransactionalArticleService 
    extends AbstractTransactionalEntityService<Article, ArticleDao>
    implements ArticleService {
    
    /**
     * Creates an instance of entity based service
     * @param dao Data access object for CRUD operations of {@link Article}
     */
    public TransactionalArticleService(ArticleDao dao) {
        this.dao = dao;            
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addArticle(Article article) {
        dao.saveOrUpdate(article);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Article createArticle(ArticleCollection articleCollection, User user) {
        Article article = new Article(new DateTime());
        article.setUserCreated(user);
        article.setArticleCollection(articleCollection);
        return article;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteArticle(Article article) throws NotFoundException {
        if(article.getId() == 0) {
            throw new NotFoundException("The current article is not persist");
        }
        if(!dao.isExist(article.getId())) {
            throw new NotFoundException("There is no article with id " + article.getId());
        }
        dao.delete(article.getId());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Article getFirstArticleFromCollection(long id) throws NotFoundException {
        if (!dao.isExist(id)){
            throw new NotFoundException("There is no collection with such id");
        }
        return dao.getFirstArticleFromCollection(id);         
    }
}
