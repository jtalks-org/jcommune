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
package org.jtalks.jcommune.test.service;

import groovy.transform.CompileStatic;
import org.jtalks.common.model.entity.Entity;
import org.jtalks.common.model.entity.Group;
import org.jtalks.common.model.permissions.JtalksPermission;
import org.jtalks.jcommune.model.dto.PermissionChanges;
import org.jtalks.jcommune.service.security.PermissionManager;

import java.util.Collections;

import static com.google.common.collect.Lists.newArrayList;

/**
 * @author Mikhail Stryzhonok
 */
@CompileStatic
public class PermissionGranter {
    private PermissionManager permissionManager;
    private Group group;

    public PermissionGranter(PermissionManager permissionManager, Group group) {
        this.permissionManager = permissionManager;
        this.group = group;
    }

    public PermissionGranter withPermissionOn(Entity entity, JtalksPermission permission) {
        PermissionChanges changes = new PermissionChanges(permission,
                newArrayList(group), Collections.<Group>emptyList());
        permissionManager.changeGrants(entity, changes);
        return this;
    }
}
