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

import org.jtalks.jcommune.model.entity.PluginConfiguration;
import org.jtalks.jcommune.model.entity.PluginProperty;
import org.jtalks.jcommune.plugin.api.exceptions.UnexpectedErrorException;
import org.mockito.Mock;
import static org.mockito.Mockito.*;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.MockitoAnnotations.initMocks;

/**
 * @author Mikhail Stryzhonok
 */
public class StateFullPluginTest {

    @Mock
    private PluginConfiguration configuration;
    @Mock
    private StatefullPlugin plugin;

    @BeforeMethod
    public void init() throws Exception {
        initMocks(this);
        doCallRealMethod().when(plugin).configure(any(PluginConfiguration.class));
        doCallRealMethod().when(plugin).getState();
    }

    @Test
    public void configureShouldApplyConfiguration() throws Exception{
        List<PluginProperty> properties = Arrays.asList(new PluginProperty());
        when(configuration.getProperties()).thenReturn(properties);
        when(plugin.applyConfiguration(properties)).thenReturn(Collections.<PluginProperty, String>emptyMap());

        plugin.configure(configuration);
        verify(plugin).applyConfiguration(properties);
    }

    @Test(expectedExceptions = UnexpectedErrorException.class)
    public void configureShouldThrowUnexpectedErrorExceptionWhenRuntimeExceptionOccurs() throws Exception{
        List<PluginProperty> properties = Arrays.asList(new PluginProperty());
        when(configuration.getProperties()).thenReturn(properties);
        when(plugin.applyConfiguration(properties)).thenThrow(new RuntimeException());

        plugin.configure(configuration);
    }

    @Test
    public void getStateShouldReturnEnabledStateWhenPluginConfiguredAsActive() throws Exception{
        List<PluginProperty> properties = Arrays.asList(new PluginProperty());
        when(configuration.getProperties()).thenReturn(properties);
        when(configuration.isActive()).thenReturn(true);
        when(plugin.applyConfiguration(properties)).thenReturn(Collections.<PluginProperty, String>emptyMap());

        plugin.configure(configuration);

        assertEquals(plugin.getState(), Plugin.State.ENABLED);
    }

    @Test
    public void getStateShouldReturnConfiguredStateWhenPluginConfiguredAsNotActive() throws Exception{
        List<PluginProperty> properties = Arrays.asList(new PluginProperty());
        when(configuration.getProperties()).thenReturn(properties);
        when(configuration.isActive()).thenReturn(false);
        when(plugin.applyConfiguration(properties)).thenReturn(Collections.<PluginProperty, String>emptyMap());

        plugin.configure(configuration);

        assertEquals(plugin.getState(), Plugin.State.CONFIGURED);
    }

    @Test
    public void getStateShouldReturnInErrorStateWhenConfigurationErrorOccurs() {
        List<PluginProperty> properties = Arrays.asList(new PluginProperty());
        when(configuration.getProperties()).thenReturn(properties);
        when(plugin.applyConfiguration(properties)).thenThrow(new RuntimeException());

        try {
            plugin.configure(configuration);
        } catch (UnexpectedErrorException ex) {
        }

        assertEquals(plugin.getState(), Plugin.State.IN_ERROR);
    }
}
