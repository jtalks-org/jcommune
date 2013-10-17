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

import org.jtalks.common.model.dao.Crud;
import org.jtalks.jcommune.model.dao.BranchDao;
import org.jtalks.jcommune.model.dao.TopicDao;
import org.jtalks.jcommune.model.entity.*;
import org.jtalks.jcommune.service.SubscriptionService;
import org.jtalks.jcommune.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import java.util.Collection;

/**
 * Implements database-backed durable subscriptions on forum object's updates.
 * All the subscriptions are performed on behalf of the current user, so there
 * is no way to subscribe someone else.
 * Current implementation just stores the subscription status in a database
 * leaving notifications to the collaborating classes.
 *
 * @author Evgeniy Naumenko
 */
@PreAuthorize("isAuthenticated()")
public class TransactionalSubscriptionService implements SubscriptionService {

    private UserService userService;
    private BranchDao branchDao;
    private TopicDao topicDao;
    private Crud<CodeReview> codeReviewDao;

    /**
     * @param userService to determine the current user requested the operation
     * @param branchDao       for branch subscription updates
     * @param topicDao        for topic subscription updates
     * @param codeReviewDao for code review subscription updates
     */
    public TransactionalSubscriptionService(UserService userService,
                                            BranchDao branchDao,
                                            TopicDao topicDao,
                                            Crud<CodeReview> codeReviewDao) {
        this.userService = userService;
        this.branchDao = branchDao;
        this.topicDao = topicDao;
        this.codeReviewDao = codeReviewDao;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void toggleTopicSubscription(Topic topic) {
        JCUser current = userService.getCurrentUser();
        if (topic.userSubscribed(current)) {
            topic.getSubscribers().remove(current);
        } else {
            topic.getSubscribers().add(current);
        }
        topicDao.saveOrUpdate(topic);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void toggleBranchSubscription(Branch branch) {
        JCUser current = userService.getCurrentUser();
        if (branch.getSubscribers().contains(current)) {
            branch.getSubscribers().remove(current);
        } else {
            branch.getSubscribers().add(current);
        }
        branchDao.saveOrUpdate(branch);
    }

    @Override
    public void toggleSubscription(SubscriptionAwareEntity entityToSubscribe) {
        JCUser current = userService.getCurrentUser();
        if (entityToSubscribe.getSubscribers().contains(current)) {
            entityToSubscribe.getSubscribers().remove(current);
        } else {
            entityToSubscribe.getSubscribers().add(current);
        }
        saveChanges(entityToSubscribe);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<JCUser> getAllowedSubscribers(SubscriptionAwareEntity entity){
        if (entity instanceof Topic) {
            return this.topicDao.getAllowedSubscribers(entity);
        } else if (entity instanceof CodeReview) {
            return this.topicDao.getAllowedSubscribers(((CodeReview) entity).getTopic());
        } else{
            return this.branchDao.getAllowedSubscribers(entity);
        }
    }

    private void saveChanges(SubscriptionAwareEntity entityToSubscribe) {
        if (entityToSubscribe instanceof CodeReview) {
            codeReviewDao.saveOrUpdate((CodeReview) entityToSubscribe);
        }
    }
}
