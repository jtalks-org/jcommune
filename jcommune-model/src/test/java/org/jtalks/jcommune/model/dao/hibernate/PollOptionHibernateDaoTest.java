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
import org.jtalks.jcommune.model.entity.PollItem;
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
public class PollOptionHibernateDaoTest extends AbstractTransactionalTestNGSpringContextTests {
    @Autowired
    private SessionFactory sessionFactory;
    @Autowired
    private Crud<PollItem> pollOptionDao;
    private Session session;

    @BeforeMethod
    public void setUp() {
        session = sessionFactory.getCurrentSession();
        PersistedObjectsFactory.setSession(session);
    }

    /*===== Common methods =====*/

    @Test
    public void testGet() {
        PollItem expectedOption = PersistedObjectsFactory.createDefaultVotingOption();
        session.save(expectedOption);

        PollItem resultOption = pollOptionDao.get(expectedOption.getId());

        Assert.assertNotNull(resultOption);
        Assert.assertEquals(resultOption.getId(), expectedOption.getId());
    }

    @Test
    public void testGetInvalidId() {
        PollItem option = pollOptionDao.get(-11111L);

        Assert.assertNull(option);
    }

    @Test
    public void testUpdate() {
        String newName = "Changed name";
        PollItem option = PersistedObjectsFactory.createDefaultVotingOption();
        session.save(option);

        option.setName(newName);
        pollOptionDao.saveOrUpdate(option);
        session.flush();
        session.evict(option);

        PollItem changedOption = (PollItem) session.get(PollItem.class, option.getId());

        Assert.assertNotNull(changedOption);
        Assert.assertEquals(newName, changedOption.getName());
    }
}
