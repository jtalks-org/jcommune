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
import org.jtalks.jcommune.model.dao.TopicDao;
import org.jtalks.jcommune.model.entity.Post;
import org.jtalks.jcommune.model.entity.Topic;
import org.jtalks.jcommune.model.entity.User;
import org.jtalks.jcommune.service.TopicService;
import org.mockito.Matchers;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author Osadchuck Eugeny
 *
 */
public class TransactionalTopicServiceTest {
    
    final Long TOPIC_ID = new Long(999);
    final String TOPIC_TITLE = "topic title";
    final DateTime TOPIC_CREATION_DATE = new DateTime();

    private TopicService topicService;
    private TopicDao topicDao;

    @BeforeMethod
    public void setUp() throws Exception {
        topicDao = mock(TopicDao.class);
        topicService = new TransactionalTopicService(topicDao);
    }
    
    
    private Topic getTopic(){
        User topicStarter = new User();
        topicStarter.setId(new Long(333));
        topicStarter.setUsername("username");
        Topic topic = new Topic();
        topic.setCreationDate(TOPIC_CREATION_DATE);
        topic.setId(TOPIC_ID);
        topic.setTitle(TOPIC_TITLE);
        topic.setTopicStarter(topicStarter);
   
        return topic;
    }
    
    @Test
    public void saveOrUpdateTest(){
        Topic topic = getTopic();        
        topicService.saveOrUpdate(topic);
        
        verify(topicDao, times(1)).saveOrUpdate(Matchers.<Topic>any());
    }
    
    @Test
    public void deleteByIdTest(){
        topicService.delete(TOPIC_ID);
        
        verify(topicDao, times(1)).delete(Matchers.anyLong());
    }
    
    @Test
    public void deleteTest(){
        Topic topic = getTopic();        
        topicService.delete(topic);
        
        verify(topicDao, times(1)).delete(Matchers.<Topic>any());
    }
    
    @Test
    public void getByIdTest(){
        when(topicDao.get(TOPIC_ID)).thenReturn(getTopic());
        
        Topic topic = topicService.get(TOPIC_ID);
        
        Assert.assertEquals(topic, getTopic(), "Topics aren't equals");
        
        verify(topicDao, times(1)).get(Matchers.anyLong());
    }
    
    @Test
    public void getAllTest(){
        List<Topic> expectedUserList = new ArrayList<Topic>();
        expectedUserList.add(getTopic());
        when(topicDao.getAll()).thenReturn(expectedUserList);
        
        List<Topic> actualUserList = topicService.getAll();  
        
        Assert.assertEquals(actualUserList, expectedUserList, "Topics lists aren't equals");
        
        verify(topicDao, times(1)).getAll();
    }
    
    @Test
    public void getTopicWithPostsTest(){
        Topic topic = getTopic();
        List<Post> posts = new ArrayList<Post>();
        topic.setPosts(posts);
        when(topicDao.getTopicWithPosts(TOPIC_ID)).thenReturn(topic);
        when(topicService.getTopicWithPosts(TOPIC_ID)).thenReturn(getTopic());
        
        Topic actualTopic = topicService.getTopicWithPosts(TOPIC_ID);        
        
        Assert.assertNotNull(actualTopic.getPosts(), "Posts is null");
        
        verify(topicDao, times(1)).getTopicWithPosts(Matchers.anyLong());        
    }    

}
