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
package org.jtalks.jcommune.web.controller;


import junit.framework.Assert;
import org.jtalks.jcommune.model.entity.Branch;
import org.jtalks.jcommune.model.entity.Topic;
import org.jtalks.jcommune.service.BranchService;
import org.jtalks.jcommune.service.SubscriptionService;
import org.jtalks.jcommune.service.TopicFetchService;
import org.jtalks.jcommune.service.exceptions.NotFoundException;
import org.mockito.Mock;
import org.springframework.web.servlet.ModelAndView;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * @author Evgeniy Naumenko
 */
public class SubscriptionControllerTest {

    @Mock
    private TopicFetchService topicFetchService;
    @Mock
    private BranchService branchService;
    @Mock
    SubscriptionService subscriptionService;

    private SubscriptionController controller;

    private Topic topic = new Topic(null, "title");
    private Branch branch = new Branch("name", "description");

    private Long id = 1L;

    @BeforeMethod
    public void setUp() {
        initMocks(this);
        controller = new SubscriptionController(topicFetchService, branchService, subscriptionService);
    }

    @Test
    public void testSubscribeToTopic() throws NotFoundException {
        when(topicFetchService.get(id)).thenReturn(topic);

        controller.subscribeToTopic(id, anyString());

        verify(subscriptionService).toggleTopicSubscription(topic);
    }

    @Test
    public void testUnsubscribeFromTopic() throws NotFoundException {
        when(topicFetchService.get(id)).thenReturn(topic);

       controller.unsubscribeFromTopic(id, anyString());

        verify(subscriptionService).toggleTopicSubscription(topic);
    }

    @Test
    public void testSubscribeOnBranch() throws NotFoundException {
        when(branchService.get(id)).thenReturn(branch);

        controller.subscribeToBranch(id, anyString());

        verify(subscriptionService).toggleBranchSubscription(branch);
    }

    @Test
    public void testUnsubscribeFromBranch() throws NotFoundException {
        when(branchService.get(id)).thenReturn(branch);

        controller.unsubscribeFromBranch(id, anyString());

        verify(subscriptionService).toggleBranchSubscription(branch);
    }

    @Test
    public void testRedirectWhenUnsubscribeFromBranchByLink() throws NotFoundException {
        when(branchService.get(id)).thenReturn(branch);
        ModelAndView actualMav = controller.unsubscribeFromBranch(id, anyString());
        Assert.assertEquals(actualMav, null);
    }

    @Test
    public void testUnsubscribeFromBranchByLink() throws NotFoundException {
        when(branchService.get(id)).thenReturn(branch);

        controller.unsubscribeFromBranch(id, anyString());

        verify(subscriptionService).toggleBranchSubscription(branch);
    }

    @Test(expectedExceptions = NotFoundException.class)
    public void testSubscribeToNonexistingTopic() throws NotFoundException {
        doThrow(new NotFoundException()).when(topicFetchService).get(id);
        controller.subscribeToTopic(id, anyString());
    }

    @Test(expectedExceptions = NotFoundException.class)
    public void testUnsubscribeFromNonexistingTopic() throws NotFoundException {
        doThrow(new NotFoundException()).when(topicFetchService).get(id);
        controller.unsubscribeFromTopic(id, anyString());
    }

    @Test(expectedExceptions = NotFoundException.class)
    public void testSubscribeOnNonexistingBranch() throws NotFoundException {
        doThrow(new NotFoundException()).when(branchService).get(id);
        controller.subscribeToBranch(id, anyString());
    }

    @Test(expectedExceptions = NotFoundException.class)
    public void testUnsubscribeFromNonexistingBranch() throws NotFoundException {
        doThrow(new NotFoundException()).when(branchService).get(id);
        controller.unsubscribeFromBranch(id, anyString());
    }
}
