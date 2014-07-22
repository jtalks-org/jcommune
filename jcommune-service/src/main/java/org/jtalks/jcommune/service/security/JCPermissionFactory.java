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

import org.jtalks.common.model.permissions.JtalksPermission;
import org.jtalks.common.security.acl.JtalksPermissionFactory;
import org.jtalks.jcommune.plugin.api.PluginsPermissionFactory;
import org.springframework.security.acls.domain.PermissionFactory;
import org.springframework.security.acls.model.Permission;
import ru.javatalks.utils.general.Assert;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Mikhail Stryzhonok
 */
public class JCPermissionFactory implements PermissionFactory {
    private JtalksPermissionFactory jtalksPermissionFactory;
    private PluginsPermissionFactory pluginPermissionFactory;

    /**
     * Created to replace permissions from disabled/removed plugins
     */
    private enum NullPermission implements JtalksPermission {
        NULL_PERMISSION("0", "NULL_PERMISSION");

        /**
         * Constructor for leading to general form of permission.Doesn't perform any operation. Permission mask have
         * value <b>0</b> and permission name have value <b>"NULL_PERMISSION"</b> independently from input arguments.
         * @param mask mask of the permission
         * @param name name of the permission
         */
        NullPermission(@Nonnull String mask, @Nonnull String name) {
        }

        @Override
        public String getName() {
            return "NULL_PERMISSION";
        }

        @Override
        public int getMask() {
            return 0;
        }

        @Override
        public String getPattern() {
            return null;
        }
    }

    public JCPermissionFactory(JtalksPermissionFactory jtalksPermissionFactory, PluginsPermissionFactory pluginPermissionFactory) {
        this.jtalksPermissionFactory = jtalksPermissionFactory;
        this.pluginPermissionFactory = pluginPermissionFactory;
    }

    @Override
    public Permission buildFromMask(int mask) {
        Permission permission = jtalksPermissionFactory.buildFromMask(mask);
        if (permission == null) {
            permission = pluginPermissionFactory.buildFromMask(mask);
        }
        if (permission == null) {
            permission = NullPermission.NULL_PERMISSION;
        }
        return permission;
    }

    @Override
    public Permission buildFromName(String name) {
        Permission permission = jtalksPermissionFactory.buildFromName(name);
        if (permission == null) {
            permission = pluginPermissionFactory.buildFromName(name);
        }
        if (permission == null) {
            permission = NullPermission.NULL_PERMISSION;
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
