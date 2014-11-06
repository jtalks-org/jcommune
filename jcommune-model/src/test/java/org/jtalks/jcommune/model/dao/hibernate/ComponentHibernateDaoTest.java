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

import org.hibernate.SessionFactory;
import org.hibernate.classic.Session;
import org.jtalks.common.model.entity.Component;
import org.jtalks.common.model.entity.ComponentType;
import org.jtalks.jcommune.model.entity.PersistedObjectsFactory;
import org.jtalks.jcommune.model.dao.ComponentDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * @autor masyan
 */
@ContextConfiguration(locations = {"classpath:/org/jtalks/jcommune/model/entity/applicationContext-dao.xml"})
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
@Transactional
public class ComponentHibernateDaoTest extends AbstractTransactionalTestNGSpringContextTests {
    private static final String PROPERTY_NAME = "property_name";
    private static final String PROPERTY_DESCRIPTION = "property_description";
    private Component cmp = new Component(PROPERTY_NAME, PROPERTY_DESCRIPTION, ComponentType.FORUM);
    @Autowired
    private SessionFactory sessionFactory;
    @Autowired
    private ComponentDao componentDao;
    private Session session;

    @BeforeMethod
    public void setUp() {
        session = sessionFactory.getCurrentSession();
        PersistedObjectsFactory.setSession(session);
    }
    
    /*===== Common methods =====*/

    @Test
    public void testGet() {
        Component component = PersistedObjectsFactory.getDefaultComponent();
        session.save(component);

        Component result = componentDao.get(component.getId());

        assertNotNull(result);
        assertEquals(result.getId(), component.getId());
        assertEquals(result.getProperties().size(), 2);
    }


    @Test
    public void testGetInvalidId() {
        Component result = componentDao.get(-567890L);

        assertNull(result);
    }

    @Test
    public void testUpdate() {
        String newUuid = "1234-1231-1231";
        Component component = PersistedObjectsFactory.getDefaultComponent();
        session.save(component);
        component.setUuid(newUuid);

        componentDao.saveOrUpdate(component);
        session.flush();
        session.evict(component);
        Component result = (Component) session.get(Component.class, component.getId());

        assertEquals(result.getUuid(), newUuid);
    }

    @Test(expectedExceptions = javax.validation.ValidationException.class)
    public void testUpdateUuidNotNullViolation() {
        Component component = PersistedObjectsFactory.getDefaultComponent();
        session.save(component);
        component.setUuid(null);

        componentDao.saveOrUpdate(component);
        session.flush();
    }

    @Test
    public void testOrphanRemoving() {
        Component component = PersistedObjectsFactory.getDefaultComponent();

        component.getProperties().remove(0);
        componentDao.saveOrUpdate(component);
        session.flush();
        session.evict(component);

        assertEquals(componentDao.get(component.getId()).getProperties().size(), 1);
    }
    
    /*===== End of common methods =====*/

    @Test
    public void testGetComponent() {
        session.save(cmp);

        Component result = componentDao.getComponent();

        assertNotNull(result, "Property is not found by name.");
        assertEquals(result.getId(), cmp.getId(), "Property not found");
    }
}
