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
import org.jtalks.antarcticle.model.dao.ArticleDao;
import org.jtalks.antarcticle.model.dao.CommentDao;
import org.jtalks.antarcticle.model.entity.Article;
import org.jtalks.antarcticle.model.entity.Comment;
import org.jtalks.antarcticle.service.CommentService;
import org.jtalks.common.service.exceptions.NotFoundException;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Mockito.*;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.jtalks.antarcticle.service.transactional.ObjectFactory.*;
/**
 *
 * @author Dmitry Sokolov
 */
public class TransactionalCommentServiceTest {
        
    private CommentDao commentDao;
    private CommentService commentService;
    private ArticleDao articleDao;
    
    @BeforeMethod
     public void setUp() throws Exception {
        commentDao = mock(CommentDao.class);
        articleDao = mock(ArticleDao.class);
        commentService = new TransactionalCommentService(commentDao, articleDao);
    }
    
    @Test
    public void testGetComment() throws NotFoundException {
        Comment comment = getDefualtComment();
        comment.setId(COMMENT_ID);
        
        when(commentDao.isExist(COMMENT_ID)).thenReturn(true);
        when(commentDao.get(COMMENT_ID)).thenReturn(comment);
        
        Comment resultComment = commentService.get(COMMENT_ID);
        assertNotNull(resultComment);
        assertEquals(resultComment.getId(), comment.getId());
        assertEquals(resultComment.getCommentContent(), comment.getCommentContent());
        
        verify(commentDao).isExist(COMMENT_ID);
        verify(commentDao).get(COMMENT_ID);
    }
    
    @Test
    public void testAddComment() {
        Comment comment = getDefualtComment();
        assertEquals(comment.getId(), 0);
        
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                Comment c = (Comment)args[0];
                c.setId(COMMENT_ID);
                return null;
            }
        }).when(commentDao).saveOrUpdate(comment);
        
        commentService.addComment(comment);
        assertEquals(comment.getId(), COMMENT_ID);
        
        verify(commentDao).saveOrUpdate(comment);
    }
    
    @Test
    public void testCreateComment() {
        Article article = getDefaultAricle();
        article.setId(ARTICLE_ID);
        Comment comment = commentService.createComment(article, getDefaultUser());
        assertNotNull(comment);
        assertNotNull(comment.getCreationDate());
        assertEquals(comment.getArticle().getId(), article.getId());
        assertEquals(comment.getUserCommented().getId(), getDefaultUser().getId());
        assertEquals(comment.getId(), 0);
    }
    
    @Test
    public void testDeleteComment() throws NotFoundException {
        Comment comment = getDefualtComment();
        comment.setId(COMMENT_ID);
        
        when(commentDao.isExist(COMMENT_ID)).thenReturn(true);
        when(commentDao.delete(COMMENT_ID)).thenReturn(true);
        
        commentService.deleteComment(comment);
        verify(commentDao).isExist(COMMENT_ID);
        verify(commentDao).delete(COMMENT_ID);
    }
    
    @Test(expectedExceptions=NotFoundException.class)
    public void testDeleteNotSavedComment() throws NotFoundException {
        Comment comment = getDefualtComment();
        comment.setId(COMMENT_ID);
        
        when(commentDao.isExist(COMMENT_ID)).thenReturn(false);
        
        commentService.deleteComment(comment);
        verify(commentDao).isExist(COMMENT_ID);
    }
    
    @Test
    public void testGetCommentsByArticle() throws NotFoundException {
        Article article = getDefaultAricle();
        article.setId(ARTICLE_ID);
        assertEquals(article.getId(), ARTICLE_ID);
        when(articleDao.isExist(ARTICLE_ID)).thenReturn(true);
        when(commentDao.findByArticle(article)).thenReturn(getDefaultComments());
        
        List<Comment> result = commentService.getCommentsByArticle(article);
        assertNotNull(result);
        assertEquals(result.size(), getDefaultComments().size());
        
        verify(articleDao).isExist(ARTICLE_ID);
        verify(commentDao).findByArticle(article);
    }
    
    @Test(expectedExceptions=NotFoundException.class)
    public void testGetCommentsByNotSavedArticle() throws NotFoundException {
        Article article = getDefaultAricle();
        article.setId(ARTICLE_ID);
        assertEquals(article.getId(), ARTICLE_ID);
        when(articleDao.isExist(ARTICLE_ID)).thenReturn(false);
        
        List<Comment> result = commentService.getCommentsByArticle(article);
        verify(articleDao.isExist(ARTICLE_ID));
    }
    
    @Test(expectedExceptions=NotFoundException.class)
    public void testDeleteCommentWithoutId() throws NotFoundException {
        Comment comment = getDefualtComment();
        assertEquals(comment.getId(), 0);
        commentService.deleteComment(comment);
    }
    
    @Test(expectedExceptions=NotFoundException.class)
    public void testFindCommentsByViolanteArticle() throws NotFoundException {
        Article article = getDefaultAricle();
        assertEquals(article.getId(), 0);
        commentService.getCommentsByArticle(article);
    }
}
