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

import org.jtalks.common.model.entity.Component;
import org.jtalks.jcommune.model.entity.SapeConfiguration;
import org.jtalks.jcommune.service.ComponentService;
import org.jtalks.jcommune.service.ConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 * Serves forum configuration web requests
 *
 * @author Vyacheslav Mishcheryakov
 */
@Controller
public class ConfigurationController {
    private static final String VIEW_SAPE_CONFIGURATION = "sapeConfiguration";
    private static final String PARAM_SAPE_CONFIGURATION = "sapeConfiguration";

    private final ConfigurationService configurationService;
    private final ComponentService componentService;
    
    
    /**
     * @param configurationService      to operate with forum configuration
     * @param componentService          to get component of forum for permission
     *      checking
     */
    @Autowired
    public ConfigurationController(ConfigurationService configurationService, ComponentService componentService) {
        this.configurationService = configurationService;
        this.componentService = componentService;
    }

    /**
     * This method turns the trim binder on. Trim binder
     * removes leading and trailing spaces from the submitted fields.
     * So, it ensures, that all validations will be applied to
     * trimmed field values only.
     *
     * @param binder Binder object to be injected
     */
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
    }
    
    /**
     * Show SAPE configuration page
     * @return SAPE configuration page 
     */
    @RequestMapping(value="/configuration/sape", method=RequestMethod.GET)
    public ModelAndView showSapeConfigurationPage() {
        Component forumComponent = componentService.getComponentOfForum();
        SapeConfiguration configuration = configurationService.getSapeConfiguration(forumComponent.getId());
        return new ModelAndView(VIEW_SAPE_CONFIGURATION)
                .addObject(PARAM_SAPE_CONFIGURATION, configuration);
    }
    
    /**
     * Save SAPE configuration
     * @param configuration DTO with new configuration
     * @param result object contains validation errors
     * @return SAPE configuration page with updates configuration data (or 
     *      validation errors)
     */
    @RequestMapping(value="/configuration/sape", method=RequestMethod.POST)
    public ModelAndView saveSapeConfiguration(@ModelAttribute SapeConfiguration configuration, BindingResult result) {
        if (!result.hasErrors()) {
            Component forumComponent = componentService.getComponentOfForum();
            configurationService.updateSapeConfiguration(configuration, forumComponent.getId());
        }
        return new ModelAndView(VIEW_SAPE_CONFIGURATION)
            .addObject(PARAM_SAPE_CONFIGURATION, configuration);
    }
    
}
