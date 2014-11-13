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

import java.util.HashSet;
import java.util.Set;

import static org.jtalks.jcommune.model.entity.ObjectsFactory.getDefaultTopic;
import static org.jtalks.jcommune.model.entity.ObjectsFactory.getRandomUser;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertTrue;

/**
 * 
 * @author Vyacheslav Mishcheryakov
 *
 */
public class CodeReviewTest {

    private CodeReview review;
    
    @BeforeMethod
    public void initEnvironmental() {
        review = new CodeReview();
    }
    
//    @Test
//    public void testAddComment() {
//        review.addComment(new PostComment());
//
//        assertEquals(review.getComments().size(), 1);
//    }

    @Test
    public void testIsUserSubscribedToCR() {
        JCUser subscribedUser = getRandomUser();
        JCUser unsubscribedUser = getRandomUser();
        Topic topic = getDefaultTopic();
        topic.setCodeReview(review);
        review.setTopic(topic);
        Set<JCUser> subscribers = new HashSet<>();
        subscribers.add(subscribedUser);
        review.setSubscribers(subscribers);
        assertTrue(review.isUserSubscribed(subscribedUser));
        assertFalse(review.isUserSubscribed(unsubscribedUser));
    }
    
    @Test
    public void getOwnerPostShouldReturnFirstPostOfCodeReviewTopic() {
        Topic topic = new Topic();
        Post expectedOwnerPost = new Post();
        topic.addPost(expectedOwnerPost);
        CodeReview codeReview = new CodeReview();
        codeReview.setTopic(topic);
        
        Post actualOwnerPost = codeReview.getOwnerPost();
        
        assertEquals("It should return first post of owner topic, cause it contains code review.",
                expectedOwnerPost, actualOwnerPost);
    }
}
