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

import org.jtalks.jcommune.model.entity.User;
import org.jtalks.jcommune.service.SecurityService;
import org.jtalks.jcommune.service.TopicService;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.servlet.ModelAndView;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.ModelAndViewAssert.assertViewName;

public class NewTopicControllerTest {
    private final String TOPIC_CONTENT = "content here";
    private final String TOPIC_THEME = "Topic theme";
    private NewTopicController controller;
    @Mock
    private TopicService topicService;
    @Mock
    private SecurityService securityService;

    @BeforeMethod
    public void init() {
        MockitoAnnotations.initMocks(this);
        controller = new NewTopicController(topicService, securityService);
    }


    @Test
    public void testSubmitNewTopic() throws Exception {
        when(securityService.getCurrentUser()).thenReturn(new User());

        ModelAndView mav = controller.submitNewTopic(TOPIC_THEME, TOPIC_CONTENT);

        assertViewName(mav, "redirect:forum.html");
        verify(securityService, times(1)).getCurrentUser();
        verify(topicService, times(1)).createTopicAsCurrentUser(TOPIC_THEME, TOPIC_CONTENT);
    }

    @Test
    public void testSubmitNewTopicUserNotLoggedIn() throws Exception {
        when(securityService.getCurrentUser()).thenReturn(null);

        ModelAndView mav = controller.submitNewTopic(TOPIC_THEME, TOPIC_CONTENT);

        assertViewName(mav, "redirect:/login.html");
        verify(securityService, times(1)).getCurrentUser();
        verify(topicService, never()).createTopicAsCurrentUser(TOPIC_THEME, TOPIC_CONTENT);
    }
}
