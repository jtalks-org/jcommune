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

import org.jtalks.jcommune.model.entity.Topic;
import org.jtalks.jcommune.plugin.api.exceptions.NotFoundException;
import org.jtalks.jcommune.plugin.api.service.PluginTopicFetchService;
import org.jtalks.jcommune.plugin.api.service.PluginTopicModificationService;
import org.jtalks.jcommune.plugin.api.service.TypeAwarePluginTopicService;

/**
 * Service for operating with topic types provided by plugins.
 * Should be used in plugins only. For core topic types use classes provided by service module
 *
 * This class is  singleton because we can't use spring dependency injection mechanism in plugins due plugins can be
 * added or removed in runtime.
 *
 * @author Mikhail Stryzhonok
 */
public class TransactionalTypeAwarePluginTopicService implements TypeAwarePluginTopicService{

    private static final TransactionalTypeAwarePluginTopicService INSTANCE =
            new TransactionalTypeAwarePluginTopicService();

    private PluginTopicFetchService topicFetchService;
    private PluginTopicModificationService topicModificationService;

    /** Use {@link #getInstance()}, this class is singleton. */
    private TransactionalTypeAwarePluginTopicService() {

    }

    /**
     * Gets instance of {@link TransactionalTypeAwarePluginTopicService}
     *
     * @return instance of {@link TransactionalTypeAwarePluginTopicService}
     */
    public static TypeAwarePluginTopicService getInstance() {
        return INSTANCE;
    }


    /**
     * Sets topic fetch service. Should be used once, during initialization
     *
     * @param topicFetchService topic fetch service to set
     */
    public void setTopicFetchService(PluginTopicFetchService topicFetchService) {
        this.topicFetchService = topicFetchService;
    }

    /**
     * Sets topic modification service. Should be used once, during initialization
     *
     * @param topicModificationService topic modification service to set
     */
    public void setTopicModificationService(PluginTopicModificationService topicModificationService) {
        this.topicModificationService = topicModificationService;
    }

    /**
     * Gets topic with specified type by id
     *
     * @param id id of interested topic
     * @param type type of interested topic
     *
     * @return topic with specified id and type
     * @throws NotFoundException if topic with specified id is not found or has different type
     */
    public Topic get(Long id, String type) throws NotFoundException {
        Topic topic =  topicFetchService.get(id);
        if (!topic.getType().equals(type)) {
            throw new NotFoundException();
        }
        return topic;
    }

    /**
     * Updates topic. Should be used top modify existent topics
     *
     * @param topic topic with modifications
     *
     * @throws NotFoundException if modifying totic not found
     */
    public void updateTopic(Topic topic) throws NotFoundException {
        topicModificationService.updateTopic(topic, null);
    }

    /**
     * Creates new topic with specified body text
     *
     * @param topicDto topic used as dto
     * @param bodyText text of first post
     *
     * @return newly created topic
     * @throws NotFoundException if {@link org.jtalks.jcommune.model.entity.Branch} not found
     */
    public Topic createTopic(Topic topicDto, String bodyText) throws NotFoundException {
        return topicModificationService.createTopic(topicDto, bodyText);
    }

    /**
     * Check if user has given permission. Throws
     *
     * @param branchId ID of the branch which holds permissions
     *
     * @throws org.springframework.security.access.AccessDeniedException} if user
     * don't have specified permission.
     */
    public void checkViewTopicPermission(Long branchId) {
        topicFetchService.checkViewTopicPermission(branchId);
    }
}
