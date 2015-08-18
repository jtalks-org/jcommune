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

import org.joda.time.DateTime;
import org.jtalks.jcommune.model.dao.LastReadPostDao;
import org.jtalks.jcommune.model.dao.BranchReadedMarkerDao;
import org.jtalks.jcommune.model.dao.UserDao;
import org.jtalks.jcommune.model.entity.*;
import org.jtalks.jcommune.service.UserService;
import org.mockito.ArgumentMatcher;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.testng.Assert.*;

/**
 * @author Evgeniy Naumenko
 * @author Anuar_Nurmakanov
 */
public class TransactionalLastReadPostServiceTest {
    private JCUser user;

    @Mock
    private LastReadPostDao lastReadPostDao;
    @Mock
    private UserService userService;
    @Mock
    private UserDao userDao;
    @Mock
    private BranchReadedMarkerDao branchReadedMarkerDao;
    //
    private TransactionalLastReadPostService lastReadPostService;


    @BeforeMethod
    public void setUp() throws Exception {
        initMocks(this);
        user = new JCUser("username", "email@mail.com", "password");
        lastReadPostService = new TransactionalLastReadPostService(
                userService,
                lastReadPostDao,
                userDao,
                branchReadedMarkerDao);
    }

    @Test
    public void userShouldNotSeeUpdatesWhenForumMarkedAsAllReadAndTopicsDoNotHaveModificationsAfter() {
        List<Topic> topicList = ObjectsFactory.topics(user, 1);
        when(lastReadPostDao.getLastReadPosts(user, Collections.<Topic>emptyList()))
                .thenReturn(Collections.<LastReadPost>emptyList());
        DateTime forumMarkedAsReadDate = new DateTime().plusYears(1);
        user.setAllForumMarkedAsReadTime(forumMarkedAsReadDate);
        when(userService.getCurrentUser()).thenReturn(user);

        List<Topic> result = lastReadPostService.fillLastReadPostForTopics(topicList);

        assertEquals(1, result.size());
        assertFalse(result.get(0).isHasUpdates());
    }

    @Test
    public void userShouldSeeUpdatesWhenForumMarkedAsAllReadAndTopicsHaveModificationsAfter() {
        DateTime forumMarkedAsReadDate = new DateTime().minusYears(1);
        user.setAllForumMarkedAsReadTime(forumMarkedAsReadDate);
        when(userService.getCurrentUser()).thenReturn(user);
        List<Topic> topics = ObjectsFactory.topics(user, 1);
        when(lastReadPostDao.getLastReadPosts(user, Collections.<Topic>emptyList()))
                .thenReturn(Collections.<LastReadPost>emptyList());

        List<Topic> result = lastReadPostService.fillLastReadPostForTopics(topics);

        assertEquals(1, result.size());
        assertTrue(result.get(0).isHasUpdates());
    }

    @Test
    public void authenticatedUserShouldSeeReadTopicAsTopicWithoutUpdates() {
        List<Topic> topicList = ObjectsFactory.topics(user, 1);
        LastReadPost post = new LastReadPost(user, topicList.get(0), topicList.get(0).getFirstPost().getCreationDate());
        when(userService.getCurrentUser()).thenReturn(user);
        when(lastReadPostDao.getLastReadPosts(user, topicList)).thenReturn(Collections.singletonList(post));

        List<Topic> result = lastReadPostService.fillLastReadPostForTopics(topicList);
        assertEquals(1, result.size());
        assertFalse(result.get(0).isHasUpdates());
    }

    @Test
    public void anonymousUserShouldSeeAllTopicsAsNotRead() {
        when(userService.getCurrentUser()).thenReturn(new AnonymousUser());

        lastReadPostService.fillLastReadPostForTopics(new ArrayList<Topic>());
        verify(lastReadPostDao, never()).getLastReadPosts(Matchers.<JCUser>any(), Matchers.<List<Topic>>any());
    }

    @Test
    public void authenticatedUserShouldSeeNotReadTopicAsTopicWithUpdates() {
        List<Topic> topicList = ObjectsFactory.topics(user, 1);
        when(userService.getCurrentUser()).thenReturn(user);
        when(lastReadPostDao.getLastReadPosts(user, topicList)).thenReturn(Collections.<LastReadPost>emptyList());

        List<Topic> result = lastReadPostService.fillLastReadPostForTopics(topicList);
        assertEquals(1, result.size());
        assertTrue(result.get(0).isHasUpdates());
    }

