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

import java.util.List;
import java.util.Locale;

/**
 *
 */
public interface Plugin {

    /**
     * LOADED - JCommune loads the plugin
     * CONFIGURED - once configured from a database or from defaults plugin becomes
     * ENABLED - plugin is disabled
     * IN_ERROR - plugin has any problem on previous stages
     */
    enum State {LOADED, CONFIGURED, ENABLED, IN_ERROR}

    boolean supportsJCommuneVersion(String version);

    String getName();

    State getState();

    List<PluginProperty> getConfiguration();

    List<PluginProperty> getDefaultConfiguration();

    /**
     * Configuring plugin with specified new parameters
     * @param configuration new parameters for the plugin
     * @throws UnexpectedErrorException when any RuntimeException was thrown during plugin configuration
     */
    void configure(PluginConfiguration configuration) throws UnexpectedErrorException;

    boolean isEnabled();

    /**
     * TODO: Replace passing of locale parameter. Use {@link org.jtalks.jcommune.plugin.api.service.ReadOnlySecurityService}
     * TODO: instead of it for gathering information about current user iside of plugin
     * @param code code for translation
     * @param locale locale for translation
     *
     * @return translated label
     */
    public String translateLabel(String code, Locale locale);
}
