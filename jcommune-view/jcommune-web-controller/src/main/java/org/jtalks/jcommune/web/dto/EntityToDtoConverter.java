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

import org.jtalks.jcommune.model.dto.PageRequest;
import org.jtalks.jcommune.model.entity.Topic;
import org.jtalks.jcommune.plugin.api.PluginLoader;
import org.jtalks.jcommune.plugin.api.core.Plugin;
import org.jtalks.jcommune.plugin.api.core.TopicPlugin;
import org.jtalks.jcommune.plugin.api.filters.TypeFilter;
import org.jtalks.jcommune.plugin.api.web.dto.TopicDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Mikhail Stryzhonok
 */
public class EntityToDtoConverter {

    private PluginLoader pluginLoader;

    public EntityToDtoConverter(PluginLoader pluginLoader) {
        this.pluginLoader = pluginLoader;
    }

    public Page<TopicDto> convertToDtoPage(Page<Topic> source) {
        List<TopicPlugin> plugins = getEnabledTopicPlugins();
        List<TopicDto> dtos = new ArrayList<>();
        for (Topic topic : source) {
            dtos.add(createTopicDto(topic, plugins));
        }
        return new PageImpl<>(dtos, PageRequest.fetchFromPage(source), source.getTotalElements());
    }

    private TopicDto createTopicDto(Topic topic, List<TopicPlugin> plugins) {
        TopicDto dto = new TopicDto(topic);
        for (TopicPlugin plugin : plugins) {
            if (plugin.getTopicType().equals(topic.getType())) {
                dto.setTopicUrl("/" + plugin.getTopicType().toLowerCase() + "/" + topic.getId());
                dto.setReadIconUrl("/" + plugin.getTopicType().toLowerCase() + "/read.png");
                dto.setUnreadIconUrl("/" + plugin.getTopicType().toLowerCase() + "/unread.png");
                return dto;
            }
        }
        dto.setTopicUrl("/topics/" + topic.getId());
        if (topic.isCodeReview()) {
            dto.setUnreadIconUrl("/resources/images/code-review-new-posts.png");
            dto.setReadIconUrl("/resources/images/code-review-no-new-posts.png");
        } else if (topic.isClosed()) {
            dto.setUnreadIconUrl("/resources/images/closed-new-posts.png");
            dto.setReadIconUrl("/resources/images/closed-no-new-posts.png");
        } else {
            dto.setUnreadIconUrl("/resources/images/new-posts.png");
            dto.setReadIconUrl("/resources/images/no-new-posts.png");
        }
        return dto;
    }

    /**
     * Gets list of enabled topic plugins
     * @return list of topic plugins
     * @see org.jtalks.jcommune.plugin.api.core.TopicPlugin
     */
    private List<TopicPlugin> getEnabledTopicPlugins() {
        List<TopicPlugin> topicPlugins = new ArrayList<>();
        List<Plugin> plugins = pluginLoader.getPlugins(new TypeFilter(TopicPlugin.class));
        for (Plugin plugin : plugins) {
            if (plugin.isEnabled()) {
                topicPlugins.add((TopicPlugin) plugin);
            }
        }
        return topicPlugins;
    }


}
