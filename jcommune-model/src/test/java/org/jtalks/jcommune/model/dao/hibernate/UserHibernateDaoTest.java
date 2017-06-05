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
import org.jtalks.jcommune.model.dto.UserDto;
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
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.*;

import static io.qala.datagen.RandomShortApi.bool;
import static io.qala.datagen.RandomShortApi.integer;
import static io.qala.datagen.RandomShortApi.sample;
import static java.util.Arrays.asList;
import static org.apache.commons.lang.RandomStringUtils.randomAlphanumeric;
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
    private UserDao userDao;
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

        userDao.saveOrUpdate(user);

        assertNotSame(user.getId(), 0, "Id not created");

        session.evict(user);
        JCUser result = (JCUser) session.get(JCUser.class, user.getId());

        assertReflectionEquals(user, result);
    }

    @Test(expectedExceptions = DataIntegrityViolationException.class)
    public void testSaveUserWithUniqueViolation() {
        JCUser user = ObjectsFactory.getDefaultUser();
        JCUser user2 = ObjectsFactory.getDefaultUser();

        userDao.saveOrUpdate(user);
        userDao.saveOrUpdate(user2);
    }

    @Test
    public void testGet() {
        JCUser user = ObjectsFactory.getDefaultUser();
        session.save(user);

        JCUser result = userDao.get(user.getId());

        assertNotNull(result);
        assertEquals(result.getId(), user.getId());
    }

    @Test
    public void testGetInvalidId() {
        JCUser result = userDao.get(-567890L);

        assertNull(result);
    }

    @Test
    public void testUpdate() {
        String newName = "new name";
        JCUser user = ObjectsFactory.getDefaultUser();
        session.save(user);
        user.setFirstName(newName);
        userDao.saveOrUpdate(user);
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
        userDao.saveOrUpdate(user);
        session.flush();
    }

    @Test
    public void testDelete() {
        JCUser user = ObjectsFactory.getDefaultUser();
        session.save(user);

        boolean result = userDao.delete(user.getId());
        int userCount = getCount();

        assertTrue(result, "Entity is not deleted");
        assertEquals(userCount, 0);
    }

    @Test
    public void testDeleteInvalidId() {
        boolean result = userDao.delete(-100500L);

        assertFalse(result, "Entity deleted");
    }

    /*===== UserDao specific methods =====*/

    @Test
    public void testGetByUsernameSameCase() {
        JCUser user = ObjectsFactory.getDefaultUser();
        session.save(user);

        JCUser result = userDao.getByUsername(user.getUsername());

        assertNotNull(result);
        assertReflectionEquals(user, result);
    }

    @Test
    public void testGetByUsernameDifferentCases() {
        JCUser user = ObjectsFactory.getDefaultUser();
        session.save(user);

        JCUser result = userDao.getByUsername(user.getUsername().toUpperCase());

        assertNotNull(result);
        assertReflectionEquals(user, result);
    }

    @Test
    public void testGetByUsernameMultipleUsersWithSameNameWhenIgnoringCase() {
        JCUser user = ObjectsFactory.getUser("usernamE", "username@mail.com");
        session.save(user);
        session.save(ObjectsFactory.getUser("Username", "Username@mail.com"));

        JCUser result = userDao.getByUsername("usernamE");

        assertNotNull(result);
        assertReflectionEquals(user, result);
    }

    @Test
    public void testGetByUsernameNotExist() {
        JCUser user = ObjectsFactory.getDefaultUser();
        session.save(user);

        JCUser result = userDao.getByUsername("Name");

        assertNull(result);
    }

    @Test
    public void testGetByUsernameNotFoundWhenMultipleUsersWithSameNameWhenIgnoringCase() {
        session.save(ObjectsFactory.getUser("usernamE", "username@mail.com"));
        session.save(ObjectsFactory.getUser("Username", "Username@mail.com"));

        JCUser result = userDao.getByUsername("username");

        assertNull(result);
    }

    @Test
    public void getCommonUserByUsernameShouldFindOne() {
        User expected = givenCommonUserWithUsernameStoredInDb("username");

        User actual = userDao.getCommonUserByUsername("username");
        assertNotNull(actual);
        assertReflectionEquals(actual, expected);
    }

    @Test
    public void getCommonUserByUsernameShouldNotFind() {
        givenCommonUserWithUsernameStoredInDb("username");

        User actual = userDao.getCommonUserByUsername("wrong username there is no such user");
        assertNull(actual);
    }

    @Test
    public void getCommonUserByUsernameShouldNotFindInEmptyDb() {
        User actual = userDao.getCommonUserByUsername("username");
        assertNull(actual);
    }

    @Test
    public void testGetByUuid() {
        JCUser user = ObjectsFactory.getDefaultUser();
        String uuid = user.getUuid();
        session.save(user);

        JCUser result = userDao.getByUuid(uuid);

        assertReflectionEquals(user, result);
    }

    @Test
    public void testGetByUuidNotExist() {
        JCUser user = ObjectsFactory.getDefaultUser();
        session.save(user);

        JCUser result = userDao.getByUuid("uuid");

        assertNull(result);
    }

    @Test
    public void testFetchByEMail() {
        JCUser user = ObjectsFactory.getDefaultUser();
        session.save(user);
        assertNotNull(userDao.getByEmail(user.getEmail()));
    }

    @Test
    public void testFetchNonActivatedAccounts() {
        JCUser activated = new JCUser("login", "email@mail.com", "password");
        activated.setEnabled(true);
        JCUser nonActivated = ObjectsFactory.getDefaultUser();
        session.save(activated);
        session.save(nonActivated);

        Collection<JCUser> users = userDao.getNonActivatedUsers();

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

        List<JCUser> foundByUsernames = userDao.getByUsernames(existsUsernames);

        assertTrue(foundByUsernames.size() == existsUsernames.size(), "It should return all users by their names.");
        assertTrue(foundByUsernames.contains(firstExistsUser), firstExistsUser.getUsername() + "should be found by his name.");
    }

    @Test
    public void addingValidUserToValidGroupShouldSucceed() {
        JCUser user = givenJCUserWithUsernameStoredInDb("test-user");
        Group group = PersistedObjectsFactory.group("test-group");
        user.addGroup(group);
        userDao.saveOrUpdate(user);
        flushAndClearSession(session);

        Group selected = groupDao.get(group.getId());
        assertEquals(selected.getUsers().size(), 1);
        assertReflectionEquals(selected.getUsers().get(0), user);
    }

    @Test
    public void getByUsernamesShouldReturnEmptyListWhenFoundUsersDoNotExist() {
        Set<String> existsUsernames = new HashSet<>(asList("Shogun", "jk1", "masyan"));

        List<JCUser> foundByUsernames = userDao.getByUsernames(existsUsernames);

        assertTrue(foundByUsernames.isEmpty(), "It should return empty list, cause found users not exist.");
    }

    @Test
    public void getUsernamesResultCount() {
        String usernamePattern = "Us";
        int resultCount = 2;
        createUser("User1", true);
        createUser("uSer2", true);
        createUser("user3", true);
        assertEquals(userDao.getUsernames(usernamePattern, resultCount).size(), 2);
    }

    @Test
    public void getUsernamesEnabledUsers() {
        String usernamePattern = "Us";
        int resultCount = 5;
        createUser("User1", true);
        createUser("uSer2", true);
        createUser("user3", false);
        assertEquals(userDao.getUsernames(usernamePattern, resultCount).size(), 2);
    }

    @Test
    public void getUsernamesWithSpecialCharacters() {
        String usernamePattern = "@/|\"&' <>#${}()";
        int resultCount = 5;
        createUserWithMail("Some_user1", "user1@mail.com", true);
        createUserWithMail("user2", "user2@mail.com", true);
        createUserWithMail("@/|\"&' <>#${}()", "user3@mail.com", true);

        assertEquals(userDao.getUsernames(usernamePattern, resultCount).size(), 1);
    }

    @Test
    public void specialCharactersShouldBeEscapedCorrectly() {
        String usernamePattern = "_us%";
        int resultCount = 5;
        createUserWithMail("Some_user1", "user1@mail.com", true);
        createUserWithMail("user2", "user2@mail.com", true);
        createUserWithMail("Some_us%2r", "user3@mail.com", true);

        assertEquals(userDao.getUsernames(usernamePattern, resultCount).size(), 1);
    }

    @Test
    public void findByUsernameOrEmailShouldSearchByUsername() {
        JCUser user1 = createUserWithMail("Arthur", "email1@mail.com", true);
        JCUser user2 = createUserWithMail("Barbara", "email2@mail.com", true);
        createUserWithMail("Epolit", "email3@mail.com", true);

        List<JCUser> result = userDao.findByUsernameOrEmail("ar", 20);

        assertEquals(result.size(), 2);
        assertTrue(result.contains(user1));
        assertTrue(result.contains(user2));
    }

    @Test
    public void findByUsernameOrEmailShouldSearchByEmail() {
        JCUser user1 = createUserWithMail("Arthur", "emAIL1@mail.com", true);
        JCUser user2 = createUserWithMail("Barbara", "email2@mail.com", true);
        createUserWithMail("Epolit", "post@google.com", true);

        List<JCUser> result = userDao.findByUsernameOrEmail("email", 20);

        assertEquals(result.size(), 2);
        assertTrue(result.contains(user1));
        assertTrue(result.contains(user2));
    }

    @Test
    public void findByUsernameOrEmailShouldSearchDisabledUsers() {
        JCUser user1 = createUserWithMail("Arthur", "emAIL1@mail.com", true);
        JCUser user2 = createUserWithMail("Barbara", "email2@mail.com", false);

        List<JCUser> result = userDao.findByUsernameOrEmail("email", 20);

        assertEquals(result.size(), 2);
        assertTrue(result.contains(user1));
        assertTrue(result.contains(user2));
    }

    @Test
    public void findByUsernameShouldNotReturnMoreUsersThanSpecified() {
        createUserWithMail("user1", "emai1@mail.com", true);
        createUserWithMail("user2", "email2@mail.com", true);
        createUserWithMail("user3", "email3@mail.com", true);

        List<JCUser> result = userDao.findByUsernameOrEmail("user", 2);

        assertEquals(result.size(), 2);
    }

    @Test
    public void findByUsernameOrEmailShouldCorrectlyEscapeSpecialCharacters() {
        String usernamePattern = "_us%";
        createUserWithMail("Some_user1", "user1@mail.com", true);
        createUserWithMail("user2", "user2@mail.com", true);
        JCUser user = createUserWithMail("Some_us%2r", "user3@mail.com", true);

        List<JCUser> result = userDao.findByUsernameOrEmail(usernamePattern, 20);

        assertEquals(result.size(), 1);
        assertTrue(result.contains(user));
    }

    @Test
    public void testFindByUsernameOrEmailWihSpecialCharacters() {
        String usernamePattern = "@/|\"&' <>#${}()";
        createUserWithMail("Some_user1", "user1@mail.com", true);
        createUserWithMail("user2", "user2@mail.com", true);
        JCUser user = createUserWithMail("@/|\"&' <>#${}()", "user3@mail.com", true);

        List<JCUser> result = userDao.findByUsernameOrEmail(usernamePattern, 20);

        assertEquals(result.size(), 1);
        assertTrue(result.contains(user));
    }


    @Test
    public void findByUsernameOrEmailTestPrimaryOrderUsername() {
        String keyWord = "keyword@email.com";
        JCUser user4 = createUserWithMail("1" + keyWord , "user4@email.com", true);
        JCUser user3 = createUserWithMail("1" + keyWord + "1", "user3@email.com", true);
        JCUser user2 = createUserWithMail(keyWord + "1", "user2@email.com", true);
        JCUser user1 = createUserWithMail(keyWord, "user1@email.com", true);

        List<JCUser> result = userDao.findByUsernameOrEmail(keyWord, 20);

        assertEquals(result.get(0), user1);
        assertEquals(result.get(1), user2);
        assertEquals(result.get(2), user3);
        assertEquals(result.get(3), user4);
    }

    @Test
    public void findByUsernameOrEmailTestPrimaryOrderEmail() {
        String keyWord = "keyword@email.com";
        JCUser user4 = createUserWithMail("user4", "a" + keyWord, true);
        JCUser user3 = createUserWithMail("user3", "a" + keyWord + "a", true);
        JCUser user2 = createUserWithMail("user2", keyWord + "a", true);
        JCUser user1 = createUserWithMail("user1", keyWord, true);

        List<JCUser> result = userDao.findByUsernameOrEmail(keyWord, 20);

        assertEquals(result.get(0), user1);
        assertEquals(result.get(1), user2);
        assertEquals(result.get(2), user3);
        assertEquals(result.get(3), user4);
    }

    @Test
    public void findByUsernameOrEmailTestPrimaryOrderMixed() {
        String keyWord = "keyword@email.com";
        JCUser user4 = createUserWithMail("a" + keyWord, "user4@email.com", true);
        JCUser user3 = createUserWithMail("user3", "a" + keyWord + "a", true);
        JCUser user2 = createUserWithMail(keyWord + "a", "user2@email.com", true);
        JCUser user1 = createUserWithMail("user1", keyWord, true);

        List<JCUser> result = userDao.findByUsernameOrEmail(keyWord, 20);

        assertEquals(result.get(0), user1);
        assertEquals(result.get(1), user2);
        assertEquals(result.get(2), user3);
        assertEquals(result.get(3), user4);
    }

    @Test
    public void testSecondaryOrderExactMatch() {
        String keyWord = "keyword@email.com";
        JCUser user2 = createUserWithMail("user2", keyWord, true);
        JCUser user1 = createUserWithMail(keyWord, "user1@email.com", true);

        List<JCUser> result = userDao.findByUsernameOrEmail(keyWord, 20);

        assertEquals(result.get(0), user1);
        assertEquals(result.get(1), user2);
    }

    @Test
    public void testSecondaryOrderStartFromKeyWord() {
        String keyWord = "keyword";
        JCUser user2 = createUserWithMail("user2", keyWord + "@email.com", true);
        JCUser user1 = createUserWithMail(keyWord + "1", "user1@email.com", true);
        JCUser user3 = createUserWithMail(keyWord + "11", keyWord + "1@email.com", true);


        List<JCUser> result = userDao.findByUsernameOrEmail(keyWord, 20);

        assertEquals(result.get(0), user3);
        assertEquals(result.get(1), user1);
        assertEquals(result.get(2), user2);
    }

    @Test
    public void testThirdaryOrderUsernameStartsFromKeyWord() {
        String keyWord = "keyword";
        JCUser user2 = createUserWithMail(keyWord + "z", "user2@email.com", true);
        JCUser user1 = createUserWithMail(keyWord + "a", "user1@email.com", true);

        List<JCUser> result = userDao.findByUsernameOrEmail(keyWord, 20);

        assertEquals(result.get(0), user1);
        assertEquals(result.get(1), user2);

    }

    @Test
    public void testThirdaryOrderEmailStartsFromKeyWord() {
        String keyWord = "keyword";
        JCUser user3 = createUserWithMail("bbbb", keyWord + "3@email.com", true);
        JCUser user2 = createUserWithMail("zzzz", keyWord + "1@email.com", true);
        JCUser user1 = createUserWithMail("aaaa", keyWord + "2@email.com", true);

        List<JCUser> result = userDao.findByUsernameOrEmail(keyWord, 20);

        assertEquals(result.get(0), user1);
        assertEquals(result.get(1), user3);
        assertEquals(result.get(2), user2);

    }

    @Test
    public void testSecondaryOrderKeywordInTheMiddle() {
        String keyWord = "keyword";
        JCUser user3 = createUserWithMail("1" + keyWord + "1", "user1@email.com", true);
        JCUser user2 = createUserWithMail("user2", "a" + keyWord + "@email.com", true);
        JCUser user1 = createUserWithMail("1" + keyWord + "11", "a" + keyWord + "1@email.com", true);

        List<JCUser> result = userDao.findByUsernameOrEmail(keyWord, 20);

        assertEquals(result.get(0), user1);
        assertEquals(result.get(1), user3);
        assertEquals(result.get(2), user2);

    }

    @Test
    public void testThirdaryOrderUsernameWithKeywordItTheMiddle() {
        String keyWord = "keyword";
        JCUser user2 = createUserWithMail("z" + keyWord + "aa", "user2@email.com", true);
        JCUser user1 = createUserWithMail("a" + keyWord + "aa", "user1@email.com", true);

        List<JCUser> result = userDao.findByUsernameOrEmail(keyWord, 20);

        assertEquals(result.get(0), user1);
        assertEquals(result.get(1), user2);
    }

    @Test
    public void testThirdaryOrderEmailWithKeyWordInTheMiddle() {
        String keyWord = "keyword";
        JCUser user2 = createUserWithMail("zuser", "11" + keyWord + "@email.com", true);
        JCUser user1 = createUserWithMail("auser", "1" + keyWord + "@email.com", true);

        List<JCUser> result = userDao.findByUsernameOrEmail(keyWord, 20);

        assertEquals(result.get(0), user1);
        assertEquals(result.get(1), user2);
    }

    @Test
    public void testSecondaryOrderKeywordAtTheEnd() {
        String keyWord = "keyword";
        JCUser user3 = createUserWithMail("user3", "user3@email." + keyWord, true);
        JCUser user2 = createUserWithMail("user2" + keyWord, "user2@email.com", true);
        JCUser user1 = createUserWithMail("1" + keyWord, "user1@email." + keyWord, true);

        List<JCUser> result = userDao.findByUsernameOrEmail(keyWord, 20);

        assertEquals(result.get(0), user1);
        assertEquals(result.get(1), user2);
        assertEquals(result.get(2), user3);

    }

    @Test
    public void testThirdaryOrderKeyWordAtTheEndOfUsername() {
        String keyWord = "keyword";
        JCUser user2 = createUserWithMail("z" + keyWord, "user2@email.com", true);
        JCUser user1 = createUserWithMail("a" + keyWord, "user1@email.com", true);

        List<JCUser> result = userDao.findByUsernameOrEmail(keyWord, 20);

        assertEquals(result.get(0), user1);
        assertEquals(result.get(1), user2);

    }

    @Test
    public void testThirdaryOrderEmailWithKeywordInTheEnd() {
        String keyWord = "keyword";
        JCUser user2 = createUserWithMail("zuser", "user2@email." + keyWord, true);
        JCUser user1 = createUserWithMail("auser", "user1@email." + keyWord, true);

        List<JCUser> result = userDao.findByUsernameOrEmail(keyWord, 20);

        assertEquals(result.get(0), user1);
        assertEquals(result.get(1), user2);
    }

    @Test
    public void findByUsernameNotInGroupId() {
        String keyWord = randomAlphanumeric(7);;
        Group group1 = createGroup("testGroup1");
        Group group2 = createGroup("testGroup2");

        JCUser jcUser1group1 = createUserWithGroup(keyWord + "1gr1", "mail1gr1@mail.ru", group1);
        addRandomGroupsToUser(jcUser1group1);
        createUserWithGroup("petrgr1", "petr1gr1@mail.ru", group1);
        JCUser jcUser1group2 = createUserWithGroup(keyWord + "1gr2", "mail2gr2@mail.ru", group2);
        JCUser jcUser2group2 = createUserWithGroup("petrgr2", "petr1gr2@mail.ru", group2);
        addRandomGroupsToUser(jcUser2group2);

        List<UserDto> result;
        result = userDao.findByUsernameOrEmailNotInGroup(keyWord, group1.getId(), 20);
        Assert.assertEquals(result.size(), 1);
        Assert.assertEquals(jcUser1group2.getUsername(), result.get(0).getUsername());

        result = userDao.findByUsernameOrEmailNotInGroup(keyWord, group2.getId(), 20);
        Assert.assertEquals(result.size(), 1);
        Assert.assertEquals(jcUser1group1.getUsername(), result.get(0).getUsername());
    }

    @Test
    public void findByEmailNotInGroupId() {
        String keyWord = randomAlphanumeric(7);;
        Group group1 = createGroup("testGroup1");
        Group group2 = createGroup("testGroup2");

        JCUser jcUser1group1 = createUserWithGroup("user1gr1", keyWord + "mail1gr1@mail.ru", group1);
        addRandomGroupsToUser(jcUser1group1);
        createUserWithGroup("petrgr1", "petr1gr1@mail.ru", group1);
        JCUser jcUser1group2 = createUserWithGroup("user1gr2", keyWord + "mail2gr2@mail.ru", group2);
        JCUser jcUser1group3 = createUserWithGroup("petrgr2", "petr1gr2@mail.ru", group2);
        addRandomGroupsToUser(jcUser1group3);

        List<UserDto> result;
        result = userDao.findByUsernameOrEmailNotInGroup(keyWord, group1.getId(), 20);
        Assert.assertEquals(result.size(), 1);
        Assert.assertEquals(jcUser1group2.getUsername(), result.get(0).getUsername());

        result = userDao.findByUsernameOrEmailNotInGroup(keyWord, group2.getId(), 20);
        Assert.assertEquals(result.size(), 1);
        Assert.assertEquals(jcUser1group1.getUsername(), result.get(0).getUsername());
    }

    @Test(invocationCount = 5)
    public void findsUser_ifHeIsNotInAnyGroup() {
        final String keyword = randomAlphanumeric(7);
        Group group1 = createGroup("testGroup1");
        JCUser jcUser = createUserWithMail(
                sample("username", keyword.toUpperCase()),
                sample("m@m.ru", keyword.toUpperCase() + "@mm.ru"), true);
        List<UserDto> result = userDao.findByUsernameOrEmailNotInGroup(keyword, group1.getId(), 20);
        Assert.assertTrue(result.size() <= 1);
        if (result.size() == 1) {
            Assert.assertEquals(jcUser.getUsername(), result.get(0).getUsername());
        } else {
            Assert.assertFalse(jcUser.getUsername().contains(keyword));
            Assert.assertFalse(jcUser.getEmail().contains(keyword));
        }
    }

    @Test(invocationCount = 5)
    public void doesNotFindUser_ifHeIsInGroup() {
        final String keyword = randomAlphanumeric(7);
        final String word = randomAlphanumeric(7);
        JCUser user = createUserWithGroup(
                sample(keyword, word),
                sample(keyword + "@mail.ru", word + "@gmail.com"), createGroup("testGroup0"));
        List<Group> grpList = addRandomGroupsToUser(user);
        for (Group group : grpList) {
            List<UserDto> result = userDao.findByUsernameOrEmailNotInGroup(keyword, group.getId(), 20);
            Assert.assertEquals(result.size(), 0);
        }
    }

    @Test(invocationCount = 5)
    public void testFindUserNotInGroupIdLimit() {
        final int limit = 10 + integer(20);
        String keyWord = randomAlphanumeric(7);
        Group group1 = createGroup("testGroup1");
        for (int i=0; i<limit + integer(50); i++) {
            JCUser user = createUserWithGroup(keyWord + String.format("%02d", i), keyWord + i + "@mail.ru", group1);
            if (bool()) {
                addRandomGroupsToUser(user);
            }
        }
        Group emptyGroup = createGroup("emptyGroup");
        List<UserDto> result = userDao.findByUsernameOrEmailNotInGroup(keyWord, emptyGroup.getId(), limit);
        Assert.assertEquals(result.size(), limit);
        Assert.assertEquals(result.get(limit - 1).getUsername(), keyWord + String.format("%02d", limit - 1));
    }

    @Test(invocationCount = 5)
    public void testFindUserNotInGroupByEmptyPattern() {
        final int count = 15 + integer(50);
        String keyWord = randomAlphanumeric(7);
        Group group1 = createGroup("testGroup1");
        long group2id = group1.getId() + 1;
        for (int i=0; i<count; i++) {
            createUserWithGroup(keyWord + String.format("%02d", i), keyWord + i + "@mail.ru", group1);
        }
        List<UserDto> result = userDao.findByUsernameOrEmailNotInGroup("", group2id, count);
        Assert.assertEquals(result.size(), count);
        Assert.assertEquals(result.get(count - 1).getUsername(), keyWord + String.format("%02d", count - 1));
    }

    @Test(dataProvider = "usersForTestResultOrderDataProvider")
    public void testFindUserNotInGroupResultOrder(List<String> shuffledList, List<String> userPatternList) {
        String pattern = "keyword";
        Group group1 = createGroup("testGroup1");
        for (String str : shuffledList) {
            createUserWithGroup(str, str + "@mail.ru", group1);
        }
        List<UserDto> result = userDao.findByUsernameOrEmailNotInGroup(pattern, group1.getId() + 1, 20);
        Assert.assertEquals(result.size(), userPatternList.size());
        for (int i=0; i<result.size(); i++) {
            Assert.assertEquals(result.get(i).getUsername(), userPatternList.get(i));
        }
    }

    @DataProvider
    public Object[][] usersForTestResultOrderDataProvider() {
        String pattern = "keyword";
        List<String> userPatternList = new ArrayList<>();
        userPatternList.add(pattern);
        userPatternList.add(pattern + "wqrrw");
        userPatternList.add("a_rtet" + pattern + "gghghgh");
        userPatternList.add("b_rtet" + pattern + "gghghgh");
        userPatternList.add("a_rtert" + pattern);
        userPatternList.add("b_rtert" + pattern);

        List<String> shuffledList = new ArrayList<>(userPatternList);
        shuffledList.add("someNotMatchingPattern1");
        shuffledList.add("someNotMatchingPattern2");
        shuffledList.add("someNotMatchingPattern3");
        Collections.shuffle(shuffledList, new Random());

        return new Object[][] {
                {shuffledList, userPatternList}
        };
    }

    private JCUser createUserWithGroup(String userName, String email, Group group) {
        JCUser jcUser = createUserWithMail(userName, email, true);
        jcUser.addGroup(group);
        userDao.saveOrUpdate(jcUser);
        return jcUser;
    }

    private List<Group> addRandomGroupsToUser(JCUser user) {
        List<Group> result = new ArrayList<>();
        Random rnd = new Random();
        int count = 3 + rnd.nextInt(10);
        for (int i=0; i<count; i++) {
            Group group = createGroup(randomAlphanumeric(12));
            result.add(group);
            user.addGroup(group);
        }
        userDao.saveOrUpdate(user);
        return result;
    }

    private Group createGroup(String groupName) {
        Group group = new Group(groupName);
        groupDao.saveOrUpdate(group);
        return group;
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
