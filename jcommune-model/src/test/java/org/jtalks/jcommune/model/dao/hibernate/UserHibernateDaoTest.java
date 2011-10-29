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
import org.jtalks.jcommune.model.dao.PostDao;
import org.jtalks.jcommune.model.dao.UserDao;
import org.jtalks.jcommune.model.entity.Branch;
import org.jtalks.jcommune.model.entity.Post;
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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNotSame;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;
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
    private PostDao daoP;
    @Autowired
    private SessionFactory sessionFactory;
    private Session session;
    private User user;

    @BeforeMethod
    public void setUp() throws Exception {
        session = sessionFactory.getCurrentSession();
        ObjectsFactory.setSession(session);
        user = ObjectsFactory.getDefaultUser();
    }

    /*===== Common methods =====*/

    @Test
    public void testSave() {
        User user = ObjectsFactory.getDefaultUser();

        dao.saveOrUpdate(user);

        assertNotSame(user.getId(), 0, "Id not created");

        session.evict(user);
        User result = (User) session.get(User.class, user.getId());

        assertReflectionEquals(user, result);
    }

    @Test(expectedExceptions = DataIntegrityViolationException.class)
    public void testSaveUserWithUniqueViolation() {
        User user = ObjectsFactory.getDefaultUser();
        User user2 = ObjectsFactory.getDefaultUser();

        dao.saveOrUpdate(user);
        dao.saveOrUpdate(user2);
    }

    @Test
    public void testGet() {
        User user = ObjectsFactory.getDefaultUser();
        session.save(user);

        User result = dao.get(user.getId());

        assertNotNull(result);
        assertEquals(result.getId(), user.getId());
    }

    @Test
    public void testGetInvalidId() {
        User result = dao.get(-567890L);

        assertNull(result);
    }

    @Test
    public void testUpdate() {
        String newName = "new name";
        User user = ObjectsFactory.getDefaultUser();
        session.save(user);
        user.setFirstName(newName);

        dao.saveOrUpdate(user);
        session.evict(user);
        User result = (User) session.get(User.class, user.getId());//!

        assertEquals(result.getFirstName(), newName);
    }

    @Test(expectedExceptions = Exception.class)
    public void testUpdateNotNullViolation() {
        User user = ObjectsFactory.getDefaultUser();
        session.save(user);
        user.setEmail(null);

        dao.saveOrUpdate(user);
    }

    @Test
    public void testDelete() {
        User user = ObjectsFactory.getDefaultUser();
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
        User user = ObjectsFactory.getDefaultUser();
        session.save(user);

        User result = dao.getByUsername(user.getUsername());

        assertNotNull(result);
        assertReflectionEquals(user, result);
    }

    @Test
    public void testGetByEncodedUsername() {
        User user = ObjectsFactory.getDefaultUser();
        session.save(user);

        User result = dao.getByEncodedUsername(user.getEncodedUsername());

        assertNotNull(result);
        assertReflectionEquals(user, result);
    }

    @Test
    public void testGetByUsernameNotExist() {
        User user = ObjectsFactory.getDefaultUser();
        session.save(user);

        User result = dao.getByUsername("Name");

        assertNull(result);
    }

    @Test
    public void testIsUserWithEmailExist() {
        User user = ObjectsFactory.getDefaultUser();
        session.save(user);

        boolean result = dao.isUserWithEmailExist(user.getEmail());

        assertTrue(result, "User not exist");
    }

    @Test
    public void testIsUserWithEmailNotExist() {
        User user = ObjectsFactory.getDefaultUser();
        session.save(user);

        boolean result = dao.isUserWithEmailExist("dick@head.com");

        assertFalse(result, "User exist");
    }

    @Test
    public void testIsUserWithUsernameExist() {
        User user = ObjectsFactory.getDefaultUser();
        session.save(user);

        boolean result = dao.isUserWithUsernameExist(user.getUsername());

        assertTrue(result, "User not exist");
    }

    @Test
    public void testIsUserWithUsernameNotExist() {
        User user = ObjectsFactory.getDefaultUser();
        session.save(user);

        boolean result = dao.isUserWithUsernameExist("qwertyuio");

        assertFalse(result, "User exist");
    }

    @Test
    public void testFetchByEMail() {
        User user = ObjectsFactory.getDefaultUser();
        session.save(user);
        assertNotNull(dao.getByEmail(user.getEmail()));
    }

    @Test
    public void testDaoUserPostCountCreateNewPost() {
        Post post = new Post(user, "");
        dao.saveOrUpdate(user);
        session.save(post);

        int result = dao.getCountPostOfUser(user);
        assertEquals(result, 1);
    }

    @Test
    public void testDaoUserPostCountDeleteBranch() {
        Post post1 = new Post(user, "");
        Topic topic = new Topic(user, "");
        Branch branch = ObjectsFactory.getDefaultBranch();

        topic.addPost(post1);
        branch.addTopic(topic);

        dao.saveOrUpdate(user);
        session.save(branch);

        int result = dao.getCountPostOfUser(user);
        assertEquals(result, 1);

        session.delete(branch);

        result = dao.getCountPostOfUser(user);
        assertEquals(result, 0);
    }

    @Test
    public void testDaoUserPostCountDeleteTopic() {
        Post post1 = new Post(user, "");
        Topic topic = new Topic(user, "");
        Branch branch = ObjectsFactory.getDefaultBranch();

        topic.addPost(post1);
        branch.addTopic(topic);

        dao.saveOrUpdate(user);
        session.save(branch);

        int result = dao.getCountPostOfUser(user);
        assertEquals(result, 1);

        branch.deleteTopic(topic);

        result = dao.getCountPostOfUser(user);
        assertEquals(result, 0);
    }

    @Test
    public void testDaoUserPostCountRemovePost() {
        Post post = new Post(user, "");

        dao.saveOrUpdate(user);
        session.save(post);

        int result = dao.getCountPostOfUser(user);
        assertEquals(result, 1);

        session.delete(post);

        result = dao.getCountPostOfUser(user);
        assertEquals(result, 0);
    }


    private int getCount() {
        return ((Number) session.createQuery("select count(*) from User").uniqueResult()).intValue();
    }
}
