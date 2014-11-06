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
import org.jtalks.jcommune.model.entity.PersistedObjectsFactory;
import org.jtalks.jcommune.model.dao.PluginConfigurationDao;
import org.jtalks.jcommune.model.entity.PluginConfiguration;
import org.jtalks.jcommune.model.entity.PluginProperty;
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
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;

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

        session.clear();

        PluginConfiguration pluginConfiguration = PersistedObjectsFactory.getDefaultPluginConfiguration();

        PluginConfiguration foundPluginConfiguration = pluginConfigurationDao.get(pluginConfiguration.getId());

        assertNotNull(foundPluginConfiguration);
        assertReflectionEquals(foundPluginConfiguration, pluginConfiguration);
    }

    @Test
    public void getWithPassedIdOfNonExistingPluginShouldReturnNull() {
        PluginConfiguration nonExistPluginConfiguration = pluginConfigurationDao.get(-788888L);

        assertNull(nonExistPluginConfiguration, "PluginConfiguration doesn't exist, so get must return null");
    }

    @Test
    public void saveOrUpdateShouldUpdatePluginConfiguration() {
        String newPluginName = "Poulpe pluginConfiguration";
        PluginConfiguration pluginConfiguration = PersistedObjectsFactory.getDefaultPluginConfiguration();
        pluginConfiguration.setName(newPluginName);

        pluginConfigurationDao.saveOrUpdate(pluginConfiguration);
        session.flush();
        session.clear();
        PluginConfiguration updatedPluginConfiguration = (PluginConfiguration) session.get(PluginConfiguration.class, pluginConfiguration.getId());

        assertEquals(updatedPluginConfiguration.getName(), newPluginName, "After update pluginConfiguration properties must be updated.");
    }

    @Test
    public void saveOrUpdateShouldSaveNewPluginConfiguration() {
        PluginConfiguration newPluginConfiguration = new PluginConfiguration("New PluginConfiguration", true, Collections.<PluginProperty>emptyList());

        pluginConfigurationDao.saveOrUpdate(newPluginConfiguration);
        session.evict(newPluginConfiguration);
        PluginConfiguration savedPluginConfiguration = (PluginConfiguration) session.get(PluginConfiguration.class, newPluginConfiguration.getId());

        assertReflectionEquals(newPluginConfiguration, savedPluginConfiguration);
    }

    @Test
    public void saveOrUpdateShouldSavePluginConfigurationProperties() {
        PluginConfiguration pluginConfiguration = PersistedObjectsFactory.getDefaultPluginConfiguration();
        PluginProperty property = new PluginProperty("Property", PluginProperty.Type.BOOLEAN, "true");
        List<PluginProperty> properties = Arrays.asList(property);
        pluginConfiguration.setProperties(properties);

        pluginConfigurationDao.saveOrUpdate(pluginConfiguration);
        session.flush();
        session.evict(pluginConfiguration);
        PluginConfiguration updatedPluginConfiguration = (PluginConfiguration) session.get(PluginConfiguration.class, pluginConfiguration.getId());

        assertEquals(updatedPluginConfiguration.getProperties(), properties, "Plugin configuration properties should be saved.");
    }

    @Test(expectedExceptions = org.hibernate.exception.ConstraintViolationException.class)
    public void saveOrUpdateWithNullValuesShouldNotSavePlugin() {
        PluginConfiguration pluginConfiguration = PersistedObjectsFactory.getDefaultPluginConfiguration();

        pluginConfiguration.setName(null);
        pluginConfigurationDao.saveOrUpdate(pluginConfiguration);
        session.flush();
    }

    @Test
    public void getByNameShouldReturnOnePluginConfiguration() throws NotFoundException {
        PluginConfiguration pluginConfiguration = PersistedObjectsFactory.getDefaultPluginConfiguration();

        PluginConfiguration actual = pluginConfigurationDao.get(pluginConfiguration.getName());

        assertEquals(actual, pluginConfiguration);
    }

    @Test(expectedExceptions = NotFoundException.class)
    public void getShouldThrowIfDidNotFindPlugin() throws NotFoundException {
        pluginConfigurationDao.get("Some fake name");
    }

    @Test
    public void updatePropertiesShouldUpdatePassedProperties() {
        //GIVEN
        PluginProperty property = PersistedObjectsFactory.getDefaultPluginConfigurationProperty();
        String newPropertyName = "New property name";
        property.setName(newPropertyName);
        //WHEN
        pluginConfigurationDao.updateProperties(Arrays.asList(property));
        //THEN
        session.evict(property);
        PluginProperty updatedProperty = (PluginProperty) session.get(PluginProperty.class, property.getId());
        assertEquals(updatedProperty.getName(), newPropertyName, "Property should be updated");
    }
}
