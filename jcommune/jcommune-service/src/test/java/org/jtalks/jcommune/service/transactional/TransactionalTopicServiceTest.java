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
package org.jtalks.jcommune.service.transactional;

import org.joda.time.DateTime;
import org.jtalks.jcommune.model.dao.BranchDao;
import org.jtalks.jcommune.model.dao.TopicDao;
import org.jtalks.jcommune.model.entity.Branch;
import org.jtalks.jcommune.model.entity.Post;
import org.jtalks.jcommune.model.entity.Topic;
import org.jtalks.jcommune.model.entity.User;
import org.jtalks.jcommune.service.BranchService;
import org.jtalks.jcommune.service.SecurityService;
import org.jtalks.jcommune.service.TopicService;
import org.jtalks.jcommune.service.exceptions.NotFoundException;
import org.mockito.Matchers;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

/**
 * This test cover {@code TransactionalTopicService} logic validation.
 * Logic validation cover update/get/error cases by this class.
 *
 * @author Osadchuck Eugeny
 * @author Kravchenko Vitaliy
 * @author Kirill Afonin
 */
public class TransactionalTopicServiceTest {

    final long TOPIC_ID = 999;
    final long BRANCH_ID = 1L;
    final long POST_ID = 333;
    final String TOPIC_TITLE = "topic title";
    final String ANSWER_BODY = "Test Answer Body";
    final DateTime TOPIC_CREATION_DATE = new DateTime();

    private TopicService topicService;
    private SecurityService securityService;
    private BranchService branchService;
    private TopicDao topicDao;
    private BranchDao branchDao;

    @BeforeMethod
    public void setUp() throws Exception {
        topicDao = mock(TopicDao.class);
        branchService = mock(BranchService.class);
        securityService = mock(SecurityService.class);
        branchDao = mock(BranchDao.class);
        topicService = new TransactionalTopicService(topicDao, securityService, branchService, branchDao);
    }

    @Test
    public void testDelete() throws NotFoundException {
        when(topicDao.isExist(POST_ID)).thenReturn(true);

        topicService.delete(POST_ID);

        verify(topicDao).isExist(POST_ID);
        verify(topicDao, times(1)).delete(POST_ID);
    }

    @Test(expectedExceptions = {NotFoundException.class})
    public void testDeleteIncorrectId() throws NotFoundException {
        when(topicDao.isExist(POST_ID)).thenReturn(false);

        topicService.delete(POST_ID);
    }

    @Test
    public void testGet() throws NotFoundException {
        Topic topic = Topic.createNewTopic();
        when(topicDao.isExist(TOPIC_ID)).thenReturn(true);
        when(topicDao.get(TOPIC_ID)).thenReturn(topic);

        Topic actualTopic = topicService.get(TOPIC_ID);

        assertEquals(actualTopic, topic, "Topics aren't equals");
        verify(topicDao).isExist(TOPIC_ID);
        verify(topicDao).get(TOPIC_ID);
    }

    @Test(expectedExceptions = {NotFoundException.class})
    public void testGetIncorrectId() throws NotFoundException {
        when(topicDao.isExist(POST_ID)).thenReturn(false);

        topicService.get(POST_ID);
    }

    @Test
    public void testAddAnswer() throws NotFoundException {
        Topic topic = Topic.createNewTopic();
        User author = new User();
        when(securityService.getCurrentUser()).thenReturn(author);
        when(topicDao.get(TOPIC_ID)).thenReturn(topic);

        topicService.addAnswer(TOPIC_ID, ANSWER_BODY);

        Post createdPost = topic.getPosts().get(0);
        assertEquals(createdPost.getPostContent(), ANSWER_BODY);
        assertEquals(createdPost.getUserCreated(), author);
        verify(securityService).getCurrentUser();
        verify(topicDao).get(TOPIC_ID);
        verify(topicDao).saveOrUpdate(topic);
    }

    @Test(expectedExceptions = {IllegalStateException.class})
    public void testAddAnswerWithoutCurrentUser() throws NotFoundException {
        when(securityService.getCurrentUser()).thenReturn(null);

        topicService.addAnswer(TOPIC_ID, ANSWER_BODY);
    }

    @Test(expectedExceptions = {NotFoundException.class})
    public void testAddAnswerWhenTopicNotExist() throws NotFoundException {
        when(securityService.getCurrentUser()).thenReturn(new User());
        when(topicDao.get(TOPIC_ID)).thenReturn(null);

        topicService.addAnswer(TOPIC_ID, ANSWER_BODY);
    }

