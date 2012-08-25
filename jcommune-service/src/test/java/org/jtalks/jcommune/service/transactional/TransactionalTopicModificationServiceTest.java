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
package org.jtalks.jcommune.service.transactional;

import org.joda.time.DateTime;
import org.jtalks.common.model.entity.User;
import org.jtalks.common.model.permissions.GeneralPermission;
import org.jtalks.common.security.SecurityService;
import org.jtalks.common.security.acl.builders.CompoundAclBuilder;
import org.jtalks.jcommune.model.dao.BranchDao;
import org.jtalks.jcommune.model.dao.TopicDao;
import org.jtalks.jcommune.model.dto.JCommunePageRequest;
import org.jtalks.jcommune.model.entity.Branch;
import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.model.entity.Post;
import org.jtalks.jcommune.model.entity.Topic;
import org.jtalks.jcommune.service.*;
import org.jtalks.jcommune.service.exceptions.NotFoundException;
import org.jtalks.jcommune.service.nontransactional.NotificationService;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.jtalks.jcommune.service.TestUtils.mockAclBuilder;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * This test cover {@code TransactionalTopicModificationService} logic validation.
 * Logic validation cover update/get/error cases by this class.
 *
 * @author Osadchuck Eugeny
 * @author Kravchenko Vitaliy
 * @author Kirill Afonin
 * @author Max Malakhov
 * @author Eugeny Batov
 */
public class TransactionalTopicModificationServiceTest {

    private final long TOPIC_ID = 999L;
    private final long BRANCH_ID = 1L;
    private final long POST_ID = 333L;
    private final String TOPIC_TITLE = "topic title";
    private final String BRANCH_NAME = "branch name";
    private final String BRANCH_DESCRIPTION = "branch description";
    private static final String USERNAME = "username";
    private JCUser user;
    private final String ANSWER_BODY = "Test Answer Body";
    private final String NEW_TOPIC_TITLE = "new title";
    private final String NEW_POST_CONTENT = "new body";
    private final boolean NEW_STICKED = false;
    private final boolean NEW_ANNOUNCEMENT = false;

    private TopicModificationService topicService;

    @Mock
    private SecurityService securityService;
    @Mock
    private TopicDao topicDao;
    @Mock
    private BranchDao branchDao;
    @Mock
    private NotificationService notificationService;
    @Mock
    private SubscriptionService subscriptionService;
    @Mock
    private UserService userService;
    @Mock
    private PollService pollService;
    @Mock
    private TopicFetchService topicFetchService;

    private CompoundAclBuilder<User> aclBuilder;

    @BeforeMethod
    public void setUp() throws Exception {
        initMocks(this);
        aclBuilder = mockAclBuilder();
        topicService = new TransactionalTopicModificationService(
                topicDao,
                securityService,
                branchDao,
                notificationService,
                subscriptionService,
                userService,
                pollService, topicFetchService);

        user = new JCUser(USERNAME, "email@mail.com", "password");

    }


    @Test
    public void testReplyToTopic() throws NotFoundException {
        Topic answeredTopic = new Topic(user, "title");
        when(userService.getCurrentUser()).thenReturn(user);
        when(topicFetchService.get(TOPIC_ID)).thenReturn(answeredTopic);
        when(securityService.<User>createAclBuilder()).thenReturn(aclBuilder);

        Post createdPost = topicService.replyToTopic(TOPIC_ID, ANSWER_BODY, BRANCH_ID);

        assertEquals(createdPost.getPostContent(), ANSWER_BODY);
        assertEquals(createdPost.getUserCreated(), user);
        assertEquals(user.getPostCount(), 1);

        verify(aclBuilder).grant(GeneralPermission.WRITE);
        verify(aclBuilder).to(user);
        verify(aclBuilder).on(createdPost);
        verify(notificationService).topicChanged(answeredTopic);
    }

    @Test
    public void testCreateTopicWithSubscription() throws NotFoundException {
        Branch branch = createBranch();

        createTopicStubs(branch);
        Topic dto = createTopic();
        Topic createdTopic = topicService.createTopic(dto, ANSWER_BODY, false);
        Post createdPost = createdTopic.getFirstPost();

        createTopicAssertions(branch, createdTopic, createdPost);
        createTopicVerifications(branch);
    }

