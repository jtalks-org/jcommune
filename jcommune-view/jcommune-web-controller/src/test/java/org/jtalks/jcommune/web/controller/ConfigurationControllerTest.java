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
import org.mockito.Mock;
import static org.mockito.MockitoAnnotations.initMocks;

import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.ModelAndViewAssert.assertAndReturnModelAttributeOfType;
import static org.springframework.test.web.ModelAndViewAssert.assertModelAttributeAvailable;
import static org.springframework.test.web.ModelAndViewAssert.assertViewName;
import static org.testng.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.eq;


/**
 * 
 * @author Vyacheslav Mishcheryakov
 *
 */
public class ConfigurationControllerTest {

    private static final String VIEW_SAPE_CONFIGURATION = "sapeConfiguration";
    private static final String PARAM_SAPE_CONFIGURATION = "sapeConfiguration";

    private ConfigurationController controller;
    
    @Mock
    private ConfigurationService configurationService;
    @Mock
    private ComponentService componentService;
    
    private SapeConfiguration sapeConfiguration;
    
    @BeforeMethod
    public void init() {
        initMocks(this);
        controller = new ConfigurationController(configurationService, componentService);
     
        when(componentService.getComponentOfForum()).thenReturn(new Component());
        
        sapeConfiguration = new SapeConfiguration();
        when(configurationService.getSapeConfiguration(anyLong())).thenReturn(sapeConfiguration);
    }
    
    @Test
    public void testShowSapeConfigurationPage() {
        ModelAndView mav = controller.showSapeConfigurationPage();
        
        assertViewName(mav, VIEW_SAPE_CONFIGURATION);
        assertModelAttributeAvailable(mav, PARAM_SAPE_CONFIGURATION);
        SapeConfiguration configuration = assertAndReturnModelAttributeOfType(
                mav, PARAM_SAPE_CONFIGURATION, SapeConfiguration.class);
        assertEquals(configuration, sapeConfiguration);
    }
    
    @Test
    public void testSaveSapeConfigurationValidationSuccess() {
        SapeConfiguration newConfiguration = new SapeConfiguration(); 
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);
        
        ModelAndView mav = controller.saveSapeConfiguration(newConfiguration, bindingResult);
        
        verify(configurationService).updateSapeConfiguration(eq(newConfiguration), anyLong());
        assertViewName(mav, VIEW_SAPE_CONFIGURATION);
        assertModelAttributeAvailable(mav, PARAM_SAPE_CONFIGURATION);
        SapeConfiguration configuration = assertAndReturnModelAttributeOfType(
                mav, PARAM_SAPE_CONFIGURATION, SapeConfiguration.class);
        assertEquals(configuration, newConfiguration);
    }
    
    @Test
    public void testSaveSapeConfigurationValidationFail() {
        SapeConfiguration newConfiguration = new SapeConfiguration(); 
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(true);
        
        ModelAndView mav = controller.saveSapeConfiguration(newConfiguration, bindingResult);
        
        verify(configurationService, never()).updateSapeConfiguration(eq(newConfiguration), anyLong());
        assertViewName(mav, VIEW_SAPE_CONFIGURATION);
        assertModelAttributeAvailable(mav, PARAM_SAPE_CONFIGURATION);
        SapeConfiguration configuration = assertAndReturnModelAttributeOfType(
                mav, PARAM_SAPE_CONFIGURATION, SapeConfiguration.class);
        assertEquals(configuration, newConfiguration);
    }
    
}
