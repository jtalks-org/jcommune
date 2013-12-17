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
import org.jtalks.jcommune.model.plugins.Plugin;
import org.jtalks.jcommune.model.plugins.exceptions.UnexpectedErrorException;
import org.jtalks.jcommune.service.ComponentService;
import org.jtalks.jcommune.service.PluginService;
import org.jtalks.jcommune.service.dto.PluginActivatingListDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;

/**
 * Provides an ability to manage plugins of the forum.
 *
 * @author Anuar Nurmakanov
 * @author Andrey Ivanov
 */
@Controller
@RequestMapping("/plugins")
@SessionAttributes({"pluginConfiguration"})
public class PluginController {

    private final PluginService pluginService;
    private final ComponentService componentService;

    /**
     * Constructs an instance with required fields.
     *
     * @param pluginService    to manage plugins
     * @param componentService to load an identifier of forum component
     */
    @Autowired
    public PluginController(final PluginService pluginService, final ComponentService componentService) {
        this.pluginService = pluginService;
        this.componentService = componentService;
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
     * Show plugin with errors
     *
     * @param configuration current plugin configuration
     *
     * @return the name of view and all parameters to display plugin that should be configured
     */
    @RequestMapping(value = "/configure/error/{name}", method = RequestMethod.GET)
    public ModelAndView errorConfiguringPlugin(@ModelAttribute PluginConfiguration configuration) {
        return getModel(configuration);
    }

    /**
     * Get model for showing plugin configuration
     *
     * @param configuration current plugin configuration
     *
     * @return ModelAndView
     */
    private ModelAndView getModel(PluginConfiguration configuration)
    {
        return new ModelAndView("plugin/pluginConfiguration").addObject("pluginConfiguration", configuration);
    }

    /**
     * Update the configuration of plugin/
     *
     * @param newConfiguration new configuration for plugin
     * @return redirect to the page of plugin configuration
     * @throws NotFoundException if configured plugin wasn't found
     */
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public String updateConfiguration(Model model, @ModelAttribute PluginConfiguration newConfiguration,
                                      final RedirectAttributes redirectAttributes)
            throws NotFoundException {
        long componentId = getForumComponentId();
        try {
            pluginService.updateConfiguration(newConfiguration, componentId);
        } catch (UnexpectedErrorException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getCause().getLocalizedMessage());
            redirectAttributes.addFlashAttribute("errorInformation", ExceptionUtils.getFullStackTrace(ex.getCause()));
            model.addAttribute("pluginConfiguration", newConfiguration);
            return "redirect:/plugins/configure/error/" + newConfiguration.getName();
        }

        return "redirect:/plugins/configure/" + newConfiguration.getName();
    }

     /**
     * Update activating state of plugins.
     *
     * @param pluginsActivatingListDto contains activating state for the list of plugins
     * @return redirect to the list of plugins
     * @throws NotFoundException if configured plugin wasn't found
     */
    @RequestMapping(value = "/update/activating", method = RequestMethod.POST)
    public String updateActivating(
            @ModelAttribute PluginActivatingListDto pluginsActivatingListDto) throws NotFoundException {
        long componentId = getForumComponentId();
        pluginService.updatePluginsActivating(pluginsActivatingListDto.getActivatingPlugins(), componentId);
        return "redirect:/plugins/list";
    }

    private long getForumComponentId() {
        return componentService.getComponentOfForum().getId();
    }
}
