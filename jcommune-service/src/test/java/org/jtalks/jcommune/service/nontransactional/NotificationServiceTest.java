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
import org.jtalks.jcommune.model.entity.Branch;
import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.model.entity.JCommuneProperty;
import org.jtalks.jcommune.model.entity.Topic;
import org.jtalks.jcommune.service.UserService;
import org.jtalks.jcommune.service.exceptions.MailingFailedException;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.jtalks.jcommune.model.entity.JCommuneProperty.SENDING_NOTIFICATIONS_ENABLED;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

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
    private PropertyDao propertyDao;
    private JCommuneProperty notificationsEnabledProperty = SENDING_NOTIFICATIONS_ENABLED;
    private NotificationService service;
    private final long TOPIC_ID = 1;

    private JCUser user1 = new JCUser("name1", "email1", "password1");
    private JCUser user2 = new JCUser("name2", "email2", "password2");
    private JCUser user3 = new JCUser("name3", "email3", "password3");
    private Topic topic;
    private Branch branch;

    @BeforeMethod
    public void setUp() {
        initMocks(this);
        notificationsEnabledProperty.setPropertyDao(propertyDao);
        notificationsEnabledProperty.setName(PROPERTY_NAME);
        service = new NotificationService(
                userService,
                mailService,
                notificationsEnabledProperty);
        topic = new Topic(user1, "title");
        branch = new Branch("name", "description");
        branch.addTopic(topic);
    }

    @Test
    public void testTopicChanged() throws MailingFailedException {
        prepareEnabledProperty();
        topic.getSubscribers().add(user1);
        topic.getSubscribers().add(user2);

        service.topicChanged(topic);

        verify(mailService).sendTopicUpdatesOnSubscription(user1, topic);
        verify(mailService).sendTopicUpdatesOnSubscription(user2, topic);
    }
    
    @Test
    public void testTopicChanedWithDisabledNotifcations() {
        prepareDisabledProperty();
        topic.getSubscribers().add(user1);

        service.topicChanged(topic);

        verify(mailService, Mockito.never()).sendTopicUpdatesOnSubscription(user1, topic);
    }

    @Test
    public void testBranchChanged() throws MailingFailedException {
        prepareEnabledProperty();
        branch.getSubscribers().add(user1);
        branch.getSubscribers().add(user2);

        service.branchChanged(branch);

        verify(mailService).sendBranchUpdatesOnSubscription(user1, branch);
        verify(mailService).sendBranchUpdatesOnSubscription(user2, branch);
    }
    
    @Test
    public void testBranchChangedWithDisabledNotifications() {
        prepareDisabledProperty();
        branch.getSubscribers().add(user1);

        service.branchChanged(branch);

        verify(mailService, Mockito.never()).sendBranchUpdatesOnSubscription(user1, branch);
    }

    @Test
    public void testTopicChangedSelfSubscribed() throws MailingFailedException {
        prepareEnabledProperty();
        when(userService.getCurrentUser()).thenReturn(user1);
        topic.getSubscribers().add(user1);
        topic.getSubscribers().add(user2);

        service.topicChanged(topic);

        verify(mailService).sendTopicUpdatesOnSubscription(user2, topic);
        verifyNoMoreInteractions(mailService);
    }

    @Test
    public void testBranchChangedSelfSubscribed() throws MailingFailedException {
        prepareEnabledProperty();
        when(userService.getCurrentUser()).thenReturn(user1);
        branch.getSubscribers().add(user1);
        branch.getSubscribers().add(user2);

        service.branchChanged(branch);

        verify(mailService).sendBranchUpdatesOnSubscription(user2, branch);
        verifyNoMoreInteractions(mailService);
    }

    @Test
    public void testTopicChangedNoSubscribers() {
        prepareEnabledProperty();
        service.topicChanged(topic);

        verifyZeroInteractions(mailService);
    }

    @Test
    public void testBranchChangedNoSubscribers() {
        prepareEnabledProperty();
        service.branchChanged(branch);

        verifyZeroInteractions(mailService);
    }

    @Test
    public void testTopicMovedWithBranchSubscribers() {
        prepareEnabledProperty();
        when(userService.getCurrentUser()).thenReturn(user1);
        branch.getSubscribers().add(user1);
        branch.getSubscribers().add(user2);
        branch.getSubscribers().add(user3);

        service.topicMoved(topic, TOPIC_ID);

        verify(mailService).sendTopicMovedMail(user2, TOPIC_ID);
        verify(mailService).sendTopicMovedMail(user3, TOPIC_ID);
    }

    @Test
    public void testTopicMovedTopicStarterIsNotASubscriber() {
        prepareEnabledProperty();
        when(userService.getCurrentUser()).thenReturn(user1);
        branch.getSubscribers().add(user2);
        branch.getSubscribers().add(user3);

        service.topicMoved(topic, TOPIC_ID);

        verify(mailService).sendTopicMovedMail(user2, TOPIC_ID);
        verify(mailService).sendTopicMovedMail(user3, TOPIC_ID);
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
}
