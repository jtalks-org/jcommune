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
import org.jtalks.jcommune.plugin.api.service.PluginTopicDraftService;
import org.jtalks.jcommune.service.TopicDraftService;
import org.jtalks.jcommune.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 * The implementation of TopicDraftService interface.
 *
 * @author Dmitry S. Dolzhenko
 */
public class TransactionalTopicDraftService implements TopicDraftService, PluginTopicDraftService {

    private final UserService userService;
    private final TopicDraftDao topicDraftDao;

    public TransactionalTopicDraftService(UserService userService, TopicDraftDao topicDraftDao) {
        this.userService = userService;
        this.topicDraftDao = topicDraftDao;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TopicDraft getDraft() {
        JCUser user = userService.getCurrentUser();
        return topicDraftDao.getForUser(user);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @PreAuthorize("isAuthenticated()")
    public TopicDraft saveOrUpdateDraft(TopicDraft draft) {
        JCUser user = userService.getCurrentUser();

        TopicDraft currentDraft = topicDraftDao.getForUser(user);
        if (currentDraft == null) {
            currentDraft = draft;
            currentDraft.setTopicStarter(user);
        } else {
            currentDraft.setContent(draft.getContent());
            currentDraft.setTitle(draft.getTitle());
            currentDraft.setPollTitle(draft.getPollTitle());
            currentDraft.setPollItemsValue(draft.getPollItemsValue());
        }

        currentDraft.updateLastSavedTime();
        topicDraftDao.saveOrUpdate(currentDraft);

        return currentDraft;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteDraft() {
        JCUser user = userService.getCurrentUser();
        topicDraftDao.deleteByUser(user);
    }
}
