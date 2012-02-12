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
import org.jtalks.jcommune.model.ObjectsFactory;
import org.jtalks.jcommune.model.entity.UserContactType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.assertEquals;

import java.util.List;

/**
 * @author Michael Gamov
 */
@ContextConfiguration(locations = {"classpath:/org/jtalks/jcommune/model/entity/applicationContext-dao.xml"})
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
@Transactional
public class UserContactsHibernateDaoTest {

    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private UserContactsHibernateDao userContactsDao;

    private Session session;

    @BeforeMethod
    public void setUp() {
        sessionFactory.getCurrentSession();
        ObjectsFactory.setSession(session);
    }

    public void testGetAvailableContactTypes() {
        UserContactType type = ObjectsFactory.getDefaultUserContactType();
        session.saveOrUpdate(type);

        List<UserContactType> types = userContactsDao.getAvailableContactTypes();
        assertEquals(types.size(), 1);
        assertTrue(types.contains(type));
    }
}
