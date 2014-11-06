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
import org.jtalks.common.service.security.SecurityContextFacade;
import org.jtalks.jcommune.model.dao.BranchDao;
import org.jtalks.jcommune.model.dao.PostDao;
import org.jtalks.jcommune.model.dao.TopicDao;
import org.jtalks.jcommune.model.entity.*;
import org.jtalks.jcommune.service.*;
import org.jtalks.jcommune.service.exceptions.NotFoundException;
import org.jtalks.jcommune.service.nontransactional.MentionedUsers;
import org.jtalks.jcommune.service.nontransactional.NotificationService;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.*;

import static org.jtalks.jcommune.service.TestUtils.mockAclBuilder;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.testng.Assert.*;

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
    private final String TOPIC_TITLE = "topic title";
    private JCUser user;
    private final String ANSWER_BODY = "Test Answer Body";

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
    @Mock
    private SecurityContextFacade securityContextFacade;
    @Mock
    private PermissionEvaluator permissionEvaluator;
    @Mock
    private SecurityContext securityContext;
    @Mock
    private BranchLastPostService branchLastPostService;
    @Mock
    private LastReadPostService lastReadPostService;
    @Mock
    private MentionedUsers mentionedUsers;
    @Mock
    private PostDao postDao;

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
                pollService,
                securityContextFacade,
                permissionEvaluator,
                branchLastPostService,
                lastReadPostService,
                postDao);

        user = new JCUser("username", "email@mail.com", "password");
        when(securityContextFacade.getContext()).thenReturn(securityContext);
    }

    @Test
    public void testReplyToTopic() throws NotFoundException {
        Topic answeredTopic = new Topic(user, "title");
        answeredTopic.setBranch(new Branch("name", "description"));
        when(userService.getCurrentUser()).thenReturn(user);
        when(topicDao.get(TOPIC_ID)).thenReturn(answeredTopic);
        when(securityService.<User>createAclBuilder()).thenReturn(aclBuilder);

        Post createdPost = topicService.replyToTopic(TOPIC_ID, ANSWER_BODY, BRANCH_ID);

        assertEquals(createdPost.getPostContent(), ANSWER_BODY);
        assertEquals(createdPost.getUserCreated(), user);
        assertEquals(user.getPostCount(), 1);

        verify(aclBuilder).grant(GeneralPermission.WRITE);
        verify(aclBuilder).to(user);
        verify(aclBuilder).on(createdPost);
        verify(notificationService).subscribedEntityChanged(answeredTopic);
    }

    @Test
    public void testAutoSubscriptionOnTopicReplyIfAutosubscribeEnabled() throws NotFoundException {
        Topic answeredTopic = ObjectsFactory.topics(user, 1).get(0);
        user.setAutosubscribe(true);
        when(userService.getCurrentUser()).thenReturn(user);
        when(topicDao.get(TOPIC_ID)).thenReturn(answeredTopic);
        when(securityService.<User>createAclBuilder()).thenReturn(aclBuilder);

        topicService.replyToTopic(TOPIC_ID, ANSWER_BODY, BRANCH_ID);

        assertTrue(answeredTopic.userSubscribed(user));
    }

    @Test
    public void testAutoSubscriptionOnTopicReplyIfAutosubscribeDisabled() throws NotFoundException {
        user.setAutosubscribe(false);
        Topic answeredTopic = ObjectsFactory.topics(user, 1).get(0);
        when(userService.getCurrentUser()).thenReturn(user);
        when(topicDao.get(TOPIC_ID)).thenReturn(answeredTopic);
        when(securityService.<User>createAclBuilder()).thenReturn(aclBuilder);

        topicService.replyToTopic(TOPIC_ID, ANSWER_BODY, BRANCH_ID);

        assertFalse(answeredTopic.userSubscribed(user));
    }

    @Test
    public void replyTopicShouldNotifyMentionedInReplyUsers() throws NotFoundException {
        Topic answeredTopic = ObjectsFactory.topics(user, 1).get(0);
        when(userService.getCurrentUser()).thenReturn(user);
        when(topicDao.get(TOPIC_ID)).thenReturn(answeredTopic);
        when(securityService.<User>createAclBuilder()).thenReturn(aclBuilder);
        String answerWithUserMentioning = "[user]Shogun[/user] was mentioned";

        Post answerPost = topicService.replyToTopic(TOPIC_ID, answerWithUserMentioning, BRANCH_ID);

        verify(userService).notifyAndMarkNewlyMentionedUsers(answerPost);
    }

    @Test(expectedExceptions = AccessDeniedException.class)
    public void testReplyToClosedTopic() throws NotFoundException {
        Topic answeredTopic = new Topic(user, "title");
        answeredTopic.setBranch(new Branch("", ""));
        answeredTopic.setClosed(true);
        when(topicDao.get(TOPIC_ID)).thenReturn(answeredTopic);

        topicService.replyToTopic(TOPIC_ID, ANSWER_BODY, BRANCH_ID);
    }

    @Test
    public void testReplyToClosedTopicWithGrant() throws NotFoundException {
        Topic answeredTopic = new Topic(user, "title");
        answeredTopic.setBranch(new Branch("", ""));
        answeredTopic.setClosed(true);
        when(permissionEvaluator.hasPermission(
                Matchers.<Authentication>any(), anyLong(), anyString(), anyString()))
                .thenReturn(true);
        when(userService.getCurrentUser()).thenReturn(user);
        when(topicDao.get(TOPIC_ID)).thenReturn(answeredTopic);
        when(securityService.<User>createAclBuilder()).thenReturn(aclBuilder);

        Post createdPost = topicService.replyToTopic(TOPIC_ID, ANSWER_BODY, BRANCH_ID);

        assertEquals(createdPost.getPostContent(), ANSWER_BODY);
        assertEquals(createdPost.getUserCreated(), user);
        assertEquals(user.getPostCount(), 1);

        verify(aclBuilder).grant(GeneralPermission.WRITE);
        verify(aclBuilder).to(user);
        verify(aclBuilder).on(createdPost);
        verify(notificationService).subscribedEntityChanged(answeredTopic);
    }

    @Test
    public void testRunSubscriptionByCreateTopicWhenNotificationTrue() throws NotFoundException {
        Branch branch = createBranch();
        user.setAutosubscribe(true);
        when(userService.getCurrentUser()).thenReturn(user);
        createTopicStubs(branch);
        Topic dto = createTopic();
        Topic createdTopic = topicService.createTopic(dto, ANSWER_BODY);
        Post createdPost = createdTopic.getFirstPost();

        createTopicAssertions(branch, createdTopic, createdPost);
        createTopicVerifications(createdTopic);
        verify(subscriptionService).toggleTopicSubscription(createdTopic);
    }

    @Test
    public void testNotRunSubscriptionByCreateTopicWhenNotificationFalse() throws NotFoundException {
        Branch branch = createBranch();
        user.setAutosubscribe(false);
        when(userService.getCurrentUser()).thenReturn(user);
        createTopicStubs(branch);
        Topic dto = createTopic();
        Topic createdTopic = topicService.createTopic(dto, ANSWER_BODY);
        Post createdPost = createdTopic.getFirstPost();

        createTopicAssertions(branch, createdTopic, createdPost);
        createTopicVerifications(createdTopic);
        verify(subscriptionService, never()).toggleTopicSubscription(createdTopic);
    }

    @Test
    public void createTopicShouldNotifyMentionedUsers() throws NotFoundException {
        Branch branch = createBranch();
        user.setAutosubscribe(false);
        createTopicStubs(branch);
        String answerBodyWithUserMentioning = "[user]Shogun[/user] you are mentioned";
        Topic topicWithUserNotification = createTopic();

        Topic createdTopic = topicService.createTopic(topicWithUserNotification, answerBodyWithUserMentioning);

        verify(userService).notifyAndMarkNewlyMentionedUsers(createdTopic.getFirstPost());
    }

    @Test
    public void testRunSubscriptionByCreateReviewWhenNotificationTrue() throws NotFoundException {
        Branch branch = createBranch();
        user.setAutosubscribe(true);
        when(userService.getCurrentUser()).thenReturn(user);

        createTopicStubs(branch);
        Topic dto = createTopic();
        dto.setAnnouncement(true);
        dto.setSticked(true);
        Topic createdTopic = topicService.createCodeReview(dto, ANSWER_BODY);
        Post createdPost = createdTopic.getFirstPost();

        createCodeReviewAssertions(branch, createdTopic, createdPost);
        createCodeReviewVerifications(branch);
        verify(subscriptionService).toggleTopicSubscription(createdTopic);
    }

    @Test
    public void testNotRunSubscriptionByCreateReviewWhenNotificationFalse() throws NotFoundException {
        Branch branch = createBranch();
        user.setAutosubscribe(false);
        when(userService.getCurrentUser()).thenReturn(user);

        createTopicStubs(branch);
        Topic dto = createTopic();
        dto.setAnnouncement(true);
        dto.setSticked(true);
        Topic createdTopic = topicService.createCodeReview(dto, ANSWER_BODY);
        Post createdPost = createdTopic.getFirstPost();

        createCodeReviewAssertions(branch, createdTopic, createdPost);
        createCodeReviewVerifications(branch);
        verify(subscriptionService, never()).toggleTopicSubscription(createdTopic);
    }

    @Test
    public void testCreateCodeReviewWithWrappedBbCode() throws NotFoundException {
        JCUser user = new JCUser("", "", "");
        user.setAutosubscribe(false);
        when(userService.getCurrentUser()).thenReturn(user);
        Branch branch = createBranch();
        createTopicStubs(branch);
        Topic dto = createTopic();
        String codeReviewPattern = "[code=java]%s[/code]";
        Topic createdTopic = topicService.createCodeReview(dto, String.format(codeReviewPattern, ANSWER_BODY));
        Post createdPost = createdTopic.getFirstPost();

        createCodeReviewAssertions(branch, createdTopic, createdPost);
        createCodeReviewVerifications(branch);
    }

    @Test
    private void updateLastPostInBranchByCreateTopic() throws NotFoundException {
        Branch branch = createBranch();
        createTopicStubs(branch);
        Topic tmp = createTopic();
        tmp.setBranch(branch);
        Topic topic = topicService.createTopic(tmp, "content");
        assertEquals(branch.getLastPost(), topic.getFirstPost());
    }

    @Test
    private void updateLastPostInBranchByCreateReview() throws NotFoundException {
        Branch branch = createBranch();
        createTopicStubs(branch);
        Topic tmp = createTopic();
        tmp.setBranch(branch);
        Topic review = topicService.createCodeReview(tmp, "content");
        assertEquals(branch.getLastPost(), review.getFirstPost());
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

    private void createCodeReviewAssertions(Branch branch, Topic createdTopic, Post createdPost) {
        assertEquals(createdTopic.getTitle(), TOPIC_TITLE);
        assertEquals(createdTopic.getTopicStarter(), user);
        assertEquals(createdTopic.getBranch(), branch);
        assertEquals(createdPost.getUserCreated(), user);
        assertEquals(createdPost.getPostContent(), "[code=java]" + ANSWER_BODY + "[/code]");
        assertEquals(user.getPostCount(), 1);
        assertFalse(createdTopic.isAnnouncement());
        assertFalse(createdTopic.isSticked());
        assertNotNull(createdTopic.getCodeReview());
        assertSame(createdTopic.getCodeReview().getTopic(), createdTopic);
        assertEquals(createdTopic.getCodeReview().getComments().size(), 0);
    }

    private void createCodeReviewVerifications(Branch branch)
            throws NotFoundException {
        verify(aclBuilder, times(2)).grant(GeneralPermission.WRITE);
        verify(notificationService).subscribedEntityChanged(branch);
    }

    private void createTopicVerifications(Topic topic)
            throws NotFoundException {
        verify(aclBuilder, times(2)).grant(GeneralPermission.WRITE);
        verify(notificationService).sendNotificationAboutTopicCreated(topic);
        verify(lastReadPostService).markTopicAsRead(topic);
    }

    @Test
    public void testDeleteTopic() throws NotFoundException {
        Collection<JCUser> subscribers = new ArrayList<>();
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
        verify(branchDao).saveOrUpdate(branch);
        verify(securityService).deleteFromAcl(Topic.class, TOPIC_ID);
        verify(notificationService).subscribedEntityChanged(branch, new ArrayList());
        verify(notificationService).sendNotificationAboutRemovingTopic(topic, subscribers);
        verify(subscriptionService).getAllowedSubscribers(topic);
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
        when(topicDao.get(TOPIC_ID)).thenReturn(topic);

        topicService.deleteTopicSilent(TOPIC_ID);

        assertEquals(branch.getTopicCount(), 0);
        assertEquals(user.getPostCount(), 0);
        verify(branchDao).saveOrUpdate(branch);
        verify(securityService).deleteFromAcl(Topic.class, TOPIC_ID);
    }

    @Test
    public void testDeleteTopicWithLastPostInBranch() throws NotFoundException {
        Topic topic = new Topic(user, "title");
        topic.setId(TOPIC_ID);
        Post firstPost = new Post(user, ANSWER_BODY);
        topic.addPost(firstPost);
        final Branch branch = createBranch();
        branch.addTopic(topic);

        Post lastPostInBranch = new Post(user, ANSWER_BODY);
        branch.setLastPost(lastPostInBranch);
        topic.addPost(lastPostInBranch);
        final Post newLastPostInBranch = new Post(user, ANSWER_BODY);

        when(topicDao.get(TOPIC_ID)).thenReturn(topic);
        doAnswer(new Answer<Void>() {
            public Void answer(InvocationOnMock invocation) {
                branch.setLastPost(newLastPostInBranch);
                return null;
            }
        }).when(branchLastPostService).refreshLastPostInBranch(branch);

        topicService.deleteTopicSilent(TOPIC_ID);

        assertEquals(newLastPostInBranch, branch.getLastPost());
        verify(branchLastPostService).refreshLastPostInBranch(branch);
    }

    @Test
    public void testDeleteTopicWithoutLastPostInBranch() throws NotFoundException {
        Topic topic = new Topic(user, "title");
        topic.setId(TOPIC_ID);
        Post firstPost = new Post(user, ANSWER_BODY);
        topic.addPost(firstPost);
        final Branch branch = createBranch();
        branch.addTopic(topic);

        Post lastPostInBranch = new Post(user, ANSWER_BODY);
        branch.setLastPost(lastPostInBranch);

        when(topicDao.get(TOPIC_ID)).thenReturn(topic);

        topicService.deleteTopicSilent(TOPIC_ID);

        assertEquals(lastPostInBranch, branch.getLastPost());
        verify(branchLastPostService, Mockito.never()).refreshLastPostInBranch(branch);
    }


    @Test(expectedExceptions = {NotFoundException.class})
    public void testDeleteTopicSilentNonExistent() throws NotFoundException {
        when(topicDao.get(TOPIC_ID)).thenReturn(null);

        topicService.deleteTopicSilent(TOPIC_ID);
    }

    @Test
    void testUpdateTopicWithSubscribe() throws NotFoundException {
        user.setAutosubscribe(true);
        when(userService.getCurrentUser()).thenReturn(user);

        Topic topic = createTopic();
        topic.addPost(createPost());
        when(userService.getCurrentUser()).thenReturn(user);

        topicService.updateTopic(topic, null);

        verify(subscriptionService).toggleTopicSubscription(topic);
    }

    @Test
    void testUpdateTopicWithRepeatedSubscribe() throws NotFoundException {
        user.setAutosubscribe(true);
        when(userService.getCurrentUser()).thenReturn(user);

        Topic topic = createTopic();
        topic.addPost(createPost());
        subscribeUserOnTopic(user, topic);
        when(userService.getCurrentUser()).thenReturn(user);

        topicService.updateTopic(topic, null);

        verify(notificationService, times(0)).subscribedEntityChanged(topic);
    }

    @Test
    void testUpdateTopicWithUnsubscribe() throws NotFoundException {
        user.setAutosubscribe(false);
        when(userService.getCurrentUser()).thenReturn(user);

        Topic topic = createTopic();
        Post post = createPost();
        topic.addPost(post);
        subscribeUserOnTopic(user, topic);
        when(userService.getCurrentUser()).thenReturn(user);

        topicService.updateTopic(topic, null);

        verify(subscriptionService).toggleTopicSubscription(topic);
    }

    @Test
    void testUpdateTopicWithRepeatedUnsubscribe() throws NotFoundException {
        user.setAutosubscribe(true);
        when(userService.getCurrentUser()).thenReturn(user);

        Topic topic = createTopic();
        Post post = createPost();
        topic.addPost(post);
        when(userService.getCurrentUser()).thenReturn(user);

        topicService.updateTopic(topic, null);

        verify(notificationService, times(0)).subscribedEntityChanged(topic);
    }

    @Test
    void testUpdateTopicWithEmptyPollNotCreatePoll() throws NotFoundException {
        when(userService.getCurrentUser()).thenReturn(user);

        Topic topic = createTopic();
        Post post = createPost();
        topic.addPost(post);
        Poll poll = new Poll();
        topic.setPoll(poll);
        when(userService.getCurrentUser()).thenReturn(user);

        topicService.updateTopic(topic, poll);

        verify(pollService, times(0)).createPoll(poll);
    }

    @Test
    void testUpdateTopicWithEmptyPollNotMergePollItems() throws NotFoundException {
        when(userService.getCurrentUser()).thenReturn(user);

        Topic topic = createTopic();
        Post post = createPost();
        topic.addPost(post);
        Poll poll = new Poll();
        topic.setPoll(poll);
        when(userService.getCurrentUser()).thenReturn(user);

        topicService.updateTopic(topic, poll);

        verify(pollService, times(0)).mergePollItems(poll, poll.getPollItems());
    }

    @Test
    void testUpdateTopicWithPresentedPoll() throws NotFoundException {
        when(userService.getCurrentUser()).thenReturn(user);

        DateTime endingDateTime = new DateTime();
        Topic topic = createTopic();
        Post post = createPost();
        topic.addPost(post);
        Poll poll = createPoll();
        poll.setEndingDate(endingDateTime);
        topic.setPoll(poll);

        when(userService.getCurrentUser()).thenReturn(user);

        topicService.updateTopic(topic, poll);

        verify(pollService, never()).mergePollItems(poll, poll.getPollItems());
        assertEquals(endingDateTime, topic.getPoll().getEndingDate());
    }

    @Test
    void testUpdateTopicPollWithNewEndingDate() throws NotFoundException {
        when(userService.getCurrentUser()).thenReturn(user);

        DateTime endingDateTime = new DateTime();
        Topic topic = createTopic();
        Post post = createPost();
        topic.addPost(post);
        Poll poll = createPoll();
        poll.setEndingDate(endingDateTime);
        topic.setPoll(poll);
        DateTime newEndingDateTime = new DateTime().plusDays(10);
        Poll pollWithNewEndingDate = createPoll();
        pollWithNewEndingDate.setEndingDate(newEndingDateTime);

        when(userService.getCurrentUser()).thenReturn(user);

        topicService.updateTopic(topic, pollWithNewEndingDate);

        verify(pollService, never()).mergePollItems(poll, poll.getPollItems());
        assertEquals(newEndingDateTime, topic.getPoll().getEndingDate());
    }

    @Test
    void testUpdateTopicWithNotPresentedPoll() throws NotFoundException {
        when(userService.getCurrentUser()).thenReturn(user);

        Topic topic = createTopic();
        Post post = createPost();
        topic.addPost(post);
        Poll poll = createPoll();
        when(userService.getCurrentUser()).thenReturn(user);

        topicService.updateTopic(topic, poll);

        verify(pollService, never()).createPoll(poll);
    }

    @Test
    void testUpdateTopicSimple() throws NotFoundException {
        user.setAutosubscribe(true);
        when(userService.getCurrentUser()).thenReturn(user);

        Topic topic = new Topic(user, "title");
        topic.setId(TOPIC_ID);
        topic.setTitle("title");
        Post post = new Post(user, "content");
        topic.addPost(post);

        topicService.updateTopic(topic, null);

        verify(topicDao).saveOrUpdate(topic);
    }

    @Test(expectedExceptions = AccessDeniedException.class)
    void shouldBeImpossibleToUpdateCodeReview() throws NotFoundException {
        user.setAutosubscribe(false);
        when(userService.getCurrentUser()).thenReturn(user);
        Topic topic = new Topic(user, "title");
        topic.setCodeReview(new CodeReview());

        topicService.updateTopic(topic, null);
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

        when(topicDao.get(TOPIC_ID)).thenReturn(topic);
        when(branchDao.get(BRANCH_ID)).thenReturn(targetBranch);

        topicService.moveTopic(topic, BRANCH_ID);

        assertEquals(targetBranch.getTopicCount(), 1);
        verify(branchDao).saveOrUpdate(targetBranch);
        verify(notificationService).sendNotificationAboutTopicMoved(topic);
    }

    @Test
    public void testMoveTopicWithLastPostInBranch() throws NotFoundException {
        Topic topic = new Topic(user, "title");
        topic.setId(TOPIC_ID);
        Post firstPost = new Post(user, ANSWER_BODY);
        topic.addPost(firstPost);
        Branch currentBranch = createBranch();
        currentBranch.addTopic(topic);
        currentBranch.setLastPost(firstPost);
        Branch targetBranch = new Branch("target branch", "target branch description");

        when(topicDao.get(TOPIC_ID)).thenReturn(topic);
        when(branchDao.get(BRANCH_ID)).thenReturn(targetBranch);

        topicService.moveTopic(topic, BRANCH_ID);

        verify(branchLastPostService).refreshLastPostInBranch(currentBranch);
        verify(branchLastPostService).refreshLastPostInBranch(targetBranch);
    }

    @Test
    public void testMoveTopicWithoutLastPostInBranch() throws NotFoundException {
        Topic topic = new Topic(user, "title");
        topic.setId(TOPIC_ID);
        Post firstPost = new Post(user, ANSWER_BODY);
        topic.addPost(firstPost);
        Branch currentBranch = createBranch();
        currentBranch.addTopic(topic);
        currentBranch.setLastPost(new Post(user, ANSWER_BODY));
        Branch targetBranch = new Branch("target branch", "target branch description");

        when(topicDao.get(TOPIC_ID)).thenReturn(topic);
        when(branchDao.get(BRANCH_ID)).thenReturn(targetBranch);

        topicService.moveTopic(topic, BRANCH_ID);

        verify(branchLastPostService, Mockito.never()).refreshLastPostInBranch(currentBranch);
        verify(branchLastPostService).refreshLastPostInBranch(targetBranch);
    }

    @Test
    public void testCloseTopic() {
        Topic topic = this.createTopic();
        topicService.closeTopic(topic);

        assertTrue(topic.isClosed());
        verify(topicDao).saveOrUpdate(topic);
    }

    @Test(expectedExceptions = AccessDeniedException.class)
    public void testCloseCodeReviewTopic() {
        Topic topic = this.createTopic();
        topic.setCodeReview(new CodeReview());
        topicService.closeTopic(topic);
    }

    @Test
    public void testOpenTopic() {
        Topic topic = this.createTopic();
        topic.setClosed(true);
        topicService.openTopic(topic);

        assertFalse(topic.isClosed());
        verify(topicDao).saveOrUpdate(topic);
    }

    private Branch createBranch() {
        Branch branch = new Branch("branch name", "branch description");
        branch.setId(BRANCH_ID);
        branch.setUuid("uuid");
        return branch;
    }

    private Poll createPoll() {
        Poll poll = new Poll();
        poll.setTitle("title");

        PollItem item = new PollItem("itemName");
        poll.setPollItems(Arrays.asList(item));
        return poll;
    }

    private Topic createTopic() {
        Topic topic = new Topic(user, TOPIC_TITLE);
        topic.setId(TOPIC_ID);
        Branch branch = createBranch();
        topic.setBranch(branch);
        return topic;
    }

    private Post createPost() {
        Post post = new Post(user, "content");
        post.setId(333L);
        return post;
    }

    private void subscribeUserOnTopic(JCUser user, Topic topic) {
        Set<JCUser> subscribers = new HashSet<>();
        subscribers.add(user);
        topic.setSubscribers(subscribers);
    }
}
