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

import java.util.List;

import org.hibernate.Query;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.jtalks.jcommune.model.dao.search.PostSearchDao;
import org.jtalks.jcommune.model.entity.Post;
/**
 * Hibernate Search DAO implementation for operations with a {@link Post}.
 * 
 * @author Anuar Nurmakanov
 *
 */
public class PostHibernateSearchDao extends AbstractHibernateSearchDao<Post> implements PostSearchDao {
	/**
	 * The number of records by default.
	 */
	public static final int DEFAULT_MAX_RECORD = 100;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Post> searchPosts(String searchText) {
		FullTextSession fullTextSession = getFullTextSession();
		Query query = createSearchPostsQuery(fullTextSession, searchText);
		@SuppressWarnings("unchecked")
		List<Post> posts = query.list();
		return posts;
	}

	private Query createSearchPostsQuery(FullTextSession fullTextSession, String searchText) {
		QueryBuilder queryBuilder = fullTextSession.
				getSearchFactory().
				buildQueryBuilder().
				forEntity(Post.class).
				get();
		org.apache.lucene.search.Query luceneQuery = queryBuilder.keyword().
				onField(Post.POST_CONTENT_FIELD_RU).
				andField(Post.POST_CONTENT_FIELD_DEF).
				matching(searchText).
				createQuery(); 
		Query query = fullTextSession.createFullTextQuery(luceneQuery);
		query.setMaxResults(DEFAULT_MAX_RECORD);
		return query;
	}
}
