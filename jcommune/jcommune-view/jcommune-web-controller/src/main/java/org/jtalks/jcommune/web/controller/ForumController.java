/**
 * Copyright (C) 2011  jtalks.org Team
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
 * Also add information on how to contact you by electronic and paper mail.
 * Creation date: Apr 12, 2011 / 8:05:19 PM
 * The jtalks.org Project
 */
package org.jtalks.jcommune.web.controller;

import org.jtalks.jcommune.model.entity.Topic;
import org.jtalks.jcommune.model.entity.TopicBranch;
import org.jtalks.jcommune.service.TopicBranchService;
import org.jtalks.jcommune.service.TopicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;


/**
 * ForumController class handles GET and POST request with URI - forum.html
 * and populates forum with existing topics.
 * Class methods throw no exceptions
 *
 * @author Kravchenko Vitaliy
 */

@Controller
public final class ForumController {
    private TopicService topicService;
    private TopicBranchService topicBranchService;

    /**
     * Class constructor which creates MVC controller with specifying TopicService
     * object injected via autowiring
     *
     * @param topicService {@link TopicService}the object that provides retrieving  data or saving to database
     */
    @Autowired
    public ForumController(TopicService topicService, TopicBranchService topicBranchService) {
        this.topicService = topicService;
        this.topicBranchService = topicBranchService;
    }

    /**
     * Method handles only GET requests with "/forum" URI
     * and display JSP page with existing topics
     * @param branchId  branch id which recieved from the url pattern
     * @return the ModelAndView object, with "forum" as view name
     */

    @RequestMapping(value = "/branches/{branchId}", method = RequestMethod.GET)
    public ModelAndView showAllTopics(@PathVariable("branchId") long branchId) {
        List<Topic> topics = topicService.getAllTopicsAccordingToBranch(branchId);
        TopicBranch topicBranch = topicBranchService.get(branchId);
        return new ModelAndView("forum", "topicsList", topics).addObject("branchId", topicBranch.getId());
    }


}
