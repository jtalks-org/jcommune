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
import org.jtalks.jcommune.model.ObjectsFactory;
import org.jtalks.jcommune.model.entity.UserContactType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.*;

/**
 * @author Michael Gamov
 */
@ContextConfiguration(locations = {"classpath:/org/jtalks/jcommune/model/entity/applicationContext-dao.xml"})
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
@Transactional
public class UserContactsHibernateDaoTest {

    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private UserContactsHibernateDao dao;

    private Session session;

    @BeforeMethod
    public void setUp() {
        sessionFactory.getCurrentSession();
        ObjectsFactory.setSession(session);
    }

    @Test
    public void testSave(){
    }

    @Test
    public void testGet() {
        UserContactType type = ObjectsFactory.getDefaultUserContactType();
        session.save(type);
        
        UserContactType result = dao.get(type.getId());
        
        assertNotNull(result);
        assertEquals(type.getId(), result.getId());
        assertEquals(type.getIcon(), result.getIcon());
        assertEquals(type.getTypeName(), result.getTypeName());
    }

    @Test
    public void testGetInvalidId() {
        UserContactType type = dao.get(-12345L);
        
        assertNull(type);
    }

    @Test
    public void testUpdate() {
        String newName = "New contact type";
        String newIcon = "/new/icon";
        UserContactType type = ObjectsFactory.getDefaultUserContactType();
        session.save(type);
        type.setTypeName(newName);
        type.setIcon(newIcon);

        dao.update(type);
        session.evict(type);
        
        UserContactType result = (UserContactType) session.get(UserContactType.class, type.getId());

        

    }
    
    @Test
    public void testGetAvailableContactTypes() {
        UserContactType type = ObjectsFactory.getDefaultUserContactType();
        session.saveOrUpdate(type);

        List<UserContactType> types = dao.getAvailableContactTypes();
        assertEquals(types.size(), 1);
        assertTrue(types.contains(type));
    }
}