    @Test
    public void anonymousUserShouldNotMarkTopicPageAsRead() {
        when(userService.getCurrentUser()).thenReturn(new AnonymousUser());

        Topic topic = this.createTestTopic();

        lastReadPostService.markTopicPageAsRead(topic, 1);
        verifyZeroInteractions(lastReadPostDao);
    }

    @Test
    public void updateLastReadPostToAuthUserWhenAllForumMarkedBefore() {
        Topic topic = this.createTestTopic();
        user.setAllForumMarkedAsReadTime(topic.getModificationDate().minusMinutes(2));
        LastReadPost post = new LastReadPost(user, topic, topic.getLastPost().getCreationDate().minusMinutes(1));
        when(userService.getCurrentUser()).thenReturn(user);
        when(lastReadPostDao.getLastReadPost(user, topic)).thenReturn(post);

        lastReadPostService.markTopicPageAsRead(topic, 1);

        verify(lastReadPostDao).saveOrUpdate(post);
    }

    @Test
    public void updateLastReadPostToAuthUserWhenAllForumMarkedNull() {
        Topic topic = this.createTestTopic();
        user.setAllForumMarkedAsReadTime(null);
        LastReadPost post = new LastReadPost(user, topic, topic.getLastPost().getCreationDate().minusMinutes(1));
        when(userService.getCurrentUser()).thenReturn(user);
        when(lastReadPostDao.getLastReadPost(user, topic)).thenReturn(post);

        lastReadPostService.markTopicPageAsRead(topic, 1);

        verify(lastReadPostDao).saveOrUpdate(post);
    }

    @Test
    public void notUpdateLastReadPostToAuthUserWhenAllForumAfter() {
        Topic topic = this.createTestTopic();
        user.setAllForumMarkedAsReadTime(topic.getModificationDate().plusSeconds(1));
        LastReadPost post = new LastReadPost(user, topic, new DateTime());
        when(userService.getCurrentUser()).thenReturn(user);
        when(lastReadPostDao.getLastReadPost(user, topic)).thenReturn(post);

        lastReadPostService.markTopicPageAsRead(topic, 1);

        verify(lastReadPostDao, never()).saveOrUpdate(post);
    }

    @Test
    public void testMarkTopicPageAsRead() {
        Topic topic = this.createTestTopic();
        user.setPageSize(3);
        when(userService.getCurrentUser()).thenReturn(user);

        lastReadPostService.markTopicPageAsRead(topic, 2);

        verify(lastReadPostDao).saveOrUpdate(argThat(
                new LastReadPostMatcher(topic, topic.getPosts().get(5).getCreationDate())));
    }

    @Test
    public void markTopicPageAsReadShouldReupdateLastReadPostInRepositoryWhenLastPostOnPageDateIsNewer() {
        Topic topic = this.createTestTopic();
        LastReadPost post = new LastReadPost(user, topic, topic.getLastPost().getCreationDate().minusMinutes(1));
        when(userService.getCurrentUser()).thenReturn(user);
        when(lastReadPostDao.getLastReadPost(user, topic)).thenReturn(post);

        lastReadPostService.markTopicPageAsRead(topic, 1);

        verify(lastReadPostDao).saveOrUpdate(argThat(
                new LastReadPostMatcher(topic, topic.getLastPost().getCreationDate())));
    }

    @Test
    public void markTopicPageAsReadShouldNotReupdateLastReadPostInRepositoryLastPostOnPageDateIsNotNewer() {
        Topic topic = this.createTestTopic();
        LastReadPost post = new LastReadPost(user, topic, topic.getLastPost().getCreationDate());
        when(userService.getCurrentUser()).thenReturn(user);
        when(lastReadPostDao.getLastReadPost(user, topic)).thenReturn(post);

        lastReadPostService.markTopicPageAsRead(topic, 1);

        verify(lastReadPostDao, never()).saveOrUpdate(argThat(
                new LastReadPostMatcher(topic, topic.getLastPost().getCreationDate())));
    }

