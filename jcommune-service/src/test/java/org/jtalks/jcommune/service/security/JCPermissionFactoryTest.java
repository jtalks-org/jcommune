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



import org.jtalks.common.model.permissions.BranchPermission;
import org.jtalks.common.model.permissions.JtalksPermission;
import org.jtalks.jcommune.plugin.api.PluginsPermissionFactory;
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
public class JCPermissionFactoryTest {
    @Mock
    private JtalksPermissionFactory jtalksPermissionFactory;
    @Mock
    private PluginsPermissionFactory pluginsPermissionFactory;

    @BeforeMethod
    public void init() {
        initMocks(this);
    }

    @Test
    public void buildFromMaskShouldReturnPermissionFromCommons() {
        Permission targetPermission = BranchPermission.VIEW_TOPICS;
        when(jtalksPermissionFactory.buildFromMask(targetPermission.getMask())).thenReturn(targetPermission);

        JCPermissionFactory permissionFactory = new JCPermissionFactory(jtalksPermissionFactory,pluginsPermissionFactory);
        Permission result = permissionFactory.buildFromMask(targetPermission.getMask());

        assertEquals(result, targetPermission);
        verify(pluginsPermissionFactory, never()).buildFromMask(anyInt());
    }

    @Test
    public void buildFromMaskShouldReturnPluginPermissionWhenCommonNotFound() {
        Permission targetPermission = BranchPermission.VIEW_TOPICS;
        when(jtalksPermissionFactory.buildFromMask(anyInt())).thenReturn(null);
        when(pluginsPermissionFactory.buildFromMask(targetPermission.getMask())).thenReturn(targetPermission);

        JCPermissionFactory permissionFactory = new JCPermissionFactory(jtalksPermissionFactory,pluginsPermissionFactory);
        Permission result = permissionFactory.buildFromMask(targetPermission.getMask());

        assertEquals(result, targetPermission);
        verify(jtalksPermissionFactory).buildFromMask(targetPermission.getMask());
    }

    @Test
    public void buildFromMaskShouldReturnAnyNotNullDefaultValueWhenNoPermissionFound() {
        Permission targetPermission = BranchPermission.VIEW_TOPICS;
        when(jtalksPermissionFactory.buildFromMask(anyInt())).thenReturn(null);
        when(pluginsPermissionFactory.buildFromMask(anyInt())).thenReturn(null);

        JCPermissionFactory permissionFactory = new JCPermissionFactory(jtalksPermissionFactory,pluginsPermissionFactory);
        Permission result = permissionFactory.buildFromMask(targetPermission.getMask());

        assertNotNull(result);
        verify(jtalksPermissionFactory).buildFromMask(targetPermission.getMask());
        verify(pluginsPermissionFactory).buildFromMask(targetPermission.getMask());
    }

    @Test
    public void buildFromNameShouldReturnPermissionFromCommons() {
        JtalksPermission targetPermission = BranchPermission.VIEW_TOPICS;
        when(jtalksPermissionFactory.buildFromName(targetPermission.getName())).thenReturn(targetPermission);

        JCPermissionFactory permissionFactory = new JCPermissionFactory(jtalksPermissionFactory,pluginsPermissionFactory);
        Permission result = permissionFactory.buildFromName(targetPermission.getName());

        assertEquals(result, targetPermission);
        verify(pluginsPermissionFactory, never()).buildFromName(anyString());
    }

    @Test
    public void buildFromNameShouldReturnPluginPermissionWhenCommonNotFound() {
        JtalksPermission targetPermission = BranchPermission.VIEW_TOPICS;
        when(jtalksPermissionFactory.buildFromName(anyString())).thenReturn(null);
        when(pluginsPermissionFactory.buildFromName(targetPermission.getName())).thenReturn(targetPermission);

        JCPermissionFactory permissionFactory = new JCPermissionFactory(jtalksPermissionFactory,pluginsPermissionFactory);
        Permission result = permissionFactory.buildFromName(targetPermission.getName());

        assertEquals(result, targetPermission);
        verify(jtalksPermissionFactory).buildFromName(targetPermission.getName());
    }

    @Test
    public void buildFromNameShouldReturnAnyNotNullDefaultValueWhenNoPermissionFound() {
        JtalksPermission targetPermission = BranchPermission.VIEW_TOPICS;
        when(jtalksPermissionFactory.buildFromName(anyString())).thenReturn(null);
        when(pluginsPermissionFactory.buildFromName(anyString())).thenReturn(null);

        JCPermissionFactory permissionFactory = new JCPermissionFactory(jtalksPermissionFactory,pluginsPermissionFactory);
        Permission result = permissionFactory.buildFromName(targetPermission.getName());

        assertNotNull(result);
        verify(jtalksPermissionFactory).buildFromName(targetPermission.getName());
        verify(pluginsPermissionFactory).buildFromName(targetPermission.getName());
    }

    @Test
    public void buildFromNamesShouldReturnBothOfCommonAndPluginPermissions() {
        JtalksPermission commonPermission = BranchPermission.VIEW_TOPICS;
        JtalksPermission pluginPermission = BranchPermission.CLOSE_TOPICS;
        List<String> names = Arrays.asList(commonPermission.getName(), pluginPermission.getName());
        List<Permission> expected = Arrays.asList((Permission)commonPermission, (Permission)pluginPermission);
        when(jtalksPermissionFactory.buildFromNames(names)).thenReturn(Arrays.asList((Permission)commonPermission));
        when(pluginsPermissionFactory.buildFromNames(names)).thenReturn(Arrays.asList((Permission)pluginPermission));

        JCPermissionFactory permissionFactory = new JCPermissionFactory(jtalksPermissionFactory,pluginsPermissionFactory);
        List<Permission> actual = permissionFactory.buildFromNames(names);

        assertEquals(actual, expected);
        verify(jtalksPermissionFactory).buildFromNames(names);
        verify(pluginsPermissionFactory).buildFromNames(names);
    }

    @Test
    public void  buildFromNamesShouldReturnEmptyListIfNoPermissionsFound() {
        when(jtalksPermissionFactory.buildFromNames(any(List.class))).thenReturn(Collections.EMPTY_LIST);
        when(pluginsPermissionFactory.buildFromNames(any(List.class))).thenReturn(Collections.EMPTY_LIST);
        List<String> names = Arrays.asList("permission1", "permission2");

        JCPermissionFactory permissionFactory = new JCPermissionFactory(jtalksPermissionFactory,pluginsPermissionFactory);
        List<Permission> result = permissionFactory.buildFromNames(names);

        assertEquals(result, Collections.EMPTY_LIST);
        verify(jtalksPermissionFactory).buildFromNames(names);
        verify(pluginsPermissionFactory).buildFromNames(names);
    }
}
