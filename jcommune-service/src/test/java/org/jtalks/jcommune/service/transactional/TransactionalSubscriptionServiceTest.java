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

import org.jtalks.jcommune.model.dao.BranchDao;
import org.jtalks.jcommune.model.dao.TopicDao;
import org.jtalks.jcommune.model.entity.Branch;
import org.jtalks.jcommune.model.entity.Topic;
import org.jtalks.jcommune.model.entity.User;
import org.jtalks.jcommune.service.nontransactional.SecurityService;
import org.mockito.Mock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * @author Evgeniy Naumenko
 */
public class TransactionalSubscriptionServiceTest {

    @Mock
    private SecurityService securityService;
    @Mock
    private BranchDao branchDao;
    @Mock
    private TopicDao topicDao;

    private TransactionalSubscriptionService service;

    User user = new User("username", "email", "password");
    Branch branch;
    Topic topic;

    @BeforeMethod
    public void setUp() {
        initMocks(this);
        service = new TransactionalSubscriptionService(securityService, branchDao, topicDao);
        branch = new Branch("name");
        topic = new Topic(user, "title");
    }

    @Test
    public void testTopicSubscription() {
        when(securityService.getCurrentUser()).thenReturn(user);

        service.toggleTopicSubscription(topic);
        
        assertTrue(topic.getSubscribers().contains(user));
        verify(topicDao).update(topic);
    }

    @Test
    public void testBranchSubscription() {
        when(securityService.getCurrentUser()).thenReturn(user);

        service.toggleBranchSubscription(branch);

        assertTrue(branch.getSubscribers().contains(user));
    }

    @Test
    public void testTopicUnsubscription() {
        when(securityService.getCurrentUser()).thenReturn(user);
        topic.getSubscribers().add(user);

        service.toggleTopicSubscription(topic);

        assertFalse(topic.getSubscribers().contains(user));
        verify(topicDao).update(topic);
    }

    @Test
    public void testBranchUnsubscription() {
        when(securityService.getCurrentUser()).thenReturn(user);
        branch.getSubscribers().add(user);

        service.toggleBranchSubscription(branch);

        assertFalse(branch.getSubscribers().contains(user));
    }
}
