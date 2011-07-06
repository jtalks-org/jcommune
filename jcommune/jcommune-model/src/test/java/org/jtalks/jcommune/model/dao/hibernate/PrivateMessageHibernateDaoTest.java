/**
 * Copyright (C) 2011  jtalks.org Team
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
 * Also add information on how to contact you by electronic and paper mail.
 * Creation date: Apr 12, 2011 / 8:05:19 PM
 * The jtalks.org Project
 */
package org.jtalks.jcommune.model.dao.hibernate;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.jtalks.jcommune.model.dao.PrivateMessageDao;
import org.jtalks.jcommune.model.entity.PrivateMessage;
import org.jtalks.jcommune.model.entity.PrivateMessageStatus;
import org.jtalks.jcommune.model.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.*;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;

/**
 * @author Pavel Vervenko
 */
@ContextConfiguration(locations = {"classpath:/org/jtalks/jcommune/model/entity/applicationContext-dao.xml"})
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
@Transactional
public class PrivateMessageHibernateDaoTest extends AbstractTransactionalTestNGSpringContextTests {

    @Autowired
    private SessionFactory sessionFactory;
    @Autowired
    private PrivateMessageDao dao;
    private Session session;
    private PrivateMessage notReadPm;
    private PrivateMessage readPm;
    private PrivateMessage draftPm;
    private User author;
    private User recipient;

    @BeforeMethod
    public void setUp() throws Exception {
        session = sessionFactory.getCurrentSession();
        ObjectsFactory.setSession(session);
    }

    @Test
    public void testSave() {
        PrivateMessage pm = getSavedPm();
        assertNotSame(pm.getId(), 0, "Id not created");

        session.evict(pm);
        PrivateMessage result = (PrivateMessage) session.get(PrivateMessage.class, pm.getId());

        assertReflectionEquals(pm, result);
    }

    @Test(expectedExceptions = DataIntegrityViolationException.class)
    public void testSavePostWithDateNotNullViolation() {
        PrivateMessage pm = new PrivateMessage();
        dao.saveOrUpdate(pm);
    }

    @Test
    public void testGet() {
        PrivateMessage pm = getSavedPm();

        PrivateMessage result = dao.get(pm.getId());

        assertNotNull(result);
        assertEquals(result.getId(), pm.getId());
    }

    @Test
    public void testGetInvalidId() {
        PrivateMessage result = dao.get(-567890L);

        assertNull(result);
    }

    @Test
    public void testUpdate() {
        String newBody = "new content";
        PrivateMessage pm = getSavedPm();
        pm.setBody(newBody);

        dao.saveOrUpdate(pm);
        session.evict(pm);
        PrivateMessage result = (PrivateMessage) session.get(PrivateMessage.class, pm.getId());

        assertEquals(result.getBody(), newBody);
    }

    @Test(expectedExceptions = DataIntegrityViolationException.class)
    public void testUpdateNotNullViolation() {
        PrivateMessage pm = getSavedPm();
        pm.setUserFrom(null);

        dao.saveOrUpdate(pm);
    }

    @Test
    public void testDelete() {
        PrivateMessage pm = getSavedPm();

        boolean result = dao.delete(pm.getId());
        int pmCount = getCount();

        assertTrue(result, "Entity is not deleted");
        assertEquals(pmCount, 0);
    }

    @Test
    public void testDeleteInvalidId() {
        boolean result = dao.delete(-100500L);

        assertFalse(result, "Entity deleted");
    }

    @Test
    public void testGetAllFromUser() {
        saveMessagesWithDifferentStatus();

        List<PrivateMessage> listFrom = dao.getAllFromUser(author);

        assertEquals(listFrom.size(), 2);
        assertFalse(listFrom.contains(draftPm));
    }

    @Test
    public void testGetAllToUser() {
        saveMessagesWithDifferentStatus();

        List<PrivateMessage> listFrom = dao.getAllForUser(recipient);

        assertEquals(listFrom.size(), 2);
        assertFalse(listFrom.contains(draftPm));
    }

    @Test
    public void testGetDraftsFromUser() {
        saveMessagesWithDifferentStatus();

        List<PrivateMessage> list = dao.getDraftsFromUser(author);

        assertEquals(list.size(), 1);
        assertEquals(list.get(0), draftPm);
    }

    @Test
    public void testGetNewMessagesCountFor() {
        saveMessagesWithDifferentStatus();

        int count = dao.getNewMessagesCountFor(recipient.getUsername());

        assertEquals(count, 1);
    }

    private void saveMessagesWithDifferentStatus() {
        author = ObjectsFactory.getUser("author", "author@aaa.com");
        recipient = ObjectsFactory.getUser("recipient", "recipient@aaa.com");
        session.saveOrUpdate(author);
        session.saveOrUpdate(recipient);
        // creating messages with different statuses
        notReadPm = ObjectsFactory.getPrivateMessage(recipient, author);
        readPm = ObjectsFactory.getPrivateMessage(recipient, author);
        readPm.setStatus(PrivateMessageStatus.READ);
        draftPm = ObjectsFactory.getPrivateMessage(recipient, author);
        draftPm.setStatus(PrivateMessageStatus.DRAFT);
        session.save(notReadPm);
        session.save(readPm);
        session.save(draftPm);
    }

    /**
     * Count the number of PrivateMessage in the db.
     */
    private int getCount() {
        return ((Number) session.createQuery("select count(*) from PrivateMessage").uniqueResult()).intValue();
    }

    /**
     * Create new PrivateMessage with filled fields and save it.
     *
     * @return saved object
     * @throws HibernateException
     */
    private PrivateMessage getSavedPm() throws HibernateException {
        PrivateMessage pm = ObjectsFactory.getDefaultPrivateMessage();
        session.saveOrUpdate(pm);
        return pm;
    }
}
