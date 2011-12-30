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
import org.jtalks.jcommune.model.entity.Section;
import org.jtalks.jcommune.model.entity.Topic;
import org.jtalks.jcommune.model.entity.User;
import org.jtalks.jcommune.service.MailService;
import org.jtalks.jcommune.service.NotificationService;
import org.jtalks.jcommune.service.SecurityService;
import org.jtalks.jcommune.service.exceptions.MailingFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Evgeniy Naumenko
 */
public class NotificationServiceImpl implements NotificationService {

    private SecurityService securrityService;
    private MailService mailService;

    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationServiceImpl.class);
    private static final String LOG_TEMPLATE = "Error occured while sending updates of %s %d to %s";

    public NotificationServiceImpl(SecurityService securrityService, MailService mailService) {
        this.securrityService = securrityService;
        this.mailService = mailService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void topicChanged(Topic topic) {
        User current = securrityService.getCurrentUser();
        this.branchChanged(topic.getBranch());
        for (User user : topic.getSubscribers()) {
            if (!current.equals(user)) {
                try {
                    mailService.sendUpdatesOnSubscription(user);
                } catch (MailingFailedException e) {
                    // no recovery is possible, just skip it and send notification to other subscribers
                    LOGGER.error(String.format(LOG_TEMPLATE, "Topic", topic.getId(), user.getUsername()));
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void branchChanged(Branch branch) {
        User current = securrityService.getCurrentUser();
        for (User user : branch.getSubscribers()) {
            if (!current.equals(user)) {
                try {
                    mailService.sendUpdatesOnSubscription(user);
                } catch (MailingFailedException e) {
                    // no recovery is possible, just skip it and send notification to other subscribers
                    LOGGER.error(String.format(LOG_TEMPLATE, "Branch", branch.getId(), user.getUsername()));
                }
            }
        }

    }


}
