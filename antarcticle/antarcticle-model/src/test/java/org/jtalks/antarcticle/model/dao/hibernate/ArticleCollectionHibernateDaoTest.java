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
import org.jtalks.antarcticle.model.dao.ArticleCollectionDao;
import org.jtalks.antarcticle.model.entity.Article;
import org.jtalks.antarcticle.model.entity.ArticleCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.*;
import static org.testng.Assert.assertEquals;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;

/**
 * @author Pavel Karpukhin
 */
@ContextConfiguration(locations = {"classpath:/org/jtalks/antarcticle/model/entity/applicationContext-dao.xml"})
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
@Transactional
public class ArticleCollectionHibernateDaoTest extends AbstractTransactionalTestNGSpringContextTests {

    @Autowired
    private SessionFactory sessionFactory;
    @Autowired
    private ArticleCollectionDao dao;
    private Session session;

    @BeforeMethod
    public void setUp() {
        session = sessionFactory.getCurrentSession();
        ObjectsFactory.setSession(session);
    }

    @Test
    public void testSave() {
         ArticleCollection articleCollection = ObjectsFactory.getDefaultArticleCollection();

        dao.saveOrUpdate(articleCollection);

        assertNotSame(articleCollection.getId(), 0, "Id not created");
    }

    @Test(expectedExceptions = DataIntegrityViolationException.class)
    public void testSaveNotNullViolation() {
        ArticleCollection articleCollection = new ArticleCollection();

        dao.saveOrUpdate(articleCollection);

        session.evict(articleCollection);
        ArticleCollection result = (ArticleCollection) session.get(ArticleCollection.class, articleCollection.getId());

        assertReflectionEquals(articleCollection, result);
    }

    @Test
    public void testGet() {
        ArticleCollection articleCollection = ObjectsFactory.getDefaultArticleCollection();
        session.save(articleCollection);

        ArticleCollection result = dao.get(articleCollection.getId());

        assertNotNull(result);
        assertEquals(result.getId(), articleCollection.getId());
    }

    @Test
    public void testGetInvalidId() {
        ArticleCollection result = dao.get(-567890L);

        assertNull(result);
    }

    @Test
    public void testUpdate() {
        String newTitle = "new title";
        ArticleCollection articleCollection = ObjectsFactory.getDefaultArticleCollection();
        session.save(articleCollection);

        articleCollection.setTitle(newTitle);
        dao.saveOrUpdate(articleCollection);

        session.evict(articleCollection);
        ArticleCollection result = (ArticleCollection)session.get(ArticleCollection.class, articleCollection.getId());
        assertReflectionEquals(articleCollection, result);
    }

    @Test(expectedExceptions = DataIntegrityViolationException.class)
    public void testUpdateNotNullViolation() {
        ArticleCollection articleCollection = ObjectsFactory.getDefaultArticleCollection();
        session.save(articleCollection);

        articleCollection.setTitle(null);
        dao.saveOrUpdate(articleCollection);
    }

    @Test
    public void testDelete() {
        ArticleCollection articleCollection = ObjectsFactory.getDefaultArticleCollection();

        session.save(articleCollection);

        session.evict(articleCollection);
        boolean deleted = dao.delete(articleCollection.getId());

        int articleCollectionCount = getCount();

        assertTrue(deleted);
        assertEquals(articleCollectionCount, 0);
    }

    @Test
    public void testDeleteInvalidId() {
        boolean result = dao.delete(-100500L);

        assertFalse(result, "Entity deleted");
    }

    @Test
    public void testDeleteWithArticles() {
        ArticleCollection articleCollection = ObjectsFactory.getDefaultArticleCollection();
        articleCollection.addArticle(ObjectsFactory.getDefaultArticleWithoutArticleCollection());

        Article article = ObjectsFactory.getDefaultArticleWithoutArticleCollection();
        articleCollection.addArticle(article);
        articleCollection.setLastArticle(article);

        session.save(articleCollection);
        session.flush();

        session.evict(articleCollection);
        boolean deleted = dao.delete(articleCollection.getId());

        session.flush();

        int articleCollectionCount = getCount();

        int articleCount = ((Number)session.createQuery("select count(*) from Article as article where article.articleCollection.id = " + articleCollection.getId()).uniqueResult()).intValue();

        int allArticleCount = ((Number)session.createQuery("select count(*) from Article").uniqueResult()).intValue();

        assertTrue(deleted);
        assertEquals(articleCollectionCount, 0);
        assertEquals(articleCount, 0);
        assertEquals(allArticleCount, 2);
    }

    @Test
    public void testGetAll() {
        ArticleCollection articleCollection1 = ObjectsFactory.getDefaultArticleCollection();
        session.save(articleCollection1);
        ArticleCollection articleCollection2 = ObjectsFactory.getDefaultArticleCollection();
        session.save(articleCollection2);

        List<ArticleCollection> articleCollections = dao.getAll();

        assertEquals(articleCollections.size(), 2);
    }

    private int getCount() {
        return ((Number) session.createQuery("select count(*) from ArticleCollection").uniqueResult()).intValue();
    }
}
