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

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.impl.CriteriaImpl;
import org.hibernate.search.FullTextQuery;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.jtalks.jcommune.model.dao.search.TopicSearchDao;
import org.jtalks.jcommune.model.dto.PageRequest;
import org.jtalks.jcommune.model.entity.Branch;
import org.jtalks.jcommune.model.entity.Post;
import org.jtalks.jcommune.model.entity.Topic;
import org.jtalks.jcommune.model.search.SearchRequestFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
/**
 * Hibernate Search DAO implementation for operations with a {@link Topic}.
 * 
 * @author Anuar Nurmakanov
 *
 */
public class TopicHibernateSearchDao extends AbstractHibernateSearchDao
        implements TopicSearchDao {
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
    @Override
    public Page<Topic> searchByTitleAndContent(String searchText,
                                               PageRequest pageRequest,
                                               List<Long> allowedBranchesIds) {

       Page<Topic> searchResults = doSearch(searchText, pageRequest, allowedBranchesIds);

       if (isSearchedAboveLastPage(searchResults)) {
           pageRequest.adjustPageNumber(Long.valueOf(searchResults.getTotalElements()).intValue());
           searchResults = doSearch(searchText, pageRequest, allowedBranchesIds);
       }

       return searchResults;
    }

    /**
     * Perform actual search
     *
     * @param searchText the search text
     * @param pageRequest contains information for pagination: page number, page
     *                    size
     * @param allowedBranchesIds list of allowed branches
     *
     * @return object that contains search results for one page(note, that one
     *         page may contain all search results) and information for pagination
     */
    @SuppressWarnings("unchecked")
    private Page<Topic> doSearch(String searchText, PageRequest pageRequest, List<Long> allowedBranchesIds) {
        List<Topic> topics = Collections.emptyList();
        int resultSize = 0;
        //TODO The latest versions of the library filtering is not needed.
        String filteredSearchText = applyFilters(searchText, filters).trim();
        if (!StringUtils.isEmpty(filteredSearchText)) {

            FullTextQuery query = createSearchQuery(getFullTextSession(), filteredSearchText, pageRequest);

            Criteria criteria = getFullTextSession().createCriteria(Topic.class).add(
                    Restrictions.in("branch.id", allowedBranchesIds)
            );
            query.setCriteriaQuery(criteria);

            topics = query.list();
            resultSize = query.getResultSize();
        }

        return new PageImpl<Topic>(topics, pageRequest, resultSize);
    }

    /**
     * Checks if this search was by made with too big page number specified
     * @param searchResults search results
     * @return true if page number is too big
     */
    private boolean isSearchedAboveLastPage(Page<Topic> searchResults) {
        return !searchResults.hasContent() && searchResults.getNumber() > searchResults.getTotalPages();
    }

    /**
     * Builds a search query.
     * 
     * @param fullTextSession the Hibernate Search session
     * @param searchText the search text
     * @param pageRequest contains information for pagination: page number, page size
     * @return the search query
     */
    private FullTextQuery createSearchQuery(
            FullTextSession fullTextSession,
            String searchText,
            PageRequest pageRequest) {
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

        query.setFirstResult(pageRequest.getOffset());
        query.setMaxResults(pageRequest.getPageSize());

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
        getFullTextSession().createIndexer(Topic.class).start();
    }
}
