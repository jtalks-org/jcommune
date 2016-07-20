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
import org.jtalks.jcommune.model.dao.GroupDao;
import org.jtalks.jcommune.model.dto.GroupAdministrationDto;
import org.jtalks.jcommune.model.entity.Branch;
import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.model.entity.ObjectsFactory;
import org.jtalks.jcommune.model.entity.PersistedObjectsFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.annotation.Nonnull;
import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.SQLException;
import java.text.Collator;
import java.text.ParseException;
import java.text.RuleBasedCollator;
import java.util.*;

import static io.qala.datagen.RandomShortApi.alphanumeric;
import static io.qala.datagen.RandomValue.between;
import static org.testng.Assert.*;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;

/**
 * @author Leonid Kazancev
 */
@ContextConfiguration(locations = {"classpath:/org/jtalks/jcommune/model/entity/applicationContext-dao.xml"})
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
@Transactional
public class GroupHibernateDaoTest extends AbstractTransactionalTestNGSpringContextTests {
    static final String NO_FILTER = "";
    private static final String SORTING_TEST_RULES =
            "< A< a< B< b< C< c< D< d< E< e< F< f< G< g< " +
                    "H< h< I< i< J< j< K< k< L< l< M< m< " +
                    "N< n< O< o< P< p< Q< q< R< r< S< s< " +
                    "T< t< U< u< V< v< W< w< X< x< Y< y< Z< z< " +
                    "А< а< Б< б< В< в< Г< г< Д< д< Е< е< " +
                    "Ё< ё< Ж< ж< З< з< И< и< Й< й< К< к< " +
                    "Л< л< М< м< Н< н< О< о< П< п< Р< р< " +
                    "С< с< Т< т< У< у< Ф< ф< Х< х< Ц< ц< " +
                    "Ч< ч< Ш< ш< Щ< щ< Ь< ь< Ы< ы< Ъ< ъ< " +
                    "Э< э< Ю< ю< Я< я";
    private static final String DICTIONARY_RU = "АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЬЫЪЭЮЯ" +
                                                "абвгдеёжзийклмнопрстуфхцчшщьыъэюя" +
                                                "0123456789";
    private static final int MIN_GROUP_NAME_LENGTH = 1;
    private static final int MAX_GROUP_NAME_LENGTH = Group.GROUP_NAME_MAX_LENGTH;
    @Autowired
    private GroupDao groupDao;

    @Autowired
    private SessionFactory sessionFactory;

    private Session session;

    @Resource(lookup = "org/jtalks/jcommune/model/datasource.properties")
    private DataSource dataSource;

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
        sortById(actual);
        assertEquals(actual.size(), 2);
        assertReflectionEquals(actual.get(0), group0);
        assertReflectionEquals(actual.get(1), group1);
    }

    private void sortById(List<Group> groups) {
        Collections.sort(groups, new Comparator<Group>() {
            @Override
            public int compare(@Nonnull Group group, @Nonnull Group group1) {
                return Long.compare(group.getId(), group1.getId());
            }
        });
    }

    @Test
    public void testGetByNameContains() {
        Group group = ObjectsFactory.getRandomGroup();

        saveAndEvict(group);

        List<Group> actual = groupDao.getMatchedByName(group.getName());
        assertEquals(actual.size(), 1);
        assertReflectionEquals(actual.get(0), group);
    }

    @Test
    public void testGetByNameContainsWithEmptyName() {
        Group group = ObjectsFactory.getRandomGroup();
        saveAndEvict(group);

        group = ObjectsFactory.getRandomGroup();
        saveAndEvict(group);

        List<Group> actual = groupDao.getMatchedByName(NO_FILTER);
        List<Group> all = groupDao.getAll();
        assertEquals(actual, all);
    }

    @Test
    public void testGetByName() {
        Group group = ObjectsFactory.getRandomGroup();

        saveAndEvict(group);

        Group actual = groupDao.getGroupByName(group.getName());
        assertReflectionEquals(actual, group);
    }

    @Test
    public void testGetByNameFailWithEmptyString() {
        Group group = ObjectsFactory.getRandomGroup();

        saveAndEvict(group);

        Group actual = groupDao.getGroupByName(NO_FILTER);
        assertNull(actual);
    }

    @Test
    public void testGetByNameLowerCase() {
        Group group = ObjectsFactory.getRandomGroup();

        saveAndEvict(group);

        Group actual = groupDao.getGroupByName(group.getName().toLowerCase());
        assertReflectionEquals(actual, group);
    }

    @Test
    public void testGetByNameUpperCase() {
        Group group = ObjectsFactory.getRandomGroup();

        saveAndEvict(group);

        Group actual = groupDao.getGroupByName(group.getName().toUpperCase());
        assertReflectionEquals(actual, group);
    }

    @Test
    public void testGetByNameWithSpecialChars() {
        Group group = ObjectsFactory.getRandomGroup();
        group.setName("!@#$%^&*()\"\'\\/");
        saveAndEvict(group);

        Group actual = groupDao.getGroupByName(group.getName());
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

    /**
     * Works properly only with MySql database
     * @throws ParseException
     */
    @Test
    public void listOfGroupsMustBeSortedAlphabetically() throws ParseException{
        if (isMySql()){
            List<GroupAdministrationDto> expected = new LinkedList<>();
            for (int i = 0; i < 10; i++) {
                Group en = new Group(alphanumeric(MIN_GROUP_NAME_LENGTH, MAX_GROUP_NAME_LENGTH));
                Group ru = new Group(between(MIN_GROUP_NAME_LENGTH, MAX_GROUP_NAME_LENGTH).string(DICTIONARY_RU));
                saveAndEvict(en);
                saveAndEvict(ru);
                expected.add(new GroupAdministrationDto(en.getName(), en.getUsers().size()));
                expected.add(new GroupAdministrationDto(ru.getName(), ru.getUsers().size()));
            }
            sortByName(expected);
            List<GroupAdministrationDto> actual = groupDao.getGroupNamesWithCountOfUsers();
            assertReflectionEquals(expected, actual);
        }
    }

    private void sortByName(List<GroupAdministrationDto> dtoList) throws ParseException {
        RuleBasedCollator enUS = (RuleBasedCollator) Collator.getInstance(new Locale("en", "US"));
        final RuleBasedCollator finalCollator = new RuleBasedCollator(enUS.getRules() + SORTING_TEST_RULES);
        Collections.sort(dtoList, new Comparator<GroupAdministrationDto>() {
            @Override
            public int compare(GroupAdministrationDto o1, GroupAdministrationDto o2) {
                return finalCollator.compare(o1.getName(), o2.getName());
            }
        });
    }

    /**
     * listOfGroupsMustBeSortedAlphabetically test can run only with MySql database,
     * so we need to check whether jdbc driver is MySql
     * @return
     */
    private boolean isMySql(){
        String driverName = "";
        try {
            driverName = dataSource.getConnection().getMetaData().getDriverName();
        } catch (SQLException e) {
            logger.warn(e.getMessage());
        }
        return driverName.equalsIgnoreCase("MySQL Connector Java");
    }
}