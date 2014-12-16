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
package org.jtalks.jcommune.web.dto;

import org.jtalks.jcommune.model.entity.Topic;
import org.jtalks.jcommune.model.entity.TopicTypeName;
import org.jtalks.jcommune.plugin.api.PluginLoader;
import org.jtalks.jcommune.plugin.api.core.Plugin;
import org.jtalks.jcommune.plugin.api.core.TopicPlugin;
import org.jtalks.jcommune.plugin.api.filters.PluginFilter;
import org.jtalks.jcommune.plugin.api.web.dto.TopicDto;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.testng.Assert.assertEquals;

/**
 * @author Mikhail Stryzhonok
 */
public class EntityToDtoConverterTest {
    @Mock
    private PluginLoader pluginLoader;
    @Mock
    private TopicPlugin topicPlugin;

    private EntityToDtoConverter converter;

    @BeforeMethod
    public void init() {
        initMocks(this);
        converter = new EntityToDtoConverter(pluginLoader);
    }

    @Test
    public void testConvertToDtoPageForDiscussionWhenTopicNotClosedAndNoTopicPluginsEnabled() {
        Topic topic = createTopic();
        topic.setType(TopicTypeName.DISCUSSION.getName());

        when(pluginLoader.getPlugins(any(PluginFilter.class), any(PluginFilter.class))).thenReturn(Collections.EMPTY_LIST);

        Page<TopicDto> result = converter.convertToDtoPage(new PageImpl<>(Arrays.asList(topic)));

        assertEquals(result.getNumberOfElements(), 1);
        TopicDto dto = result.getContent().get(0);
        assertEquals(dto.getTopicUrl(), EntityToDtoConverter.PREFIX + topic.getId());
        assertEquals(dto.getUnreadIconUrl(), EntityToDtoConverter.DISCUSSION_NEW_POSTS);
        assertEquals(dto.getReadIconUrl(), EntityToDtoConverter.DISCUSSION_NO_NEW_POSTS);
    }

    @Test
    public void testConvertToDtoPageForDiscussionWhenTopicNotClosedButTopicPluginsEnabled() {
        Topic topic = createTopic();
        topic.setType(TopicTypeName.DISCUSSION.getName());

        when(pluginLoader.getPlugins(any(PluginFilter.class), any(PluginFilter.class)))
                .thenReturn(Arrays.<Plugin>asList(topicPlugin));
        when(topicPlugin.getTopicType()).thenReturn("Type 1");

        Page<TopicDto> result = converter.convertToDtoPage(new PageImpl<>(Arrays.asList(topic)));

        assertEquals(result.getNumberOfElements(), 1);
        TopicDto dto = result.getContent().get(0);
        assertEquals(dto.getTopicUrl(), EntityToDtoConverter.PREFIX + topic.getId());
        assertEquals(dto.getUnreadIconUrl(), EntityToDtoConverter.DISCUSSION_NEW_POSTS);
        assertEquals(dto.getReadIconUrl(), EntityToDtoConverter.DISCUSSION_NO_NEW_POSTS);
    }

    @Test
    public void pluginsShouldNotOverrideDiscussionTopicTypeWhenTopicNotClosed() {
        Topic topic = createTopic();
        topic.setType(TopicTypeName.DISCUSSION.getName());

        when(pluginLoader.getPlugins(any(PluginFilter.class), any(PluginFilter.class)))
                .thenReturn(Arrays.<Plugin>asList(topicPlugin));
        when(topicPlugin.getTopicType()).thenReturn(TopicTypeName.DISCUSSION.getName());

        Page<TopicDto> result = converter.convertToDtoPage(new PageImpl<>(Arrays.asList(topic)));

        assertEquals(result.getNumberOfElements(), 1);
        TopicDto dto = result.getContent().get(0);
        assertEquals(dto.getTopicUrl(), EntityToDtoConverter.PREFIX + topic.getId());
        assertEquals(dto.getUnreadIconUrl(), EntityToDtoConverter.DISCUSSION_NEW_POSTS);
        assertEquals(dto.getReadIconUrl(), EntityToDtoConverter.DISCUSSION_NO_NEW_POSTS);
    }

    @Test
    public void testConvertToDtoPageForDiscussionWhenTopicClosedAndNoTopicPluginsEnabled() {
        Topic topic = createTopic();
        topic.setType(TopicTypeName.DISCUSSION.getName());
        topic.setClosed(true);

        when(pluginLoader.getPlugins(any(PluginFilter.class), any(PluginFilter.class))).thenReturn(Collections.EMPTY_LIST);

        Page<TopicDto> result = converter.convertToDtoPage(new PageImpl<>(Arrays.asList(topic)));

        assertEquals(result.getNumberOfElements(), 1);
        TopicDto dto = result.getContent().get(0);
        assertEquals(dto.getTopicUrl(), EntityToDtoConverter.PREFIX + topic.getId());
        assertEquals(dto.getUnreadIconUrl(), EntityToDtoConverter.DISCUSSION_CLOSED_NEW_POSTS);
        assertEquals(dto.getReadIconUrl(), EntityToDtoConverter.DISCUSSION_CLOSED_NO_NEW_POSTS);
    }

