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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * This test cover {@code TransactionalPostService} logic validation.
 * Logic validation cover update/get/error cases by this class.
 *
 * @author Osadchuck Eugeny
 * @author Kirill Afonin
 */
public class TransactionalPostServiceTest {

    final long POST_ID = 9L;
    final long TOPIC_ID = 1L;

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
        user = new User("username", "email@mail.com", "password");
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
    public void testGetPostRangeInTopic() throws NotFoundException {
        int start = 1;
        int max = 2;
        List<Post> expectedList = new ArrayList<Post>();
        expectedList.add(new Post(user, "content"));
        expectedList.add(new Post(user, "content"));
        when(postDao.getPostRangeInTopic(TOPIC_ID, start, max)).thenReturn(expectedList);
        when(topicDao.isExist(TOPIC_ID)).thenReturn(true);

        List<Post> posts = postService.getPostRangeInTopic(TOPIC_ID, start, max);

        assertNotNull(posts);
        assertEquals(posts, expectedList, "Unexpected list size");
        verify(postDao).getPostRangeInTopic(TOPIC_ID, start, max);
        verify(topicDao).isExist(TOPIC_ID);
    }

    @Test(expectedExceptions = {NotFoundException.class})
    public void testGetPostsRangeInNonExistentTopic() throws NotFoundException {
         when(topicDao.isExist(TOPIC_ID)).thenReturn(false);

        postService.getPostRangeInTopic(TOPIC_ID, 1, 5);
    }

    @Test
    public void testGetPostsInTopicCount() throws NotFoundException {
        int expectedCount = 10;
        when(postDao.getPostsInTopicCount(TOPIC_ID)).thenReturn(expectedCount);
        when(topicDao.isExist(TOPIC_ID)).thenReturn(true);

        int count = postService.getPostsInTopicCount(TOPIC_ID);

        assertEquals(count, expectedCount);
        verify(postDao).getPostsInTopicCount(TOPIC_ID);
        verify(topicDao).isExist(TOPIC_ID);
    }

    @Test(expectedExceptions = {NotFoundException.class})
    public void testGetPostsCountInNonExistentTopic() throws NotFoundException {
        when(topicDao.isExist(TOPIC_ID)).thenReturn(false);

        postService.getPostsInTopicCount(TOPIC_ID);
    }
    
    @Test
    void updatePost() throws NotFoundException {
        String newBody = "new body";
        Topic topic = new Topic(user, "title");
        Post post = new Post(topic);          
        post.setId(POST_ID);
        
        when(postDao.isExist(POST_ID)).thenReturn(true);      
        when(postService.get(POST_ID)).thenReturn(post); 
        
        postService.updatePost(POST_ID,newBody);
        
        assertEquals(post.getPostContent(), newBody);
        
        verify(postDao).get(POST_ID);
        verify(postDao).update(post);        
    }


    @Test
    public void testDeletePost() throws NotFoundException {
        Topic topic = new Topic(user, "title");
        Post post1 = new Post(user, "content");
        post1.setId(1L);
        Post postForDelete = new Post(user, "content");
        postForDelete.setId(POST_ID);
        topic.addPost(post1);
        topic.addPost(postForDelete);
        when(postDao.isExist(POST_ID)).thenReturn(true);
        when(postDao.get(POST_ID)).thenReturn(postForDelete);

        postService.deletePost(POST_ID);

        assertEquals(topic.postCount(), 1, "Post not deleted from list");
        verify(postDao).get(POST_ID);
        verify(topicDao).update(topic);
        verify(securityService).deleteFromAcl(postForDelete);
    }

    @Test(expectedExceptions = {NotFoundException.class})
    public void testDeleteNonExistentPost() throws NotFoundException {
        when(postDao.isExist(POST_ID)).thenReturn(false);

        postService.deletePost(POST_ID);
    }
}
