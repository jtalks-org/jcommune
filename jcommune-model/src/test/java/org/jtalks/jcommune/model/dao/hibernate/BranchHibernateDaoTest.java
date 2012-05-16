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
import org.jtalks.jcommune.model.dao.BranchDao;
import org.jtalks.jcommune.model.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * @author Kirill Afonin
 */
@ContextConfiguration(locations = {"classpath:/org/jtalks/jcommune/model/entity/applicationContext-dao.xml"})
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
@Transactional
public class BranchHibernateDaoTest extends AbstractTransactionalTestNGSpringContextTests {

    @Autowired
    private SessionFactory sessionFactory;
    @Autowired
    private BranchDao dao;
    private Session session;
    Branch branch;

    @BeforeMethod
    public void setUp() throws Exception {
        session = sessionFactory.getCurrentSession();
        ObjectsFactory.setSession(session);
        branch = ObjectsFactory.getDefaultBranch();
    }

    @Test
    public void testDeleteTopicFromBranchCascade() {
        Branch branch = ObjectsFactory.getDefaultBranch();
        JCUser author = ObjectsFactory.getDefaultUser();
        session.save(author);
        Topic topic = new Topic(author, "title");
        Post post = new Post(author, "content");
        topic.addPost(post);
        branch.addTopic(topic);
        session.save(branch);

        branch.deleteTopic(topic);
        dao.update(branch);
        session.flush();

        assertEquals(getCount("select count(*) from org.jtalks.jcommune.model.entity.Branch"), 1);
        assertEquals(getCount("select count(*) from Topic"), 0);
        assertEquals(getCount("select count(*) from Post"), 0);
    }

    private int getCount(String hql) {
        return ((Number) session.createQuery(hql).uniqueResult()).intValue();
    }
}
