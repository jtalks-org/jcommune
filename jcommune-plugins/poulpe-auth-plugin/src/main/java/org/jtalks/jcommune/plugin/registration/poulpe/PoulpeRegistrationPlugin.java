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

package org.jtalks.jcommune.plugin.registration.poulpe;

import org.jtalks.jcommune.model.entity.PluginConfiguration;
import org.jtalks.jcommune.model.entity.PluginConfigurationProperty;

import org.jtalks.jcommune.model.plugins.SimpleRegistrationPlugin;
import org.jtalks.jcommune.model.plugins.StatefullPlugin;
import org.jtalks.jcommune.model.plugins.exceptions.NoConnectionException;
import org.jtalks.jcommune.model.plugins.exceptions.UnexpectedErrorException;
import org.jtalks.jcommune.plugin.registration.poulpe.controller.PoulpeRegistrationController;
import org.jtalks.jcommune.plugin.registration.poulpe.service.PoulpeRegistrationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.config.AopNamespaceHandler;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.*;

/**
 * Provides user registration service via Poulpe.
 *
 * @author Andrey Pogorelov
 */
public class PoulpeRegistrationPlugin extends StatefullPlugin implements SimpleRegistrationPlugin {

    private static final Logger LOGGER = LoggerFactory.getLogger(PoulpeRegistrationPlugin.class);
    private PoulpeRegistrationService service;
    private PluginConfiguration pluginConfiguration;
    private State state;

    static {
        LOGGER.warn("PoulpeRegistrationPlugin Class initialized");
    }

    public PoulpeRegistrationPlugin() {
        LOGGER.warn("PoulpeRegistrationPlugin initialized");
        loadContext();
        PluginConfiguration conf = new PluginConfiguration();
        conf.setProperties(getDefaultConfiguration());
        configure(conf);
    }

    @Override
    public Map<String, String> registerUser(String username, String password, String email)
            throws NoConnectionException, UnexpectedErrorException {
        try {
            return service.registerUser(username, password, email);
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
        return pluginConfiguration.getProperties();
    }

    @Override
    public List<PluginConfigurationProperty> getDefaultConfiguration() {
        PluginConfigurationProperty url =
                new PluginConfigurationProperty(PluginConfigurationProperty.Type.STRING,
                        "http://localhost/rest/private/user");
        url.setName("PoulpeUrl");
        PluginConfigurationProperty login
                = new PluginConfigurationProperty(PluginConfigurationProperty.Type.STRING, "user");
        login.setName("Login");
        PluginConfigurationProperty password
                = new PluginConfigurationProperty(PluginConfigurationProperty.Type.STRING, "1234");
        login.setName("Password");
        return Arrays.asList(url, login, password);
    }

    @Override
    public void configure(PluginConfiguration configuration) {
        LOGGER.debug("Plugin {} start configuring", this.getName());
        try {
            loadConfiguration(configuration.getProperties());
            this.pluginConfiguration = configuration;
            if (configuration.isActive()){
                state = State.ENABLED;
                LOGGER.debug("Plugin {} is configured and activated", this.getName());
            } else {
                state = State.CONFIGURED;
                LOGGER.debug("Plugin {} is configured", this.getName());
            }
        } catch (RuntimeException e) {
            state = State.IN_ERROR;
            LOGGER.warn("Plugin {} configuration failed", this.getName(), e);
        }

    }


    private void loadContext() {
        LOGGER.warn("Plugin {} start load context", this.getName());
        ApplicationContext appContext = new ClassPathXmlApplicationContext("/applicationContext.xml");
        PoulpeRegistrationController controller =
                (PoulpeRegistrationController) appContext.getBean("registrationPluginController");
        controller.testAop2();
        LOGGER.warn("Plugin {} end load context", this.getName());

    }

    @Override
    protected Map<PluginConfigurationProperty, String> applyConfiguration(List<PluginConfigurationProperty> properties) {
        this.pluginConfiguration.setProperties(properties);
        return Collections.emptyMap();
    }

    @Override
    public State getState() {
        return state;
    }

    private void loadConfiguration(List<PluginConfigurationProperty> properties) throws RuntimeException {
        String url = null;
        String login = null;
        String password = null;
        for (PluginConfigurationProperty property : properties) {
            if (property.getName().equalsIgnoreCase("PoulpeUrl")) {
                url = property.getValue();
            } else if (property.getName().equalsIgnoreCase("Login")) {
                login = property.getValue();
            } else if (property.getName().equalsIgnoreCase("Password")) {
                password = property.getValue();
            }
        }
        if(url != null && login != null && password != null) {
            service = new PoulpeRegistrationService(url, login, password);
            pluginConfiguration.setActive(true);
        } else {
            throw new RuntimeException();
        }
    }
}
