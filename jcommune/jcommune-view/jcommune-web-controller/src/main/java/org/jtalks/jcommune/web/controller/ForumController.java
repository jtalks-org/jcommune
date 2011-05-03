/**
 * JTalks for uniting people
 * Copyright (C) 2011  JavaTalks Team
 *
 *  This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 * Also add information on how to contact you by electronic and paper mail.
 */

package org.jtalks.jcommune.web.controller;


import org.jtalks.jcommune.model.entity.Topic;
import org.jtalks.jcommune.service.TopicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

/**
 * ForumController class handles GET and POST request with URI - forum.html
 * and populates forum with existing topics.
 * Class methods throw no exceptions
 * @author Kravchenko Vitaliy
 */


@Controller
public class ForumController {

    private TopicService topicService;

    /**
     * ForumController class constructor
     * @param topicService the object that provides retrieving  data or saving to database
     * @see org.springframework.beans.factory.annotation.Autowired;
     */
    @Autowired
    public ForumController(TopicService topicService) {
      this.topicService = topicService;
    }

    /**
     * Populates pages with topics
     * @return the List of topics
     */
    @ModelAttribute("topicsList")
    public List<Topic> populateForum() {
        return topicService.getAll();
    }

    /**
     * Method handles only GET requests with "/forum" URI
     * and display JSP page with existing topics
     * @return  the ModelAndView object, with "forum" as view name
     */
    @RequestMapping(value = "/forum", method = RequestMethod.GET)
    public ModelAndView showAllTopics() {
       return new ModelAndView("forum");
    }


    /**
     * Method Handles POST requests always when the user pressing  "Create new topic" button
     * @return  the JSP's view name
     */
    @RequestMapping(value = "/forum", method = RequestMethod.POST)
    public ModelAndView postPage() {
        return new ModelAndView("newTopic");
    }
}
