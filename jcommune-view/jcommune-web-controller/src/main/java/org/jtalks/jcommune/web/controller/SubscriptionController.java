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
import org.jtalks.jcommune.service.TopicFetchService;
import org.jtalks.jcommune.service.exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

/**
 * Manages the topic/branch subscription by email.
 * Serves AJAX subscription/unsubscription requests
 *
 * @author Evgeniy Naumenko
 */
@Controller
public class SubscriptionController {

    private TopicFetchService topicFetchService;
    private BranchService branchService;
    private SubscriptionService subscriptionService;

    /**
     * @param topicFetchService   topic service to load topics from DB
     * @param branchService       branch service to load branches from DB
     * @param subscriptionService to manage actual updates subscription info
     */
    @Autowired
    public SubscriptionController(TopicFetchService topicFetchService,
                                  BranchService branchService,
                                  SubscriptionService subscriptionService) {
        this.topicFetchService = topicFetchService;
        this.branchService = branchService;
        this.subscriptionService = subscriptionService;
    }

    /**
     * Activates branch updates subscription for the current user.
     * This includes post creation, removal and updates
     *
     * @param id identifies topic to subscribe to
     * @throws NotFoundException if no object is found for id given
     */
    @RequestMapping("topics/{id}/subscribe")
    @ResponseBody
    public void subscribeToTopic(@PathVariable Long id) throws NotFoundException {
        Topic topic = topicFetchService.get(id);
        subscriptionService.toggleTopicSubscription(topic);
    }

    /**
     * Deactivates branch updates subscription for the current user
     *
     * @param id identifies topic to unsubscribe from
     * @throws NotFoundException if no object is found for id given
     */
    @RequestMapping("topics/{id}/unsubscribe")
    @ResponseBody
    public void unsubscribeFromTopic(@PathVariable Long id) throws NotFoundException {
        Topic topic = topicFetchService.get(id);
        subscriptionService.toggleTopicSubscription(topic);
    }

    /**
     * Activates branch updates subscription for the current user.
     * This includes topic creation, removal and all topic updates
     *
     * @param id identifies branch to subscribe to
     * @throws NotFoundException if no object is found for id given
     */
    @RequestMapping("branches/{id}/subscribe")
    @ResponseBody
    public void subscribeToBranch(@PathVariable Long id) throws NotFoundException {
        Branch branch = branchService.get(id);
        subscriptionService.toggleBranchSubscription(branch);
    }

    /**
     * Deactivates branch updates subscription for the current user
     *
     * @param id identifies branch to unsubscribe from
     * @throws NotFoundException if no object is found for id given
     */
    @RequestMapping("branches/{id}/unsubscribe")
    @ResponseBody
    public void unsubscribeFromBranch(@PathVariable Long id) throws NotFoundException {
        Branch branch = branchService.get(id);
        subscriptionService.toggleBranchSubscription(branch);
    }

    /**
     * Deactivates branch updates subscription for the current user
     *
     * @param id identifies branch to unsubscribe from
     * @throws NotFoundException if no object is found for id given
     */
    @RequestMapping("branches/{id}/unsubscribe_link")
    public ModelAndView  unsubscribeFromBranchByLink(@PathVariable Long id) throws NotFoundException {
        Branch branch = branchService.get(id);
        subscriptionService.unsubscribeFromBranch(branch);
        return new ModelAndView("redirect:/branches/" + id);
    }
}
