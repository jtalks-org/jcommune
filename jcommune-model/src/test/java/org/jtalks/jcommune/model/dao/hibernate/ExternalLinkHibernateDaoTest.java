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

import org.apache.commons.lang.RandomStringUtils;
import org.hibernate.SessionFactory;
import org.hibernate.classic.Session;

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

import javax.validation.ConstraintViolationException;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
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
    private static final int TITLE_MAX_SIZE = 30;
    private static final int TITLE_MIN_SIZE = 1;
    private static final int URL_MAX_SIZE = 512;
    private static final int URL_MIN_SIZE = 10;
    private static final int HINT_MAX_SIZE = 128;

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
        assertReflectionEquals(expected, actual);
    }

    @Test
    public void testGetMissingId() {
        assertNull(dao.get(Long.MAX_VALUE));
    }

    @Test
    public void testUpdate() throws Exception {
        ExternalLink link = ObjectsFactory.getDefaultExternalLink();
        dao.saveOrUpdate(link);
        session.evict(link);

        link = (ExternalLink) session.get(ExternalLink.class, link.getId());
        link.setTitle("New title");
        link.setUrl("http://jtalks.org");
        link.setHint("New hint");

        dao.update(link);
        session.clear();

        ExternalLink actual = (ExternalLink) session.get(ExternalLink.class, link.getId());
        assertReflectionEquals(link, actual);
    }

    @Test
    public void testGetLinks() throws Exception {
        ExternalLink link = ObjectsFactory.getDefaultExternalLink();
        session.saveOrUpdate(link);
        session.clear();

        List<ExternalLink> actual = dao.getAll();
        assertReflectionEquals(link, actual.get(0));
    }

    @Test(expectedExceptions = ConstraintViolationException.class)
    public void shouldFailWithEmptyTitle() {
        ExternalLink link = ObjectsFactory.getDefaultExternalLink();
        link.setTitle("");
        session.saveOrUpdate(link);
    }

    @Test(expectedExceptions = ConstraintViolationException.class)
    public void shouldFailWithLongTitle() {
        ExternalLink link = ObjectsFactory.getDefaultExternalLink();
        link.setTitle(RandomStringUtils.random(TITLE_MAX_SIZE + 1));
        session.saveOrUpdate(link);
    }

    @Test
    public void shouldSuccessWithMaxLengthTitle() {
        ExternalLink link = ObjectsFactory.getDefaultExternalLink();
        link.setTitle(RandomStringUtils.random(TITLE_MAX_SIZE));
        session.saveOrUpdate(link);
    }

    @Test
    public void shouldSuccessWithMinLengthTitle() {
        ExternalLink link = ObjectsFactory.getDefaultExternalLink();
        link.setTitle(RandomStringUtils.random(TITLE_MIN_SIZE));
        session.saveOrUpdate(link);
    }

    @Test(expectedExceptions = ConstraintViolationException.class)
    public void shouldFailWithEmptyUrl() {
        ExternalLink link = ObjectsFactory.getDefaultExternalLink();
        link.setUrl("");
        session.saveOrUpdate(link);
    }

    @Test(expectedExceptions = ConstraintViolationException.class)
    public void shouldFailWithNotValidUrl() {
        ExternalLink link = ObjectsFactory.getDefaultExternalLink();
        link.setUrl("jtalks.org");
        session.saveOrUpdate(link);
    }

    @Test(expectedExceptions = ConstraintViolationException.class)
    public void shouldFailWithLongUrl() {
        ExternalLink link = ObjectsFactory.getDefaultExternalLink();
        //-10 = protocol + domen
        link.setUrl("http://" + RandomStringUtils.random(URL_MAX_SIZE - 10) + ".org");
        session.saveOrUpdate(link);
    }

    @Test
    public void shouldSuccessWithMaxLengthUrl() {
        ExternalLink link = ObjectsFactory.getDefaultExternalLink();
        //-10 = protocol + domen
        link.setUrl("http://" + RandomStringUtils.random(URL_MAX_SIZE - 11) + ".org");
        session.saveOrUpdate(link);
    }

    @Test
    public void shouldSuccessWithNullHint() {
        ExternalLink link = ObjectsFactory.getDefaultExternalLink();
        link.setHint("");
        session.saveOrUpdate(link);
        assertEquals(link.getHint(), "");
    }

    @Test
    public void shouldSuccessWithMaxLengthHint() {
        ExternalLink link = ObjectsFactory.getDefaultExternalLink();
        link.setHint(RandomStringUtils.random(HINT_MAX_SIZE));
        session.saveOrUpdate(link);
    }

    @Test(expectedExceptions = ConstraintViolationException.class)
    public void shouldFailWithLongHint() {
        ExternalLink link = ObjectsFactory.getDefaultExternalLink();
        link.setHint(RandomStringUtils.random(HINT_MAX_SIZE + 1));
        session.saveOrUpdate(link);
    }
}
