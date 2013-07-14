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
package org.jtalks.jcommune.web.controller;

import org.jtalks.jcommune.model.entity.PluginConfiguration;
import org.jtalks.jcommune.model.entity.PluginConfigurationProperty;
import org.jtalks.jcommune.model.plugins.Plugin;
import org.jtalks.jcommune.service.PluginService;
import org.jtalks.jcommune.service.exceptions.NotFoundException;
import org.mockito.Mock;
import org.springframework.web.servlet.ModelAndView;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.test.web.ModelAndViewAssert.assertModelAttributeAvailable;
import static org.springframework.test.web.ModelAndViewAssert.assertViewName;
import static org.testng.Assert.assertEquals;

/**
 *
 * @author Anuar Nurmakanov
 */
public class PluginControllerTest {

    @Mock
    private PluginService pluginService;

    private PluginController pluginController;

    @BeforeMethod
    public void init() {
        initMocks(this);
        this.pluginController = new PluginController(pluginService);
    }

    @Test
    public void getPluginsShouldReturnAllPlugins() {
        List<Plugin> expectedPlugins = Arrays.asList((Plugin) new DummyPlugin(), new DummyPlugin());
        when(pluginService.getPlugins()).thenReturn(expectedPlugins);

        ModelAndView pluginsModelAndView = pluginController.getPlugins();

        assertViewName(pluginsModelAndView, "pluginsList");
        assertModelAttributeAvailable(pluginsModelAndView, "plugins");
        List<Plugin> actualPlugins = (List<Plugin>) pluginsModelAndView.getModel().get("plugins");
        assertEquals(actualPlugins, expectedPlugins, "Plugins should be returned from services.");
    }

    @Test
    public void configurePluginShouldMoveToPluginConfigurationPage() throws NotFoundException {
        long configuredPluginId = 25L;
        PluginConfiguration expected = new PluginConfiguration();
        when(pluginService.get(configuredPluginId)).thenReturn(expected);

        ModelAndView pluginConfigModelAndView = pluginController.configurePlugin(configuredPluginId);

        assertViewName(pluginConfigModelAndView, "pluginConfiguration");
        assertModelAttributeAvailable(pluginConfigModelAndView, "plugin");
        PluginConfiguration actualPlugin = (PluginConfiguration) pluginConfigModelAndView.getModel().get("plugin");
        assertEquals(actualPlugin,expected, "Plugin should be returned from services.");
    }

    @Test(expectedExceptions = NotFoundException.class)
    public void configurePluginWhenPluginWasNotFoundShouldShowNotFoundError() throws NotFoundException {
        long nonExistPluginId = 25L;
        when(pluginService.get(nonExistPluginId)).thenThrow(new NotFoundException());

        pluginController.configurePlugin(nonExistPluginId);
    }

    /**
     * Created for tests plugin.
     */
    private static final class DummyPlugin implements Plugin {
        @Override
        public boolean supportsJCommuneVersion(String version) {
            return false;
        }

        @Override
        public String getName() {
            return null;
        }

        @Override
        public List getConfiguration() {
            return null;
        }

        @Override
        public List<PluginConfigurationProperty> getDefaultConfiguration() {
            return null;
        }

        @Override
        public void configure(PluginConfiguration configuration) {

        }

        @Override
        public State getState() {
            return null;
        }
    }
}
