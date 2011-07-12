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

import java.util.List;
import org.joda.time.DateTime;
import org.jtalks.antarcticle.model.dao.ArticleDao;
import org.jtalks.antarcticle.model.dao.CommentDao;
import org.jtalks.antarcticle.model.entity.Article;
import org.jtalks.antarcticle.model.entity.Comment;
import org.jtalks.antarcticle.service.CommentService;
import org.jtalks.jcommune.model.entity.User;
import org.jtalks.jcommune.service.exceptions.NotFoundException;
import org.jtalks.jcommune.service.transactional.AbstractTransactionalEntityService;

/**
 * @author Vitaliy Kravchenko
 */

public class TransactionalCommentService extends AbstractTransactionalEntityService<Comment, CommentDao> implements CommentService {
    
    private ArticleDao articleDao;
    
    public TransactionalCommentService(CommentDao commentDao, ArticleDao articleDao) {
        this.dao = commentDao;
        this.articleDao =  articleDao;
    }

    @Override
    public void addComment(Comment comment) {
        dao.saveOrUpdate(comment);
    }

    @Override
    public Comment createComment(Article article, User user) {
        Comment comment = new Comment(new DateTime());
        comment.setArticle(article);
        comment.setUserCommented(user);
        return comment;
    }

    @Override
    public List<Comment> getCommentsByArticle(Article article) throws NotFoundException {
        if(article.getId() == 0) throw new NotFoundException("The current article is not persist");
        if(!articleDao.isExist(article.getId())) throw new NotFoundException("There is no article with id " + article.getId());
        return dao.findByArticle(article);
        
    }

    @Override
    public void deleteComment(Comment comment) throws NotFoundException {
        if(comment.getId() == 0) throw new NotFoundException("The current comment is not persist yet");
        if(!dao.isExist(comment.getId())) throw new NotFoundException("There is no comment with is " + comment.getId());
        dao.delete(comment.getId());
    }
}
