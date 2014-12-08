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
import org.jtalks.common.model.dao.GroupDao;
import org.jtalks.common.model.entity.Group;
import org.jtalks.common.model.entity.User;
import org.jtalks.jcommune.model.entity.PersistedObjectsFactory;
import org.jtalks.jcommune.model.dao.UserDao;
import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.model.entity.ObjectsFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.Arrays.asList;
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
    private GroupDao groupDao;
    @Autowired
    private SessionFactory sessionFactory;
    private Session session;

    @BeforeMethod
    public void setUp() throws Exception {
        session = sessionFactory.getCurrentSession();
        PersistedObjectsFactory.setSession(session);
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
        session.flush();
        session.evict(user);
        JCUser result = (JCUser) session.get(JCUser.class, user.getId());//!
        assertEquals(result.getFirstName(), newName);
    }

    @Test(expectedExceptions = org.hibernate.exception.ConstraintViolationException.class)
    public void testUpdateNotNullViolation() {
        JCUser user = ObjectsFactory.getDefaultUser();
        session.save(user);
        user.setEmail(null);
        dao.saveOrUpdate(user);
        session.flush();
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
    public void testGetByUsernameSameCase() {
        JCUser user = ObjectsFactory.getDefaultUser();
        session.save(user);

        JCUser result = dao.getByUsername(user.getUsername());

        assertNotNull(result);
        assertReflectionEquals(user, result);
    }

    @Test
    public void testGetByUsernameDifferentCases() {
        JCUser user = ObjectsFactory.getDefaultUser();
        session.save(user);

        JCUser result = dao.getByUsername(user.getUsername().toUpperCase());

        assertNotNull(result);
        assertReflectionEquals(user, result);
    }

    @Test
    public void testGetByUsernameMultipleUsersWithSameNameWhenIgnoringCase() {
        JCUser user = ObjectsFactory.getUser("usernamE", "username@mail.com");
        session.save(user);
        session.save(ObjectsFactory.getUser("Username", "Username@mail.com"));

        JCUser result = dao.getByUsername("usernamE");

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
    public void testGetByUsernameNotFoundWhenMultipleUsersWithSameNameWhenIgnoringCase() {
        session.save(ObjectsFactory.getUser("usernamE", "username@mail.com"));
        session.save(ObjectsFactory.getUser("Username", "Username@mail.com"));

        JCUser result = dao.getByUsername("username");

        assertNull(result);
    }

    @Test
    public void getCommonUserByUsernameShouldFindOne() {
        User expected = givenCommonUserWithUsernameStoredInDb("username");

        User actual = dao.getCommonUserByUsername("username");
        assertNotNull(actual);
        assertReflectionEquals(actual, expected);
    }

    @Test
    public void getCommonUserByUsernameShouldNotFind() {
        givenCommonUserWithUsernameStoredInDb("username");

        User actual = dao.getCommonUserByUsername("wrong username there is no such user");
        assertNull(actual);
    }

    @Test
    public void getCommonUserByUsernameShouldNotFindInEmptyDb() {
        User actual = dao.getCommonUserByUsername("username");
        assertNull(actual);
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
        JCUser activated = new JCUser("login", "email@mail.com", "password");
        activated.setEnabled(true);
        JCUser nonActivated = ObjectsFactory.getDefaultUser();
        session.save(activated);
        session.save(nonActivated);

        Collection<JCUser> users = dao.getNonActivatedUsers();

        assertTrue(users.contains(nonActivated));
        assertEquals(users.size(), 1);
    }

    /**
     * Creates a user with the specified username, stores it into database and clears the session so that we won't get
     * the same object from the session, but rather a new one will be returned from database.
     *
     * @param username a username to store the user with, other properties will be pretty random
     * @return a user that was stored in the database and removed from the Hibernate session
     */
    private User givenCommonUserWithUsernameStoredInDb(String username) {
        User expected = new User(username, "mail@mail.com", "pass", null);//salt will be null anyway after retrieval
        session.save(expected);
        session.clear();
        return expected;
    }


    private int getCount() {
        return ((Number) session
                .createQuery("select count(*) from org.jtalks.jcommune.model.entity.JCUser")
                .uniqueResult())
                .intValue();
    }

    @Test
    public void getByUsernamesShouldReturnExistsInRepoUsers() {
        String firstExistsUsername = "Shogun";
        JCUser firstExistsUser = givenJCUserWithUsernameStoredInDb(firstExistsUsername);
        Set<String> existsUsernames = new HashSet<>(asList(firstExistsUsername));

        List<JCUser> foundByUsernames = dao.getByUsernames(existsUsernames);

        assertTrue(foundByUsernames.size() == existsUsernames.size(), "It should return all users by their names.");
        assertTrue(foundByUsernames.contains(firstExistsUser), firstExistsUser.getUsername() + "should be found by his name.");
    }

    @Test
    public void addingValidUserToValidGroupShouldSucceed() {
        JCUser user = givenJCUserWithUsernameStoredInDb("test-user");
        Group group = PersistedObjectsFactory.group("test-group");
        user.addGroup(group);
        dao.saveOrUpdate(user);
        flushAndClearSession(session);

        Group selected = groupDao.get(group.getId());
        assertEquals(selected.getUsers().size(), 1);
        assertReflectionEquals(selected.getUsers().get(0), user);
    }

    @Test
    public void getByUsernamesShouldReturnEmptyListWhenFoundUsersDoNotExist() {
        Set<String> existsUsernames = new HashSet<>(asList("Shogun", "jk1", "masyan"));

        List<JCUser> foundByUsernames = dao.getByUsernames(existsUsernames);

        assertTrue(foundByUsernames.isEmpty(), "It should return empty list, cause found users not exist.");
    }

    @Test
    public void getUsernamesResultCount() {
        String usernamePattern = "Us";
        int resultCount = 2;
        createUser("User1", true);
        createUser("uSer2", true);
        createUser("user3", true);
        assertEquals(dao.getUsernames(usernamePattern, resultCount).size(), 2);
    }

    @Test
    public void getUsernamesEnabledUsers() {
        String usernamePattern = "Us";
        int resultCount = 5;
        createUser("User1", true);
        createUser("uSer2", true);
        createUser("user3", false);
        assertEquals(dao.getUsernames(usernamePattern, resultCount).size(), 2);
    }

    @Test
    public void getUsernamesWithSpecialCharacters() {
        String usernamePattern = "@/|\"&' <>#${}()";
        int resultCount = 5;
        createUserWithMail("Some_user1", "user1@mail.com", true);
        createUserWithMail("user2", "user2@mail.com", true);
        createUserWithMail("@/|\"&' <>#${}()", "user3@mail.com", true);

        assertEquals(dao.getUsernames(usernamePattern, resultCount).size(), 1);
    }

    @Test
    public void specialCharactersShouldBeEscapedCorrectly() {
        String usernamePattern = "_us%";
        int resultCount = 5;
        createUserWithMail("Some_user1", "user1@mail.com", true);
        createUserWithMail("user2", "user2@mail.com", true);
        createUserWithMail("Some_us%2r", "user3@mail.com", true);

        assertEquals(dao.getUsernames(usernamePattern, resultCount).size(), 1);
    }

    private JCUser givenJCUserWithUsernameStoredInDb(String username) {
        JCUser expected = new JCUser(username, username + "@mail.com", username + "pass");
        session.save(expected);
        session.clear();
        return expected;
    }

    private JCUser createUserWithMail(String username, String email, boolean enabled) {
        JCUser user = new JCUser(username, email, username + "pass");
        user.setEnabled(enabled);
        session.persist(user);
        return user;
    }

    private JCUser createUser(String username, boolean enabled) {
        return createUserWithMail(username, username + "@mail.com", enabled);
    }

    private void flushAndClearSession(Session session) {
        session.flush();
        session.clear();
    }
}
