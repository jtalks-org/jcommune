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

import org.jtalks.jcommune.service.TopicService;
import org.jtalks.jcommune.web.dto.TopicDto;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.ModelAndViewAssert.assertAndReturnModelAttributeOfType;
import static org.springframework.test.web.ModelAndViewAssert.assertViewName;
import static org.testng.Assert.assertEquals;

/**
 * Tests for {@link NewTopicController} actions.
 *
 * @author Kirill Afonin
 */
public class NewTopicControllerTest {
    private final String TOPIC_CONTENT = "content here";
    private final String TOPIC_THEME = "Topic theme";
    private NewTopicController controller;
    @Mock
    private TopicService topicService;

    @BeforeMethod
    public void init() {
        MockitoAnnotations.initMocks(this);
        controller = new NewTopicController(topicService);
    }


    @Test
    public void testSubmitNewTopic() throws Exception {
        TopicDto dto = getDto();
        BindingResult result = new BeanPropertyBindingResult(dto, "topicDto");

        String view = controller.submitNewTopic(dto, result, 1l);

        assertEquals(view, "redirect:/branch/1.html");
        verify(topicService, times(1)).createTopic(TOPIC_THEME, TOPIC_CONTENT, 1l);
    }

    @Test
    public void testGetNewTopicPage() {

        ModelAndView mav = controller.getNewTopicPage(1l);

        assertAndReturnModelAttributeOfType(mav, "topicDto", TopicDto.class);
        Long branchId = assertAndReturnModelAttributeOfType(mav, "branchId", Long.class);
        assertEquals(branchId, new Long(1));
        assertViewName(mav, "newTopic");
    }

    private TopicDto getDto() {
        TopicDto dto = new TopicDto();
        dto.setBodyText(TOPIC_CONTENT);
        dto.setTopicName(TOPIC_THEME);
        return dto;
    }
}
