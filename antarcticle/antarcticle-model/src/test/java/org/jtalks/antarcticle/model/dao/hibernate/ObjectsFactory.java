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

package org.jtalks.antarcticle.model.dao.hibernate;

import org.hibernate.Session;
import org.joda.time.DateTime;
import org.jtalks.antarcticle.model.entity.Article;
import org.jtalks.antarcticle.model.entity.ArticleCollection;
import org.jtalks.antarcticle.model.entity.Comment;
import org.jtalks.jcommune.model.entity.User;

/**
 * @author Pavel Karpukhin
 * @author Dmitry Sokolov
 */
public final class ObjectsFactory {
    
    private static int increment = 0;

    private static Session session;

    private ObjectsFactory() {
    }

    public static void setSession(Session session) {
        ObjectsFactory.session = session;
    }

    public static ArticleCollection getDefaultArticleCollection() {
        ArticleCollection newArticleCollection = new ArticleCollection();
        newArticleCollection.setTitle("articleCollection title");
        newArticleCollection.setDescription("articleCollection description");
        return newArticleCollection;
    }

    public static Article getDefaultArticle() {
        Article article = new Article();
        article.setArticleTitle("article title");
        article.setArticleContent("article content");
        article.setCreationDate(new DateTime());
        article.setUserCreated(persist(getDefaultUser()));
        return article;
    }
    
    public static Comment getDefaultComment() {
        Comment comment = new Comment(new DateTime());
        comment.setCommentContent("comment content");
        comment.setUserCommented(persist(getDefaultUser()));
        comment.setArticle(persist(getDefaultArticle()));
        return comment;
    }
    
    // from jcommune ObjectsFactory   
    public static User getDefaultUser() {
        int val = increment++;
        return getUser("username"+val, "username"+val+"@mail.com");
    }

    public static User getUser(String username, String email) {
        User newUser = new User();
        newUser.setEmail(email);
        newUser.setFirstName("first name");
        newUser.setLastName("last name");
        newUser.setUsername(username);
        newUser.setPassword("password");
        return newUser;
    }
    
     private static <T> T persist(T entity) {
        session.save(entity);
        return entity;
    }
}
