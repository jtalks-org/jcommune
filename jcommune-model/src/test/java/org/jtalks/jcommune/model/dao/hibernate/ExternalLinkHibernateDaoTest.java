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

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Alexandre Teterin
 *         Date: 03.02.13
 */

@ContextConfiguration(locations = {"classpath:/org/jtalks/jcommune/model/entity/applicationContext-dao.xml"})
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
@Transactional
public class ExternalLinkHibernateDaoTest extends AbstractTransactionalTestNGSpringContextTests {

//    @Autowired
//    private SessionFactory sessionFactory;
//    @Autowired
//    private ExternalLinkDao dao;
//
//    private Session session;
//
//    @BeforeMethod
//    public void setUp() throws Exception {
//        session = sessionFactory.getCurrentSession();
//        PersistedObjectsFactory.setSession(session);
//    }
//
//    @Test
//    public void testSave() throws Exception {
//        long id = 1L;
//        ExternalLink expected = ObjectsFactory.getDefaultExternalLink();
//        expected.setId(id);
//        session.save(expected);
//        session.evict(expected);
//
//        ExternalLink actual = (ExternalLink) session.get(ExternalLink.class, expected.getId());
//        assertReflectionEquals(expected, actual);
//    }
//
//    @Test
//    public void testGetMissingId() {
//        assertNull(dao.get(Long.MAX_VALUE));
//    }
//
//    @Test
//    public void testUpdate() throws Exception {
//        ExternalLink link = ObjectsFactory.getDefaultExternalLink();
//        dao.saveOrUpdate(link);
//        session.evict(link);
//
//        link = (ExternalLink) session.get(ExternalLink.class, link.getId());
//        link.setTitle("New title");
//        link.setUrl("New url");
//        link.setHint("New hint");
//
//        dao.update(link);
//        session.clear();
//
//        ExternalLink actual = (ExternalLink) session.get(ExternalLink.class, link.getId());
//        assertReflectionEquals(link, actual);
//    }
//
//    @Test
//    public void testGetLinks() throws Exception {
//        ExternalLink link = ObjectsFactory.getDefaultExternalLink();
//        session.saveOrUpdate(link);
//        session.clear();
//
//        List<ExternalLink> actual = dao.getAll();
//        assertReflectionEquals(link, actual.get(0));
//    }
//
//    @Test(expectedExceptions = ConstraintViolationException.class)
//    public void shouldFailWithNullTitle() {
//        ExternalLink link = ObjectsFactory.getDefaultExternalLink();
//        link.setTitle(null);
//        session.saveOrUpdate(link);
//    }
//
//    @Test(expectedExceptions = ConstraintViolationException.class)
//    public void shouldFailWithNullUrl() {
//        ExternalLink link = ObjectsFactory.getDefaultExternalLink();
//        link.setUrl(null);
//        session.saveOrUpdate(link);
//    }
//
//    @Test(expectedExceptions = ConstraintViolationException.class)
//    public void shouldFailWithNullHint() {
//        ExternalLink link = ObjectsFactory.getDefaultExternalLink();
//        link.setHint(null);
//        session.saveOrUpdate(link);
//    }
}
