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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.joda.time.DateTime;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author Evgeniy Naumenko
 */
public class PostTest {

    private Post post;

    @BeforeMethod
    public void init() {
        Topic topic = new Topic(null, "header");
        post = new Post(null, "content");
        topic.addPost(post);
    }

    @Test
    public void testGetPostIndex() {
        assertEquals(0, post.getPostIndexInTopic());
    }

    @Test
    public void testUpdatePostModificationDate() throws InterruptedException {
        post.updateModificationDate();
        DateTime prevDate = post.getModificationDate();
        Thread.sleep(25); // to catch the date difference

        post.updateModificationDate();

        assertTrue(post.getModificationDate().isAfter(prevDate));
    }
    
    @Test
    public void testGetLastTouchedDatePostWasNotModified() {
        DateTime createdDate = new DateTime();
        post.setCreationDate(createdDate);
        post.setModificationDate(null);
        
        assertEquals(post.getLastTouchedDate(), createdDate);
    }
    
    @Test
    public void testGetLastTouchedDatePostWasModified() {
        DateTime modifiedDate = new DateTime();
        post.setCreationDate(modifiedDate.minusDays(1));
        post.setModificationDate(modifiedDate);
        
        assertEquals(post.getLastTouchedDate(), modifiedDate);
    }
    
    @Test
    public void getTopicSubscribersShouldReturnSubscribersOfParentTopic() {
        Set<JCUser> expectedSubscribers = new HashSet<>();
        expectedSubscribers.add(new JCUser());
        expectedSubscribers.add(new JCUser());
        post.getTopic().setSubscribers(expectedSubscribers);
        
        Set<JCUser> actualSubscribers = post.getTopicSubscribers();
        
        assertEquals(actualSubscribers, expectedSubscribers,
                "Post should have the same subscribers as parent topic.");
    
    }

    @Test
    public void testAddComment() {
        PostComment comment = new PostComment();

        post.addComment(comment);

        assertTrue(post.getComments().contains(comment));
        assertEquals(comment.getOwnerPost(), post);
    }

    @Test
    public void testPutVote() {
        Post post = new Post();
        PostVote vote = ObjectsFactory.getDefaultPostVote();

        post.putVote(vote);

        assertTrue(post.getVotes().contains(vote));
    }

    @Test
    public void putVoteShouldOverrideVoteIfAlreadyExist() {
        Post post = new Post();
        PostVote vote = ObjectsFactory.getDefaultPostVote();
        vote.setVotedUp(true);
        post.putVote(vote);

        vote.setVotedUp(false);
        post.putVote(vote);

        assertEquals(post.getVotes().size(), 1);
        PostVote result = (PostVote)post.getVotes().toArray()[0];
        assertEquals(result.getUser(), vote.getUser());
        assertEquals(result.getPost(), vote.getPost());
        assertEquals(result.getVoteDate(), vote.getVoteDate());
        assertEquals(result.isVotedUp(), false);
    }

    @Test
    public void isVotedUpByShouldReturnTrueIfUserVotedUpForPost() {
        Post post = new Post();
        JCUser user = ObjectsFactory.getDefaultUser();
        PostVote vote = new PostVote(user);
        vote.setVotedUp(true);
        post.putVote(vote);

        assertTrue(post.isVotedUpBy(user));
    }

    @Test
    public void isVotedUpByShouldReturnFalseIfUserVotedDown() {
        Post post = new Post();
        JCUser user = ObjectsFactory.getDefaultUser();
        PostVote vote = new PostVote(user);
        vote.setVotedUp(false);
        post.putVote(vote);

        assertFalse(post.isVotedUpBy(user));
    }

    @Test
    public void isVotedUpByShouldReturnFalseIfUserNotVoted() {
        Post post = new Post();

        assertFalse(post.isVotedUpBy(ObjectsFactory.getDefaultUser()));
    }

    @Test
    public void isVotedDownByShouldReturnTrueIfUserVotedDownForPost() {
        Post post = new Post();
        JCUser user = ObjectsFactory.getDefaultUser();
        PostVote vote = new PostVote(user);
        vote.setVotedUp(false);
        post.putVote(vote);

        assertTrue(post.isVotedDownBy(user));
    }

    @Test
    public void isVotedDownByShouldReturnFalseIfUserVotedUp() {
        Post post = new Post();
        JCUser user = ObjectsFactory.getDefaultUser();
        PostVote vote = new PostVote(user);
        vote.setVotedUp(true);
        post.putVote(vote);

        assertFalse(post.isVotedDownBy(user));
    }

    @Test
    public void isVotedDownByShouldReturnFalseIfUserNotVoted() {
        Post post = new Post();

        assertFalse(post.isVotedDownBy(ObjectsFactory.getDefaultUser()));
    }

