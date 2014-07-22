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


import org.jtalks.common.model.permissions.BranchPermission;
import org.springframework.security.acls.model.Permission;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.mockito.Mock;

import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.testng.Assert.*;
/**
 * @author Mikhail Stryzhonok
 */
public class PluginsPermissionFactoryTest {

    @Mock
    private PluginPermissionManager pluginPermissionManager;

    @BeforeMethod
    public void init() {
        initMocks(this);
    }

    @Test
    public void buildFromNamesShouldReturnPermissionsAccordingNames() {
        List<String> names = Arrays.asList(BranchPermission.VIEW_TOPICS.getName(), BranchPermission.CLOSE_TOPICS.getName());
        when(pluginPermissionManager.findPluginsBranchPermissionByName(BranchPermission.VIEW_TOPICS.getName()))
                .thenReturn(BranchPermission.VIEW_TOPICS);
        when(pluginPermissionManager.findPluginsBranchPermissionByName(BranchPermission.CLOSE_TOPICS.getName()))
                .thenReturn(BranchPermission.CLOSE_TOPICS);
        List<Permission> expected = Arrays.asList((Permission)BranchPermission.VIEW_TOPICS,
                (Permission)BranchPermission.CLOSE_TOPICS);

        PluginsPermissionFactory permissionFactory = new PluginsPermissionFactory(pluginPermissionManager);
        List<Permission> actual = permissionFactory.buildFromNames(names);

        assertEquals(actual, expected);
    }

    @Test
    public void buildFromNamesResultShouldNotContainNullWhenPermissionNotFound() {
        List<String> names = Arrays.asList(BranchPermission.VIEW_TOPICS.getName(), BranchPermission.CLOSE_TOPICS.getName());
        when(pluginPermissionManager.findPluginsBranchPermissionByName(BranchPermission.VIEW_TOPICS.getName()))
                .thenReturn(BranchPermission.VIEW_TOPICS);
        when(pluginPermissionManager.findPluginsBranchPermissionByName(BranchPermission.CLOSE_TOPICS.getName())).thenReturn(null);

        PluginsPermissionFactory permissionFactory = new PluginsPermissionFactory(pluginPermissionManager);
        List<Permission> result = permissionFactory.buildFromNames(names);

        assertFalse(result.contains(null));
    }

    @Test
    public void buildFromNamesShouldReturnEmptyListWhenNoPermissionsFound() {
        List<String> names = Arrays.asList(BranchPermission.VIEW_TOPICS.getName(), BranchPermission.CLOSE_TOPICS.getName());
        when(pluginPermissionManager.findPluginsBranchPermissionByName(anyString())).thenReturn(null);

        PluginsPermissionFactory permissionFactory = new PluginsPermissionFactory(pluginPermissionManager);
        List<Permission> result = permissionFactory.buildFromNames(names);

        assertEquals(result, Collections.EMPTY_LIST);
    }
}
