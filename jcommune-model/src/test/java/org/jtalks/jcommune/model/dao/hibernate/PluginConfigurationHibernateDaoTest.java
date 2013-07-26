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
package org.jtalks.jcommune.model.dao.hibernate;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.jtalks.common.service.exceptions.NotFoundException;
import org.jtalks.jcommune.model.PersistedObjectsFactory;
import org.jtalks.jcommune.model.dao.PluginConfigurationDao;
import org.jtalks.jcommune.model.entity.PluginConfiguration;
import org.jtalks.jcommune.model.entity.PluginConfigurationProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.testng.Assert.*;

/**
 * @author Anuar Nurmakanov
 */
@ContextConfiguration(locations = {"classpath:/org/jtalks/jcommune/model/entity/applicationContext-dao.xml"})
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
@Transactional
public class PluginConfigurationHibernateDaoTest extends AbstractTransactionalTestNGSpringContextTests {
    @Autowired
    private SessionFactory sessionFactory;
    @Autowired
    private PluginConfigurationDao pluginConfigurationDao;
    private Session session;

    @BeforeMethod
    public void init() {
        this.session = sessionFactory.getCurrentSession();
        PersistedObjectsFactory.setSession(session);
    }

    /*===== Common methods =====*/

    @Test
    public void getShouldReturnPluginById() {
        PluginConfiguration pluginConfiguration = PersistedObjectsFactory.getDefaultPlugin();

        PluginConfiguration foundPluginConfiguration = pluginConfigurationDao.get(pluginConfiguration.getId());

        assertNotNull(foundPluginConfiguration);
        assertEquals(foundPluginConfiguration.getId(), pluginConfiguration.getId(),
                "Get should return pluginConfiguration by it ID, so found pluginConfiguration must have the same ID as passed to get.");
    }

    @Test
    public void getWithPassedIdOfNonExistPluginShouldReturnNull() {
        PluginConfiguration nonExistPluginConfiguration = pluginConfigurationDao.get(-788888L);

        assertNull(nonExistPluginConfiguration, "PluginConfiguration doesn't exist, so get must return null");
    }

    @Test
    public void saveOrUpdateShouldUpdatePluginConfiguration() {
        String newPluginName = "Poulpe pluginConfiguration";
        PluginConfiguration pluginConfiguration = PersistedObjectsFactory.getDefaultPlugin();
        pluginConfiguration.setName(newPluginName);


        pluginConfigurationDao.saveOrUpdate(pluginConfiguration);
        session.evict(pluginConfiguration);
        PluginConfiguration updatedPluginConfiguration = (PluginConfiguration) session.get(PluginConfiguration.class, pluginConfiguration.getId());

        assertEquals(updatedPluginConfiguration.getName(), newPluginName, "After update pluginConfiguration properties must be updated.");
    }

    @Test
    public void saveOrUpdateShouldSaveNewPluginConfiguration() {
        PluginConfiguration newPluginConfiguration = new PluginConfiguration("New PluginConfiguration", true, Collections.<PluginConfigurationProperty> emptyList());

        pluginConfigurationDao.saveOrUpdate(newPluginConfiguration);
        session.evict(newPluginConfiguration);
        PluginConfiguration savedPluginConfiguration = (PluginConfiguration) session.get(PluginConfiguration.class, newPluginConfiguration.getId());

        assertNotNull(savedPluginConfiguration, "PluginConfiguration should be found after persisting to database.");
    }

    @Test
    public void saveOrUpdateShouldSavePluginConfigurationProperties() {
        PluginConfiguration pluginConfiguration = PersistedObjectsFactory.getDefaultPlugin();
        PluginConfigurationProperty property = new PluginConfigurationProperty("Property", PluginConfigurationProperty.Type.BOOLEAN, "true");
        List<PluginConfigurationProperty> properties = Arrays.asList(property);
        pluginConfiguration.setProperties(properties);

        pluginConfigurationDao.saveOrUpdate(pluginConfiguration);
        session.evict(pluginConfiguration);
        PluginConfiguration updatedPluginConfiguration = (PluginConfiguration) session.get(PluginConfiguration.class, pluginConfiguration.getId());

        assertEquals(updatedPluginConfiguration.getProperties(), properties, "Plugin configuration properties should be saved.");
    }

    @Test(expectedExceptions = org.springframework.dao.DataIntegrityViolationException.class)
    public void saveOrUpdateWithNullValuesShouldNotSavePlugin() {
        PluginConfiguration pluginConfiguration = PersistedObjectsFactory.getDefaultPlugin();
        session.save(pluginConfiguration);

        pluginConfiguration.setName(null);
        pluginConfigurationDao.saveOrUpdate(pluginConfiguration);
    }

    @Test
    public void testGetByName() throws NotFoundException {
        PluginConfiguration pluginConfiguration = PersistedObjectsFactory.getDefaultPlugin();
        session.save(pluginConfiguration);

        PluginConfiguration actual = pluginConfigurationDao.get(pluginConfiguration.getName());

        assertEquals(actual, pluginConfiguration);
    }

    @Test(expectedExceptions = NotFoundException.class)
    public void testGetByNonExistingName() throws NotFoundException {
        pluginConfigurationDao.get("Some fake name");
    }
}
