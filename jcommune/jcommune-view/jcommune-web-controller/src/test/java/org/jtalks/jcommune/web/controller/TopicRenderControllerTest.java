package org.jtalks.jcommune.web.controller;

import org.jtalks.jcommune.model.entity.Topic;
import org.jtalks.jcommune.service.TopicService;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.servlet.ModelAndView;
import org.testng.Assert;
import org.testng.annotations.*;


import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.ModelAndViewAssert.assertViewName;
import static org.springframework.test.web.ModelAndViewAssert.assertModelAttributeValues;
import static org.testng.Assert.assertEquals;


/**
 * Created by IntelliJ IDEA.
 * User: Christoph
 * Date: 02.05.2011
 * Time: 21:03:18
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
public class TopicRenderControllerTest {

    @Mock
    private TopicService topicService;

    private TopicRenderController topicRenderController;

    @BeforeMethod
    public void init(){
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testShowTopic(){
        topicRenderController = new TopicRenderController(topicService);

        Topic topic = new Topic();
        topic.setId(1l);
        topic.setTitle("Simple Title");

        Map<String, Object> topicMap = new HashMap<String, Object>();
        topicMap.put("selectedTopic", topic);
        
        when(topicService.get(1l)).thenReturn(topic);

        ModelAndView mav = topicRenderController.showTopic(1l);

        verify(topicService).get(1l);

        assertModelAttributeValues(mav, topicMap);
        assertViewName(mav, "renderTopic");

    }
}
