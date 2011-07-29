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
package org.jtalks.antarcticle.web.controller;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;
import org.joda.time.DateTime;
import org.jtalks.antarcticle.model.entity.Article;
import org.jtalks.antarcticle.model.entity.Comment;
import org.jtalks.antarcticle.service.ArticleService;
import org.jtalks.antarcticle.service.CommentService;
import org.jtalks.common.service.exceptions.NotFoundException;
import org.springframework.web.servlet.ModelAndView;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.ModelAndViewAssert.assertModelAttributeAvailable;
import static org.springframework.test.web.ModelAndViewAssert.assertViewName;
/**
 *
 * @author Dmitry Sokolov
 */
public class ArticleControllerTest {
    
    private static final long ARTICLE_ID = 12L;
    
    private ArticleService articleService;
    private CommentService commentService;
    private ArticleController articleController;
    
    @BeforeMethod
    public void init() {
        articleService = mock(ArticleService.class);
        commentService = mock(CommentService.class);
        articleController = new ArticleController(articleService, commentService);
    }
    
    @Test
    public void testDisplayArticle() throws NotFoundException {
        when(articleService.get(ARTICLE_ID)).thenReturn(getDefaultArticle());
        when(commentService.getCommentsByArticle(getDefaultArticle()))
            .thenReturn(getDefaultCommnetsList());
        
        ModelAndView mav = articleController.displayArticleWithComments(ARTICLE_ID);
        assertViewName(mav, "articleWithComments");
        assertModelAttributeAvailable(mav, "article");
        assertModelAttributeAvailable(mav, "comments");
        
        verify(articleService).get(ARTICLE_ID);
        verify(commentService).getCommentsByArticle(getDefaultArticle());
    }
    
    private Article getDefaultArticle() {
        Article article = new Article();
        article.setId(ARTICLE_ID);
        article.setArticleContent("Article content");
        article.setArticleTitle("Article Title");
        article.setCreationDate(new DateTime());
        article.setUuid("UUID article");
        return article;
    }
    
    private List<Comment> getDefaultCommnetsList() {
        List<Comment> result = new ArrayList<Comment>();
        Comment comment = new Comment();
        comment.setId(1L);
        comment.setCreationDate(new DateTime());
        comment.setCommentContent("Comment Content");
        comment.setArticle(getDefaultArticle());
        result.add(comment);
        comment = new Comment();
        comment.setId(2L);
        comment.setCreationDate(new DateTime());
        comment.setCommentContent("Comment Content 2");
        comment.setArticle(getDefaultArticle());
        result.add(comment);
        return result;
    }
    
}
