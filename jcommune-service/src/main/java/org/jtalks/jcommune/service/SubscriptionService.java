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
package org.jtalks.jcommune.service;

import org.jtalks.jcommune.model.entity.Branch;
import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.model.entity.SubscriptionAwareEntity;
import org.jtalks.jcommune.model.entity.Topic;

import java.util.Collection;

/**
 * This service enables or disables updates subscription by email.
 * That means user is able to receive emails when new post is created
 * or topic is updated/deleted/created depends on subscription type.
 *
 * @author Evgeniy Naumenko
 */
public interface SubscriptionService {

    /**
     * Toggle subscription state for the topic given. Thus, if user is subscribed
     * to the topic updates the method call will unsubscribe him and vice versa.
     * Subscription will be applied to the current user logged in.
     * Topic subscription means that user will receive email notification for every
     * topic update except for topic creation/removal.
     *
     * @param topic topic to subscribe or unsubscribe current user to
     */
    //todo may be is needed to replace it to toggleSubscription(SubscriptionAwareEntity entityToSubscribe)
    void toggleTopicSubscription(Topic topic);

    /**
     * Toggle subscription state for the branch given. Thus, if user is subscribed
     * to the branch updates the method call will unsubscribe him and vice versa.
     * Subscription will be applied to the current user logged in.
     * Branch subscription means that user will receive email notification for every
     * new/updated/removed posts/topics in the branch given.
     *
     * @param branch branch to subscribe or unsubscribe current user to
     */
    //todo may be is needed to replace it to toggleSubscription(SubscriptionAwareEntity entityToSubscribe)
    void toggleBranchSubscription(Branch branch);

    /**
     * Unsubscribe current user from branch.
     *
     * @param branch branch to unsubscribe
     */
    void unsubscribeFromBranch(Branch branch);

    /**
     * Toggle subscription state for the {@link SubscriptionAwareEntity} given. Thus, if user is subscribed
     * to the  updates the method call will unsubscribe him and vice versa.
     * Subscription will be applied to the current user logged in.
     *
     * @param entityToSubscribe object to subscribe or unsubscribe current user to
     */
    void toggleSubscription(SubscriptionAwareEntity entityToSubscribe);

    /**
     * Get subscribers for specified entity with allowed permission to read.
     *
     *
     *
     * @param entity the Topic or Branch or CodeReview
     * @return subscribers with allowed permission
     */
    Collection<JCUser> getAllowedSubscribers(SubscriptionAwareEntity entity);

    /**
     * Subscribe subscription state for the {@link SubscriptionAwareEntity} given.
     * Subscription will be applied to the current user logged in.
     *
     * @param entityToSubscribe object to subscribe or unsubscribe current user to
     */
    void subscribe(SubscriptionAwareEntity entityToSubscribe);

}