    @Test
    public void testConvertToDtoPageForDiscussionWhenTopicClosedButTopicPluginsEnabled() {
        Topic topic = createTopic();
        topic.setType(TopicTypeName.DISCUSSION.getName());
        topic.setClosed(true);

        when(pluginLoader.getPlugins(any(PluginFilter.class), any(PluginFilter.class)))
                .thenReturn(Arrays.<Plugin>asList(topicPlugin));
        when(topicPlugin.getTopicType()).thenReturn("Type 1");

        Page<TopicDto> result = converter.convertToDtoPage(new PageImpl<>(Arrays.asList(topic)));

        assertEquals(result.getNumberOfElements(), 1);
        TopicDto dto = result.getContent().get(0);
        assertEquals(dto.getTopicUrl(), EntityToDtoConverter.PREFIX + topic.getId());
        assertEquals(dto.getUnreadIconUrl(), EntityToDtoConverter.DISCUSSION_CLOSED_NEW_POSTS);
        assertEquals(dto.getReadIconUrl(), EntityToDtoConverter.DISCUSSION_CLOSED_NO_NEW_POSTS);
    }

    @Test
    public void pluginsShouldNotOverrideDiscussionTopicTypeWhenTopicClosed() {
        Topic topic = createTopic();
        topic.setType(TopicTypeName.DISCUSSION.getName());
        topic.setClosed(true);

        when(pluginLoader.getPlugins(any(PluginFilter.class), any(PluginFilter.class)))
                .thenReturn(Arrays.<Plugin>asList(topicPlugin));
        when(topicPlugin.getTopicType()).thenReturn(TopicTypeName.DISCUSSION.getName());

        Page<TopicDto> result = converter.convertToDtoPage(new PageImpl<>(Arrays.asList(topic)));

        assertEquals(result.getNumberOfElements(), 1);
        TopicDto dto = result.getContent().get(0);
        assertEquals(dto.getTopicUrl(), EntityToDtoConverter.PREFIX + topic.getId());
        assertEquals(dto.getUnreadIconUrl(), EntityToDtoConverter.DISCUSSION_CLOSED_NEW_POSTS);
        assertEquals(dto.getReadIconUrl(), EntityToDtoConverter.DISCUSSION_CLOSED_NO_NEW_POSTS);
    }

    @Test
    public void testConvertToDtoPageForCodeReviewWhenNoTopicPluginsEnabled() {
        Topic topic = createTopic();
        topic.setType(TopicTypeName.CODE_REVIEW.getName());

        when(pluginLoader.getPlugins(any(PluginFilter.class), any(PluginFilter.class))).thenReturn(Collections.EMPTY_LIST);

        Page<TopicDto> result = converter.convertToDtoPage(new PageImpl<>(Arrays.asList(topic)));

        assertEquals(result.getNumberOfElements(), 1);
        TopicDto dto = result.getContent().get(0);
        assertEquals(dto.getTopicUrl(), EntityToDtoConverter.PREFIX + topic.getId());
        assertEquals(dto.getUnreadIconUrl(), EntityToDtoConverter.CODE_REVIEW_NEW_POSTS);
        assertEquals(dto.getReadIconUrl(), EntityToDtoConverter.CODE_REVIEW_NO_NEW_POSTS);
    }

    @Test
    public void testConvertToDtoPageForCodeReviewWhenTopicPluginsEnabled() {
        Topic topic = createTopic();
        topic.setType(TopicTypeName.CODE_REVIEW.getName());

        when(pluginLoader.getPlugins(any(PluginFilter.class), any(PluginFilter.class)))
                .thenReturn(Arrays.<Plugin>asList(topicPlugin));
        when(topicPlugin.getTopicType()).thenReturn("Type 1");

        Page<TopicDto> result = converter.convertToDtoPage(new PageImpl<>(Arrays.asList(topic)));

        assertEquals(result.getNumberOfElements(), 1);
        TopicDto dto = result.getContent().get(0);
        assertEquals(dto.getTopicUrl(), EntityToDtoConverter.PREFIX + topic.getId());
        assertEquals(dto.getUnreadIconUrl(), EntityToDtoConverter.CODE_REVIEW_NEW_POSTS);
        assertEquals(dto.getReadIconUrl(), EntityToDtoConverter.CODE_REVIEW_NO_NEW_POSTS);
    }

    @Test
    public void pluginShouldNotOverrideCodeReviewTopicType() {
        Topic topic = createTopic();
        topic.setType(TopicTypeName.CODE_REVIEW.getName());

        when(pluginLoader.getPlugins(any(PluginFilter.class), any(PluginFilter.class)))
                .thenReturn(Arrays.<Plugin>asList(topicPlugin));
        when(topicPlugin.getTopicType()).thenReturn(TopicTypeName.CODE_REVIEW.getName());

        Page<TopicDto> result = converter.convertToDtoPage(new PageImpl<>(Arrays.asList(topic)));

        assertEquals(result.getNumberOfElements(), 1);
        TopicDto dto = result.getContent().get(0);
        assertEquals(dto.getTopicUrl(), EntityToDtoConverter.PREFIX + topic.getId());
        assertEquals(dto.getUnreadIconUrl(), EntityToDtoConverter.CODE_REVIEW_NEW_POSTS);
        assertEquals(dto.getReadIconUrl(), EntityToDtoConverter.CODE_REVIEW_NO_NEW_POSTS);
    }

