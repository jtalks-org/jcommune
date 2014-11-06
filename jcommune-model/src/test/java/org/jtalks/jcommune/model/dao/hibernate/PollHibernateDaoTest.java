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
import org.jtalks.common.model.dao.Crud;
import org.jtalks.jcommune.model.entity.PersistedObjectsFactory;
import org.jtalks.jcommune.model.entity.Poll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author Anuar Nurmakanov
 */
@ContextConfiguration(locations = {"classpath:/org/jtalks/jcommune/model/entity/applicationContext-dao.xml"})
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
@Transactional
public class PollHibernateDaoTest extends AbstractTransactionalTestNGSpringContextTests {
    @Autowired
    private SessionFactory sessionFactory;
    @Autowired
    private Crud<Poll> pollDao;
    private Session session;

    @BeforeMethod
    public void setUp() {
        session = sessionFactory.getCurrentSession();
        PersistedObjectsFactory.setSession(session);
    }

    /*===== Common methods =====*/

    @Test
    public void testGet() {
        Poll expectedPoll = PersistedObjectsFactory.createDefaultVoting();
        session.save(expectedPoll);

        Poll resultPoll = pollDao.get(expectedPoll.getId());

        Assert.assertNotNull(resultPoll);
        Assert.assertEquals(resultPoll.getId(), expectedPoll.getId());
    }

    @Test
    public void testGetInvalidId() {
        Poll poll = pollDao.get(-111111L);

        Assert.assertNull(poll);
    }

    @Test
    public void testUpdate() {
        String newTitle = "Changed title";
        Poll poll = PersistedObjectsFactory.createDefaultVoting();
        session.save(poll);

        poll.setTitle(newTitle);
        pollDao.saveOrUpdate(poll);
        session.flush();
        session.evict(poll);

        Poll changedPoll = (Poll) session.get(Poll.class, poll.getId());

        Assert.assertNotNull(changedPoll);
        Assert.assertEquals(newTitle, changedPoll.getTitle());
    }
}