    @Test
    public void testCreateTopicWithoutSubscription() throws NotFoundException {
        Branch branch = createBranch();
        createTopicStubs(branch);
        Topic dto = createTopic();
        Topic createdTopic = topicService.createTopic(dto, ANSWER_BODY, false);
        Post createdPost = createdTopic.getFirstPost();

        createTopicAssertions(branch, createdTopic, createdPost);
        createTopicVerifications(branch);
    }

    private void createTopicStubs(Branch branch) throws NotFoundException {
        when(userService.getCurrentUser()).thenReturn(user);
        when(branchDao.get(BRANCH_ID)).thenReturn(branch);
        when(securityService.<User>createAclBuilder()).thenReturn(aclBuilder);
    }

    private void createTopicAssertions(Branch branch, Topic createdTopic, Post createdPost) {
        assertEquals(createdTopic.getTitle(), TOPIC_TITLE);
        assertEquals(createdTopic.getTopicStarter(), user);
        assertEquals(createdTopic.getBranch(), branch);
        assertEquals(createdPost.getUserCreated(), user);
        assertEquals(createdPost.getPostContent(), ANSWER_BODY);
        assertEquals(user.getPostCount(), 1);
    }

    private void createTopicVerifications(Branch branch)
            throws NotFoundException {
        verify(branchDao).update(branch);
        verify(aclBuilder, times(2)).grant(GeneralPermission.WRITE);
        verify(notificationService).branchChanged(branch);
    }

    @Test
    public void testDeleteTopic() throws NotFoundException {
        Topic topic = new Topic(user, "title");
        topic.setId(TOPIC_ID);
        Post firstPost = new Post(user, ANSWER_BODY);
        topic.addPost(firstPost);
        user.setPostCount(1);
        Branch branch = createBranch();
        branch.addTopic(topic);
        when(topicDao.isExist(TOPIC_ID)).thenReturn(true);
        when(topicDao.get(TOPIC_ID)).thenReturn(topic);

        topicService.deleteTopic(topic);

        assertEquals(branch.getTopicCount(), 0);
        assertEquals(user.getPostCount(), 0);
        verify(branchDao).update(branch);
        verify(securityService).deleteFromAcl(Topic.class, TOPIC_ID);
        verify(notificationService).branchChanged(branch);
    }


    @Test
    public void testDeleteTopicSilent() throws NotFoundException {
        Topic topic = new Topic(user, "title");
        topic.setId(TOPIC_ID);
        Post firstPost = new Post(user, ANSWER_BODY);
        topic.addPost(firstPost);
        user.setPostCount(1);
        Branch branch = createBranch();
        branch.addTopic(topic);
        when(topicFetchService.get(TOPIC_ID)).thenReturn(topic);

        topicService.deleteTopicSilent(TOPIC_ID);

        assertEquals(branch.getTopicCount(), 0);
        assertEquals(user.getPostCount(), 0);
        verify(branchDao).update(branch);
        verify(securityService).deleteFromAcl(Topic.class, TOPIC_ID);
    }
    
    @Test(expectedExceptions = {NotFoundException.class})
    public void testDeleteTopicSilentNonExistent() throws NotFoundException {
        when(topicFetchService.get(TOPIC_ID)).thenThrow(new NotFoundException());

        topicService.deleteTopicSilent(TOPIC_ID);
    }

    @Test
    void testUpdateTopicWithSubscribe() throws NotFoundException {
        Topic topic = createTopic();
        Post post = createPost();
        topic.addPost(post);

        updateTopicStubs(topic);
        Topic newTopic = createNewTopic();
        newTopic.setId(topic.getId());
        topicService.updateTopic(newTopic, NEW_POST_CONTENT, true);

        updateTopicVerifications(topic);
        verify(subscriptionService).toggleTopicSubscription(topic);
    }

    @Test
    void testUpdateTopicWithRepeatedSubscribe() throws NotFoundException {
        Topic topic = createTopic();
        Post post = createPost();
        topic.addPost(post);
        subscribeUserOnTopic(user, topic);
        updateTopicStubs(topic);
        Topic newTopic = createNewTopic();
        newTopic.setId(topic.getId());
        when(topicFetchService.get(TOPIC_ID)).thenReturn(topic);

        topicService.updateTopic(newTopic, NEW_POST_CONTENT, false);

        updateTopicVerifications(topic);
    }

