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

import org.jtalks.common.model.dao.Crud;
import org.jtalks.common.model.dao.hibernate.GenericDao;
import org.jtalks.common.model.permissions.BranchPermission;
import org.jtalks.jcommune.model.dao.PostDao;
import org.jtalks.jcommune.model.dao.TopicDao;
import org.jtalks.jcommune.model.dto.PageRequest;
import org.jtalks.jcommune.model.entity.*;
import org.jtalks.jcommune.plugin.api.PluginLoader;
import org.jtalks.jcommune.plugin.api.core.Plugin;
import org.jtalks.jcommune.plugin.api.core.TopicPlugin;
import org.jtalks.jcommune.plugin.api.filters.StateFilter;
import org.jtalks.jcommune.plugin.api.filters.TypeFilter;
import org.jtalks.jcommune.service.BranchLastPostService;
import org.jtalks.jcommune.service.PostService;
import org.jtalks.jcommune.service.UserService;
import org.jtalks.jcommune.plugin.api.exceptions.NotFoundException;
import org.jtalks.jcommune.service.nontransactional.MentionedUsers;
import org.jtalks.jcommune.service.nontransactional.NotificationService;
import org.jtalks.jcommune.service.security.AclClassName;
import org.jtalks.jcommune.service.security.PermissionService;
import org.jtalks.jcommune.service.security.SecurityService;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.access.AccessDeniedException;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * This test cover {@code TransactionalPostService} logic validation.
 * Logic validation cover update/get/error cases by this class.
 *
 * @author Osadchuck Eugeny
 * @author Evgeniy Naumenko
 * @author Kirill Afonin
 */
public class TransactionalPostServiceTest {

    private final long POST_ID = 9L;
    private static final String USERNAME = "username";
    private static final String EMAIL = "username@mail.com";
    private static final String PASSWORD = "password";

    @Mock
    private NotificationService notificationService;
    @Mock
    private PostDao postDao;
    @Mock
    private SecurityService securityService;
    @Mock
    private TopicDao topicDao;
    @Mock
    private UserService userService;
    @Mock
    private BranchLastPostService branchLastPostService;
    @Mock
    private MentionedUsers mentionedUsers;
    @Mock
    private PermissionService permissionService;
    @Mock
    private Crud<PostComment> postCommentDao;
    @Mock
    private PluginLoader pluginLoader;
    @Mock
    private TopicPlugin topicPlugin;
    @Mock
    private GenericDao<PostDraft> postDraftDao;

    private PostService postService;

    private JCUser user;

    private JCUser currentUser;

    @BeforeMethod
    public void setUp() throws Exception {
        initMocks(this);
        user = new JCUser(USERNAME, EMAIL, PASSWORD);

        currentUser = new JCUser("current", null, null);
        when(userService.getCurrentUser()).thenReturn(currentUser);

        postService = new TransactionalPostService(
                postDao,
                topicDao,
                securityService,
                notificationService,
                userService,
                branchLastPostService,
                permissionService,
                pluginLoader,
                postDraftDao);
    }

    @Test
    public void testGet() throws NotFoundException {
        Post post = new Post(user, "content");
        when(postDao.isExist(POST_ID)).thenReturn(true);
        when(postDao.get(POST_ID)).thenReturn(post);

        Post actualPost = postService.get(POST_ID);

        Assert.assertEquals(actualPost, post, "Posts aren't equals");
        verify(postDao).isExist(POST_ID);
        verify(postDao).get(POST_ID);
    }

    @Test(expectedExceptions = {NotFoundException.class})
    public void testGetIncorrectId() throws NotFoundException {
        when(postDao.isExist(POST_ID)).thenReturn(false);

        postService.get(POST_ID);
    }

