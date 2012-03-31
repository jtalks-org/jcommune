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
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.jtalks.jcommune.model.dao.search.SearchDao;
import org.jtalks.jcommune.model.dao.search.hibernate.filter.SearchRequestFilter;
import org.jtalks.jcommune.model.entity.IndexedEntity;

/**
 * The base class for full-text search.
 * The implementation is based on the Hibernate Search.
 * 
 * @author Anuar Nurmakanov
 * 
 * @param <E> indexed entity
 */
public abstract class AbstractHibernateSearchDao<E extends IndexedEntity>
        implements SearchDao<E> {
    /**
     * Hibernate SessionFactory
     */
    private SessionFactory sessionFactory;
    
    /**
     * List of filters
     */
    private List<SearchRequestFilter> filters = Collections.emptyList();
 
    /**
     * Injects the Hibernate SessionFactory.
     *
     * @param sessionFactory the sessionFactory to set
     */
    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
    
    /**
     * Injects filters for search requests.
     * 
     * @param filters the list of filters to correct the dirty search requests
     */
    public void setFilters(List<SearchRequestFilter> filters) {
        this.filters = filters;
    }
    
    /**
     * Gets the Hibernate Search session.
     * 
     * @return the full text session
     */
    protected FullTextSession getFullTextSession() {
        Session session = sessionFactory.getCurrentSession();
        return Search.getFullTextSession(session);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void rebuildIndex(Class<E> entityClass) {
        getFullTextSession().createIndexer(entityClass).start();
    }
    
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<E> search(String searchText) {
        //TODO The latest versions of the library filtering is not needed.
        String filteredSearchText = applyFilters(searchText, filters).trim();
        if (!StringUtils.isEmpty(filteredSearchText)) {
            FullTextSession fullTextSession = getFullTextSession();
            Query query = createSearchQuery(fullTextSession, filteredSearchText);
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
    protected abstract Query createSearchQuery(FullTextSession fullTextSession, String searchText);

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
}
