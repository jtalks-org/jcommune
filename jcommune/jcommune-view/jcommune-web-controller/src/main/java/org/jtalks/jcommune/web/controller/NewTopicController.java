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
import org.jtalks.jcommune.service.SecurityService;
import org.jtalks.jcommune.service.TopicService;
import org.jtalks.jcommune.web.dto.TopicDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;


/**
 * NewTopicController handles only POST request with /createNewTopic URI
 * and save Topic entity to database. Controller thrown no exceptions
 *
 * @author Kravchenko Vitaliy
 * @author Kirill Afonin
 * @see Topic
 */
@Controller
public final class NewTopicController {

    private TopicService topicService;
    private SecurityService securityService;

    /**
     * Constructor creates MVC controller with specifying TopicService
     * objects injected via autowiring
     *
     * @param topicService    {@link TopicService}   the object which provides actions on Topic entity
     * @param securityService {@link org.jtalks.jcommune.service.SecurityService}
     */
    @Autowired
    public NewTopicController(TopicService topicService,
                              SecurityService securityService) {
        this.topicService = topicService;
        this.securityService = securityService;
    }

    /**
     * Method handles newTopic.html GET request and display page for creation new topic
     *
     * @param branchId {@link org.jtalks.jcommune.model.entity.Branch} id which we have recived from the hidden
     *                 field of previous JSP page
     * @return  view name - newTopic
     */
    @RequestMapping(value = "/branch/{branchId}/topic/newTopic", method = RequestMethod.GET)
    public ModelAndView getNewTopicPage(@RequestParam("branchId") long branchId) {
        securityService.getCurrentUser();
        ModelAndView mav = new ModelAndView("newTopic");
        mav.addObject("topicDto", new TopicDto());
        mav.addObject("branchId", branchId);
        return mav;
    }

    /**
     * This method handles POST requests, it will be always activated when the user pressing "Submit topic"
     *
     * @param topicDto the object that provides communication between spring form and controller
     * @param result   {@link BindingResult} object for spring validation
     * @param branchId hold the current branchId
     * @return ModelAndView object which will be redirect to forum.html
     */
    @RequestMapping(value = "/branch/{branchId}/topic/newTopic", method = RequestMethod.POST)
    public ModelAndView submitNewTopic(@Valid @ModelAttribute TopicDto topicDto,
                                       BindingResult result,
                                       @RequestParam("branchId") long branchId) {
        // this method will be secured by url
        if (result.hasErrors()) {
            return new ModelAndView("newTopic");
        } else {
            topicService.createTopic(topicDto.getTopicName(), topicDto.getBodyText(),
                    branchId);
            return new ModelAndView("redirect:/branch/" + branchId + ".html");
        }
    }

}
