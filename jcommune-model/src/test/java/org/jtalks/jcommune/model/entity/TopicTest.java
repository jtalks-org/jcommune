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
import org.testng.annotations.Test;

import java.util.*;

import static org.testng.Assert.*;

public class TopicTest {

    @Test
    public void getNeighborPostIfOnlyOnePostInTheTopic() {
        Post singlePost = new Post();
        Topic topicWithSinglePost = new Topic(new JCUser(),
                "title4getNeighborPostIfOnlyOnePostInTheTopic");
        topicWithSinglePost.addPost(singlePost);

        Post post = topicWithSinglePost.getNeighborPost(singlePost);
        // same post (singlePost) should be returned
        assertEquals(post, singlePost);
    }

    @Test
    public void getNeighborDisplayedPostIfOnlyOnePostInTheTopic() {
        Post singlePost = new Post();
        Topic topicWithSinglePost = new Topic(new JCUser(),
                "title4getNeighborPostIfOnlyOnePostInTheTopic");
        topicWithSinglePost.addPost(singlePost);

        Post post = topicWithSinglePost.getNeighborDisplayedPost(singlePost);
        // same post (singlePost) should be returned
        assertEquals(post, singlePost);
    }

    @Test
    public void getNeighborPostForMiddlePostInTheTopic() {
        Topic topicLocal = new Topic(new JCUser(),
                "title4getNeighborPostForMiddlePostInTheTopic");
        // add 3 posts to topic
        Post[] posts = postList(3, topicLocal);
        // get neighbor for post in the middle.
        Post post = topicLocal.getNeighborPost(posts[1]);
        // next post (post2) should be returned
        assertEquals(post, posts[2]);
    }

    @Test
    public void getNeighborDisplayedPostShouldReturnNextDisplayedPostForMiddlePostInTheTopic() {
        Topic topicLocal = new Topic(new JCUser(),
                "title4getNeighborPostForMiddlePostInTheTopic");
        // add 3 posts to topic
        Post[] posts = postList(4, topicLocal);
        topicLocal.getPosts().get(2).setState(PostState.DRAFT);
        // get neighbor for post in the middle.
        Post post = topicLocal.getNeighborDisplayedPost(posts[1]);
        // next post (post2) should be returned
        assertEquals(post, posts[3]);
    }

    @Test
    public void getNeighborDisplayedPostShouldReturnPreviewsDisplayedPostForMiddlePostInTheTopicIfAllNexNotDisplayed() {
        Topic topicLocal = new Topic(new JCUser(),
                "title4getNeighborPostForMiddlePostInTheTopic");
        // add 3 posts to topic
        Post[] posts = postList(4, topicLocal);
        topicLocal.getPosts().get(3).setState(PostState.DRAFT);
        // get neighbor for post in the middle.
        Post post = topicLocal.getNeighborDisplayedPost(posts[2]);
        // next post (post2) should be returned
        assertEquals(post, posts[1]);
    }

    @Test
    public void getNeighborPostForLastPostInTheTopic() {
        Topic topic = createTopic();
        Post post = topic.getNeighborPost(topic.getPosts().get(1));
        // previous post (post1) should be returned
        assertEquals(post, topic.getPosts().get(0));
    }

    @Test
    public void firstPostShouldReturnFirstPostOfTheTopic() {
        Topic topic = createTopic();
        Post firstPost = topic.getFirstPost();

        assertEquals(firstPost, topic.getPosts().get(0));
    }

    @Test
    public void addPostShouldUpdateModificationDate() throws InterruptedException {
        Topic topic = createTopic();
        DateTime prevDate = topic.getModificationDate();
        Post post = new Post();
        post.setCreationDate(new DateTime().plusHours(1));
        topic.addPost(post);
        assertTrue(topic.getModificationDate().isAfter(prevDate));
    }

    @Test
    public void additionOfDraftShouldNotUpdateModificetionDate() {
        Topic topic = createTopic();
        DateTime modificationDate = topic.getModificationDate();
        Post post = new Post();
        post.setState(PostState.DRAFT);
        post.setCreationDate(new DateTime().plusMonths(12));

        topic.addPost(post);

        assertEquals(topic.getModificationDate(), modificationDate);
    }

    @Test
    public void addPostShouldSetModificationDateToPostCreationDate() throws InterruptedException {
        Topic topic = createTopic();
        DateTime prevDate = topic.getModificationDate();
        Post post = new Post();
        post.setCreationDate(new DateTime().plusDays(1));
        topic.addPost(post);

        assertEquals(topic.getModificationDate(), post.getCreationDate());
        assertFalse(prevDate.equals(topic.getModificationDate()));
    }

