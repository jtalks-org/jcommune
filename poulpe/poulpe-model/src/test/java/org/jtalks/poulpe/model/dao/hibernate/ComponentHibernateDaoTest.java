/**
 * Copyright (C) 2011  jtalks.org Team
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
 * Also add information on how to contact you by electronic and paper mail.
 * Creation date: Apr 12, 2011 / 8:05:19 PM
 * The jtalks.org Project
 */
package org.jtalks.poulpe.model.dao.hibernate;

import java.util.Arrays;
import java.util.Set;
import org.testng.annotations.BeforeMethod;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.jtalks.poulpe.model.dao.ComponentDao;
import org.jtalks.poulpe.model.entity.Component;
import org.jtalks.poulpe.model.entity.ComponentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

/**
 *
 * @author Pavel Vervenko
 */
@ContextConfiguration(locations = {"classpath:/org/jtalks/poulpe/model/entity/applicationContext-dao.xml"})
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
@Transactional
public class ComponentHibernateDaoTest extends AbstractTransactionalTestNGSpringContextTests {

    @Autowired
    private SessionFactory sessionFactory;
    @Autowired
    private ComponentDao dao;
    private Session session;

    @BeforeMethod
    public void setUp() throws Exception {
        session = sessionFactory.getCurrentSession();
    }

    @Test
    public void testGetAll() {
        Component cmp1 = getComponent("name1", "desc1", ComponentType.ARTICLE);
        session.save(cmp1);
        Component cmp2 = getComponent("name2", "desc2", ComponentType.FORUM);
        session.save(cmp2);

        List<Component> cList = dao.getAll();

        assertEquals(cList.size(), 2);
    }

    @Test
    public void testGetAvailableTypes() {
        Set<ComponentType> availableTypes = dao.getAvailableTypes();

        assertEquals(availableTypes.size(), ComponentType.values().length);
        assertTrue(availableTypes.containsAll(Arrays.asList(ComponentType.values())));
    }

    @Test
    public void testGetAvailableTypesAfterInsert() {
        ComponentType usedType = ComponentType.FORUM;
        session.save(getComponent("name", "desc", usedType));
        Set<ComponentType> availableTypes = dao.getAvailableTypes();

        assertFalse(availableTypes.contains(usedType));
    }

    private Component getComponent(String name, String desc, ComponentType componentType) {
        Component component = new Component();
        component.setName(name);
        component.setDescription(desc);
        component.setComponentType(componentType);
        return component;
    }
}
