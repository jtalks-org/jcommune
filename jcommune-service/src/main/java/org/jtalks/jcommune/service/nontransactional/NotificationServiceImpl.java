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
import org.jtalks.jcommune.model.entity.User;
import org.jtalks.jcommune.service.MailService;
import org.jtalks.jcommune.service.NotificationService;
import org.jtalks.jcommune.service.SecurityService;
import org.jtalks.jcommune.service.exceptions.MailingFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
 */
public class NotificationServiceImpl implements NotificationService {

    private SecurityService securityService;
    private MailService mailService;

    /**
     * @param securityService to determine the update author
     * @param mailService to perform actual email notifications.
     */
    public NotificationServiceImpl(SecurityService securityService, MailService mailService) {
        this.securityService = securityService;
        this.mailService = mailService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void topicChanged(Topic topic) {
        User current = securityService.getCurrentUser();
        this.branchChanged(topic.getBranch());
        for (User user : topic.getSubscribers()) {
            if (!user.equals(current)) {
                mailService.sendTopicUpdatesOnSubscription(user, topic);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void branchChanged(Branch branch) {
        User current = securityService.getCurrentUser();
        for (User user : branch.getSubscribers()) {
            if (!user.equals(current)) {
                mailService.sendBranchUpdatesOnSubscription(user, branch);
            }
        }
    }

}

