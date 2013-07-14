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
import org.jtalks.jcommune.model.entity.PluginConfigurationProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 *
 */
public abstract class StatefullPlugin implements Plugin {

    private static final Logger LOGGER = LoggerFactory.getLogger(StatefullPlugin.class);

    private State state = State.LOADED;

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
     *
     * @param properties
     */
    protected abstract void applyConfiguration(List<PluginConfigurationProperty> properties);

    /**
     * {@inheritDoc}
     */
    @Override
    public State getState() {
        return state;
    }
}
