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
import org.hibernate.criterion.Order;
import org.jtalks.common.model.dao.hibernate.GenericDao;
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
public class SectionHibernateDao extends GenericDao<Section> implements SectionDao {


    /**
     * @param sessionFactory The SessionFactory.
     */
    public SectionHibernateDao(SessionFactory sessionFactory) {
        super(sessionFactory, Section.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<Section> getAll() {
        List<Section> sectionList = session()
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
        if (branches.isEmpty()) {
            return 0L;
        }
        List<Long> branchIds = getEntityIdsLongs(new ArrayList<Entity>(branches));
        if (!user.isAnonymous()) {
            return countVisibleBranchesForLoggedIn(user, branchIds);
        }
        Query query = session().getNamedQuery("getCountAvailableBranchesForAnonymousUser");
        query.setParameterList("branchIds", branchIds);
        return (Long) query.uniqueResult();
    }

    /**
     * Get count visible branches, from branch ids, for logged user
     *
     * @param user      user
     * @param branchIds branch ids
     * @return count visible branches
     */
    private Long countVisibleBranchesForLoggedIn(JCUser user, List<Long> branchIds) {
        List<Group> groups = user.getGroups();
        if (groups.isEmpty()) {
            return 0L;
        }
        List<String> groupIds = new ArrayList(getEntityIdsStrings(new ArrayList<Entity>(groups)));

        Query query = session().getNamedQuery("getCountAvailableBranchesByGroupsIds");
        query.setParameterList("groupIds", groupIds);
        query.setParameterList("branchIds", branchIds);
        return (Long) query.uniqueResult();
    }

    /**
     * Return entity ids from list entities as strings
     *
     * @param entities entities
     * @return id's entities
     */
    private List<String> getEntityIdsStrings(List<Entity> entities) {
        List<String> ids = new ArrayList<String>();
        for (Entity e : entities) {
            ids.add(e.getId() + "");
        }
        return ids;
    }

    /**
     * Return entity id's from list entities as long numbers
     *
     * @param entities entities
     * @return id's entities
     */
    private List<Long> getEntityIdsLongs(List<Entity> entities) {
        List<Long> ids = new ArrayList<Long>();
        for (Entity e : entities) {
            ids.add(e.getId());
        }
        return ids;
    }
}
