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

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.jtalks.jcommune.model.dto.UserDto;
import org.jtalks.jcommune.model.entity.PluginProperty;
import org.jtalks.jcommune.model.plugins.RegistrationPlugin;
import org.jtalks.jcommune.model.plugins.StatefullPlugin;
import org.jtalks.jcommune.model.plugins.exceptions.NoConnectionException;
import org.jtalks.jcommune.model.plugins.exceptions.UnexpectedErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.velocity.VelocityEngineUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.regex.Pattern;

import static org.jtalks.jcommune.model.entity.PluginProperty.Type.INT;
import static org.jtalks.jcommune.model.entity.PluginProperty.Type.STRING;

/**
 * @author Andrey Pogorelov
 */
public class KaptchaPlugin extends StatefullPlugin implements RegistrationPlugin {
    private static final Logger LOGGER = LoggerFactory.getLogger(KaptchaPlugin.class);
    private static final String WIDTH_PROPERTY = "Width";
    private static final String HEIGHT_PROPERTY = "Height";
    private static final String LENGTH_PROPERTY = "Length";
    private static final String POSSIBLE_SYMBOLS_PROPERTY = "Possible Symbols";
    private List<PluginProperty> pluginProperties;
    private KaptchaPluginService service;

    @Override
    protected Map<PluginProperty, String> applyConfiguration(List<PluginProperty> properties) {
        int width = 0;
        int height = 0;
        int length = 0;
        String possibleSymbols = "";
        for (PluginProperty property : properties) {
            if (WIDTH_PROPERTY.equalsIgnoreCase(property.getName())) {
                width = Integer.valueOf(property.getValue());
            } else if (HEIGHT_PROPERTY.equalsIgnoreCase(property.getName())) {
                height = Integer.valueOf(property.getValue());
            } else if (LENGTH_PROPERTY.equalsIgnoreCase(property.getName())) {
                length = Integer.valueOf(property.getValue());
            } else if (POSSIBLE_SYMBOLS_PROPERTY.equalsIgnoreCase(property.getName())) {
                possibleSymbols = property.getValue();
            }
        }
        if (width < 1 || height < 1 || length < 1 || possibleSymbols.length() < 1) {
            // this should be returned as a map, but this mechanism should be implemented in the plugin API first
            throw new RuntimeException(
                    "Can't apply configuration: Width, height, length and possible symbols should not be empty.");
        }
        service = new KaptchaPluginService(properties);
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
        PluginProperty length = new PluginProperty(LENGTH_PROPERTY, INT, "50");
        PluginProperty possibleSymbols = new PluginProperty(POSSIBLE_SYMBOLS_PROPERTY, STRING, "0123456789");
        return Arrays.asList(width, height, length, possibleSymbols);
    }

    @Override
    public Map<String, String> registerUser(UserDto userDto) throws NoConnectionException, UnexpectedErrorException {
        return new HashMap<>();
    }

    @Override
    public Map<String, String> validateUser(UserDto userDto) throws NoConnectionException, UnexpectedErrorException {
        return new HashMap<>();
    }

    @Override
    public String getHtml(HttpServletRequest request) {
        SecurityContextHolder.getContext();
        ResourceBundle resourceBundle  = ResourceBundle.getBundle("org.jtalks.jcommune.plugins.kaptcha.messages", Locale.ENGLISH);
        Properties properties = new Properties();
        properties.put("resource.loader", "class");
        properties.put("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        VelocityEngine engine = new VelocityEngine(properties);
        engine.init();

        return VelocityEngineUtils.mergeTemplateIntoString(
                engine,"org/jtalks/jcommune/plugins/kaptcha/template/captcha.vm",
                "UTF-8", new HashMap<String, Object>());
    }
}
