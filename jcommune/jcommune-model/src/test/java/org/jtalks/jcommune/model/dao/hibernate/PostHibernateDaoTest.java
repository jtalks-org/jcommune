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

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.jtalks.jcommune.model.dao.PostDao;
import org.jtalks.jcommune.model.entity.Post;
import org.jtalks.jcommune.model.entity.Topic;
import org.jtalks.jcommune.model.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.*;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;

/**
 * @author Kirill Afonin
 */
@ContextConfiguration(locations = {"classpath:/org/jtalks/jcommune/model/entity/applicationContext-dao.xml"})
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
@Transactional
public class PostHibernateDaoTest extends AbstractTransactionalTestNGSpringContextTests {

    @Autowired
    private SessionFactory sessionFactory;
    @Autowired
    private PostDao dao;
    private Session session;

    @BeforeMethod
    public void setUp() {
        session = sessionFactory.getCurrentSession();
        ObjectsFactory.setSession(session);
    }

    /*===== Common methods =====*/

    @Test
    public void testSave() {
        Post post = ObjectsFactory.getDefaultPost();

        dao.saveOrUpdate(post);

        assertNotSame(post.getId(), 0, "Id not created");

        session.evict(post);
        Post result = (Post) session.get(Post.class, post.getId());

        assertReflectionEquals(post, result);
    }

    @Test(expectedExceptions = DataIntegrityViolationException.class)
    public void testSavePostWithDateNotNullViolation() {
        Post post = new Post();

        dao.saveOrUpdate(post);
    }

    @Test
    public void testGet() {
        Post post = ObjectsFactory.getDefaultPost();
        session.save(post);

        Post result = dao.get(post.getId());

        assertNotNull(result);
        assertEquals(result.getId(), post.getId());
    }

    @Test
    public void testGetInvalidId() {
        Post result = dao.get(-567890L);

        assertNull(result);
    }

    @Test
    public void testUpdate() {
        String newContent = "new content";
        Post post = ObjectsFactory.getDefaultPost();
        session.save(post);
        post.setPostContent(newContent);

        dao.saveOrUpdate(post);
        session.evict(post);
        Post result = (Post) session.get(Post.class, post.getId());

        assertEquals(result.getPostContent(), newContent);
    }

    @Test(expectedExceptions = DataIntegrityViolationException.class)
    public void testUpdateNotNullViolation() {
        Post post = ObjectsFactory.getDefaultPost();
        session.save(post);
        post.setUserCreated(null);

        dao.saveOrUpdate(post);
    }

    @Test
    public void testDelete() {
        Post post = ObjectsFactory.getDefaultPost();
        session.save(post);

        boolean result = dao.delete(post.getId());
        int postCount = getCount();

        assertTrue(result, "Entity is not deleted");
        assertEquals(postCount, 0);
    }

    @Test
    public void testDeleteInvalidId() {
        boolean result = dao.delete(-100500L);

        assertFalse(result, "Entity deleted");
    }

    @Test
    public void testGetAll() {
        Post post1 = ObjectsFactory.getDefaultPost();
        session.save(post1);
        User post2Author = ObjectsFactory.getUser("user2", "user2@mail.com");
        session.save(post2Author);
        Post post2 = ObjectsFactory.getPost(post2Author);
        session.save(post2);

        List<Post> posts = dao.getAll();

        assertEquals(posts.size(), 2);
    }

    @Test
    public void testGetAllWithEmptyTable() {
        List<Post> posts = dao.getAll();

        assertTrue(posts.isEmpty());
    }

    private List<Post> createAndSavePostList(int size) {
        List<Post> posts = new ArrayList<Post>();
        Topic topic = ObjectsFactory.getDefaultTopic();
        session.save(topic);
        User author = topic.getTopicStarter();
        for (int i = 0; i < size; i++) {
            Post newPost = new Post(topic, author, "content " + i);
            session.save(newPost);
            posts.add(newPost);
        }
        return posts;
    }

    /* PostDao specific methods */

    @Test
    public void testGetPostRangeInTopic() {
        int start = 1;
        int max = 2;
        List<Post> persistedPosts = createAndSavePostList(5);
        long topicId = persistedPosts.get(0).getTopic().getId();

        List<Post> posts = dao.getPostRangeInTopic(topicId, start, max);

        assertEquals(max, posts.size(), "Unexpected list size");
        assertEquals(topicId, posts.get(0).getTopic().getId(), "Incorrect topic");
    }

    @Test
    public void testGetTopicsInBranchCount() {
        List<Post> persistedPosts = createAndSavePostList(5);
        long topicId = persistedPosts.get(0).getTopic().getId();

        int count = dao.getPostsInTopicCount(topicId);

        assertEquals(count, 5);
    }

    private int getCount() {
        return ((Number) session.createQuery("select count(*) from Post").uniqueResult()).intValue();
    }

}
