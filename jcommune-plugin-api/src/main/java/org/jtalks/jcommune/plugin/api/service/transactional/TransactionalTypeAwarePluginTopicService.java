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
 * @author Mikhail Stryzhonok
 */
public class TransactionalTypeAwarePluginTopicService implements TypeAwarePluginTopicService {

    private static final TransactionalTypeAwarePluginTopicService INSTANCE =
            new TransactionalTypeAwarePluginTopicService();

    private PluginTopicFetchService topicFetchService;
    private PluginTopicModificationService topicModificationService;

    private TransactionalTypeAwarePluginTopicService() {

    }

    public static TransactionalTypeAwarePluginTopicService getInstance() {
        return INSTANCE;
    }



    public void setTopicFetchService(PluginTopicFetchService topicFetchService) {
        this.topicFetchService = topicFetchService;
    }

    public void setTopicModificationService(PluginTopicModificationService topicModificationService) {
        this.topicModificationService = topicModificationService;
    }

    @Override
    public Topic get(Long id, String type) throws NotFoundException {
        Topic topic =  topicFetchService.get(id);
        if (!topic.getType().equals(type)) {
            throw new NotFoundException();
        }
        return topic;
    }

    @Override
    public void updateTopic(Topic topic) throws NotFoundException {
        topicModificationService.updateTopic(topic, null);
    }

    @Override
    public Topic createTopic(Topic topicDto, String bodyText) throws NotFoundException {
        return topicModificationService.createTopic(topicDto, bodyText);
    }

    public void checkViewTopicPermission(Long branchId) {
        topicFetchService.checkViewTopicPermission(branchId);
    }
}
