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

import org.jtalks.common.model.dao.hibernate.AbstractHibernateChildRepository;
import org.jtalks.jcommune.model.dao.LastReadPostDao;
import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.model.entity.LastReadPost;
import org.jtalks.jcommune.model.entity.Topic;

import java.util.List;

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
}
