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

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.jtalks.jcommune.model.dao.TopicDao;
import org.jtalks.jcommune.model.dao.search.TopicSearchDao;
import org.jtalks.jcommune.model.dto.PageRequest;
import org.jtalks.jcommune.model.entity.Branch;
import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.model.entity.Topic;
import org.jtalks.jcommune.service.TopicFetchService;
import org.jtalks.jcommune.service.UserService;
import org.jtalks.jcommune.service.exceptions.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.Collections;
import java.util.List;

/**
 * Performs load operations on topic based on various
 * conditions. Topic search operations are also performed here.
 */
public class TransactionalTopicFetchService extends AbstractTransactionalEntityService<Topic, TopicDao>
        implements TopicFetchService {

    private UserService userService;
    private TopicSearchDao searchDao;

    /**
     * @param dao         topic dao for database manipulations
     * @param userService to get current user and his preferences
     * @param searchDao   for search index access
     */
    public TransactionalTopicFetchService(TopicDao dao, UserService userService, TopicSearchDao searchDao) {
        super(dao);
        this.userService = userService;
        this.searchDao = searchDao;
    }

    /**
     * {@inheritDoc}
     */
    @Override    
    public Topic get(Long id) throws NotFoundException {
        Topic topic = super.get(id);
        topic.setViews(topic.getViews() + 1);
        this.getDao().saveOrUpdate(topic);
        return topic;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<Topic> getRecentTopics(String page) {
        int pageSize = userService.getCurrentUser().getPageSize();
        PageRequest pageRequest = new PageRequest(page, pageSize);
        DateTime date24HoursAgo = new DateTime().minusDays(1);
        return this.getDao().getTopicsUpdatedSince(date24HoursAgo, pageRequest, userService.getCurrentUser());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<Topic> getUnansweredTopics(String page) {
        int pageSize = userService.getCurrentUser().getPageSize();
        PageRequest pageRequest = new PageRequest(page, pageSize);
        return this.getDao().getUnansweredTopics(pageRequest, userService.getCurrentUser());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<Topic> getTopics(Branch branch, String page) {
        int pageSize = userService.getCurrentUser().getPageSize();
        PageRequest pageRequest = new PageRequest(page, pageSize);
        return getDao().getTopics(branch, pageRequest);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<Topic> searchByTitleAndContent(String phrase, String page) {

        if (!StringUtils.isEmpty(phrase)) {
            JCUser currentUser = userService.getCurrentUser();
            int pageSize = currentUser.getPageSize();
            PageRequest pageRequest = new PageRequest(page, pageSize);
            // hibernate search refuses to process long string throwing error
            String normalizedPhrase = StringUtils.left(phrase, 50);
            List<Long> forbiddenBranchesIds = this.getDao().getForbiddenBranchesIds(currentUser);

            return searchDao.searchByTitleAndContent(normalizedPhrase, pageRequest, forbiddenBranchesIds);
        }
        return new PageImpl<Topic>(Collections.<Topic>emptyList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void rebuildSearchIndex() {
        searchDao.rebuildIndex();
    }
    
    /**
     * {@inheritDoc}
     */
    @PreAuthorize("hasPermission(#branchId, 'BRANCH', 'BranchPermission.VIEW_TOPICS')")
    @Override    
    public void checkViewTopicPermission(Long branchId) {
    }
}
