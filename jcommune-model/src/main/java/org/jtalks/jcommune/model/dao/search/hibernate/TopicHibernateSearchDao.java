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
package org.jtalks.jcommune.model.dao.search.hibernate;

import org.hibernate.Query;
import org.hibernate.search.FullTextQuery;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.jtalks.jcommune.model.dao.search.TopicSearchDao;
import org.jtalks.jcommune.model.entity.Post;
import org.jtalks.jcommune.model.entity.Topic;
/**
 * Hibernate Search DAO implementation for operations with a {@link Topic}.
 * 
 * @author Anuar Nurmakanov
 *
 */
public class TopicHibernateSearchDao extends AbstractHibernateSearchDao<Topic> implements TopicSearchDao {
    /**
     * The number of records by default
     */
    public static final int DEFAULT_MAX_RECORD = 100;
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected Query createSearchQuery(FullTextSession fullTextSession,
            String searchText) {
        QueryBuilder queryBuilder = fullTextSession.
                getSearchFactory().
                buildQueryBuilder().
                forEntity(Topic.class).
                get();
        org.apache.lucene.search.Query luceneQuery = queryBuilder.
                keyword().
                onField(Topic.TOPIC_TITLE_FIELD_DEF).
                andField(Topic.TOPIC_TITLE_FIELD_RU).
                andField(Topic.TOPIC_POSTS_PREFIX + Post.POST_CONTENT_FIELD_DEF).
                andField(Topic.TOPIC_POSTS_PREFIX + Post.POST_CONTENT_FIELD_RU).
                matching(searchText).
                createQuery();
        FullTextQuery query = fullTextSession.createFullTextQuery(luceneQuery);
        query.setMaxResults(DEFAULT_MAX_RECORD);
        return query;
    }
}
