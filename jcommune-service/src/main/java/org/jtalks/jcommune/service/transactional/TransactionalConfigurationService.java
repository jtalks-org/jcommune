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
import org.springframework.security.access.prepost.PreAuthorize;

/**
 * Implementation of {@link ConfigurationService}
 * @author Vyacheslav Mishcheryakov
 *
 */
public class TransactionalConfigurationService implements ConfigurationService {
    private JCommuneProperty sapeAccountId;
    private JCommuneProperty sapeTimeout;
    private JCommuneProperty sapeHostUrl;
    private JCommuneProperty sapeNumberOrLinks;
    private JCommuneProperty sapeShowOnMainPage;
    private JCommuneProperty sapeShowDummyLinks;
    private JCommuneProperty sapeEnableService;
    

    /**
     * 
     * @param sapeAccountId         property to read SAPE account ID
     * @param sapeTimeout           property to read SAPE timeout
     * @param sapeHostUrl           property to read SAPE host URL
     * @param sapeNumberOrLinks     property to read SAPE number of link to return
     * @param sapeShowOnMainPage    property to read SAPE show on main page value
     * @param sapeShowDummyLinks    property to read SAP show dummy links value
     * @param sapeEnableService     property to read SAP enable service
     */
    public TransactionalConfigurationService(
            JCommuneProperty sapeAccountId,
            JCommuneProperty sapeTimeout, 
            JCommuneProperty sapeHostUrl,
            JCommuneProperty sapeNumberOrLinks,
            JCommuneProperty sapeShowOnMainPage,
            JCommuneProperty sapeShowDummyLinks,
            JCommuneProperty sapeEnableService) {
        this.sapeAccountId = sapeAccountId;
        this.sapeTimeout = sapeTimeout;
        this.sapeHostUrl = sapeHostUrl;
        this.sapeNumberOrLinks = sapeNumberOrLinks;
        this.sapeShowOnMainPage = sapeShowOnMainPage;
        this.sapeShowDummyLinks = sapeShowDummyLinks;
        this.sapeEnableService = sapeEnableService;
    }



    /**
     * {@inheritDoc}
     */
    @Override
    @PreAuthorize("hasPermission(#componentId, 'COMPONENT', 'GeneralPermission.ADMIN')")
    public SapeConfiguration getSapeConfiguration(long componentId) {
        SapeConfiguration configuration = new SapeConfiguration();
        configuration.setAccountId(sapeAccountId.getValue());
        configuration.setTimeout(sapeTimeout.intValue());
        configuration.setHostUrl(sapeHostUrl.getValue());
        configuration.setNumberOfLinks(sapeNumberOrLinks.intValue());
        configuration.setShowOnMainPage(sapeShowOnMainPage.booleanValue());
        configuration.setShowDummyLinks(sapeShowDummyLinks.booleanValue());
        configuration.setEnableSape(sapeEnableService.booleanValue());
        return configuration;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @PreAuthorize("hasPermission(#componentId, 'COMPONENT', 'GeneralPermission.ADMIN')")
    public void updateSapeConfiguration(SapeConfiguration configuration, long componentId) {
        sapeAccountId.setValue(configuration.getAccountId());
        sapeTimeout.setValue(String.valueOf(configuration.getTimeout()));
        sapeHostUrl.setValue(configuration.getHostUrl());
        sapeNumberOrLinks.setValue(String.valueOf(configuration.getNumberOfLinks()));
        sapeShowOnMainPage.setValue(String.valueOf(configuration.isShowOnMainPage()));
        sapeShowDummyLinks.setValue(String.valueOf(configuration.isShowDummyLinks()));
        sapeEnableService.setValue(String.valueOf(configuration.isEnableSape()));
    }
    
}
