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

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.testng.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.jtalks.common.security.SecurityService;
import org.jtalks.jcommune.model.dao.PostDao;
import org.jtalks.jcommune.model.dao.TopicDao;
import org.jtalks.jcommune.model.dto.JCommunePageRequest;
import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.model.entity.Post;
import org.jtalks.jcommune.model.entity.Topic;
import org.jtalks.jcommune.service.LastReadPostService;
import org.jtalks.jcommune.service.PostService;
import org.jtalks.jcommune.service.UserService;
import org.jtalks.jcommune.service.exceptions.NotFoundException;
import org.jtalks.jcommune.service.nontransactional.NotificationService;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

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
    private static final Long BRANCH_ID = 1L;
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
    private LastReadPostService lastReadPostService;
    @Mock 
    private UserService userService;

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
                lastReadPostService,
                userService);
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
    void updatePost() throws NotFoundException {
        String newBody = "new body";
        Topic topic = new Topic(user, "title");
        Post post = new Post(user, "");
        topic.addPost(post);
        post.setId(POST_ID);
        topic.addPost(post);
        when(postDao.isExist(POST_ID)).thenReturn(true);
        when(postService.get(POST_ID)).thenReturn(post);

        postService.updatePost(post, newBody);

        assertEquals(post.getPostContent(), newBody);

        verify(notificationService).topicChanged(topic);

        verify(postDao).update(post);
    }

    @Test
    public void testDeletePost() throws NotFoundException {
        Topic topic = new Topic(user, "title");
        Post post = new Post(user, "content");
        post.setId(1L);
        Post postForDelete = new Post(user, "content");
        postForDelete.setId(POST_ID);
        topic.addPost(post);
        topic.addPost(postForDelete);
        user.setPostCount(2);
        when(postDao.isExist(POST_ID)).thenReturn(true);
        when(postDao.get(POST_ID)).thenReturn(postForDelete);

        postService.deletePost(POST_ID, BRANCH_ID);

        assertEquals(user.getPostCount(), 1);
        verify(postDao).get(POST_ID);
        verify(topicDao).update(topic);
        verify(securityService).deleteFromAcl(postForDelete);
        verify(notificationService).topicChanged(topic);
    }

    @Test(expectedExceptions = {NotFoundException.class})
    public void testDeleteNonExistentPost() throws NotFoundException {
        when(postDao.isExist(POST_ID)).thenReturn(false);

        postService.deletePost(POST_ID, BRANCH_ID);
    }

    @Test
    public void testPostsOfUser() {
        int page = 1;
        int pageSize = 50;
        boolean pagingEnabled = true;
        List<Post> posts = Arrays.asList(new Post(user, ""));
        Page<Post> expectedPostsPage = new PageImpl<Post>(posts);
        when(postDao.getUserPosts(Matchers.<JCUser> any(), Matchers.<JCommunePageRequest>any()))
            .thenReturn(expectedPostsPage);
        
        currentUser.setPageSize(pageSize);

        Page<Post> actualPostsPage = postService.getPostsOfUser(user, page, pagingEnabled);
        
        assertEquals(actualPostsPage, expectedPostsPage);
        verify(postDao).getUserPosts(
                Matchers.<JCUser> any(),
                Matchers.<JCommunePageRequest> any()
                );
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
        int pageSize = 50;
        Topic topic = new Topic(user, "");
        Page<Post> expectedPage = new PageImpl<Post>(Collections.<Post> emptyList());
        
        currentUser.setPageSize(pageSize);
        
        when(postDao.getPosts(
                Matchers.any(Topic.class), Matchers.any(JCommunePageRequest.class)))
            .thenReturn(expectedPage);
        
        Page<Post> actualPage = postService.getPosts(topic, pageSize, true);
        
        assertEquals(actualPage, expectedPage, "Service returned incorrect data for one page of posts");
        verify(postDao).getPosts(
                Matchers.any(Topic.class), Matchers.any(JCommunePageRequest.class));
    }
}