    @Test
    public void testUpdatePost() throws NotFoundException {
        String newBody = "new body";
        Topic topic = new Topic(user, "title");
        topic.setType(TopicTypeName.DISCUSSION.getName());
        Post post = new Post(user, "");
        topic.addPost(post);
        post.setId(POST_ID);
        topic.addPost(post);
        when(postDao.isExist(POST_ID)).thenReturn(true);
        when(postService.get(POST_ID)).thenReturn(post);

        postService.updatePost(post, newBody);

        assertEquals(post.getPostContent(), newBody);

        verify(postDao).saveOrUpdate(post);

        verify(userService).notifyAndMarkNewlyMentionedUsers(post);
    }

    @Test
    public void testUpdatePost_shouldNotSendNotifications() throws NotFoundException {
        String newBody = "new body";
        Topic topic = new Topic(user, "title");
        topic.setType(TopicTypeName.DISCUSSION.getName());
        Post post = new Post(user, "");
        topic.addPost(post);
        post.setId(POST_ID);
        topic.addPost(post);
        when(postDao.isExist(POST_ID)).thenReturn(true);
        when(postService.get(POST_ID)).thenReturn(post);

        postService.updatePost(post, newBody);

        verify(notificationService, times(0)).subscribedEntityChanged(topic);
    }

    @Test(expectedExceptions = AccessDeniedException.class)
    void shouldBeImpossibleToEditCodeReviewBody() throws NotFoundException {
        Post post = firstPostOfCodeReview();
        when(postDao.isExist(post.getId())).thenReturn(true);
        when(postService.get(post.getId())).thenReturn(post);

        postService.updatePost(post, null);
    }

    @Test
    public void testDeletePostDeletedPostIsLastModified() throws NotFoundException {
        Topic topic = new Topic(user, "title");
        Post post = new Post(user, "content");
        post.setId(1L);
        Post postForDelete = new Post(user, "content");
        postForDelete.setId(POST_ID);
        topic.addPost(post);
        topic.addPost(postForDelete);
        topic.recalculateModificationDate();
        Branch branch = new Branch("branch", "branch description");
        topic.setBranch(branch);
        user.setPostCount(2);

        postService.deletePost(postForDelete);

        assertEquals(user.getPostCount(), 1);
        assertEquals(topic.getModificationDate(), topic.getFirstPost().getCreationDate());
        verify(topicDao).saveOrUpdate(topic);
        verify(securityService).deleteFromAcl(postForDelete);
        verify(notificationService).subscribedEntityChanged(postForDelete);
    }

