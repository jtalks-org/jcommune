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
package org.jtalks.jcommune.service;

import org.jtalks.common.service.exceptions.NotFoundException;
import org.jtalks.jcommune.model.entity.PluginConfiguration;
import org.jtalks.jcommune.model.plugins.Plugin;
import org.jtalks.jcommune.service.dto.PluginActivatingDto;

import java.util.List;

/**
 * Provides an ability to work with plugins: load plugins, configure plugins,
 * activate/deactivate plugins.
 *
 * @author Anuar Nurmakanov
 */
public interface PluginService extends EntityService<PluginConfiguration> {

    /**
     * Get all plugins which have been added to the forum.
     *
     * @param forumComponentId an identifier of forum component id, that is needed to check permissions
     * @return the list of plugins that have been added to the forum
     */
    List<Plugin> getPlugins(long forumComponentId);

    /**
     * Update configuration of plugin.
     *
     * @param pluginConfiguration contains new configuration of plugin
     * @param forumComponentId    an identifier of forum component id, that is needed to check permissions
     * @throws NotFoundException when configuration passed for non-exists plugin
     */
    void updateConfiguration(PluginConfiguration pluginConfiguration, long forumComponentId) throws NotFoundException;

    /**
     * Get configuration of plugin.
     *
     * @param pluginName       the plugin for which we need configuration
     * @param forumComponentId an identifier of forum component id, that is needed to check permissions
     * @return loaded configuration for passed plugin
     * @throws NotFoundException when passed plugin doesn't exist
     */
    PluginConfiguration getPluginConfiguration(String pluginName, long forumComponentId) throws NotFoundException;

    /**
     * Activate/deactivate plugins.
     *
     * @param pluginActivatingDtoList contains the list of plugins that should be activated/deactivated
     * @param forumComponentId        an identifier of forum component id, that is needed to check permissions
     * @throws NotFoundException when one of plugins that should be activated/deactivated doesn't exist
     */
    void updatePluginsActivating(List<PluginActivatingDto> pluginActivatingDtoList,
                                 long forumComponentId) throws NotFoundException;
}
