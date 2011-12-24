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

import org.jtalks.jcommune.model.dao.PostDao;
import org.jtalks.jcommune.model.dao.TopicDao;
import org.jtalks.jcommune.model.entity.Post;
import org.jtalks.jcommune.model.entity.Topic;
import org.jtalks.jcommune.model.entity.User;
import org.jtalks.jcommune.service.PostService;
import org.jtalks.jcommune.service.SecurityService;
import org.jtalks.jcommune.service.exceptions.NotFoundException;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.testng.Assert.assertEquals;


/**
 * This test cover {@code TransactionalPostService} logic validation.
 * Logic validation cover update/get/error cases by this class.
 *
 * @author Osadchuck Eugeny
 * @author Kirill Afonin
 */
public class TransactionalPostServiceTest {

    final long POST_ID = 9L;
    private static final String USERNAME = "username";
    private static final String EMAIL = "username@mail.com";
    private static final String PASSWORD = "password";

    private PostService postService;
    private PostDao postDao;
    private SecurityService securityService;
    private TopicDao topicDao;
    private User user;

    @BeforeMethod
    public void setUp() throws Exception {
        postDao = mock(PostDao.class);
        topicDao = mock(TopicDao.class);
        securityService = mock(SecurityService.class);
        postService = new TransactionalPostService(postDao, topicDao, securityService);
        user = new User(USERNAME, EMAIL, PASSWORD);
        when(securityService.getCurrentUser()).thenReturn(user);
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

        postService.updatePost(POST_ID, newBody);

        assertEquals(post.getPostContent(), newBody);

        verify(postDao).get(POST_ID);
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
        when(postDao.isExist(POST_ID)).thenReturn(true);
        when(postDao.get(POST_ID)).thenReturn(postForDelete);

        postService.deletePost(POST_ID);

        verify(postDao).get(POST_ID);
        verify(topicDao).update(topic);
        verify(securityService).deleteFromAcl(postForDelete);
    }

    @Test(expectedExceptions = {NotFoundException.class})
    public void testDeleteNonExistentPost() throws NotFoundException {
        when(postDao.isExist(POST_ID)).thenReturn(false);

        postService.deletePost(POST_ID);
    }

    @Test
    public void testNullPostsOfUser() {
        List<Post> posts = new ArrayList<Post>();
        when(postDao.getPostsOfUser(user)).thenReturn(posts);

        assertEquals(postService.getPostsOfUser(user), new ArrayList<Post>());

        verify(postDao).getPostsOfUser(user);
    }

    @Test
    public void testPostsOfUser() {
        List<Post> posts = new ArrayList<Post>();
        Post post = new Post(user, "");
        posts.add(post);
        when(postDao.getPostsOfUser(user)).thenReturn(posts);

        assertEquals(postService.getPostsOfUser(user), posts);

        verify(postDao).getPostsOfUser(user);
    }

    @Test
    public void testLastPostInTopicPageCalculation() {
        user.setPageSize(2);
        Topic topic = new Topic(user, "");
        Post post = new Post(user, "");
        topic.addPost(new Post(null, null));
        topic.addPost(new Post(null, null));
        topic.addPost(post);

        assertEquals(postService.getPageForPost(post), 2);
    }

    @Test
    public void testFirstPostInTopicPageCalculation() {
        user.setPageSize(2);
        Topic topic = new Topic(user, "");
        Post post = new Post(user, "");
        topic.addPost(post);

        assertEquals(postService.getPageForPost(post), 1);
    }

    @Test
    public void testFirstPostInTopicPageCalculationWithNoUser() {
        when(securityService.getCurrentUser()).thenReturn(null);
        Topic topic = new Topic(user, "");
        Post post = new Post(user, "");
        topic.addPost(post);

        assertEquals(postService.getPageForPost(post), 1);
    }

    @Test
    public void testLastPostOnFirstPagePageCalculation() {
        user.setPageSize(2);
        Topic topic = new Topic(user, "");
        Post post = new Post(user, "");
        topic.addPost(new Post(null, null));
        topic.addPost(post);

        assertEquals(postService.getPageForPost(post), 1);
    }

    @Test
    public void testLastPostOnPagePageCalculation() {
        user.setPageSize(2);
        Topic topic = new Topic(user, "");
        Post post = new Post(user, "");
        topic.addPost(new Post(null, null));
        topic.addPost(new Post(null, null));
        topic.addPost(new Post(null, null));
        topic.addPost(post);

        assertEquals(postService.getPageForPost(post), 2);
    }

    @Test
    public void testPostInCenterOfTopicPageCalculation() {
        user.setPageSize(2);
        Topic topic = new Topic(user, "");
        Post post = new Post(user, "");
        topic.addPost(new Post(null, null));
        topic.addPost(post);
        topic.addPost(new Post(null, null));

        assertEquals(postService.getPageForPost(post), 1);
    }
}