    @Test
    public void testConvertToDtoPageForPlugableTopicWhenAppropriatePluginEnabledAndTopicNotClosed() {
        String topicType = "Type1";
        Topic topic = createTopic();
        topic.setType(topicType);

        when(pluginLoader.getPlugins(any(PluginFilter.class), any(PluginFilter.class)))
                .thenReturn(Arrays.<Plugin>asList(topicPlugin));
        when(topicPlugin.getTopicType()).thenReturn(topicType);

        Page<TopicDto> result = converter.convertToDtoPage(new PageImpl<>(Arrays.asList(topic)));

        assertEquals(result.getNumberOfElements(), 1);
        TopicDto dto = result.getContent().get(0);
        assertEquals(dto.getTopicUrl(), EntityToDtoConverter.PREFIX + topicType.toLowerCase() + "/" + topic.getId());
        assertEquals(dto.getUnreadIconUrl(), EntityToDtoConverter.PREFIX + topicType.toLowerCase()
                + EntityToDtoConverter.PLUGABLE_UNREAD );
        assertEquals(dto.getReadIconUrl(), EntityToDtoConverter.PREFIX + topicType.toLowerCase()
                + EntityToDtoConverter.PLUGABLE_READ);
    }

    @Test
    public void testConvertToDtoPageForPlugableTopicWhenAppropriatePluginEnabledAndTopicClosed() {
        String topicType = "Type1";
        Topic topic = createTopic();
        topic.setType(topicType);
        topic.setClosed(true);

        when(pluginLoader.getPlugins(any(PluginFilter.class), any(PluginFilter.class)))
                .thenReturn(Arrays.<Plugin>asList(topicPlugin));
        when(topicPlugin.getTopicType()).thenReturn(topicType);

        Page<TopicDto> result = converter.convertToDtoPage(new PageImpl<>(Arrays.asList(topic)));

        assertEquals(result.getNumberOfElements(), 1);
        TopicDto dto = result.getContent().get(0);
        assertEquals(dto.getTopicUrl(), EntityToDtoConverter.PREFIX + topicType.toLowerCase() + "/" + topic.getId());
        assertEquals(dto.getUnreadIconUrl(), EntityToDtoConverter.PREFIX + topicType.toLowerCase()
                + EntityToDtoConverter.PLUGABLE_CLOSED_UNREAD );
        assertEquals(dto.getReadIconUrl(), EntityToDtoConverter.PREFIX + topicType.toLowerCase()
                + EntityToDtoConverter.PLUGABLE_CLOSED_READ);
    }

    @Test
    public void plugableTopicShouldUseDefaultsWhenAppropriateDisabledAndTopicNotClosed() {
        String topicType = "Type1";
        Topic topic = createTopic();
        topic.setType(topicType);

        when(pluginLoader.getPlugins(any(PluginFilter.class), any(PluginFilter.class))).thenReturn(Collections.EMPTY_LIST);

        Page<TopicDto> result = converter.convertToDtoPage(new PageImpl<>(Arrays.asList(topic)));

        assertEquals(result.getNumberOfElements(), 1);
        TopicDto dto = result.getContent().get(0);
        assertEquals(dto.getTopicUrl(), EntityToDtoConverter.PREFIX + topic.getId());
        assertEquals(dto.getUnreadIconUrl(), EntityToDtoConverter.DISCUSSION_NEW_POSTS);
        assertEquals(dto.getReadIconUrl(), EntityToDtoConverter.DISCUSSION_NO_NEW_POSTS);
    }

    @Test
    public void plugableTopicShouldUseDefaultsWhenAppropriateDisabledAndTopicClosed() {
        String topicType = "Type1";
        Topic topic = createTopic();
        topic.setType(topicType);
        topic.setClosed(true);

        when(pluginLoader.getPlugins(any(PluginFilter.class), any(PluginFilter.class))).thenReturn(Collections.EMPTY_LIST);

        Page<TopicDto> result = converter.convertToDtoPage(new PageImpl<>(Arrays.asList(topic)));

        assertEquals(result.getNumberOfElements(), 1);
        TopicDto dto = result.getContent().get(0);
        assertEquals(dto.getTopicUrl(), EntityToDtoConverter.PREFIX + topic.getId());
        assertEquals(dto.getUnreadIconUrl(), EntityToDtoConverter.DISCUSSION_CLOSED_NEW_POSTS);
        assertEquals(dto.getReadIconUrl(), EntityToDtoConverter.DISCUSSION_CLOSED_NO_NEW_POSTS);
    }

    private Topic createTopic() {
        Topic topic = new Topic();
        topic.setId(1);
        return topic;
    }


}
