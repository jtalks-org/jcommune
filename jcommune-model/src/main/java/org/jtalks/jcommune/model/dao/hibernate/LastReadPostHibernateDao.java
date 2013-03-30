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

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.jtalks.common.model.dao.hibernate.AbstractHibernateChildRepository;
import org.jtalks.jcommune.model.dao.LastReadPostDao;
import org.jtalks.jcommune.model.entity.Branch;
import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.model.entity.LastReadPost;
import org.jtalks.jcommune.model.entity.Topic;

/**
 * The implementation of {@link LastReadPostDao} based on Hibernate ORM.
 * The class is responsible for loading {@link LastReadPost} objects from database,
 * save, update and delete them.
 * 
 * @author Evgeniy Naumenko
 * @author Anuar_Nurmakanov
 */
public class LastReadPostHibernateDao extends AbstractHibernateChildRepository<LastReadPost>
        implements LastReadPostDao {

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<LastReadPost> getLastReadPostsInTopic(Topic topic) {
        return (List<LastReadPost>) getSession().getNamedQuery("getLastReadPostInTopicForAllUsers")
                .setParameter("topic", topic)
                .list();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LastReadPost getLastReadPost(JCUser forWho, Topic topic) {
        return (LastReadPost) getSession().getNamedQuery("getLastReadPostInTopicForUser")
                .setParameter("topic", topic)
                .setParameter("user", forWho)
                .uniqueResult();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<LastReadPost> getLastReadPosts(JCUser forWho, List<Topic> sourceTopics) {
        if (!sourceTopics.isEmpty()) {
            return (List<LastReadPost>) getSession().getNamedQuery("getLastReadPostsInTopicsForUser")
                    .setParameterList("sourceTopics", sourceTopics)
                    .setParameter("user", forWho)
                    .list();
        }
        return Collections.emptyList();
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
                .executeUpdate();

        @SuppressWarnings("unchecked")
        List<Object[]> topicsOfBranch = session.getNamedQuery("getTopicAndCountOfPostsInBranch")
                .setParameter("branch", branch.getId())
                .list();

        SQLQuery insertQuery = (SQLQuery) session.getNamedQuery("markAllTopicsRead");
        insertQuery
                .addSynchronizedEntityClass(LastReadPost.class);

        for (Object[] o : topicsOfBranch) {
            insertQuery.setParameter("uuid", UUID.randomUUID().toString())
                    .setParameter("user", forWho.getId())
                    .setParameter("lastPostIndex", o[1])
                    .setParameter("topic", o[0])
                    .executeUpdate();
        }

        session.flush();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteLastReadPostsFor(JCUser user) {
        getSession().getNamedQuery("deleteAllLastReadPostsOfUser")
            .setParameter("user", user)
            .executeUpdate();
    }
}
