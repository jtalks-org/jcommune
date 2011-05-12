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
package org.jtalks.jcommune.model.dao.hibernate;

import org.hibernate.SessionFactory;
import org.jtalks.jcommune.model.entity.Persistent;
import org.jtalks.jcommune.model.entity.Topic;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;
import org.jtalks.jcommune.model.dao.PostDao;
import org.jtalks.jcommune.model.dao.TopicDao;
import org.jtalks.jcommune.model.dao.UserDao;
import org.jtalks.jcommune.model.entity.Post;
import org.jtalks.jcommune.model.entity.User;
import org.springframework.beans.factory.annotation.Autowired;

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
    @Autowired
    private SessionFactory sessionFactory;
    @Autowired
    private TopicDao dao;
    @Autowired
    private UserDao userDao;
    @Autowired
    private PostDao postDao;
    private Topic entity;
    private Post testPost;
    private User testUser;
    private List<Topic> listAll;
    private User dummyUser;

    @BeforeMethod
    public void setUp() throws Exception {
        entity = Topic.createNewTopic();
        clearDbTable(entity, sessionFactory);

        initUser();
        initPost();
        Assert.assertNotNull(testPost);
        entity.setTitle("TopicName");
        entity.addPost(testPost);
        entity.setTopicStarter(testUser);

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
        int size = dao.getAll().size();
        Assert.assertEquals(1, size, ENTITIES_IS_NOT_INCREASED_BY_2);
    }

    @Test
    public void testDeleteById() throws Exception {
        testSave();
        listAll = dao.getAll();
        int size = listAll.size();
        Assert.assertEquals(1, size, DB_MUST_BE_NOT_EMPTY);

        for (Persistent p : listAll) {
            dao.delete(p.getId());
        }
        testDBEmpty();
    }

    @Test
    public void testAddSomePosts() throws Exception {
        Topic topic = getNewTopic();
        Post firstPost = createDummyPost();
        topic.addPost(firstPost);
        dao.saveOrUpdate(topic);
        Assert.assertEquals(topic.getPosts().size(), 1, "More than 1 posts in the topic! " + topic.getPosts().size());
        Assert.assertEquals(topic.getPosts().get(0), firstPost, "The first post of the topic loaded or saved incorrectly");
        Assert.assertEquals(dao.getAll().size(), 1, "More than 1 Topic in the DB! " + dao.getAll().size());
        Post secondPost = createDummyPost();
        Topic loadedTopic = dao.get(topic.getId());
        loadedTopic.addPost(secondPost);
        dao.saveOrUpdate(topic);
        loadedTopic = dao.get(loadedTopic.getId());
        List loadedPosts = loadedTopic.getPosts();
        int postsNumber = loadedPosts.size();
        int topicsNumber = dao.getAll().size();
        Assert.assertEquals(postsNumber, 2, "The Topic should contains 2 posts but there are " + postsNumber);
        Assert.assertEquals(loadedPosts.get(0), firstPost, "The first post of the topic loaded or saved incorrectly");
        Assert.assertEquals(loadedPosts.get(1), secondPost, "The second post of the topic loaded or saved incorrectly");
        Assert.assertEquals(topicsNumber, 1, "More than 1 Topic in the DB! " + topicsNumber);        
    }

    public Post createDummyPost() {
        Post post = Post.createNewPost();
        post.setUserCreated(getDummyUser());
        post.setPostContent("Post content " + post.getCreationDate());
        return post;
    }
    
    public User getDummyUser() {
        if (this.dummyUser == null) {
            dummyUser = new User();
            dummyUser.setFirstName("Dummy FNM");
            dummyUser.setLastName("Dummy LNM");
            dummyUser.setNickName("Dummy Nick");
            userDao.saveOrUpdate(dummyUser);
        }
        return this.dummyUser;
    }
    
    @Test
    public void testGetById() throws Exception {
        testSave();
        listAll = dao.getAll();
        int size = listAll.size();
        Assert.assertEquals(1, size, DB_MUST_BE_NOT_EMPTY);

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
    public void testGetTopicWithPosts() throws Exception {
        testSave();
        Topic topic = dao.getTopicWithPosts(entity.getId());
        List postst = topic.getPosts();
        Assert.assertEquals(postst.size(), 1);
    }

    private void initPost() {
        Post post = Post.createNewPost();
        post.setUserCreated(testUser);
        post.setPostContent("Test content");
        postDao.saveOrUpdate(post);
        this.testPost = post;
    }

    private void initUser() {
        User user = new User();
        user.setFirstName("FNM");
        user.setLastName("LNM");
        user.setNickName("TestNickname");
        userDao.saveOrUpdate(user);
        this.testUser = user;
    }

    private Topic getNewTopic() {
        Topic topic = Topic.createNewTopic();
        topic.setTitle("Topic Title");
        topic.setTopicStarter(getDummyUser());
        return topic;
    }
}