    @Test
    void testUpdateTopicWithUnsubscribe() throws NotFoundException {
        Topic topic = createTopic();
        Post post = createPost();
        topic.addPost(post);
        subscribeUserOnTopic(user, topic);
        updateTopicStubs(topic);
        Topic newTopic = createNewTopic();
        newTopic.setId(topic.getId());
        topicService.updateTopic(newTopic, NEW_POST_CONTENT, false);

        updateTopicVerifications(topic);
        verify(subscriptionService).toggleTopicSubscription(topic);
    }

    @Test
    void testUpdateTopicWithRepeatedUnsubscribe() throws NotFoundException {
        Topic topic = createTopic();
        Post post = createPost();
        topic.addPost(post);

        updateTopicStubs(topic);
        Topic newTopic = createNewTopic();
        newTopic.setId(topic.getId());
        topicService.updateTopic(newTopic, NEW_POST_CONTENT, false);

        updateTopicVerifications(topic);
    }


    private void updateTopicStubs(Topic topic) throws NotFoundException {
        when(userService.getCurrentUser()).thenReturn(user);
        when(topicFetchService.get(TOPIC_ID)).thenReturn(topic);
    }

    private void updateTopicVerifications(Topic topic) throws NotFoundException {
        verify(topicFetchService).get(TOPIC_ID);
        verify(notificationService).topicChanged(topic);
    }


    @Test
    void testUpdateTopicSimple() throws NotFoundException {
        Topic topic = new Topic(user, "title");
        topic.setId(TOPIC_ID);
        topic.setTitle("title");
        Post post = new Post(user, "content");
        post.setId(POST_ID);
        topic.addPost(post);

        when(topicFetchService.get(TOPIC_ID)).thenReturn(topic);
        Topic newTopic = createNewTopic();
        newTopic.setId(topic.getId());
        topicService.updateTopic(newTopic, NEW_POST_CONTENT, false);

        assertEquals(topic.getTitle(), NEW_TOPIC_TITLE);
        assertEquals(post.getPostContent(), NEW_POST_CONTENT);
        assertEquals(topic.isSticked(), NEW_STICKED);
        assertEquals(topic.isAnnouncement(), NEW_ANNOUNCEMENT);

        verify(notificationService).topicChanged(topic);
    }

    @Test(expectedExceptions = {NotFoundException.class})
    void testUpdateTopicNonExistentTopic() throws NotFoundException {
        Topic topic = new Topic();
        topic.setId(TOPIC_ID);
        topic.setTitle("new title");
        String newBody = "new body";
        topic.setSticked(false);
        topic.setAnnouncement(false);
        when(topicFetchService.get(TOPIC_ID)).thenThrow(new NotFoundException());

        topicService.updateTopic(topic, newBody, false);
    }

    @Test
    public void testMoveTopic() throws NotFoundException {
        Topic topic = new Topic(user, "title");
        topic.setId(TOPIC_ID);
        Post firstPost = new Post(user, ANSWER_BODY);
        topic.addPost(firstPost);
        Branch currentBranch = createBranch();
        currentBranch.addTopic(topic);
        Branch targetBranch = new Branch("target branch", "target branch description");

        when(topicFetchService.get(TOPIC_ID)).thenReturn(topic);
        when(branchDao.get(BRANCH_ID)).thenReturn(targetBranch);

        topicService.moveTopic(topic, BRANCH_ID);

        assertEquals(targetBranch.getTopicCount(), 1);
        verify(branchDao).update(targetBranch);
        verify(notificationService).topicMoved(topic, TOPIC_ID);
    }

    private Branch createBranch() {
        Branch branch = new Branch(BRANCH_NAME, BRANCH_DESCRIPTION);
        branch.setId(BRANCH_ID);
        branch.setUuid("uuid");
        return branch;
    }

    private Topic createTopic() {
        Topic topic = new Topic(user, TOPIC_TITLE);
        topic.setId(TOPIC_ID);
        Branch branch = createBranch();
        topic.setBranch(branch);
        return topic;
    }

    private Topic createNewTopic() {
        Topic topic = new Topic();
        topic.setTitle(NEW_TOPIC_TITLE);
        topic.setSticked(NEW_STICKED);
        topic.setAnnouncement(NEW_ANNOUNCEMENT);
        return topic;
    }

    private Post createPost() {
        Post post = new Post(user, "content");
        post.setId(POST_ID);
        return post;
    }

    private void subscribeUserOnTopic(JCUser user, Topic topic) {
        Set<JCUser> subscribers = new HashSet<JCUser>();
        subscribers.add(user);
        topic.setSubscribers(subscribers);
    }

}