    @Test
    public void canBeVotedByShouldReturnTrueIfUserTryToVoteUpAndStillNotVoted() {
        Post post = new Post();

        assertTrue(post.canBeVotedBy(ObjectsFactory.getDefaultUser(), true));
    }

    @Test
    public void canBeVotedByShouldReturnTrueIfUserTryToVoteDownAndStillNotVoted() {
        Post post = new Post();

        assertTrue(post.canBeVotedBy(ObjectsFactory.getDefaultUser(), false));
    }

    @Test
    public void canBeVotedByShouldReturnTrueIfUserTyToUpOldVote() {
        Post post = new Post();
        JCUser user = ObjectsFactory.getDefaultUser();
        PostVote vote = new PostVote(user);
        vote.setVotedUp(false);
        post.putVote(vote);

        assertTrue(post.canBeVotedBy(user, true));
    }

    @Test
    public void canBeVotedByShouldReturnTrueIfUserTyToDownOldVote() {
        Post post = new Post();
        JCUser user = ObjectsFactory.getDefaultUser();
        PostVote vote = new PostVote(user);
        vote.setVotedUp(true);
        post.putVote(vote);

        assertTrue(post.canBeVotedBy(user, false));
    }

    @Test
    public void canBeVotedByFalseIfUserTryToVoteUpSecondTime() {
        Post post = new Post();
        JCUser user = ObjectsFactory.getDefaultUser();
        PostVote vote = new PostVote(user);
        vote.setVotedUp(true);
        post.putVote(vote);

        assertFalse(post.canBeVotedBy(user, true));
    }

    @Test
    public void canBeVotedByFalseIfUserTryToVoteDownSecondTime() {
        Post post = new Post();
        JCUser user = ObjectsFactory.getDefaultUser();
        PostVote vote = new PostVote(user);
        vote.setVotedUp(false);
        post.putVote(vote);

        assertFalse(post.canBeVotedBy(user, false));
    }

    @Test
    public void testCalculateRatingChangesWhenUserCanNotVote() {
        Post post = new Post();
        JCUser user = ObjectsFactory.getDefaultUser();
        PostVote vote = new PostVote(user);
        vote.setVotedUp(false);
        post.putVote(vote);

        assertEquals(post.calculateRatingChanges(vote), 0);
    }

    @Test
    public void testCalculateRatingChangesWhenUserVoteUpFirstTime() {
        Post post = new Post();
        JCUser user = ObjectsFactory.getDefaultUser();
        PostVote vote = new PostVote(user);
        vote.setVotedUp(true);

        assertEquals(post.calculateRatingChanges(vote), 1);
    }

    @Test
    public void testCalculateRatingChangesWhenUserVoteDownFirstTime() {
        Post post = new Post();
        JCUser user = ObjectsFactory.getDefaultUser();
        PostVote vote = new PostVote(user);
        vote.setVotedUp(false);

        assertEquals(post.calculateRatingChanges(vote), -1);
    }

    @Test
    public void testCalculateRatingChangesWhenUserChangesVoteFromUpToDown() {
        Post post = new Post();
        JCUser user = ObjectsFactory.getDefaultUser();
        PostVote vote1 = new PostVote(user);
        vote1.setVotedUp(true);
        post.putVote(vote1);
        PostVote vote2 = new PostVote(user);
        vote2.setVotedUp(false);

        assertEquals(post.calculateRatingChanges(vote2), -2);
    }

    @Test
    public void testCalculateRatingChangesWhenUserChangesVoteFromDownToUp() {
        Post post = new Post();
        JCUser user = ObjectsFactory.getDefaultUser();
        PostVote vote1 = new PostVote(user);
        vote1.setVotedUp(false);
        post.putVote(vote1);
        PostVote vote2 = new PostVote(user);
        vote2.setVotedUp(true);

        assertEquals(post.calculateRatingChanges(vote2), 2);
    }

    @Test
    public void testGetNotRemovedComments() {
        Post post = new Post();
        PostComment comment1 = new PostComment();
        PostComment comment2 = new PostComment();
        comment2.setDeletionDate(new DateTime());
        post.addComment(comment1);
        post.addComment(comment2);

        List<PostComment> notRemovedComments = post.getNotRemovedComments();

        assertEquals(1, notRemovedComments.size());
        assertTrue(notRemovedComments.contains(comment1));
    }

    @Test
    public void getCommentsShouldReturnAllCommentsIncludingMarkedAsDeleted() {
        Post post = new Post();
        PostComment comment1 = new PostComment();
        PostComment comment2 = new PostComment();
        comment2.setDeletionDate(new DateTime());
        post.addComment(comment1);
        post.addComment(comment2);

        List<PostComment> result = post.getComments();

        assertEquals(2, result.size());
        assertTrue(result.contains(comment1));
        assertTrue(result.contains(comment2));
    }
}
