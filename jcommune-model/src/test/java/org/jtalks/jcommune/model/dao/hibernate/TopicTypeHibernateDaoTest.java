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
import org.jtalks.jcommune.model.dao.TopicTypeDao;
import org.jtalks.jcommune.model.entity.ObjectsFactory;
import org.jtalks.jcommune.model.entity.PersistedObjectsFactory;
import org.jtalks.jcommune.model.entity.TopicType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;

/**
 * @author Mikhail Stryzhonok
 */
@ContextConfiguration(locations = {"classpath:/org/jtalks/jcommune/model/entity/applicationContext-dao.xml"})
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
@Transactional
public class TopicTypeHibernateDaoTest extends AbstractTransactionalTestNGSpringContextTests {
    @Autowired
    private SessionFactory sessionFactory;
    @Autowired
    private TopicTypeDao topicTypeDao;
    private Session session;

    @BeforeMethod
    public void setUp() {
        session = sessionFactory.getCurrentSession();
        PersistedObjectsFactory.setSession(session);
    }

    @Test
    public void testSaveOrUpdate() {
        TopicType topicType = ObjectsFactory.getDefaultTopicType();
        topicTypeDao.saveOrUpdate(topicType);
        flushAndClearSession();

        TopicType result = (TopicType)session.get(TopicType.class, topicType.getId());

        assertReflectionEquals(topicType, result);
    }

    @Test(expectedExceptions = DataIntegrityViolationException.class)
    public void saveNotUniqueNameShouldThrowException() {
        TopicType topicType1 = ObjectsFactory.getDefaultTopicType();
        TopicType topicType2 = ObjectsFactory.getDefaultTopicType();

        topicTypeDao.saveOrUpdate(topicType1);
        topicTypeDao.saveOrUpdate(topicType2);
    }

    @Test
    public void testGetByName() {
        TopicType topicType = ObjectsFactory.getDefaultTopicType();
        session.save(topicType);
        flushAndClearSession();

        TopicType result = topicTypeDao.getByName(topicType.getName());

        assertReflectionEquals(topicType, result);
    }

    private void flushAndClearSession() {
        session.flush();
        session.clear();
    }
}
