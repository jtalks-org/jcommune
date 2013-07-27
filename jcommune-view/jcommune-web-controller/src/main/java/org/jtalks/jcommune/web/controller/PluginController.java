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

import org.jtalks.common.service.exceptions.NotFoundException;
import org.jtalks.jcommune.model.entity.PluginConfiguration;
import org.jtalks.jcommune.model.entity.PluginConfigurationProperty;
import org.jtalks.jcommune.model.plugins.Plugin;
import org.jtalks.jcommune.service.ComponentService;
import org.jtalks.jcommune.service.PluginService;
import org.jtalks.jcommune.service.dto.PluginActivatingListDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Anuar Nurmakanov
 */
@Controller
@RequestMapping("/plugins")
public class PluginController {

    private PluginService pluginService;
    private ComponentService componentService;

    @Autowired
    public PluginController(PluginService pluginService, ComponentService componentService) {
        this.pluginService = pluginService;
        this.componentService = componentService;
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ModelAndView getPlugins() {
        long componentId = getForumComponentId();
        List<Plugin> plugins = pluginService.getPlugins(componentId);
        return new ModelAndView("pluginList")
                .addObject("plugins", plugins)
                .addObject("pluginsActivatingListDto", PluginActivatingListDto.valueOf(plugins));
    }

    @RequestMapping(value = "/configure/{name}", method = RequestMethod.GET)
    public ModelAndView configurePlugin(@PathVariable String name) throws NotFoundException {
        long componentId = getForumComponentId();
        PluginConfiguration pluginConfiguration = pluginService.getPluginConfiguration(name, componentId);
        return new ModelAndView("pluginConfiguration")
                .addObject("pluginConfiguration", pluginConfiguration);
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public ModelAndView updateConfiguration(@ModelAttribute PluginConfiguration newConfiguration) throws NotFoundException {
        long componentId = getForumComponentId();
        pluginService.updateConfiguration(newConfiguration, componentId);
        return new ModelAndView("redirect:/plugins/configure/" + newConfiguration.getName())
                .addObject("pluginConfiguration", newConfiguration);
    }

    @RequestMapping(value = "/update/activating", method = RequestMethod.POST)
    public String updateActivating(@ModelAttribute PluginActivatingListDto pluginsActivatingListDto) throws NotFoundException {
        long componentId = getForumComponentId();
        pluginService.updatePluginsActivating(pluginsActivatingListDto.getActivatingPlugins(), componentId);
        return "redirect:/plugins/list";
    }

    private long getForumComponentId() {
        return componentService.getComponentOfForum().getId();
    }
}
