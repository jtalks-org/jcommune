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
import org.hibernate.criterion.Order;
import org.jtalks.common.model.dao.hibernate.AbstractHibernateParentRepository;
import org.jtalks.common.model.entity.Branch;
import org.jtalks.common.model.entity.Entity;
import org.jtalks.common.model.entity.Group;
import org.jtalks.common.model.entity.Section;
import org.jtalks.jcommune.model.dao.SectionDao;
import org.jtalks.jcommune.model.entity.JCUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Hibernate DAO implementation from the {@link Section}.
 *
 * @author Max Malakhov
 */
public class SectionHibernateDao extends AbstractHibernateParentRepository<Section> implements SectionDao {


    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<Section> getAll() {
        List<Section> sectionList = getSession()
                .createCriteria(Section.class)
                .addOrder(Order.asc("position"))
                .setCacheable(true).list();
        return sectionList;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long getCountAvailableBranches(JCUser user, List<Branch> branches) {
        if(branches.isEmpty()){return 0L;}
        List<Long> branchIds =  new ArrayList(getEntityIds(new ArrayList<Entity>(branches),Long.class));
        if(!user.isAnonymous()){
            List<Group> groups = user.getGroups();
            if(groups.isEmpty()){return 0L;}
            List<String> groupIds = new ArrayList(getEntityIds(new ArrayList<Entity>(groups),String.class));

            Query query = getSession().getNamedQuery("getCountAvailableBranchesByGroupsIds");
            query.setParameterList("groupIds",groupIds);
            query.setParameterList("branchIds",branchIds);
            return (Long)query.uniqueResult();
        }
        Query query = getSession().getNamedQuery("getCountAvailableBranchesForAnonymousUser");
        query.setParameterList("branchIds",branchIds);
        return (Long)query.uniqueResult();
    }

    /**
     * Return entity id's from list entities
     * @param entities entities
     * @param type type for result list
     * @return id's entities
     */
    private List<Object> getEntityIds(List<Entity> entities, Class type){
        List<Object> ids = new ArrayList<Object>();
        for(Entity e: entities){
            if(type.equals(String.class)){
                ids.add(e.getId()+"");
            }else if(type.equals(Long.class)){
                ids.add(e.getId());
            }
        }
        return ids;
    }
}
