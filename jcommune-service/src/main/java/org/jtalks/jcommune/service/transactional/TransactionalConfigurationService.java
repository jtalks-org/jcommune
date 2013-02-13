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

    /**
     * 
     * @param sapeAccountId         property to read SAPE account ID
     * @param sapeTimeout           property to read SAPE timeout
     * @param sapeHostUrl           property to read SAPE host URL
     * @param sapeNumberOrLinks     property to read SAPE number of link to return
     * @param sapeShowOnMainPage    property to read SAPE show on main page value
     */
    public TransactionalConfigurationService(
            JCommuneProperty sapeAccountId,
            JCommuneProperty sapeTimeout, 
            JCommuneProperty sapeHostUrl,
            JCommuneProperty sapeNumberOrLinks,
            JCommuneProperty sapeShowOnMainPage) {
        this.sapeAccountId = sapeAccountId;
        this.sapeTimeout = sapeTimeout;
        this.sapeHostUrl = sapeHostUrl;
        this.sapeNumberOrLinks = sapeNumberOrLinks;
        this.sapeShowOnMainPage = sapeShowOnMainPage;
    }



    /**
     * {@inheritDoc}
     */
    @Override
    public SapeConfiguration getSapeConfiguration() {
        SapeConfiguration configuration = new SapeConfiguration();
        configuration.setAccountId(sapeAccountId.getValue());
        configuration.setTimeout(sapeTimeout.intValue());
        configuration.setHostUrl(sapeHostUrl.getValue());
        configuration.setNumberOfLinks(sapeNumberOrLinks.intValue());
        configuration.setShowOnMainPage(sapeShowOnMainPage.booleanValue());
        return configuration;
    }
}
