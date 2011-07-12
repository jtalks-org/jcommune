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
import org.hibernate.SessionFactory;
import org.jtalks.antarcticle.model.dao.CommentDao;
import org.jtalks.antarcticle.model.entity.Comment;
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
public class CommentHibernateDaoTest extends AbstractTransactionalTestNGSpringContextTests {
    
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
    public void testGet() {
        Comment comment = ObjectsFactory.getDefaultComment();
        session.persist(comment);
        long id = comment.getId();
        Comment sameComment = dao.get(id);
        assertNotNull(sameComment);
        assertEquals(sameComment.getCommentContent(), comment.getCommentContent());
        assertEquals(sameComment.getCreationDate(), comment.getCreationDate());
        assertEquals(sameComment.getId(), id);
        assertEquals(sameComment.getArticle().getId(), comment.getArticle().getId());
        assertEquals(sameComment.getUserCommented().getId(), comment.getUserCommented().getId());
    }
    
    @Test
    public void testSave() {
        Comment comment = ObjectsFactory.getDefaultComment();
        dao.saveOrUpdate(comment);
        assertNotSame(comment.getId(), 0, "Id is not generated for comment");
        session.evict(comment);
        Comment sameComment = (Comment)session.get(Comment.class, comment.getId());
        assertReflectionEquals(comment, sameComment);
    }
    
    @Test
    public void testDelete() {
        int initialCount = getCount();
        Comment comment = ObjectsFactory.getDefaultComment();
        session.persist(comment);
        assertEquals(getCount(), initialCount+1);
        dao.delete(comment.getId());
        assertEquals(getCount(), initialCount);
    }
    
    private int getCount() {
        return ((Number) session.createQuery("select count(*) from Comment").uniqueResult()).intValue();
    }
}
