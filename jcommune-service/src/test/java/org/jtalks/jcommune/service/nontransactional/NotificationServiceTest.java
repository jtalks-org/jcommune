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


import org.jtalks.common.model.entity.Property;
import org.jtalks.jcommune.model.dao.PropertyDao;
import org.jtalks.jcommune.model.entity.*;
import org.jtalks.jcommune.service.SubscriptionService;
import org.jtalks.jcommune.service.UserService;
import org.jtalks.jcommune.service.exceptions.MailingFailedException;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Collection;

import static org.jtalks.jcommune.model.entity.JCommuneProperty
        .SENDING_NOTIFICATIONS_ENABLED;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.testng.Assert.assertEquals;

/**
 * @author Evgeniy Naumenko
 */
public class NotificationServiceTest {
    private static final String PROPERTY_NAME = "property";
    private static final String TRUE_STRING = Boolean.TRUE.toString();
    private static final String FALSE_STRING = Boolean.FALSE.toString();
    @Mock
    private MailService mailService;
    @Mock
    private UserService userService;
    @Mock
    private SubscriptionService subscriptionService;
    @Mock
    private PropertyDao propertyDao;
    private JCommuneProperty notificationsEnabledProperty = SENDING_NOTIFICATIONS_ENABLED;
    private NotificationService service;
    private final long TOPIC_ID = 1;

    private JCUser user1 = new JCUser("name1", "email1", "password1");
    private JCUser user2 = new JCUser("name2", "email2", "password2");
    private JCUser user3 = new JCUser("name3", "email3", "password3");
    private JCUser currentUser = new JCUser("current", "email4", "password4");
    
    private Topic topic;
    private Branch branch;
    private CodeReview codeReview;

    @BeforeMethod
    public void setUp() {
        initMocks(this);
        notificationsEnabledProperty.setPropertyDao(propertyDao);
        notificationsEnabledProperty.setName(PROPERTY_NAME);
        service = new NotificationService(
                userService,
                mailService,
                subscriptionService,
                notificationsEnabledProperty);
        topic = new Topic(user1, "title");
        branch = new Branch("name", "description");
        branch.addTopic(topic);
        codeReview = new CodeReview();
        topic.setCodeReview(codeReview);
        codeReview.setTopic(topic);
        
        when(userService.getCurrentUser()).thenReturn(currentUser);
    }

    @Test
    public void testSubscribedEntityChangedCodeReviewCase() throws MailingFailedException {
        prepareEnabledProperty();
        topic.getSubscribers().add(user1);
        topic.getSubscribers().add(user2);
        topic.getSubscribers().add(currentUser);
        when(subscriptionService.getAllowedSubscribers(codeReview)).thenReturn(codeReview.getSubscribers());

        service.subscribedEntityChanged(codeReview);

        verify(mailService, times(2)).sendUpdatesOnSubscription(any(JCUser.class), eq(codeReview));
        verify(mailService).sendUpdatesOnSubscription(user1, codeReview);
        verify(mailService).sendUpdatesOnSubscription(user2, codeReview);
        assertEquals(topic.getSubscribers().size(), 3);
    }

    @Test
    public void testSubscribedEntityChangedCodeReviewCaseWithDisabledNotifications() {
        prepareDisabledProperty();
        topic.getSubscribers().add(user1);

        service.subscribedEntityChanged(codeReview);

        verify(mailService, Mockito.never()).sendUpdatesOnSubscription(user1, codeReview);
    }

    @Test
    public void testSubscribedEntityChangedCodeReviewCaseNoSubscribers() {
        prepareEnabledProperty();
        service.subscribedEntityChanged(codeReview);
        verifyZeroInteractions(mailService);
    }


    @Test
    public void testTopicChanged() throws MailingFailedException {
        prepareEnabledProperty();
        topic.getSubscribers().add(user1);
        topic.getSubscribers().add(user2);
        topic.getSubscribers().add(currentUser);
        when(subscriptionService.getAllowedSubscribers(topic)).thenReturn(topic.getSubscribers());

        service.subscribedEntityChanged(topic);

        verify(mailService, times(2)).sendUpdatesOnSubscription(any(JCUser.class), eq(topic));
        verify(mailService).sendUpdatesOnSubscription(user1, topic);
        verify(mailService).sendUpdatesOnSubscription(user2, topic);
        assertEquals(topic.getSubscribers().size(), 3);
    }

