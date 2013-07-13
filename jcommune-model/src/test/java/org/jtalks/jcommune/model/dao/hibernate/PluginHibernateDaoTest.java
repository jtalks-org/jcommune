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
import org.jtalks.jcommune.model.PersistedObjectsFactory;
import org.jtalks.jcommune.model.entity.Plugin;
import org.jtalks.jcommune.model.entity.PluginProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Collections;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

/**
 * @author Anuar Nurmakanov
 */
@ContextConfiguration(locations = {"classpath:/org/jtalks/jcommune/model/entity/applicationContext-dao.xml"})
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
@Transactional
public class PluginHibernateDaoTest {
    @Autowired
    private SessionFactory sessionFactory;
    @Autowired
    private PluginHibernateDao pluginHibernateDao;
    private Session session;

    @BeforeMethod
    public void init() {
        this.session = sessionFactory.getCurrentSession();
        PersistedObjectsFactory.setSession(session);
    }

    /*===== Common methods =====*/

    @Test
    public void getShouldReturnPluginById() {
        Plugin plugin = PersistedObjectsFactory.getDefaultPlugin();

        Plugin foundPlugin = pluginHibernateDao.get(plugin.getId());

        assertNotNull(foundPlugin);
        assertEquals(foundPlugin.getId(), plugin.getId(),
                "Get should return plugin by it ID, so found plugin must have the same ID as passed to get.");
    }

    @Test
    public void getWithPassedIdOfNonExistPluginShouldReturnNull() {
        Plugin nonExistPlugin = pluginHibernateDao.get(-788888L);

        assertNull(nonExistPlugin, "Plugin doesn't exist, so get must return null");
    }

    @Test
    public void saveOrUpdateShouldUpdatePluginProperties() {
        String newPluginName = "Poulpe plugin";
        Plugin plugin = PersistedObjectsFactory.getDefaultPlugin();
        plugin.setName(newPluginName);

        pluginHibernateDao.saveOrUpdate(plugin);
        session.evict(plugin);
        Plugin updatedPlugin = (Plugin) session.get(Plugin.class, plugin.getId());

        assertEquals(updatedPlugin.getName(), newPluginName, "After update plugin properties must be updated.");
    }

    @Test
    public void saveOrUpdateShouldSaveNewPlugin() {
        Plugin newPlugin = new Plugin("New Plugin", true, Collections.<PluginProperty> emptyList());

        pluginHibernateDao.saveOrUpdate(newPlugin);
        session.evict(newPlugin);
        Plugin savedPlugin = (Plugin) session.get(Plugin.class, newPlugin.getId());

        assertNotNull(savedPlugin, "Plugin should be found after persisting to database.");
    }

    @Test(expectedExceptions = org.springframework.dao.DataIntegrityViolationException.class)
    public void saveOrUpdateWithNullValuesShouldNotSavePlugin() {
        Plugin plugin = PersistedObjectsFactory.getDefaultPlugin();
        session.save(plugin);

        plugin.setName(null);
        pluginHibernateDao.saveOrUpdate(plugin);
    }
}
