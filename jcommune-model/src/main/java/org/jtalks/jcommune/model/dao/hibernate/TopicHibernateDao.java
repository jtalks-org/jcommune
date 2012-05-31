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

import org.hibernate.classic.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.joda.time.DateTime;
import org.jtalks.common.model.dao.hibernate.AbstractHibernateChildRepository;
import org.jtalks.common.model.entity.Branch;
import org.jtalks.jcommune.model.dao.TopicDao;
import org.jtalks.jcommune.model.entity.Topic;

/**
 * Hibernate DAO implementation from the {@link Topic}.
 *
 * @author Pavel Vervenko
 * @author Kirill Afonin
 * @author Vitaliy Kravchenko
 * @author Eugeny Batov
 */
public class TopicHibernateDao extends AbstractHibernateChildRepository<Topic> implements TopicDao {


    /**
     * {@inheritDoc}
     */
    @Override
    public List<Topic> getTopicsUpdatedSince(DateTime timeStamp) {
        return (List<Topic>) getSession().createQuery("FROM Topic WHERE modificationDate > :maxModDate " +
                "ORDER BY modificationDate DESC")
                .setParameter("maxModDate", timeStamp)
                .list();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public List<Topic> getUnansweredTopics() {
        return (List<Topic>) getSession().createQuery("FROM Topic t WHERE t.posts.size=1 " +
                "ORDER BY modificationDate DESC")
                .list();
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
}
