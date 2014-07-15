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

import org.jtalks.common.model.entity.Branch;
import org.jtalks.common.model.permissions.JtalksPermission;
import org.jtalks.jcommune.model.dto.GroupsPermissions;
import org.jtalks.jcommune.model.plugins.Plugin;
import org.jtalks.jcommune.model.plugins.PluginWithPermissions;
import org.jtalks.jcommune.service.security.PermissionManager;

import java.util.ArrayList;
import java.util.List;

/**
 * This class manages plugin's extensions.
 *
 * @author Evgeniy Myslovets
 */
public class PluginManager {

    private PermissionManager permissionManager;
    private PluginLoader pluginLoader;

    /**
     * Constructs {@link org.jtalks.jcommune.service.plugins.PluginManager} with given
     * {@link PermissionManager} and {@link PluginLoader}
     *
     * @param permissionManager permission manager instance
     * @param pluginLoader plugin loader instance
     */
    public PluginManager(PermissionManager permissionManager, PluginLoader pluginLoader) {
        this.permissionManager = permissionManager;
        this.pluginLoader = pluginLoader;
    }
    /**
     * @param branch object identity
     * @return {@link org.jtalks.jcommune.model.dto.GroupsPermissions<JtalksPermission>} for given branch
     */
    public GroupsPermissions<JtalksPermission> getPluginsPermissionsMapFor(Branch branch) {
        List<Plugin> plugins = pluginLoader.getPlugins();
        List<JtalksPermission> branchPermissions = new ArrayList<>();
        for (Plugin plugin : plugins) {
            if (plugin instanceof PluginWithPermissions && plugin.isEnabled()) {
                branchPermissions.addAll(((PluginWithPermissions) plugin).getBranchPermissions());
            }
        }
        return permissionManager.getPermissionsMapFor(branchPermissions, branch);
    }
}
