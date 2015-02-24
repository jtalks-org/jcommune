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
package org.jtalks.jcommune.plugin.api.core;

import org.jtalks.common.model.permissions.JtalksPermission;
import org.jtalks.jcommune.plugin.api.web.dto.CreateTopicBtnDto;


/**
 * Provides plugin which allow add custom topic types to forum
 *
 * @author Mikhail Stryzhonok
 */
public interface TopicPlugin extends PluginWithBranchPermissions {

    /**
     * Creates dto object for create topic button which will be used for creation of topic type provided by this plugin
     * (allow use custom text, tooltip, url)
     *
     * @param branchId id of branch where button should be placed
     * @return dto object for create topic button
     * @see org.jtalks.jcommune.plugin.api.web.dto.CreateTopicBtnDto
     */
    CreateTopicBtnDto getCreateTopicBtnDto(long branchId);

    /**
     * Gets permission which allows to create topic type provided by this plugin
     *
     * @return permission which allows to create topic
     */
    JtalksPermission getCreateTopicPermission();

    /**
     * Gets topic type supported by plugin
     *
     * @return topic type
     */
    String getTopicType();

    /**
     * Gets the permission which allows to add comments to a post for topic provided by this plugin
     *
     * @return he permission which allows to add comments
     */
    JtalksPermission getCommentPermission();

    /**
     * Gets subscribers filter of current plugin
     *
     * @return subscribers filter of current plugin
     */
    SubscribersFilter getSubscribersFilter();
}
