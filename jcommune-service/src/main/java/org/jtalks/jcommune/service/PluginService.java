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
import org.jtalks.jcommune.plugin.api.core.Plugin;
import org.jtalks.jcommune.plugin.api.core.RegistrationPlugin;
import org.jtalks.jcommune.plugin.api.exceptions.UnexpectedErrorException;
import org.jtalks.jcommune.plugin.api.dto.PluginActivatingDto;
import org.jtalks.jcommune.plugin.api.filters.PluginFilter;

import java.util.List;
import java.util.Map;

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
     * @throws UnexpectedErrorException when any RuntimeException was thrown during plugin configuration
     *                                  or saving configuration in the database
     */
    void updateConfiguration(PluginConfiguration pluginConfiguration, long forumComponentId)
            throws NotFoundException, UnexpectedErrorException;

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
     * Activate/deactivate one particular plugin
     * 
     * @param pluginActivatingDto contains the plugin that should be activated/deactivated
     * @param forumComponentId        an identifier of forum component id, that is needed to check permissions
     * @throws NotFoundException when one of plugins that should be activated/deactivated doesn't exist
     */
    void updatePluginActivating(PluginActivatingDto pluginActivatingDto, long forumComponentId) throws NotFoundException;

    /**
     * Get all enabled registration plugins {@link RegistrationPlugin}.
     *
     * @return registration plugins as pairs pluginId - registrationPlugin
     */
    Map<Long, RegistrationPlugin> getRegistrationPlugins();

    /**
     * Get plugin by specified id and filters {@link PluginFilter}
     *
     * @param pluginId plugin id
     * @param filters applied filters
     * @return plugin
     * @throws NotFoundException if plugin not found
     */
    Plugin getPluginById(String pluginId, PluginFilter... filters) throws NotFoundException;
}
