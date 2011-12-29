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

import org.joda.time.DateTime;
import org.jtalks.common.model.dao.hibernate.AbstractHibernateChildRepository;
import org.jtalks.jcommune.model.dao.TopicDao;
import org.jtalks.jcommune.model.entity.Topic;

import java.util.List;

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
    public List<Topic> getTopicsUpdatedSince(DateTime lastLogin) {
        DateTime time = lastLogin.toDateTime();
        return (List<Topic>) getSession().createQuery("FROM Topic WHERE modificationDate > :maxModDate " +
                "ORDER BY modificationDate DESC")
                .setParameter("maxModDate", time)
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

}