    @Test
    public void recalculateModificationDateShouldSetModificationDateAsTheLatestDateAmongAllPosts() {
        Topic topic = createTopic();
        DateTime lastModificationDate = new DateTime();
        
        topic.getFirstPost().setCreationDate(lastModificationDate.minusDays(1));
        topic.getPosts().get(1).setCreationDate(lastModificationDate);
        Post post3 = new Post();
        post3.setCreationDate(lastModificationDate.minusDays(2));
        
        topic.addPost(post3);
        
        topic.recalculateModificationDate();
        
        assertEquals(topic.getModificationDate(), lastModificationDate);
    }

    @Test
    public void hasUpdatesShouldReturnTrueByDefault() {
        Topic topic = createTopic();

        assertTrue(topic.isHasUpdates());
    }

    @Test
    public void isHasUpdatesInDisplayedPostsShouldReturnTrueByDefault() {
        Topic topic = createTopic();

        assertTrue(topic.isHasUpdatesInDisplayedPosts());
    }

    @Test
    public void hasUpdatesShouldReturnTrueInCaseOfUpdatesExist() {
        Topic topic = createTopic();
        DateTime creationDate = new DateTime();
        topic.getFirstPost().setCreationDate(creationDate);
        topic.getLastPost().setCreationDate(creationDate.plusDays(1));
        topic.setLastReadPostDate(creationDate);

        assertTrue(topic.isHasUpdates());
    }

    @Test
    public void hasUpdatesInDisplayedPostsShouldReturnTrueInCaseOfUpdatesExist() {
        Topic topic = createTopic();
        DateTime creationDate = new DateTime();
        topic.getFirstPost().setCreationDate(creationDate);
        topic.getLastPost().setCreationDate(creationDate.plusDays(1));
        topic.setLastReadPostDate(creationDate);

        assertTrue(topic.isHasUpdatesInDisplayedPosts());
    }

    @Test
    public void hasUpdatesShouldReturnFalseInCaseOfNoUpdatesExist() {
        Topic topic = createTopic();
        DateTime lastModificationDate = new DateTime();

        topic.getFirstPost().setCreationDate(lastModificationDate.minusDays(1));
        topic.getPosts().get(1).setCreationDate(lastModificationDate);

        topic.setLastReadPostDate(topic.getLastPost().getCreationDate());
        assertFalse(topic.isHasUpdates());
    }

    @Test
    public void hasUpdatesInDisplayedPostsShouldReturnFalseInCaseOfNoUpdatesExist() {
        Topic topic = createTopic();
        DateTime lastModificationDate = new DateTime();

        topic.getFirstPost().setCreationDate(lastModificationDate.minusDays(1));
        topic.getPosts().get(1).setCreationDate(lastModificationDate);

        topic.setLastReadPostDate(topic.getLastPost().getCreationDate());
        assertFalse(topic.isHasUpdatesInDisplayedPosts());
    }

    @Test
    public void hasUpdatesInDisplayedPostsShouldReturnFalseIfUpdatesExistInNotDisplayedPosts() {
        Topic topic = createTopic();
        DateTime creationDate = new DateTime();
        topic.getFirstPost().setCreationDate(creationDate);
        topic.getLastPost().setCreationDate(creationDate.plusDays(1));
        topic.getLastPost().setState(PostState.DRAFT);
        topic.setLastReadPostDate(creationDate);

        assertFalse(topic.isHasUpdatesInDisplayedPosts());
    }

    @Test
    public void getFirstUnreadPostIdShouldReturnTheNextPostAfterLastRead() {
        Topic topic = createTopic();
        DateTime lastModificationDate = new DateTime();

        topic.getFirstPost().setCreationDate(lastModificationDate.minusDays(1));
        topic.getPosts().get(1).setCreationDate(lastModificationDate);

        topic.setLastReadPostDate(topic.getFirstPost().getCreationDate());

        long id = topic.getFirstUnreadPostId();

        assertEquals(topic.getPosts().get(1).getId(), id);
    }

    @Test
    public void getFirstUnreadDisplayedPostIdShouldReturnTheNextDisplayedPostAfterLastRead() {
        Topic topic = createTopic();
        DateTime lastModificationDate = new DateTime();

        topic.getFirstPost().setCreationDate(lastModificationDate.minusDays(1));
        topic.getPosts().add(1, new Post(new JCUser(), "conetnt", PostState.DRAFT));
        topic.getPosts().get(2).setCreationDate(lastModificationDate);

        topic.setLastReadPostDate(topic.getFirstPost().getCreationDate());

        long id = topic.getFirstUnreadDisplayedPostId();

        assertEquals(topic.getPosts().get(2).getId(), id);
    }


