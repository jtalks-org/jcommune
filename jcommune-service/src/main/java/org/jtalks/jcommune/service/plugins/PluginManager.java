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
package org.jtalks.jcommune.service.plugins;

import org.jtalks.common.model.permissions.JtalksPermission;
import org.jtalks.jcommune.model.plugins.Plugin;
import org.jtalks.jcommune.model.plugins.PluginWithPermissions;

import java.util.ArrayList;
import java.util.List;

/**
 * This class manages plugin's extensions.
 *
 * @author Evgeniy Myslovets
 */
public class PluginManager {

    private PluginLoader pluginLoader;

    /**
     * Constructs {@link org.jtalks.jcommune.service.plugins.PluginManager} with given {@link PluginLoader}
     *
     * @param pluginLoader plugin loader instance
     */
    public PluginManager(PluginLoader pluginLoader) {
        this.pluginLoader = pluginLoader;
    }

    /**
     * @return plugin branch permission for given branch
     */
    public <T extends JtalksPermission> List<T> getPluginsBranchPermissions() {
        List<Plugin> plugins = pluginLoader.getPlugins();
        List<T> branchPermissions = new ArrayList<>();
        for (Plugin plugin : plugins) {
            if (plugin instanceof PluginWithPermissions && plugin.isEnabled()) {
                branchPermissions.addAll((List<T>) ((PluginWithPermissions) plugin).getBranchPermissions());
            }
        }
        return branchPermissions;
    }

    /**
     * @return plugin general permission for given component
     */
    public <T extends JtalksPermission> List<T> getPluginsGeneralPermissions() {
        List<Plugin> plugins = pluginLoader.getPlugins();
        List<T> generalPermissions = new ArrayList<>();
        for (Plugin plugin : plugins) {
            if (plugin instanceof PluginWithPermissions && plugin.isEnabled()) {
                generalPermissions.addAll((List<T>) ((PluginWithPermissions) plugin).getGeneralPermissions());
            }
        }
        return generalPermissions;
    }

    /**
     * @return plugin profile permission for given branch
     */
    public <T extends JtalksPermission> List<T> getPluginsProfilePermissions() {
        List<Plugin> plugins = pluginLoader.getPlugins();
        List<T> profilePermissions = new ArrayList<>();
        for (Plugin plugin : plugins) {
            if (plugin instanceof PluginWithPermissions && plugin.isEnabled()) {
                profilePermissions.addAll((List<T>) ((PluginWithPermissions) plugin).getProfilePermissions());
            }
        }
        return profilePermissions;
    }
}
