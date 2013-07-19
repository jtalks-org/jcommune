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
package org.jtalks.jcommune.service.transactional;

import org.jtalks.common.service.exceptions.NotFoundException;
import org.jtalks.jcommune.model.dao.PluginDao;
import org.jtalks.jcommune.model.entity.PluginConfiguration;
import org.jtalks.jcommune.service.dto.PluginActivatingDto;
import org.mockito.Mock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * @author Anuar_Nurmakanov
 */
public class TransactionalPluginServiceTest {
    private static final long FAKE_COMPONENT_ID = 25L;

    @Mock
    private PluginDao pluginDao;
    private TransactionalPluginService pluginService;

    @BeforeMethod
    public void init() throws Exception {
        initMocks(this);
        Path tempDirectory = Files.createTempDirectory("test-jtalks-plugins");
        pluginService = new TransactionalPluginService(tempDirectory.toString(), pluginDao);
    }

    @Test
    public void getPluginConfigurationShouldFindItInDatabase() throws NotFoundException {
        PluginConfiguration expectedConfiguration = new PluginConfiguration();
        String pluginName = "plugin";
        when(pluginDao.get(pluginName)).thenReturn(expectedConfiguration);

        PluginConfiguration actualConfiguration = pluginService.getPluginConfiguration(pluginName, FAKE_COMPONENT_ID);

        assertEquals(actualConfiguration, expectedConfiguration, "Plugin configuration should be retrieved from database.");
    }

    @Test(expectedExceptions = NotFoundException.class)
    public void getPluginConfigurationShouldShowErrorWhenPluginDoesNotExist() throws NotFoundException {
        String pluginName = "plugin";
        when(pluginDao.get(pluginName)).thenThrow(new NotFoundException());

        pluginService.getPluginConfiguration(pluginName, FAKE_COMPONENT_ID);
    }

    @Test
    public void updatePluginsEnablingShouldUpdatePluginsEnabling() throws NotFoundException {
        String pluginName = "plugin";
        PluginConfiguration pluginConfiguration = new PluginConfiguration();
        pluginConfiguration.setActive(false);
        when(pluginDao.get(pluginName)).thenReturn(pluginConfiguration);
        List<PluginActivatingDto> pluginsActivatingDtoList = new ArrayList<>();
        pluginsActivatingDtoList.add(new PluginActivatingDto(pluginName, true));

        pluginService.updatePluginsActivating(pluginsActivatingDtoList, FAKE_COMPONENT_ID);

        verify(pluginDao).saveOrUpdate(pluginConfiguration);
        assertTrue(pluginConfiguration.isActive(), "Plugin must be activated.");
    }

    @Test
    public void updatePluginsEnablingShouldUpdateAllPassedPlugins() throws NotFoundException {
        int pluginsCount = 10;
        String pluginName = "plugin";
        PluginConfiguration pluginConfiguration = new PluginConfiguration();
        when(pluginDao.get(anyString())).thenReturn(pluginConfiguration);
        List<PluginActivatingDto> pluginsActivatingDtoList = new ArrayList<>();
        for (int i=0; i< pluginsCount; i++) {
            pluginsActivatingDtoList.add(new PluginActivatingDto(pluginName + i, true));
        }

        pluginService.updatePluginsActivating(pluginsActivatingDtoList, FAKE_COMPONENT_ID);

        verify(pluginDao, times(pluginsCount)).saveOrUpdate(pluginConfiguration);
    }
}
