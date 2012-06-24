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
import org.jtalks.jcommune.model.dto.JcommunePageRequest;
import org.jtalks.jcommune.model.dto.JcommunePageable;
import org.jtalks.jcommune.model.entity.Branch;
import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.model.entity.Poll;
import org.jtalks.jcommune.model.entity.Post;
import org.jtalks.jcommune.model.entity.Topic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

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
        int size = 5;
        createAndSaveTopicList(size);
        JcommunePageable pageRequest = new JcommunePageRequest(1, size);
        DateTime lastLogin = new DateTime().minusDays(1);

        Page<Topic> page = dao.getTopicsUpdatedSince(lastLogin, pageRequest);

        assertEquals(page.getContent().size(), size);
    }
    
    @Test
    public void testGetUpdateTopicsUpdatedSinceWithPaging() {
        int listSize = 10;
        int pageSize = 5;
        int lastPage = listSize / pageSize;
        createAndSaveTopicList(listSize);
        JcommunePageable pageRequest = new JcommunePageRequest(lastPage, pageSize);
        DateTime lastLogin = new DateTime().minusDays(1);

        Page<Topic> page = dao.getTopicsUpdatedSince(lastLogin, pageRequest);

        assertEquals(page.getContent().size(), pageSize);
        assertEquals(page.getSize(), pageSize);
        assertEquals(page.getTotalElements(), listSize);
    }

    @Test
    public void testGetUnansweredTopics() {
        createAndSaveTopicsWithUnansweredTopics();
        JcommunePageable pageRequest = new JcommunePageRequest(1, 2);
        Page<Topic> result = dao.getUnansweredTopics(pageRequest);
        assertEquals(result.getContent().size(), 2);
    }
    
    @Test
    public void testGetUnansweredTopicsWithPaging() {
        createAndSaveTopicsWithUnansweredTopics();
        JcommunePageable pageRequest = new JcommunePageRequest(2, 1);
        Page<Topic> result = dao.getUnansweredTopics(pageRequest);
        assertEquals(result.getContent().size(), 1);
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
        branch.addTopic(secondTopic);
        Topic expectedLastUpdatedTopic = firstTopic;
        ReflectionTestUtils.setField(
                expectedLastUpdatedTopic,
                "modificationDate",
                new DateTime(2100, 12, 25, 0, 0, 0, 0));

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

    @Test
    public void testDeleteWithPoll() {
        Poll poll = ObjectsFactory.createDefaultVoting();
        Topic topic = poll.getTopic();
        Branch branch = topic.getBranch();
        branch.deleteTopic(topic);

        session.save(branch);
        session.flush();
        assertNull(dao.get(topic.getId()));
    }
    
    @Test
    public void testGetCountTopicsInBranch() {
        //this topic is persisted
        Topic topic = ObjectsFactory.getDefaultTopic();
        JCUser user = topic.getTopicStarter();
        Branch branch = topic.getBranch();
        branch.addTopic(new Topic(user, "Second topic"));
        session.save(branch);
        int expectedCount = branch.getTopics().size();
        
        int actualCount = dao.getCountTopicsInBranch(branch);
        
        assertEquals(actualCount, expectedCount, "Count of topics in the branch is wrong");
    }
    
    @Test
    public void testGetTopicsWithEnabledPaging() {
        boolean pagingEnabled = true;
        int totalSize = 50;
        int pageCount = 2;
        int pageSize = totalSize/pageCount;
        JcommunePageable pageRequest = new JcommunePageRequest(1, pageSize);
        List<Topic> topicList = ObjectsFactory.createAndSaveTopicList(totalSize);
        Branch branch = topicList.get(0).getBranch();
        
        Page<Topic> topicsPage = dao.getTopics(branch, pageRequest, pagingEnabled);
        
        assertEquals(topicsPage.getContent().size(), pageSize, "Incorrect count of topics in one page.");
        assertEquals(topicsPage.getTotalElements(), totalSize, "Incorrect total count.");
        assertEquals(topicsPage.getTotalPages(), pageCount, "Incorrect count of pages.");
        
    }
    
    @Test
    public void testGetTopicsWithDisabledPaging() {
        boolean pagingEnabled = false;
        int size = 50;
        JcommunePageable pageRequest = new JcommunePageRequest(1, size/2);
        List<Topic> topicList = ObjectsFactory.createAndSaveTopicList(size);
        Branch branch = topicList.get(0).getBranch();
        
        Page<Topic> topicsPage = dao.getTopics(branch, pageRequest, pagingEnabled);
        
        assertEquals(topicsPage.getContent().size(), size, 
                "Paging is disabled, so it should retrieve all topics in the branch.");
        assertEquals(topicsPage.getTotalElements(), size, "Incorrect total count.");
    }
}
