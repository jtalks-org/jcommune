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
package org.jtalks.jcommune.model.entity;

import org.joda.time.DateTime;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.HashSet;
import java.util.Set;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotSame;
import static org.testng.Assert.assertTrue;

public class TopicTest {
    private Topic topic;
    Post post1 = new Post();
    Post post2 = new Post();

    @BeforeMethod
    public void setUp() {
        topic = new Topic(new JCUser(), "title");
        topic.addPost(post1);
        topic.addPost(post2);
    }

    @Test
    public void firstPostShouldReturnFirstPostOfTheTopic() {
        Post firstPost = topic.getFirstPost();

        assertEquals(firstPost, post1);
    }

    @Test
    public void addPostShouldUpdateModificationDate() throws InterruptedException {
        DateTime prevDate = topic.getModificationDate();
        Thread.sleep(25); // millisecond precise is a kind of fiction
        topic.addPost(new Post());

        assertTrue(topic.getModificationDate().isAfter(prevDate));
    }


    public void updatePostShouldUpdateModificationDate() throws InterruptedException {
        DateTime prevDate = topic.getModificationDate();
        Thread.sleep(25); // millisecond precise is a kind of fiction
        post1.updateModificationDate();

        assertTrue(topic.getModificationDate().isBefore(prevDate));
    }

    @Test
    public void updateModificationDateShouldChangeTheModificationDate() {
        DateTime prevDate = topic.getModificationDate();

        DateTime modDate = topic.updateModificationDate();

        assertNotSame(modDate, prevDate);
    }
    
    @Test
    public void recalculateModificationDateShouldSetModificationDateAsTheLatestDateAmongAllPosts() {
        DateTime lastModificationDate = new DateTime();
        
        topic.getFirstPost().setCreationDate(lastModificationDate.minusDays(1));
        topic.getPosts().get(1).setCreationDate(lastModificationDate);
        Post post3 = new Post();
        post3.setCreationDate(lastModificationDate.minusDays(2));
        
        topic.addPost(post3);
        
        topic.updateModificationDate();
        topic.recalculateModificationDate();
        
        assertEquals(topic.getModificationDate(), lastModificationDate);
    }

    @Test
    public void hasUpdatesShouldReturnTrueByDefault() {
        assertTrue(topic.isHasUpdates());
    }

    @Test
    public void hasUpdatesShouldReturnTrueInCaseOfUpdatesExist() {
        topic.setLastReadPostDate(topic.getFirstPost().getCreationDate());
        assertTrue(topic.isHasUpdates());
    }

    @Test
    public void hasUpdatesShouldReturnFalseInCaseOfNoUpdatesExist() {
        DateTime lastModificationDate = new DateTime();

        topic.getFirstPost().setCreationDate(lastModificationDate.minusDays(1));
        topic.getPosts().get(1).setCreationDate(lastModificationDate);

        topic.setLastReadPostDate(topic.getLastPost().getCreationDate());
        assertFalse(topic.isHasUpdates());
    }

    @Test
    public void getFirstUnreadPostIdShouldReturnTheNextPostAfterLastRead() {
        DateTime lastModificationDate = new DateTime();

        topic.getFirstPost().setCreationDate(lastModificationDate.minusDays(1));
        topic.getPosts().get(1).setCreationDate(lastModificationDate);

        topic.setLastReadPostDate(topic.getFirstPost().getCreationDate());

        long id = topic.getFirstUnreadPostId();

        assertEquals(post2.getId(), id);
    }

    @Test
    public void getFirstUnreadPostIdShouldReturnFirstPostIdIfAllPostAreRead() {
        DateTime lastModificationDate = new DateTime();

        topic.getFirstPost().setCreationDate(lastModificationDate.minusDays(1));
        topic.getPosts().get(1).setCreationDate(lastModificationDate);

        long id = topic.getFirstUnreadPostId();

        assertEquals(post1.getId(), id);
    }

    @Test
    public void topicShouldHasNoUpdatesIfLastReadPostIsTheLatestPost() {
        DateTime lastPostCreationDate = topic.getLastPost().getCreationDate();
        topic.setLastReadPostDate(lastPostCreationDate);
        assertEquals(topic.getLastReadPostDate(), lastPostCreationDate);
        assertFalse(topic.isHasUpdates());
    }

    @Test
    public void removePostShouldRemovePostFromTheTopic() {
        DateTime lastModification = new DateTime(1900, 11, 11, 11, 11, 11, 11);
        topic.setModificationDate(lastModification);

        topic.removePost(post1);

        assertFalse(topic.getPosts().contains(post1), "The post isn't removed from the topic");
    }

    @Test
    public void setSubscribersShouldSubscribeUserToTheTopic() {
        JCUser subscribedUser = new JCUser();
        JCUser notSubscribedUser = new JCUser();
        Set<JCUser> subscribers = new HashSet<JCUser>();
        subscribers.add(subscribedUser);
        topic.setSubscribers(subscribers);
        assertTrue(topic.userSubscribed(subscribedUser));
        assertFalse(topic.userSubscribed(notSubscribedUser));
    }
}
