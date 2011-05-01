/*
 * JTalks for uniting people
 * Copyright (C) 2011  JavaTalks Team
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 * Also add information on how to contact you by electronic and paper mail.
 *
 * This file creation date: Apr 12, 2011 / 8:05:19 PM
 * The JTalks Project
 * http://www.jtalks.org
 */
package org.jtalks.jcommune.model.dao.hibernate;

import java.util.Collection;
import org.hibernate.SessionFactory;
import org.jtalks.jcommune.model.entity.Persistent;
import org.jtalks.jcommune.model.entity.Topic;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.annotation.Resource;
import java.util.List;
import org.jtalks.jcommune.model.entity.Post;
import org.jtalks.jcommune.model.entity.User;

/**
 * DAO tests for instance of {@link TopicHibernateDao}
 *
 * @author Artem Mamchych
 */
public class TopicHibernateDaoTest extends BaseTest {
    public static final String LOADED_USER_ERROR = "Loaded user is not the same as it was saved";
    public static final String TOPIC_POSTS_ERROR = "Topic contains wrong collection of posts";
    public static final String USER_IS_NULL = "Topic.userCreated is null";

    /** Hibernate Session Factory instance. */
    @Resource(name = "sessionFactory")
    private SessionFactory sessionFactory;
    private TopicHibernateDao dao;
    private Topic entity;
    private List<Topic> listAll;
    private UserHibernateDao userHibernateDao;
    private PostHibernateDao postHibernateDao;

    @BeforeMethod
    public void setUp() throws Exception {
        dao = new TopicHibernateDao();
        dao.setSessionFactory(sessionFactory);
        Assert.assertNotNull(sessionFactory, SESSION_FACTORY_IS_NULL);
        entity = new Topic();
        entity.setTitle("TopicName");
        entity.setTopicStarter(null);

        clearDbTable(entity, sessionFactory);
    }

    @AfterMethod
    public void tearDown() throws Exception {
        entity = null;
    }

    @Test
    public void testEntityState() throws Exception {
        testSave();
        listAll = dao.getAll();
        Assert.assertTrue(entity.equals(listAll.get(0)), PERSISTENCE_ERROR);
        Assert.assertFalse(entity.equals(listAll.get(1)), PERSISTENCE_ERROR);
    }

    @Test
    public void testDBEmpty() throws Exception {
        int sizeBefore = dao.getAll().size();
        Assert.assertEquals(0, sizeBefore, DB_TABLE_NOT_EMPTY);
    }

    @Test
    public void testSave() throws Exception {
        //Add 2 Topics to DB
        dao.saveOrUpdate(entity);
        dao.saveOrUpdate(new Topic());

        int size = dao.getAll().size();
        Assert.assertEquals(2, size, ENTITIES_IS_NOT_INCREASED_BY_2);
    }

    @Test
    public void testDeleteById() throws Exception {
        testSave();
        listAll = dao.getAll();
        int size = listAll.size();
        Assert.assertEquals(2, size, DB_MUST_BE_NOT_EMPTY);

        for (Persistent p : listAll) {
            dao.delete(p.getId());
        }
        testDBEmpty();
    }

    @Test
    public void testGetById() throws Exception {
        testSave();
        listAll = dao.getAll();
        int size = listAll.size();
        Assert.assertEquals(2, size, DB_MUST_BE_NOT_EMPTY);

        for (Persistent p : listAll) {
            dao.delete(dao.get(p.getId()));
        }
        testDBEmpty();
    }

    @Test
    public void testGetAll() throws Exception {
        dao.saveOrUpdate(entity);

        int size = dao.getAll().size();
        Assert.assertEquals(1, size, ENTITIES_IS_NOT_INCREASED_BY_1);
    }

    @Test
    public void testUpdate() throws Exception {
        dao.saveOrUpdate(entity);
        dao.saveOrUpdate(entity);
        dao.saveOrUpdate(entity);

        int size = dao.getAll().size();
        Assert.assertEquals(1, size, ENTITIES_IS_NOT_INCREASED_BY_1);
    }

    @Test
    public void testGetTopicWithUser() throws Exception {
        User user = new User();
        user.setNickName("NickName");
        getUserDao().saveOrUpdate(user);
        entity.setTopicStarter(user);
        dao.saveOrUpdate(entity);
        Long topicId = entity.getId();
        Topic loadedTopic = dao.getTopicWithUser(topicId);
        Assert.assertNotNull(loadedTopic.getTopicStarter(), USER_IS_NULL);
        Assert.assertEquals(user, loadedTopic.getTopicStarter(), LOADED_USER_ERROR);
    }

    @Test
    public void testGetTopicWithPosts() throws Exception {
        User user = new User();
        user.setNickName("TestNickName");
        getUserDao().saveOrUpdate(user);
        Post post1 = new Post();
        post1.setPostContent("content1");
        Post post2 = new Post();
        post2.setPostContent("content2");
        getPostDao().saveOrUpdate(post1);
        getPostDao().saveOrUpdate(post2);
        entity.addPost(post1);
        entity.addPost(post2);
        Collection allPosts = entity.getPosts();
        entity.setTopicStarter(user);
        dao.saveOrUpdate(entity);
        Long topicId = entity.getId();
        Topic loadedTopic = dao.getTopicWithPosts(topicId);
        Assert.assertEquals(allPosts, loadedTopic.getPosts(), TOPIC_POSTS_ERROR);
    }

    private UserHibernateDao getUserDao() {
        if (userHibernateDao == null) {
            userHibernateDao = new UserHibernateDao();
            userHibernateDao.setSessionFactory(sessionFactory);
        }
        return userHibernateDao;
    }

    private PostHibernateDao getPostDao() {
        if (postHibernateDao == null) {
            postHibernateDao = new PostHibernateDao();
            postHibernateDao.setSessionFactory(sessionFactory);
        }
        return postHibernateDao;
    }
}
