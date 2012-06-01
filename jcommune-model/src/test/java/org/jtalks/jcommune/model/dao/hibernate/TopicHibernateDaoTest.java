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
import org.joda.time.DateTime;
import org.jtalks.jcommune.model.ObjectsFactory;
import org.jtalks.jcommune.model.dao.TopicDao;
import org.jtalks.jcommune.model.entity.Branch;
import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.model.entity.Post;
import org.jtalks.jcommune.model.entity.Topic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

/**
 * @author Kirill Afonin
 * @author Eugeny Batov
 */
@ContextConfiguration(locations = {"classpath:/org/jtalks/jcommune/model/entity/applicationContext-dao.xml"})
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
@Transactional
public class TopicHibernateDaoTest extends AbstractTransactionalTestNGSpringContextTests {
    @Autowired
    private SessionFactory sessionFactory;
    @Autowired
    private TopicDao dao;
    private Session session;

    @BeforeMethod
    public void setUp() throws Exception {
        session = sessionFactory.getCurrentSession();
        ObjectsFactory.setSession(session);
    }

    /*===== Common methods =====*/

    @Test
    public void testGet() {
        Topic topic = ObjectsFactory.getDefaultTopic();
        session.save(topic);

        Topic result = dao.get(topic.getId());

        assertNotNull(result);
        assertEquals(result.getId(), topic.getId());
    }


    @Test
    public void testGetInvalidId() {
        Topic result = dao.get(-567890L);

        assertNull(result);
    }

    @Test
    public void testUpdate() {
        String newTitle = "new title";
        Topic topic = ObjectsFactory.getDefaultTopic();
        session.save(topic);
        topic.setTitle(newTitle);

        dao.update(topic);
        session.evict(topic);
        Topic result = (Topic) session.get(Topic.class, topic.getId());

        assertEquals(result.getTitle(), newTitle);
    }

    @Test(expectedExceptions = Exception.class)
    public void testUpdateNotNullViolation() {
        Topic topic = ObjectsFactory.getDefaultTopic();
        session.save(topic);
        topic.setTitle(null);

        dao.update(topic);
    }

    private List<Topic> createAndSaveTopicList(int size) {
        List<Topic> topics = new ArrayList<Topic>();
        JCUser author = ObjectsFactory.getDefaultUser();
        session.save(author);
        Branch branch = ObjectsFactory.getDefaultBranch();
        for (int i = 0; i < size; i++) {
            Topic newTopic = new Topic(author, "title" + i);
            branch.addTopic(newTopic);
            topics.add(newTopic);
        }
        session.save(branch);
        return topics;
    }

    /*===== TopicDao specific methods =====*/

    @Test
    public void testGetTopicsUpdatedSince() {
        createAndSaveTopicList(5);
        DateTime lastLogin = new DateTime().minusDays(1);

        List<Topic> result = dao.getTopicsUpdatedSince(lastLogin);

        assertEquals(result.size(), 5);
    }


    @Test
    public void testGetUnansweredTopics() {
        createAndSaveTopicsWithUnansweredTopics();
        List<Topic> result = dao.getUnansweredTopics();
        assertEquals(result.size(), 2);
    }

    private void createAndSaveTopicsWithUnansweredTopics() {
        JCUser author = ObjectsFactory.getDefaultUser();
        session.save(author);
        Topic firstTopic = new Topic(author, "firstTopic");
        firstTopic.addPost(new Post(author, "first topic initial post"));
        Topic secondTopic = new Topic(author, "secondTopic");
        secondTopic.addPost(new Post(author, "second topic initial post"));
        Topic thirdTopic = new Topic(author, "thirdTopic");
        thirdTopic.addPost(new Post(author, "third topic initial post"));
        thirdTopic.addPost(new Post(author, "another post"));
        session.save(firstTopic);
        session.save(secondTopic);
        session.save(thirdTopic);
    }
    
    
    @Test
    public void testGetLastUpdatedTopicInBranch() {
        Topic firstTopic = ObjectsFactory.getDefaultTopic();
        Branch branch = firstTopic.getBranch();
        Topic secondTopic = new Topic(firstTopic.getTopicStarter(), "Second topic");
        firstTopic.updateModificationDate();
        branch.addTopic(secondTopic);
        Topic expectedLastUpdatedTopic = firstTopic;
        
        session.save(branch);
        
        Topic actualLastUpdatedTopic = dao.getLastUpdatedTopicInBranch(branch);
        
        assertNotNull(actualLastUpdatedTopic, "Last updated topic is not found");
        assertEquals(actualLastUpdatedTopic.getId(), expectedLastUpdatedTopic.getId(),
                "Found incorrect last updated topic");
    }
    
    @Test
    public void testGetLastUpdatedTopicInEmptyBranch() {
        Branch branch = ObjectsFactory.getDefaultBranch();
        session.save(branch);
        
        assertNull(dao.getLastUpdatedTopicInBranch(branch), "The branch is empty, so the topic should not be found");
    }
}
