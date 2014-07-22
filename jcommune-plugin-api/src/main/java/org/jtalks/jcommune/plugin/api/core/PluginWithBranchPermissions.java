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
package org.jtalks.jcommune.plugin.api.core;

import org.jtalks.common.model.permissions.JtalksPermission;

import java.util.List;

/**
 * Provides plugin which adds own branch permission to common functional
 *
 * @author Evgeniy Myslovets
 */
public interface PluginWithBranchPermissions extends Plugin {

    /**
     * Gets all branch permissions which this plugin provides
     *
     * @return branch permissions for current plugin
     */
    <T extends JtalksPermission> List<T> getBranchPermissions();

    /**
     * Performs branch permission search by mask
     * @param mask permission mask for search
     * @return branch permission with specified mask or <b>null/b> if where no branch permissions with specified mask
     * provided by this plugin
     */
    JtalksPermission getBranchPermissionByMask(int mask);

    /**
     * Performs branch permission search by name
     * @param name permission name for search
     * @return branch permission with specified mask or <b>null/b> if where no branch permissions with specified mask
     * provided by this plugin
     */
    JtalksPermission getBranchPermissionByName(String name);
}
