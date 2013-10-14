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
package org.jtalks.jcommune.model.dao.search;

import org.jtalks.jcommune.model.dto.PageRequest;
import org.jtalks.jcommune.model.entity.Branch;
import org.jtalks.jcommune.model.entity.Topic;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * This interface describes the contract of the DAO for full-text search of topics.
 * 
 * @author Anuar Nurmakanov
 * @see org.jtalks.jcommune.model.dao.search.hibernate.TopicHibernateSearchDao
 * @see org.jtalks.jcommune.model.entity.Topic
 */
public interface TopicSearchDao {

    /**
     * Performs the full-text search by the topic title and
     * his content(the list of posts).
     * 
     * @param searchText the search text
     * @param pageRequest contains information for pagination: page number, page size
     * @param allowedBranchesIds list of allowed branches id
     * @return object that contains search results for one page(note, that one page
     *         may contain all search results) and information for pagination
     */
    Page<Topic> searchByTitleAndContent(String searchText, PageRequest pageRequest, List<Long> allowedBranchesIds);

    /**
     * Indexes the data from the database.
     * This functionality is required either when data exists in the database,
     * but the index doesn't contain this data or the index is re-created.
     */
    void rebuildIndex();
}
