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
import org.jtalks.jcommune.service.BranchService;
import org.jtalks.jcommune.service.SubscriptionService;
import org.jtalks.jcommune.service.TopicService;
import org.jtalks.jcommune.service.UserService;
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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * This test cover {@code TransactionalTopicService} logic validation.
 * Logic validation cover update/get/error cases by this class.
 *
 * @author Osadchuck Eugeny
 * @author Kravchenko Vitaliy
 * @author Kirill Afonin
 * @author Max Malakhov
 * @author Eugeny Batov
 */
public class TransactionalTopicServiceTest {

    final long TOPIC_ID = 999L;
    final long BRANCH_ID = 1L;
    final long POST_ID = 333L;
    final String TOPIC_TITLE = "topic title";
    final String BRANCH_NAME = "branch name";
    final String BRANCH_DESCRIPTION = "branch description";
    private static final String USERNAME = "username";
    private JCUser user;
    final String ANSWER_BODY = "Test Answer Body";
    private final String NEW_TOPIC_TITLE = "new title";
    private final String NEW_POST_CONTENT = "new body";
    private final boolean NEW_STICKED = false;
    private final boolean NEW_ANNOUNCEMENT = false;

    private TopicService topicService;

    @Mock
    private SecurityService securityService;
    @Mock
    private BranchService branchService;
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

    private CompoundAclBuilder<User> aclBuilder;

    @BeforeMethod
    public void setUp() throws Exception {
        initMocks(this);
        aclBuilder = mockAclBuilder();
        topicService = new TransactionalTopicService(
                topicDao,
                securityService,
                branchService,
                branchDao,
                notificationService,
                subscriptionService,
                userService);

        user = new JCUser(USERNAME, "email@mail.com", "password");

    }

    @Test
    public void testGetTopic() throws NotFoundException {
        Topic expectedTopic = new Topic(user, "title");
        when(topicDao.isExist(TOPIC_ID)).thenReturn(true);
        when(topicDao.get(TOPIC_ID)).thenReturn(expectedTopic);

        int viewsCount = expectedTopic.getViews();

        Topic actualTopic = topicService.get(TOPIC_ID);

        assertEquals(actualTopic.getViews(), viewsCount + 1);
        assertEquals(actualTopic, expectedTopic, "Topics aren't equal");
        verify(topicDao).isExist(TOPIC_ID);
        verify(topicDao).get(TOPIC_ID);
    }

    @Test(expectedExceptions = {NotFoundException.class})
    public void testGetTopicWithIncorrectId() throws NotFoundException {
        when(topicDao.isExist(POST_ID)).thenReturn(false);

        topicService.get(POST_ID);
    }

    @Test
    public void testReplyToTopic() throws NotFoundException {
        Topic answeredTopic = new Topic(user, "title");
        when(userService.getCurrentUser()).thenReturn(user);
        when(topicDao.isExist(TOPIC_ID)).thenReturn(true);
        when(topicDao.get(TOPIC_ID)).thenReturn(answeredTopic);
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
        Branch branch = new Branch(BRANCH_NAME, BRANCH_DESCRIPTION);
        createTopicStubs(branch);

        Topic createdTopic = topicService.createTopic(TOPIC_TITLE, ANSWER_BODY, BRANCH_ID, false);
        Post createdPost = createdTopic.getFirstPost();

        createTopicAssertions(branch, createdTopic, createdPost);
        createTopicVerifications(branch, createdTopic, createdPost);
    }

    @Test
    public void testCreateTopicWithoutSubscription() throws NotFoundException {
        Branch branch = new Branch(BRANCH_NAME, BRANCH_DESCRIPTION);
        createTopicStubs(branch);

        Topic createdTopic = topicService.createTopic(TOPIC_TITLE, ANSWER_BODY, BRANCH_ID, false);
        Post createdPost = createdTopic.getFirstPost();

        createTopicAssertions(branch, createdTopic, createdPost);
        createTopicVerifications(branch, createdTopic, createdPost);
    }

