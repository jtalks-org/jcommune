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

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.jtalks.jcommune.model.PersistedObjectsFactory;
import org.jtalks.jcommune.model.dao.LastReadPostDao;
import org.jtalks.jcommune.model.entity.LastReadPost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author Evgeniy Naumenko
 * @author Anuar Nurmakanov
 */
@ContextConfiguration(locations = { "classpath:/org/jtalks/jcommune/model/entity/applicationContext-dao.xml" })
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
@Transactional
public class LastReadPostHibernateDaoTest extends AbstractTransactionalTestNGSpringContextTests {
    @Autowired
    private LastReadPostDao dao;
    @Autowired
    private SessionFactory sessionFactory;
    private Session session;
    
    @BeforeMethod
    public void setUp() {
        session = sessionFactory.getCurrentSession();
        PersistedObjectsFactory.setSession(session);
    }
    
    /*===== Common methods =====*/
    @Test
    public void testGet() {
        LastReadPost expected = PersistedObjectsFactory.getDefaultLastReadPost();
        session.save(expected);
        
        LastReadPost actual = dao.get(expected.getId());
        
        Assert.assertNotNull(actual, "Get returns null.");
        Assert.assertEquals(actual.getId(), expected.getId(),
                "Get return incorrect object");
    }
    
    @Test
    public void testUpdate() {
        LastReadPost post = PersistedObjectsFactory.getDefaultLastReadPost();
        session.save(post);
        int newPostIndex = post.getPostIndex() + 1;
        post.setPostIndex(newPostIndex);
        
        dao.update(post);
        LastReadPost updatedPost = (LastReadPost) session.get(LastReadPost.class, post.getId());
        
        Assert.assertEquals(updatedPost.getPostIndex(), newPostIndex,
                "Update doesn't work, because field value didn't change.");
    }
    
    /*===== Specific methods =====*/
    @Test
    public void testListLastReadPostsForTopic() {
        LastReadPost post = PersistedObjectsFactory.getDefaultLastReadPost();
        session.save(post);
        
        List<LastReadPost> lastReadPosts = dao.listLastReadPostsForTopic(post.getTopic());
        
        Assert.assertTrue(lastReadPosts.size() == 1, "Result list has incorrect size");
        Assert.assertEquals(lastReadPosts.get(0).getId(), post.getId(), 
                "Results contains invalid data.");
    }
    
    @Test
    public void testGetLastReadPost() {
        LastReadPost expected = PersistedObjectsFactory.getDefaultLastReadPost();
        session.save(expected);
        
        LastReadPost actual = dao.getLastReadPost(expected.getUser(), expected.getTopic());
        
        Assert.assertEquals(actual.getId(), expected.getId(), 
                "Found incorrect last read post.");
    }
}
