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

import com.google.common.collect.ImmutableMap;
import org.jtalks.jcommune.model.dto.UserDto;
import org.jtalks.jcommune.model.entity.PluginProperty;
import org.jtalks.jcommune.plugin.api.exceptions.NoConnectionException;
import org.jtalks.jcommune.plugin.api.exceptions.PluginConfigurationException;
import org.jtalks.jcommune.plugin.api.exceptions.UnexpectedErrorException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

import static org.mockito.Mockito.*;
import static org.testng.Assert.assertEquals;

/**
 * @author Andrey Pogorelov
 */
public class KaptchaPluginTest {

    KaptchaPlugin kaptchaPlugin;

    @BeforeMethod
    public void setUp() throws Exception {
        kaptchaPlugin = spy(new KaptchaPlugin());
    }

    @Test
    public void testGetHtml() throws Exception {
        String pluginId = "1";
        KaptchaPluginService service = mock(KaptchaPluginService.class);
        when(kaptchaPlugin.getService()).thenReturn(service);
        String newLine = System.getProperty("line.separator");
        String expected = "<div class='control-group'>" + newLine +
                "  <div class='controls captcha-images'>" + newLine +
                "    <img class='captcha-img' alt='Captcha' src='http://localhost:8080/plugin/1/refreshCaptcha'/>" + newLine +
                "    <img class='captcha-refresh' alt='Refresh captcha'" +
                " src='http://localhost:8080/resources/images/captcha-refresh.png'/>" + newLine +
                "  </div>" + newLine +
                "  <div class='controls'>" + newLine +
                "    <input type='text' id='plugin-1' name='userDto.captchas[plugin-1]'" + newLine +
                "      placeholder='Captcha text' class='reg_input captcha'/>" + newLine +
                "  </div>" + newLine +
                "</div>";

        when(service.getHtml(null, pluginId, Locale.ENGLISH)).thenReturn(expected);

        String actual = kaptchaPlugin.getHtml(null, pluginId, Locale.ENGLISH);

        assertEquals(actual, expected);
    }

    @Test
    public void doActionShouldBeSuccessfulIfCaptchaRefreshed() throws IOException {
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpServletRequest request = mock(HttpServletRequest.class);
        KaptchaPluginService service = mock(KaptchaPluginService.class);
        when(kaptchaPlugin.getService()).thenReturn(service);

        Boolean result = (Boolean) kaptchaPlugin.doAction("1", "refreshCaptcha", request, response);

        assertEquals(result, Boolean.TRUE);
    }

    @Test
    public void doActionShouldBeSuccessfulIfCaptchaNotRefreshed() throws IOException {
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpServletRequest request = mock(HttpServletRequest.class);
        KaptchaPluginService service = mock(KaptchaPluginService.class);
        when(kaptchaPlugin.getService()).thenReturn(service);
        doThrow(new IOException()).when(service).refreshCaptchaImage(request, response);

        Boolean result = (Boolean) kaptchaPlugin.doAction("1", "refreshCaptcha", request, response);

        assertEquals(result, Boolean.FALSE);
    }

    @Test
    public void testValidateUserIfThereNoValidationErrorsShouldBeSuccessful()
            throws UnexpectedErrorException, NoConnectionException {
        UserDto userDto = new UserDto();
        Long pluginId = 1L;
        KaptchaPluginService service = mock(KaptchaPluginService.class);
        when(service.validateCaptcha(userDto, pluginId)).thenReturn(Collections.EMPTY_MAP);
        when(kaptchaPlugin.getService()).thenReturn(service);

        Map<String, String> result = kaptchaPlugin.validateUser(userDto, pluginId);

        assertEquals(result.size(), 0);
    }

    @Test
    public void testValidateUserIfThereWereValidationErrorsShouldReturnErrors()
            throws UnexpectedErrorException, NoConnectionException {
        UserDto userDto = new UserDto();
        Long pluginId = 1L;
        KaptchaPluginService service = mock(KaptchaPluginService.class);
        when(service.validateCaptcha(userDto, pluginId)).thenReturn(new ImmutableMap.Builder<String, String>()
                .put("userDto.captchas[plugin-1]", "Invalid value for captcha").build());
        when(kaptchaPlugin.getService()).thenReturn(service);

        Map<String, String> result = kaptchaPlugin.validateUser(userDto, pluginId);

        assertEquals(result.size(), 1);
    }

    @Test
    public void testRegisterUserIfThereNoValidationErrorsShouldBeSuccessful()
            throws UnexpectedErrorException, NoConnectionException {
        UserDto userDto = new UserDto();
        Long pluginId = 1L;
        KaptchaPluginService service = mock(KaptchaPluginService.class);
        when(service.validateCaptcha(userDto, pluginId)).thenReturn(Collections.EMPTY_MAP);
        when(kaptchaPlugin.getService()).thenReturn(service);

        Map<String, String> result = kaptchaPlugin.registerUser(userDto, pluginId);

        assertEquals(result.size(), 0);
    }

    @Test
    public void testRegisterUserIfThereWereValidationErrorsShouldReturnErrors()
            throws UnexpectedErrorException, NoConnectionException {
        UserDto userDto = new UserDto();
        Long pluginId = 1L;
        KaptchaPluginService service = mock(KaptchaPluginService.class);
        when(service.validateCaptcha(userDto, pluginId)).thenReturn(new ImmutableMap.Builder<String, String>()
                .put("userDto.captchas[plugin-1]", "Invalid value for captcha").build());
        when(kaptchaPlugin.getService()).thenReturn(service);

        Map<String, String> result = kaptchaPlugin.registerUser(userDto, pluginId);

        assertEquals(result.size(), 1);
    }

    @Test
    public void applyConfigurationWithCorrectValuesShouldBeSuccessful() throws PluginConfigurationException {
        List<PluginProperty> properties = new ArrayList<>();
        properties.add(new PluginProperty(KaptchaPlugin.WIDTH_PROPERTY, PluginProperty.Type.INT, "400"));
        properties.add(new PluginProperty(KaptchaPlugin.HEIGHT_PROPERTY, PluginProperty.Type.INT, "400"));
        properties.add(new PluginProperty(KaptchaPlugin.LENGTH_PROPERTY, PluginProperty.Type.INT, "4"));
        properties.add(new PluginProperty(KaptchaPlugin.POSSIBLE_SYMBOLS_PROPERTY,
                PluginProperty.Type.STRING, "0123456789"));

        Map<PluginProperty, String> errors = kaptchaPlugin.applyConfiguration(properties);

        assertEquals(errors.size(), 0);
    }

    @Test(expectedExceptions = PluginConfigurationException.class)
    public void applyConfigurationWithInvalidPropertiesShouldBeFailed() throws PluginConfigurationException {
        List<PluginProperty> properties = new ArrayList<>();
        properties.add(new PluginProperty(KaptchaPlugin.WIDTH_PROPERTY, PluginProperty.Type.INT, "400"));
        properties.add(new PluginProperty(KaptchaPlugin.HEIGHT_PROPERTY, PluginProperty.Type.INT, "400"));
        properties.add(new PluginProperty(KaptchaPlugin.LENGTH_PROPERTY, PluginProperty.Type.INT, "ABC4"));

        kaptchaPlugin.applyConfiguration(properties);
    }

    @Test
    public void testGetDefaultConfiguration() {
        List<PluginProperty> result = kaptchaPlugin.getDefaultConfiguration();

        assertEquals(result.size(), 4);
    }
}
