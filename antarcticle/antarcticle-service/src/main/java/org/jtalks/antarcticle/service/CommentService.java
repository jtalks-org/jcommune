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

import java.util.List;
import org.jtalks.antarcticle.model.entity.Article;
import org.jtalks.antarcticle.model.entity.Comment;
import org.jtalks.jcommune.model.entity.User;
import org.jtalks.jcommune.service.EntityService;
import org.jtalks.jcommune.service.exceptions.NotFoundException;

/**
 * @author Vitaliy Kravchenko
 */

public interface CommentService extends EntityService<Comment> {
    
    /**
     * Persists {@link Comment}
     * @param comment comment which should be persisted
     */
    void addComment(Comment comment);
    
    /**
     * Creates a comment with defined {@link Article} and {@link User}
     * @param article article for which comment is created
     * @param user user who creates a comment
     * @return comment
     */
    Comment createComment(Article article, User user);
    
    /**
     * Get list of {@link Comment} by {@link Article}
     * @param article article for which comment need to search
     * @return list of comments
     * @throws NotFoundException if article is not persisted 
     */
    List<Comment> getCommentsByArticle(Article article) throws NotFoundException;
    
    /**
     * Delete comment from persistence
     * @param comment comment which should be deleted
     * @throws NotFoundException if comment is not persisted
     */
    void deleteComment(Comment comment) throws NotFoundException;
}
