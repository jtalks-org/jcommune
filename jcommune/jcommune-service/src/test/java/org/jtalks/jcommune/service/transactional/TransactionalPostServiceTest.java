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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.jtalks.jcommune.model.dao.PostDao;
import org.jtalks.jcommune.model.entity.Post;
import org.jtalks.jcommune.model.entity.Topic;
import org.jtalks.jcommune.model.entity.User;
import org.jtalks.jcommune.service.PostService;
import org.mockito.Matchers;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author Osadchuck Eugeny
 *
 */
public class TransactionalPostServiceTest {
    
    final Long POST_ID = new Long(999);
    final String POST_CONTENT = "post content";
    final DateTime POST_CREATION_DATE = new DateTime();

    private PostService postService;
    private PostDao postDao;

    @BeforeMethod
    public void setUp() throws Exception {
        postDao = mock(PostDao.class);
        postService = new TransactionalPostService(postDao);
    }
        
    private Post getPost(){
        User topicStarter = new User();
        topicStarter.setId(new Long(333));
        topicStarter.setUsername("username");
        Topic topic = new Topic();
        topic.setId(333);
        topic.setTopicStarter(topicStarter);
        
        Post post = new Post();
        post.setId(POST_ID);
        post.setPostContent(POST_CONTENT);
        post.setCreationDate(POST_CREATION_DATE);
        post.setTopic(topic);
        return post;
    }
    
    @Test
    public void saveOrUpdateTest(){
        Post post = getPost();        
        postService.saveOrUpdate(post);
        
        verify(postDao, times(1)).saveOrUpdate(Matchers.<Post>any());
    }
    
    @Test
    public void deleteByIdTest(){
        postService.delete(POST_ID);
        
        verify(postDao, times(1)).delete(Matchers.anyLong());
    }
    
    @Test
    public void deleteTest(){
        Post post = getPost();        
        postService.delete(post);
        
        verify(postDao, times(1)).delete(Matchers.<Post>any());
    }
    
    @Test
    public void getByIdTest(){
        when(postDao.get(POST_ID)).thenReturn(getPost());
        
        Post post = postService.get(POST_ID);
        
        Assert.assertEquals(post, getPost(), "Posts aren't equals");
        
        verify(postDao, times(1)).get(Matchers.anyLong());
    }
    
    @Test
    public void getAllTest(){
        List<Post> expectedUserList = new ArrayList<Post>();
        expectedUserList.add(getPost());
        when(postDao.getAll()).thenReturn(expectedUserList);
        
        List<Post> actualUserList = postService.getAll();  
        
        Assert.assertEquals(actualUserList, expectedUserList, "Posts lists aren't equals");
        
        verify(postDao, times(1)).getAll();
    }

}
