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

import org.joda.time.DateTime;
import org.jtalks.jcommune.model.dao.TopicDao;
import org.jtalks.jcommune.model.dto.JCommunePageRequest;
import org.jtalks.jcommune.model.entity.Branch;
import org.jtalks.jcommune.model.entity.Topic;
import org.jtalks.jcommune.service.TopicFetchService;
import org.jtalks.jcommune.service.UserService;
import org.jtalks.jcommune.service.exceptions.NotFoundException;
import org.springframework.data.domain.Page;

/**
 *
 */
public class TransactionalTopicFetchService extends AbstractTransactionalEntityService<Topic, TopicDao>
        implements TopicFetchService {
    
    private UserService userService;

    /**
     * @param dao topic dao for database manipulations
     * @param userService to get current user and his preferences
     */
    public TransactionalTopicFetchService(TopicDao dao, UserService userService) {
        super(dao);
        this.userService = userService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Topic get(Long id) throws NotFoundException {
        Topic topic = super.get(id);
        topic.setViews(topic.getViews() + 1);
        this.getDao().update(topic);
        return topic;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<Topic> getRecentTopics(int page) {
        JCommunePageRequest pageRequest = JCommunePageRequest.
                createWithPagingEnabled(page, userService.getCurrentUser().getPageSize());
        DateTime date24HoursAgo = new DateTime().minusDays(1);
        return this.getDao().getTopicsUpdatedSince(date24HoursAgo, pageRequest);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<Topic> getUnansweredTopics(int page) {
        JCommunePageRequest pageRequest = JCommunePageRequest.
                createWithPagingEnabled(page, userService.getCurrentUser().getPageSize());
        return this.getDao().getUnansweredTopics(pageRequest);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<Topic> getTopics(Branch branch, int page, boolean pagingEnabled) {
        JCommunePageRequest pageRequest = new JCommunePageRequest(
                page, userService.getCurrentUser().getPageSize(), pagingEnabled);
        return getDao().getTopics(branch, pageRequest);
    }
}
