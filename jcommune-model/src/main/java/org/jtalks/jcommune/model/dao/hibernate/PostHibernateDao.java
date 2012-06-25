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

import java.util.List;

import org.hibernate.Query;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.jtalks.common.model.dao.hibernate.AbstractHibernateChildRepository;
import org.jtalks.jcommune.model.dao.PostDao;
import org.jtalks.jcommune.model.dto.JcommunePageable;
import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.model.entity.Post;
import org.jtalks.jcommune.model.entity.Topic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

/**
 * The implementation of PostDao based on Hibernate.
 * The class is responsible for loading {@link Post} objects from database,
 * save, update and delete them.
 *
 * @author Pavel Vervenko
 * @author Kirill Afonin
 * @author Anuar Nurmakanov
 */
public class PostHibernateDao extends AbstractHibernateChildRepository<Post> implements PostDao {
    private static final String TOPIC_PARAMETER_NAME = "topic";

    /**
     * {@inheritDoc}
     */
    public Page<Post> getUserPosts(JCUser author, JcommunePageable pageRequest, boolean pagingEnabled) {
        Number totalCount = (Number) getSession()
                .getNamedQuery("getCountPostsOfUser")
                .setParameter("userCreated", author)
                .uniqueResult();
        Query query = getSession()
                .getNamedQuery("getPostsOfUser")
                .setParameter("userCreated", author);
        if (pagingEnabled) {
            query.setFirstResult(pageRequest.getIndexOfFirstItem());
            query.setMaxResults(pageRequest.getPageSize());
        }
        @SuppressWarnings("unchecked")
        List<Post> posts = (List<Post>) query.list();
        return new PageImpl<Post>(posts, pageRequest, totalCount.intValue());
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Post getLastPostInTopic(Topic topic) {
        String creationDateProperty = "creationDate";
        DetachedCriteria postMaxCreationDateCriteria = 
                DetachedCriteria.forClass(Post.class)
                .setProjection(Projections.max(creationDateProperty))
                .add(Restrictions.eq("topic", topic));
        //Uses the list because it's possible that two posts will have the same creation date
        @SuppressWarnings("unchecked")
        List<Post> posts = (List<Post>) getSession().createCriteria(Post.class)
                .add(Restrictions.eq("topic", topic))
                .add(Property.forName(creationDateProperty).eq(postMaxCreationDateCriteria))
                .list();
        //if the result contains more than one message, then we return the first
        return posts.isEmpty()? null: posts.get(0);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<Post> getPosts(Topic topic, JcommunePageable pageRequest, boolean pagingEnabled) {
        Number totalCount = (Number) getSession()
                .getNamedQuery("getCountPostsInTopic")
                .setParameter(TOPIC_PARAMETER_NAME, topic)
                .uniqueResult();
        Query query = getSession()
                .getNamedQuery("getPostsInTopic")
                .setParameter(TOPIC_PARAMETER_NAME, topic);
        if (pagingEnabled) {
            query.setFirstResult(pageRequest.getIndexOfFirstItem());
            query.setMaxResults(pageRequest.getPageSize());
        }
        //TODO LIST_INDEX?
        @SuppressWarnings("unchecked")
        List<Post> posts = (List<Post> ) query.list();
        return new PageImpl<Post>(posts, pageRequest, totalCount.intValue());
    }
}
