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
import org.jtalks.jcommune.model.ObjectsFactory;
import org.jtalks.jcommune.model.dao.PostDao;
import org.jtalks.jcommune.model.entity.Post;
import org.jtalks.jcommune.model.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.*;

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

        dao.update(post);
        session.evict(post);
        Post result = (Post) session.get(Post.class, post.getId());

        assertEquals(result.getPostContent(), newContent);
    }

    @Test(expectedExceptions = Exception.class)
    public void testUpdateNotNullViolation() {
        Post post = ObjectsFactory.getDefaultPost();
        session.save(post);
        post.setPostContent(null);

        dao.update(post);
    }

    /* PostDao specific methods */

    @Test
    public void testPostOfUser() {
        User user = ObjectsFactory.getDefaultUser();
        Post post = new Post(user, "first");
        List<Post> posts = new ArrayList<Post>();
        posts.add(post);
        session.save(user);
        session.save(post);

        List<Post> postsTwo = dao.getPostsOfUser(user);

        assertEquals(postsTwo, posts);
    }

    @Test
    public void testNullPostOfUser() {
        User user = ObjectsFactory.getDefaultUser();
        session.save(user);

        List<Post> posts = dao.getPostsOfUser(user);

        assertEquals(posts, new ArrayList<Post>());
    }
}
