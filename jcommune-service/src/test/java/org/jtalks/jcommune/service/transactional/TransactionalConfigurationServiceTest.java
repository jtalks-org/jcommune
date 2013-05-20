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

import org.jtalks.common.model.entity.Component;
import org.jtalks.jcommune.model.dao.ComponentDao;
import org.jtalks.jcommune.model.entity.JCommuneProperty;
import org.jtalks.jcommune.model.entity.SapeConfiguration;
import org.jtalks.jcommune.service.ConfigurationService;
import org.mockito.Mock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.testng.Assert.assertEquals;

/**
 * @author Vyacheslav Mishcheryakov
 */
public class TransactionalConfigurationServiceTest {

    private static final long COMPONENT_ID = 1L;
    private static final String SAPE_ACCOUNT_ID = "accountId";
    private static final int SAPE_TIMEOUT = 100;
    private static final String SAPE_HOST_URL = "http://host.url";
    private static final int SAPE_NUMBER_OF_LINKS = 10;
    private static final boolean SAPE_SHOW_ON_MAIN_PAGE = true;
    private static final boolean SAPE_SHOW_DUMMY_LINKS = false;

    private JCommuneProperty sapeAccountId = JCommuneProperty.CMP_SAPE_ACCOUNT_ID;
    private JCommuneProperty sapeTimeout = JCommuneProperty.CMP_SAPE_TIMEOUT;
    private JCommuneProperty sapeHostUrl = JCommuneProperty.CMP_HOST_URL;
    private JCommuneProperty sapeNumberOrLinks = JCommuneProperty.CMP_SAPE_LINKS_COUNT;
    private JCommuneProperty sapeShowOnMainPage = JCommuneProperty.CMP_SAPE_ON_MAIN_PAGE_ENABLE;
    private JCommuneProperty sapeShowDummyLinks = JCommuneProperty.CMP_SAPE_SHOW_DUMMY_LINKS;
    private JCommuneProperty sapeEnabled = JCommuneProperty.CMP_SAPE_ENABLED;
    @Mock
    private ComponentDao componentDao;

    private ConfigurationService configurationService;

    @BeforeMethod
    public void init() {
        initMocks(this);
        configurationService = new TransactionalConfigurationService(
                sapeAccountId, sapeTimeout, sapeHostUrl, sapeNumberOrLinks,
                sapeShowOnMainPage, sapeShowDummyLinks, sapeEnabled);

        sapeAccountId.setName("sape.account.id");
        sapeTimeout.setName("sape.timeout");
        sapeHostUrl.setName("sape.host.url");
        sapeNumberOrLinks.setName("sape.number.of.links");
        sapeShowOnMainPage.setName("sape.show.on.main.page");
        sapeShowDummyLinks.setName("sape.show.dummy.links");

        sapeAccountId.setDefaultValue(SAPE_ACCOUNT_ID);
        sapeTimeout.setDefaultValue(String.valueOf(SAPE_TIMEOUT));
        sapeHostUrl.setDefaultValue(SAPE_HOST_URL);
        sapeNumberOrLinks.setDefaultValue(String.valueOf(SAPE_NUMBER_OF_LINKS));
        sapeShowOnMainPage.setDefaultValue(String.valueOf(SAPE_SHOW_ON_MAIN_PAGE));
        sapeShowDummyLinks.setDefaultValue(String.valueOf(SAPE_SHOW_DUMMY_LINKS));

        sapeAccountId.setComponentDao(componentDao);
        sapeTimeout.setComponentDao(componentDao);
        sapeHostUrl.setComponentDao(componentDao);
        sapeNumberOrLinks.setComponentDao(componentDao);
        sapeShowOnMainPage.setComponentDao(componentDao);
        sapeShowDummyLinks.setComponentDao(componentDao);
    }

    @Test
    public void testGetSapeConfiguration() {
        SapeConfiguration configuration = configurationService.getSapeConfiguration(COMPONENT_ID);

        assertEquals(configuration.getAccountId(), SAPE_ACCOUNT_ID);
        assertEquals(configuration.getTimeout(), SAPE_TIMEOUT);
        assertEquals(configuration.getHostUrl(), SAPE_HOST_URL);
        assertEquals(configuration.getNumberOfLinks(), SAPE_NUMBER_OF_LINKS);
        assertEquals(configuration.isShowOnMainPage(), SAPE_SHOW_ON_MAIN_PAGE);
        assertEquals(configuration.isShowDummyLinks(), SAPE_SHOW_DUMMY_LINKS);
    }

    @Test
    public void testUpdateSapeConfiguration() {
        SapeConfiguration configuration = new SapeConfiguration();
        configuration.setAccountId(SAPE_ACCOUNT_ID);
        configuration.setTimeout(SAPE_TIMEOUT);
        configuration.setHostUrl(SAPE_HOST_URL);
        configuration.setNumberOfLinks(SAPE_NUMBER_OF_LINKS);
        configuration.setShowOnMainPage(SAPE_SHOW_ON_MAIN_PAGE);
        configuration.setShowDummyLinks(SAPE_SHOW_DUMMY_LINKS);

        Component component = new Component();
        when(componentDao.getComponent()).thenReturn(component);

        configurationService.updateSapeConfiguration(configuration, COMPONENT_ID);

        verify(componentDao, times(6)).saveOrUpdate(any(Component.class));
        assertEquals(component.getProperties().size(), 6);
    }


}