    @Test
    public void testCreateTopic() throws NotFoundException {
        User author = new User();
        Branch branch = new Branch();
        when(securityService.getCurrentUser()).thenReturn(author);
        when(branchService.get(BRANCH_ID)).thenReturn(branch);

        Topic createdTopic = topicService.createTopic(TOPIC_TITLE, ANSWER_BODY, BRANCH_ID);

        Post createdPost = createdTopic.getPosts().get(0);
        assertEquals(createdTopic.getTitle(), TOPIC_TITLE);
        assertEquals(createdTopic.getTopicStarter(), author);
        assertEquals(createdTopic.getBranch(), branch);
        assertEquals(createdPost.getUserCreated(), author);
        assertEquals(createdPost.getPostContent(), ANSWER_BODY);
        verify(securityService).getCurrentUser();
        verify(topicDao).saveOrUpdate(createdTopic);
        verify(branchService).get(BRANCH_ID);
    }

    @Test(expectedExceptions = {IllegalStateException.class})
    public void testCreateTopicWithoutCurrentUser() throws NotFoundException {
        when(securityService.getCurrentUser()).thenReturn(null);

        topicService.createTopic(TOPIC_TITLE, ANSWER_BODY, 1L);
    }

    @Test
    public void deletePostTest() throws NotFoundException {
        Topic topic = getTopic(true);
        int expectedPostCount = topic.getPosts().size();
        when(topicDao.get(TOPIC_ID)).thenReturn(topic);

        topicService.deletePost(TOPIC_ID, POST_ID);

        int actualPostCount = topic.getPosts().size();
        assertTrue(actualPostCount == 0 || actualPostCount < expectedPostCount, "Post was not deleted");
        verify(topicDao, times(1)).get(TOPIC_ID);
        verify(topicDao, times(1)).saveOrUpdate(Matchers.any(Topic.class));
    }

    @Test
    public void testGetTopicsRangeInBranch() throws NotFoundException {
        int start = 1;
        int max = 2;
        long branchId = 1L;
        List<Topic> list = new ArrayList<Topic>();
        list.add(Topic.createNewTopic());
        list.add(Topic.createNewTopic());
        when(branchDao.isExist(branchId)).thenReturn(true);
        when(topicDao.getTopicRangeInBranch(branchId, start, max)).thenReturn(list);

        List<Topic> topics = topicService.getTopicRangeInBranch(branchId, start, max);

        assertNotNull(topics);
        assertEquals(max, topics.size(), "Unexpected list size");
        verify(topicDao).getTopicRangeInBranch(branchId, start, max);
        verify(branchDao).isExist(branchId);
    }

    @Test(expectedExceptions = {NotFoundException.class})
    public void testGetTopicsRangeInNonExistentBranch() throws NotFoundException {
        long branchId = 1L;
        when(branchDao.isExist(branchId)).thenReturn(false);

        topicService.getTopicRangeInBranch(branchId, 1, 5);
    }

    @Test
    public void testGetTopicsInBranchCount() throws NotFoundException {
        long branchId = 1L;
        when(branchDao.isExist(branchId)).thenReturn(true);
        when(topicDao.getTopicsInBranchCount(branchId)).thenReturn(10);

        int count = topicService.getTopicsInBranchCount(branchId);

        assertEquals(count, 10);
        verify(topicDao).getTopicsInBranchCount(branchId);
        verify(branchDao).isExist(branchId);
    }

    @Test(expectedExceptions = {NotFoundException.class})
    public void testGetTopicsCountInNonExistentBranch() throws NotFoundException {
        long branchId = 1L;
        when(branchDao.isExist(branchId)).thenReturn(false);

        topicService.getTopicsInBranchCount(branchId);
    }

    /**
     * Create new dummy User with username "Test" and user.id  equal to 100500.
     *
     * @return the user
     */
    private User getUser() {
        User currentUser = new User();
        currentUser.setId(100500);
        currentUser.setUsername("Test");
        return currentUser;
    }

    /**
     * Get dummy topic.
     *
     * @param withPosts - set to true if you want add collection of 10 posts to topic
     * @return - dummy topic with topicId=TOPIC_ID,topictitle= TOPIC_TITLE,
     *         topic_creationDate=TOPIC_CREATION_DATE and posts. Posts ids started from POST_ID to POST_ID plus 10.
     */
    private Topic getTopic(boolean withPosts) {
        User topicStarter = getUser();
        Topic topic = new Topic();
        topic.setCreationDate(TOPIC_CREATION_DATE);
        topic.setId(TOPIC_ID);
        topic.setUuid("xxxxx123");
        topic.setTitle(TOPIC_TITLE);
        topic.setTopicStarter(topicStarter);
        if (withPosts) {
            for (long i = POST_ID; i <= POST_ID + 10; i++) {
                Post post = Post.createNewPost();
                post.setId(i);
                topic.addPost(post);
            }
        }
        return topic;
    }
}
