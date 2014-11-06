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
import org.jtalks.jcommune.model.entity.PersistedObjectsFactory;
import org.jtalks.jcommune.model.dao.UserContactsDao;
import org.jtalks.jcommune.model.entity.ObjectsFactory;
import org.jtalks.jcommune.model.entity.UserContact;
import org.jtalks.jcommune.model.entity.UserContactType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.*;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;

/**
 * @author Michael Gamov
 */
@ContextConfiguration(locations = {"classpath:/org/jtalks/jcommune/model/entity/applicationContext-dao.xml"})
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
@Transactional
public class UserContactsHibernateDaoTest extends AbstractTransactionalTestNGSpringContextTests {

    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private UserContactsDao dao;

    private Session session;

    @BeforeMethod
    public void setUp() {
        session = sessionFactory.getCurrentSession();
        PersistedObjectsFactory.setSession(session);
    }

    @Test
    public void testSave() {
    }

    @Test
    public void testGet() {
        UserContactType type = ObjectsFactory.getDefaultUserContactType();
        dao.saveOrUpdate(type);
        UserContactType result = dao.get(type.getId());
        assertNotNull(result);
        assertEquals(type.getId(), result.getId());
        assertEquals(type.getIcon(), result.getIcon());
        assertEquals(type.getTypeName(), result.getTypeName());
        assertEquals(type.getMask(), result.getMask());
        assertEquals(type.getDisplayPattern(), result.getDisplayPattern());
        assertEquals(type.getValidationPattern(), result.getValidationPattern());
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
        dao.saveOrUpdate(type);
        session.flush();
        session.evict(type);
        UserContactType result = (UserContactType) session.get(UserContactType.class, type.getId());
        assertNotNull(result);
        assertEquals(type.getId(), result.getId());
        assertEquals(type.getIcon(), result.getIcon());
        assertEquals(type.getTypeName(), result.getTypeName());
        assertEquals(type.getMask(), result.getMask());
        assertEquals(type.getDisplayPattern(), result.getDisplayPattern());
        assertEquals(type.getValidationPattern(), result.getValidationPattern());

    }

    @Test
    public void testGetAvailableContactTypes() {
        UserContactType type = ObjectsFactory.getDefaultUserContactType();
        session.saveOrUpdate(type);
        session.clear();

        List<UserContactType> types = dao.getAvailableContactTypes();
        assertReflectionEquals(type, types.get(0));
    }

    @Test
    public void testGetContactById() {
        UserContact contact = ObjectsFactory.getDefaultUserContact();
        session.saveOrUpdate(contact.getOwner());
        session.saveOrUpdate(contact.getType());
        session.saveOrUpdate(contact);

        UserContact persisted = dao.getContactById(contact.getId());

        assertEquals(contact, persisted);
    }

    @Test
    public void testGetContactByWrongId() {
        UserContact persisted = dao.getContactById(5L);

        assertNull(persisted);
    }
}
