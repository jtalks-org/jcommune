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
import org.jtalks.jcommune.plugin.api.PluginLoader;
import org.jtalks.jcommune.service.SubscriptionService;
import org.jtalks.jcommune.service.UserService;
import org.jtalks.jcommune.service.exceptions.MailingFailedException;
import org.mockito.Mock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.HashSet;

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
    @Mock
    private PluginLoader pluginLoader;
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
                subscriptionService,
                pluginLoader);
        topic = new Topic(user1, "title");
        topic.setId(TOPIC_ID);
        branch = new Branch("name", "description");
        branch.addTopic(topic);
        
        when(userService.getCurrentUser()).thenReturn(currentUser);
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
        branch.setSubscribers(new HashSet<JCUser>());
        branch.getSubscribers().add(user2);
        branch.getSubscribers().add(user3);

        when(subscriptionService.getAllowedSubscribers(branch)).thenReturn(branch.getSubscribers());

        service.sendNotificationAboutTopicMoved(topic);

        verify(mailService).sendTopicMovedMail(user2, topic, currentUser.getUsername(), Branch.class);
        verify(mailService).sendTopicMovedMail(user3, topic, currentUser.getUsername(), Branch.class);
    }

    @Test
    public void testTopicMovedCurrentUserIsBranchSubscriber() {
        branch.setSubscribers(new HashSet<JCUser>());
        branch.getSubscribers().add(currentUser);
        branch.getSubscribers().add(user2);

        when(subscriptionService.getAllowedSubscribers(branch)).thenReturn(branch.getSubscribers());

        service.sendNotificationAboutTopicMoved(topic);

        verify(mailService, never()).sendTopicMovedMail(currentUser, topic, currentUser.getUsername(), Branch.class);
        verify(mailService).sendTopicMovedMail(user2, topic, currentUser.getUsername(), Branch.class);
    }

    @Test
    public void testTopicMovedWithTopicSubscribers() {
        topic.setSubscribers(new HashSet<JCUser>());
        topic.getSubscribers().add(user1);
        topic.getSubscribers().add(user2);

        when(subscriptionService.getAllowedSubscribers(topic)).thenReturn(topic.getSubscribers());

        service.sendNotificationAboutTopicMoved(topic);

        verify(mailService).sendTopicMovedMail(user1, topic, currentUser.getUsername(), Topic.class);
        verify(mailService).sendTopicMovedMail(user2, topic, currentUser.getUsername(), Topic.class);
    }

    @Test
    public void testTopicMovedCurrentUserIsTopicSubscriber() {
        topic.setSubscribers(new HashSet<JCUser>());
        topic.getSubscribers().add(user1);
        topic.getSubscribers().add(currentUser);

        when(subscriptionService.getAllowedSubscribers(topic)).thenReturn(topic.getSubscribers());

        service.sendNotificationAboutTopicMoved(topic);

        verify(mailService, never()).sendTopicMovedMail(currentUser, topic, currentUser.getUsername(), Topic.class);
        verify(mailService).sendTopicMovedMail(user1, topic, currentUser.getUsername(), Topic.class);
    }

    @Test
    public void testTopicMovedUserSubscribedToBranchAndTopic() {
        branch.setSubscribers(new HashSet<JCUser>());
        branch.getSubscribers().add(user1);
        topic.getSubscribers().add(user1);
        branch.getSubscribers().add(user2);

        when(subscriptionService.getAllowedSubscribers(branch)).thenReturn(branch.getSubscribers());
        when(subscriptionService.getAllowedSubscribers(topic)).thenReturn(topic.getSubscribers());

        service.sendNotificationAboutTopicMoved(topic);

        verify(mailService).sendTopicMovedMail(user1, topic, currentUser.getUsername(), Topic.class);
        verify(mailService, never()).sendTopicMovedMail(user1, topic, currentUser.getUsername(), Branch.class);
        verify(mailService, never()).sendTopicMovedMail(user2, topic, currentUser.getUsername(), Topic.class);
        verify(mailService).sendTopicMovedMail(user2, topic, currentUser.getUsername(), Branch.class);

    }

    @Test
    public void testTopicMovedNoSubscribers() {
        service.sendNotificationAboutTopicMoved(topic);

        verifyZeroInteractions(mailService);
    }


    @Test
    public void testTopicRemovedTopicSubscribers() {
        topic.setSubscribers(new HashSet<JCUser>());
        topic.getSubscribers().add(user1);
        topic.getSubscribers().add(user2);

        when(subscriptionService.getAllowedSubscribers(topic)).thenReturn(topic.getSubscribers());

        service.sendNotificationAboutRemovingTopic(topic);

        verify(mailService).sendRemovingTopicMail(user1, topic, currentUser.getUsername());
        verify(mailService).sendRemovingTopicMail(user2, topic, currentUser.getUsername());
    }

    @Test
    public void testTopicRemovedCurrentUserIsTopicSubscriber() {
        topic.setSubscribers(new HashSet<JCUser>());
        topic.getSubscribers().add(currentUser);
        topic.getSubscribers().add(user1);

        when(subscriptionService.getAllowedSubscribers(topic)).thenReturn(topic.getSubscribers());

        service.sendNotificationAboutRemovingTopic(topic);

        verify(mailService, never()).sendRemovingTopicMail(currentUser, topic, currentUser.getUsername());
        verify(mailService).sendRemovingTopicMail(user1, topic, currentUser.getUsername());
    }

    @Test
    public void testTopicRemovedBranchSubscribers() {
        branch.setSubscribers(new HashSet<JCUser>());
        branch.getSubscribers().add(user1);
        branch.getSubscribers().add(user2);

        when(subscriptionService.getAllowedSubscribers(branch)).thenReturn(branch.getSubscribers());

        service.sendNotificationAboutRemovingTopic(topic);

        verify(mailService).sendUpdatesOnSubscription(user1, branch);
        verify(mailService).sendUpdatesOnSubscription(user2, branch);
    }

    @Test
    public void testTopicRemovedCurrentUserIsBranchSubscriber() {
        branch.setSubscribers(new HashSet<JCUser>());
        branch.getSubscribers().add(currentUser);
        branch.getSubscribers().add(user1);

        when(subscriptionService.getAllowedSubscribers(branch)).thenReturn(branch.getSubscribers());

        service.sendNotificationAboutRemovingTopic(topic);

        verify(mailService, never()).sendUpdatesOnSubscription(currentUser, branch);
        verify(mailService).sendUpdatesOnSubscription(user1, branch);

    }

    @Test
    public void testTopicRemovedUserSubscribedToBranchAndTopic() {
        branch.setSubscribers(new HashSet<JCUser>());
        branch.getSubscribers().add(user1);
        branch.getSubscribers().add(user2);
        topic.getSubscribers().add(user2);
        topic.getSubscribers().add(user3);

        when(subscriptionService.getAllowedSubscribers(branch)).thenReturn(branch.getSubscribers());
        when(subscriptionService.getAllowedSubscribers(topic)).thenReturn(topic.getSubscribers());

        service.sendNotificationAboutRemovingTopic(topic);

        verify(mailService).sendUpdatesOnSubscription(user1, branch);
        verify(mailService, never()).sendRemovingTopicMail(user1, topic, currentUser.getUsername());
        verify(mailService, never()).sendRemovingTopicMail(user2, topic, currentUser.getUsername());
        verify(mailService).sendUpdatesOnSubscription(user2, branch);
        verify(mailService).sendRemovingTopicMail(user3, topic, currentUser.getUsername());
        verify(mailService, never()).sendUpdatesOnSubscription(user3, branch);
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
