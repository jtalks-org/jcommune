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
package org.jtalks.jcommune.service.transactional;

import org.jtalks.jcommune.model.dao.TopicDraftDao;
import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.model.entity.TopicDraft;
import org.jtalks.jcommune.service.TopicDraftService;
import org.jtalks.jcommune.service.UserService;
import org.mockito.Mock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.testng.Assert.assertNull;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;

/**
 * @author Dmitry S. Dolzhenko
 */
public class TransactionalTopicDraftServiceTest {

    @Mock
    private UserService userService;
    @Mock
    private TopicDraftDao topicDraftDao;

    private TopicDraftService topicDraftService;

    private JCUser currentUser;

    @BeforeMethod
    public void setUp() {
        initMocks(this);

        currentUser = new JCUser("current", null, null);
        when(userService.getCurrentUser()).thenReturn(currentUser);

        topicDraftService = new TransactionalTopicDraftService(userService, topicDraftDao);
    }

    @Test
    public void getDraftShouldReturnDraftIfUserHasOne() throws Exception {
        TopicDraft expectedDraft = createTopicDraft();
        when(topicDraftDao.getForUser(currentUser)).thenReturn(expectedDraft);

        TopicDraft draft = topicDraftService.getDraft();

        assertReflectionEquals(draft, expectedDraft);
    }


    @Test
    public void getDraftShouldReturnNullIfUserHasNoDraft() throws Exception {
        when(topicDraftDao.getForUser(currentUser)).thenReturn(null);

        assertNull(topicDraftService.getDraft());
    }

    @Test
    public void saveOrUpdateDraftShouldCreateNewDraftIfUserStillHasNoDraft() {
        when(topicDraftDao.getForUser(currentUser)).thenReturn(null);

        TopicDraft topicDraft = topicDraftService.saveOrUpdateDraft(createTopicDraft());

        verify(topicDraftDao).saveOrUpdate(topicDraft);
    }

    @Test
    public void saveOrUpdateDraftShouldUpdateDraftIfUserAlreadyHasOne() {
        TopicDraft topicDraft = createTopicDraft();
        when(topicDraftDao.getForUser(currentUser)).thenReturn(topicDraft);

        topicDraftService.saveOrUpdateDraft(topicDraft);

        verify(topicDraftDao).saveOrUpdate(topicDraft);
    }

    @Test
    public void deleteDraftShouldDeleteDraftIfUserHasOne() {
        TopicDraft topicDraft = createTopicDraft();
        when(topicDraftDao.getForUser(currentUser)).thenReturn(topicDraft);

        topicDraftService.deleteDraft();

        verify(topicDraftDao).deleteByUser(topicDraft.getTopicStarter());
    }

    private TopicDraft createTopicDraft() {
        TopicDraft topicDraft = new TopicDraft(currentUser, "title", "content");
        topicDraft.setId(1L);

        return topicDraft;
    }
}
