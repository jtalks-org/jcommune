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
package org.jtalks.jcommune.web.controller;

import org.jtalks.jcommune.model.entity.Branch;
import org.jtalks.jcommune.model.entity.Topic;
import org.jtalks.jcommune.service.BranchService;
import org.jtalks.jcommune.service.SubscriptionService;
import org.jtalks.jcommune.service.TopicService;
import org.jtalks.jcommune.service.exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Locale;

/**
 * @author Evgeniy Naumenko
 */
@Controller
public class SubscriptionsController {

    private TopicService topicService;
    private BranchService branchService;
    private SubscriptionService subscriptionService;
    private MessageSource messageSource;

    @Autowired
    public SubscriptionsController(TopicService topicService,
                                   BranchService branchService,
                                   SubscriptionService subscriptionService,
                                   MessageSource messageSource) {
        this.topicService = topicService;
        this.branchService = branchService;
        this.subscriptionService = subscriptionService;
        this.messageSource = messageSource;
    }

    @RequestMapping("topics/{topicId}/subscribe")
    public void subscribeToTopic(@PathVariable Long topicId, Locale locale) {
        try {
            Topic topic = topicService.get(topicId);
            subscriptionService.subscribeToTopic(topic);

        } catch (NotFoundException e) {

        }
    }

    @RequestMapping("topics/{topicId}/unsubscribe")
    public void unsubscribeFromTopic(@PathVariable Long topicId, Locale locale) {
        try {
            Topic topic = topicService.get(topicId);
            subscriptionService.unsubscribeFromTopic(topic);
        } catch (NotFoundException e) {

        }
    }

    @RequestMapping("branches/{branchId}/subscribe")
    @ResponseBody
    public ControlInfo subscribeTobranch(@PathVariable Long branchId, Locale locale) throws NotFoundException {
        Branch branch = branchService.get(branchId);
        subscriptionService.subscribeToBranch(branch);
        String message = messageSource.getMessage("label.unsubscribe", null, locale);
        return new ControlInfo(message, "branches/{branchId}/unsubscribe");
    }

    @RequestMapping("branches/{branchId}/unsubscribe")
    @ResponseBody
    public ControlInfo unsubscribeFromBranch(@PathVariable Long branchId, Locale locale) throws NotFoundException {
        Branch branch = branchService.get(branchId);
        subscriptionService.subscribeToBranch(branch);
        String message = messageSource.getMessage("label.subscribe", null, locale);
        return new ControlInfo(message, "branches/{branchId}/subscribe");
    }

    public static class ControlInfo {
        public final String caption;
        public final String urlSuffix;

        private ControlInfo(String caption, String urlSuffix) {
            this.caption = caption;
            this.urlSuffix = urlSuffix;
        }
    }
}
