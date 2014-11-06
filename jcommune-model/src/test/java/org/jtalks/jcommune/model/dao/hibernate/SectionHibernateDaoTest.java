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
import org.jtalks.common.model.entity.Group;
import org.jtalks.common.model.entity.Section;
import org.jtalks.jcommune.model.entity.PersistedObjectsFactory;
import org.jtalks.jcommune.model.dao.BranchDao;
import org.jtalks.jcommune.model.dao.SectionDao;
import org.jtalks.jcommune.model.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng
        .AbstractTransactionalTestNGSpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.*;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;

/**
 * @author Max Malakhov
 */
@ContextConfiguration(locations = {"classpath:/org/jtalks/jcommune/model/entity/applicationContext-dao.xml"})
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
@Transactional
public class SectionHibernateDaoTest extends AbstractTransactionalTestNGSpringContextTests {

    @Autowired
    private SessionFactory sessionFactory;
    @Autowired
    private SectionDao dao;
    @Autowired
    private BranchDao branchDao;
    private Session session;

    @BeforeMethod
    public void setUp() throws Exception {
        session = sessionFactory.getCurrentSession();
        PersistedObjectsFactory.setSession(session);
    }

    /*===== Common methods =====*/

    @Test
    public void testSave() {
        Section section = ObjectsFactory.getDefaultSection();

        dao.saveOrUpdate(section);

        assertNotSame(section.getId(), 0, "Id not created");

        session.evict(section);
        Section result = (Section) session.get(Section.class, section.getId());

        assertReflectionEquals(section, result);
    }

    @Test(expectedExceptions = ConstraintViolationException.class)
    public void testSaveSectionWithNameNotNullViolation() {
        Section section = ObjectsFactory.getDefaultSection();
        session.save(section);
        section.setName(null);

        dao.saveOrUpdate(section);
        session.flush();
    }

    @Test
    public void testGet() {
        Section section = ObjectsFactory.getDefaultSection();
        session.save(section);

        Section result = dao.get(section.getId());

        assertNotNull(result);
        assertEquals(result.getId(), section.getId());
    }

    @Test
    public void testGetInvalidId() {
        Section result = dao.get(-567890L);

        assertNull(result);
    }

    @Test
    public void testBranchesCascadingDeletesFromSection() {
        Branch actualBranch = ObjectsFactory.getDefaultBranch();
        Section section = ObjectsFactory.getDefaultSection();
        section.addOrUpdateBranch(actualBranch);
        branchDao.saveOrUpdate(actualBranch);
        dao.saveOrUpdate(section);
        session.flush();
        Branch expectedBranch = branchDao.get(actualBranch.getId());
        assertEquals(expectedBranch.getName(), actualBranch.getName());
        section.deleteBranch(actualBranch);
        dao.saveOrUpdate(section);
        session.flush();
        session.clear();
        expectedBranch = branchDao.get(actualBranch.getId());
        assertNull(expectedBranch);
    }

    @Test
    public void testUpdate() {
        String newName = "new name";
        Section section = ObjectsFactory.getDefaultSection();
        session.save(section);
        section.setName(newName);

        dao.saveOrUpdate(section);
        session.flush();
        session.evict(section);
        Section result = (Section) session.get(Section.class, section.getId());

        assertEquals(result.getName(), newName);
    }

    @Test(expectedExceptions = javax.validation.ConstraintViolationException.class)
    public void testUpdateNotNullViolation() {
        Section section = ObjectsFactory.getDefaultSection();
        session.save(section);
        section.setName(null);
        dao.saveOrUpdate(section);
        session.flush();
    }

    @Test
    public void testDelete() {
        Section section = ObjectsFactory.getDefaultSection();
        session.save(section);

        boolean result = dao.delete(section.getId());
        int sectionCount = getSectionCount();

        assertTrue(result, "Entity is not deleted");
        assertEquals(sectionCount, 0);
    }

    @Test
    public void testDeleteInvalidId() {
        boolean result = dao.delete(-100500L);

        assertFalse(result, "Entity deleted");
    }

