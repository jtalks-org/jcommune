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
package org.jtalks.jcommune.service;

import org.jtalks.jcommune.model.entity.Topic;
import org.springframework.data.domain.Page;

/**
 * This service provides full-text search topics.
 * 
 * @author Anuar Nurmakanov
 *
 */
public interface TopicFullSearchService {
    /**
     * Search by topics, title and content of which corresponds to the text of search.
     * 
     * @param phrase phrase
     * @param page TODO
     * @return list of topics
     */
    Page<Topic> searchByTitleAndContent(String phrase, int page);
    
    /**
     * Indexing topics from the database.
     * This functionality is required either when data exists in the database,
     * but the index doesn't contain this data or the index is re-created.
     */
    void rebuildIndex();
}
