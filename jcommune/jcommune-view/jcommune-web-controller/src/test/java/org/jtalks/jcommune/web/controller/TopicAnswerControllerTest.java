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
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.servlet.ModelAndView;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import static org.springframework.test.web.ModelAndViewAssert.assertViewName;
import static org.springframework.test.web.ModelAndViewAssert.assertAndReturnModelAttributeOfType;
import static org.mockito.Mockito.*;

/**
 * Tests for {@link TopicAnswerController} actions.
 * 
 * @author Pavel Vervenko
 */
public class TopicAnswerControllerTest {

    public static final String ANSWER_BODY = "Body Text";
    public static final String SHORT_ANSWER_BODY = " a  ";
    public static final long TOPIC_ID = 1L;
    private TopicAnswerController controller;
    @Mock
    private TopicService topicService;

    @BeforeMethod
    public void init() {
        MockitoAnnotations.initMocks(this);
        controller = new TopicAnswerController(topicService);
        when(topicService.get(TOPIC_ID)).thenReturn(new Topic());
    }

    @Test
    public void testGetAnswerPage() {
        ModelAndView mav = controller.getAnswerPage(TOPIC_ID, false);

        assertAndReturnModelAttributeOfType(mav, "topic", Topic.class);
        assertViewName(mav, "answer");

        verify(topicService, times(1)).get(TOPIC_ID);
    }

    @Test
    public void testSubmitAnswer() {
        ModelAndView mav = controller.submitAnswer(TOPIC_ID, ANSWER_BODY);

        assertViewName(mav, "redirect:/topics/" + TOPIC_ID + ".html");

        verify(topicService, times(1)).addAnswer(TOPIC_ID, ANSWER_BODY);
    }
    
    @Test
    public void testSubmitShortAnswer() {
        ModelAndView mav = controller.submitAnswer(TOPIC_ID, SHORT_ANSWER_BODY);

        assertAndReturnModelAttributeOfType(mav, "validationError", Boolean.class);   
        assertViewName(mav, "answer");
        
        verify(topicService, times(0)).addAnswer(anyLong(), anyString());
    }
}
