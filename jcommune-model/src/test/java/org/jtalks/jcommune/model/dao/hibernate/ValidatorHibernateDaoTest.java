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

import org.hibernate.SessionFactory;
import org.jtalks.jcommune.model.entity.ObjectsFactory;
import org.jtalks.jcommune.model.dao.ValidatorDao;
import org.jtalks.jcommune.model.entity.JCUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.Test;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * @author Evgeniy Naumenko
 */
@ContextConfiguration(locations = {"classpath:/org/jtalks/jcommune/model/entity/applicationContext-dao.xml"})
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
@Transactional
public class ValidatorHibernateDaoTest extends AbstractTransactionalTestNGSpringContextTests {

    @Autowired
    private SessionFactory sessionFactory;
    @Autowired
    ValidatorDao<String> dao;

    @Test
    public void testResultSetisEmptyNoField() {
        assertTrue(dao.isResultSetEmpty(JCUser.class, "username", "lol", false));
    }
    
    @Test
    public void testResultSetIsEmptyDifferentCases() {
        String realname = ObjectsFactory.getDefaultUser().getUsername().toUpperCase();
        sessionFactory.getCurrentSession().saveOrUpdate(ObjectsFactory.getDefaultUser());

        assertTrue(dao.isResultSetEmpty(JCUser.class, "username", realname, false));
    }

    @Test
    public void testResultSetIsNotEmptyCaseSensitive() {
        String realname = ObjectsFactory.getDefaultUser().getUsername();
        sessionFactory.getCurrentSession().saveOrUpdate(ObjectsFactory.getDefaultUser());

        assertFalse(dao.isResultSetEmpty(JCUser.class, "username", realname, false));
    }
    
    @Test
    public void testResultSetIsNotEmptyIgnoreCase() {
        String realname = ObjectsFactory.getDefaultUser().getUsername().toUpperCase();
        sessionFactory.getCurrentSession().saveOrUpdate(ObjectsFactory.getDefaultUser());

        assertFalse(dao.isResultSetEmpty(JCUser.class, "username", realname, true));
    }
    
    @Test
    public void testIsExistsNoField() {
        assertFalse(dao.isExists(JCUser.class, "username", "lol", false));
    }
    
    @Test
    public void testIsExistsDifferentCases() {
        String realname = ObjectsFactory.getDefaultUser().getUsername().toUpperCase();
        sessionFactory.getCurrentSession().saveOrUpdate(ObjectsFactory.getDefaultUser());

        assertFalse(dao.isExists(JCUser.class, "username", realname, false));
    }

    @Test
    public void testIsExistsExistCaseSensitive() {
        String realname = ObjectsFactory.getDefaultUser().getUsername();
        sessionFactory.getCurrentSession().saveOrUpdate(ObjectsFactory.getDefaultUser());

        assertTrue(dao.isExists(JCUser.class, "username", realname, false));
    }
    
    @Test
    public void testIsExistsExistIgnoreCase() {
        String realname = ObjectsFactory.getDefaultUser().getUsername().toUpperCase();
        sessionFactory.getCurrentSession().saveOrUpdate(ObjectsFactory.getDefaultUser());

        assertTrue(dao.isExists(JCUser.class, "username", realname, true));
    }
    
    @Test
    public void testIsExistsSameIgnoreCaseValuesNotExists() {
        sessionFactory.getCurrentSession().saveOrUpdate(ObjectsFactory.getUser("Username", "Username@mail.com"));
        sessionFactory.getCurrentSession().saveOrUpdate(ObjectsFactory.getUser("uSername", "uSername@mail.com"));

        assertFalse(dao.isExists(JCUser.class, "username", "USERNAME", true));
    }
    
    @Test
    public void testIsExistsSameIgnoreCaseValuesExists() {
        JCUser expectedUser = ObjectsFactory.getUser("Username", "Username@mail.com");
        String expectedUsername = expectedUser.getUsername(); 
        sessionFactory.getCurrentSession().saveOrUpdate(expectedUser);
        sessionFactory.getCurrentSession().saveOrUpdate(ObjectsFactory.getUser("uSername", "uSername@mail.com"));

        assertTrue(dao.isExists(JCUser.class, "username", expectedUsername, true));
    }
}
