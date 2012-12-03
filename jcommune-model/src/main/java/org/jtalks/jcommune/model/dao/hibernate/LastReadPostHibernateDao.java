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

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.jtalks.common.model.dao.hibernate.AbstractHibernateChildRepository;
import org.jtalks.jcommune.model.dao.LastReadPostDao;
import org.jtalks.jcommune.model.entity.Branch;
import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.model.entity.LastReadPost;
import org.jtalks.jcommune.model.entity.Topic;

import java.util.List;
import java.util.UUID;

/**
 * @author Evgeniy Naumenko
 */
public class LastReadPostHibernateDao extends AbstractHibernateChildRepository<LastReadPost>
        implements LastReadPostDao {

    /**
     * {@inheritDoc}
     */
    @Override
    public List<LastReadPost> listLastReadPostsForTopic(Topic topic) {
        return (List<LastReadPost>) getSession().createQuery("FROM LastReadPost p where p.topic=:topic")
                .setParameter("topic", topic)
                .list();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LastReadPost getLastReadPost(JCUser forWho, Topic topic) {
        return (LastReadPost) getSession().createQuery("FROM LastReadPost p WHERE p.topic = ? and p.user = ?")
                .setParameter(0, topic)
                .setParameter(1, forWho)
                .setCacheable(false)
                .uniqueResult();

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void markAllRead(JCUser forWho, Branch branch) {
        Session session = getSession();

        SQLQuery deletedEntities = (SQLQuery) session.getNamedQuery("deleteAllMarksReadToUser");
        deletedEntities
                .addSynchronizedEntityClass(LastReadPost.class)
                .setParameter("user", forWho.getId())
                .setParameter("branch", branch.getId())
                .setCacheable(false)
                .executeUpdate();

        List<Object[]> topicsOfBranch = session.getNamedQuery("getTopicAndCountOfPostsInBranch")
                .setParameter("branch", branch.getId())
                .setCacheable(false)
                .list();

        SQLQuery insertQuery = (SQLQuery) session.getNamedQuery("markAllTopicsRead");
        insertQuery
                .addSynchronizedEntityClass(LastReadPost.class)
                .setCacheable(false);

        for (Object[] o : topicsOfBranch) {
            insertQuery.setParameter("uuid", UUID.randomUUID().toString())
                    .setParameter("user", forWho.getId())
                    .setParameter("lrpi", o[1])
                    .setParameter("topic", o[0])
                    .executeUpdate();
        }

        session.flush();
    }
}
