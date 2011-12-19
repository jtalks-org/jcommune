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

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.assertEquals;


/**
 * @author Evgeniy Naumenko
 */
public class BranchTest {

    private Branch branch;
    private Topic first;
    private Topic second;
    private Topic third;

    @BeforeMethod
    public void setUp() {
        branch = new Branch("test branch");
        List<Topic> topics = new ArrayList<Topic>();
        first = new Topic(null, null);
        second = new Topic(null, null);
        third = new Topic(null, null);
        topics.add(first);
        topics.add(second);
        topics.add(third);
        branch.setTopics(topics);

    }

    @Test
    public void testNextTopicRetrieval() {
        assertEquals(branch.getNextTopic(first), second);
        assertEquals(branch.getNextTopic(second), third);
        assertEquals(branch.getNextTopic(third), null);
    }

    @Test
    public void testPreviousTopicRetrieval() {
        assertEquals(branch.getPreviousTopic(first), null);
        assertEquals(branch.getPreviousTopic(second), first);
        assertEquals(branch.getPreviousTopic(third), second);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testPreviousTopicRetrievalError() {
        branch.getNextTopic(new Topic(null, null));
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testNextTopicRetrievalError() {
        branch.getPreviousTopic(new Topic(null, null));
    }

    @Test
    public void testLastUpdatedTopic() throws InterruptedException {
        Thread.sleep(25);
        second.addPost(new Post());
        assertEquals(branch.getLastUpdatedTopic(), second);
    }

    @Test
    public void testLastUpdatedTopicWithoutTopics()  {
        branch.setTopics(new ArrayList<Topic>());
        assertEquals(branch.getLastUpdatedTopic(), null);
    }

    @Test
    public void testPostCount(){
        first.addPost(new Post());
        second.addPost(new Post());
        third.addPost(new Post());
        assertEquals(branch.getPostCount(), 3);
    }

    @Test
    public void testPostCountWithoutTopics(){
        branch.setTopics(new ArrayList<Topic>());
        assertEquals(branch.getPostCount(), 0);
    }
}
