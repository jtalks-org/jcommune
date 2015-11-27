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

import org.hibernate.SessionFactory;
import org.hibernate.classic.Session;
import org.jtalks.jcommune.model.dao.TopicDraftDao;
import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.model.entity.ObjectsFactory;
import org.jtalks.jcommune.model.entity.PersistedObjectsFactory;
import org.jtalks.jcommune.model.entity.TopicDraft;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.validation.ConstraintViolationException;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;

/**
 * @author Dmitry S. Dolzhenko
 */
@ContextConfiguration(locations = {"classpath:/org/jtalks/jcommune/model/entity/applicationContext-dao.xml"})
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
@Transactional
public class TopicDraftHibernateDaoTest extends AbstractTransactionalTestNGSpringContextTests {
    @Autowired
    private SessionFactory sessionFactory;
    @Autowired
    private TopicDraftDao dao;

    private Session session;

    @BeforeMethod
    public void setUp() throws Exception {
        session = sessionFactory.getCurrentSession();
        PersistedObjectsFactory.setSession(session);
    }

    /*===== Common methods =====*/

    @Test
    public void getShouldReturnDraftByValidId() {
        TopicDraft topicDraft = PersistedObjectsFactory.getDefaultTopicDraft();

        session.flush();
        session.clear();

        TopicDraft result = dao.get(topicDraft.getId());

        assertNotNull(result);
        assertReflectionEquals(topicDraft, result);
    }

    @Test
    public void getShouldReturnNullWhenSearchingByInvalidId() {
        assertNull(dao.get(-567890L));
    }

    @Test
    public void saveOrUpdateShouldUpdateExistingDraft() {
        TopicDraft topicDraft = PersistedObjectsFactory.getDefaultTopicDraft();

        session.flush();
        session.clear();

        dao.saveOrUpdate(topicDraft);

        session.flush();
        session.clear();

        TopicDraft result = dao.get(topicDraft.getId());

        assertReflectionEquals(topicDraft, result);
    }

    /*===== TopicDraftDao specific methods =====*/

    @Test
    public void getForUserShouldReturnDraftIfUserHasOne() {
        JCUser user = PersistedObjectsFactory.getDefaultUser();
        TopicDraft topicDraft = ObjectsFactory.getDefaultTopicDraft();

        topicDraft.setTopicStarter(user);
        session.save(topicDraft);

        session.flush();
        session.clear();

        TopicDraft result = dao.getForUser(user);

        assertNotNull(result);
        assertReflectionEquals(user, result.getTopicStarter());
    }

    @Test
    public void getForUserShouldReturnNullIfUserHasNoDraft() {
        JCUser user = PersistedObjectsFactory.getDefaultUser();

        session.flush();
        session.clear();

        assertNull(dao.getForUser(user));
    }

    @Test(expectedExceptions = ConstraintViolationException.class)
    public void saveOrUpdateShouldThrowConstraintViolationExceptionIfAllContentFieldsIsNull() {
        JCUser user = PersistedObjectsFactory.getDefaultUser();

        TopicDraft topicDraft = new TopicDraft();
        topicDraft.setTopicStarter(user);

        topicDraft.setTitle(null);
        topicDraft.setContent(null);
        topicDraft.setPollTitle(null);
        topicDraft.setPollItemsValue(null);

        dao.saveOrUpdate(topicDraft);
    }

    @Test
    public void deleteByUserShouldDeleteTopicDraft() {
        TopicDraft topicDraft = PersistedObjectsFactory.getDefaultTopicDraft();

        session.flush();
        session.clear();

        dao.deleteByUser(topicDraft.getTopicStarter());

        session.flush();
        session.clear();

        assertNull(dao.getForUser(topicDraft.getTopicStarter()));
    }
}
