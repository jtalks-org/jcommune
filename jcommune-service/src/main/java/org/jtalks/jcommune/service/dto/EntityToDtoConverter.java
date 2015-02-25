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
package org.jtalks.jcommune.service.dto;

import org.jtalks.jcommune.model.dto.PageRequest;
import org.jtalks.jcommune.model.entity.Post;
import org.jtalks.jcommune.model.entity.Topic;
import org.jtalks.jcommune.plugin.api.PluginLoader;
import org.jtalks.jcommune.plugin.api.core.Plugin;
import org.jtalks.jcommune.plugin.api.core.TopicPlugin;
import org.jtalks.jcommune.plugin.api.filters.StateFilter;
import org.jtalks.jcommune.plugin.api.filters.TypeFilter;
import org.jtalks.jcommune.plugin.api.web.dto.PostDto;
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

    public static final String PREFIX = "/topics/";
    public static final String PLUGABLE_UNREAD = "/icon/unread.png";
    public static final String PLUGABLE_READ = "/icon/read.png";
    public static final String PLUGABLE_CLOSED_UNREAD = "/icon/closed_unread.png";
    public static final String PLUGABLE_CLOSED_READ = "/icon/closed_unread.png";
    public static final String CODE_REVIEW_NEW_POSTS = "/resources/images/code-review-new-posts.png";
    public static final String CODE_REVIEW_NO_NEW_POSTS = "/resources/images/code-review-no-new-posts.png";
    public static final String DISCUSSION_CLOSED_NEW_POSTS = "/resources/images/closed-new-posts.png";
    public static final String DISCUSSION_CLOSED_NO_NEW_POSTS = "/resources/images/closed-no-new-posts.png";
    public static final String DISCUSSION_NEW_POSTS = "/resources/images/new-posts.png";
    public static final String DISCUSSION_NO_NEW_POSTS = "/resources/images/no-new-posts.png";


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
    public Page<TopicDto> convertTopicPageToTopicDtoPage(Page<Topic> source) {
        List<Plugin> plugins = pluginLoader.getPlugins(new TypeFilter(TopicPlugin.class),
                new StateFilter(Plugin.State.ENABLED));
        List<TopicDto> dtos = new ArrayList<>();
        for (Topic topic : source) {
            dtos.add(createTopicDto(topic, plugins));
        }
        return new PageImpl<>(dtos, PageRequest.fetchFromPage(source), source.getTotalElements());
    }

    /**
     * Converts page of {@link Post} to page of {@link PostDto}
     *
     * @param source page of {@link Post}
     *
     * @return page of {@link PostDto}
     */
    public Page<PostDto> convertPostPageToPostDtoPage(Page<Post> source) {
        List<Plugin> plugins = pluginLoader.getPlugins(new TypeFilter(TopicPlugin.class),
                new StateFilter(Plugin.State.ENABLED));
        List<PostDto> dtos = new ArrayList<>();
        for (Post post : source) {
            PostDto dto = PostDto.getDtoFor(post);
            dto.setTopicDto(createTopicDto(post.getTopic(), plugins));
            dtos.add(dto);
        }
        return new PageImpl<>(dtos, PageRequest.fetchFromPage(source), source.getTotalElements());
    }

    /**
     * Converts {@link Topic} to {@link TopicDto}
     *
     * @param topic {@link Topic} to be converted
     *
     * @return resulted {@link TopicDto}
     */
    public TopicDto convertTopicToDto(Topic topic) {
        List<Plugin> plugins = pluginLoader.getPlugins(new TypeFilter(TopicPlugin.class),
                new StateFilter(Plugin.State.ENABLED));
        return createTopicDto(topic, plugins);
    }

    /**
     * Creates topic dto depends of topic type and state of plugin which provides this topic
     *
     * @param topic {@link Topic} entity
     * @param plugins list of enabled topic plugins
     *
     * @return topic dto
     */
    private TopicDto createTopicDto(Topic topic, List<Plugin> plugins) {
        TopicDto dto = new TopicDto(topic);
        if (topic.isPlugable()) {
            for (Plugin plugin : plugins) {
                TopicPlugin topicPlugin = (TopicPlugin) plugin;
                if (topicPlugin.getTopicType().equals(topic.getType())) {
                    return populatePlugableTopicDto(dto, topicPlugin);
                }
            }
        }
        return populateCoreTopicDto(dto);
    }

    /**
     * Populate topicUrl, readIconUrl, unreadIconUrl parameters of dto for topics provided by plugins
     *
     * @param plugableTopicDto dto to be populated
     * @param plugin plugin which provides this topic
     *
     * @return populated dto
     */
    private TopicDto populatePlugableTopicDto(TopicDto plugableTopicDto, TopicPlugin plugin) {
        plugableTopicDto.setTopicUrl(PREFIX + plugin.getTopicType().toLowerCase() + "/" + plugableTopicDto.getTopic().getId());
        plugableTopicDto.setPostUrlPrefix(PREFIX + plugin.getTopicType().toLowerCase() + "/" + plugableTopicDto.getTopic().getId() + "/post/");
        if (plugableTopicDto.getTopic().isClosed()) {
            plugableTopicDto.setReadIconUrl(PREFIX + plugin.getTopicType().toLowerCase() + PLUGABLE_CLOSED_READ);
            plugableTopicDto.setUnreadIconUrl(PREFIX + plugin.getTopicType().toLowerCase() + PLUGABLE_CLOSED_UNREAD);
        } else {
            plugableTopicDto.setReadIconUrl(PREFIX + plugin.getTopicType().toLowerCase() + PLUGABLE_READ);
            plugableTopicDto.setUnreadIconUrl(PREFIX + plugin.getTopicType().toLowerCase() + PLUGABLE_UNREAD);
        }
        return plugableTopicDto;
    }

    /**
     * Populate topicUrl, readIconUrl, unreadIconUrl parameters of dto for topics (Discussion and CodeReview)
     *
     * @param dto dto to be populated
     *
     * @return populated dto
     */
    private TopicDto populateCoreTopicDto(TopicDto dto) {
        dto.setTopicUrl(PREFIX + dto.getTopic().getId());
        dto.setPostUrlPrefix("/posts/");
        if (dto.getTopic().isCodeReview()) {
            dto.setUnreadIconUrl(CODE_REVIEW_NEW_POSTS);
            dto.setReadIconUrl(CODE_REVIEW_NO_NEW_POSTS);
        } else if (dto.getTopic().isClosed()) {
            dto.setUnreadIconUrl(DISCUSSION_CLOSED_NEW_POSTS);
            dto.setReadIconUrl(DISCUSSION_CLOSED_NO_NEW_POSTS);
        } else {
            dto.setUnreadIconUrl(DISCUSSION_NEW_POSTS);
            dto.setReadIconUrl(DISCUSSION_NO_NEW_POSTS);
        }
        return dto;
    }
}
