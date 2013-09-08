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
package org.jtalks.jcommune.model.plugins;

import org.jtalks.jcommune.model.entity.PluginConfiguration;
import org.jtalks.jcommune.model.entity.PluginProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * Base class for plugins that have a typical lifecycle:
 * <p> - When JCommune loads the plugin bytecode it's state is set to LOADED state
 * <p> - Once configured from a database or from defaults plugin becomes CONFIGURED
 * <p> - If plugin has been explicitly turned on from UI it is set to ENABLED
 * <p> - Any problem on previous stages causes plugin to be IN_ERROR
 *
 * Custom implementations may decide to extend Plugin interface directly
 * if more sophisticated state transition logic is necessary
 *
 * @author Evgeny Naumenko
 */
public abstract class StatefullPlugin implements Plugin {

    private static final Logger LOGGER = LoggerFactory.getLogger(StatefullPlugin.class);

    private State state = State.LOADED;

    /**
     * {@inheritDoc}
     */
    @Override
    public void configure(PluginConfiguration configuration) {
        try {
            this.applyConfiguration(configuration.getProperties());
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

    /**
     * Configures plugin with pre-saved values from a database.
     * Implementation should throw an exception from this method if configuration
     * is insufficient, invalid or makes little sense.
     *
     * @param properties configuration for plugin to apply
     * @return configuration errors mapped for PCP given, empty map means OK
     */
    protected abstract Map<PluginProperty, String> applyConfiguration(
            List<PluginProperty> properties);

    /**
     * {@inheritDoc}
     */
    @Override
    public State getState() {
        return state;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEnabled() {
        return state == State.ENABLED;
    }
}
