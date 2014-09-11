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
package org.jtalks.jcommune.web.controller;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.jtalks.common.service.exceptions.NotFoundException;
import org.jtalks.jcommune.model.entity.PluginConfiguration;
import org.jtalks.jcommune.model.entity.PluginProperty;
import org.jtalks.jcommune.plugin.api.PluginLoader;
import org.jtalks.jcommune.plugin.api.core.Plugin;
import org.jtalks.jcommune.plugin.api.dto.PluginActivatingDto;
import org.jtalks.jcommune.plugin.api.dto.PluginActivatingListDto;
import org.jtalks.jcommune.plugin.api.exceptions.UnexpectedErrorException;
import org.jtalks.jcommune.plugin.api.filters.NameFilter;
import org.jtalks.jcommune.service.ComponentService;
import org.jtalks.jcommune.service.PluginService;
import org.jtalks.jcommune.service.UserService;
import org.jtalks.jcommune.web.dto.json.JsonResponse;
import org.jtalks.jcommune.web.dto.json.JsonResponseStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Provides an ability to manage plugins of the forum.
 *
 * @author Anuar Nurmakanov
 * @author Andrey Ivanov
 */
@Controller
@RequestMapping("/plugins")
public class PluginController {

    private final PluginService pluginService;
    private final ComponentService componentService;
    private final PluginLoader pluginLoader;
    private final UserService userService;

    /**
     * Constructs an instance with required fields.
     *
     * @param pluginService    to manage plugins
     * @param componentService to load an identifier of forum component
     * @param pluginLoader     to load plugins
     * @param userService      to work with user data
     */
    @Autowired
    public PluginController(PluginService pluginService, ComponentService componentService, PluginLoader pluginLoader,
                            UserService userService) {
        this.pluginService = pluginService;
        this.componentService = componentService;
        this.pluginLoader = pluginLoader;
        this.userService = userService;
    }

    /**
     * Get the list of plugins that have been added to forum.
     *
     * @return the list of plugins that runs in the forum
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ModelAndView getPlugins() {
        long componentId = getForumComponentId();
        List<Plugin> plugins = pluginService.getPlugins(componentId);
        return new ModelAndView("plugin/pluginList")
                .addObject("plugins", plugins)
                .addObject("pluginsActivatingListDto", PluginActivatingListDto.valueOf(plugins));
    }

    /**
     * Start configuring a plugin.
     *
     * @param name a name of a plugin that should be configured
     * @return the name of view and all parameters to display plugin that should be configured
     * @throws NotFoundException if passed plugin wasn't found
     */
    @RequestMapping(value = "/configure/{name}", method = RequestMethod.GET)
    public ModelAndView startConfiguringPlugin(@PathVariable String name) throws NotFoundException {
        long componentId = getForumComponentId();
        PluginConfiguration pluginConfiguration = pluginService.getPluginConfiguration(name, componentId);
        return getModel(pluginConfiguration);
    }

    /**
     * Update the configuration of plugin
     *
     * @param newConfiguration new configuration for plugin
     * @return redirect to the page of plugin configuration
     * @throws NotFoundException if configured plugin wasn't found
     */
    @RequestMapping(value = "/configure/{name}", method = RequestMethod.POST)
    public ModelAndView updateConfiguration(Model model, @ModelAttribute PluginConfiguration newConfiguration)
            throws NotFoundException {
        long componentId = getForumComponentId();
        try {
            pluginService.updateConfiguration(newConfiguration, componentId);
        } catch (UnexpectedErrorException ex) {
            model.addAttribute("error", ex.getCause().getLocalizedMessage());
            model.addAttribute("errorInformation", ExceptionUtils.getFullStackTrace(ex.getCause()));
            model.addAttribute("pluginConfiguration", newConfiguration);
            return getModel(newConfiguration);
        }

        return new ModelAndView("redirect:/plugins/configure/" + newConfiguration.getName());
    }

    /**
     * Get model for showing plugin configuration
     *
     * @param configuration current plugin configuration
     * @return ModelAndView
     */
    private ModelAndView getModel(PluginConfiguration configuration) {
        Map<String, String> labels = new HashMap<>();
        List<Plugin> plugins = pluginLoader.getPlugins(new NameFilter(configuration.getName()));
        if (!plugins.isEmpty()) {
            Plugin plugin = plugins.get(0);
            if (plugin != null) {
                labels = translatePropertiesLabels(configuration.getProperties(), plugin);
            }
        }
        return new ModelAndView("plugin/pluginConfiguration")
                .addObject("pluginConfiguration", configuration)
                .addObject("labelsTranslation", labels);
    }

    /**
     * Translate labels for specified plugin into language which selected by current user as forum language.
     *
     * @param properties list of properties to translate
     * @param plugin plugin with have specified properties
     *
     * @return map of properties translation depending from name
     */
    private Map<String, String> translatePropertiesLabels(List<PluginProperty> properties, Plugin plugin) {
        Map<String, String> labels = new HashMap<>();
        Locale locale = userService.getCurrentUser().getLanguage().getLocale();
        for (PluginProperty property : properties) {
            String translation = plugin.translateLabel(property.getName(), locale);
            labels.put(property.getName(), translation);
            if (property.getHint() != null) {
                String hintTranslation = plugin.translateLabel(property.getHint(), locale);
                labels.put(property.getHint(), hintTranslation);
            }
        }
        return labels;
    }

    @RequestMapping(value = "/activate", method = RequestMethod.POST)
    @ResponseBody
    public JsonResponse activatePlugin(@RequestBody PluginActivatingDto pluginActivatingDto) throws NotFoundException {
        long componentId = getForumComponentId();
        pluginService.updatePluginActivating(pluginActivatingDto, componentId);
        return new JsonResponse(JsonResponseStatus.SUCCESS);
    }

    private long getForumComponentId() {
        return componentService.getComponentOfForum().getId();
    }
}
