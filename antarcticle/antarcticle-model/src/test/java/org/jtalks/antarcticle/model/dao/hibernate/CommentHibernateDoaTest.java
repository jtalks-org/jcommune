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

import java.util.List;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.joda.time.DateTime;
import org.jtalks.antarcticle.model.dao.CommentDao;
import org.jtalks.antarcticle.model.entity.Article;
import org.jtalks.antarcticle.model.entity.Comment;
import org.jtalks.common.model.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.*;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;

/**
 *
 * @author Dmitry Sokolov
 */
@ContextConfiguration(locations = {"classpath:/org/jtalks/antarcticle/model/entity/applicationContext-dao.xml"})
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
@Transactional
public class CommentHibernateDoaTest extends AbstractTransactionalTestNGSpringContextTests {
    
    @Autowired
    private SessionFactory sessionFactory;
    @Autowired
    private CommentDao dao;
    private Session session;
    
    @BeforeMethod
    public void setUp() {
        session = sessionFactory.getCurrentSession();
        ObjectsFactory.setSession(session);
    }
    
    @Test
    public void testSave() {
        Comment comment = ObjectsFactory.getDefaultComment();
        dao.saveOrUpdate(comment);
        assertFalse(comment.getId() == 0);
        Long id = comment.getId();
        Comment sameComment = (Comment)session.get(Comment.class, id);
        assertNotNull(sameComment);
        assertReflectionEquals(comment, sameComment);
        assertEquals(comment.getId(), sameComment.getId());
        assertEquals(comment.getCommentContent(), sameComment.getCommentContent());
        assertEquals(comment.getCreationDate(), sameComment.getCreationDate());
        assertEquals(comment.getUserCommented().getId(), sameComment.getUserCommented().getId());
        assertEquals(comment.getArticle().getId(), sameComment.getArticle().getId());
    }
    
    @Test
    public void testGet() {
        Comment comment = ObjectsFactory.getDefaultComment();
        session.save(comment);
        assertFalse(comment.getId() == 0);
        Long id = comment.getId();
        Comment sameComment = dao.get(id);
        assertNotNull(sameComment);
        assertReflectionEquals(comment, sameComment);
    }

    @Test
    public void testDelete() {
        int initialCount = getCount();
        Comment comment = ObjectsFactory.getDefaultComment();
        session.save(comment);
        Long id1 = comment.getId();
        session.evict(comment);
        comment = ObjectsFactory.getDefaultComment();
        session.save(comment);
        Long id2 = comment.getId();
        session.evict(comment);
        assertEquals(getCount(), initialCount+2);
        assertTrue(dao.isExist(id1));
        assertTrue(dao.isExist(id2));
        assertTrue(dao.delete(id1));
        assertTrue(dao.delete(id2));
        assertEquals(getCount(), initialCount);
        
//        assertFalse(dao.isExist(id1));
//        assertFalse(dao.isExist(id2));
    }
    
    @Test
    public void testUpdate() {
        String newContent = "New Content";
        DateTime newDate = new DateTime();
        Comment comment = ObjectsFactory.getDefaultComment();
        session.save(comment);
        assertNotSame(comment.getId(), 0);
        Long id = comment.getId();
        session.evict(comment);
        Comment updatedComment = (Comment)session.get(Comment.class, id);
        updatedComment.setCommentContent(newContent);
        updatedComment.setCreationDate(newDate);
        dao.saveOrUpdate(updatedComment);
        Comment sameComment = (Comment)session.get(Comment.class, id);
        assertReflectionEquals(sameComment, updatedComment);
        assertFalse(comment.getCommentContent().equals(sameComment.getCommentContent()));
    }
    
    @Test
    public void testFindByArticle() {
        Comment comment = ObjectsFactory.getDefaultComment();
        Article article = comment.getArticle();
        session.save(comment);
        Long id1 = comment.getId();
        comment = ObjectsFactory.getDefaultComment();
        session.save(comment);
        Long id2 = comment.getId();
        assertTrue(dao.isExist(id1));
        assertTrue(dao.isExist(id2));
        List<Comment> result = dao.findByArticle(article);
        assertEquals(result.size(), 1);
        comment.setArticle(article);
        session.save(comment);
        result = dao.findByArticle(article);
        assertEquals(result.size(), 2);
    }
    
    @Test
    public void testGetByArticle() {
        Article article = ObjectsFactory.getDefaultArticle();
        session.save(article);
        assertTrue(article.getId() != 0);
        User user = article.getUserCreated();
        assertTrue(user.getId() != 0);
        Comment comment = new Comment();
        comment.setArticle(article);
        comment.setCreationDate(new DateTime());
        comment.setUserCommented(user);
        comment.setUuid("UUID-12");
        comment.setCommentContent("Comment content");
        session.save(comment);
        long id1 = comment.getId();
        comment = new Comment(user, new DateTime(), "Comment contents 2", article);
        comment.setUuid("dsds");
        session.save(comment);
        long id2 = comment.getId();
        List<Comment> comments = dao.findByArticle(article);
        assertNotNull(comments);
        assertEquals(comments.size(), 2);
        assertTrue(comments.get(0).getId() == id1 || comments.get(0).getId() == id2);
        assertTrue(comments.get(1).getId() == id1 || comments.get(1).getId() == id2);
    }
        
    private int getCount() {
        return ((Number) session.createQuery("select count(*) from Comment").uniqueResult()).intValue();
    }
    
}
