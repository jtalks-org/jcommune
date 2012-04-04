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
import org.jtalks.jcommune.model.dao.VotingDao;
import org.jtalks.jcommune.model.entity.Voting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * 
 * @author Anuar Nurmakanov
 *
 */
@ContextConfiguration(locations = {"classpath:/org/jtalks/jcommune/model/entity/applicationContext-dao.xml"})
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
@Transactional
public class VotingHibernateDaoTest extends AbstractTransactionalTestNGSpringContextTests {
    @Autowired
    private SessionFactory sessionFactory;
    @Autowired
    private VotingDao votingDao;
    private Session session;
    
    @BeforeMethod
    public void setUp() {
        session = sessionFactory.getCurrentSession();
        ObjectsFactory.setSession(session);
    }
    
    /*===== Common methods =====*/
    
    @Test
    public void testGet() {
        Voting expectedVoting = ObjectsFactory.createDefaultVoting();
        session.save(expectedVoting);
        
        Voting resultVoting = votingDao.get(expectedVoting.getId());
        
        Assert.assertNotNull(resultVoting);
        Assert.assertEquals(resultVoting.getId(), expectedVoting.getId());
    }
    
    @Test
    public void testGetInvalidId() {
        Voting voting = votingDao.get(-111111L);
        
        Assert.assertNull(voting);
    }
    
    @Test
    public void testUpdate() {
        String newTitle = "Changed title";
        Voting voting = ObjectsFactory.createDefaultVoting();
        session.save(voting);
        
        voting.setTitle(newTitle);
        votingDao.update(voting);
        session.evict(voting);
        
        Voting changedVoting = (Voting) session.get(Voting.class, voting.getId());
        
        Assert.assertNotNull(changedVoting);
        Assert.assertEquals(newTitle, changedVoting.getTitle());
    }
}
