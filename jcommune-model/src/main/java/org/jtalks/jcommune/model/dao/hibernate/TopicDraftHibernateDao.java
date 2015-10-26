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
import org.jtalks.jcommune.model.dao.TopicDraftDao;
import org.jtalks.jcommune.model.entity.TopicDraft;
import org.jtalks.jcommune.model.entity.JCUser;

/**
 * Hibernate DAO implementation for {@link TopicDraft}
 *
 * @author Dmitry S. Dolzhenko
 */
public class TopicDraftHibernateDao extends GenericDao<TopicDraft> implements TopicDraftDao {

    public TopicDraftHibernateDao(SessionFactory sessionFactory) {
        super(sessionFactory, TopicDraft.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TopicDraft getForUser(JCUser user) {
        Query query = session().getNamedQuery("getForUser")
                .setEntity("user", user);
        return (TopicDraft) query.uniqueResult();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteByUser(JCUser user) {
        session().getNamedQuery("deleteByUser")
                .setEntity("user", user)
                .executeUpdate();
    }
}
