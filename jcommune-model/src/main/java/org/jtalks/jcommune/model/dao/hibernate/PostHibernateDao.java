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

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.jtalks.common.model.dao.hibernate.GenericDao;
import org.jtalks.jcommune.model.dao.PostDao;
import org.jtalks.jcommune.model.dto.PageRequest;
import org.jtalks.jcommune.model.entity.Branch;
import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.model.entity.Post;
import org.jtalks.jcommune.model.entity.Topic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.List;

/**
 * The implementation of PostDao based on Hibernate.
 * The class is responsible for loading {@link Post} objects from database,
 * save, update and delete them.
 *
 * @author Pavel Vervenko
 * @author Kirill Afonin
 * @author Anuar Nurmakanov
 */
public class PostHibernateDao extends GenericDao<Post> implements PostDao {
    private static final String TOPIC_PARAMETER_NAME = "topic";

    /**
     * @param sessionFactory The SessionFactory.
     */
    public PostHibernateDao(SessionFactory sessionFactory) {
        super(sessionFactory, Post.class);
    }

    /**
     * {@inheritDoc}
     */
    public Page<Post> getUserPosts(JCUser author, PageRequest pageRequest, List<Long> allowedBranchesIds) {
        Number totalCount = (Number) session()
                .getNamedQuery("getCountPostsOfUser")
                .setParameter("userCreated", author)
                .setParameterList("allowedBranchesIds", allowedBranchesIds)
                .uniqueResult();
        Query query = session()
                .getNamedQuery("getPostsOfUser")
                .setParameter("userCreated", author)
                .setParameterList("allowedBranchesIds", allowedBranchesIds);
        pageRequest.adjustPageNumber(totalCount.intValue());
        query.setFirstResult(pageRequest.getOffset());
        query.setMaxResults(pageRequest.getPageSize());
        @SuppressWarnings("unchecked")
        List<Post> posts = (List<Post>) query.list();
        return new PageImpl<Post>(posts, pageRequest, totalCount.intValue());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<Post> getPosts(Topic topic, PageRequest pageRequest) {
        Number totalCount = (Number) session()
                .getNamedQuery("getCountPostsInTopic")
                .setParameter(TOPIC_PARAMETER_NAME, topic)
                .uniqueResult();
        Query query = session()
                .getNamedQuery("getPostsInTopic")
                .setParameter(TOPIC_PARAMETER_NAME, topic);
        pageRequest.adjustPageNumber(totalCount.intValue());
        query.setFirstResult(pageRequest.getOffset());
        query.setMaxResults(pageRequest.getPageSize());
        @SuppressWarnings("unchecked")
        List<Post> posts = (List<Post>) query.list();
        return new PageImpl<Post>(posts, pageRequest, totalCount.intValue());
    }

    /**
     * Get last post that was posted in a topic of branch.
     * Uses hibernate criteria instead of invoking {@link #getLastPostsFor} method that uses hql query.
     * Done for better performance results. This solution can return only one last post so method
     * {@link #getLastPostsFor} stay without changes.
     *
     * @param branch in this branch post was posted
     * @return last post that was posted in a topic of branch
     */
    @Override
    public Post getLastPostFor(Branch branch) {
        List<Post> posts = (List<Post>) session()
                .getNamedQuery("getLastPostForBranch")
                .setParameter("branchId", branch.getId())
                .setMaxResults(1)
                .list();
        if (!posts.isEmpty()) {
            return posts.get(0);
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Post> getLastPostsFor(List<Long> branchIds, int postCount) {
        List<Post> result = (List<Post>) session()
                .getNamedQuery("getLastPostsForBranch")
                .setParameterList("branchIds", branchIds)
                .setMaxResults(postCount).list();
        return result;
    }
}
