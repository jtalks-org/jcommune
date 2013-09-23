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
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.joda.time.DateTime;
import org.jtalks.common.model.dao.hibernate.GenericDao;
import org.jtalks.common.model.entity.Branch;
import org.jtalks.common.model.entity.Group;
import org.jtalks.jcommune.model.dao.TopicDao;
import org.jtalks.jcommune.model.dto.PageRequest;
import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.model.entity.SubscriptionAwareEntity;
import org.jtalks.jcommune.model.entity.Topic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.*;

/**
 * Hibernate DAO implementation from the {@link Topic}.
 *
 * @author Pavel Vervenko
 * @author Kirill Afonin
 * @author Vitaliy Kravchenko
 * @author Eugeny Batov
 * @author Anuar Nurmakanov
 */
public class TopicHibernateDao extends GenericDao<Topic> implements TopicDao {
    private static final String BRANCH = "branch";
    private static final String MAX_MOD_DATE = "maxModDate";
    private static final String GROUP_IDS = "groupIds";
    private static final String UNCHECKED = "unchecked";

    /**
     * @param sessionFactory The SessionFactory.
     */
    public TopicHibernateDao(SessionFactory sessionFactory) {
        super(sessionFactory, Topic.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<Topic> getTopicsUpdatedSince(DateTime timeStamp, PageRequest pageRequest, JCUser user) {
        if (!user.isAnonymous()) {
            return getRecentTopicsByGroupIds(getGroupIds(user), timeStamp, pageRequest);
        }
        return getRecentTopicsForAnonymousUser(timeStamp, pageRequest);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Page<Topic> getUnansweredTopics(PageRequest pageRequest, JCUser user) {
        if (!user.isAnonymous()) {
            return getUnansweredTopicsByGroupIds(getGroupIds(user), pageRequest);
        }
        return getUnansweredTopicsForAnonymousUser(pageRequest);
    }

    /**
     * Return group ids for select branches with VIEW_TOPICS permission
     *
     * @param user current user
     * @return group ids
     */
    private List<String> getGroupIds(JCUser user) {
        List<Group> groups = user.getGroups();
        List<String> groupIds = new ArrayList<String>();
        for (Group g : groups) {
            groupIds.add(g.getId() + "");
        }
        return groupIds;
    }

    /**
     * Return unanswered topics with VIEW_TOPICS permission by group ids
     *
     * @param groupIds    group ids
     * @param pageRequest ontains information for pagination: page number, page size
     * @return unanswered topics
     */
    private PageImpl<Topic> getUnansweredTopicsByGroupIds(List<String> groupIds, PageRequest pageRequest) {
        if (!groupIds.isEmpty()) {
            Query query = session().getNamedQuery("getCountUnansweredTopicsByGroups");
            query.setParameterList(GROUP_IDS, groupIds);
            Number totalCount = (Number) query.uniqueResult();
            pageRequest.adjustPageNumber(totalCount.intValue());

            query = session().getNamedQuery("getUnansweredTopicsByGroups");
            query.setParameterList(GROUP_IDS, groupIds);
            query.setFirstResult(pageRequest.getOffset()).setMaxResults(pageRequest.getPageSize());
            @SuppressWarnings(UNCHECKED)
            List<Topic> unansweredTopics = (List<Topic>) query.list();
            return new PageImpl<Topic>(unansweredTopics, pageRequest, totalCount.intValue());
        }
        return new PageImpl<Topic>(new ArrayList<Topic>(), pageRequest, 0);
    }

    /**
     * Return unanswered topics with VIEW_TOPICS permission for anonymous user
     *
     * @param pageRequest ontains information for pagination: page number, page size
     * @return unanswered topics
     */
    private PageImpl<Topic> getUnansweredTopicsForAnonymousUser(PageRequest pageRequest) {
        Query query = session().getNamedQuery("getCountUnansweredTopicsForAnonymousUser");
        Number totalCount = (Number) query.uniqueResult();
        pageRequest.adjustPageNumber(totalCount.intValue());

        query = session().getNamedQuery("getUnansweredTopicsForAnonymousUser");
        query.setFirstResult(pageRequest.getOffset()).setMaxResults(pageRequest.getPageSize());
        @SuppressWarnings(UNCHECKED)
        List<Topic> unansweredTopics = (List<Topic>) query.list();
        return new PageImpl<Topic>(unansweredTopics, pageRequest, totalCount.intValue());
    }

    /**
     * Return recent topics with VIEW_TOPICS permission by group ids
     *
     * @param groupIds    group ids
     * @param timeStamp   user's last login date and time
     * @param pageRequest ontains information for pagination: page number, page size
     * @return recent topics
     */
    private PageImpl<Topic> getRecentTopicsByGroupIds(List<String> groupIds, DateTime timeStamp,
                                                      PageRequest pageRequest) {
        if (!groupIds.isEmpty()) {
            Query query = session().getNamedQuery("getCountRecentTopicsByGroups");
            query.setParameter(MAX_MOD_DATE, timeStamp);
            query.setParameterList(GROUP_IDS, groupIds);
            Number totalCount = (Number) query.uniqueResult();
            pageRequest.adjustPageNumber(totalCount.intValue());

            query = session().getNamedQuery("getRecentTopicsByGroups");
            query.setParameter(MAX_MOD_DATE, timeStamp);
            query.setParameterList(GROUP_IDS, groupIds);
            query.setFirstResult(pageRequest.getOffset()).setMaxResults(pageRequest.getPageSize());
            @SuppressWarnings(UNCHECKED)
            List<Topic> recentTopics = (List<Topic>) query.list();
            return new PageImpl<Topic>(recentTopics, pageRequest, totalCount.intValue());
        }
        return new PageImpl<Topic>(new ArrayList<Topic>(), pageRequest, 0);
    }

    /**
     * Return recent topics with VIEW_TOPICS permission for anonymous user
     *
     * @param timeStamp   user's last login date and time
     * @param pageRequest ontains information for pagination: page number, page size
     * @return recent topics
     */
    private PageImpl<Topic> getRecentTopicsForAnonymousUser(DateTime timeStamp, PageRequest pageRequest) {
        Query query = session().getNamedQuery("getCountRecentTopicsForAnonymousUser");
        query.setParameter(MAX_MOD_DATE, timeStamp);
        Number totalCount = (Number) query.uniqueResult();
        pageRequest.adjustPageNumber(totalCount.intValue());

        query = session().getNamedQuery("getRecentTopicsForAnonymousUser");
        query.setParameter(MAX_MOD_DATE, timeStamp);
        query.setFirstResult(pageRequest.getOffset()).setMaxResults(pageRequest.getPageSize());
        @SuppressWarnings(UNCHECKED)
        List<Topic> recentTopics = (List<Topic>) query.list();
        return new PageImpl<Topic>(recentTopics, pageRequest, totalCount.intValue());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Topic getLastUpdatedTopicInBranch(Branch branch) {
        //find the last topic in the branch
        String modificationDateProperty = "modificationDate";
        DetachedCriteria topicMaxModificationDateCriteria =
                DetachedCriteria.forClass(Topic.class)
                        .setProjection(Projections.max(modificationDateProperty))
                        .add(Restrictions.eq(BRANCH, branch));
        //possible that the two topics will be modified at the same time
        @SuppressWarnings(UNCHECKED)
        List<Topic> topics = (List<Topic>) session()
                .createCriteria(Topic.class)
                .add(Restrictions.eq(BRANCH, branch))
                .add(Property.forName(modificationDateProperty).eq(topicMaxModificationDateCriteria))
                .list();
        return topics.isEmpty() ? null : topics.get(0);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Page<Topic> getTopics(Branch branch, PageRequest pageRequest) {
        int totalCount = countTopics(branch);
        Query query = session().getNamedQuery("getTopicsInBranch")
                .setParameter(BRANCH, branch);
        pageRequest.adjustPageNumber(totalCount);
        query = query.setFirstResult(pageRequest.getOffset())
                .setMaxResults(pageRequest.getPageSize());
        @SuppressWarnings(UNCHECKED)
        List<Topic> topics = (List<Topic>) query.list();
        return new PageImpl<Topic>(topics, pageRequest, totalCount);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int countTopics(Branch branch) {
        Number count = (Number) session()
                .getNamedQuery("getCountTopicsInBranch")
                .setParameter(BRANCH, branch)
                .uniqueResult();
        return count.intValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<JCUser> getAllowedSubscribers(SubscriptionAwareEntity entity){
        // use set for remove duplicates
        @SuppressWarnings("unchecked")
        Set<JCUser> foundUsers = new HashSet<JCUser>(session()
                .getNamedQuery("getAllowedSubscribersForTopic")
                .setParameter("topic", entity)
                .list());
        return foundUsers;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Long> getForbiddenBranchesIds(JCUser user) {

        Query query = session().getNamedQuery("getForbiddenBranchesIds");

        if(user.isAnonymous()) {
            query.setString("sid", user.getClass().getSimpleName());
        } else {
            query.setParameterList("sid", getGroupIds(user));
        }

        return query.list();
    }
}
