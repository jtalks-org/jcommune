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
import org.jtalks.jcommune.model.dao.BranchDao;
import org.jtalks.jcommune.model.entity.Branch;
import org.jtalks.jcommune.model.entity.Post;
import org.jtalks.jcommune.model.entity.Section;
import org.jtalks.jcommune.model.entity.Topic;
import org.jtalks.jcommune.model.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNotSame;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;

/**
 * @author Kirill Afonin
 */
@ContextConfiguration(locations = {"classpath:/org/jtalks/jcommune/model/entity/applicationContext-dao.xml"})
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
@Transactional
public class BranchHibernateDaoTest extends AbstractTransactionalTestNGSpringContextTests {

    @Autowired
    private SessionFactory sessionFactory;
    @Autowired
    private BranchDao dao;
    private Session session;

    @BeforeMethod
    public void setUp() throws Exception {
        session = sessionFactory.getCurrentSession();
        ObjectsFactory.setSession(session);
    }

    /*===== Common methods =====*/

    @Test
    public void testSave() {
        Branch branch = ObjectsFactory.getDefaultBranch();

        dao.update(branch);

        assertNotSame(branch.getId(), 0, "Id not created");

        session.evict(branch);
        Branch result = (Branch) session.get(Branch.class, branch.getId());

        assertReflectionEquals(branch, result);
    }


    @Test(expectedExceptions = DataIntegrityViolationException.class)
    public void testSaveBranchWithNameNotNullViolation() {
        Branch branch = ObjectsFactory.getDefaultBranch();
        session.save(branch);
        branch.setName(null);

        dao.update(branch);
    }

    @Test
    public void testGet() {
        Branch branch = ObjectsFactory.getDefaultBranch();
        session.save(branch);

        Branch result = dao.get(branch.getId());

        assertNotNull(result);
        assertEquals(result.getId(), branch.getId());
    }

    @Test
    public void testGetInvalidId() {
        Branch result = dao.get(-567890L);

        assertNull(result);
    }

    @Test
    public void testUpdate() {
        String newName = "new name";
        Branch branch = ObjectsFactory.getDefaultBranch();
        session.save(branch);
        branch.setName(newName);

        dao.update(branch);
        session.evict(branch);
        Branch result = (Branch) session.get(Branch.class, branch.getId());

        assertEquals(result.getName(), newName);
    }

    @Test(expectedExceptions = Exception.class)
    public void testUpdateNotNullViolation() {
        Branch branch = ObjectsFactory.getDefaultBranch();
        session.save(branch);
        branch.setName(null);

        dao.update(branch);
    }

    @Test
    public void testIsExist() {
        Branch branch = ObjectsFactory.getDefaultBranch();
        session.save(branch);

        assertTrue(dao.isExist(branch.getId()));
    }

    @Test
    public void testIsNotExist() {
        assertFalse(dao.isExist(99999L));
    }

    @Test
    public void testDeleteTopicFromBranchCascade() {
        Branch branch = ObjectsFactory.getDefaultBranch();
        User author = ObjectsFactory.getDefaultUser();
        session.save(author);
        Topic topic = new Topic(author, "title");
        Post post = new Post(author, "content");
        topic.addPost(post);
        branch.addTopic(topic);
        session.save(branch);

        branch.deleteTopic(topic);
        dao.update(branch);
        session.flush();

        assertEquals(getBranchCount(), 1);
        assertEquals(((Number) session.createQuery("select count(*) from Topic").uniqueResult()).intValue(), 0);
        assertEquals(((Number) session.createQuery("select count(*) from Post").uniqueResult()).intValue(), 0);
    }

    private int getBranchCount() {
        return ((Number) session.createQuery("select count(*) from Branch").uniqueResult()).intValue();
    }

    private List<Branch> createAndSaveBranchList(int size) {
        List<Branch> branches = new ArrayList<Branch>();
        Section section = ObjectsFactory.getDefaultSection();
        for (int i = 0; i < size; i++) {
            Branch newBranch = new Branch("Branch #" + i);
            section.addBranch(newBranch);
            branches.add(newBranch);
        }
        session.save(section);
        return branches;
    }

    @Test
    public void testGetBranchesInSection() {
        List<Branch> persistedBranches = createAndSaveBranchList(5);
        long sectionId = persistedBranches.get(0).getSection().getId();

        List<Branch> branches = dao.getBranchesInSection(sectionId);

        assertEquals(sectionId, branches.get(0).getSection().getId(), "Incorrect section");
    }
    
    @Test
    public void testGetBranchesInSectionCount() {
        List<Branch> persistedBranches = createAndSaveBranchList(5);
        long sectionId = persistedBranches.get(0).getSection().getId();

        int count = dao.getBranchesInSectionCount(sectionId);

        assertEquals(count, 5);
    }
}
