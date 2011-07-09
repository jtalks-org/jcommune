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
 * @author Max Malakhov
 */
public class TransactionalTopicServiceTest {

    final long TOPIC_ID = 999L;
    final long BRANCH_ID = 1L;
    final long POST_ID = 333L;
    final String TOPIC_TITLE = "topic title";
    final String ANSWER_BODY = "Test Answer Body";

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
    public void testGet() throws NotFoundException {
        Topic expectedTopic = Topic.createNewTopic();
        when(topicDao.isExist(TOPIC_ID)).thenReturn(true);
        when(topicDao.get(TOPIC_ID)).thenReturn(expectedTopic);

        Topic actualTopic = topicService.get(TOPIC_ID);

        assertEquals(actualTopic, expectedTopic, "Topics aren't equals");
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
        Topic answeredTopic = Topic.createNewTopic();
        User author = new User();
        when(securityService.getCurrentUser()).thenReturn(author);
        when(topicDao.get(TOPIC_ID)).thenReturn(answeredTopic);

        Post createdPost = topicService.addAnswer(TOPIC_ID, ANSWER_BODY);

        assertEquals(createdPost.getPostContent(), ANSWER_BODY);
        assertEquals(createdPost.getUserCreated(), author);
        verify(securityService).getCurrentUser();
        verify(topicDao).get(TOPIC_ID);
        verify(topicDao).saveOrUpdate(answeredTopic);
        verify(securityService).grantAdminPermissionToCurrentUserAndAdmins(createdPost);
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

        Post createdPost = createdTopic.getFirstPost();
        assertEquals(createdTopic.getTitle(), TOPIC_TITLE);
        assertEquals(createdTopic.getTopicStarter(), author);
        assertEquals(createdTopic.getBranch(), branch);
        assertEquals(createdPost.getUserCreated(), author);
        assertEquals(createdPost.getPostContent(), ANSWER_BODY);
        verify(securityService).getCurrentUser();
        verify(topicDao).saveOrUpdate(createdTopic);
        verify(branchService).get(BRANCH_ID);
        verify(securityService).grantAdminPermissionToCurrentUserAndAdmins(createdPost);
        verify(securityService).grantAdminPermissionToCurrentUserAndAdmins(createdTopic);
    }

    @Test(expectedExceptions = {IllegalStateException.class})
    public void testCreateTopicWithoutCurrentUser() throws NotFoundException {
        when(securityService.getCurrentUser()).thenReturn(null);

        topicService.createTopic(TOPIC_TITLE, ANSWER_BODY, 1L);
    }

    @Test
    public void testDeletePost() throws NotFoundException {
        Topic topic = Topic.createNewTopic();
        Post post1 = Post.createNewPost();
        post1.setId(1L);
        Post postForDelete = Post.createNewPost();
        postForDelete.setId(POST_ID);
        topic.addPost(post1);
        topic.addPost(postForDelete);
        when(topicDao.get(TOPIC_ID)).thenReturn(topic);

        topicService.deletePost(TOPIC_ID, POST_ID);

        assertFalse(topic.getPosts().contains(postForDelete), "Post not deleted from list");
        verify(topicDao).get(TOPIC_ID);
        verify(topicDao).saveOrUpdate(topic);
        verify(securityService).deleteFromAcl(postForDelete);
    }

    @Test(expectedExceptions = {NotFoundException.class})
    public void testDeletePostFromNonExistentTopic() throws NotFoundException {
        when(topicDao.get(TOPIC_ID)).thenReturn(null);

        topicService.deletePost(TOPIC_ID, POST_ID);
    }

    @Test(expectedExceptions = {NotFoundException.class})
    public void testDeleteNonExistentPost() throws NotFoundException {
        Topic topic = Topic.createNewTopic();
        Post post1 = Post.createNewPost();
        post1.setId(1L);
        Post post2 = Post.createNewPost();
        post2.setId(2L);
        topic.addPost(post1);
        topic.addPost(post2);
        when(topicDao.get(TOPIC_ID)).thenReturn(topic);

        topicService.deletePost(TOPIC_ID, POST_ID);
    }

