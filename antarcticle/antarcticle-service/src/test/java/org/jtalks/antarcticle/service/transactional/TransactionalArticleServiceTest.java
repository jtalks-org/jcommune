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

import org.jtalks.antarcticle.model.dao.ArticleDao;
import org.jtalks.antarcticle.model.entity.Article;
import org.jtalks.antarcticle.service.ArticleService;
import org.jtalks.jcommune.service.exceptions.NotFoundException;
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
public class TransactionalArticleServiceTest {
    
    private ArticleService articleService;
    private ArticleDao articleDao;
    
    @BeforeMethod
     public void setUp() throws Exception {
        articleDao = mock(ArticleDao.class);
        articleService = new TransactionalArticleService(articleDao);
    }
    
    @Test
    public void testGet() throws NotFoundException {
        Article article = ObjectFactory.getDefaultAricle();
        article.setId(ARTICLE_ID);
        
        when(articleDao.isExist(ARTICLE_ID)).thenReturn(true);
        when(articleDao.get(ARTICLE_ID)).thenReturn(article);
        
        Article resultArticle = articleService.get(ARTICLE_ID);
        
        assertNotNull(resultArticle);
        assertEquals(resultArticle.getId(), ARTICLE_ID);
        assertEquals(resultArticle.getArticleContent(), article.getArticleContent());
        
        verify(articleDao).get(ARTICLE_ID);
        verify(articleDao).isExist(ARTICLE_ID);
    }
    
    @Test
    public void testAddArticle() {
        Article article = getDefaultAricle();
        assertEquals(article.getId(), 0);
        
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                Article a = (Article)args[0];
                a.setId(ARTICLE_ID);
                return null; 
            }
        }).when(articleDao).saveOrUpdate(article);
        
        articleService.addArticle(article);
        assertEquals(article.getId(), ARTICLE_ID);
        
        verify(articleDao).saveOrUpdate(article);
    }
    
    @Test
    public void testCreateArticle() throws NotFoundException {
        Article article = articleService
                .createArticle(getDefaultArticleCollection(), getDefaultUser());
        assertNotNull(article);
        assertEquals(article.getUserCreated().getId(), getDefaultUser().getId());
        assertEquals(article.getArticleCollection().getId(), getDefaultArticleCollection().getId());
        assertNotNull(article.getCreationDate());
        assertEquals(article.getId(), 0);
    }
    
    @Test
    public void testDeleteArticle() throws NotFoundException {
        Article article = getDefaultAricle();
        article.setId(ARTICLE_ID);
        
        when(articleDao.isExist(ARTICLE_ID)).thenReturn(true);
        when(articleDao.delete(ARTICLE_ID)).thenReturn(true);
        
        articleService.deleteArticle(article);
        
        verify(articleDao).isExist(ARTICLE_ID);
        verify(articleDao).delete(ARTICLE_ID);
    }
    
    
    @Test(expectedExceptions=NotFoundException.class)
    public void testDeleteNotSavedArticle() throws NotFoundException {
        Article notSavedArticle = getDefaultAricle();
        assertEquals(notSavedArticle.getId(), 0);
        notSavedArticle.setId(ARTICLE_ID);
        
        when(articleDao.isExist(ARTICLE_ID)).thenReturn(false);
        articleService.deleteArticle(notSavedArticle);
        
        verify(articleDao).isExist(ARTICLE_ID);
    }
}
