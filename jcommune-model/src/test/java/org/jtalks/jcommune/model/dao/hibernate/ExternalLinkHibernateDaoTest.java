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
import org.apache.commons.lang.StringEscapeUtils;
import org.hibernate.SessionFactory;
import org.hibernate.classic.Session;
import org.jtalks.jcommune.model.dao.ExternalLinkDao;
import org.jtalks.jcommune.model.entity.ExternalLink;
import org.jtalks.jcommune.model.entity.ObjectsFactory;
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
 * @author Maksim Reshetov
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
    }

    @Test
    public void testSave() throws Exception {
        long id = 1L;
        ExternalLink expected = ObjectsFactory.getDefaultExternalLink();
        expected.setId(id);
        session.save(expected);
        session.clear();

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
        session.clear();

        link = (ExternalLink) session.get(ExternalLink.class, link.getId());
        fillFieldsRandomly(link);

        dao.saveOrUpdate(link);
        session.flush();
        session.clear();

        ExternalLink actual = (ExternalLink) session.get(ExternalLink.class, link.getId());
        assertReflectionEquals(link, actual);
    }

    @Test
    public void testGetLinks() throws Exception {
        List<ExternalLink> link = ObjectsFactory.getExternalLinks(3);
        for (ExternalLink externalLink : link) {
            session.saveOrUpdate(externalLink);
        }
        session.clear();

        List<ExternalLink> actual = dao.getAll();
        assertReflectionEquals(link.get(0), actual.get(0));
        assertEquals(actual.size(), 3);
    }

    @Test(expectedExceptions = ConstraintViolationException.class)
    public void shouldFailWithEmptyTitle() {
        ExternalLink link = ObjectsFactory.getDefaultExternalLink();
        link.setTitle("");
        dao.saveOrUpdate(link);
    }

    @Test(expectedExceptions = ConstraintViolationException.class)
    public void shouldFailWithNullTitle() {
        ExternalLink link = ObjectsFactory.getDefaultExternalLink();
        link.setTitle(null);
        dao.saveOrUpdate(link);
    }


    @Test(expectedExceptions = ConstraintViolationException.class)
    public void shouldFailWithLongTitle() {
        ExternalLink link = ObjectsFactory.getDefaultExternalLink();
        link.setTitle(RandomStringUtils.random(ExternalLink.TITLE_MAX_SIZE + 1, true, false));
        dao.saveOrUpdate(link);
    }

    @Test
    public void shouldSuccessWithMaxLengthTitle() {
        ExternalLink link = ObjectsFactory.getDefaultExternalLink();
        link.setTitle(RandomStringUtils.random(ExternalLink.TITLE_MAX_SIZE, true, false));
        dao.saveOrUpdate(link);
    }

    @Test
    public void shouldSuccessWithMinLengthTitle() {
        ExternalLink link = ObjectsFactory.getDefaultExternalLink();
        link.setTitle(RandomStringUtils.random(ExternalLink.TITLE_MIN_SIZE, true, false));
        dao.saveOrUpdate(link);
    }

    @Test(expectedExceptions = ConstraintViolationException.class)
    public void shouldFailWithNullUrl() {
        ExternalLink link = ObjectsFactory.getDefaultExternalLink();
        link.setUrl(null);
        dao.saveOrUpdate(link);
    }

    @Test(expectedExceptions = ConstraintViolationException.class)
    public void shouldFailWithNotValidUrl() {
        ExternalLink link = ObjectsFactory.getDefaultExternalLink();
        link.setUrl("://jtalks.org");
        dao.saveOrUpdate(link);
    }

    @Test(expectedExceptions = ConstraintViolationException.class)
    public void shouldFailWithLongUrl() {
        ExternalLink link = ObjectsFactory.getDefaultExternalLink();
        //-10 = protocol + domen
        link.setUrl("http://" + RandomStringUtils.random(ExternalLink.URL_MAX_SIZE - 10, true, false) + ".org");
        session.saveOrUpdate(link);
    }

    @Test
    public void shouldSuccessWithMaxLengthUrl() {
        ExternalLink link = ObjectsFactory.getDefaultExternalLink();
        //-10 = protocol + domen
        link.setUrl("http://" + RandomStringUtils.random(ExternalLink.URL_MAX_SIZE - 11, true, false) + ".org");
        dao.saveOrUpdate(link);
    }

    @Test(expectedExceptions = ConstraintViolationException.class)
    public void nullHintForExternalLinkShouldRaiseConstraintException() {
        ExternalLink link = ObjectsFactory.getDefaultExternalLink();
        link.setHint(null);
        dao.saveOrUpdate(link);
    }

    @Test
    public void shouldSuccessWithMaxLengthHint() {
        ExternalLink link = ObjectsFactory.getDefaultExternalLink();
        link.setHint(RandomStringUtils.random(ExternalLink.HINT_MAX_SIZE, true, false));
        dao.saveOrUpdate(link);
    }

    @Test(expectedExceptions = ConstraintViolationException.class)
    public void shouldFailWithLongHint() {
        ExternalLink link = ObjectsFactory.getDefaultExternalLink();
        link.setHint(RandomStringUtils.random(ExternalLink.HINT_MAX_SIZE + 1, true, false));
        dao.saveOrUpdate(link);
    }

    private void fillFieldsRandomly(ExternalLink link) {
        link.setTitle("New title");
        link.setUrl(StringEscapeUtils.escapeJava("http://jtalks.org"));
        link.setHint("New hint");
    }
}
