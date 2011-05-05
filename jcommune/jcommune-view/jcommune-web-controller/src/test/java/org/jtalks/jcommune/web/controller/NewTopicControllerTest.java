/**
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

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.ModelAndViewAssert.assertViewName;
import static org.testng.Assert.assertEquals;

import org.jtalks.jcommune.model.entity.Post;
import org.jtalks.jcommune.model.entity.Topic;
import org.jtalks.jcommune.model.entity.User;
import org.jtalks.jcommune.service.TopicService;
import org.jtalks.jcommune.service.UserService;

import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.springframework.web.servlet.ModelAndView;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class NewTopicControllerTest {
    private final String TOPIC_CONTENT = "Spring Questions";
    private final String BODY_TEXT_CONTENT = "Topic info goes here";
    private final String NICK_USER_NAME = "Christoph";
    private NewTopicController newTopicController;
    @Mock
    private TopicService topicService;
    @Mock
    private UserService userService;

    @BeforeMethod
    public void init() {
        MockitoAnnotations.initMocks(this);
        newTopicController = new NewTopicController(topicService, userService);
    }


    @Test
    public void testSubmitNewTopic() throws Exception {
        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        ArgumentCaptor<Post> postArgumentCaptor = ArgumentCaptor.forClass(Post.class);
        ArgumentCaptor<Topic> topicArgumentCaptor = ArgumentCaptor.forClass(Topic.class);

        ModelAndView mav = newTopicController.submitNewTopic(TOPIC_CONTENT, NICK_USER_NAME, BODY_TEXT_CONTENT);

        User user = new User();
        user.setFirstName(NICK_USER_NAME);
        user.setLastName(NICK_USER_NAME);
        user.setNickName(NICK_USER_NAME);

        verify(userService).saveOrUpdate(userArgumentCaptor.capture());
        assertEquals(user, userArgumentCaptor.getValue());

        Post post = Post.createNewPost();
        post.setUserCreated(user);
        post.setPostContent(BODY_TEXT_CONTENT);

        Topic topic = new Topic();
        topic.setTitle(TOPIC_CONTENT);
        topic.setTopicStarter(user);
        topic.addPost(post);

        verify(topicService).saveOrUpdate(topicArgumentCaptor.capture());
        assertEquals(topic, topicArgumentCaptor.getValue());

        assertViewName(mav, "redirect:forum.html");
    }
}
