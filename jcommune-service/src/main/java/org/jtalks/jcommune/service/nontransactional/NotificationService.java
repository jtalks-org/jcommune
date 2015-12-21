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
package org.jtalks.jcommune.service.nontransactional;

import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.model.entity.SubscriptionAwareEntity;
import org.jtalks.jcommune.model.entity.Topic;
import org.jtalks.jcommune.plugin.api.PluginLoader;
import org.jtalks.jcommune.plugin.api.core.Plugin;
import org.jtalks.jcommune.plugin.api.core.TopicPlugin;
import org.jtalks.jcommune.plugin.api.filters.StateFilter;
import org.jtalks.jcommune.plugin.api.filters.TypeFilter;
import org.jtalks.jcommune.service.SubscriptionService;
import org.jtalks.jcommune.service.UserService;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Send email notifications to the users subscribed.
 * If the update author is subscribed he won't get the notification message.
 * This service also assumes, that topic update as a enclosing branch update as well.
 * <p/>
 * Errors occurred while sending emails are suppressed (logged only) as updates
 * notifications are themselves a kind of a side effect, so they should not prevent
 * the whole operation from being completed.
 *
 * @author Evgeniy Naumenko
 * @author Vitaliy Kravchenko
 */
public class NotificationService {

    SubscriptionService subscriptionService;
    private UserService userService;
    private MailService mailService;
    private final PluginLoader pluginLoader;

    /**
     * @param userService                  to determine the update author
     * @param mailService                  to perform actual email notifications
     * @param subscriptionService          to get the subscribers of the entity
     * @param pluginLoader                 to get different subscribers for plugable topics
     */
    public NotificationService(
            UserService userService,
            MailService mailService,
            SubscriptionService subscriptionService,
            PluginLoader pluginLoader) {
        this.userService = userService;
        this.mailService = mailService;
        this.subscriptionService = subscriptionService;
        this.pluginLoader = pluginLoader;
    }

    /**
     * Notifies subscribers about subscribed entity updates by email.
     * If mailing failed this implementation simply continues
     * with other subscribers.
     *
     * @param entity changed subscribed entity.
     */
    public void subscribedEntityChanged(SubscriptionAwareEntity entity) {
        Collection<JCUser> subscribers = subscriptionService.getAllowedSubscribers(entity);
        filterSubscribers(subscribers, entity);

        for (JCUser user : subscribers) {
            mailService.sendUpdatesOnSubscription(user, entity);
        }
    }

    /**
     * Overload for skipping topic subscribers
     *
     * @param entity changed subscribed entity.
     * @param excludeFromNotification a collection of subscribers who are excluded from a notification.
     */
    public void subscribedEntityChanged(SubscriptionAwareEntity entity, Collection<JCUser> excludeFromNotification) {
        Collection<JCUser> subscribers = subscriptionService.getAllowedSubscribers(entity);
        subscribers.removeAll(excludeFromNotification);
        filterSubscribers(subscribers, entity);

        for (JCUser user : subscribers) {
            mailService.sendUpdatesOnSubscription(user, entity);
        }
    }

    /**
     * Notifies topic starter by email that his or her topic
     * was moved to another sections and also notifies all branch
     * subscribers
     *
     * @param topic topic moved
     */
    public void sendNotificationAboutTopicMoved(Topic topic) {
        String curUser = userService.getCurrentUser().getUsername();

        //send notification to topic subscribers
        Collection<JCUser> topicSubscribers = subscriptionService.getAllowedSubscribers(topic);
        this.filterSubscribers(topicSubscribers, topic);

        for (JCUser subscriber : topicSubscribers) {
            mailService.sendTopicMovedMail(subscriber, topic, curUser);
        }

        //send notification to branch subscribers
        Set<JCUser> branchSubscribers = new HashSet<>(topic.getBranch().getSubscribers());

        this.filterSubscribers(branchSubscribers, topic.getBranch());
        for (JCUser subscriber : branchSubscribers) {
            if (!topicSubscribers.contains(subscriber)) {
                mailService.sendTopicMovedMail(subscriber, topic, curUser);
            }
        }
    }

    /**
     * Filter collection - remove current user from subscribers and performs plugin filtering
     *
     * @param subscribers collection of subscribers
     * @see org.jtalks.jcommune.plugin.api.core.SubscribersFilter
     */
    private void filterSubscribers(Collection<JCUser> subscribers, SubscriptionAwareEntity entity) {
        List<Plugin> plugins = pluginLoader.getPlugins(new StateFilter(Plugin.State.ENABLED),
                new TypeFilter(TopicPlugin.class));
        for (Plugin plugin : plugins) {
            TopicPlugin topicPlugin = (TopicPlugin)plugin;
            topicPlugin.getSubscribersFilter().filter(subscribers, entity);
        }
        // Current user should be removed after filtering by plugin because filter don't know anything
        // about current user
        subscribers.remove(userService.getCurrentUser());
    }

    /**
     * Send notification to subscribers about removing topic or code review.
     *
     * @param topic       Current topic
     * @param subscribers Collection of subscribers
     */
    public void sendNotificationAboutRemovingTopic(Topic topic, Collection<JCUser> subscribers) {
        String curUser = userService.getCurrentUser().getUsername();
        this.filterSubscribers(subscribers, topic);
        for (JCUser subscriber : subscribers) {
            mailService.sendRemovingTopicMail(subscriber, topic, curUser);
        }
    }

    /**
     * Notify about new topic creation in the subscribed branch.
     *
     * @param topic newly created topic
     */
    public void sendNotificationAboutTopicCreated(Topic topic) {
        Collection<JCUser> branchSubscribers = subscriptionService.getAllowedSubscribers(topic.getBranch());
        this.filterSubscribers(branchSubscribers, topic);
        for (JCUser subscriber : branchSubscribers) {
            mailService.sendTopicCreationMail(subscriber, topic);
        }
    }
}

