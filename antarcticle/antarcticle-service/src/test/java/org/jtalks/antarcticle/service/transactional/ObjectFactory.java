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

import java.util.ArrayList;
import java.util.List;
import org.joda.time.DateTime;
import org.jtalks.antarcticle.model.entity.Article;
import org.jtalks.antarcticle.model.entity.ArticleCollection;
import org.jtalks.antarcticle.model.entity.Comment;
import org.jtalks.jcommune.model.entity.User;

/**
 *
 * @author Dmitry Sokolov
 */
public class ObjectFactory {
    
    public static long ARTICLE_ID = 33L;
    public static long USER_ID = 11L;
    public static long ARTICL_COL_ID = 12L;   
    public static long COMMENT_ID = 16L;
    
    public static Article getDefaultAricle() {
        Article article = new Article();
        article.setArticleContent("article conetent");
        article.setCreationDate(new DateTime());
        article.setArticleTitle("article title");
        return article;
    }
    
    public static User getDefaultUser() {
        User user = new User();
        user.setId(USER_ID);
        user.setFirstName("FirstName");
        user.setLastName("LastName");
        user.setPassword("password1");
        user.setUsername("fakeuser");
        user.setEmail("user@jtalks.org");
        user.setRole("ADminRole");
        return user;
    }      
    
    public static ArticleCollection getDefaultArticleCollection() {
        ArticleCollection ac = new ArticleCollection();
        ac.setTitle("Article Collection Title");
        ac.setId(ARTICL_COL_ID);
        ac.setDescription("Article Collection Descr");
        return ac;
    }
    
    public static Comment getDefualtComment() {
        Comment comment = new Comment();
        comment.setArticle(getDefaultAricle());
        comment.setCommentContent("Comment Content");
        comment.setCreationDate(new DateTime());
        comment.setUserCommented(getDefaultUser());
        return comment;
    }
    
    public static List<Comment> getDefaultComments() {
        List<Comment> comments = new ArrayList<Comment>();
        comments.add(getDefualtComment());
        return comments;
    }
    
}
