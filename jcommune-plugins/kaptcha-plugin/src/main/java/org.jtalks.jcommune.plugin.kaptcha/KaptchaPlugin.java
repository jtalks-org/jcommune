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

import com.google.code.kaptcha.Constants;
import com.google.code.kaptcha.Producer;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang.StringUtils;
import org.apache.velocity.app.VelocityEngine;
import org.jtalks.jcommune.model.dto.UserDto;
import org.jtalks.jcommune.model.entity.PluginProperty;
import org.jtalks.jcommune.model.plugins.ExtendedPlugin;
import org.jtalks.jcommune.model.plugins.RegistrationPlugin;
import org.jtalks.jcommune.model.plugins.StatefullPlugin;
import org.jtalks.jcommune.model.plugins.exceptions.NoConnectionException;
import org.jtalks.jcommune.model.plugins.exceptions.UnexpectedErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.velocity.VelocityEngineUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;

import static org.jtalks.jcommune.model.entity.PluginProperty.Type.INT;
import static org.jtalks.jcommune.model.entity.PluginProperty.Type.STRING;

/**
 * @author Andrey Pogorelov
 */
public class KaptchaPlugin extends StatefullPlugin implements RegistrationPlugin, ExtendedPlugin {
    private static final Logger LOGGER = LoggerFactory.getLogger(KaptchaPlugin.class);
    private static final String WIDTH_PROPERTY = "Width";
    private static final String HEIGHT_PROPERTY = "Height";
    private static final String LENGTH_PROPERTY = "Length";
    private static final String POSSIBLE_SYMBOLS_PROPERTY = "Possible Symbols";
    private static final String CAPTCHA_LABEL = "captchaLabel";
    private static final String ALT_CAPTCHA = "altCaptcha";
    private static final String ALT_REFRESH_CAPTCHA = "altRefreshCaptcha";
    private static final String CAPTCHA_PLUGIN_ID = "captchaPluginId";
    private static final String BASE_URL = "BASE_URL";
    private static final String FORM_ELEMENT_ID = "formElementId";
    private static final String PLUGIN_PREFIX = "plugin-";
    private List<PluginProperty> pluginProperties;
    private KaptchaPluginService service;
    private Producer captchaProducer;

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
        captchaProducer = createCaptchaProducer(width, height, length, possibleSymbols);
        service = new KaptchaPluginService(properties);
        pluginProperties = properties;
        return new HashMap<>();
    }

    private Producer createCaptchaProducer(int width, int height, int length, String possibleSymbols) {
        Properties props = new Properties();
        props.setProperty("kaptcha.textproducer.char.string", possibleSymbols);
        props.setProperty("kaptcha.textproducer.char.length", String.valueOf(length));
        props.setProperty("kaptcha.image.width", String.valueOf(width));
        props.setProperty("kaptcha.image.height", String.valueOf(height));
        Config conf = new Config(props);
        DefaultKaptcha captcha = new DefaultKaptcha();
        captcha.setConfig(conf);
        return captcha;
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
    public Map<String, String> registerUser(UserDto userDto, Long pluginId)
            throws NoConnectionException, UnexpectedErrorException {
        return validate(userDto, pluginId);
    }

    @Override
    public Map<String, String> validateUser(UserDto userDto, Long pluginId)
            throws NoConnectionException, UnexpectedErrorException {
        return validate(userDto, pluginId);
    }

    private Map<String, String> validate(UserDto userDto, Long pluginId) {
        String captcha = userDto.getCaptchas().get(PLUGIN_PREFIX + String.valueOf(pluginId));
        if (!isValid(captcha)) {
            ResourceBundle resourceBundle = ResourceBundle.getBundle("org.jtalks.jcommune.plugin.kaptcha.messages",
                            userDto.getLanguage().getLocale());
            String fieldName = "userDto.captchas['" + (PLUGIN_PREFIX + pluginId) + "']";
            return new ImmutableMap.Builder<String, String>().put(fieldName,
                    resourceBundle.getString("validation.captcha.wrong")).build();
        }
        return new HashMap<>();
    }

    @Override
    public String getHtml(HttpServletRequest request, String pluginId, Locale locale) {
        SecurityContextHolder.getContext();
        ResourceBundle resourceBundle  = ResourceBundle.getBundle("org.jtalks.jcommune.plugin.kaptcha.messages", locale);
        Properties properties = new Properties();

        properties.put("resource.loader", "jar");
        properties.put("jar.resource.loader.class", "org.apache.velocity.runtime.resource.loader.JarResourceLoader");
        String jarPath = this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
        properties.put("jar.resource.loader.path", "jar:file:" + jarPath);

        VelocityEngine engine = new VelocityEngine(properties);
        engine.init();
        Map<String, Object> model = new HashMap<>();
        model.put(CAPTCHA_LABEL, resourceBundle.getObject("label.tip.captcha"));
        model.put(ALT_CAPTCHA, resourceBundle.getObject("alt.captcha.image"));
        model.put(ALT_REFRESH_CAPTCHA, resourceBundle.getObject("alt.captcha.update"));
        model.put(CAPTCHA_PLUGIN_ID, pluginId);
        model.put(FORM_ELEMENT_ID, PLUGIN_PREFIX + pluginId);
        model.put(BASE_URL, getDeploymentRootUrlWithoutPort(request));
        return VelocityEngineUtils.mergeTemplateIntoString(
                engine, "org/jtalks/jcommune/plugin/kaptcha/template/captcha.vm", "UTF-8", model);
    }

    /**
     * Returns current deployment root without port for using as label link, for example.
     *
     * @return current deployment root without port, e.g. "http://myhost.com/myforum"
     */
    private String getDeploymentRootUrlWithoutPort(HttpServletRequest request) {
        return request.getScheme()
                + "://" + request.getServerName()
                + request.getContextPath();
    }

    @Override
    public Object doAction(String pluginId, String action, HttpServletResponse response,
                           ServletOutputStream out, HttpSession session) {
        response.setContentType("image/jpeg");
        String capText = captchaProducer.createText();
        session.setAttribute(Constants.KAPTCHA_SESSION_KEY, capText);
        BufferedImage bi = captchaProducer.createImage(capText);
        try {
            ImageIO.write(bi, "jpg", out);
            out.flush();
        } catch (IOException ex) {
            LOGGER.error("Exception at writing captcha image {}", ex);
        }
        return null;
    }

    public boolean isValid(String value) {
        RequestAttributes attributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest httpServletRequest = ((ServletRequestAttributes) attributes).getRequest();
        String sessionCaptchaId = (String) httpServletRequest.getSession().getAttribute(Constants.KAPTCHA_SESSION_KEY);
        return StringUtils.equals(sessionCaptchaId, value);
    }
}
