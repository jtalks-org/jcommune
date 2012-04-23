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

import org.jtalks.jcommune.model.entity.Branch;
import org.jtalks.jcommune.model.entity.Topic;
import org.jtalks.jcommune.model.entity.JCUser;

import java.util.Set;

/**
 * Send email notifications to the users subscribed.
 * If the update author is subscribed he won't get the notification message.
 * This service also assumes, that topic update as a enclosing branch update as well.
 *
 * Errors occured while sending emails are suppressed (logged only) as updates
 * notifications are themselves a kind of a side effect, so they should not prevent
 * the whole operation from beeing completed.
 *
 * @author Evgeniy Naumenko
 * @author Vitaliy Kravchenko
 */
public class NotificationService {

    private SecurityService securityService;
    private MailService mailService;

    /**
     * @param securityService to determine the update author
     * @param mailService to perform actual email notifications.
     */
    public NotificationService(SecurityService securityService, MailService mailService) {
        this.securityService = securityService;
        this.mailService = mailService;
    }

    /**
     * Notifices subscribers that the ceertain topic has been changed by email.
     * Call of this method will alos trigger enclosing branch update event.
     *
     * @param topic topic changed
     */
    public void topicChanged(Topic topic) {
        JCUser current = securityService.getCurrentUser();
        for (JCUser user : topic.getSubscribers()) {
            if (!user.equals(current)) {
                mailService.sendTopicUpdatesOnSubscription(user, topic);
            }
        }
    }

    /**
     * Notifies subscribers about branch updates by email.
     * If mailing failed thi implementation simply continues
     * with other sunscribers.
     *
     * @param branch branch changed
     */
    public void branchChanged(Branch branch) {
        JCUser current = securityService.getCurrentUser();
        for (JCUser user : branch.getSubscribers()) {
            if (!user.equals(current)) {
                mailService.sendBranchUpdatesOnSubscription(user, branch);
            }
        }
    }

    /**
     * Notifies topic starter by email that his or her topic
     * was moved to another sections and also notifies all branch
     * subscribers
     * 
     * @param topic  topic moved
     * @param topicId  topic id
     */
    public void topicMoved(Topic topic, long topicId){
         JCUser currentUser = securityService.getCurrentUser();
         JCUser topicStarter = topic.getTopicStarter();        
         Set<JCUser> subscribers = topic.getBranch().getSubscribers();
         for (JCUser subscriber : subscribers){
             if (!subscriber.equals(currentUser)) {
                mailService.sendTopicMovedMail(subscriber, topicId);
             }
         }
         if (!subscribers.contains(topicStarter)){
             mailService.sendTopicMovedMail(topicStarter,topicId);
         }
    }

}

