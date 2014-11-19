/**
 * Copyright (C) 2011  JTalks.org Team
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
 */
package org.jtalks.jcommune.model.dao.hibernate;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.exception.ConstraintViolationException;
import org.jtalks.common.model.dao.Crud;
import org.jtalks.jcommune.model.entity.CommentProperty;
import org.jtalks.jcommune.model.entity.PersistedObjectsFactory;
import org.jtalks.jcommune.model.entity.PostComment;
import org.jtalks.jcommune.model.entity.PropertyType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

@ContextConfiguration(locations = {"classpath:/org/jtalks/jcommune/model/entity/applicationContext-dao.xml"})
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
@Transactional
public class PostCommentHibernateDaoTest extends AbstractTransactionalTestNGSpringContextTests {

    @Autowired
    private SessionFactory sessionFactory;
    @Autowired
    private Crud<PostComment> postCommentDao;
    private Session session;

    @BeforeMethod
    public void setUp() throws Exception {
        session = sessionFactory.getCurrentSession();
        PersistedObjectsFactory.setSession(session);
    }
    
    /*===== Common methods =====*/

    @Test
    public void testGet() {
        PostComment comment = PersistedObjectsFactory.getDefaultPostComment();
        session.save(comment);
        flushAndClearSession();

        PostComment result = postCommentDao.get(comment.getId());

        assertNotNull(result);
        assertEquals(result.getId(), comment.getId());
        assertEquals(result.getBody(), comment.getBody());
        assertEquals(result.getCreationDate(), comment.getCreationDate());
        assertEquals(result.getAuthor(), comment.getAuthor());
    }


    @Test
    public void testGetInvalidId() {
        PostComment result = postCommentDao.get(-567890L);

        assertNull(result);
    }

    @Test
    public void testUpdate() {
        String newUuid = "1234-1231-1231";
        PostComment review = PersistedObjectsFactory.getDefaultPostComment();
        session.save(review);
        review.setUuid(newUuid);

        postCommentDao.saveOrUpdate(review);
        session.flush();
        session.evict(review);
        PostComment result = (PostComment) session.get(PostComment.class, review.getId());

        assertEquals(result.getUuid(), newUuid);
    }

    @Test(expectedExceptions = org.hibernate.exception.ConstraintViolationException.class)
    public void testUpdateNotNullViolation() {
        PostComment review = PersistedObjectsFactory.getDefaultPostComment();
        session.save(review);
        review.setUuid(null);
        postCommentDao.saveOrUpdate(review);
        session.flush();
    }

    @Test
    public void testSaveCommentWithCustomProperties() {
        CommentProperty property = new CommentProperty("name", PropertyType.STRING, "value");
        PostComment comment = PersistedObjectsFactory.getDefaultPostComment();
        comment.addCustomProperty(property);
        postCommentDao.saveOrUpdate(comment);
        flushAndClearSession();

        PostComment result = (PostComment)session.get(PostComment.class, comment.getId());

        assertEquals(result.getCustomProperties().size(), comment.getCustomProperties().size());
    }

    @Test
    public void commentPropertiesShouldBeUpdatedByCascade() {
        PostComment comment = PersistedObjectsFactory.getCommentWithProperties();
        postCommentDao.saveOrUpdate(comment);
        String updatedValue = "updatedValue";
        comment.getCustomProperties().get(0).setValue(updatedValue);

        postCommentDao.saveOrUpdate(comment);
        flushAndClearSession();

        PostComment result = (PostComment)session.get(PostComment.class, comment.getId());

        assertEquals(result.getCustomProperties().get(0).getValue(), updatedValue);
    }

    @Test(expectedExceptions = ConstraintViolationException.class)
    public void testCascadeUpdateNotNulViolation() {
        PostComment comment = PersistedObjectsFactory.getCommentWithProperties();
        postCommentDao.saveOrUpdate(comment);
        comment.getCustomProperties().get(0).setValue(null);

        postCommentDao.saveOrUpdate(comment);
        flushAndClearSession();
    }

    @Test
    public void commentPropertiesShouldBeRemovedIfCommentRemoved() {
        PostComment comment = PersistedObjectsFactory.getCommentWithProperties();
        postCommentDao.saveOrUpdate(comment);
        flushAndClearSession();

        postCommentDao.delete(comment);

        CommentProperty property = (CommentProperty)session.get(CommentProperty.class,
                comment.getCustomProperties().get(0).getId());

        assertNull(property);
    }

    @Test(expectedExceptions = DataIntegrityViolationException.class)
    public void itShouldBeImpossibleToSaveCommentWithTwoIdenticalProperties() {
        CommentProperty property1 = new CommentProperty("name", PropertyType.STRING, "value1");
        CommentProperty property2 = new CommentProperty("name", PropertyType.STRING, "value1");
        PostComment comment = PersistedObjectsFactory.getDefaultPostComment();
        comment.addCustomProperty(property1);
        comment.addCustomProperty(property2);

        postCommentDao.saveOrUpdate(comment);
        flushAndClearSession();
    }

    private void flushAndClearSession() {
        session.flush();
        session.clear();
    }
}
