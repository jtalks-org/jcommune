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

import org.hibernate.SessionFactory;
import org.jtalks.common.model.dao.hibernate.GenericDao;
import org.jtalks.jcommune.model.dao.LastReadPostDao;
import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.model.entity.LastReadPost;
import org.jtalks.jcommune.model.entity.Topic;

import java.util.Collections;
import java.util.List;

/**
 * The implementation of {@link LastReadPostDao} based on Hibernate ORM.
 * The class is responsible for loading {@link LastReadPost} objects from database,
 * save, update and delete them.
 *
 * @author Evgeniy Naumenko
 * @author Anuar_Nurmakanov
 */
public class LastReadPostHibernateDao extends GenericDao<LastReadPost>
        implements LastReadPostDao {

    /**
     * @param sessionFactory The SessionFactory.
     */
    public LastReadPostHibernateDao(SessionFactory sessionFactory) {
        super(sessionFactory, LastReadPost.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<LastReadPost> getLastReadPostsInTopic(Topic topic) {
        return (List<LastReadPost>) session().getNamedQuery("getLastReadPostInTopicForAllUsers")
                .setParameter("topic", topic)
                .list();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LastReadPost getLastReadPost(JCUser forWho, Topic topic) {
        return (LastReadPost) session().getNamedQuery("getLastReadPostInTopicForUser")
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
            return (List<LastReadPost>) session().getNamedQuery("getLastReadPostsInTopicsForUser")
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
    public void deleteLastReadPostsFor(JCUser user) {
        session().getNamedQuery("deleteAllLastReadPostsOfUser")
                .setParameter("user", user)
                .executeUpdate();
    }
}
