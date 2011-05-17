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

import org.jtalks.jcommune.model.entity.Post;
import org.jtalks.jcommune.model.entity.Topic;
import org.jtalks.jcommune.service.SecurityService;
import org.jtalks.jcommune.service.TopicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;


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
     * @param topicService    the object which provides actions on Topic entity
     * @param securityService
     * @see TopicService
     * @see Topic
     * @see Post
     */
    @Autowired
    public NewTopicController(TopicService topicService,
                              SecurityService securityService) {
        this.topicService = topicService;
        this.securityService = securityService;
    }

    /**
     * This method handles POST requests, it will be always activated when the user pressing "Submit topic"
     *
     * @param topicName input value from form which represents topic's name
     * @param bodyText  input value from form which represents topic's body
     * @return ModelAndView object which will be redirect to forum.html
     */
    @RequestMapping(value = "/createNewTopic", method = RequestMethod.POST)
    public ModelAndView submitNewTopic(@RequestParam("topic") String topicName,
                                       @RequestParam("bodytext") String bodyText) {

        if (securityService.getCurrentUser() != null) {
            topicService.createTopicAsCurrentUser(topicName, bodyText);
            return new ModelAndView("redirect:forum.html");
        } else {
            return new ModelAndView("redirect:/login.html");
        }
    }

}
