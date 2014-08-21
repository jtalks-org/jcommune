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
package org.jtalks.jcommune.plugin.api.dto;

import org.jtalks.jcommune.model.entity.PluginConfiguration;
import org.jtalks.jcommune.model.entity.PluginProperty;
import org.jtalks.jcommune.plugin.api.core.Plugin;
import org.jtalks.jcommune.plugin.api.exceptions.UnexpectedErrorException;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.testng.Assert.*;

/**
 * @author Mikhail Stryzhonok
 */
public class PluginActivatingListDtoTest {

    public class TestPlugin implements Plugin {

        @Override
        public boolean supportsJCommuneVersion(String version) {
            return false;
        }

        @Override
        public String getName() {
            return "Test plugin";
        }

        @Override
        public State getState() {
            return null;
        }

        @Override
        public List<PluginProperty> getConfiguration() {
            return null;
        }

        @Override
        public List<PluginProperty> getDefaultConfiguration() {
            return null;
        }

        @Override
        public void configure(PluginConfiguration configuration) throws UnexpectedErrorException {

        }

        @Override
        public boolean isEnabled() {
            return true;
        }

        @Override
        public String translateLabel(String code, Locale locale) {
            return null;
        }

    }

    @Test
    public void testValueOf() {
        List<Plugin> plugins = new ArrayList<>();
        plugins.add(new TestPlugin());
        plugins.add(new TestPlugin());

        PluginActivatingListDto result = PluginActivatingListDto.valueOf(plugins);

        assertEquals(result.getActivatingPlugins().size(), plugins.size());
        List<PluginActivatingDto> pluginActivatingListDtos = result.getActivatingPlugins();
        for (int i = 0; i < plugins.size(); i ++) {
            assertPluginActivatingDto(pluginActivatingListDtos.get(i), plugins.get(i));
        }
    }

    private void assertPluginActivatingDto(PluginActivatingDto pluginActivatingDto, Plugin plugin) {
        assertEquals(pluginActivatingDto.isActivated(), plugin.isEnabled());
        assertEquals(pluginActivatingDto.getPluginName(), plugin.getName());
    }

}
