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
 * Manages the topic/branch subscription by email URL mappings.
 * Serves AJAX subscription/unsubscription requests, so locale-dependent
 * messages ar resolved here as there is no way to resolve them on a client.
 *
 * @author Evgeniy Naumenko
 */
@Controller
public class SubscriptionsController {

    private TopicService topicService;
    private BranchService branchService;
    private SubscriptionService subscriptionService;
    private MessageSource messageSource;

    /**
     * @param topicService        topic service to load topics from DB
     * @param branchService       branch service to load branches from DB
     * @param subscriptionService to manage actual updates subscription info
     * @param messageSource       to resolve locale-dependent messages
     */
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

    /**
     * @param topicId
     * @param locale current user locale settings to resolve messages
     * @return info to alter "subscribe" button to the "unsubscribe" one
     * @throws NotFoundException if no object is found for id given
     */
    @RequestMapping("topics/{topicId}/subscribe")
    public ControlInfo subscribeToTopic(@PathVariable Long topicId, Locale locale) throws NotFoundException {
        Topic topic = topicService.get(topicId);
        subscriptionService.subscribeToTopic(topic);
        String message = messageSource.getMessage("label.unsubscribe", null, locale);
        return new ControlInfo(message, "topics/" + topicId + "/unsubscribe");
    }

    /**
     * @param topicId
     * @param locale current user locale settings to resolve messages
     * @return info to alter "unsubscribe" button to the "subscribe" one
     * @throws NotFoundException if no object is found for id given
     */
    @RequestMapping("topics/{topicId}/unsubscribe")
    public ControlInfo unsubscribeFromTopic(@PathVariable Long topicId, Locale locale) throws NotFoundException {
        Topic topic = topicService.get(topicId);
        subscriptionService.unsubscribeFromTopic(topic);
        String message = messageSource.getMessage("label.subscribe", null, locale);
        return new ControlInfo(message, "topics/" + topicId + "/subscribe");
    }

    /**
     * @param branchId
     * @param locale current user locale settings to resolve messages
     * @return info to alter "subscribe" button to the "unsubscribe" one
     * @throws NotFoundException if no object is found for id given
     */
    @RequestMapping("branches/{branchId}/subscribe")
    @ResponseBody
    public ControlInfo subscribeTobranch(@PathVariable Long branchId, Locale locale) throws NotFoundException {
        Branch branch = branchService.get(branchId);
        subscriptionService.subscribeToBranch(branch);
        String message = messageSource.getMessage("label.unsubscribe", null, locale);
        return new ControlInfo(message, "branches/" + branchId + "/unsubscribe");
    }

    /**
     * @param branchId
     * @param locale current user locale settings to resolve messages
     * @return info to alter "unsubscribe" button to the "subscribe" one
     * @throws NotFoundException if no object is found for id given
     */
    @RequestMapping("branches/{branchId}/unsubscribe")
    @ResponseBody
    public ControlInfo unsubscribeFromBranch(@PathVariable Long branchId, Locale locale) throws NotFoundException {
        Branch branch = branchService.get(branchId);
        subscriptionService.subscribeToBranch(branch);
        String message = messageSource.getMessage("label.subscribe", null, locale);
        return new ControlInfo(message, "branches/" + branchId + "/subscribe");
    }

    private static class ControlInfo {
        public final String caption;
        public final String urlSuffix;

        private ControlInfo(String caption, String urlSuffix) {
            this.caption = caption;
            this.urlSuffix = urlSuffix;
        }
    }
}
