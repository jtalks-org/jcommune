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
import org.hibernate.classic.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.joda.time.DateTime;
import org.jtalks.common.model.dao.hibernate.AbstractHibernateChildRepository;
import org.jtalks.common.model.entity.Branch;
import org.jtalks.jcommune.model.dao.TopicDao;
import org.jtalks.jcommune.model.dto.JCommunePageRequest;
import org.jtalks.jcommune.model.entity.Topic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * Hibernate DAO implementation from the {@link Topic}.
 *
 * @author Pavel Vervenko
 * @author Kirill Afonin
 * @author Vitaliy Kravchenko
 * @author Eugeny Batov
 * @author Anuar Nurmakanov
 */
public class TopicHibernateDao extends AbstractHibernateChildRepository<Topic> implements TopicDao {


    /**
     * {@inheritDoc}
     */
    @Override
    public Page<Topic> getTopicsUpdatedSince(DateTime timeStamp, JCommunePageRequest pageRequest) {
        Number totalCount = (Number) getSession()
                .getNamedQuery("getCountResentTopics")
                .setParameter("maxModDate", timeStamp)
                .uniqueResult();
        @SuppressWarnings("unchecked")
        List<Topic> recentTopics = (List<Topic>) getSession()
                .getNamedQuery("getResentTopics")
                .setParameter("maxModDate", timeStamp)
                .setFirstResult(pageRequest.getIndexOfFirstItem())
                .setMaxResults(pageRequest.getPageSize())
                .list();
        return new PageImpl<Topic>(recentTopics, pageRequest, totalCount.intValue());
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Page<Topic> getUnansweredTopics(JCommunePageRequest pageRequest, List<Long> branchIds) {
        PageImpl<Topic> result = new PageImpl(new ArrayList(), pageRequest, 0);
        if (!branchIds.isEmpty()) {
            Query query = getSession().getNamedQuery("getCountUnansweredTopics");
            query.setParameterList("branchIds", branchIds);
            Number totalCount = (Number) query.uniqueResult();
            query = getSession().getNamedQuery("getUnansweredTopics");
            query.setParameterList("branchIds", branchIds);
            query.setFirstResult(pageRequest.getIndexOfFirstItem()).setMaxResults(pageRequest.getPageSize());
            @SuppressWarnings("unchecked")
            List<Topic> unansweredTopics = (List<Topic>) query.list();
            result = new PageImpl<Topic>(unansweredTopics, pageRequest, totalCount.intValue());
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Topic getLastUpdatedTopicInBranch(Branch branch) {
        Session session = getSession();
        //find the last topic in the branch
        String modificationDateProperty = "modificationDate";
        DetachedCriteria topicMaxModificationDateCriteria =
                DetachedCriteria.forClass(Topic.class)
                        .setProjection(Projections.max(modificationDateProperty))
                        .add(Restrictions.eq("branch", branch));
        //possible that the two topics will be modified at the same time
        @SuppressWarnings("unchecked")
        List<Topic> topics = (List<Topic>) session
                .createCriteria(Topic.class)
                .add(Restrictions.eq("branch", branch))
                .add(Property.forName(modificationDateProperty).eq(topicMaxModificationDateCriteria))
                .list();
        return topics.isEmpty() ? null : topics.get(0);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Page<Topic> getTopics(Branch branch, JCommunePageRequest pageRequest) {
        int totalCount = countTopics(branch);
        Query query = getSession().getNamedQuery("getTopicsInBranch")
                .setParameter("branch", branch);
        if (pageRequest.isPagingEnabled()) {
            query = query.setFirstResult(pageRequest.getIndexOfFirstItem())
                    .setMaxResults(pageRequest.getPageSize());
        }
        @SuppressWarnings("unchecked")
        List<Topic> topics = (List<Topic>) query.list();
        return new PageImpl<Topic>(topics, pageRequest, totalCount);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int countTopics(Branch branch) {
        Number count = (Number) getSession()
                .getNamedQuery("getCountTopicsInBranch")
                .setParameter("branch", branch)
                .uniqueResult();
        return count.intValue();
    }
}
