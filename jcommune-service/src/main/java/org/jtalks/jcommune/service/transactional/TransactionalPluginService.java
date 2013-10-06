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
package org.jtalks.jcommune.service.transactional;

import org.apache.commons.lang.StringUtils;
import org.jtalks.common.service.exceptions.NotFoundException;
import org.jtalks.jcommune.model.dao.PluginConfigurationDao;
import org.jtalks.jcommune.model.entity.PluginConfiguration;
import org.jtalks.jcommune.model.entity.PluginProperty;
import org.jtalks.jcommune.model.plugins.Plugin;
import org.jtalks.jcommune.model.plugins.RegistrationPlugin;
import org.jtalks.jcommune.service.PluginService;
import org.jtalks.jcommune.service.dto.PluginActivatingDto;
import org.jtalks.jcommune.service.plugins.PluginLoader;
import org.jtalks.jcommune.service.plugins.TypeFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Anuar_Nurmakanov
 * @author Evgeny Naumenko
 */
public class TransactionalPluginService extends AbstractTransactionalEntityService<PluginConfiguration,
        PluginConfigurationDao> implements PluginService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionalPluginService.class);
    private PluginLoader pLuginLoader;

    /**
     * Constructs an instance with required fields.
     *
     * @param dao          to save/update configurations for plugins
     * @param pluginLoader to load plugins that have been added to the forum
     */
    public TransactionalPluginService(PluginConfigurationDao dao, PluginLoader pluginLoader) {
        super(dao);
        this.pLuginLoader = pluginLoader;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    @PreAuthorize("hasPermission(#forumComponentId, 'COMPONENT', 'GeneralPermission.ADMIN')")
    public List<Plugin> getPlugins(long forumComponentId) {
        return pLuginLoader.getPlugins();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @PreAuthorize("hasPermission(#forumComponentId, 'COMPONENT', 'GeneralPermission.ADMIN')")
    public void updateConfiguration(PluginConfiguration pluginConfiguration,
                                    long forumComponentId) throws NotFoundException {
        String name = pluginConfiguration.getName();
        List<Plugin> pluginsList = pLuginLoader.getPlugins();
        Plugin willBeConfigured = findPluginByName(pluginsList, name);
        if (willBeConfigured == null) {
            throw new NotFoundException("Plugin " + name + " is not loaded");
        }
        willBeConfigured.configure(pluginConfiguration);
        saveNewPluginConfiguration(pluginConfiguration);
    }

    @Override
    public List<RegistrationPlugin> getRegistrationPlugins(){
        List<RegistrationPlugin> registrationPluginList = new ArrayList<>();
        List<Plugin> plugins =  pLuginLoader.getPlugins(new TypeFilter(RegistrationPlugin.class));
        for(Plugin registrationPlugin : plugins) {
            registrationPluginList.add((RegistrationPlugin) registrationPlugin);
        }
        return registrationPluginList;
    }

    private Plugin findPluginByName(List<Plugin> searchSource, String pluginName) {
        Plugin foundPlugin = null;
        for (Plugin plugin : searchSource) {
            if (StringUtils.equals(pluginName, plugin.getName())) {
                foundPlugin = plugin;
            }
        }
        return foundPlugin;
    }

    private void saveNewPluginConfiguration(PluginConfiguration newPluginConfiguration) {
        List<PluginProperty> properties = newPluginConfiguration.getProperties();
        for (PluginProperty property : properties) {
            property.setPluginConfiguration(newPluginConfiguration);
        }
        this.getDao().updateProperties(newPluginConfiguration.getProperties());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @PreAuthorize("hasPermission(#forumComponentId, 'COMPONENT', 'GeneralPermission.ADMIN')")
    public PluginConfiguration getPluginConfiguration(String pluginName,
                                                      long forumComponentId) throws NotFoundException {
        return getDao().get(pluginName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getPluginId(String pluginName) throws NotFoundException {
        return getDao().get(pluginName).getId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @PreAuthorize("hasPermission(#forumComponentId, 'COMPONENT', 'GeneralPermission.ADMIN')")
    public void updatePluginsActivating(List<PluginActivatingDto> updatedPlugins,
                                        long forumComponentId) throws NotFoundException {
        for (PluginActivatingDto updatedPlugin : updatedPlugins) {
            PluginConfigurationDao pluginConfigurationDao = getDao();
            String pluginName = updatedPlugin.getPluginName();
            PluginConfiguration configuration = pluginConfigurationDao.get(pluginName);
            boolean isActivated = updatedPlugin.isActivated();
            LOGGER.debug("Plugin activation for {} will be changed to {}.", pluginName, isActivated);
            configuration.setActive(isActivated);
            pluginConfigurationDao.saveOrUpdate(configuration);
        }
    }
}
