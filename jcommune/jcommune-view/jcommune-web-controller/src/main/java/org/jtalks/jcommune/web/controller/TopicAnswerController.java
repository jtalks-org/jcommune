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
import org.jtalks.jcommune.service.TopicService;
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
@RequestMapping(value = "/answer")
public class TopicAnswerController {

    public static final int MIN_ANSWER_LENGTH = 1;
    private TopicService topicService;

    /**
     * Constructor creates MVC controller with specifying TopicService, SecurityService.
     * 
     * @param topicService {@link TopicService} to be injected
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
    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView getAnswerPage(@RequestParam("topicId") Long topicId,
            @RequestParam(value = "validationError", required = false) Boolean validationError) {
        ModelAndView mav = new ModelAndView("answer");
        Topic answeringTopic = topicService.get(topicId);
        mav.addObject("topic", answeringTopic);
        if (validationError != null && validationError) {
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
    @RequestMapping(method = RequestMethod.POST)
    public ModelAndView submitAnswer(@RequestParam("topicId") Long topicId,
            @RequestParam("bodytext") String bodyText) {
        if (isValidAnswer(bodyText)) {
            topicService.addAnswer(topicId, bodyText);
            return new ModelAndView("redirect:/topics/" + topicId + ".html");            
        } else {
            return getAnswerPage(topicId, true);
        }

    }
    
    /**
     * Check the answer length.
     * @param bodyText answer content
     * @return true if answer is valid
     */
    private boolean isValidAnswer(String bodyText) {
        return bodyText.trim().length() > MIN_ANSWER_LENGTH;
    }
}