    @Test
    public void anonymousUserShouldNotMarkAllTopicsInBranchAsRead() {
        when(userService.getCurrentUser()).thenReturn(new AnonymousUser());
        Branch branch = new Branch("branch name", "branch description");

        lastReadPostService.markAllTopicsAsRead(branch);
        verifyZeroInteractions(lastReadPostDao);
    }

    @Test
    public void authenticatedUserShouldHaveAbilityToMarkAllTopicsInBranchAsRead() throws Exception {
        Branch branch = new Branch("branch name", "branch description");
        when(userService.getCurrentUser()).thenReturn(user);

        lastReadPostService.markAllTopicsAsRead(branch);

        verify(userService).getCurrentUser();
        verify(branchReadedMarkerDao).markBranchAsRead(user, branch);
    }

    @Test
    public void authenticatedUserShouldHaveAbilityToMarkTopicAsRead() {
        Topic topic = this.createTestTopic();
        when(userService.getCurrentUser()).thenReturn(user);

        lastReadPostService.markTopicAsRead(topic);
        verify(lastReadPostDao).saveOrUpdate(argThat(new LastReadPostMatcher(topic, topic.getLastPost().getCreationDate())));
    }

    @Test
    public void anonymousUserShouldNotHaveAbilityToMarkTopicAsRead() {
        when(userService.getCurrentUser()).thenReturn(new AnonymousUser());

        Topic topic = this.createTestTopic();

        lastReadPostService.markTopicAsRead(topic);
        verifyZeroInteractions(lastReadPostDao);
    }

    @Test
    public void markTopicAsReadShouldReupdateLastReadPostInRepositoryIfLastReadDateIsNewer() {
        Topic topic = this.createTestTopic();
        LastReadPost post = new LastReadPost(user, topic, new DateTime(1L));
        when(userService.getCurrentUser()).thenReturn(user);
        when(lastReadPostDao.getLastReadPost(user, topic)).thenReturn(post);

        lastReadPostService.markTopicAsRead(topic);

        verify(lastReadPostDao).saveOrUpdate(argThat(
                new LastReadPostMatcher(topic, topic.getLastPost().getCreationDate())));
    }

    @Test
    public void markTopicAsReadShouldNotReupdateLastReadPostInRepositoryIfLastReadDateIsNotNew() {
        Topic topic = this.createTestTopic();
        LastReadPost post = new LastReadPost(user, topic, topic.getLastPost().getCreationDate());
        when(userService.getCurrentUser()).thenReturn(user);
        when(lastReadPostDao.getLastReadPost(user, topic)).thenReturn(post);

        lastReadPostService.markTopicAsRead(topic);

        verify(lastReadPostDao, never()).saveOrUpdate(argThat(
                new LastReadPostMatcher(topic, topic.getLastPost().getCreationDate())));
    }

    @Test
    public void markAllForumAsReadShouldRememberMarkDateAndClearLastReadPostsForUser() {
        JCUser user = new JCUser("user", "use@gmail.com", "gangam-style-password");
        when(userService.getCurrentUser()).thenReturn(user);

        lastReadPostService.markAllForumAsReadForCurrentUser();

        assertNotNull(user.getAllForumMarkedAsReadTime(), "Mark date should be remembered for user.");
        verify(userDao).saveOrUpdate(user);
        verify(lastReadPostDao).deleteLastReadPostsFor(user);

    }

    private Topic createTestTopic() {
        Topic topic = new Topic(user, "title");
        for (int i = 0; i < 10; i++) {
            topic.addPost(new Post(user, "content"));
        }
        return topic;
    }

    private class LastReadPostMatcher extends ArgumentMatcher<LastReadPost> {

        private Topic topic;
        private DateTime dateTime;

        private LastReadPostMatcher(Topic topic, DateTime dateTime) {
            this.topic = topic;
            this.dateTime = dateTime;
        }

        @Override
        public boolean matches(Object argument) {
            LastReadPost post = (LastReadPost) argument;
            boolean result = post.getTopic().equals(topic);
            result &= post.getUser().equals(user);
            result &= (post.getPostCreationDate() == dateTime);
            return result;
        }
    }
}
