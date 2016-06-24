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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.jtalks.common.model.dao.hibernate.GenericDao;
import org.jtalks.common.model.entity.Group;
import org.jtalks.common.model.entity.User;
import org.jtalks.jcommune.model.dao.GroupDao;
import org.jtalks.jcommune.model.dao.utils.SqlLikeEscaper;
import org.jtalks.jcommune.model.dto.GroupAdministrationDto;
import ru.javatalks.utils.general.Assert;

import java.util.List;

/**
 * Hibernate implementation of {@link GroupDao}
 *
 * @author Vitaliy Kravchenko
 * @author Pavel Vervenko
 * @author Leonid Kazancev
 */
public class GroupHibernateDao extends GenericDao<Group> implements GroupDao {
    private static final String FIND_GROUP_BY_NAME = "findGroupByName", FIND_ALL_GROUPS = "findAllGroups";
    private static final String FIND_EXACTLY_BY_NAME = "findGroupExactlyByName";

    /**
     * @param sessionFactory The SessionFactory.
     */
    public GroupHibernateDao(SessionFactory sessionFactory) {
        super(sessionFactory, Group.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<Group> getAll() {
        return session().getNamedQuery("findAllGroups").list();
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<Group> getMatchedByName(String name) {
        Validate.notNull(name, "User Group name can't be null");
        if (StringUtils.isBlank(name)) {
            return this.getAll();
        }
        Query query = session().getNamedQuery("findGroupByName");
        query.setString("name", SqlLikeEscaper.escapeControlCharacters(name));
        return query.list();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Group getGroupByName(String name) {
        Validate.notNull(name, "User Group name can't be null");
        Query query = session().getNamedQuery("findGroupExactlyByName");
        // we should use lower case to search ignoring case
        query.setString("name", name);
        return (Group) query.uniqueResult();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(Group group) {
        session().update(group);

        group.getUsers().clear();
        saveOrUpdate(group);
        super.delete(group);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Group> getGroupsOfUser(User user) {
        Assert.throwIfNull(user, "user");

        Query query = session().getNamedQuery("findGroupsOfUser");
        query.setParameter(0, "%" + user + "%");

        return query.list();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Group> getGroupsByIds(List<Long> ids) {
        return (List<Group>)session().getNamedQuery("getGroupsByIds")
                .setParameterList("ids", ids).list();
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<Group> getByNameContains(String name) {
        Validate.notNull(name, "User Group name can't be null");
        if (org.apache.commons.lang3.StringUtils.isBlank(name)) {
            return this.getAll();
        }
        Query query = session().getNamedQuery(FIND_GROUP_BY_NAME);
        query.setString("name", SqlLikeEscaper.escapeControlCharacters(name));
        return query.list();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Group> getByName(String name) {
        Validate.notNull(name, "User Group name can't be null");
        Query query = session().getNamedQuery(FIND_EXACTLY_BY_NAME);
        // we should use lower case to search ignoring case
        query.setString("name", name);
        return query.list();
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<GroupAdministrationDto> getGroupNamesWithCountOfUsers() {
        return (List<GroupAdministrationDto>) session().getNamedQuery("selectGroupsWithUserCount").list();
    }
}
