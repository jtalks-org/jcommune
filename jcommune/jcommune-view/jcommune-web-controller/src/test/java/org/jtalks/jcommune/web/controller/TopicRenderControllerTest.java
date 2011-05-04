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

import org.jtalks.jcommune.model.entity.Topic;
import org.jtalks.jcommune.service.TopicService;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.servlet.ModelAndView;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.ModelAndViewAssert.assertViewName;
import static org.springframework.test.web.ModelAndViewAssert.assertModelAttributeValues;
import static org.testng.Assert.assertEquals;

public class TopicRenderControllerTest {

    @Mock
    private TopicService topicService;

    private TopicRenderController topicRenderController;

    @BeforeMethod
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testShowTopic() {
        topicRenderController = new TopicRenderController(topicService);

        Topic topic = Topic.createNewTopic();
        topic.setId(1l);
        topic.setTitle("Simple Title");

        Map<String, Object> topicMap = new HashMap<String, Object>();
        topicMap.put("selectedTopic", topic);
        

        when(topicService.getTopic(1l,true)).thenReturn(topic);

        ModelAndView mav = topicRenderController.showTopic(1l);

        verify(topicService).getTopic(1l,true);

        assertModelAttributeValues(mav, topicMap);
        assertViewName(mav, "renderTopic");

    }

    @Test
    public void testRedirectionToMainPage(){
        topicRenderController = new TopicRenderController(topicService);
        ModelAndView mav = topicRenderController.redirectToMainPage();

        assertViewName(mav,"redirect:forum.html");
    }
}