    @Test
    public void getFirstUnreadPostIdShouldReturnFirstPostIdIfAllPostAreRead() {
        Topic topic = createTopic();
        DateTime lastModificationDate = new DateTime();

        topic.getFirstPost().setCreationDate(lastModificationDate.minusDays(1));
        topic.getPosts().get(1).setCreationDate(lastModificationDate);

        long id = topic.getFirstUnreadPostId();

        assertEquals(topic.getPosts().get(0).getId(), id);
    }

    @Test
    public void getFirstUnreadDisplayedPostIdShouldReturnFirstPostIdIfAllPostAreRead() {
        Topic topic = createTopic();
        DateTime lastModificationDate = new DateTime();

        topic.getFirstPost().setCreationDate(lastModificationDate.minusDays(1));
        topic.getPosts().get(1).setCreationDate(lastModificationDate);

        long id = topic.getFirstUnreadDisplayedPostId();

        assertEquals(topic.getPosts().get(0).getId(), id);
    }

    @Test
    public void topicShouldHasNoUpdatesIfLastReadPostIsTheLatestPost() {
        Topic topic = createTopic();
        DateTime lastPostCreationDate = new DateTime();
        topic.getLastPost().setCreationDate(lastPostCreationDate);
        topic.setLastReadPostDate(lastPostCreationDate);
        assertEquals(topic.getLastReadPostDate(), lastPostCreationDate);
        assertFalse(topic.isHasUpdates());
    }

    @Test
    public void removePostShouldRemovePostFromTheTopic() {
        Topic topic = createTopic();
        DateTime lastModification = new DateTime(1900, 11, 11, 11, 11, 11, 11);
        topic.setModificationDate(lastModification);
        Post toRemove = topic.getPosts().get(0);

        topic.removePost(toRemove);

        assertFalse(topic.getPosts().contains(toRemove), "The post isn't removed from the topic");
    }

    @Test
    public void removePostShouldSetModificationDateToLastPostInTheTopic(){
        Post post1 = new Post();
        post1.setCreationDate(new DateTime());
        Post post2 = new Post();
        post2.setCreationDate(new DateTime());
        Topic topic = new Topic(new JCUser(), "title");
        topic.addPost(post1);
        topic.addPost(post2);
        Post post3 = new Post();
        post3.setCreationDate(new DateTime());
        topic.addPost(post3);
        topic.removePost(post3);

        assertEquals(topic.getModificationDate(), post2.getCreationDate());

    }

    @Test
    public void setSubscribersShouldSubscribeUserToTheTopic() {
        Topic topic = createTopic();
        JCUser subscribedUser = new JCUser();
        JCUser notSubscribedUser = new JCUser();
        Set<JCUser> subscribers = new HashSet<>();
        subscribers.add(subscribedUser);
        topic.setSubscribers(subscribers);
        assertTrue(topic.userSubscribed(subscribedUser));
        assertFalse(topic.userSubscribed(notSubscribedUser));
    }

    @Test
    public void addOrOverrideAttributeShouldAddAttributes() {
        Topic topic = createTopic();
        String name = "name";
        String value = "value";

        topic.addOrOverrideAttribute(name, value);

        assertEquals(topic.getAttributes().get(name), value);
    }

    @Test
    public void addOrOverrideAttributeShouldOverrideExistentAttributes() {
        Topic topic = createTopic();
        String name = "name";
        String newValue = "newValue";
        topic.addOrOverrideAttribute(name, "value");

        topic.addOrOverrideAttribute(name, newValue);

        assertEquals(topic.getAttributes().get(name), newValue);
    }

    private Post[] postList(int numberOfPosts, Topic topic) {
        Post[] posts = new Post[numberOfPosts];
        for (int i = 0; i < posts.length; i++) {
            posts[i] = new Post();
            topic.addPost(posts[i]);
        }
        return posts;
    }

    @Test
    public void isCodeReviewShouldReturnTrueIfTopicTypeIsCodeReview() {
        Topic topic = createTopic();
        topic.setType(TopicTypeName.CODE_REVIEW.getName());

        assertTrue(topic.isCodeReview());
    }

    @Test
    public void isCodeReviewShouldReturnFalseIfTopicTypeIsNotCodeReview() {
        Topic topic = createTopic();
        topic.setType(TopicTypeName.DISCUSSION.getName());

        assertFalse(topic.isCodeReview());
    }

    @Test
    public void isCodeReviewShouldReturnFalseIfTopicTypeNotSet() {
        Topic topic = createTopic();
        topic.setType(null);

        assertFalse(topic.isCodeReview());
    }

