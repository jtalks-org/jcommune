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
package org.jtalks.jcommune.plugin.api;

import org.springframework.security.acls.domain.PermissionFactory;
import org.springframework.security.acls.model.Permission;

import java.util.*;

/**
 * Factory for permissions provided by plugins. Currently works only with branch permissions but in future may be
 * supplemented to work with other permission types
 *
 * @author Mikhail Styzhonok
 */
public class PluginsPermissionFactory implements PermissionFactory {
    private PluginPermissionManager pluginPermissionManager;

    public PluginsPermissionFactory(PluginPermissionManager pluginPermissionManager) {
        this.pluginPermissionManager = pluginPermissionManager;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Permission buildFromMask(int mask) {
        return pluginPermissionManager.findPluginsBranchPermissionByMask(mask);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Permission buildFromName(String name) {
        return pluginPermissionManager.findPluginsBranchPermissionByName(name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Permission> buildFromNames(List<String> names) {
        List<Permission> resultingPermissions = new ArrayList<>();
        for (String name : names) {
            Permission permission = pluginPermissionManager.findPluginsBranchPermissionByName(name);
            if (permission != null) {
                resultingPermissions.add(permission);
            }
        }
        return resultingPermissions;
    }
}
