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

import org.jtalks.common.model.entity.Component;
import org.jtalks.common.service.exceptions.NotFoundException;
import org.jtalks.jcommune.model.entity.PluginConfiguration;
import org.jtalks.jcommune.model.entity.PluginProperty;
import org.jtalks.jcommune.plugin.api.core.Plugin;
import org.jtalks.jcommune.plugin.api.exceptions.UnexpectedErrorException;
import org.jtalks.jcommune.service.ComponentService;
import org.jtalks.jcommune.service.PluginService;
import org.jtalks.jcommune.service.UserService;
import org.jtalks.jcommune.web.dto.json.JsonResponse;
import org.jtalks.jcommune.web.dto.json.JsonResponseStatus;
import org.jtalks.jcommune.plugin.api.dto.PluginActivatingDto;
import org.jtalks.jcommune.plugin.api.filters.NameFilter;
import org.jtalks.jcommune.plugin.api.PluginLoader;
import org.mockito.Mock;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.servlet.ModelAndView;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.*;

import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.test.web.ModelAndViewAssert.assertModelAttributeAvailable;
import static org.springframework.test.web.ModelAndViewAssert.assertViewName;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * @author Anuar Nurmakanov
 */
public class PluginControllerTest {

    @Mock
    private PluginService pluginService;
    @Mock
    private ComponentService componentService;
    @Mock
    private PluginLoader pluginLoader;
    @Mock
    private UserService userService;

    private PluginController pluginController;

    @BeforeMethod
    public void init() {
        initMocks(this);
        this.pluginController = new PluginController(pluginService, componentService, pluginLoader, userService);
    }

    @Test
    public void getPluginsShouldReturnAllPlugins() {
        long componentId = 25L;
        Component component = new Component();
        component.setId(componentId);
        when(componentService.getComponentOfForum()).thenReturn(component);
        List<Plugin> expectedPlugins = Arrays.asList((Plugin) new DummyPlugin(), new DummyPlugin());
        when(pluginService.getPlugins(componentId)).thenReturn(expectedPlugins);

        ModelAndView pluginsModelAndView = pluginController.getPlugins();

        assertViewName(pluginsModelAndView, "plugin/pluginList");
        assertModelAttributeAvailable(pluginsModelAndView, "plugins");
        assertModelAttributeAvailable(pluginsModelAndView, "pluginsActivatingListDto");
        List<Plugin> actualPlugins = (List<Plugin>) pluginsModelAndView.getModel().get("plugins");
        assertEquals(actualPlugins, expectedPlugins, "Plugins should be returned from services.");
    }

    @Test
    public void startConfiguringPluginShouldMoveToPluginConfigurationPage() throws NotFoundException {
        String configuredPluginName = "plugin";
        PluginConfiguration expectedConfiguration = new PluginConfiguration();
        long componentId = 25L;
        Component component = new Component();
        component.setId(componentId);
        List<Plugin> pluginList = Arrays.asList((Plugin) new DummyPlugin());

        when(componentService.getComponentOfForum()).thenReturn(component);
        when(pluginService.getPluginConfiguration(configuredPluginName, componentId)).thenReturn(expectedConfiguration);
        when(pluginLoader.getPlugins(new NameFilter(configuredPluginName))).thenReturn(pluginList);

        ModelAndView pluginConfigModelAndView = pluginController.startConfiguringPlugin(configuredPluginName);

        assertViewName(pluginConfigModelAndView, "plugin/pluginConfiguration");
        assertModelAttributeAvailable(pluginConfigModelAndView, "pluginConfiguration");
        assertModelAttributeAvailable(pluginConfigModelAndView, "labelsTranslation");
        PluginConfiguration actualPluginConfiguration = (PluginConfiguration) pluginConfigModelAndView.getModel().get("pluginConfiguration");
        assertEquals(actualPluginConfiguration, expectedConfiguration, "Plugin should be returned from services.");
    }

    @Test(expectedExceptions = NotFoundException.class)
    public void startConfiguringPluginWhenPluginWasNotFoundShouldShowNotFoundError() throws NotFoundException {
        long componentId = 25L;
        Component component = new Component();
        component.setId(componentId);
        when(componentService.getComponentOfForum()).thenReturn(component);
        String nonExistPluginName = "non-exist";
        when(pluginService.getPluginConfiguration(nonExistPluginName, componentId)).thenThrow(new NotFoundException());

        pluginController.startConfiguringPlugin(nonExistPluginName);
    }

    @Test
    public void updateConfigurationShouldUpdateItByCallingServiceLayer()
            throws NotFoundException, UnexpectedErrorException {
        long componentId = 25L;
        Component component = new Component();
        component.setId(componentId);
        when(componentService.getComponentOfForum()).thenReturn(component);
        String pluginName = "plugin";
        PluginConfiguration newConfiguration = new PluginConfiguration();
        newConfiguration.setName(pluginName);

        Model model = new ExtendedModelMap();
        ModelAndView mav = pluginController.updateConfiguration(model, newConfiguration);

        assertViewName(mav, "redirect:/plugins/configure/" + pluginName);
        verify(pluginService).updateConfiguration(newConfiguration, componentId);
    }

    @Test
    public void updateConfigurationShouldReturnConfigurationPageWithErrorWhenConfigurationWasFailed()
            throws NotFoundException, UnexpectedErrorException {
        PluginConfiguration newConfiguration = createFailingConfiguration();

        Model model = new ExtendedModelMap();
        ModelAndView mav = pluginController.updateConfiguration(model, newConfiguration);

        assertViewName(mav, "plugin/pluginConfiguration");
        assertTrue(model.containsAttribute("pluginConfiguration"));
    }

    private PluginConfiguration createFailingConfiguration() throws NotFoundException, UnexpectedErrorException {
        long componentId = 25L;
        Component component = new Component();
        component.setId(componentId);
        when(componentService.getComponentOfForum()).thenReturn(component);
        String pluginName = "plugin";
        PluginConfiguration newConfiguration = new PluginConfiguration();
        newConfiguration.setName(pluginName);

        doThrow(new UnexpectedErrorException(new IllegalArgumentException("Testing exception!")))
                .when(pluginService).updateConfiguration(newConfiguration, componentId);
        return newConfiguration;
    }

    @Test
    public void updateEnablingShouldUpdatePassedPlugin() throws NotFoundException {
        long componentId = 25L;
        Component component = new Component();
        component.setId(componentId);
        when(componentService.getComponentOfForum()).thenReturn(component);
        PluginActivatingDto pluginActivatingDto = new PluginActivatingDto("Dummy plugin", true);
        
        String expectedStatus = new JsonResponse(JsonResponseStatus.SUCCESS).getStatus().name();
        
        String actualStatus = pluginController.activatePlugin(pluginActivatingDto.getPluginName(), pluginActivatingDto.isActivated()).getStatus().name();
        
        assertEquals(actualStatus, expectedStatus);
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
        public List<PluginProperty> getDefaultConfiguration() {
            return null;
        }

        @Override
        public void configure(PluginConfiguration configuration) {

        }

        @Override
        public State getState() {
            return null;
        }

        @Override
        public boolean isEnabled() {
            return false;
        }

        @Override
        public String translateLabel(String code, Locale locale) {
            return "translation";
        }
    }
}
