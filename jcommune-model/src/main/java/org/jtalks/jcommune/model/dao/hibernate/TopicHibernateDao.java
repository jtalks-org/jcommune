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
import org.jtalks.jcommune.model.dao.TopicDao;
import org.jtalks.jcommune.model.entity.Topic;

import java.util.List;

/**
 * Hibernate DAO implementation from the {@link Topic}.
 *
 * @author Pavel Vervenko
 * @author Kirill Afonin
 * @author Vitaliy Kravchenko
 */
public class TopicHibernateDao extends AbstractHibernateChildRepository<Topic> implements TopicDao {

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<Topic> getTopicRangeInBranch(Long branchId, int start, int max) {
        return getSession().getNamedQuery("getAllTopicsInBranch")
                .setCacheable(true)
                .setLong("branchId", branchId)
                .setFirstResult(start)
                .setMaxResults(max)
                .list();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getTopicsInBranchCount(long branchId) {
        return ((Number) getSession().createQuery("select count(*) from Topic t where t.branch = ?")
                .setCacheable(true).setLong(0, branchId)
                .uniqueResult())
                .intValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getTopicsPastLastDayCount(DateTime lastLogin) {
        DateTime time = lastLogin.toDateTime();
        Number big = (Number) getSession()
                .createQuery("select count(*) FROM Topic WHERE modificationDate > :maxModDate")
                .setParameter("maxModDate", time)
                .uniqueResult();
        return big.intValue();

    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<Topic> getAllTopicsPastLastDay(int start, int max, DateTime lastLogin) {
        DateTime time = lastLogin.toDateTime();
        return getSession().createQuery("FROM Topic WHERE modificationDate > :maxModDate")
                .setParameter("maxModDate", time)
                .setFirstResult(start)
                .setMaxResults(max)
                .list();
    }
}
