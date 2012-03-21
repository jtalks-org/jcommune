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
import org.jtalks.jcommune.model.dao.search.PostSearchDao;
import org.jtalks.jcommune.model.entity.Post;
import org.jtalks.jcommune.service.PostSearchService;

/**
 * The implementation of PostSearchService.
 * 
 * @author Anuar Nurmakanov
 *
 */
public class TransactionalPostSearchService implements PostSearchService {
	private PostSearchDao postSearchDao;
	
	/**
	 * @param postSearchDao for full-text search operations
	 */
	public TransactionalPostSearchService(PostSearchDao postSearchDao) {
		this.postSearchDao = postSearchDao;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Post> searchPostsByPhrase(String phrase) {
		if (!StringUtils.isEmpty(phrase) && !StringUtils.isEmpty(phrase.trim())) {
			List<Post> posts = postSearchDao.searchPosts(phrase);
			return posts;
		} else {
			return Collections.emptyList();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void rebuildIndex() {
		postSearchDao.rebuildIndex(Post.class);
	}
}
