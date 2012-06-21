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
package org.jtalks.jcommune.service.transactional;

import org.jtalks.common.security.SecurityService;
import org.jtalks.jcommune.model.dao.BranchDao;
import org.jtalks.jcommune.model.dao.TopicDao;
import org.jtalks.jcommune.model.entity.Branch;
import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.model.entity.Topic;
import org.jtalks.jcommune.service.SubscriptionService;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 * Implements database-backed durable subscriptions on forum object's updates.
 * All the subscriptions are performed on behalf of the current user, so there
 * is no way to subscribe someone else.
 * Current implementation just stores the subscription status in a database
 * leaving notifications to the collaborating classes.
 *
 * @author Evgeniy Naumenko
 */
@PreAuthorize("hasRole('ROLE_USER')")
public class TransactionalSubscriptionService implements SubscriptionService {

    private SecurityService securityService;
    private BranchDao branchDao;
    private TopicDao topicDao;

    /**
     * @param securityService to determine the current user requested the operation
     * @param branchDao       for branch subscription updates
     * @param topicDao        for topic subscription updates
     */
    public TransactionalSubscriptionService(SecurityService securityService, BranchDao branchDao, TopicDao topicDao) {
        this.securityService = securityService;
        this.branchDao = branchDao;
        this.topicDao = topicDao;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void toggleTopicSubscription(Topic topic) {
        JCUser current = (JCUser) securityService.getCurrentUser();
        if (topic.getSubscribers().contains(current)) {
            topic.getSubscribers().remove(current);
        } else {
            topic.getSubscribers().add(current);
        }
        topicDao.update(topic);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void toggleBranchSubscription(Branch branch) {
        JCUser current = (JCUser) securityService.getCurrentUser();
        if (branch.getSubscribers().contains(current)) {
            branch.getSubscribers().remove(current);
        } else {
            branch.getSubscribers().add(current);
        }
        branchDao.update(branch);
    }
}
