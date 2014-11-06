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
import org.jtalks.jcommune.model.entity.PersistedObjectsFactory;
import org.jtalks.jcommune.model.dao.BranchDao;
import org.jtalks.jcommune.model.dao.LastReadPostDao;
import org.jtalks.jcommune.model.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
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
 * @author Kirill Afonin
 * @author masyan
 */
@ContextConfiguration(locations = {"classpath:/org/jtalks/jcommune/model/entity/applicationContext-dao.xml"})
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
@Transactional
public class BranchHibernateDaoTest extends AbstractTransactionalTestNGSpringContextTests {

    @Autowired
    private SessionFactory sessionFactory;
    @Autowired
    private BranchDao dao;
    @Autowired
    private LastReadPostDao lastReadPostDao;
    private Session session;
    Branch branch;

    @BeforeMethod
    public void setUp() throws Exception {
        session = sessionFactory.getCurrentSession();
        PersistedObjectsFactory.setSession(session);
        branch = ObjectsFactory.getDefaultBranch();
    }

    /*===== Common methods =====*/

    @Test
    public void testSave() {
        Branch branch = ObjectsFactory.getDefaultBranch();
        dao.saveOrUpdate(branch);

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

        dao.saveOrUpdate(branch);
        session.flush();
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

        dao.saveOrUpdate(branch);
        session.flush();
        session.evict(branch);
        Branch result = (Branch) session.get(Branch.class, branch.getId());

        assertEquals(result.getName(), newName);
    }

    @Test(expectedExceptions = javax.validation.ConstraintViolationException.class)
    public void testUpdateNotNullViolation() {
        Branch branch = ObjectsFactory.getDefaultBranch();
        session.save(branch);
        branch.setName(null);

        dao.saveOrUpdate(branch);
        session.flush();
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
        dao.saveOrUpdate(branch);
        session.flush();

        assertEquals(getCount("select count(*) from org.jtalks.jcommune.model.entity.Branch"), 1);
        assertEquals(getCount("select count(*) from Topic"), 0);
        assertEquals(getCount("select count(*) from Post"), 0);
    }

    private int getCount(String hql) {
        return ((Number) session.createQuery(hql).uniqueResult()).intValue();
    }

    private List<Branch> createAndSaveBranchList(int size, int sectionPosition) {
        List<Branch> branches = new ArrayList<>();
        Section section = ObjectsFactory.getDefaultSection();
        section.setPosition(sectionPosition);
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
    public void shouldReturnNoBranchesWhenDbIsEmpty() {
        Section emptySection = ObjectsFactory.getDefaultSection();
        session.save(emptySection);
        List<Branch> selectedBranches = dao.getAllBranches();
        assertTrue(selectedBranches.isEmpty());
    }

    @Test
    public void testGetAllBranches() {
        int sectionSize = 5;
        List<Branch> branchesOfFirstSection = createAndSaveBranchList(sectionSize, 1);
        List<Branch> branchesOfSecondSection = createAndSaveBranchList(sectionSize, 0);

        // build desired order
        List<Branch> createdBranches = new ArrayList<>(branchesOfSecondSection);
        createdBranches.addAll(branchesOfFirstSection);

        List<Branch> selectedBranches = dao.getAllBranches();

        assertEquals(createdBranches, selectedBranches);//checking the order
    }

    @Test
    public void testGetCountPostsInBranch() {
        //topic with one post
        Topic topic = PersistedObjectsFactory.getDefaultTopic();
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

    @Test
    @Rollback(false)
    /**
     * User have unread posts in branch
     */
    public void testUnreadPostsInBranchWithExist() {
        Topic topic = PersistedObjectsFactory.getDefaultTopic();
        Branch branch = topic.getBranch();
        JCUser user = topic.getTopicStarter();
        //add post
        topic.addPost(new Post(topic.getTopicStarter(), "New post"));

        session.save(branch);

        PersistedObjectsFactory.createViewUnreadPostsInBranch();
        boolean expectedState = true;
        boolean actualState = dao.isUnreadPostsInBranch(branch, user);

        //manual rollback
        session.delete(branch);
        session.delete(user);
        PersistedObjectsFactory.deleteViewUnreadPostsInBranch();
        session.flush();

        assertEquals(actualState, expectedState, "State of unread posts in the branch is wrong");
    }

    @Test
    @Rollback(false)
    /**
     * User have't unread posts in branch
     */
    public void testUnreadPostsInBranchWithoutExist() {
        Topic topic = PersistedObjectsFactory.getDefaultTopic();
        Branch branch = topic.getBranch();
        JCUser user = topic.getTopicStarter();
        //add post
        topic.addPost(new Post(topic.getTopicStarter(), "New post"));

        session.save(branch);

        PersistedObjectsFactory.createViewUnreadPostsInBranch();
        lastReadPostDao.markAllRead(topic.getTopicStarter(), branch);
        boolean expectedState = false;
        boolean actualState = dao.isUnreadPostsInBranch(branch, user);

        //manual rollback
        session.delete(branch);
        session.delete(user);
        PersistedObjectsFactory.deleteViewUnreadPostsInBranch();
        session.delete(lastReadPostDao.getLastReadPost(user, topic));
        session.flush();

        assertEquals(actualState, expectedState, "State of unread posts in the branch is wrong");
    }

    @Test
    public void testGetSubscribersWithAllowedPermission() {
        JCUser subscriber = PersistedObjectsFactory.getDefaultUserWithGroups();
        branch.getSubscribers().add(subscriber);
        session.save(branch);
        PersistedObjectsFactory.createAndSaveViewTopicsBranchesEntity(
                branch.getId(), String.valueOf(subscriber.getGroups().get(0).getId()), true);
        assertEquals(dao.getAllowedSubscribers(branch).size(), 1,
                "Should return subscribers which are contained in some group with VIEW_TOPIC permission.");
    }

    @Test
    public void testGetSubscribersWithDisallowedPermission() {
        JCUser subscriber = PersistedObjectsFactory.getDefaultUserWithGroups();
        branch.getSubscribers().add(subscriber);
        session.save(branch);
        PersistedObjectsFactory.createAndSaveViewTopicsBranchesEntity(
                branch.getId(), String.valueOf(subscriber.getGroups().get(0).getId()), false);

        assertEquals(dao.getAllowedSubscribers(branch).size(), 0,
                "Should not return subscribers which are contained in any group with disallowed VIEW_TOPIC permission.");
    }

    @Test
    public void testGetSubscribersWithAllowedAndDisallowedPermission() {
        JCUser subscriber = PersistedObjectsFactory.getDefaultUserWithGroups();
        branch.getSubscribers().add(subscriber);
        session.save(branch);
        PersistedObjectsFactory.createAndSaveViewTopicsBranchesEntity(
                branch.getId(), String.valueOf(subscriber.getGroups().get(0).getId()), false);
        PersistedObjectsFactory.createAndSaveViewTopicsBranchesEntity(
                branch.getId(), String.valueOf(subscriber.getGroups().get(1).getId()), true);

        assertEquals(dao.getAllowedSubscribers(branch).size(), 0,
                "Should not return subscribers which are contained in any group with disallowed VIEW_TOPIC permission.");
    }

    @Test
    public void testGetSubscribersWithoutAllowedAndDisallowedPermission() {
        JCUser subscriber = PersistedObjectsFactory.getDefaultUserWithGroups();
        branch.getSubscribers().add(subscriber);
        session.save(branch);

        assertEquals(dao.getAllowedSubscribers(branch).size(), 0,
                "Should not return subscribers which are not contained in any group with allowed VIEW_TOPIC permission.");
    }
}