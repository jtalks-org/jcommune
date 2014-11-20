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
package org.jtalks.jcommune.service.nontransactional;


import org.jtalks.jcommune.model.entity.Branch;
import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.model.entity.Topic;
import org.jtalks.jcommune.service.SubscriptionService;
import org.jtalks.jcommune.service.UserService;
import org.jtalks.jcommune.service.exceptions.MailingFailedException;
import org.mockito.Mock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Collection;

import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.testng.Assert.assertEquals;

/**
 * @author Evgeniy Naumenko
 */
public class NotificationServiceTest {
    @Mock
    private MailService mailService;
    @Mock
    private UserService userService;
    @Mock
    private SubscriptionService subscriptionService;
    private NotificationService service;
    private final long TOPIC_ID = 1;

    private JCUser user1 = new JCUser("name1", "email1", "password1");
    private JCUser user2 = new JCUser("name2", "email2", "password2");
    private JCUser user3 = new JCUser("name3", "email3", "password3");
    private JCUser currentUser = new JCUser("current", "email4", "password4");
    
    private Topic topic;
    private Branch branch;

    @BeforeMethod
    public void setUp() {
        initMocks(this);
        service = new NotificationService(
                userService,
                mailService,
                subscriptionService);
        topic = new Topic(user1, "title");
        topic.setId(TOPIC_ID);
        branch = new Branch("name", "description");
        branch.addTopic(topic);
//        codeReview = new CodeReview();
//        topic.setCodeReview(codeReview);
//        codeReview.setTopic(topic);
        
        when(userService.getCurrentUser()).thenReturn(currentUser);
    }

    @Test
    public void testSubscribedEntityChangedCodeReviewCase() throws MailingFailedException {
        topic.getSubscribers().add(user1);
        topic.getSubscribers().add(user2);
        topic.getSubscribers().add(currentUser);
//        when(subscriptionService.getAllowedSubscribers(codeReview)).thenReturn(codeReview.getSubscribers());
//
//        service.subscribedEntityChanged(codeReview);
//
//        verify(mailService, times(2)).sendUpdatesOnSubscription(any(JCUser.class), eq(codeReview));
//        verify(mailService).sendUpdatesOnSubscription(user1, codeReview);
//        verify(mailService).sendUpdatesOnSubscription(user2, codeReview);
//        assertEquals(topic.getSubscribers().size(), 2);
    }

    @Test
    public void testSubscribedEntityChangedCodeReviewCaseNoSubscribers() {
//        service.subscribedEntityChanged(codeReview);
        verifyZeroInteractions(mailService);
    }


    @Test
    public void testTopicChanged() throws MailingFailedException {
        topic.getSubscribers().add(user1);
        topic.getSubscribers().add(user2);
        topic.getSubscribers().add(currentUser);
        when(subscriptionService.getAllowedSubscribers(topic)).thenReturn(topic.getSubscribers());

        service.subscribedEntityChanged(topic);

        verify(mailService, times(2)).sendUpdatesOnSubscription(any(JCUser.class), eq(topic));
        verify(mailService).sendUpdatesOnSubscription(user1, topic);
        verify(mailService).sendUpdatesOnSubscription(user2, topic);
        assertEquals(topic.getSubscribers().size(), 2);
    }

    @Test
    public void testBranchChanged() throws MailingFailedException {
        branch.getSubscribers().add(user1);
        branch.getSubscribers().add(user2);
        branch.getSubscribers().add(currentUser);
        when(subscriptionService.getAllowedSubscribers(branch)).thenReturn(branch.getSubscribers());

        service.subscribedEntityChanged(branch);

        verify(mailService, times(2)).sendUpdatesOnSubscription(
                any(JCUser.class), eq(branch));
        verify(mailService).sendUpdatesOnSubscription(user1, branch);
        verify(mailService).sendUpdatesOnSubscription(user2, branch);
        assertEquals(branch.getSubscribers().size(), 2);
    }

    @Test
    public void testTopicChangedSelfSubscribed() throws MailingFailedException {
        when(userService.getCurrentUser()).thenReturn(user1);
        topic.getSubscribers().add(user1);
        topic.getSubscribers().add(user2);
        when(subscriptionService.getAllowedSubscribers(topic)).thenReturn(topic.getSubscribers());

        service.subscribedEntityChanged(topic);

        verify(mailService).sendUpdatesOnSubscription(user2, topic);
        verifyNoMoreInteractions(mailService);
    }

