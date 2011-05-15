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
import org.jtalks.jcommune.model.entity.User;
import org.jtalks.jcommune.service.SecurityService;
import org.jtalks.jcommune.service.TopicService;
import org.springframework.web.servlet.ModelAndView;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.ModelAndViewAssert.assertViewName;
import static org.springframework.test.web.ModelAndViewAssert.assertModelAttributeValues;

public class TopicRenderControllerTest {
    private TopicService topicService;
    private SecurityService securitySecvice;
    private TopicRenderController topicRenderController;

    @BeforeMethod
    public void init() {
        topicService = mock(TopicService.class);
        securitySecvice = mock(SecurityService.class);
        topicRenderController = new TopicRenderController(topicService,securitySecvice);
    }
    
    private User getUser(){
        User user = new User();
        user.setUsername("username");
        return user;
    }

    @Test
    public void testShowTopic() {
        Topic topic = Topic.createNewTopic();
        topic.setId(1l);
        topic.setTitle("Simple Title");
        
        Map<String, Object> topicMap = new HashMap<String, Object>();
        topicMap.put("selectedTopic", topic);
        topicMap.put("currentUser", getUser());
        
        when(topicService.getTopicWithPosts(1l)).thenReturn(topic);
        when(securitySecvice.getCurrentUser()).thenReturn(getUser());
        
        ModelAndView mav = topicRenderController.showTopic(1l);
        verify(topicService).getTopicWithPosts(1l);
        verify(securitySecvice.getCurrentUser(),times(1));
        
        assertModelAttributeValues(mav, topicMap);
        assertViewName(mav, "renderTopic");
    }
}
