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
import org.jtalks.common.model.permissions.JtalksPermission;
import org.jtalks.jcommune.plugin.api.filters.TypeFilter;
import org.jtalks.jcommune.plugin.api.plugins.Plugin;
import org.jtalks.jcommune.plugin.api.plugins.PluginWithBranchPermissions;
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
public class PluginPermissionManagerTest {

    @Mock
    private PluginLoader pluginLoader;
    @Mock
    private PluginWithBranchPermissions pluginWithBranchPermissions;

    @BeforeMethod
    public void init() {
        initMocks(this);
        when(pluginLoader.getPlugins(any(TypeFilter.class))).thenReturn(Arrays.asList((Plugin)pluginWithBranchPermissions));
    }

    @Test
    public void getPluginsBranchPermissionsShouldReturnAllBranchPermissionsForEnabledPlugins() {
        List<JtalksPermission> expected = Arrays.asList((JtalksPermission) BranchPermission.VIEW_TOPICS);
        when(pluginWithBranchPermissions.getBranchPermissions()).thenReturn(expected);
        when(pluginWithBranchPermissions.isEnabled()).thenReturn(true);

        PluginPermissionManager pluginPermissionManager = new PluginPermissionManager(pluginLoader);
        List<JtalksPermission> actual = pluginPermissionManager.getPluginsBranchPermissions();

        assertEquals(actual, expected);
    }

    @Test
    public void getPluginsBranchPermissionsShouldNotReturnPermissionsForDisabledPlugins() {
        List<JtalksPermission> permissions = Arrays.asList((JtalksPermission) BranchPermission.VIEW_TOPICS);
        when(pluginWithBranchPermissions.getBranchPermissions()).thenReturn(permissions);
        when(pluginWithBranchPermissions.isEnabled()).thenReturn(false);

        PluginPermissionManager pluginPermissionManager = new PluginPermissionManager(pluginLoader);
        List<JtalksPermission> actual = pluginPermissionManager.getPluginsBranchPermissions();

        assertEquals(actual, Collections.EMPTY_LIST);
    }

    @Test
    public void findPluginBranchPermissionByMaskShouldReturnPermissionIfPluginEnabled() {
        JtalksPermission targetPermission = BranchPermission.VIEW_TOPICS;
        when(pluginWithBranchPermissions.getBranchPermissionByMask(targetPermission.getMask())).thenReturn(targetPermission);
        when(pluginWithBranchPermissions.isEnabled()).thenReturn(true);

        PluginPermissionManager pluginPermissionManager = new PluginPermissionManager(pluginLoader);
        JtalksPermission actual = pluginPermissionManager.findPluginsBranchPermissionByMask(targetPermission.getMask());

        assertEquals(actual, targetPermission);
    }

    @Test
    public void findPluginBranchPermissionByMaskShouldReturnNullIfPluginDisabled() {
        JtalksPermission targetPermission = BranchPermission.VIEW_TOPICS;
        when(pluginWithBranchPermissions.getBranchPermissionByMask(targetPermission.getMask())).thenReturn(targetPermission);
        when(pluginWithBranchPermissions.isEnabled()).thenReturn(false);

        PluginPermissionManager pluginPermissionManager = new PluginPermissionManager(pluginLoader);
        JtalksPermission result = pluginPermissionManager.findPluginsBranchPermissionByMask(targetPermission.getMask());

        assertNull(result);
    }

    @Test
    public void findPluginBranchPermissionByMaskShouldReturnNullIfNoPermissionFound() {
        when(pluginWithBranchPermissions.getBranchPermissionByMask(anyInt())).thenReturn(null);
        when(pluginWithBranchPermissions.isEnabled()).thenReturn(true);

        PluginPermissionManager pluginPermissionManager = new PluginPermissionManager(pluginLoader);
        JtalksPermission actual = pluginPermissionManager.findPluginsBranchPermissionByMask(1);

        assertNull(actual);
    }

    @Test
    public void findPluginBranchPermissionByNameShouldReturnPermissionIfPluginEnabled() {
        JtalksPermission targetPermission = BranchPermission.VIEW_TOPICS;
        when(pluginWithBranchPermissions.getBranchPermissionByName(targetPermission.getName())).thenReturn(targetPermission);
        when(pluginWithBranchPermissions.isEnabled()).thenReturn(true);

        PluginPermissionManager pluginPermissionManager = new PluginPermissionManager(pluginLoader);
        JtalksPermission actual = pluginPermissionManager.findPluginsBranchPermissionByName(targetPermission.getName());

        assertEquals(actual, targetPermission);
    }

    @Test
    public void findPluginBranchPermissionByNameShouldReturnNullIfPluginDisabled() {
        JtalksPermission targetPermission = BranchPermission.VIEW_TOPICS;
        when(pluginWithBranchPermissions.getBranchPermissionByName(targetPermission.getName())).thenReturn(targetPermission);
        when(pluginWithBranchPermissions.isEnabled()).thenReturn(false);

        PluginPermissionManager pluginPermissionManager = new PluginPermissionManager(pluginLoader);
        JtalksPermission result = pluginPermissionManager.findPluginsBranchPermissionByName(targetPermission.getName());

        assertNull(result);
    }

    @Test
    public void findPluginBranchPermissionByNameShouldReturnNullIfNoPermissionFound() {
        when(pluginWithBranchPermissions.getBranchPermissionByName(anyString())).thenReturn(null);
        when(pluginWithBranchPermissions.isEnabled()).thenReturn(true);

        PluginPermissionManager pluginPermissionManager = new PluginPermissionManager(pluginLoader);
        JtalksPermission actual = pluginPermissionManager.findPluginsBranchPermissionByName("anyName");

        assertNull(actual);
    }

}
