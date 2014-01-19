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
import org.jtalks.common.model.entity.Property;
import org.jtalks.jcommune.model.dao.PropertyDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * 
 * @author Anuar Nurmakanov
 *
 */
@ContextConfiguration(locations = {"classpath:/org/jtalks/jcommune/model/entity/applicationContext-dao.xml"})
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
@Transactional
public class PropertyHibernateDaoTest  extends AbstractTransactionalTestNGSpringContextTests {
    private static final String PROPERTY_NAME = "property_name";
    private Property property = new Property(PROPERTY_NAME, "value");
    @Autowired
    private SessionFactory sessionFactory;
    @Autowired
    private PropertyDao propertyDao;
    private Session session;
    
    @BeforeMethod
    public void setUp() {
        session = sessionFactory.getCurrentSession();
    }
    
    /*===== Common methods =====*/
    
    @Test(expectedExceptions = {UnsupportedOperationException.class})
    public void testUpdate() {
        propertyDao.saveOrUpdate(property);
    }
    
    @Test
    public void testGet() {
        session.save(property);

        Property result = propertyDao.get(property.getId());

        assertNotNull(result, "Property not found");
        assertEquals(result.getId(), property.getId(), "Property not found");
    }
    
    /*===== Specific methods =====*/
    @Test
    public void testGetByName() {
        session.save(property);

        Property result = propertyDao.getByName(PROPERTY_NAME);
        
        assertNotNull(result, "Property is not found by name.");
        assertEquals(result.getId(), property.getId(), "Property not found");
    }
}
