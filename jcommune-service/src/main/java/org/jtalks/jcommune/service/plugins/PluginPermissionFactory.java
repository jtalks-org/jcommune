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
import org.springframework.security.acls.domain.PermissionFactory;
import org.springframework.security.acls.model.Permission;

import java.util.*;

/**
 * @author Mikhail Styzhonok
 */
public class PluginPermissionFactory implements PermissionFactory {
    private final Map<Integer, JtalksPermission> permissionsByMask = new HashMap<>();
    private final Map<String, JtalksPermission> permissionsByName = new HashMap<>();
    private PluginManager pluginManager;
    private boolean isInitialized = false;

    public PluginPermissionFactory(PluginManager pluginManager) {
        this.pluginManager = pluginManager;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Permission buildFromMask(int mask) {
        if (!isInitialized) {
            init();
        }
        return permissionsByMask.get(mask);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Permission buildFromName(String name) {
        if (!isInitialized) {
            init();
        }
        return permissionsByName.get(name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Permission> buildFromNames(List<String> names) {
        if (!isInitialized) {
            init();
        }
        List<Permission> resultingPermissions = new ArrayList<>();
        for (String name : names) {
            if (permissionsByName.containsKey(name)) {
                resultingPermissions.add(buildFromName(name));
            }
        }
        return resultingPermissions;
    }

    /**
     * Initializes the class by loading lists of the permissions from classes like {@link org.jtalks.common.model.permissions.BranchPermission}.
     *
     *
     */
    public void init() {
        isInitialized = true;
        List<JtalksPermission> allPluginPermissions = new LinkedList<>();
        allPluginPermissions.addAll(pluginManager.getPluginsBranchPermissions());
        allPluginPermissions.addAll(pluginManager.getPluginsGeneralPermissions());
        allPluginPermissions.addAll(pluginManager.getPluginsProfilePermissions());
        for (JtalksPermission permission : allPluginPermissions) {
            permissionsByMask.put(permission.getMask(), permission);
            permissionsByName.put(permission.getName(), permission);
        }
    }
}
