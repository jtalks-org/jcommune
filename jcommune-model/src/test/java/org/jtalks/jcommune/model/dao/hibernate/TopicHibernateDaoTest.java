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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.joda.time.DateTime;
import org.jtalks.jcommune.model.ObjectsFactory;
import org.jtalks.jcommune.model.dao.TopicDao;
import org.jtalks.jcommune.model.entity.Branch;
import org.jtalks.jcommune.model.entity.Topic;
import org.jtalks.jcommune.model.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author Kirill Afonin
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
        User author = ObjectsFactory.getDefaultUser();
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
    public void testGetTopicRangeInBranch() {
        int start = 1;
        int max = 2;
        List<Topic> persistedTopics = createAndSaveTopicList(5);
        long branchId = persistedTopics.get(0).getBranch().getId();

        List<Topic> topics = dao.getTopicRangeInBranch(branchId, start, max);

        assertEquals(max, topics.size(), "Unexpected list size");
        assertEquals(branchId, topics.get(0).getBranch().getId(), "Incorrect branch");
    }

    @Test
    public void testGetTopicsInBranchCount() {
        List<Topic> persistedTopics = createAndSaveTopicList(5);
        long branchId = persistedTopics.get(0).getBranch().getId();

        int count = dao.getTopicsInBranchCount(branchId);

        assertEquals(count, 5);
    }

    @Test
    public void testGetTopicsPastLastDayCount() {
        User author = ObjectsFactory.getDefaultUser();
        session.save(author);
        Topic newTopic = new Topic(author, "title1");
        session.save(newTopic);
        Topic oldTopic = new Topic(author, "title2");
        ReflectionTestUtils.setField(oldTopic, "modificationDate", new DateTime().minusDays(2), DateTime.class);
        session.save(oldTopic);
        DateTime lastLogin = new DateTime().minusDays(1);

        int count = dao.getTopicsPastLastDayCount(lastLogin);

        assertEquals(count, 1);
    }

    @Test
    public void testGetAllTopicsPastLastDay() {
        int start = 1;
        int max = 2;
        createAndSaveTopicList(5);
        DateTime lastLogin = new DateTime().minusDays(1);

        List<Topic> result = dao.getAllTopicsPastLastDay(start, max, lastLogin);

        assertEquals(result.size(), max);
    }    
       
}
