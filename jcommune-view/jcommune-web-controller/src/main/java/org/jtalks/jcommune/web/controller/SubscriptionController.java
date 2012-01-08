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

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Manages the topic/branch subscription by email.
 * Serves AJAX subscription/unsubscription requests, so locale-dependent
 * messages are resolved here and passed to the client.
 *
 * @author Evgeniy Naumenko
 */
@Controller
public class SubscriptionController {

    private final static String CAPTION = "caption";
    private final static String SUFFIX = "urlSuffix";

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
    public SubscriptionController(TopicService topicService,
                                  BranchService branchService,
                                  SubscriptionService subscriptionService,
                                  MessageSource messageSource) {
        this.topicService = topicService;
        this.branchService = branchService;
        this.subscriptionService = subscriptionService;
        this.messageSource = messageSource;
    }

    /**
     * Activates branch updates subscription for the current user.
     * This includes post creation, removal and updates
     *
     * @param topicId identifies topic to subscribe to
     * @param locale  current user locale settings to resolve messages
     * @return info to alter "subscribe" button to the "unsubscribe" one
     * @throws NotFoundException if no object is found for id given
     */
    @RequestMapping("topics/{topicId}/subscribe")
    @ResponseBody
    public Map<String, String> subscribeToTopic(@PathVariable Long topicId, Locale locale) throws NotFoundException {
        Topic topic = topicService.get(topicId);
        subscriptionService.toggleTopicSubscription(topic);
        Map model = new HashMap();
        model.put(CAPTION, messageSource.getMessage("label.unsubscribe", null, locale));
        model.put(SUFFIX, "/topics/" + topicId + "/unsubscribe");
        return model;
    }

    /**
     * Deactivates branch updates subscription for the current user
     *
     * @param topicId identifies topic to unsubscribe from
     * @param locale  current user locale settings to resolve messages
     * @return info to alter "unsubscribe" button to the "subscribe" one
     * @throws NotFoundException if no object is found for id given
     */
    @RequestMapping("topics/{topicId}/unsubscribe")
    @ResponseBody
    public Map<String, String> unsubscribeFromTopic(@PathVariable Long topicId, Locale locale)
            throws NotFoundException {
        Topic topic = topicService.get(topicId);
        subscriptionService.toggleTopicSubscription(topic);
        Map model = new HashMap();
        model.put(CAPTION, messageSource.getMessage("label.subscribe", null, locale));
        model.put(SUFFIX, "/topics/" + topicId + "/subscribe");
        return model;
    }

    /**
     * Activates branch updates subscription for the current user.
     * This includes topic creation, removal and all topic updates
     *
     * @param branchId identifies branch to subscribe to
     * @param locale   current user locale settings to resolve messages
     * @return info to alter "subscribe" button to the "unsubscribe" one
     * @throws NotFoundException if no object is found for id given
     */
    @RequestMapping("branches/{branchId}/subscribe")
    @ResponseBody
    public Map<String, String> subscribeToBranch(@PathVariable Long branchId, Locale locale)
            throws NotFoundException {
        Branch branch = branchService.get(branchId);
        subscriptionService.toggleBranchSubscription(branch);
        Map model = new HashMap();
        model.put(CAPTION, messageSource.getMessage("label.unsubscribe", null, locale));
        model.put(SUFFIX, "/branches/" + branchId + "/unsubscribe");
        return model;
    }

    /**
     * Deactivates branch updates subscription for the current user
     *
     * @param branchId identifies branch to unsubscribe from
     * @param locale   current user locale settings to resolve messages
     * @return info to alter "unsubscribe" button to the "subscribe" one
     * @throws NotFoundException if no object is found for id given
     */
    @RequestMapping("branches/{branchId}/unsubscribe")
    @ResponseBody
    public Map<String, String> unsubscribeFromBranch(@PathVariable Long branchId, Locale locale)
            throws NotFoundException {
        Branch branch = branchService.get(branchId);
        subscriptionService.toggleBranchSubscription(branch);
        Map model = new HashMap();
        model.put(CAPTION, messageSource.getMessage("label.subscribe", null, locale));
        model.put(SUFFIX, "/branches/" + branchId + "/subscribe");
        return model;
    }
}
