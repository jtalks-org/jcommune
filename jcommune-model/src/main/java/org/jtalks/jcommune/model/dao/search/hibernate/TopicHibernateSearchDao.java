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

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.search.FullTextQuery;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.jtalks.jcommune.model.dao.search.TopicSearchDao;
import org.jtalks.jcommune.model.dao.search.hibernate.filter.SearchRequestFilter;
import org.jtalks.jcommune.model.entity.Post;
import org.jtalks.jcommune.model.entity.Topic;
/**
 * Hibernate Search DAO implementation for operations with a {@link Topic}.
 * 
 * @author Anuar Nurmakanov
 *
 */
public class TopicHibernateSearchDao extends AbstractHibernateSearchDao
        implements TopicSearchDao {
    /**
     * The number of records by default.
     */
    public static final int DEFAULT_MAX_RECORD = 100;
    /**
     * List of filters.
     */
    private List<SearchRequestFilter> filters = Collections.emptyList();
    
    /**
     * @param sessionFactory the Hibernate SessionFactory
     * @param filters the list of filters to correct the dirty search requests
     */
    public TopicHibernateSearchDao(SessionFactory sessionFactory, List<SearchRequestFilter> filters) {
        super(sessionFactory);
        this.filters = filters;
    }

    /**
     * Injects filters for search requests. It needed for testing.
     * 
     * @param filters the list of filters to correct the dirty search requests
     */
    void setFilters(List<SearchRequestFilter> filters) {
        this.filters = filters;
    }
    
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<Topic> searchByTitleAndContent(String searchText) {
        //TODO The latest versions of the library filtering is not needed.
        String filteredSearchText = applyFilters(searchText, filters).trim();
        if (!StringUtils.isEmpty(filteredSearchText)) {
            Query query = createSearchQuery(getFullTextSession(), filteredSearchText);
            return query.list();
        } else {
            return Collections.emptyList();
        }
    }
    
    /**
     * Builds a search query.
     * 
     * @param fullTextSession the Hibernate Search session
     * @param searchText the search text
     * @return the search query
     */
    private Query createSearchQuery(FullTextSession fullTextSession,
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
    
    /**
     * This method filters the text.
     * 
     * @param searchText the search text
     * @param filters the list of filters
     * @return the filtered search text
     */
    private String applyFilters(String searchText, List<SearchRequestFilter> filters) {
        for (SearchRequestFilter filter : filters) {
            searchText = filter.filter(searchText);
        }
        return searchText;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void rebuildIndex() {
        //TODO We need a testing of performance.
        getFullTextSession().createIndexer(Topic.class).start();
    }
}
