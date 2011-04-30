package org.jtalks.jcommune.web.controller;

/**
 * Created by IntelliJ IDEA.
 * User: Christoph
 * Date: 29.04.2011
 * Time: 18:24:35
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

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.testng.Assert.assertEquals;

import org.jtalks.jcommune.model.entity.Post;
import org.jtalks.jcommune.model.entity.User;
import org.jtalks.jcommune.service.PostService;
import org.jtalks.jcommune.service.TopicService;
import org.jtalks.jcommune.service.UserService;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.servlet.ModelAndView;

import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;


public class NewTopicControllerTest {

    private final String TOPIC_PARAMETER = "topic";
    private final String AUTHOR_PARAMETER = "author";
    private final String BODY_TEXT_PARAMETER = "bodytext";

    private final String TOPIC_CONTENT = "Spring Questions";
    private final String BODY_TEXT_CONTENT = "Topic info goes here";

    private final String NICK_USER_NAME = "Christoph";

    private NewTopicController newTopicController;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private AnnotationMethodHandlerAdapter adapter;

    @Mock
    private TopicService topicService;

    @Mock
    private UserService userService;

    @Mock
    private PostService postService;

    @Mock
    private User user;

    @Mock
    private Post post;

    @BeforeMethod
    public void init() {
        MockitoAnnotations.initMocks(this);
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        adapter = new AnnotationMethodHandlerAdapter();

    }

    @Test
    public void testControllerMapping() throws Exception {
    }

    @Test
    public void testSubmitNewTopic() throws Exception {
        // Initialization request
        request.setMethod("POST");
        request.addParameter(TOPIC_PARAMETER, TOPIC_CONTENT);
        request.addParameter(AUTHOR_PARAMETER, NICK_USER_NAME);
        request.addParameter(BODY_TEXT_PARAMETER, BODY_TEXT_CONTENT);
        request.setRequestURI("/createNewTopic");

        // Creating NewTopicController with appropriate services
        newTopicController = new NewTopicController(topicService, postService, userService);
        adapter.handle(request, response, newTopicController);

        assertEquals(TOPIC_CONTENT, request.getParameter(TOPIC_PARAMETER));
        assertEquals(AUTHOR_PARAMETER,request.getParameter(NICK_USER_NAME));
        assertEquals(BODY_TEXT_CONTENT, request.getParameter(BODY_TEXT_PARAMETER));

        // Verify User and UserService
        verify(newTopicController);


        verify(userService).saveOrUpdate(user);
    }
}
