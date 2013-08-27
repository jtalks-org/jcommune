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

import org.jtalks.jcommune.model.dto.UserDto;
import org.jtalks.jcommune.model.entity.PluginConfigurationProperty;
import org.jtalks.jcommune.model.plugins.SimpleAuthenticationPlugin;
import org.jtalks.jcommune.model.plugins.StatefullPlugin;
import org.jtalks.jcommune.model.plugins.exceptions.NoConnectionException;
import org.jtalks.jcommune.model.plugins.exceptions.UnexpectedErrorException;
import org.jtalks.jcommune.plugin.auth.poulpe.service.PoulpeAuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.jtalks.jcommune.model.entity.PluginConfigurationProperty.Type.STRING;

/**
 * Provides user registration and authentication services via Poulpe.
 *
 * @author Andrey Pogorelov
 */
public class PoulpeAuthPlugin extends StatefullPlugin
        implements SimpleAuthenticationPlugin {

    private static final Logger LOGGER = LoggerFactory.getLogger(PoulpeAuthPlugin.class);
    private PoulpeAuthService service;
    List<PluginConfigurationProperty> pluginProperties;

    @Override
    public Map<String, String> registerUser(UserDto userDto, Boolean dryRun)
            throws NoConnectionException, UnexpectedErrorException {
        try {
            return service.registerUser(userDto, dryRun);
        } catch (IOException | JAXBException e) {
            LOGGER.error("Parse response error", e);
            throw new UnexpectedErrorException(e);
        } catch (NoConnectionException e) {
            LOGGER.error("Can't connect to Poulpe", e);
            throw e;
        }
    }

    @Override
    public Map<String, String> authenticate(String login, String password)
            throws UnexpectedErrorException, NoConnectionException {
        try {
            return service.authenticate(login, password);
        } catch (IOException | JAXBException e) {
            LOGGER.error("Parse response error", e);
            throw new UnexpectedErrorException(e);
        } catch (NoConnectionException e) {
            LOGGER.error("Can't connect to Poulpe", e);
            throw e;
        }
    }

    @Override
    public boolean supportsJCommuneVersion(String version) {
        return true;
    }

    @Override
    public String getName() {
        return "Poulpe Auth Plugin";
    }

    @Override
    public List<PluginConfigurationProperty> getConfiguration() {
        return pluginProperties;
    }

    @Override
    public List<PluginConfigurationProperty> getDefaultConfiguration() {
        PluginConfigurationProperty url = new PluginConfigurationProperty("URL", STRING, "http://localhost:8080");
        url.setName("Url");
        PluginConfigurationProperty login = new PluginConfigurationProperty("LOGIN", STRING, "user");
        login.setName("Login");
        PluginConfigurationProperty password = new PluginConfigurationProperty("PASSWORD", STRING, "1234");
        password.setName("Password");
        return Arrays.asList(url, login, password);
    }

    @Override
    protected Map<PluginConfigurationProperty, String> applyConfiguration(List<PluginConfigurationProperty> properties) {

        String url = null;
        String login = null;
        String password = null;
        for (PluginConfigurationProperty property : properties) {
            if ("Url".equalsIgnoreCase(property.getName())) {
                url = property.getValue();
            } else if ("Login".equalsIgnoreCase(property.getName())) {
                login = property.getValue();
            } else if ("Password".equalsIgnoreCase(property.getName())) {
                password = property.getValue();
            }
        }
        if (url != null && login != null && password != null) {
            service = new PoulpeAuthService(url, login, password);
            pluginProperties = properties;
        } else {
            throw new RuntimeException();
        }
        return Collections.EMPTY_MAP;
    }

    public void setPluginService(PoulpeAuthService service) {
        this.service = service;
    }
}
