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
import org.jtalks.jcommune.model.dao.BranchReadedMarkerDao;
import org.jtalks.jcommune.model.entity.Branch;
import org.jtalks.jcommune.model.entity.BranchReadedMarker;
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

import static junit.framework.Assert.assertNotSame;
import static junit.framework.Assert.assertNull;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;

/**
 * @author Mikhail Stryzhonok
 */
@ContextConfiguration(locations = {"classpath:/org/jtalks/jcommune/model/entity/applicationContext-dao.xml"})
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
@Transactional
public class BranchReadedMarkerDaoTest extends AbstractTransactionalTestNGSpringContextTests {

    @Autowired
    private BranchReadedMarkerDao dao;

    @Autowired
    private SessionFactory sessionFactory;

    private JCUser user;
    private Branch branch;
    private Session session;

    @BeforeMethod
    public void init() {
        session = sessionFactory.getCurrentSession();
        PersistedObjectsFactory.setSession(session);
        user = ObjectsFactory.getDefaultUser();
        branch = ObjectsFactory.getDefaultBranch();
    }

    @Test
    public void markBranchAsReadedTest() {
        session.save(user);
        session.save(branch);
        BranchReadedMarker marker = dao.markBranchAsRead(user, branch);

        assertNotSame(0, marker.getId());
        session.evict(marker);

        BranchReadedMarker result = (BranchReadedMarker)session.get(BranchReadedMarker.class, marker.getId());
        assertReflectionEquals(marker, result);
    }

    @Test
    public void testGetMarkerFor() {
        session.save(user);
        session.save(branch);
        BranchReadedMarker marker = new BranchReadedMarker(user, branch);
        session.save(marker);

        session.evict(marker);

        BranchReadedMarker result = dao.getMarkerFor(user, branch);

        assertReflectionEquals(marker, result);
    }

    @Test
    public void testGetMarkerForShouldReturnNullIfMarkerNotExist() {
        session.save(user);
        session.save(branch);

        BranchReadedMarker result = dao.getMarkerFor(user, branch);

        assertNull(result);
    }

}