    @Test
    public void testDeletePostFirstPostIsLastModified() throws NotFoundException {
        Topic topic = new Topic(user, "title");
        Post post = new Post(user, "content");
        post.setId(1L);

        try {
            Thread.sleep(100);    //delay added to prevent same modification time on fast PC
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Post postForDelete = new Post(user, "content");
        postForDelete.setId(POST_ID);

        topic.addPost(post);
        topic.addPost(postForDelete);

        try {
            Thread.sleep(100);    //delay added to prevent same modification time on fast PC
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        topic.recalculateModificationDate();

        Branch branch = new Branch("branch", "branch description");
        topic.setBranch(branch);
        user.setPostCount(2);

        postService.deletePost(postForDelete);

        assertEquals(user.getPostCount(), 1);
        assertEquals(topic.getModificationDate(), topic.getFirstPost().getCreationDate());
        verify(topicDao).saveOrUpdate(topic);
        verify(securityService).deleteFromAcl(postForDelete);
        verify(notificationService).subscribedEntityChanged(postForDelete);

    }

    @Test
    public void testDeletePostThatIsLastInBranch() throws NotFoundException {
        Topic topic = new Topic(user, "title");
        Post post = new Post(user, "content");
        post.setId(1L);
        Post postForDelete = new Post(user, "content");
        postForDelete.setId(POST_ID);
        topic.addPost(post);
        topic.addPost(postForDelete);
        topic.recalculateModificationDate();
        Branch branch = new Branch("branch", "branch description");
        topic.setBranch(branch);
        branch.setLastPost(postForDelete);

        postService.deletePost(postForDelete);

        assertEquals(topic.getModificationDate(), post.getCreationDate());
        verify(branchLastPostService).refreshLastPostInBranch(branch);
    }

    @Test
    public void testDeletePostThatIsNotLastInBranch() throws NotFoundException {
        Topic topic = new Topic(user, "title");
        Post post = new Post(user, "content");
        Post postLast = new Post(user, "content");
        post.setId(1L);
        postLast.setId(3L);
        Post postForDelete = new Post(user, "content");
        postForDelete.setId(2L);
        topic.addPost(post);
        topic.addPost(postForDelete);
        topic.addPost(postLast);
        topic.recalculateModificationDate();
        Branch branch = new Branch("branch", "branch description");
        topic.setBranch(branch);
        branch.setLastPost(postLast);

        postService.deletePost(postForDelete);

        assertEquals(topic.getModificationDate(), postLast.getCreationDate());
        verify(branchLastPostService, Mockito.never()).refreshLastPostInBranch(branch);
    }

    @Test
    public void whenOwnerRemovesThePost_thenThereIsNoNotificationToTheSubscribers() {
        Post post = getPostWithTopicInBranch();
        Topic topic = post.getTopic();
        topic.setSubscribers(Collections.singleton(user));  //add the creator of the post to subscribers

        postService.deletePost(post);

        verify(notificationService).subscribedEntityChanged(post);
    }

    @Test
    public void whenNotPostCreatorRemovesThePost_thenOnlyPostCreatorGetsNotification() {
        Post post = getPostWithTopicInBranch();
        Topic topic = post.getTopic();
        Set<JCUser> subscribers = new HashSet<>();
        subscribers.add(user);  //post creator
        subscribers.add(currentUser);   //user, that removes post
        topic.setSubscribers(subscribers);

        postService.deletePost(post);

        verify(notificationService).subscribedEntityChanged(post);
    }

    @Test
    public void testPostsOfUser() {
        Page<Post> expectedPostsPage = getPageWithPost();
        when(postDao.getUserPosts(Matchers.<JCUser>any(), Matchers.<PageRequest>any(), Matchers.anyList()))
                .thenReturn(expectedPostsPage);
        when(topicDao.getAllowedBranchesIds(Matchers.<JCUser>any())).thenReturn(Arrays.asList(1L));

        currentUser.setPageSize(50);

        Page<Post> actualPostsPage = postService.getPostsOfUser(user, "1");

        assertEquals(actualPostsPage, expectedPostsPage);
    }

    @Test
    public void getPostsOfUserShouldReturnEmptyPageInNoBranchesAllowed() {
        when(postDao.getUserPosts(Matchers.<JCUser>any(), Matchers.<PageRequest>any(), Matchers.anyList()))
                .thenReturn(getPageWithPost());
        when(topicDao.getAllowedBranchesIds(Matchers.<JCUser>any())).thenReturn(Collections.EMPTY_LIST);

        currentUser.setPageSize(50);

        Page<Post> actualPostsPage = postService.getPostsOfUser(user, "1");

        assertEquals(actualPostsPage.getSize(), 0);
    }

    @Test
    public void testLastPostInTopicPageCalculation() {
        int pageSize = 2;
        currentUser.setPageSize(pageSize);

        Topic topic = new Topic(null, "");
        Post post = new Post(null, "");
        topic.addPost(new Post(null, null));
        topic.addPost(new Post(null, null));
        topic.addPost(post);

        assertEquals(postService.calculatePageForPost(post), 2);
    }

    @Test
    public void testFirstPostInTopicPageCalculation() {
        int pageSize = 2;
        currentUser.setPageSize(pageSize);
        Topic topic = new Topic(null, "");
        Post post = new Post(null, "");
        topic.addPost(post);

        assertEquals(postService.calculatePageForPost(post), 1);
    }

    @Test
    public void testFirstPostInTopicPageCalculationWithNoUser() {
        currentUser.setPageSize(JCUser.DEFAULT_PAGE_SIZE);

        Topic topic = new Topic(user, "");
        Post post = new Post(user, "");
        topic.addPost(post);

        assertEquals(postService.calculatePageForPost(post), 1);
    }

    @Test
    public void testLastPostOnFirstPagePageCalculation() {
        int pageSize = 2;
        currentUser.setPageSize(pageSize);

        Topic topic = new Topic(user, "");
        Post post = new Post(user, "");
        topic.addPost(new Post(null, null));
        topic.addPost(post);

        assertEquals(postService.calculatePageForPost(post), 1);
    }

    @Test
    public void testLastPostOnPagePageCalculation() {
        int pageSize = 2;
        currentUser.setPageSize(pageSize);

        Topic topic = new Topic(null, "");
        Post post = new Post(null, "");
        topic.addPost(new Post(null, null));
        topic.addPost(new Post(null, null));
        topic.addPost(new Post(null, null));
        topic.addPost(post);

        assertEquals(postService.calculatePageForPost(post), 2);
    }

    @Test
    public void testPostInCenterOfTopicPageCalculation() {
        int pageSize = 2;
        currentUser.setPageSize(pageSize);

        Topic topic = new Topic(null, "");
        Post post = new Post(null, "");
        topic.addPost(new Post(null, null));
        topic.addPost(post);
        topic.addPost(new Post(null, null));

        assertEquals(postService.calculatePageForPost(post), 1);
    }

    @Test
    public void testGetPosts() {
        String pageNumber = "50";
        Topic topic = new Topic(user, "");
        Page<Post> expectedPage = new PageImpl<>(Collections.<Post>emptyList());

        currentUser.setPageSize(50);

        when(postDao.getPosts(
                Matchers.any(Topic.class), Matchers.any(PageRequest.class)))
                .thenReturn(expectedPage);

        Page<Post> actualPage = postService.getPosts(topic, pageNumber);

        assertEquals(actualPage, expectedPage, "Service returned incorrect data for one page of posts");
        verify(postDao).getPosts(Matchers.any(Topic.class), Matchers.any(PageRequest.class));
    }

    @Test
    public void testGetLastPostForBranch() {
        Branch postBranch = new Branch(null, null);
        Post expectedPost = new Post(null, null);
        when(postDao.getLastPostFor(Mockito.<Branch>any()))
                .thenReturn(expectedPost);

        Post actualPost = postService.getLastPostFor(postBranch);

        assertEquals(actualPost, expectedPost, "Service returned incorrect last post for branch");
        verify(postDao).getLastPostFor(postBranch);
    }

    @Test
    public void testGetLastPostsForBranch() {
        Branch postBranch = new Branch(null, null);
        Post expectedPost = new Post(null, null);
        List<Post> posts = Arrays.asList(expectedPost);
        List<Long> branches = Arrays.asList(postBranch.getId());
        when(postDao.getLastPostsFor(Mockito.<List<Long>>any(), Mockito.eq(42)))
                .thenReturn(posts);

        List<Post> actualPosts = postService.getLastPostsFor(postBranch, 42);

        assertEquals(actualPosts, posts, "Service returned incorrect last posts for branch");
        verify(postDao).getLastPostsFor(branches, 42);
    }

    @Test
    public void testAddComment() throws Exception {
        Post post = getPostWithTopicInBranch();
        post.getTopic().setType(TopicTypeName.CODE_REVIEW.getName());
        when(postDao.isExist(POST_ID)).thenReturn(true);
        when(postDao.get(POST_ID)).thenReturn(post);

        PostComment comment = postService.addComment(POST_ID, Collections.EMPTY_MAP, "text");

        assertEquals(post.getComments().size(), 1);
        assertEquals(comment.getBody(), "text");
        assertEquals(comment.getAuthor(), currentUser);
        verify(postDao).saveOrUpdate(post);
    }

    @Test
    public void testAddCommentWithProperties() throws Exception {
        Post post = getPostWithTopicInBranch();
        post.getTopic().setType(TopicTypeName.CODE_REVIEW.getName());
        when(postDao.isExist(POST_ID)).thenReturn(true);
        when(postDao.get(POST_ID)).thenReturn(post);
        Map<String, String> attributes = new HashMap<>();
        attributes.put("name", "value");

        PostComment comment = postService.addComment(POST_ID, attributes, "text");

        assertEquals(comment.getAttributes().size(), 1);
        assertEquals(comment.getAttributes().get("name"), "value");
        verify(postDao).saveOrUpdate(post);

    }

    @Test(expectedExceptions = NotFoundException.class)
    public void addCommentShouldThrowExceptionIfPostNotFound() throws Exception {
        postService.addComment(0L, Collections.EMPTY_MAP, "text");
    }

    @Test(expectedExceptions = AccessDeniedException.class)
    public void addingCommentToDiscussionShouldThrowException() throws Exception {
        Post post = getPostWithTopicInBranch();
        post.getTopic().setType(TopicTypeName.DISCUSSION.getName());
        when(postDao.isExist(POST_ID)).thenReturn(true);
        when(postDao.get(POST_ID)).thenReturn(post);

        postService.addComment(POST_ID, Collections.EMPTY_MAP, "text");
    }

    @Test(expectedExceptions = AccessDeniedException.class)
    public void addingCommentsToCodeReviewShouldThrowExceptionIfUserHasNoPermissions() throws Exception {
        Post post = getPostWithTopicInBranch();
        post.getTopic().setType(TopicTypeName.CODE_REVIEW.getName());
        when(postDao.isExist(POST_ID)).thenReturn(true);
        when(postDao.get(POST_ID)).thenReturn(post);

        doThrow(new AccessDeniedException(""))
                .when(permissionService).checkPermission(post.getTopic().getBranch().getId(), AclClassName.BRANCH,
                BranchPermission.LEAVE_COMMENTS_IN_CODE_REVIEW);

        postService.addComment(POST_ID, Collections.EMPTY_MAP, "text");
    }

    @Test
    public void addingCommentToPlugablePostShouldBeSucceedIfAppropriatePluginIsEnabledAndUserHasPermissions()
        throws Exception {
        String topicType = "Plugable";
        Post post = getPostWithTopicInBranch();
        post.getTopic().setType(topicType);

        when(pluginLoader.getPlugins(any(TypeFilter.class), any(StateFilter.class)))
                .thenReturn(Arrays.<Plugin>asList(topicPlugin));
        when(topicPlugin.getTopicType()).thenReturn(topicType);
        when(postDao.isExist(POST_ID)).thenReturn(true);
        when(postDao.get(POST_ID)).thenReturn(post);

        PostComment comment = postService.addComment(POST_ID, Collections.EMPTY_MAP, "text");

        assertEquals(post.getComments().size(), 1);
        assertEquals(comment.getBody(), "text");
        assertEquals(comment.getAuthor(), currentUser);
        verify(postDao).saveOrUpdate(post);

    }

    @Test(expectedExceptions = AccessDeniedException.class)
    public void addingCommentToPostFromPlugableTopicShouldThrowExceptionIfUserHaveNoPermissions() throws Exception {
        String topicType = "Plugable";
        Post post = getPostWithTopicInBranch();
        post.getTopic().setType(topicType);

        when(pluginLoader.getPlugins(any(TypeFilter.class), any(StateFilter.class)))
                .thenReturn(Arrays.<Plugin>asList(topicPlugin));
        when(topicPlugin.getTopicType()).thenReturn(topicType);
        when(topicPlugin.getCommentPermission()).thenReturn(BranchPermission.CREATE_POSTS);
        when(postDao.isExist(POST_ID)).thenReturn(true);
        when(postDao.get(POST_ID)).thenReturn(post);
        doThrow(new AccessDeniedException(""))
                .when(permissionService).checkPermission(post.getTopic().getBranch().getId(), AclClassName.BRANCH,
                BranchPermission.CREATE_POSTS);

        postService.addComment(POST_ID, Collections.EMPTY_MAP, "text");
    }

    @Test(expectedExceptions = AccessDeniedException.class)
    public void addingCommentsToPostFromPluggableTopicShouldThrowExceptionIfAppropriatePluginNotFound() throws Exception {
        String topicType = "Plugable";
        Post post = getPostWithTopicInBranch();
        post.getTopic().setType(topicType);

        when(pluginLoader.getPlugins(any(TypeFilter.class), any(StateFilter.class))).thenReturn(Collections.EMPTY_LIST);
        when(postDao.isExist(POST_ID)).thenReturn(true);
        when(postDao.get(POST_ID)).thenReturn(post);

        postService.addComment(POST_ID, Collections.EMPTY_MAP, "text");
    }

    @Test(expectedExceptions = AccessDeniedException.class)
    public void shouldBeImpossibleToAddCommentsToClosedTopicWithoutCloseTopicPermission() throws Exception {
        String topicType = "Plugable";
        Post post = getPostWithTopicInBranch();
        post.getTopic().setType(topicType);
        post.getTopic().setClosed(true);

        when(pluginLoader.getPlugins(any(TypeFilter.class), any(StateFilter.class)))
                .thenReturn(Arrays.<Plugin>asList(topicPlugin));
        when(topicPlugin.getTopicType()).thenReturn(topicType);
        when(topicPlugin.getCommentPermission()).thenReturn(BranchPermission.CREATE_POSTS);
        when(postDao.isExist(POST_ID)).thenReturn(true);
        when(postDao.get(POST_ID)).thenReturn(post);
        doThrow(new AccessDeniedException(""))
                .when(permissionService).checkPermission(post.getTopic().getBranch().getId(), AclClassName.BRANCH,
                BranchPermission.CLOSE_TOPICS);

        postService.addComment(POST_ID, Collections.EMPTY_MAP, "text");
    }

    @Test
    public void testAddUserToSubscriptedByComment() throws Exception {
        JCUser user = new JCUser("username", null, null);
        Post post = getPostWithTopicInBranch();
        post.getTopic().setType(TopicTypeName.CODE_REVIEW.getName());
        user.setAutosubscribe(true);

        when(userService.getCurrentUser()).thenReturn(user);
        when(postDao.isExist(POST_ID)).thenReturn(true);
        when(postDao.get(POST_ID)).thenReturn(post);

        postService.addComment(POST_ID, Collections.EMPTY_MAP, "text");

        assertTrue(post.getTopic().getSubscribers().contains(user));

    }

    @Test
    public void testNotAddUserToSubscriptedByComment() throws Exception {
        JCUser user = new JCUser("username", null, null);
        Post post = getPostWithTopicInBranch();
        post.getTopic().setType(TopicTypeName.CODE_REVIEW.getName());
        user.setAutosubscribe(false);

        when(userService.getCurrentUser()).thenReturn(user);
        when(postDao.isExist(POST_ID)).thenReturn(true);
        when(postDao.get(POST_ID)).thenReturn(post);

        postService.addComment(POST_ID, Collections.EMPTY_MAP, "text");

        assertFalse(post.getTopic().getSubscribers().contains(user));

    }

    @Test
    public void testDeleteCommentSuccess() {
        Post post = getPostWithTopicInBranch();
        PostComment comment = new PostComment();
        post.addComment(comment);

        postService.deleteComment(post, comment);

        assertEquals(post.getComments().size(), 0);
        verify(postDao).saveOrUpdate(post);
    }

    @Test
    public void testVoteUpSuccess() {
        Post post = new Post(null, null);
        post.setId(1L);
        PostVote vote = new PostVote();
        vote.setVotedUp(true);
        JCUser user = new JCUser("username", null, null);

        when(userService.getCurrentUser()).thenReturn(user);

        Post result = postService.vote(post, vote);

        assertTrue(result.getVotes().contains(vote));
        verify(postDao).saveOrUpdate(post);
        verify(postDao).changeRating(1L, 1);
    }

    @Test
    public void testVoteUpForVotedDownPost() {
        Post post = new Post(null, null);
        post.setId(1L);
        JCUser user = new JCUser("username", null, null);
        PostVote vote1 = new PostVote(user);
        vote1.setVotedUp(false);
        post.putVote(vote1);
        PostVote vote2 = new PostVote();
        vote2.setVotedUp(true);

        when(userService.getCurrentUser()).thenReturn(user);

        postService.vote(post, vote2);

        verify(postDao).saveOrUpdate(post);
        verify(postDao).changeRating(1L, 2);
    }

    @Test
    public void testVoteDownSuccess() {
        Post post = new Post(null, null);
        post.setId(1L);
        PostVote vote = new PostVote();
        vote.setVotedUp(false);
        JCUser user = new JCUser("username", null, null);

        when(userService.getCurrentUser()).thenReturn(user);

        Post result = postService.vote(post, vote);

        assertTrue(result.getVotes().contains(vote));
        verify(postDao).saveOrUpdate(post);
        verify(postDao).changeRating(1L, -1);
    }

    @Test
    public void testVoteDownForVotedUpPost() {
        Post post = new Post(null, null);
        post.setId(1L);
        JCUser user = new JCUser("username", null, null);
        PostVote vote1 = new PostVote(user);
        vote1.setVotedUp(true);
        post.putVote(vote1);
        PostVote vote2 = new PostVote();
        vote2.setVotedUp(false);

        when(userService.getCurrentUser()).thenReturn(user);

        postService.vote(post, vote2);

        verify(postDao).saveOrUpdate(post);
        verify(postDao).changeRating(1L, -2);
    }

    @Test(expectedExceptions = AccessDeniedException.class)
    public void voteForPostInSameDirectionMoreThanOneTimeShouldThrowException() {
        Post post = new Post(null, null);
        post.setId(1L);
        JCUser user = new JCUser("username", null, null);
        PostVote vote = new PostVote(user);
        vote.setVotedUp(true);
        post.putVote(vote);

        when(userService.getCurrentUser()).thenReturn(user);

        postService.vote(post, vote);

    }

    @Test
    public void saverOrUpdateDraftShouldCreateNewDraftIfUserStillHasNoDraftsInSpecifiedTopic() {
        Topic topic = new Topic();
        JCUser currentUser = new JCUser("username", null, null);
        String content = "content";

        when(userService.getCurrentUser()).thenReturn(currentUser);

        PostDraft draft = postService.saveOrUpdateDraft(topic, content);

        verify(topicDao).saveOrUpdate(topic);
        assertEquals(draft.getContent(), content);
        assertTrue(topic.getDrafts().contains(draft));
    }

    @Test
    public void saverOrUpdateDraftShouldUpdateDraftIfUserAlreadyHasDraftInSpecifiedTopic() {
        Topic topic = new Topic();
        JCUser currentUser = new JCUser("username", null, null);
        PostDraft draft = new PostDraft("content", currentUser);
        topic.addDraft(draft);
        String newContent = "Something amazing";

        when(userService.getCurrentUser()).thenReturn(currentUser);

        PostDraft result = postService.saveOrUpdateDraft(topic, newContent);

        verify(topicDao).saveOrUpdate(topic);
        assertEquals(result, draft);
        assertEquals(result.getContent(), newContent);
        assertTrue(topic.getDrafts().contains(result));
        assertEquals(topic.getDrafts().size(), 1);
    }

    @Test
    public void saverOrUpdateDraftShouldNotModifyCounterOfUserPosts() {
        Topic topic = new Topic();
        JCUser currentUser = new JCUser("username", null, null);
        int before = currentUser.getPostCount();

        when(userService.getCurrentUser()).thenReturn(currentUser);

        postService.saveOrUpdateDraft(topic, "123");

        assertEquals(before, user.getPostCount());
    }

    @Test
    public void saveOrUpdateDraftShouldNotSendNotifications() {
        Topic topic = new Topic();
        JCUser currentUser = new JCUser("username", null, null);

        when(userService.getCurrentUser()).thenReturn(currentUser);

        postService.saveOrUpdateDraft(topic, "123");

        verify(notificationService, never()).subscribedEntityChanged(any(SubscriptionAwareEntity.class));
    }

    @Test
    public void testDeleteDraft() throws Exception{
        PostDraft draft = getDraftWithTopicInBranch();

        when(postDraftDao.isExist(draft.getId())).thenReturn(true);
        when(postDraftDao.get(draft.getId())).thenReturn(draft);

        postService.deleteDraft(draft.getId());

        verify(topicDao).saveOrUpdate(draft.getTopic());
        assertFalse(draft.getTopic().getPosts().contains(draft));
    }

    @Test(expectedExceptions = NotFoundException.class)
    public void deleteDraftShouldThrowExceptionIfDraftNotFound() throws Exception {
        when(postDraftDao.isExist(anyLong())).thenReturn(false);

        postService.deleteDraft(1l);
    }

    @Test(expectedExceptions = AccessDeniedException.class)
    public void deleteDraftShouldThrowExceptionIfAnotherUserTryToDeleteDeraft() throws Exception {
        PostDraft draft = getDraftWithTopicInBranch();
        draft.setAuthor(new JCUser("name", "mylo@mail.ru", "123"));

        when(postDraftDao.isExist(draft.getId())).thenReturn(true);
        when(postDraftDao.get(draft.getId())).thenReturn(draft);

        postService.deleteDraft(draft.getId());
    }

    @Test
    public void deleteDraftShouldNotChangeUserPostCounter() throws Exception {
        PostDraft draft = getDraftWithTopicInBranch();
        int before = draft.getAuthor().getPostCount();

        when(postDraftDao.isExist(draft.getId())).thenReturn(true);
        when(postDraftDao.get(draft.getId())).thenReturn(draft);

        postService.deleteDraft(draft.getId());

        assertEquals(draft.getAuthor().getPostCount(), before);
    }

    @Test
    public void deleteDraftShouldNotSendNotifications() throws Exception {
        PostDraft draft = getDraftWithTopicInBranch();

        when(postDraftDao.isExist(draft.getId())).thenReturn(true);
        when(postDraftDao.get(draft.getId())).thenReturn(draft);

        postService.deleteDraft(draft.getId());

        verify(notificationService, never()).subscribedEntityChanged(any(SubscriptionAwareEntity.class));
    }

    private PostDraft getDraftWithTopicInBranch() {
        Branch branch = new Branch(null, null);
        branch.setId(1);
        Topic topic = new Topic();
        PostDraft draft = new PostDraft("content", currentUser);
        draft.setId(1);
        topic.addDraft(draft);
        return draft;
    }

    private Post getPostWithTopicInBranch() {
        Branch branch = new Branch(null, null);
        branch.setId(1);
        Topic topic = new Topic();
        Post firstPost = new Post(user, null);
        firstPost.setId(1l);
        Post secondPost = new Post(user, null);
        secondPost.setId(2l);
        topic.addPost(firstPost);
        topic.addPost(secondPost);
        topic.setBranch(branch);
        return firstPost;
    }

    /**
     * Creates a code review with the first post.
     *
     * @return a post of the created code review
     */
    private Post firstPostOfCodeReview() {
        Topic topic = new Topic(user, "title");
        topic.setType(TopicTypeName.CODE_REVIEW.getName());
        Post post = new Post(user, "");
        topic.addPost(post);
        post.setId(123L);//we don't care about ID
        topic.addPost(post);
        return post;
    }

    private Page<Post> getPageWithPost() {
        Branch branch = new Branch("branch","");
        branch.setId(1L);
        Topic topic = new Topic();
        topic.setBranch(branch);
        Post post = new Post(user, "");
        post.setTopic(topic);
        return new PageImpl<>(Arrays.asList(post));
    }
}
