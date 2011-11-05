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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNotSame;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.jtalks.jcommune.model.ObjectsFactory;
import org.jtalks.jcommune.model.dao.SectionDao;
import org.jtalks.jcommune.model.entity.Branch;
import org.jtalks.jcommune.model.entity.Section;
import org.jtalks.jcommune.model.entity.Topic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

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
    private Session session;

    @BeforeMethod
    public void setUp() throws Exception {
        session = sessionFactory.getCurrentSession();
        ObjectsFactory.setSession(session);
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
    @Test(expectedExceptions = DataIntegrityViolationException.class)
    public void testSaveSectionWithNameNotNullViolation() {
        Section section = ObjectsFactory.getDefaultSection();
        session.save(section);
        section.setName(null);

        dao.saveOrUpdate(section);
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
    public void testUpdate() {
        String newName = "new name";
        Section section = ObjectsFactory.getDefaultSection();
        session.save(section);
        section.setName(newName);

        dao.saveOrUpdate(section);
        session.evict(section);
        Section result = (Section) session.get(Section.class, section.getId());

        assertEquals(result.getName(), newName);
    }

    @Test(expectedExceptions = Exception.class)
    public void testUpdateNotNullViolation() {
        Section section = ObjectsFactory.getDefaultSection();
        session.save(section);
        section.setName(null);

        dao.saveOrUpdate(section);
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
        section.addBranch(branch);
        session.save(section);

        List<Section> sectionList = dao.getAll();

        assertEquals(sectionList.get(0).getBranches().get(0).getTopicCount(), 1);
    }

    private int getSectionCount() {
        return ((Number) session.createQuery("select count(*) from Section").uniqueResult()).intValue();
    }
}
