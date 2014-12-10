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
import org.jtalks.jcommune.plugin.api.filters.StateFilter;
import org.jtalks.jcommune.plugin.api.filters.TypeFilter;
import org.jtalks.jcommune.plugin.api.web.dto.TopicDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * Class for conversion entity to dto
 *
 * @author Mikhail Stryzhonok
 */
public class EntityToDtoConverter {

    private PluginLoader pluginLoader;

    public EntityToDtoConverter(PluginLoader pluginLoader) {
        this.pluginLoader = pluginLoader;
    }

    /**
     * Convert page of {@link Topic} to page of {@link TopicDto}
     *
     * @param source page of {@link Topic}
     * @return page of {@link TopicDto}
     */
    public Page<TopicDto> convertToDtoPage(Page<Topic> source) {
        List<Plugin> plugins = pluginLoader.getPlugins(new TypeFilter(TopicPlugin.class),
                new StateFilter(Plugin.State.ENABLED));
        List<TopicDto> dtos = new ArrayList<>();
        for (Topic topic : source) {
            dtos.add(createTopicDto(topic, plugins));
        }
        return new PageImpl<>(dtos, PageRequest.fetchFromPage(source), source.getTotalElements());
    }

    private TopicDto createTopicDto(Topic topic, List<Plugin> plugins) {
        TopicDto dto = new TopicDto(topic);
        for (Plugin plugin : plugins) {
            TopicPlugin topicPlugin = (TopicPlugin)plugin;
            if (topicPlugin.getTopicType().equals(topic.getType())) {
                dto.setTopicUrl("/topics/" + topicPlugin.getTopicType().toLowerCase() + "/" + topic.getId());
                if (dto.getTopic().isClosed()) {
                    dto.setReadIconUrl("/topics/" + topicPlugin.getTopicType().toLowerCase() + "/icon/closed_read.png");
                    dto.setUnreadIconUrl("/topics/" + topicPlugin.getTopicType().toLowerCase() + "/icon/closed_unread.png");
                } else {
                    dto.setReadIconUrl("/topics/" + topicPlugin.getTopicType().toLowerCase() + "/icon/read.png");
                    dto.setUnreadIconUrl("/topics/" + topicPlugin.getTopicType().toLowerCase() + "/icon/unread.png");
                }
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

}
