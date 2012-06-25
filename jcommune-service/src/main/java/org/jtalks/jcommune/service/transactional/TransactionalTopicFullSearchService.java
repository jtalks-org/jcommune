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

import java.util.Collections;

import org.apache.commons.lang.StringUtils;
import org.jtalks.jcommune.model.dao.search.TopicSearchDao;
import org.jtalks.jcommune.model.dto.JcommunePageRequest;
import org.jtalks.jcommune.model.dto.JcommunePageable;
import org.jtalks.jcommune.model.entity.Topic;
import org.jtalks.jcommune.service.TopicFullSearchService;
import org.jtalks.jcommune.service.nontransactional.PaginationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

/**
 * The implementation of TopicFullSearchService, that provides possibility
 * to search for topics. Searches for data in the index that have been indexed
 * using the Hibernate Search functionality. Hibernate Search keeps track of all
 * the data that have been saved or updated by using Hibernate. The data that 
 * have been saved or updated without Hibernate will not be indexed. 
 * 
 * @author Anuar Nurmakanov
 *
 */
public class TransactionalTopicFullSearchService implements TopicFullSearchService {
    private TopicSearchDao topicSearchDao;
    private PaginationService paginationService;
    
    /**
     * Constructs an instance with required fields.
     * 
     * @param topicSearchDao for full-text search operations
     * @param paginationService this service provides functionality,
     *        that is needed for pagination
     */
    public TransactionalTopicFullSearchService(
            TopicSearchDao topicSearchDao,
            PaginationService paginationService) {
        this.topicSearchDao = topicSearchDao;
        this.paginationService = paginationService;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Page<Topic> searchByTitleAndContent(String phrase, int page) {
        if (!StringUtils.isEmpty(phrase)) {
            int pageSize = paginationService.getPageSizeForCurrentUser();
            JcommunePageable pageRequest = new JcommunePageRequest(page, pageSize);
            // hibernate search refuses to process long string throwing error
            String normalizedPhrase = StringUtils.left(phrase, 50);
            return topicSearchDao.searchByTitleAndContent(normalizedPhrase, pageRequest);
        } 
        return new PageImpl<Topic>(Collections.<Topic> emptyList());
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void rebuildIndex() {
        topicSearchDao.rebuildIndex();
    }
}
