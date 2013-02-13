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
package org.jtalks.jcommune.service.transactional;

import org.jtalks.jcommune.model.entity.JCommuneProperty;
import org.jtalks.jcommune.model.entity.SapeConfiguration;
import org.jtalks.jcommune.service.ConfigurationService;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.MockitoAnnotations.initMocks;
import static org.testng.Assert.assertEquals;

/**
 * 
 * @author Vyacheslav Mishcheryakov
 *
 */
public class TransactionalConfigurationServiceTest {

    private static final String SAPE_ACCOUNT_ID = "accountId";
    private static final int SAPE_TIMEOUT = 100;
    private static final String SAPE_HOST_URL = "http://host.url";
    private static final int SAPE_NUMBER_OF_LINKS = 10;
    private static final boolean SAPE_SHOW_ON_MAIN_PAGE = true;
    
    private JCommuneProperty sapeAccountId = JCommuneProperty.CMP_SAPE_ACCOUNT_ID;
    private JCommuneProperty sapeTimeout = JCommuneProperty.CMP_SAPE_TIMEOUT;
    private JCommuneProperty sapeHostUrl = JCommuneProperty.CMP_HOST_URL;
    private JCommuneProperty sapeNumberOrLinks = JCommuneProperty.CMP_SAPE_LINKS_COUNT;
    private JCommuneProperty sapeShowOnMainPage = JCommuneProperty.CMP_SAPE_ON_MAIN_PAGE_ENABLE;
    
    private ConfigurationService configurationService;
    
    @BeforeMethod
    public void init() {
        initMocks(this);
        configurationService = new TransactionalConfigurationService(
                sapeAccountId, sapeTimeout, sapeHostUrl, sapeNumberOrLinks, 
                sapeShowOnMainPage);
        
        sapeAccountId.setDefaultValue(SAPE_ACCOUNT_ID);
        sapeTimeout.setDefaultValue(String.valueOf(SAPE_TIMEOUT));
        sapeHostUrl.setDefaultValue(SAPE_HOST_URL);
        sapeNumberOrLinks.setDefaultValue(String.valueOf(SAPE_NUMBER_OF_LINKS));
        sapeShowOnMainPage.setDefaultValue(String.valueOf(SAPE_SHOW_ON_MAIN_PAGE));
    }
    
    @Test
    public void testGetSapeConfiguration() {
        SapeConfiguration configuration = configurationService.getSapeConfiguration();
        
        assertEquals(configuration.getAccountId(), SAPE_ACCOUNT_ID);
        assertEquals(configuration.getTimeout(), SAPE_TIMEOUT);
        assertEquals(configuration.getHostUrl(), SAPE_HOST_URL);
        assertEquals(configuration.getNumberOfLinks(), SAPE_NUMBER_OF_LINKS);
        assertEquals(configuration.isShowOnMainPage(), SAPE_SHOW_ON_MAIN_PAGE);
        
    }
    
    
    
}