    private void createTopicStubs(Branch branch) throws NotFoundException {
        when(userService.getCurrentUser()).thenReturn(user);
        when(branchService.get(BRANCH_ID)).thenReturn(branch);
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

    private void createTopicVerifications(Branch branch, Topic createdTopic, Post createdPost)
            throws NotFoundException {
        verify(branchDao).update(branch);
        verify(aclBuilder, times(2)).grant(GeneralPermission.WRITE);
        verify(notificationService).branchChanged(branch);
    }


    @Test
    public void testGetAllTopicsPastLastDay() throws NotFoundException {
        int pageNumber = 1;
        int pageSize = 20;
        List<Topic> expectedList = Collections.nCopies(2, new Topic(user, "title"));
        Page<Topic> expectedPage = new PageImpl<Topic>(expectedList);
        when(topicDao.getTopicsUpdatedSince(Matchers.<DateTime>any(), Matchers.<JCommunePageRequest>any()))
                .thenReturn(expectedPage);

        JCUser currentUser = new JCUser("current", null, null);
        currentUser.setPageSize(pageSize);
        when(userService.getCurrentUser()).thenReturn(currentUser);

        Page<Topic> actualPage = topicService.getRecentTopics(pageNumber);

        assertNotNull(actualPage);
        assertEquals(expectedPage, actualPage);
        verify(topicDao).getTopicsUpdatedSince(Matchers.<DateTime>any(), Matchers.<JCommunePageRequest>any());
    }

    @Test
    public void testGetUnansweredTopics() {
        int pageNumber = 1;
        int pageSize = 20;
        List<Topic> expectedList = Collections.nCopies(2, new Topic(user, "title"));
        Page<Topic> expectedPage = new PageImpl<Topic>(expectedList);
        when(topicDao.getUnansweredTopics(Matchers.<JCommunePageRequest>any()))
                .thenReturn(expectedPage);
        JCUser currentUser = new JCUser("current", null, null);
        currentUser.setPageSize(pageSize);
        when(userService.getCurrentUser()).thenReturn(currentUser);

        Page<Topic> actualPage = topicService.getUnansweredTopics(pageNumber);
        assertNotNull(actualPage);
        assertEquals(actualPage, expectedPage);
    }


    @Test
    public void testDeleteTopic() throws NotFoundException {
        Topic topic = new Topic(user, "title");
        topic.setId(TOPIC_ID);
        Post firstPost = new Post(user, ANSWER_BODY);
        topic.addPost(firstPost);
        user.setPostCount(1);
        Branch branch = new Branch(BRANCH_NAME, BRANCH_DESCRIPTION);
        branch.addTopic(topic);
        when(topicDao.isExist(TOPIC_ID)).thenReturn(true);
        when(topicDao.get(TOPIC_ID)).thenReturn(topic);

        Branch branchFromWhichTopicDeleted = topicService.deleteTopic(TOPIC_ID);

        assertEquals(branchFromWhichTopicDeleted, branch);
        assertEquals(branch.getTopicCount(), 0);
        assertEquals(user.getPostCount(), 0);
        verify(branchDao).update(branch);
        verify(securityService).deleteFromAcl(Topic.class, TOPIC_ID);
        verify(notificationService).branchChanged(branch);
    }

    @Test(expectedExceptions = {NotFoundException.class})
    public void testDeleteNonExistentTopic() throws NotFoundException {
        when(topicDao.isExist(TOPIC_ID)).thenReturn(false);

        topicService.deleteTopic(TOPIC_ID);
    }

    @Test
    void testUpdateTopicWithSubscribe() throws NotFoundException {
        Topic topic = createTopic();
        Post post = createPost();
        topic.addPost(post);

        updateTopicStubs(topic);

        topicService.updateTopic(TOPIC_ID, NEW_TOPIC_TITLE, NEW_POST_CONTENT, NEW_STICKED, NEW_ANNOUNCEMENT, true);


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

        topicService.updateTopic(TOPIC_ID, NEW_TOPIC_TITLE, NEW_POST_CONTENT, NEW_STICKED, NEW_ANNOUNCEMENT, false);

        updateTopicVerifications(topic);
    }

    @Test
    void testUpdateTopicWithUnsubscribe() throws NotFoundException {
        Topic topic = createTopic();
        Post post = createPost();
        topic.addPost(post);
        subscribeUserOnTopic(user, topic);
        updateTopicStubs(topic);

        topicService.updateTopic(TOPIC_ID, NEW_TOPIC_TITLE, NEW_POST_CONTENT, NEW_STICKED, NEW_ANNOUNCEMENT, false);

        updateTopicVerifications(topic);
        verify(subscriptionService).toggleTopicSubscription(topic);
    }

    @Test
    void testUpdateTopicWithRepeatedUnsubscribe() throws NotFoundException {
        Topic topic = createTopic();
        Post post = createPost();
        topic.addPost(post);

        updateTopicStubs(topic);

        topicService.updateTopic(TOPIC_ID, NEW_TOPIC_TITLE, NEW_POST_CONTENT, NEW_STICKED, NEW_ANNOUNCEMENT, false);

        updateTopicVerifications(topic);
    }

    private Topic createTopic() {
        Topic topic = new Topic(user, "title");
        topic.setId(TOPIC_ID);
        topic.setTitle("title");
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

    private void updateTopicStubs(Topic topic) {
        when(userService.getCurrentUser()).thenReturn(user);
        when(topicDao.isExist(TOPIC_ID)).thenReturn(true);
        when(topicDao.get(TOPIC_ID)).thenReturn(topic);
    }

    private void updateTopicVerifications(Topic topic) {
        verify(topicDao).isExist(TOPIC_ID);
        verify(topicDao).get(TOPIC_ID);
        verify(notificationService).topicChanged(topic);
    }


    @Test
    void testUpdateTopicSimple() throws NotFoundException {
        String newTitle = "new title";
        String newBody = "new body";
        boolean newSticked = false;
        boolean newAnnouncement = false;
        Topic topic = new Topic(user, "title");
        topic.setId(TOPIC_ID);
        topic.setTitle("title");
        Post post = new Post(user, "content");
        post.setId(POST_ID);
        topic.addPost(post);

        when(topicDao.isExist(TOPIC_ID)).thenReturn(true);
        when(topicDao.get(TOPIC_ID)).thenReturn(topic);

        topicService.updateTopic(TOPIC_ID, newTitle, newBody);

        assertEquals(topic.getTitle(), newTitle);
        assertEquals(post.getPostContent(), newBody);
        assertEquals(topic.isSticked(), newSticked);
        assertEquals(topic.isAnnouncement(), newAnnouncement);

        verify(topicDao).isExist(TOPIC_ID);
        verify(topicDao).get(TOPIC_ID);
        verify(notificationService).topicChanged(topic);
    }

    @Test(expectedExceptions = {NotFoundException.class})
    void testUpdateTopicNonExistentTopic() throws NotFoundException {
        String newTitle = "new title";
        String newBody = "new body";
        boolean newSticked = false;
        boolean newAnnouncement = false;
        when(topicDao.isExist(TOPIC_ID)).thenReturn(false);

        topicService.updateTopic(TOPIC_ID, newTitle, newBody, newSticked, newAnnouncement, false);
    }

    @Test
    public void testMoveTopic() throws NotFoundException {
        Topic topic = new Topic(user, "title");
        Post firstPost = new Post(user, ANSWER_BODY);
        topic.addPost(firstPost);
        Branch currentBranch = new Branch(BRANCH_NAME, BRANCH_DESCRIPTION);
        currentBranch.addTopic(topic);
        Branch targetBranch = new Branch("target branch", "target branch description");

        when(topicDao.isExist(TOPIC_ID)).thenReturn(true);
        when(topicDao.get(TOPIC_ID)).thenReturn(topic);
        when(branchService.get(BRANCH_ID)).thenReturn(targetBranch);

        topicService.moveTopic(TOPIC_ID, BRANCH_ID);

        assertEquals(targetBranch.getTopicCount(), 1);
        verify(branchDao).update(targetBranch);
        verify(notificationService).topicMoved(topic, TOPIC_ID);
    }

    @Test(expectedExceptions = {NotFoundException.class})
    public void testMoveNonExistentTopic() throws NotFoundException {
        when(topicDao.isExist(TOPIC_ID)).thenReturn(false);

        topicService.moveTopic(TOPIC_ID, BRANCH_ID);
    }

    @Test(expectedExceptions = {NotFoundException.class})
    public void testMoveTopicInNonExistentTargetBranch() throws NotFoundException {
        when(branchDao.isExist(BRANCH_ID)).thenReturn(false);

        topicService.moveTopic(TOPIC_ID, BRANCH_ID);
    }

    @Test
    public void testGetTopics() {
        int pageSize = 50;
        Branch branch = new Branch(BRANCH_NAME, BRANCH_DESCRIPTION);
        Page<Topic> expectedPage = new PageImpl<Topic>(Collections.<Topic>emptyList());

        JCUser currentUser = new JCUser("current", null, null);
        currentUser.setPageSize(pageSize);
        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(topicDao.getTopics(
                Matchers.any(Branch.class), Matchers.any(JCommunePageRequest.class)))
                .thenReturn(expectedPage);

        Page<Topic> actualPage = topicService.getTopics(branch, pageSize, true);

        assertEquals(actualPage, expectedPage, "Service returned incorrect data for one page of topics");
        verify(topicDao).getTopics(
                Matchers.any(Branch.class), Matchers.any(JCommunePageRequest.class));
    }
}
