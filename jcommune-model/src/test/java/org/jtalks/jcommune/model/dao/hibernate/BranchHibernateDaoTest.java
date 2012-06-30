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
import org.jtalks.common.model.entity.Section;
import org.jtalks.jcommune.model.ObjectsFactory;
import org.jtalks.jcommune.model.PersistedObjectFactory;
import org.jtalks.jcommune.model.dao.BranchDao;
import org.jtalks.jcommune.model.entity.Branch;
import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.model.entity.Post;
import org.jtalks.jcommune.model.entity.Topic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.validation.ConstraintViolationException;
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
    Branch branch;

    @BeforeMethod
    public void setUp() throws Exception {
        session = sessionFactory.getCurrentSession();
        PersistedObjectFactory.setSession(session);
        branch = ObjectsFactory.getDefaultBranch();
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


    @Test(expectedExceptions = ConstraintViolationException.class)
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
        JCUser author = ObjectsFactory.getDefaultUser();
        session.save(author);
        Topic topic = new Topic(author, "title");
        Post post = new Post(author, "content");
        topic.addPost(post);
        branch.addTopic(topic);
        session.save(branch);

        branch.deleteTopic(topic);
        dao.update(branch);
        session.flush();

        assertEquals(getCount("select count(*) from org.jtalks.jcommune.model.entity.Branch"), 1);
        assertEquals(getCount("select count(*) from Topic"), 0);
        assertEquals(getCount("select count(*) from Post"), 0);
    }

    private int getCount(String hql) {
        return ((Number) session.createQuery(hql).uniqueResult()).intValue();
    }

    private List<Branch> createAndSaveBranchList(int size) {
        List<Branch> branches = new ArrayList<Branch>();
        Section section = ObjectsFactory.getDefaultSection();
        for (int i = 0; i < size; i++) {
            Branch newBranch = new Branch("Branch #" + i, "Branch #" + i);
            section.addOrUpdateBranch(newBranch);
            newBranch.setSection(section);
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
    public void testGetAllBranches() {
        List<Branch> persistedBranches = createAndSaveBranchList(5);

        List<Branch> branches = dao.getAllBranches();

        assertEquals(persistedBranches.size(), branches.size());
    }
    
    @Test
    public void testGetCountTopicsInBranch() {
        //this topic is persisted
        Topic topic = PersistedObjectFactory.getDefaultTopic();
        JCUser user = topic.getTopicStarter();
        Branch branch = topic.getBranch();
        branch.addTopic(new Topic(user, "Second topic"));
        session.save(branch);
        int expectedCount = branch.getTopics().size();
        
        int actualCount = dao.getCountTopicsInBranch(branch);
        
        assertEquals(actualCount, expectedCount, "Count of topics in the branch is wrong");
    }
    
    @Test
    public void testGetCountPostsInBranch() {
        //topic with one post
        Topic topic = PersistedObjectFactory.getDefaultTopic();
        Branch branch = topic.getBranch();
        //add two posts
        topic.addPost(new Post(topic.getTopicStarter(), "Second post"));
        topic.addPost(new Post(topic.getTopicStarter(), "Third post"));
        //
        session.save(branch);
        int expectedCount = topic.getPosts().size();
        
        int actualCount = dao.getCountPostsInBranch(branch);
        
        assertEquals(actualCount, expectedCount, "Count of posts in the branch is wrong");
        
    }
}