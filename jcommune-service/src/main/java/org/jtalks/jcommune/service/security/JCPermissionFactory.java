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
package org.jtalks.jcommune.service.security;

import org.jtalks.common.security.acl.JtalksPermissionFactory;
import org.jtalks.jcommune.service.plugins.PluginPermissionFactory;
import org.springframework.security.acls.domain.PermissionFactory;
import org.springframework.security.acls.model.Permission;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Mikhail Stryzhonok
 */
public class JCPermissionFactory implements PermissionFactory {
    private JtalksPermissionFactory jtalksPermissionFactory;
    private PluginPermissionFactory pluginPermissionFactory;

    public JCPermissionFactory(JtalksPermissionFactory jtalksPermissionFactory, PluginPermissionFactory pluginPermissionFactory) {
        this.jtalksPermissionFactory = jtalksPermissionFactory;
        this.pluginPermissionFactory = pluginPermissionFactory;
    }

    @Override
    public Permission buildFromMask(int mask) {
        Permission permission = jtalksPermissionFactory.buildFromMask(mask);
        if (permission == null) {
            permission = pluginPermissionFactory.buildFromMask(mask);
        }
        return permission;
    }

    @Override
    public Permission buildFromName(String name) {
        Permission permission = jtalksPermissionFactory.buildFromName(name);
        if (permission == null) {
            permission = pluginPermissionFactory.buildFromName(name);
        }
        return permission;
    }

    @Override
    public List<Permission> buildFromNames(List<String> names) {
        List<Permission> permissions = new ArrayList<>();
        permissions.addAll(jtalksPermissionFactory.buildFromNames(names));
        permissions.addAll(pluginPermissionFactory.buildFromNames(names));
        return permissions;
    }
}
