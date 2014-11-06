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
import org.jtalks.jcommune.model.entity.PersistedObjectsFactory;
import org.jtalks.jcommune.model.dao.TopicDao;
import org.jtalks.jcommune.model.dto.PageRequest;
import org.jtalks.jcommune.model.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;
import static org.jtalks.jcommune.model.matchers.HasPages.hasPages;
import static org.testng.Assert.*;

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
        PersistedObjectsFactory.setSession(session);
    }

    /*===== Common methods =====*/

    @Test
    public void testGet() {
        Topic topic = PersistedObjectsFactory.getDefaultTopic();
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
        Topic topic = PersistedObjectsFactory.getDefaultTopic();
        session.save(topic);
        topic.setTitle(newTitle);

        dao.saveOrUpdate(topic);
        session.flush();
        session.evict(topic);
        Topic result = (Topic) session.get(Topic.class, topic.getId());

        assertEquals(result.getTitle(), newTitle);
    }

    @Test(expectedExceptions = Exception.class)
    public void testUpdateNotNullViolation() {
        Topic topic = ObjectsFactory.getDefaultTopic();
        session.save(topic);
        topic.setTitle(null);

        dao.saveOrUpdate(topic);
    }

    private List<Topic> createAndSaveTopicList(int size) {
        List<Topic> topics = new ArrayList<>();
        JCUser author = ObjectsFactory.getDefaultUser();
        session.save(author);
        Branch branch = ObjectsFactory.getDefaultBranch();
        for (int i = 0; i < size; i++) {
            Topic newTopic = new Topic(author, "title" + i);
            newTopic.addPost(new Post(author, "post_content" + i));
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
        PageRequest pageRequest = new PageRequest("1", size);
        DateTime lastLogin = new DateTime().minusDays(1);

        Page<Topic> page = dao.getTopicsUpdatedSince(lastLogin, pageRequest, ObjectsFactory.getDefaultUser());

        assertEquals(page.getContent().size(), 0);
    }

    @Test
    public void testGetUpdateTopicsUpdatedSinceWithPagingAndRegisteredUser() {
        int listSize = 5;
        int pageSize = 2;
        int lastPage = listSize / pageSize;
        List<Topic> createdTopicList = createAndSaveTopicList(listSize);
        PageRequest pageRequest = new PageRequest(
                String.valueOf(lastPage), pageSize);
        DateTime lastLogin = new DateTime().minusDays(1);

        JCUser user = new JCUser("Current", null, null);
        user.setGroups(ObjectsFactory.getDefaultGroupList());
        PersistedObjectsFactory.createAndSaveViewTopicsBranchesEntity(createdTopicList.get(0).getBranch().getId(),
                String.valueOf(user.getGroups().get(0).getId()), true);
        Page<Topic> page = dao.getTopicsUpdatedSince(lastLogin, pageRequest, user);

        assertThat("Topics should be paginated for registered users", page, hasPages());
    }

    @Test
    public void testGetUpdateTopicsUpdatedSinceWithPagingAndRegisteredUserPageTooLow() {
        int listSize = 5;
        int pageSize = 2;
        List<Topic> createdTopicList = createAndSaveTopicList(listSize);
        PageRequest pageRequest = new PageRequest("0", pageSize);
        DateTime lastLogin = new DateTime().minusDays(1);

        JCUser user = new JCUser("Current", null, null);
        user.setGroups(ObjectsFactory.getDefaultGroupList());
        PersistedObjectsFactory.createAndSaveViewTopicsBranchesEntity(createdTopicList.get(0).getBranch().getId(),
                String.valueOf(user.getGroups().get(0).getId()), true);
        Page<Topic> page = dao.getTopicsUpdatedSince(lastLogin, pageRequest, user);

        assertThat("Topics should be paginated for registered users", page, hasPages());
        assertEquals(page.getNumber(), 1);
    }

    @Test
    public void testGetUpdateTopicsUpdatedSinceWithPagingAndRegisteredUserPageTooBig() {
        int listSize = 5;
        int pageSize = 2;
        int lastPage = 3;
        List<Topic> createdTopicList = createAndSaveTopicList(listSize);
        PageRequest pageRequest = new PageRequest("1000",
                pageSize);
        DateTime lastLogin = new DateTime().minusDays(1);

        JCUser user = new JCUser("Current", null, null);
        user.setGroups(ObjectsFactory.getDefaultGroupList());
        PersistedObjectsFactory.createAndSaveViewTopicsBranchesEntity(createdTopicList.get(0).getBranch().getId(),
                String.valueOf(user.getGroups().get(0).getId()), true);
        Page<Topic> page = dao.getTopicsUpdatedSince(lastLogin, pageRequest, user);

        assertThat("Topics should be paginated for registered users", page, hasPages());
        assertEquals(page.getNumber(), lastPage);
    }

    @Test
    public void testGetUpdateTopicsUpdatedSinceWithPagingAndAnonymousUser() {
        int listSize = 5;
        int pageSize = 2;
        int lastPage = listSize / pageSize;
        List<Topic> createdTopicList = createAndSaveTopicList(listSize);
        PageRequest pageRequest = new PageRequest(
                String.valueOf(lastPage), pageSize);
        DateTime lastLogin = new DateTime().minusDays(1);

        PersistedObjectsFactory.createAndSaveViewTopicsBranchesEntity(
                createdTopicList.get(0).getBranch().getId(), "anonymousUser", true);
        Page<Topic> page = dao.getTopicsUpdatedSince(lastLogin, pageRequest, new AnonymousUser());

        assertThat("Topics should be paginated for anonymous group", page, hasPages());
    }

    @Test
    public void testGetUpdateTopicsUpdatedSinceWithPagingAndAnonymousUserPageTooLow() {
        int listSize = 5;
        int pageSize = 2;
        List<Topic> createdTopicList = createAndSaveTopicList(listSize);
        PageRequest pageRequest = new PageRequest("0", pageSize);
        DateTime lastLogin = new DateTime().minusDays(1);

        PersistedObjectsFactory.createAndSaveViewTopicsBranchesEntity(
                createdTopicList.get(0).getBranch().getId(), "anonymousUser", true);
        Page<Topic> page = dao.getTopicsUpdatedSince(lastLogin, pageRequest, new AnonymousUser());

        assertThat("Topics should be paginated for anonymous group", page, hasPages());
        assertEquals(page.getNumber(), 1);
    }

    @Test
    public void testGetUpdateTopicsUpdatedSinceWithPagingAndAnonymousUserPageTooBig() {
        int listSize = 5;
        int pageSize = 2;
        int lastPage = 3;
        List<Topic> createdTopicList = createAndSaveTopicList(listSize);
        PageRequest pageRequest = new PageRequest("1000",
                pageSize);
        DateTime lastLogin = new DateTime().minusDays(1);

        PersistedObjectsFactory.createAndSaveViewTopicsBranchesEntity(
                createdTopicList.get(0).getBranch().getId(), "anonymousUser", true);
        Page<Topic> page = dao.getTopicsUpdatedSince(lastLogin, pageRequest, new AnonymousUser());

        assertThat("Topics should be paginated for anonymous group", page, hasPages());
        assertEquals(page.getNumber(), lastPage);
    }

    @Test
    public void testGetUpdateTopicsUpdatedForNoneExistingBranchAndUserGroup() {
        int listSize = 5;
        int pageSize = 2;
        int lastPage = listSize / pageSize;
        createAndSaveTopicList(listSize);
        PageRequest pageRequest = new PageRequest(
                String.valueOf(lastPage), pageSize);
        DateTime lastLogin = new DateTime().minusDays(1);

        PersistedObjectsFactory.createAndSaveViewTopicsBranchesEntity(22l, "noneExistingGroup", true);
        Page<Topic> page = dao.getTopicsUpdatedSince(lastLogin, pageRequest, new AnonymousUser());

        assertThat("Topics shouldn't be paginated for none existing group", page, not(hasPages()));
    }

    @Test
    public void testGetUnansweredTopicsForRegisteredUsers() {
        JCUser user = createAndSaveTopicsWithUnansweredTopics();
        PageRequest pageRequest = new PageRequest("1", 2);

        Page<Topic> result = dao.getUnansweredTopics(pageRequest, user);
        assertEquals(result.getContent().size(), 2);
        assertEquals(result.getTotalElements(), 2);
    }

    @Test
    public void testGetUnansweredTopicsForAnonymousUsers() {
        createAndSaveTopicsWithUnansweredTopics();
        PageRequest pageRequest = new PageRequest("1", 2);

        Page<Topic> result = dao.getUnansweredTopics(pageRequest, new AnonymousUser());
        assertEquals(result.getContent().size(), 2);
        assertEquals(result.getTotalElements(), 2);
    }

    @Test
    public void testGetUnansweredTopicsWithPaging() {
        JCUser user = createAndSaveTopicsWithUnansweredTopics();
        PageRequest pageRequest = new PageRequest("2", 1);
        Page<Topic> result = dao.getUnansweredTopics(pageRequest, user);
        assertEquals(result.getContent().size(), 1);
        assertEquals(result.getTotalElements(), 2);
    }

    @Test
    public void testGetUnansweredTopicsWithPagingPageTooLow() {
        JCUser user = createAndSaveTopicsWithUnansweredTopics();
        PageRequest pageRequest = new PageRequest("0", 1);
        Page<Topic> result = dao.getUnansweredTopics(pageRequest, user);
        assertEquals(result.getContent().size(), 1);
        assertEquals(result.getTotalElements(), 2);
        assertEquals(result.getNumber(), 1);
    }

    @Test
    public void testGetUnansweredTopicsWithPagingPageTooBig() {
        JCUser user = createAndSaveTopicsWithUnansweredTopics();
        PageRequest pageRequest = new PageRequest("1000", 1);
        Page<Topic> result = dao.getUnansweredTopics(pageRequest, user);
        assertEquals(result.getContent().size(), 1);
        assertEquals(result.getTotalElements(), 2);
        assertEquals(result.getNumber(), 2);
    }

    private JCUser createAndSaveTopicsWithUnansweredTopics() {
        JCUser author = PersistedObjectsFactory.getDefaultUserWithGroups();

        Branch branch = ObjectsFactory.getDefaultBranch();
        branch.addTopic(ObjectsFactory.getTopic(author, 1));
        branch.addTopic(ObjectsFactory.getTopic(author, 1));
        branch.addTopic(ObjectsFactory.getTopic(author, 2));
        session.save(branch);

        PersistedObjectsFactory.createAndSaveViewTopicsBranchesEntity(branch.getId(),
                String.valueOf(author.getGroups().get(0).getId()), true);
        PersistedObjectsFactory.createAndSaveViewTopicsBranchesEntity(branch.getId(), "anonymousUser", true);

        return author;
    }


    @Test
    public void testGetLastUpdatedTopicInBranch() {
        Topic firstTopic = PersistedObjectsFactory.getDefaultTopic();
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
        Poll poll = PersistedObjectsFactory.createDefaultVoting();
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
        Topic topic = PersistedObjectsFactory.getDefaultTopic();
        JCUser user = topic.getTopicStarter();
        Branch branch = topic.getBranch();
        branch.addTopic(new Topic(user, "Second topic"));
        session.save(branch);
        int expectedCount = branch.getTopics().size();

        int actualCount = dao.countTopics(branch);

        assertEquals(actualCount, expectedCount, "Count of topics in the branch is wrong");
    }

    @Test
    public void testGetTopicsWithEnabledPaging() {
        int totalSize = 50;
        int pageCount = 2;
        int pageSize = totalSize / pageCount;
        PageRequest pageRequest = new PageRequest("1", pageSize);
        List<Topic> topicList = PersistedObjectsFactory.createAndSaveTopicList(totalSize);
        Branch branch = topicList.get(0).getBranch();

        Page<Topic> topicsPage = dao.getTopics(branch, pageRequest);

        assertThat("Incorrect pagination", topicsPage, hasPages());
    }

    @Test
    public void testGetTopicsWithEnabledPagingPageTooLow() {
        int totalSize = 50;
        int pageCount = 2;
        int pageSize = totalSize / pageCount;
        PageRequest pageRequest = new PageRequest("0", pageSize);
        List<Topic> topicList = PersistedObjectsFactory.createAndSaveTopicList(totalSize);
        Branch branch = topicList.get(0).getBranch();

        Page<Topic> topicsPage = dao.getTopics(branch, pageRequest);

        assertThat("Incorrect pagination", topicsPage, hasPages());
        assertEquals(topicsPage.getNumber(), 1);
    }

    @Test
    public void testGetTopicsWithEnabledPagingTooBigg() {
        int totalSize = 50;
        int pageCount = 2;
        int pageSize = totalSize / pageCount;
        PageRequest pageRequest = new PageRequest("1000",
                pageSize);
        List<Topic> topicList = PersistedObjectsFactory.createAndSaveTopicList(totalSize);
        Branch branch = topicList.get(0).getBranch();

        Page<Topic> topicsPage = dao.getTopics(branch, pageRequest);

        assertThat("Incorrect pagination", topicsPage, hasPages());
        assertEquals(topicsPage.getNumber(), pageCount);
    }

    @Test
    public void testAddCodeReview() {
        Topic topic = PersistedObjectsFactory.getDefaultTopic();

        CodeReview review = new CodeReview();
        topic.setCodeReview(review);
        review.setTopic(topic);
        dao.saveOrUpdate(topic);
        session.flush();
        session.evict(topic);
        assertNotNull(dao.get(topic.getId()).getCodeReview());
    }

    @Test
    public void testRemoveCodeReview() {
        Topic topic = PersistedObjectsFactory.getCodeReviewTopic();

        topic.getCodeReview().setTopic(null);
        topic.setCodeReview(null);
        dao.saveOrUpdate(topic);

        session.evict(topic);

        assertNull(dao.get(topic.getId()).getCodeReview());
    }

    @Test
    public void testDeleteCodeReviewTopic() {
        Topic topic = PersistedObjectsFactory.getCodeReviewTopic();
        long reviewId = topic.getCodeReview().getId();

        Branch topicBranch = topic.getBranch();
        topicBranch.deleteTopic(topic);
        session.update(topicBranch);
        session.flush();

        assertNull(dao.get(topic.getId()));
        assertNull(session.get(CodeReview.class, reviewId));
    }

    @Test
    public void testGetSubscribersWithAllowedPermission() {
        Topic topic = createAndSaveTopicWithSubscribers();
        JCUser subscriber = topic.getTopicStarter();
        PersistedObjectsFactory.createAndSaveViewTopicsBranchesEntity(
                topic.getBranch().getId(), String.valueOf(subscriber.getGroups().get(0).getId()), true);
        assertEquals(dao.getAllowedSubscribers(topic).size(), 1,
                "Should return subscribers which are contained in some group with VIEW_TOPIC permission and not contained in any group with disallowed VIEW_TOPIC permission.");
    }

    @Test
    public void testGetSubscribersWithDisallowedPermission() {
        Topic topic = createAndSaveTopicWithSubscribers();
        JCUser subscriber = topic.getTopicStarter();
        PersistedObjectsFactory.createAndSaveViewTopicsBranchesEntity(
                topic.getBranch().getId(), String.valueOf(subscriber.getGroups().get(0).getId()), false);

        assertEquals(dao.getAllowedSubscribers(topic).size(), 0,
                "Should not return subscribers which are contained in any group with disallowed VIEW_TOPIC permission.");
    }

    @Test
    public void testGetSubscribersWithAllowedAndDisallowedPermission() {
        Topic topic = createAndSaveTopicWithSubscribers();
        JCUser subscriber = topic.getTopicStarter();
        PersistedObjectsFactory.createAndSaveViewTopicsBranchesEntity(
                topic.getBranch().getId(), String.valueOf(subscriber.getGroups().get(0).getId()), false);
        PersistedObjectsFactory.createAndSaveViewTopicsBranchesEntity(
                topic.getBranch().getId(), String.valueOf(subscriber.getGroups().get(1).getId()), true);

        assertEquals(dao.getAllowedSubscribers(topic).size(), 0,
                "Should not return subscribers which are contained in any group with disallowed VIEW_TOPIC permission.");
    }

    @Test
    public void testGetSubscribersWithoutAllowedAndDisallowedPermission() {
        Topic topic = createAndSaveTopicWithSubscribers();

        assertEquals(dao.getAllowedSubscribers(topic).size(), 0,
                "Should not return subscribers which are not contained in any group with VIEW_TOPIC permission.");
    }

    private Topic createAndSaveTopicWithSubscribers() {
        JCUser subscriber = PersistedObjectsFactory.getDefaultUserWithGroups();
        Branch branch = ObjectsFactory.getDefaultBranch();
        Topic topic = ObjectsFactory.getTopic(subscriber, 5);
        topic.getSubscribers().add(subscriber);
        branch.addTopic(topic);
        session.save(branch);
        return topic;
    }

    @Test
    public void testGetForbiddenBranchesIdsWithAnonymousUser() {

        AnonymousUser user = new AnonymousUser();

        PersistedObjectsFactory.createAndSaveViewTopicsBranchesEntity(
                ObjectsFactory.getDefaultBranch().getId(), user.getClass().getSimpleName(), false
        );

        List<Long> collection = this.dao.getForbiddenBranchesIds(user);
        assertFalse(collection.isEmpty());
    }

    @Test
    public void testGetForbiddenBranchesIdsWithRealUser() {

        JCUser user = new JCUser("username", null, null);
        user.setGroups(ObjectsFactory.getDefaultGroupList());

        PersistedObjectsFactory.createAndSaveViewTopicsBranchesEntity(
                ObjectsFactory.getDefaultBranch().getId(), String.valueOf(user.getGroups().get(0).getId()), false
        );

        List<Long> collection = this.dao.getForbiddenBranchesIds(user);
        assertFalse(collection.isEmpty());
    }

    @Test
    public void testGetAllowedBranchesIdsWithAnonymousUser() {

        AnonymousUser user = new AnonymousUser();

        PersistedObjectsFactory.createAndSaveViewTopicsBranchesEntity(
                ObjectsFactory.getDefaultBranch().getId(), user.getClass().getSimpleName(), true
        );

        List<Long> collection = this.dao.getAllowedBranchesIds(user);
        assertTrue(collection.size() > 0);
    }

    @Test
    public void testGetAllowedBranchesIdsWithAnonymousUserAllowedAndRestricted() {

        AnonymousUser user = new AnonymousUser();

        PersistedObjectsFactory.createAndSaveViewTopicsBranchesEntity(
                ObjectsFactory.getDefaultBranch().getId(), user.getClass().getSimpleName(), true
        );

        PersistedObjectsFactory.createAndSaveViewTopicsBranchesEntity(
                ObjectsFactory.getDefaultBranch().getId(), user.getClass().getSimpleName(), false
        );

        List<Long> collection = this.dao.getAllowedBranchesIds(user);
        assertTrue(collection.isEmpty());
    }

    @Test
    public void testGetAllowedBranchesIdsWithRealUser() {

        JCUser user = new JCUser("username", null, null);
        user.setGroups(ObjectsFactory.getDefaultGroupList());

        PersistedObjectsFactory.createAndSaveViewTopicsBranchesEntity(
                ObjectsFactory.getDefaultBranch().getId(), String.valueOf(user.getGroups().get(0).getId()), true
        );

        List<Long> collection = this.dao.getAllowedBranchesIds(user);
        assertTrue(collection.size() > 0);
    }

    @Test
    public void testGetAllowedBranchesIdsWithRealUserAllowedAndRestricted() {

        JCUser user = new JCUser("username", null, null);
        user.setGroups(ObjectsFactory.getDefaultGroupList());

        PersistedObjectsFactory.createAndSaveViewTopicsBranchesEntity(
                ObjectsFactory.getDefaultBranch().getId(), user.getClass().getSimpleName(), true
        );

        PersistedObjectsFactory.createAndSaveViewTopicsBranchesEntity(
                ObjectsFactory.getDefaultBranch().getId(), user.getClass().getSimpleName(), false
        );

        List<Long> collection = this.dao.getAllowedBranchesIds(user);
        assertTrue(collection.isEmpty());
    }
}
