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
import org.jtalks.jcommune.model.PersistedObjectsFactory;
import org.jtalks.jcommune.model.dao.ExternalLinkDao;
import org.jtalks.jcommune.model.entity.ExternalLink;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNull;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;

/**
 * @author Alexandre Teterin
 *         Date: 03.02.13
 */

@ContextConfiguration(locations = {"classpath:/org/jtalks/jcommune/model/entity/applicationContext-dao.xml"})
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
@Transactional
public class ExternalLinkHibernateDaoTest extends AbstractTransactionalTestNGSpringContextTests {

    @Autowired
    private SessionFactory sessionFactory;
    @Autowired
    private ExternalLinkDao dao;

    private Session session;

    @BeforeMethod
    public void setUp() throws Exception {
        session = sessionFactory.getCurrentSession();
        PersistedObjectsFactory.setSession(session);
    }

    @Test
    public void testSave() throws Exception {
        long id = 1L;
        ExternalLink expected = ObjectsFactory.getDefaultExternalLink();
        expected.setId(id);
        session.save(expected);
        session.evict(expected);
        ExternalLink actual = (ExternalLink) session.get(ExternalLink.class, expected.getId());
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getTitle(), actual.getTitle());
        assertEquals(expected.getUrl(), actual.getUrl());
        assertEquals(expected.getHint(), actual.getHint());
    }

    @Test
    public void testGetMissingId() {
        assertNull(dao.get(Long.MAX_VALUE));
    }

    @Test
    public void testUpdate() throws Exception {
        long id = 1L;
        ExternalLink expected = ObjectsFactory.getDefaultExternalLink();
        expected.setId(id);
        session.save(expected);
        session.evict(expected);
        expected = (ExternalLink) session.get(ExternalLink.class, expected.getId());
        expected.setTitle("New title");
        expected.setUrl("New url");
        expected.setHint("New hint");

        dao.update(expected);
        session.evict(expected);
        ExternalLink actual = (ExternalLink) session.get(ExternalLink.class, expected.getId());
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getTitle(), actual.getTitle());
        assertEquals(expected.getUrl(), actual.getUrl());
        assertEquals(expected.getHint(), actual.getHint());
    }

    @Test
    public void testGetLinks() throws Exception {
        ExternalLink expected = ObjectsFactory.getDefaultExternalLink();
        session.saveOrUpdate(expected);
        session.clear();

        List<ExternalLink> actual = dao.getAll();
        assertReflectionEquals(expected, actual.get(0));
    }

    @Test(expectedExceptions = {Exception.class})
    public void tesShouldNotSaveWithNullTitle() {
        ExternalLink link = ObjectsFactory.getDefaultExternalLink();
        link.setTitle(null);
        session.saveOrUpdate(link);
    }

    @Test(expectedExceptions = {Exception.class})
    public void tesShouldNotSaveWithNullUrl() {
        ExternalLink link = ObjectsFactory.getDefaultExternalLink();
        link.setUrl(null);
        session.saveOrUpdate(link);
    }

    @Test(expectedExceptions = {Exception.class})
    public void tesShouldNotSaveWithNullHint() {
        ExternalLink link = ObjectsFactory.getDefaultExternalLink();
        link.setHint(null);
        session.saveOrUpdate(link);
    }
}
