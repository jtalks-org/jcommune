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
import org.jtalks.common.model.dao.hibernate.AbstractHibernateParentRepository;
import org.jtalks.common.model.entity.Group;
import org.jtalks.jcommune.model.dao.ViewTopicsBranchesDao;
import org.jtalks.jcommune.model.entity.ViewTopicsBranches;

import java.util.ArrayList;
import java.util.List;

public class ViewTopicsBranchesHibernateDao extends AbstractHibernateParentRepository<ViewTopicsBranches>
        implements ViewTopicsBranchesDao {
    /**
     * {@inheritDoc}
     */
    @Override
    public List<ViewTopicsBranches> getViewTopicsBranchesByGroups(List<Group> groups) {
        Query query =  getSession().getNamedQuery("findViewTopicsBranchesByGroups");
        ArrayList<String> groupIds = new ArrayList<String>();
        for (Group group : groups) {
            groupIds.add(group.getId()+"");
        }
        query.setParameterList("groupIds",groupIds);
        return query.list();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ViewTopicsBranches> getViewTopicsBranchesForAnonymous() {
        Query query =  getSession().getNamedQuery("findViewTopicsBranchesForAnonymous");
        return query.list();
    }
}
