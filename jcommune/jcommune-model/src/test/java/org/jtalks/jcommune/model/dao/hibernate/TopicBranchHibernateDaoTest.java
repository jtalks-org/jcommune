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
package org.jtalks.jcommune.model.dao.hibernate;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.jtalks.jcommune.model.dao.TopicBranchDao;
import org.jtalks.jcommune.model.entity.TopicBranch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
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
 * @author Kirill Afonin
 */
@ContextConfiguration(locations = {"classpath:/org/jtalks/jcommune/model/entity/applicationContext-dao.xml"})
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
@Transactional
public class TopicBranchHibernateDaoTest extends AbstractTransactionalTestNGSpringContextTests {

    @Autowired
    private SessionFactory sessionFactory;
    @Autowired
    private TopicBranchDao dao;
    private Session session;

    @BeforeMethod
    public void setUp() throws Exception {
        session = sessionFactory.getCurrentSession();
        ObjectsFactory.setSession(session);
    }

    /*===== Common methods =====*/

    @Test
    public void testSave() {
        TopicBranch branch = ObjectsFactory.getDefaultTopicBranch();

        dao.saveOrUpdate(branch);

        assertNotSame(branch.getId(), 0, "Id not created");

        session.evict(branch);
        TopicBranch result = (TopicBranch) session.get(TopicBranch.class, branch.getId());

        assertReflectionEquals(branch, result);
    }


    @Test(expectedExceptions = DataIntegrityViolationException.class)
    public void testSaveBranchWithNameNotNullViolation() {
        TopicBranch branch = new TopicBranch();

        dao.saveOrUpdate(branch);
    }

    @Test
    public void testGet() {
        TopicBranch branch = ObjectsFactory.getDefaultTopicBranch();
        session.save(branch);

        TopicBranch result = dao.get(branch.getId());

        assertNotNull(result);
        assertEquals(result.getId(), branch.getId());
    }

    @Test
    public void testGetInvalidId() {
        TopicBranch result = dao.get(-567890L);

        assertNull(result);
    }

    @Test
    public void testUpdate() {
        String newName = "new name";
        TopicBranch branch = ObjectsFactory.getDefaultTopicBranch();
        session.save(branch);
        branch.setName(newName);

        dao.saveOrUpdate(branch);
        session.evict(branch);
        TopicBranch result = (TopicBranch) session.get(TopicBranch.class, branch.getId());

        assertEquals(result.getName(), newName);
    }

    @Test(expectedExceptions = DataIntegrityViolationException.class)
    public void testUpdateNotNullViolation() {
        TopicBranch branch = ObjectsFactory.getDefaultTopicBranch();
        session.save(branch);
        branch.setName(null);

        dao.saveOrUpdate(branch);
    }

    @Test
    public void testDelete() {
        TopicBranch branch = ObjectsFactory.getDefaultTopicBranch();
        session.save(branch);

        boolean result = dao.delete(branch.getId());
        int branchCount = getCount();

        assertTrue(result, "Entity is not deleted");
        assertEquals(branchCount, 0);
    }

    @Test
    public void testDeleteInvalidId() {
        boolean result = dao.delete(-100500L);

        assertFalse(result, "Entity deleted");
    }

    @Test
    public void testGetAll() {
        TopicBranch branch1 = ObjectsFactory.getDefaultTopicBranch();
        session.save(branch1);
        TopicBranch branch2 = ObjectsFactory.getDefaultTopicBranch();
        session.save(branch2);

        List<TopicBranch> branches = dao.getAll();

        assertEquals(branches.size(), 2);
    }

    @Test
    public void testGetAllWithEmptyTable() {
        List<TopicBranch> branches = dao.getAll();

        assertTrue(branches.isEmpty());
    }

    private int getCount() {
        return ((Number) session.createQuery("select count(*) from TopicBranch").uniqueResult()).intValue();
    }
}
