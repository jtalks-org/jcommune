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

/**
 * Serves for processing basic captcha functionality, such as refresh captcha, validate captcha and get captcha as html.
 *
 * @author Andrey Pogorelov
 */
public class KaptchaPluginService {

    private static final String CAPTCHA_LABEL = "captchaLabel";
    private static final String ALT_CAPTCHA = "altCaptcha";
    private static final String ALT_REFRESH_CAPTCHA = "altRefreshCaptcha";
    private static final String CAPTCHA_PLUGIN_ID = "captchaPluginId";
    private static final String BASE_URL = "baseUrl";
    private static final String FORM_ELEMENT_ID = "formElementId";
    private static final String PLUGIN_PREFIX = "plugin-";
    private Producer captchaProducer;

    public KaptchaPluginService(int width, int height, int length, String possibleSymbols) {
        captchaProducer = createCaptchaProducer(width, height, length, possibleSymbols);
    }

    /**
     * Get plugin input element id for registration form.
     *
     * @param pluginId plugin id
     * @return form element id
     */
    private String getFormElementId(String pluginId) {
        return PLUGIN_PREFIX + pluginId;
    }

    /**
     * Get plugin field name for registration form.
     *
     * @param pluginId plugin id
     * @return field name
     */
    private String getFormFieldName(String pluginId) {
        return "userDto.captchas['" + (PLUGIN_PREFIX + pluginId) + "']";
    }

    /**
     * Validates captcha value.
     *
     * @param userDto user container with captcha
     * @param pluginId plugin id
     * @return validation errors
     */
    public Map<String, String> validateCaptcha(UserDto userDto, Long pluginId) {
        String captcha = userDto.getCaptchas().get(getFormElementId(String.valueOf(pluginId)));
        if (!isValid(captcha)) {
            ResourceBundle resourceBundle = ResourceBundle.getBundle("org.jtalks.jcommune.plugin.kaptcha.messages",
                    userDto.getLanguage().getLocale());
            String fieldName = getFormFieldName(String.valueOf(pluginId));
            return new ImmutableMap.Builder<String, String>().put(fieldName,
                    resourceBundle.getString("validation.captcha.wrong")).build();
        }
        return new HashMap<>();
    }

    /**
     * Checks if specified captcha value is the same as a captcha showed to user.
     *
     * @param value submitted captcha value
     * @return success
     */
    private boolean isValid(String value) {
        String sessionCaptchaId = (String) getSession().getAttribute(Constants.KAPTCHA_SESSION_KEY);
        return StringUtils.equals(sessionCaptchaId, value);
    }

    protected Properties getProperties() {
        Properties properties = new Properties();
        properties.put("resource.loader", "jar");
        properties.put("jar.resource.loader.class", "org.apache.velocity.runtime.resource.loader.JarResourceLoader");
        String jarPath = this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
        properties.put("jar.resource.loader.path", "jar:file:" + jarPath);
        properties.put("runtime.log.logsystem.class", "org.apache.velocity.runtime.log.NullLogSystem");
        return properties;
    }

    public String getHtml(HttpServletRequest request, String pluginId, Locale locale) {
        SecurityContextHolder.getContext();
        ResourceBundle resourceBundle = ResourceBundle.getBundle("org.jtalks.jcommune.plugin.kaptcha.messages", locale);

        VelocityEngine engine = new VelocityEngine(getProperties());
        engine.init();
        Map<String, Object> model = new HashMap<>();
        model.put(CAPTCHA_LABEL, resourceBundle.getObject("label.tip.captcha"));
        model.put(ALT_CAPTCHA, resourceBundle.getObject("alt.captcha.image"));
        model.put(ALT_REFRESH_CAPTCHA, resourceBundle.getObject("alt.captcha.update"));
        model.put(CAPTCHA_PLUGIN_ID, pluginId);
        model.put(FORM_ELEMENT_ID, getFormElementId(pluginId));
        model.put(BASE_URL, getDeploymentRootUrl(request));
        return VelocityEngineUtils.mergeTemplateIntoString(
                engine, "org/jtalks/jcommune/plugin/kaptcha/template/captcha.vm", "UTF-8", model);
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

    protected Producer getCaptchaProducer() {
        return this.captchaProducer;
    }

    /**
     * Refresh captcha image on registration form.
     *
     * @param request http request
     * @param response http response
     * @throws IOException
     */
    public void refreshCaptchaImage(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        ServletOutputStream out = response.getOutputStream();
        response.setContentType("image/jpeg");
        String capText = getCaptchaProducer().createText();
        request.getSession().setAttribute(Constants.KAPTCHA_SESSION_KEY, capText);
        BufferedImage bi = getCaptchaProducer().createImage(capText);
        ImageIO.write(bi, "jpg", out);
        out.flush();
    }

    /**
     * Returns current deployment root with port (if required) for using as label link, for example.
     *
     * @return current deployment root with port, e.g. "http://myhost.com:8080/myforum" or "http://myhost.com/myforum"
     */
    protected String getDeploymentRootUrl(HttpServletRequest request) {
        StringBuilder urlBuilder = new StringBuilder().append(request.getScheme())
                .append("://").append(request.getServerName());
        if (request.getServerPort() != 80) {
            urlBuilder.append(":").append(request.getServerPort());
        }
        urlBuilder.append(request.getContextPath());
        return urlBuilder.toString();
    }

    /**
     * Remove current captcha in session
     */
    public void removeCurrentCaptcha() {
        getSession().removeAttribute(Constants.KAPTCHA_SESSION_KEY);
    }

    private HttpSession getSession() {
        RequestAttributes attributes = RequestContextHolder.currentRequestAttributes();
        return ((ServletRequestAttributes) attributes).getRequest().getSession();
    }
}
