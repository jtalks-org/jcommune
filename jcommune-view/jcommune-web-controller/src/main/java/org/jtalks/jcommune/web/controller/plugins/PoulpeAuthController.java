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

package org.jtalks.jcommune.web.controller.plugins;

import org.jtalks.jcommune.model.plugins.Plugin;
import org.jtalks.jcommune.model.plugins.SimpleAuthenticationPlugin;
import org.jtalks.jcommune.model.plugins.exceptions.NoConnectionException;
import org.jtalks.jcommune.model.plugins.exceptions.UnexpectedErrorException;
import org.jtalks.jcommune.service.nontransactional.EncryptionService;
import org.jtalks.jcommune.service.plugins.PluginFilter;
import org.jtalks.jcommune.service.plugins.PluginLoader;
import org.jtalks.jcommune.service.plugins.TypeFilter;
import org.jtalks.jcommune.web.dto.RegisterUserDto;
import org.jtalks.jcommune.web.dto.json.JsonResponse;
import org.jtalks.jcommune.web.dto.json.JsonResponseStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Controller
public class PoulpeAuthController {

    public static final String REGISTRATION = "registration";
    public static final String AFTER_REGISTRATION = "afterRegistration";

    private EncryptionService encryptionService;

    private PluginLoader pluginLoader;

    @Autowired
    public PoulpeAuthController(PluginLoader pluginLoader,
                                EncryptionService encryptionService) {
        this.pluginLoader = pluginLoader;
        this.encryptionService = encryptionService;
    }

    /**
     * Register {@link org.jtalks.jcommune.model.entity.JCUser} from populated in form {@link RegisterUserDto}.
     * <p/>
     *
     * @param userDto {@link RegisterUserDto} populated in form
     * @param result  result of {@link RegisterUserDto} validation
     * @param locale  to set currently selected language as user's default
     * @return redirect to / if registration successful or back to "/registration" if failed
     */
    @RequestMapping(value = "/user/new/poulpe", method = RequestMethod.POST)
    public ModelAndView registerUserPlugin(@ModelAttribute("newUser") RegisterUserDto userDto,
                                           BindingResult result, Locale locale) {
        SimpleAuthenticationPlugin authPlugin = getAuthPlugin();
        if (authPlugin != null && authPlugin.getState() == Plugin.State.ENABLED) {
            String passwordHash = encryptionService.encryptPassword(userDto.getPassword());
            Map<String, String> errors = new HashMap<>();
            try {
                errors.putAll(authPlugin.registerUser(userDto.getUsername(), passwordHash, userDto.getEmail()));
            } catch (NoConnectionException e) {
                errors.put("captcha", "Registration service not available");
            } catch (UnexpectedErrorException e) {
                errors.put("captcha", "Unexpected error was happened");
            }
            result = parseValidationErrors(errors, result);
            if (result.hasErrors()) {
                return new ModelAndView(REGISTRATION);
            }
        }
        return new ModelAndView(AFTER_REGISTRATION);
    }

    private SimpleAuthenticationPlugin getAuthPlugin() {
        Class cl = SimpleAuthenticationPlugin.class;
        PluginFilter pluginFilter = new TypeFilter(cl);
        List<Plugin> plugins = pluginLoader.getPlugins(pluginFilter);
        return !plugins.isEmpty() ? (SimpleAuthenticationPlugin) plugins.get(0) : null;
    }

    private BindingResult parseValidationErrors(Map<String, String> errors, BindingResult result) {
        for (Map.Entry error : errors.entrySet()) {
            result.rejectValue(error.getKey().toString(), null, error.getValue().toString());
        }
        return result;
    }

    /**
     * Register {@link org.jtalks.jcommune.model.entity.JCUser} from populated {@link RegisterUserDto}.
     * <p/>
     *
     * @param userDto {@link RegisterUserDto} populated in form
     * @param result  result of {@link RegisterUserDto} validation
     * @param locale  to set currently selected language as user's default
     * @return redirect validation result in JSON format
     */
    @RequestMapping(value = "/user/new_ajax/poulpe", method = RequestMethod.POST)
    @ResponseBody
    public JsonResponse registerUserAjax(@ModelAttribute("newUser") RegisterUserDto userDto,
                                         BindingResult result, Locale locale) {
        SimpleAuthenticationPlugin authPlugin = getAuthPlugin();
        if (authPlugin != null) {
            String passwordHash = encryptionService.encryptPassword(userDto.getPassword());
            Map<String, String> errors = new HashMap<>();
            try {
                errors.putAll(authPlugin.registerUser(userDto.getUsername(), passwordHash, userDto.getEmail()));
            } catch (NoConnectionException e) {
                errors.put("captcha", "Registration service not available.");
            } catch (UnexpectedErrorException e) {
                errors.put("captcha", "Unexpected error was happend");
            }
            result = parseValidationErrors(errors, result);
            if (result.hasErrors()) {
                return new JsonResponse(JsonResponseStatus.FAIL, result.getAllErrors());
            }
        }
        return new JsonResponse(JsonResponseStatus.SUCCESS);
    }
}
