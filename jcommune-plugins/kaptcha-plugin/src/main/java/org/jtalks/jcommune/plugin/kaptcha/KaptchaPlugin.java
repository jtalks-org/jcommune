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

package org.jtalks.jcommune.plugin.kaptcha;

import org.jtalks.jcommune.model.dto.UserDto;
import org.jtalks.jcommune.model.entity.PluginProperty;
import org.jtalks.jcommune.model.plugins.ExtendedPlugin;
import org.jtalks.jcommune.model.plugins.RegistrationPlugin;
import org.jtalks.jcommune.model.plugins.StatefullPlugin;
import org.jtalks.jcommune.model.plugins.exceptions.NoConnectionException;
import org.jtalks.jcommune.model.plugins.exceptions.UnexpectedErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

import static org.jtalks.jcommune.model.entity.PluginProperty.Type.INT;
import static org.jtalks.jcommune.model.entity.PluginProperty.Type.STRING;

/**
 * Provides refresh, validating captcha, and obtaining captcha as html for registration form.
 * Uses {@link com.google.code.kaptcha} for captcha functionality.
 *
 * @author Andrey Pogorelov
 */
public class KaptchaPlugin extends StatefullPlugin implements RegistrationPlugin, ExtendedPlugin {
    protected static final Logger LOGGER = LoggerFactory.getLogger(KaptchaPlugin.class);
    protected static final String WIDTH_PROPERTY = "Width";
    protected static final String HEIGHT_PROPERTY = "Height";
    protected static final String LENGTH_PROPERTY = "Length";
    protected static final String POSSIBLE_SYMBOLS_PROPERTY = "Possible Symbols";

    private List<PluginProperty> pluginProperties;
    private KaptchaPluginService service;

    @Override
    protected Map<PluginProperty, String> applyConfiguration(List<PluginProperty> properties) {
        int width = 0;
        int height = 0;
        int length = 0;
        String possibleSymbols = "";
        for (PluginProperty property : properties) {
            try {
                switch (property.getName()) {
                    case WIDTH_PROPERTY:
                        width = Integer.valueOf(property.getValue());
                        break;
                    case HEIGHT_PROPERTY:
                        height = Integer.valueOf(property.getValue());
                        break;
                    case LENGTH_PROPERTY:
                        length = Integer.valueOf(property.getValue());
                        break;
                    case POSSIBLE_SYMBOLS_PROPERTY:
                        possibleSymbols = property.getValue();
                        break;
                }
            } catch (NumberFormatException ex) {
                LOGGER.error(property.getValue() + " is not valid value for property " + property.getName(), ex);
            }
        }
        if (width < 1 || height < 1 || length < 1 || possibleSymbols.length() < 1) {
            // this should be returned as a map, but this mechanism should be implemented in the plugin API first
            throw new RuntimeException(
                    "Can't apply configuration: Width, height, length and possible symbols properties should not be empty.");
        }
        service = new KaptchaPluginService(width, height, length, possibleSymbols);
        pluginProperties = properties;
        return new HashMap<>();
    }

    @Override
    public boolean supportsJCommuneVersion(String version) {
        return true;
    }

    @Override
    public String getName() {
        return "Kaptcha";
    }

    @Override
    public List<PluginProperty> getConfiguration() {
        return pluginProperties;
    }

    @Override
    public List<PluginProperty> getDefaultConfiguration() {
        PluginProperty width = new PluginProperty(WIDTH_PROPERTY, INT, "100");
        PluginProperty height = new PluginProperty(HEIGHT_PROPERTY, INT, "50");
        PluginProperty length = new PluginProperty(LENGTH_PROPERTY, INT, "4");
        PluginProperty possibleSymbols = new PluginProperty(POSSIBLE_SYMBOLS_PROPERTY, STRING, "0123456789");
        return Arrays.asList(width, height, length, possibleSymbols);
    }

    @Override
    public String translateLabel(String code, Locale locale) {
        return code;
    }

    @Override
    public Map<String, String> registerUser(UserDto userDto, Long pluginId)
            throws NoConnectionException, UnexpectedErrorException {
        Map<String, String> errors = getService().validateCaptcha(userDto, pluginId);
        getService().removeCurrentCaptcha();
        return errors;
    }

    @Override
    public Map<String, String> validateUser(UserDto userDto, Long pluginId)
            throws NoConnectionException, UnexpectedErrorException {
        return getService().validateCaptcha(userDto, pluginId);
    }

    @Override
    public String getHtml(HttpServletRequest request, String pluginId, Locale locale) {
        return getService().getHtml(request, pluginId, locale);
    }

    @Override
    public Object doAction(String pluginId, String action, HttpServletRequest request, HttpServletResponse response) {
        try {
            if ("refreshCaptcha".equalsIgnoreCase(action)) {
                getService().refreshCaptchaImage(request, response);
                return Boolean.TRUE;
            }
        } catch (IOException ex) {
            LOGGER.error("Exception at writing captcha image {}", ex);
        }
        return Boolean.FALSE;
    }

    protected KaptchaPluginService getService() {
        return this.service;
    }
}
