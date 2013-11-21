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
package org.jtalks.jcommune.model.dao.hibernate.security;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.jtalks.common.model.entity.Group;
import org.jtalks.common.model.entity.Section;
import org.jtalks.jcommune.model.entity.PersistedObjectsFactory;
import org.jtalks.jcommune.model.dao.security.GroupDao;
import org.jtalks.jcommune.model.entity.Branch;
import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.model.entity.ObjectsFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.*;
import static org.testng.Assert.assertEquals;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;

/**
 * @author Leonid Kazancev
 */
@ContextConfiguration(locations = {"classpath:/org/jtalks/jcommune/model/entity/applicationContext-dao.xml"})
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
@Transactional
public class GroupHibernateDaoTest extends AbstractTransactionalTestNGSpringContextTests {
    static final String NO_FILTER = "";

    @Autowired
    private GroupDao groupDao;

    @Autowired
    private SessionFactory sessionFactory;

    private Session session;

    @BeforeMethod
    public void setUp() throws Exception {
        session = sessionFactory.getCurrentSession();
        PersistedObjectsFactory.setSession(session);
    }

    @Test
    public void testSave() {
        Group group = ObjectsFactory.getRandomGroup();

        saveAndEvict(group);
        Group savedGroup = (Group) session.get(Group.class, group.getId());

        assertReflectionEquals(group, savedGroup);
    }

    @Test
    public void testSaveIdGeneration() {
        Group group = ObjectsFactory.getRandomGroup();
        long initialId = 0;
        group.setId(initialId);

        saveAndEvict(group);

        assertNotSame(group.getId(), initialId, "ID is not created");
    }

    @Test
    public void testGetById() {
        Group group = ObjectsFactory.getRandomGroup();

        saveAndEvict(group);

        Group actual = groupDao.get(group.getId());
        assertReflectionEquals(actual, group);
    }


    @Test
    public void testGetAll() {
        Group group0 = ObjectsFactory.getRandomGroup();
        saveAndEvict(group0);

        Group group1 = ObjectsFactory.getRandomGroup();
        saveAndEvict(group1);

        List<Group> actual = groupDao.getAll();
        assertEquals(actual.size(), 2);
        assertReflectionEquals(actual.get(0), group0);
        assertReflectionEquals(actual.get(1), group1);
    }

    @Test
    public void testGetByNameContains() {
        Group group = ObjectsFactory.getRandomGroup();

        saveAndEvict(group);

        List<Group> actual = groupDao.getByNameContains(group.getName());
        assertEquals(actual.size(), 1);
        assertReflectionEquals(actual.get(0), group);
    }

    @Test
    public void testGetByNameContainsWithEmptyName() {
        Group group = ObjectsFactory.getRandomGroup();
        saveAndEvict(group);

        group = ObjectsFactory.getRandomGroup();
        saveAndEvict(group);

        List<Group> actual = groupDao.getByNameContains(NO_FILTER);
        List<Group> all = groupDao.getAll();
        assertEquals(actual, all);
    }

    @Test
    public void testGetByName() {
        Group group = ObjectsFactory.getRandomGroup();

        saveAndEvict(group);

        Group actual = groupDao.getByName(group.getName());
        assertReflectionEquals(actual, group);
    }

    @Test
    public void testGetByNameFailWithEmptyString() {
        Group group = ObjectsFactory.getRandomGroup();

        saveAndEvict(group);

        Group actual = groupDao.getByName(NO_FILTER);
        assertNull(actual);
    }

    @Test
    public void testGetByNameLowerCase() {
        Group group = ObjectsFactory.getRandomGroup();

        saveAndEvict(group);

        Group actual = groupDao.getByName(group.getName().toLowerCase());
        assertReflectionEquals(actual, group);
    }

    @Test
    public void testGetByNameUpperCase() {
        Group group = ObjectsFactory.getRandomGroup();

        saveAndEvict(group);

        Group actual = groupDao.getByName(group.getName().toUpperCase());
        assertReflectionEquals(actual, group);
    }

    @Test
    public void testGetByNameWithSpecialChars() {
        Group group = ObjectsFactory.getRandomGroup();
        group.setName("!@#$%^&*()\"\'\\/");
        saveAndEvict(group);

        Group actual = groupDao.getByName(group.getName());
        assertReflectionEquals(actual, group);
    }


    @Test
    public void getGetUsersCount() {
        int count = 5;
        Group group = PersistedObjectsFactory.groupWithUsers(count);
        int actual = groupDao.get(group.getId()).getUsers().size();
        assertEquals(actual, count);
    }

    @Test
    public void testDeleteGroup() {
        Group group = ObjectsFactory.getRandomGroup();
        saveAndEvict(group);

        groupDao.delete(group);
        Group actual = groupDao.get(group.getId());
        assertNull(actual);
    }


    private void saveAndEvict(Branch branch) {
        saveAndEvict(branch.getModeratorsGroup());
        Section section = ObjectsFactory.getDefaultSection();
        branch.setSection(section);
        session.save(section);
        session.save(branch);
        session.evict(branch);
        session.evict(section);
    }

    private void saveAndEvict(JCUser user) {
        session.save(user);
        session.evict(user);
    }

    private void saveAndEvict(Group group) {
        saveAndEvict((Iterable<JCUser>) (Object) group.getUsers());
        session.save(group);
        session.evict(group);
    }

    private void saveAndEvict(Iterable<JCUser> users) {
        for (JCUser user : users) {
            saveAndEvict(user);
        }
    }


}