    @Test
    public void isPlugableShouldReturnFalseForDiscussion() {
        Topic topic = createTopic();
        topic.setType(TopicTypeName.DISCUSSION.getName());

        assertFalse(topic.isPlugable());
    }

    @Test
    public void isPlugableShouldReturnFalseForCodeReview() {
        Topic topic = createTopic();
        topic.setType(TopicTypeName.CODE_REVIEW.getName());

        assertFalse(topic.isPlugable());
    }

    @Test
    public void isPlugableShouldReturnTrueForUnknownTopicType() {
        Topic topic = createTopic();
        topic.setType("Any plugable");

        assertTrue(topic.isPlugable());
    }

    @Test
    public void isPlugableShouldReturnFalseIfTopicTypeNotSet() {
        Topic topic = createTopic();
        topic.setType(null);

        assertFalse(topic.isPlugable());
    }

    @Test
    public void getDisplayedPostsShouldReturnAllPostsWithDisplayedState() {
        Topic topic = new Topic();
        Post post1 = new Post();
        Post post2 = new Post();
        Post post3 = new Post();
        post3.setState(PostState.DRAFT);
        topic.setPosts(Arrays.asList(post1, post2, post3));

        List<Post> posts = topic.getDisplayedPosts();

        assertEquals(posts.size(), 2);
        assertTrue(posts.contains(post1));
        assertTrue(posts.contains(post2));
        assertFalse(posts.contains(post3));
    }

    @Test
    public void getDisplayedPostsShouldReturnEmptyListIfNoDisplayedPostsFound() {
        Topic topic = new Topic();
        topic.addPost(new Post(new JCUser(), "content", PostState.DRAFT));

        List<Post> result = topic.getDisplayedPosts();

        assertTrue(result.isEmpty());
    }

    @Test
    public void getDraftForUserShouldReturnDraftOfSpecifiedUser() {
        JCUser user = new JCUser();
        Topic topic = new Topic();
        Post targetDraft = new Post(user, "content", PostState.DRAFT);
        topic.addPost(targetDraft);
        topic.addPost(new Post(new JCUser(), "text"));

        assertEquals(topic.getDraftForUser(user), targetDraft);
    }

    @Test
    public void getDraftForUserShouldReturnNullIfDraftOfSpecifiedUserNotFound() {
        Topic topic = new Topic();
        topic.addPost(new Post(new JCUser(), "text"));

        assertNull(topic.getDraftForUser(new JCUser()));
    }

    @Test
    public void testGetLastDisplayedPost() {
        Topic topic = createTopic();
        topic.getPosts().get(1).setState(PostState.DRAFT);

        assertEquals(topic.getLastDisplayedPost(), topic.getPosts().get(0));
    }

	@Test
	public void testIsContainsOwnerPostsOnlyReturnsTrueIfOthersDidNotRespond() {
		Topic topic = createTopic();

		assertTrue(topic.getPosts().size() > 0);
		assertTrue(topic.isContainsOwnerPostsOnly());
	}

	@Test
	public void testIsContainsOwnerPostsOnlyReturnsFalseIfOthersHaveResponded() {
		Topic topic = createTopicWithOthersPosts();

		assertTrue(topic.getPosts().size() > 0);
		assertFalse(topic.isContainsOwnerPostsOnly());
	}

	@Test
	public void testIsContainsOwnerPostsOnlyReturnsTrueOnEmptyTopic() {
		Topic topic = createTopicWithOthersPosts();
		topic.setPosts(Collections.<Post>emptyList());

		assertTrue(topic.getPosts().size() == 0);
		assertTrue(topic.isContainsOwnerPostsOnly());
	}

    private Topic createTopic() {
		JCUser topicStarter = new JCUser();

        Post post1 = new Post(topicStarter, "Post N1 for tests");
        post1.setCreationDate(new DateTime());
		Post post2 = new Post(topicStarter, "Post N2 for tests");
		post2.setCreationDate(new DateTime());

        Topic topic = new Topic(topicStarter, "Topic title for testing");
        topic.addPost(post1);
        topic.addPost(post2);

        return topic;
    }

	private Topic createTopicWithOthersPosts() {
		JCUser topicStarter = new JCUser();
		JCUser otherUser = new JCUser();

		Post post1 = new Post(topicStarter, "Post N1 by topic starter");
		post1.setCreationDate(new DateTime());
		Post post2 = new Post(otherUser, "Post N2 by other user");
		post2.setCreationDate(new DateTime());

		Topic topic = new Topic(topicStarter, "Topic title for testing");
		topic.addPost(post1);
		topic.addPost(post2);

		return topic;
	}
}
