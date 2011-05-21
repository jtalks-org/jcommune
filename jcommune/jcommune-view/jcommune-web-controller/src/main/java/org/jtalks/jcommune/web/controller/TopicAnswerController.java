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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

/**
 * Controller for Topic answer related actions.
 * 
 * @author Pavel Vervenko
 */
@Controller
public class TopicAnswerController {
    public static final int MIN_ANSWER_LENGTH = 2;

    private TopicService topicService;
    private final Logger logger = LoggerFactory.getLogger(TopicAnswerController.class);

    /**
     * Constructor creates MVC controller with specifying TopicService, SecurityService.
     * 
     * @param topicService {@link TopicService} to be injected
     * @param securityService {@link SecurityService} to be injected
     */
    @Autowired
    public TopicAnswerController(TopicService topicService) {
        this.topicService = topicService;
    }

    /**
     * Creates the answering page with empty answer form.
     * If the user isn't logged in he will be redirected to the login page.
     * @param topicId the id of the topic for the answer
     * @param validationError is true when post length is less then 2
     * @return answering <code>ModelAndView</code> or redirect to the login page
     */
    @RequestMapping(method = RequestMethod.GET, value = "/newAnswer")
    public ModelAndView getAnswerPage(@RequestParam("topicId") Long topicId, 
                                      @RequestParam(value="validationError", required = false) Boolean validationError) {
        ModelAndView mav = new ModelAndView("answer");
        Topic answeringTopic = topicService.get(topicId);
        mav.addObject("topic", answeringTopic);
        if (validationError != null && validationError == true) {
            mav.addObject("validationError", validationError);
        }
        return mav;
    }

    /**
     * Process the answer form. Adds new post to the specified topic and redirects to the topic view page.
     * @param topicId the id of the answered topic
     * @param bodyText the content of the answer
     * @return redirect to the topic view
     */
    @RequestMapping(method = RequestMethod.POST, value = "/addAnswer")
    public ModelAndView submitAnswer(@RequestParam("topicId") Long topicId, @RequestParam("bodytext") String bodyText) {
        if (bodyText.trim().length() < MIN_ANSWER_LENGTH) {
            return getAnswerPage(topicId, true);
        } else {
            topicService.addAnswer(topicId, bodyText);
            return new ModelAndView("redirect:/topics/" + topicId + ".html");
        }
        
    }
}
