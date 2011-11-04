package org.jtalks.jcommune.model.entity;

import static org.testng.Assert.assertTrue;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotSame;

import org.joda.time.DateTime;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class TopicTest {
    private Topic topic;
    Post post1 = new Post();
    Post post2 = new Post();
    
    @BeforeMethod
    public void setUp() {
        topic = new Topic(new User(), "title");
        topic.addPost(post1);
        topic.addPost(post2);
    }
    
    @Test
    public void getFirstPost() {
        Post firstPost = topic.getFirstPost();
        
        assertEquals(firstPost, post1);
    }

    @Test
    public void getLastPost() {
        Post lastPost = topic.getLastPost();
        
        assertEquals(lastPost, post2);
    }

    @Test
    public void getPostCount() {
        assertEquals(topic.getPostCount(), 2);
    }

    @Test
    public void removePost() {
        DateTime prevDate = topic.getModificationDate();
        
        topic.removePost(post1);
        
        assertEquals(topic.getPostCount(), 1);
        assertNotSame(topic.getModificationDate(), prevDate);
    }

    @Test
    public void updateModificationDate() {
        DateTime prevDate = topic.getModificationDate();
        
        DateTime modDate = topic.updateModificationDate();
        
        assertNotSame(modDate, prevDate);
    }
    
    @Test
    public void testSetStickedResetWeight() {
        topic.setTopicWeight(10);
        
        topic.setSticked(false);
        
        assertEquals(topic.getTopicWeight(), 0);
    }
    
    @Test
    public void testSetStickedNotResetWeight() {
        topic.setTopicWeight(10);
        
        topic.setSticked(true);
        
        assertEquals(topic.getTopicWeight(), 10);
    }
}
