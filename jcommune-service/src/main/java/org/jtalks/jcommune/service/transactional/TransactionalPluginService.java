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

import org.jtalks.common.service.exceptions.NotFoundException;
import org.jtalks.jcommune.model.dao.PluginDao;
import org.jtalks.jcommune.model.entity.PluginConfiguration;
import org.jtalks.jcommune.model.plugins.Plugin;
import org.jtalks.jcommune.service.PluginService;
import org.jtalks.jcommune.service.dto.PluginActivatingDto;
import org.jtalks.jcommune.service.plugins.PluginLoader;

import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

/**
 * @author Anuar_Nurmakanov
 * @author Evgeny Naumenko
 */
public class TransactionalPluginService extends AbstractTransactionalEntityService<PluginConfiguration, PluginDao>
        implements PluginService {
    private PluginLoader pluginLoader;

    /**
     * @param dao to update, edit plugin configuration.
     * @param pluginLoader to load plugins that are used by the forum
     */
    public TransactionalPluginService(PluginDao dao, PluginLoader pluginLoader) {
        super(dao);
        this.pluginLoader = pluginLoader;
    }

    @Override
    @PreAuthorize("hasPermission(#componentId, 'COMPONENT', 'GeneralPermission.ADMIN')")
    public List<Plugin> getPlugins(long componentId) {
        return pluginLoader.getPlugins();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @PreAuthorize("hasPermission(#componentId, 'COMPONENT', 'GeneralPermission.ADMIN')")
    public void updateConfiguration(PluginConfiguration pluginConfiguration, long componentId) throws NotFoundException {
        String name = pluginConfiguration.getName();
        Plugin result;
        List<Plugin> plugins1 = pluginLoader.getPlugins();
        if (plugins1.isEmpty()) {
            throw new NotFoundException("Plugin " + name + " is not loaded");
        } else {
            result = plugins1.get(0);
        }
        Plugin plugin = result;
        plugin.configure(pluginConfiguration);
        this.getDao().saveOrUpdate(pluginConfiguration);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @PreAuthorize("hasPermission(#componentId, 'COMPONENT', 'GeneralPermission.ADMIN')")
    public PluginConfiguration getPluginConfiguration(String pluginName, long componentId) throws NotFoundException {
        return getDao().get(pluginName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @PreAuthorize("hasPermission(#componentId, 'COMPONENT', 'GeneralPermission.ADMIN')")
    public void updatePluginsActivating(List<PluginActivatingDto> updatedPlugins, long componentId) throws NotFoundException {
        for (PluginActivatingDto updatedPlugin : updatedPlugins) {
            PluginDao pluginDao = getDao();
            String pluginName = updatedPlugin.getPluginName();
            PluginConfiguration configuration = pluginDao.get(pluginName);
            boolean isActivated = updatedPlugin.isActivated();
            configuration.setActive(isActivated);
            pluginDao.saveOrUpdate(configuration);
        }
    }
}
