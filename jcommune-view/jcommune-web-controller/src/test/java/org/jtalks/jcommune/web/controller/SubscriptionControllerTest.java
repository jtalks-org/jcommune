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


import org.jtalks.jcommune.model.entity.Branch;
import org.jtalks.jcommune.model.entity.Topic;
import org.jtalks.jcommune.model.entity.User;
import org.jtalks.jcommune.service.BranchService;
import org.jtalks.jcommune.service.nontransactional.SecurityService;
import org.jtalks.jcommune.service.SubscriptionService;
import org.jtalks.jcommune.service.TopicService;
import org.jtalks.jcommune.service.exceptions.NotFoundException;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.springframework.context.MessageSource;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Locale;
import java.util.Map;

import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.testng.Assert.assertEquals;

/**
 * @author Evgeniy Naumenko
 */
public class SubscriptionControllerTest {

    @Mock
    private TopicService topicService;
    @Mock
    private BranchService branchService;
    @Mock
    private MessageSource messageSource;
    @Mock
    SubscriptionService subscriptionService;

    private SubscriptionController controller;

    private Topic topic = new Topic(null, "title");
    private Branch branch = new Branch("name");

    private String message = "message";
    private Locale locale = Locale.ENGLISH;
    private Long id = 1L;

    @BeforeMethod
    public void setUp() {
        initMocks(this);
        controller = new SubscriptionController(topicService, branchService, subscriptionService, messageSource);
        when(messageSource.getMessage(
                anyString(), Matchers.<Object[]>any(), Matchers.<Locale>any() )).thenReturn(message);
    }

    @Test
    public void testSubscribeToTopic() throws NotFoundException {
        when(topicService.get(id)).thenReturn(topic);

        Map<String, String> map = controller.subscribeToTopic(id, locale);

        assertEquals(map.get("caption"), message);
        assertEquals(map.get("urlSuffix"), "/topics/1/unsubscribe");
    }

    @Test
    public void testUnsubscribeFromTopic() throws NotFoundException {
        when(topicService.get(id)).thenReturn(topic);

        Map<String, String> map = controller.unsubscribeFromTopic(id, locale);

        assertEquals(map.get("caption"), message);
        assertEquals(map.get("urlSuffix"), "/topics/1/subscribe");
    }

    @Test
    public void testSubscribeOnBranch() throws NotFoundException {
        when(branchService.get(id)).thenReturn(branch);

        Map<String, String> map = controller.subscribeToBranch(id, locale);

        assertEquals(map.get("caption"), message);
        assertEquals(map.get("urlSuffix"), "/branches/1/unsubscribe");
    }

    @Test
    public void testUnsubscribeFromBranch() throws NotFoundException {
        when(branchService.get(id)).thenReturn(branch);

        Map<String, String> map = controller.unsubscribeFromBranch(id, locale);

        assertEquals(map.get("caption"), message);
        assertEquals(map.get("urlSuffix"), "/branches/1/subscribe");
    }

    @Test(expectedExceptions = NotFoundException.class)
    public void testSubscribeToNonexistingTopic() throws NotFoundException {
        doThrow(new NotFoundException()).when(topicService).get(id);
        Map<String, String> map = controller.subscribeToTopic(id, locale);
    }

    @Test(expectedExceptions = NotFoundException.class)
    public void testUnsubscribeFromNonexistingTopic() throws NotFoundException {
        doThrow(new NotFoundException()).when(topicService).get(id);
        Map<String, String> map = controller.unsubscribeFromTopic(id, locale);
    }

    @Test(expectedExceptions = NotFoundException.class)
    public void testSubscribeOnNonexistingBranch() throws NotFoundException {
        doThrow(new NotFoundException()).when(branchService).get(id);
        Map<String, String> map = controller.subscribeToBranch(id, locale);
    }

    @Test(expectedExceptions = NotFoundException.class)
    public void testUnsubscribeFromNonexistingBranch() throws NotFoundException {
        doThrow(new NotFoundException()).when(branchService).get(id);
        Map<String, String> map = controller.unsubscribeFromBranch(id, locale);
    }
}
