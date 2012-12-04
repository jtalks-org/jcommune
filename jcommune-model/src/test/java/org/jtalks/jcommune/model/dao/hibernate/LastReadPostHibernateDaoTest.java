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

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.jtalks.jcommune.model.PersistedObjectsFactory;
import org.jtalks.jcommune.model.dao.LastReadPostDao;
import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.model.entity.LastReadPost;
import org.jtalks.jcommune.model.entity.Topic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author Evgeniy Naumenko
 * @author Anuar Nurmakanov
 */
@ContextConfiguration(locations = {"classpath:/org/jtalks/jcommune/model/entity/applicationContext-dao.xml"})
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
@Transactional
public class LastReadPostHibernateDaoTest extends AbstractTransactionalTestNGSpringContextTests {
    @Autowired
    private LastReadPostDao lastReadPostDao;
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

        LastReadPost actual = lastReadPostDao.get(expected.getId());

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

        lastReadPostDao.update(post);
        LastReadPost updatedPost = (LastReadPost) session.get(LastReadPost.class, post.getId());

        Assert.assertEquals(updatedPost.getPostIndex(), newPostIndex,
                "Update doesn't work, because field value didn't change.");
    }

    @Test
    public void testMarkAsReadTopicsToUser() {
        List<Topic> topics = PersistedObjectsFactory.createAndSaveTopicListWithPosts(10);
        JCUser user = PersistedObjectsFactory.getDefaultUser();
        //records of posts in topics
        Map<Long, Integer> listCountPostsToTopics = new HashMap<Long, Integer>();
        //records in database about read topic
        Map<Long, Integer> actualCountPostsToTopics = new HashMap<Long, Integer>();

        listCountPostsToTopics = markAllTopicsASRead(topics, user);
        actualCountPostsToTopics = getActualListCountPostsToTopics(topics, user);

        Assert.assertEquals(actualCountPostsToTopics, listCountPostsToTopics);
    }

    @Test
    public void testDeleteMarksTopicsToUser() {
        List<Topic> topics = PersistedObjectsFactory.createAndSaveTopicListWithPosts(10);
        JCUser user = PersistedObjectsFactory.getDefaultUser();
        SQLQuery deletedEntities = (SQLQuery) session.getNamedQuery("deleteAllMarksReadToUser");
        deletedEntities
                .addSynchronizedEntityClass(LastReadPost.class)
                .setParameter("user", user.getId())
                .setParameter("branch", topics.get(0).getBranch().getId())
                .setCacheable(false)
                .executeUpdate();
        SQLQuery checkDelete = (SQLQuery) session.createSQLQuery("select TOPIC_ID, " +
                "LAST_READ_POST_INDEX FROM LAST_READ_POSTS where TOPIC_ID IN (select TOPIC_ID from TOPIC where " +
                "BRANCH_ID=:branch) and USER_ID = :user");
        checkDelete.setParameter("user", user.getId());
        checkDelete.setParameter("branch", topics.get(0).getBranch().getId());
        //check delete record about read posts for user
        Assert.assertTrue(checkDelete.list().isEmpty());
    }

    @Test
    public void testGetTopicAndCountOfPostsInBranch() {
        List<Topic> topics = PersistedObjectsFactory.createAndSaveTopicListWithPosts(10);
        //records of posts in topics
        Map<Long, Integer> actualCountOfPosts = getTopicAndCountOfPostsInBranch(topics);
        Map<Long, Integer> resultOfGetTopics = new HashMap<Long, Integer>();
        List<Object[]> resultCheckGetTopics = session.getNamedQuery("getTopicAndCountOfPostsInBranch")
                .setParameter("branch", topics.get(0).getBranch().getId())
                .setCacheable(false)
                .list();

        for (Object[] record : resultCheckGetTopics) {
            resultOfGetTopics.put(new Long(record[0].toString()), new Integer(record[1].toString()));
        }

        Assert.assertEquals(resultOfGetTopics, actualCountOfPosts);
    }

    @Test
    public void testMarkAllReadToUserInTwoBranches() {
        JCUser user = PersistedObjectsFactory.getDefaultUser();
        List<Topic> topicsOfFirstBranch = PersistedObjectsFactory.createAndSaveTopicListWithPosts(10);
        List<Topic> topicsOfSecondBranch = PersistedObjectsFactory.createAndSaveTopicListWithPosts(10);
        //records of posts in topics
        Map<Long, Integer> listCountPostsToTopicsInFBranch = new HashMap<Long, Integer>();
        Map<Long, Integer> listCountPostsToTopicsInSBranch = new HashMap<Long, Integer>();
        //records in database about read topic
        Map<Long, Integer> actualCountPostsToTopicsInFBranch = new HashMap<Long, Integer>();
        Map<Long, Integer> actualCountPostsToTopicsInSBranch = new HashMap<Long, Integer>();

        listCountPostsToTopicsInFBranch = markAllTopicsASRead(topicsOfFirstBranch, user);
        listCountPostsToTopicsInSBranch = markAllTopicsASRead(topicsOfSecondBranch, user);
        actualCountPostsToTopicsInFBranch = getActualListCountPostsToTopics(topicsOfFirstBranch, user);
        actualCountPostsToTopicsInSBranch = getActualListCountPostsToTopics(topicsOfSecondBranch, user);

        //concatenate  results from first and second branches
        listCountPostsToTopicsInFBranch.putAll(listCountPostsToTopicsInSBranch);
        actualCountPostsToTopicsInFBranch.putAll(actualCountPostsToTopicsInSBranch);

        Assert.assertEquals(listCountPostsToTopicsInFBranch, actualCountPostsToTopicsInFBranch);
    }

    /*===== Specific methods =====*/
    @Test
    public void testListLastReadPostsForTopic() {
        LastReadPost post = PersistedObjectsFactory.getDefaultLastReadPost();
        session.save(post);

        List<LastReadPost> lastReadPosts = lastReadPostDao.listLastReadPostsForTopic(post.getTopic());

        Assert.assertTrue(lastReadPosts.size() == 1, "Result list has incorrect size");
        Assert.assertEquals(lastReadPosts.get(0).getId(), post.getId(),
                "Results contains invalid data.");
    }

    @Test
    public void testGetLastReadPost() {
        LastReadPost expected = PersistedObjectsFactory.getDefaultLastReadPost();
        session.save(expected);

        LastReadPost actual = lastReadPostDao.getLastReadPost(expected.getUser(), expected.getTopic());

        Assert.assertEquals(actual.getId(), expected.getId(),
                "Found incorrect last read post.");
    }

    /**
     * Method marks topics as read to user
     *
     * @param topics List of topics to mark
     * @param user   User for which threads are marked as read
     * @return list of count posts for each topic, for verification
     */
    private Map<Long, Integer> markAllTopicsASRead(List<Topic> topics, JCUser user) {
        SQLQuery insertQuery = (SQLQuery) session.getNamedQuery("markAllTopicsRead");
        insertQuery.setCacheable(false);
        Map<Long, Integer> listCountPostsToTopics = new HashMap<Long, Integer>();

        for (Topic tp : topics) {
            Integer indexReadPosts = tp.getPosts().size() - 1;
            insertQuery.setParameter("uuid", UUID.randomUUID().toString())
                    .setParameter("user", user.getId())
                    .setParameter("lastPostIndex", indexReadPosts)
                    .setParameter("topic", tp.getId())
                    .executeUpdate();
            listCountPostsToTopics.put(tp.getId(), indexReadPosts);
        }
        return listCountPostsToTopics;
    }

    /**
     * Method returns the data read topics that are stored in the database
     *
     * @param topics List of topics, which marked as read to user
     * @param user   User for which threads are marked as read
     * @return List of count posts for each topic, for verification, stored in the database
     */
    private Map<Long, Integer> getActualListCountPostsToTopics(List<Topic> topics, JCUser user) {
        Map<Long, Integer> listCountPostsToTopics = new HashMap<Long, Integer>();

        SQLQuery checkInsert = (SQLQuery) session.createSQLQuery("select TOPIC_ID, " +
                "LAST_READ_POST_INDEX FROM LAST_READ_POSTS where TOPIC_ID IN (select TOPIC_ID from TOPIC where " +
                "BRANCH_ID=:branch) and USER_ID = :user");
        checkInsert.setParameter("user", user.getId());
        checkInsert.setParameter("branch", topics.get(0).getBranch().getId());
        List<Object[]> resultCheckInsert = checkInsert.list();

        for (Object[] record : resultCheckInsert) {
            listCountPostsToTopics.put(new Long(record[0].toString()), new Integer(record[1].toString()));
        }
        return listCountPostsToTopics;
    }

    /**
     * Method returns the data for each topics, which marked as read
     *
     * @param topics List of topics in branch
     * @return List of count posts for each topic
     */
    private Map<Long, Integer> getTopicAndCountOfPostsInBranch(List<Topic> topics) {
        Map<Long, Integer> result = new HashMap<Long, Integer>();
        for (Topic topic : topics) {
            //second parameter it's index of last post
            result.put(topic.getId(), topic.getPostCount() - 1);
        }
        return result;
    }
}
