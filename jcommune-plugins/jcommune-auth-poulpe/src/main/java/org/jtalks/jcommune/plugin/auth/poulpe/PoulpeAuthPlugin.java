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
package org.jtalks.jcommune.plugin.auth.poulpe;

import org.jtalks.jcommune.model.entity.PluginConfigurationProperty;
import org.jtalks.jcommune.model.plugins.SimpleAuthenticationPlugin;
import org.jtalks.jcommune.model.plugins.StatefullPlugin;
import org.jtalks.jcommune.model.plugins.exceptions.NoConnectionException;
import org.jtalks.jcommune.model.plugins.exceptions.UnexpectedErrorException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.jtalks.jcommune.model.entity.PluginConfigurationProperty.Type.STRING;

/**
 *
 */
public class PoulpeAuthPlugin extends StatefullPlugin implements SimpleAuthenticationPlugin {

    @Override
    public String getName() {
        return "Poulpe authentication plugin";
    }

    @Override
    public Map<String, String> authenticate(String login, String password) {
        return Collections.EMPTY_MAP;
    }

    @Override
    public Map<String, String> registerUser(String login, String password, String email)
            throws NoConnectionException, UnexpectedErrorException {
        return Collections.EMPTY_MAP;
    }

    @Override
    public boolean supportsJCommuneVersion(String version) {
        return true;
    }

    @Override
    public List<PluginConfigurationProperty> getConfiguration() {
        return Collections.emptyList();
    }

    @Override
    public List<PluginConfigurationProperty> getDefaultConfiguration() {
        PluginConfigurationProperty url = new PluginConfigurationProperty("URL", STRING, "http://localhost:1234");
        PluginConfigurationProperty login = new PluginConfigurationProperty("LOGIN", STRING, "login");
        PluginConfigurationProperty password = new PluginConfigurationProperty("PASSWORD", STRING, "password");
        return Arrays.asList(url, login, password);
    }

    @Override
    protected Map<PluginConfigurationProperty, String> applyConfiguration(List<PluginConfigurationProperty> properties) {
        return Collections.emptyMap();
    }
}
