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

/**
 * Created by IntelliJ IDEA.
 * User: Alexander
 * Date: 27.05.12
 * Time: 15:47
 * To change this template use File | Settings | File Templates.
 */

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.jtalks.jcommune.model.dao.SimplePageDao;
import org.jtalks.jcommune.model.entity.ObjectsFactory;
import org.jtalks.jcommune.model.entity.SimplePage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.testng.AssertJUnit.*;

/**
 * @author Alexander Gavrikov
 */

@ContextConfiguration(locations = {"classpath:/org/jtalks/jcommune/model/entity/applicationContext-dao.xml"})
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
@Transactional
public class SimplePageHibernateDaoTest extends AbstractTransactionalTestNGSpringContextTests {
    @Autowired
    private SessionFactory sessionFactory;
    @Autowired
    private SimplePageDao dao;
    private Session session;


    @BeforeMethod
    public void setUp() throws Exception {
        session = sessionFactory.getCurrentSession();
    }

    /*===== Common methods =====*/

    @Test
    public void testGet() {
        SimplePage simplePage = ObjectsFactory.getDefaultSimplePage();
        session.save(simplePage);

        SimplePage result = dao.get(simplePage.getId());

        assertNotNull(result);
        assertEquals(result.getId(), simplePage.getId());
        assertEquals(result.getName(), simplePage.getName());
        assertEquals(result.getContent(), simplePage.getContent());
        assertEquals(result.getPathName(), simplePage.getPathName());
    }

    @Test
    public void testGetInvalidId() {
        SimplePage simplePage = dao.get(-567890L);
        assertNull(simplePage);
    }

    @Test
    public void testUpdate() {
        String newName = "newName";
        String newContent = "newContent";
        SimplePage simplePage = ObjectsFactory.getDefaultSimplePage();
        session.save(simplePage);

        simplePage.setName(newName);
        simplePage.setContent(newContent);

        dao.saveOrUpdate(simplePage);
        session.flush();
        session.evict(simplePage);
        SimplePage result = (SimplePage) session.get(SimplePage.class, simplePage.getId());

        assertEquals(result.getName(), newName);
        assertEquals(result.getContent(), newContent);
    }

    @Test
    public void testIsExist() {
        SimplePage simplePage = ObjectsFactory.getDefaultSimplePage();
        session.save(simplePage);
        assertTrue(dao.isExist(simplePage.getId()));
    }

    @Test
    public void testIsNotExist() {
        assertFalse(dao.isExist(-567890L));
    }

    /* SimplePageHibernateDao specific methods */

    @Test
    public void testCreatePage() {
        SimplePage simplePage = ObjectsFactory.getDefaultSimplePage();
        session.save(simplePage);

        dao.createPage(simplePage);
        session.evict(simplePage);
        SimplePage result = (SimplePage) session.get(SimplePage.class, simplePage.getId());

        assertNotNull(result);
        assertEquals(simplePage.getId(), result.getId());
        assertEquals(simplePage.getName(), result.getName());
        assertEquals(simplePage.getContent(), result.getContent());
        assertEquals(simplePage.getPathName(), result.getPathName());
    }

    @Test
    public void testGetPageByPathName() {
        SimplePage simplePage = ObjectsFactory.getDefaultSimplePage();
        session.save(simplePage);

        SimplePage result = dao.getPageByPathName(simplePage.getPathName());

        assertNotNull(result);
        assertEquals(simplePage.getId(), result.getId());
        assertEquals(simplePage.getName(), result.getName());
        assertEquals(simplePage.getContent(), result.getContent());
        assertEquals(simplePage.getPathName(), result.getPathName());
    }

    @Test
    public void testGetPageByPathNameNotExist() {
        SimplePage simplePage = ObjectsFactory.getDefaultSimplePage();
        session.save(simplePage);

        SimplePage result = dao.getPageByPathName("not Exist Path Name");

        assertNull(result);
    }
}