    @Test
    public void testTopicChanedWithDisabledNotifcations() {
        prepareDisabledProperty();
        topic.getSubscribers().add(user1);

        service.subscribedEntityChanged(topic);

        verify(mailService, Mockito.never()).sendUpdatesOnSubscription(user1,
                topic);
    }

    @Test
    public void testBranchChanged() throws MailingFailedException {
        prepareEnabledProperty();
        branch.getSubscribers().add(user1);
        branch.getSubscribers().add(user2);
        branch.getSubscribers().add(currentUser);
        when(subscriptionService.getAllowedSubscribers(branch)).thenReturn(branch.getSubscribers());

        service.subscribedEntityChanged(branch);

        verify(mailService, times(2)).sendUpdatesOnSubscription(
                any(JCUser.class), eq(branch));
        verify(mailService).sendUpdatesOnSubscription(user1, branch);
        verify(mailService).sendUpdatesOnSubscription(user2, branch);
        assertEquals(branch.getSubscribers().size(), 3);
    }

    @Test
    public void testBranchChangedWithDisabledNotifications() {
        prepareDisabledProperty();
        branch.getSubscribers().add(user1);

        service.subscribedEntityChanged(branch);

        verify(mailService, Mockito.never()).sendUpdatesOnSubscription(user1,
                branch);
    }

    @Test
    public void testTopicChangedSelfSubscribed() throws MailingFailedException {
        prepareEnabledProperty();
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
        prepareEnabledProperty();
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
        prepareEnabledProperty();
        service.subscribedEntityChanged(topic);

        verifyZeroInteractions(mailService);
    }

    @Test
    public void testBranchChangedNoSubscribers() {
        prepareEnabledProperty();
        service.subscribedEntityChanged(branch);

        verifyZeroInteractions(mailService);
    }

    @Test
    public void testTopicMovedWithBranchSubscribers() {
        prepareEnabledProperty();
        branch.getSubscribers().add(currentUser);
        branch.getSubscribers().add(user2);
        branch.getSubscribers().add(user3);

        service.topicMoved(topic, TOPIC_ID);

        verify(mailService).sendTopicMovedMail(user2, TOPIC_ID);
        verify(mailService).sendTopicMovedMail(user3, TOPIC_ID);
    }

    @Test
    public void testTopicMovedTopicStarterIsNotASubscriber() {
        prepareEnabledProperty();
        branch.getSubscribers().add(currentUser);
        branch.getSubscribers().add(user2);
        branch.getSubscribers().add(user3);

        service.topicMoved(topic, TOPIC_ID);

        verify(mailService, times(3)).sendTopicMovedMail(any(JCUser.class), eq(TOPIC_ID));
        verify(mailService).sendTopicMovedMail(user2, TOPIC_ID);
        verify(mailService).sendTopicMovedMail(user3, TOPIC_ID);
        assertEquals(branch.getSubscribers().size(), 3);
    }

    @Test
    public void testTopicMovedWithDisabledNotifications() {
        prepareDisabledProperty();
        when(userService.getCurrentUser()).thenReturn(user1);
        branch.getSubscribers().add(user2);

        service.topicMoved(topic, TOPIC_ID);

        verify(mailService, Mockito.never()).sendTopicMovedMail(user2, TOPIC_ID);
    }
    
    private void prepareDisabledProperty() {
        Property disabledProperty = new Property(PROPERTY_NAME, FALSE_STRING);
        when(propertyDao.getByName(PROPERTY_NAME)).thenReturn(disabledProperty);
    }
    
    private void prepareEnabledProperty() {
        Property enabledProperty = new Property(PROPERTY_NAME, TRUE_STRING);
        when(propertyDao.getByName(PROPERTY_NAME)).thenReturn(enabledProperty);
    }

    @Test
    public void testSendNotificationAboutRemovingTopicWithEnabledProperty() {
        prepareEnabledProperty();
        Collection<JCUser> subscribers = new ArrayList();
        subscribers.add(user1);
        subscribers.add(user2);
        service.sendNotificationAboutRemovingTopic(topic, subscribers);
        verify(mailService).sendRemovingTopicMail(user2, topic);
    }

    @Test
    public void testSendNotificationAboutRemovingTopicWithDisabledProperty() {
        prepareDisabledProperty();
        service.sendNotificationAboutRemovingTopic(topic, new ArrayList());
        verify(mailService, Mockito.never()).sendRemovingTopicMail(user1, topic);
    }
}
