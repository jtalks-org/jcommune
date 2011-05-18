/**
 * Copyright (C) 2011  jtalks.org Team
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
 * Also add information on how to contact you by electronic and paper mail.
 */

package org.jtalks.jcommune.model.dao.hibernate;

import org.hibernate.Query;
import org.jtalks.jcommune.model.dao.TopicBranchDao;
import org.jtalks.jcommune.model.entity.TopicBranch;

import java.util.List;

public class TopicBranchHibernateDao extends AbstractHibernateDao<TopicBranch> implements TopicBranchDao {
    @Override
    public void saveOrUpdate(TopicBranch persistent) {
        getSession().save(persistent);
    }

    @Override
    public void delete(Long topicsBranchId) {
        Query query = getSession().createQuery("delete TopicBranch where id= :topicsBranchId");
        query.setLong("topicsBranchId", topicsBranchId);
        query.executeUpdate();
    }

    @Override
    public TopicBranch get(Long id) {
        return (TopicBranch) getSession().get(TopicBranch.class, id);
    }

    @Override
    public List<TopicBranch> getAll() {
        return getSession().createQuery("from TopicBranch").list();
    }
}