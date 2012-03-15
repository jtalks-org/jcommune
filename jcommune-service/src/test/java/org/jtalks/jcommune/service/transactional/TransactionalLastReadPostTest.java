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
import org.jtalks.jcommune.model.entity.Branch;
import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.model.entity.LastReadPost;
import org.jtalks.jcommune.model.entity.Post;
import org.jtalks.jcommune.model.entity.Topic;
import org.jtalks.jcommune.service.nontransactional.SecurityService;
import org.mockito.ArgumentMatcher;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * @author Evgeniy Naumenko
 */
public class TransactionalLastReadPostTest {

    private String BRANCH_NAME = "branch name";
    private JCUser user = new JCUser("username", "email@mail.com", "password");

    private TransactionalLastReadPostService lastReadPostService;

    @Mock
    private PostDao postDao;
    @Mock
    SecurityService securityService;

    @BeforeMethod
    public void setUp() throws Exception {
        initMocks(this);
        lastReadPostService = new TransactionalLastReadPostService(securityService, postDao);
    }

        @Test
    public void testFillLastReadPostsForTopics() {
        Topic topic = new Topic(user, "title");
        LastReadPost post = new LastReadPost(user, topic, 0);
        when(securityService.getCurrentUser()).thenReturn(user);
        when(postDao.getLastReadPost(user, topic)).thenReturn(post);

        List<Topic> result
                = lastReadPostService.fillLastReadPostForTopics(Collections.singletonList(topic));

        assertEquals(1, result.size());
        assertFalse(result.get(0).isHasUpdates());
    }

    @Test
    public void testFillLastReadPostsForTopicsAnonymous() {
        lastReadPostService.fillLastReadPostForTopics(new ArrayList<Topic>());

        verify(postDao, never()).getLastReadPost(Matchers.<JCUser>any(), Matchers.<Topic>any());
    }

    @Test
    public void testFillLastReadPostsForTopicsNoLastReadPostRecordExists() {
        Topic topic = new Topic(user, "title");
        when(securityService.getCurrentUser()).thenReturn(user);

        List<Topic> result
                = lastReadPostService.fillLastReadPostForTopics(Collections.singletonList(topic));

        assertEquals(1, result.size());
        assertTrue(result.get(0).isHasUpdates());
    }

    @Test
    public void testMarkTopicPageAsReadAnonymous() {
        Topic topic = this.createTestTopic();

        lastReadPostService.markTopicPageAsRead(topic, 1, true);
        verifyZeroInteractions(postDao);
    }

    @Test
    public void testMarkTopicPageAsReadLoggedIn() {
        final Topic topic = this.createTestTopic();
        when(securityService.getCurrentUser()).thenReturn(user);

        lastReadPostService.markTopicPageAsRead(topic, 1, false);

        verify(postDao).saveLastReadPost(argThat(
                new LastReadPostMatcher(topic, topic.getPostCount() - 1)));
    }

    @Test
    public void testMarkTopicPageAsReadPagingEnabled() {
        final Topic topic = this.createTestTopic();
        user.setPageSize(3);
        when(securityService.getCurrentUser()).thenReturn(user);

        lastReadPostService.markTopicPageAsRead(topic, 2, true);

        verify(postDao).saveLastReadPost(argThat(
                new LastReadPostMatcher(topic, 5)));
    }

    @Test
    public void testMarkTopicAsReadUpdateExistingDbRecord() {
        final Topic topic = this.createTestTopic();
        LastReadPost post = new LastReadPost(user, topic, 0);
        when(securityService.getCurrentUser()).thenReturn(user);
        when(postDao.getLastReadPost(user, topic)).thenReturn(post);

        lastReadPostService.markTopicPageAsRead(topic, 1, false);

        verify(postDao).saveLastReadPost(argThat(
                new LastReadPostMatcher(topic, topic.getPostCount() - 1)));
    }

    @Test
    public void testMarkAllTopicReadAnonymous() {
        Branch branch = new Branch(BRANCH_NAME);

        lastReadPostService.markAllTopicsAsRead(branch);

        verifyZeroInteractions(postDao);
    }

    @Test
    public void testMarkAllTopicRead() {
        Branch branch = new Branch(BRANCH_NAME);
        Topic topic = this.createTestTopic();
        branch.addTopic(topic);
        when(securityService.getCurrentUser()).thenReturn(user);

        lastReadPostService.markAllTopicsAsRead(branch);

        verify(postDao).saveLastReadPost(argThat(
                new LastReadPostMatcher(topic, topic.getPostCount() - 1)));
    }

    @Test
    public void testMarkTopicAsRead() {
        final Topic topic = this.createTestTopic();
        when(securityService.getCurrentUser()).thenReturn(user);

        lastReadPostService.markTopicAsRead(topic);

        verify(postDao).saveLastReadPost(argThat(
                new LastReadPostMatcher(topic, topic.getPostCount() - 1)));
    }

    @Test
    public void testMarkTopicAsReadAnonymous() {
        Topic topic = this.createTestTopic();

       lastReadPostService.markTopicAsRead(topic);
        verifyZeroInteractions(postDao);
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
        private int index;

        private LastReadPostMatcher(Topic topic, int index) {
            this.topic = topic;
            this.index = index;
        }

        @Override
        public boolean matches(Object argument) {
            LastReadPost post = (LastReadPost) argument;
            boolean result = post.getTopic().equals(topic);
            result &= post.getUser().equals(user);
            result &= (post.getPostIndex() == index);
            return result;
        }
    }
}
