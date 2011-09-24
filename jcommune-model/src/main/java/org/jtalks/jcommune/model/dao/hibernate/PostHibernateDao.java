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
package org.jtalks.jcommune.model.dao.hibernate;

import org.jtalks.jcommune.model.dao.PostDao;
import org.jtalks.jcommune.model.entity.Post;

import java.util.List;

/**
 * The implementation of PostDao based on Hibernate.
 * The class is responsible for loading {@link Post} objects from database,
 * save, update and delete them.
 *
 * @author Pavel Vervenko
 * @author Kirill Afonin
 */
public class PostHibernateDao extends AbstractHibernateChildRepository<Post> implements PostDao {

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<Post> getPostRangeInTopic(long topicId, int start, int max) {
        return getSession().getNamedQuery("getAllPostsInTopic")
                .setCacheable(true)
                .setLong("topicId", topicId)
                .setFirstResult(start)
                .setMaxResults(max)
                .list();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getPostsInTopicCount(long topicId) {
        return ((Number) getSession().createQuery("select count(*) from Post p where p.topic = ?")
                .setCacheable(true).setLong(0, topicId).uniqueResult()).intValue();
    }
}