    @Test
    public void testBranchChangedSelfSubscribed() throws MailingFailedException {
        when(userService.getCurrentUser()).thenReturn(user1);
        branch.getSubscribers().add(user1);
        branch.getSubscribers().add(user2);
        when(subscriptionService.getAllowedSubscribers(branch)).thenReturn(branch.getSubscribers());

        service.subscribedEntityChanged(branch);

        verify(mailService).sendUpdatesOnSubscription(user2, branch);
        verifyNoMoreInteractions(mailService);
    }

    @Test
    public void testTopicChangedNoSubscribers() {
        service.subscribedEntityChanged(topic);

        verifyZeroInteractions(mailService);
    }

    @Test
    public void testBranchChangedNoSubscribers() {
        service.subscribedEntityChanged(branch);

        verifyZeroInteractions(mailService);
    }

    @Test
    public void testTopicMovedWithBranchSubscribers() {
        branch.getSubscribers().add(currentUser);
        branch.getSubscribers().add(user2);
        branch.getSubscribers().add(user3);

        service.sendNotificationAboutTopicMoved(topic);

        verify(mailService).sendTopicMovedMail(user2, topic, "current");
        verify(mailService).sendTopicMovedMail(user3, topic, "current");
    }

    @Test
    public void testTopicMovedTopicStarterIsNotASubscriber() {
        branch.getSubscribers().add(currentUser);
        branch.getSubscribers().add(user2);
        branch.getSubscribers().add(user3);

        service.sendNotificationAboutTopicMoved(topic);

        verify(mailService, times(2)).sendTopicMovedMail(any(JCUser.class), eq(topic), eq("current"));
        verify(mailService).sendTopicMovedMail(user2, topic, "current");
        verify(mailService).sendTopicMovedMail(user3, topic, "current");
        assertEquals(branch.getSubscribers().size(), 3);
    }

    @Test
    public void testTopicMovedWhenUserIsSubscribedForBranchAndTopic() {
        branch.getSubscribers().add(user2);
        branch.getSubscribers().add(user3);

        service.sendNotificationAboutTopicMoved(topic);

        Collection<JCUser> topicSubscribers = new ArrayList();
        topicSubscribers.add(user2);

        when(subscriptionService.getAllowedSubscribers(topic)).thenReturn(topicSubscribers);

        verify(mailService, times(1)).sendTopicMovedMail(user3, topic, "current");
    }

    @Test
    public void testSendNotificationAboutRemovingTopic() {
        Collection<JCUser> subscribers = new ArrayList();
        subscribers.add(user1);
        subscribers.add(user2);
        service.sendNotificationAboutRemovingTopic(topic, subscribers);
        verify(mailService).sendRemovingTopicMail(user2, topic, "current");
    }

    @Test
    public void testTopicChangedWithFilterByTopicSubscribers() throws MailingFailedException {
        topic.getSubscribers().add(user1);
        topic.getSubscribers().add(user2);
        topic.getSubscribers().add(currentUser);
        when(subscriptionService.getAllowedSubscribers(topic)).thenReturn(topic.getSubscribers());

        Collection<JCUser> topicSubscribers = new ArrayList();
        topicSubscribers.add(user2);

        service.subscribedEntityChanged(topic, topicSubscribers);

        verify(mailService, times(1)).sendUpdatesOnSubscription(any(JCUser.class), eq(topic));
        verify(mailService).sendUpdatesOnSubscription(user1, topic);
        assertEquals(topic.getSubscribers().size(), 2);
    }
    
    @Test
    public void notificationMailShouldBeSendAfterTopicWasCreated() {
        branch.getSubscribers().add(user1);
        branch.getSubscribers().add(currentUser);
        when(subscriptionService.getAllowedSubscribers(branch)).thenReturn(branch.getSubscribers());
        
        service.sendNotificationAboutTopicCreated(topic);
        
        verify(mailService, times(1)).sendTopicCreationMail(user1, topic);
        verifyNoMoreInteractions(mailService);
    }
}
