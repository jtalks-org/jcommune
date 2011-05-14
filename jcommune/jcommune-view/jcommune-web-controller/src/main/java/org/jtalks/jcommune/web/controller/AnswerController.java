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
import org.jtalks.jcommune.model.entity.User;
import org.jtalks.jcommune.service.TopicService;
import org.jtalks.jcommune.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

/**
 *
 * @author Temdegon
 */
@Controller
public class AnswerController {

    private TopicService topicService;
    private final UserService userService;

    @Autowired
    public AnswerController(TopicService topicService, UserService userService) {
        this.topicService = topicService;
        this.userService = userService;
    }

    @RequestMapping(value = "/answer")
    public ModelAndView answer(@RequestParam("topicId") Long topicId) {
        System.out.println("AnswerController: Answering..." + topicId);
        ModelAndView mav = new ModelAndView("answer");
        mav.addObject("topicId", topicId);
        return mav;
    }

    @RequestMapping(value = "/postAnswer")
    public ModelAndView postAnswer(@RequestParam("topicId") Long topicId,
            @RequestParam("author") String author,
            @RequestParam("bodytext") String bodyText) {
        addNewPost(topicId, author, bodyText);
        return new ModelAndView("redirect:/topics/" + topicId + ".html");
    }

    /**
     * Add new answer to the topic.
     * @param topicId - the id of the topic to add the answer
     * @param author - author who adds the answer
     * @param bodyText - the body of the answer
     */
    private void addNewPost(Long topicId, String author, String bodyText) {
        Post answer = Post.createNewPost();
        answer.setPostContent(bodyText);
        answer.setUserCreated(getUser(author));
        topicService.addAnswer(topicId, answer);
    }

    private User getUser(String nickName) {
        User user = new User();
        user.setFirstName(nickName);
        user.setLastName(nickName);
        user.setUsername(nickName);
        userService.saveOrUpdate(user);
        return user;
    }
}