    @Test
    public void testGetTopicsRangeInBranch() throws NotFoundException {
        int start = 1;
        int max = 2;
        List<Topic> expectedList = new ArrayList<Topic>();
        expectedList.add(Topic.createNewTopic());
        expectedList.add(Topic.createNewTopic());
        when(branchDao.isExist(BRANCH_ID)).thenReturn(true);
        when(topicDao.getTopicRangeInBranch(BRANCH_ID, start, max)).thenReturn(expectedList);

        List<Topic> topics = topicService.getTopicRangeInBranch(BRANCH_ID, start, max);

        assertNotNull(topics);
        assertEquals(topics.size(), max, "Unexpected list size");
        verify(topicDao).getTopicRangeInBranch(BRANCH_ID, start, max);
        verify(branchDao).isExist(BRANCH_ID);
    }

    @Test(expectedExceptions = {NotFoundException.class})
    public void testGetTopicsRangeInNonExistentBranch() throws NotFoundException {
        when(branchDao.isExist(BRANCH_ID)).thenReturn(false);

        topicService.getTopicRangeInBranch(BRANCH_ID, 1, 5);
    }

    @Test
    public void testGetTopicsInBranchCount() throws NotFoundException {
        int expectedCount = 10;
        when(branchDao.isExist(BRANCH_ID)).thenReturn(true);
        when(topicDao.getTopicsInBranchCount(BRANCH_ID)).thenReturn(expectedCount);

        int count = topicService.getTopicsInBranchCount(BRANCH_ID);

        assertEquals(count, expectedCount);
        verify(topicDao).getTopicsInBranchCount(BRANCH_ID);
        verify(branchDao).isExist(BRANCH_ID);
    }

    @Test(expectedExceptions = {NotFoundException.class})
    public void testGetTopicsCountInNonExistentBranch() throws NotFoundException {
        when(branchDao.isExist(BRANCH_ID)).thenReturn(false);

        topicService.getTopicsInBranchCount(BRANCH_ID);
    }

    @Test
    public void testDeleteTopic() throws NotFoundException {
        when(topicDao.isExist(TOPIC_ID)).thenReturn(true);

        topicService.deleteTopic(TOPIC_ID);

        verify(topicDao).delete(TOPIC_ID);
        verify(securityService).deleteFromAcl(Topic.class, TOPIC_ID);
    }

    @Test(expectedExceptions = {NotFoundException.class})
    public void testDeleteNonExistentTopic() throws NotFoundException {
        when(topicDao.isExist(TOPIC_ID)).thenReturn(false);

        topicService.deleteTopic(TOPIC_ID);
    }

    @Test 
    void testSaveTopic() throws NotFoundException {
        String newTitle = "new title";
        String newBody = "new body";
        Topic topic = Topic.createNewTopic();
        topic.setId(TOPIC_ID);
        topic.setTitle("title");
        Post post = Post.createNewPost();
        post.setId(POST_ID);
        post.setPostContent("body");
        topic.setFirstPost(post);

        when(topicDao.isExist(TOPIC_ID)).thenReturn(true);
        when(topicDao.get(TOPIC_ID)).thenReturn(topic);

        topicService.saveTopic(TOPIC_ID, newTitle, newBody);

        assertEquals(topic.getTitle(), newTitle);
        assertEquals(post.getPostContent(), newBody);

        verify(topicDao).isExist(TOPIC_ID);
        verify(topicDao).get(TOPIC_ID);
        verify(topicDao).saveOrUpdate(topic);
    }

    @Test(expectedExceptions = {NotFoundException.class})
    void testSaveTopicNonExistentTopic() throws NotFoundException {
        String newTitle = "new title";
        String newBody = "new body";
        when(topicDao.isExist(TOPIC_ID)).thenReturn(false);

        topicService.saveTopic(TOPIC_ID, newTitle, newBody);
    }
}
