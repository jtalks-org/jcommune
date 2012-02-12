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
import org.jtalks.jcommune.model.dao.UserDao;
import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.model.entity.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.testng.Assert.*;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;

/**
 * @author Kirill Afonin
 * @author Osadchuck Eugeny
 */
@ContextConfiguration(locations = {"classpath:/org/jtalks/jcommune/model/entity/applicationContext-dao.xml"})
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
@Transactional
public class UserHibernateDaoTest extends AbstractTransactionalTestNGSpringContextTests {
    @Autowired
    private UserDao dao;
    @Autowired
    private SessionFactory sessionFactory;
    private Session session;

    @BeforeMethod
    public void setUp() throws Exception {
        session = sessionFactory.getCurrentSession();
        ObjectsFactory.setSession(session);
    }

    /*===== Common methods =====*/

    @Test
    public void testSave() {
        JCUser user = ObjectsFactory.getDefaultUser();

        dao.saveOrUpdate(user);

        assertNotSame(user.getId(), 0, "Id not created");

        session.evict(user);
        JCUser result = (JCUser) session.get(JCUser.class, user.getId());

        assertReflectionEquals(user, result);
    }

    @Test(expectedExceptions = DataIntegrityViolationException.class)
    public void testSaveUserWithUniqueViolation() {
        JCUser user = ObjectsFactory.getDefaultUser();
        JCUser user2 = ObjectsFactory.getDefaultUser();

        dao.saveOrUpdate(user);
        dao.saveOrUpdate(user2);
    }

    @Test
    public void testGet() {
        JCUser user = ObjectsFactory.getDefaultUser();
        session.save(user);

        JCUser result = dao.get(user.getId());

        assertNotNull(result);
        assertEquals(result.getId(), user.getId());
    }

    @Test
    public void testGetInvalidId() {
        JCUser result = dao.get(-567890L);

        assertNull(result);
    }

    @Test
    public void testUpdate() {
        String newName = "new name";
        JCUser user = ObjectsFactory.getDefaultUser();
        session.save(user);
        user.setFirstName(newName);

        dao.saveOrUpdate(user);
        session.evict(user);
        JCUser result = (JCUser) session.get(JCUser.class, user.getId());//!

        assertEquals(result.getFirstName(), newName);
    }

    @Test(expectedExceptions = DataIntegrityViolationException.class)
    public void testUpdateNotNullViolation() {
        JCUser user = ObjectsFactory.getDefaultUser();
        session.save(user);
        user.setEmail(null);

        dao.saveOrUpdate(user);
    }

    @Test
    public void testDelete() {
        JCUser user = ObjectsFactory.getDefaultUser();
        session.save(user);

        boolean result = dao.delete(user.getId());
        int userCount = getCount();

        assertTrue(result, "Entity is not deleted");
        assertEquals(userCount, 0);
    }

    @Test
    public void testDeleteInvalidId() {
        boolean result = dao.delete(-100500L);

        assertFalse(result, "Entity deleted");
    }

    /*===== UserDao specific methods =====*/

    @Test
    public void testGetByUsername() {
        JCUser user = ObjectsFactory.getDefaultUser();
        session.save(user);

        JCUser result = dao.getByUsername(user.getUsername());

        assertNotNull(result);
        assertReflectionEquals(user, result);
    }

    @Test
    public void testGetByUsernameNotExist() {
        JCUser user = ObjectsFactory.getDefaultUser();
        session.save(user);

        JCUser result = dao.getByUsername("Name");

        assertNull(result);
    }

    @Test
    public void testGetByUuid() {
        JCUser user = ObjectsFactory.getDefaultUser();
        String uuid = user.getUuid();
        session.save(user);

        JCUser result = dao.getByUuid(uuid);

        assertReflectionEquals(user, result);
    }

    @Test
    public void testGetByUuidNotExist() {
        JCUser user = ObjectsFactory.getDefaultUser();
        session.save(user);

        JCUser result = dao.getByUuid("uuid");

        assertNull(result);
    }

    @Test
    public void testFetchByEMail() {
        JCUser user = ObjectsFactory.getDefaultUser();
        session.save(user);
        assertNotNull(dao.getByEmail(user.getEmail()));
    }

    @Test
    public void testFetchNonActivatedAccounts() {
        JCUser activated = new JCUser("login", "email", "password");
        activated.setEnabled(true);
        JCUser nonActivated = ObjectsFactory.getDefaultUser();
        session.save(activated);
        session.save(nonActivated);

        Collection<JCUser> users = dao.getNonActivatedUsers();

        assertTrue(users.contains(nonActivated));
        assertEquals(users.size(), 1);
    }


    private int getCount() {
        return ((Number) session.createQuery("select count(*) from JCUser").uniqueResult()).intValue();
    }
}
