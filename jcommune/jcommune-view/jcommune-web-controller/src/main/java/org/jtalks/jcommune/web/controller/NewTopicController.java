/**
 * Created by IntelliJ IDEA.
 * User: Christoph
 * Date: 21.04.2011
 * Time: 0:57:43
 *
 * JTalks for uniting people
 * Copyright (C) 2011  JavaTalks Team
 *
 * This library is free software; you can redistribute it and/or
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

import org.jtalks.jcommune.model.entity.Post;
import org.jtalks.jcommune.model.entity.Topic;
import org.jtalks.jcommune.model.entity.User;
import org.jtalks.jcommune.service.PostService;
import org.jtalks.jcommune.service.TopicService;
import org.jtalks.jcommune.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;


/**
 * NewTopicController handles only POST request with /createNewTopic URI
 * and save Topic entity to database. Controller thrown no exceptions
 * @see Topic
 * @author  Kravchenko Vitaliy
 */
@Controller
public final class NewTopicController {
     
    private TopicService topicService;
    private PostService postService;
    private UserService userService;


    /**
     * Constructor creates MVC controller with specifying TopicService,PostService,UserService
     * objects injected via autowiring
     * @param topicService the object which provides actions on Topic entity
     * @param postService  the object which provides action on Post entity
     * @param userService  the object which provides action on User entity
     * @see TopicService
     * @see PostService
     * @see UserService
     * @see Topic
     * @see Post
     * @See User
     */
    @Autowired
    public NewTopicController(TopicService topicService, PostService postService, UserService userService) {
        this.topicService = topicService;
        this.postService = postService;
        this.userService = userService;
    }

    /**
     * This method handles POST requests, it will be always activated when the user pressing "Submit topic"
     * @param topicName  input value from form which represents topic's name
     * @param author     input value from form which represents author name
     * @param bodyText   input value from form which represents topic's context
     * @return ModelAndView object which will be redirect to forum.html 
     */
    @RequestMapping(value = "/createNewTopic", method = RequestMethod.POST)
    public ModelAndView submitNewTopic(@RequestParam("topic") String topicName,
                                       @RequestParam("author") String author,
                                       @RequestParam("bodytext") String bodyText) {
        User user = new User();
        user.setFirstName(author);
        user.setLastName(author);
        user.setNickName(author);
        userService.saveOrUpdate(user);

        Post post = Post.createNewPost();
        post.setUserCreated(user);
        post.setPostContent(bodyText);
        postService.saveOrUpdate(post);

        Topic topic = Topic.createNewTopic();
        topic.setTitle(topicName);
        topic.setTopicStarter(user);
        topic.addPost(post);

        topicService.saveOrUpdate(topic);

        return new ModelAndView("redirect:forum.html");
    }
}
