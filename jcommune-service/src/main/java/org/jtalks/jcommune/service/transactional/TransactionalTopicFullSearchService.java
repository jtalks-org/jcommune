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
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.jtalks.jcommune.model.dao.search.TopicSearchDao;
import org.jtalks.jcommune.model.entity.Topic;
import org.jtalks.jcommune.service.TopicFullSearchService;

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
    
    /**
     * @param topicSearchDao for full-text search operations
     */
    public TransactionalTopicFullSearchService(TopicSearchDao topicSearchDao) {
        this.topicSearchDao = topicSearchDao;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<Topic> searchByTitleAndContent(String phrase) {
        if (!StringUtils.isEmpty(phrase)) {
            // hibernate search refuses to process long string throwing error
            String normalizedPhrase = StringUtils.left(phrase, 50);
            return topicSearchDao.searchByTitleAndContent(normalizedPhrase);
        } else {
            return Collections.emptyList();
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void rebuildIndex() {
        topicSearchDao.rebuildIndex();
    }
}