    @Test
    public void testGetAll() {
        Section section1 = ObjectsFactory.getDefaultSection();
        session.save(section1);
        Section section2 = ObjectsFactory.getDefaultSection();
        session.save(section2);

        List<Section> sectiones = dao.getAll();

        assertEquals(sectiones.size(), 2);
    }

    @Test
    public void testGetAllWithEmptyTable() {
        List<Section> sectiones = dao.getAll();

        assertTrue(sectiones.isEmpty());
    }

    @Test
    public void testIsExist() {
        Section section = ObjectsFactory.getDefaultSection();
        session.save(section);

        assertTrue(dao.isExist(section.getId()));
    }

    @Test
    public void testIsNotExist() {
        assertFalse(dao.isExist(99999L));
    }

    @Test
    public void testGetAllTopicInBranchCount() {
        Section section = ObjectsFactory.getDefaultSection();
        Branch branch = ObjectsFactory.getDefaultBranch();
        Topic topic = ObjectsFactory.getDefaultTopic();
        branch.addTopic(topic);
        section.addOrUpdateBranch(branch);
        session.save(section);

        List<Section> sectionList = dao.getAll();

        assertEquals(((Branch) sectionList.get(0).getBranches().get(0)).getTopicCount(), 1);
    }

    @Test
    public void testTopicInBranch() {
        Section section = ObjectsFactory.getDefaultSection();
        Branch branch = ObjectsFactory.getDefaultBranch();
        Topic topic = ObjectsFactory.getDefaultTopic();
        section.addOrUpdateBranch(branch);
        session.save(section);

        Section sectionTwo = dao.get(1L);
        Branch branchTwo = (Branch) section.getBranches().get(0);

        assertEquals(branchTwo.getTopicCount(), 0);
    }

    @Test
    public void testGetCountAvailableBranches() {
        JCUser user = ObjectsFactory.getDefaultUser();
        assertTrue(dao.getCountAvailableBranches(user,
                new ArrayList<org.jtalks.common.model.entity.Branch>()) == 0);
        user.setGroups(new ArrayList<Group>());

        List<Branch> branches = ObjectsFactory.getDefaultBranchList();
        assertTrue(dao.getCountAvailableBranches(user,
                new ArrayList<org.jtalks.common.model.entity.Branch>(branches)) == 0);


        List<Group> groups = ObjectsFactory.getDefaultGroupList();
        user.setGroups(groups);
        assertTrue(dao.getCountAvailableBranches(user,
                new ArrayList<org.jtalks.common.model.entity.Branch>(branches)) == 0);
        assertTrue(dao.getCountAvailableBranches(new AnonymousUser(),
                new ArrayList<org.jtalks.common.model.entity.Branch>(branches)) == 0);
    }

    @Test
    public void getAvailableBranchIdsShouldReturnEmptyListForEmptyBranchList() {
        JCUser user = ObjectsFactory.getDefaultUser();
        assertEquals(dao.getAvailableBranchIds(user,
                new ArrayList<org.jtalks.common.model.entity.Branch>()).size(), 0);
    }

    @Test
    public void getAvailableBranchIdsShouldReturnEmptyListWhenUserHasNoGroupAssigned() {
        JCUser user = ObjectsFactory.getDefaultUser();
        user.setGroups(new ArrayList<Group>());
        List<Branch> branches = ObjectsFactory.getDefaultBranchList();
        assertEquals(dao.getAvailableBranchIds(user,
                new ArrayList<org.jtalks.common.model.entity.Branch>(branches)).size(), 0);
    }

    @Test
    public void getAvailableBranchIdsShouldReturnEmptyListWhenAnonymousUserHasNoGroupAssigned() {
        List<Branch> branches = ObjectsFactory.getDefaultBranchList();
        assertEquals(dao.getAvailableBranchIds(new AnonymousUser(),
                new ArrayList<org.jtalks.common.model.entity.Branch>(branches)).size(), 0);
    }

    private int getSectionCount() {
        return ((Number) session.createQuery("select count(*) from org.jtalks.common.model.entity.Section").uniqueResult()).intValue();
    }
}
