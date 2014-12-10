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
package org.jtalks.jcommune.plugin.api.service.transactional;

import org.jtalks.jcommune.model.entity.Poll;
import org.jtalks.jcommune.model.entity.Topic;
import org.jtalks.jcommune.plugin.api.exceptions.NotFoundException;
import org.jtalks.jcommune.plugin.api.service.PluginTopicFetchService;
import org.jtalks.jcommune.plugin.api.service.PluginTopicModificationService;
import org.mockito.Mock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.testng.Assert.assertEquals;

/**
 * @author Mikhail Stryzhonok
 */
public class TransactionalTypeAwarePluginTopicServiceTest {

    @Mock
    private PluginTopicFetchService topicFetchService;
    @Mock
    private PluginTopicModificationService topicModificationService;

    @BeforeMethod
    public void init() {
        initMocks(this);
        TransactionalTypeAwarePluginTopicService service =
                (TransactionalTypeAwarePluginTopicService)TransactionalTypeAwarePluginTopicService.getInstance();
        service.setTopicFetchService(topicFetchService);
        service.setTopicModificationService(topicModificationService);
    }

    @Test
    public void testGet() throws Exception{
        String type = "Type";
        Topic topic = new Topic();
        topic.setType(type);

        when(topicFetchService.get(anyLong())).thenReturn(topic);

        Topic actual = TransactionalTypeAwarePluginTopicService.getInstance().get(1L, type);

        assertEquals(actual, topic);
    }

    @Test(expectedExceptions = NotFoundException.class)
    public void getShouldThrowExceptionIfUnexpectedTopicTypeGot() throws Exception {
        Topic topic = new Topic();
        topic.setType("type");

        when(topicFetchService.get(anyLong())).thenReturn(topic);

        TransactionalTypeAwarePluginTopicService.getInstance().get(1L, "anothrer type");
    }

    @Test(expectedExceptions = NotFoundException.class)
    public void getShouldThrowExceptionIfTopicNotFound() throws Exception {
        when(topicFetchService.get(anyLong())).thenThrow(new NotFoundException());

        TransactionalTypeAwarePluginTopicService.getInstance().get(1L, "type");
    }

    @Test
    public void updateTopicShouldCallTopicModificationService() throws Exception {
        Topic topic = new Topic();
        TransactionalTypeAwarePluginTopicService.getInstance().updateTopic(topic);

        verify(topicModificationService).updateTopic(topic, null);
    }

    @Test(expectedExceptions = NotFoundException.class)
    public void updateTopicShouldThrowExceptionIfTopicNotFound() throws Exception {
        doThrow(new NotFoundException()).when(topicModificationService).updateTopic(any(Topic.class), any(Poll.class));

        TransactionalTypeAwarePluginTopicService.getInstance().updateTopic(new Topic());
    }

    @Test
    public void testCreateTopic() throws Exception {
        Topic expected = new Topic();
        when(topicModificationService.createTopic(any(Topic.class), anyString())).thenReturn(expected);

        Topic actual = TransactionalTypeAwarePluginTopicService.getInstance().createTopic(new Topic(), "text");

        assertEquals(actual, expected);
    }

    @Test(expectedExceptions = NotFoundException.class)
    public void createTopicShouldThrowExceptionIfBranchNotFound() throws Exception {
        when(topicModificationService.createTopic(any(Topic.class), anyString())).thenThrow(new NotFoundException());

        TransactionalTypeAwarePluginTopicService.getInstance().createTopic(new Topic(), "text");
    }

    @Test
    public void checkViewTopicPermissionShouldCallTopicFetchService() {
        TransactionalTypeAwarePluginTopicService.getInstance().checkViewTopicPermission(1L);

        verify(topicFetchService).checkViewTopicPermission(1L);
    }
}
