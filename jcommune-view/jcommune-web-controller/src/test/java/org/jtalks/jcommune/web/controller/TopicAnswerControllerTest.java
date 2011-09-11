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
import org.jtalks.jcommune.service.exceptions.NotFoundException;
import org.jtalks.jcommune.web.dto.Breadcrumb;
import org.jtalks.jcommune.web.dto.BreadcrumbBuilder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.servlet.ModelAndView;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;
import static org.springframework.test.web.ModelAndViewAssert.assertAndReturnModelAttributeOfType;
import static org.springframework.test.web.ModelAndViewAssert.assertModelAttributeAvailable;
import static org.springframework.test.web.ModelAndViewAssert.assertViewName;

/**
 * Tests for {@link TopicAnswerController} actions.
 *
 * @author Pavel Vervenko
 */
public class TopicAnswerControllerTest {

    public static final String ANSWER_BODY = "Body Text";
    public static final String SHORT_ANSWER_BODY = " a  ";
    public static final long TOPIC_ID = 1L;
    private static final long BRANCH_ID = 1L;
    private TopicAnswerController controller;

    @Mock
    private BreadcrumbBuilder breadcrumbBuilder;

    @Mock
    private TopicService topicService;

    @BeforeMethod
    public void init() throws NotFoundException {
        MockitoAnnotations.initMocks(this);
        controller = new TopicAnswerController(topicService, breadcrumbBuilder);
    }

    @Test
    public void testGetAnswerPage() throws NotFoundException {
        boolean isValid = false;
        Topic topic = mock(Topic.class);

        //set expectations
        when(topicService.get(TOPIC_ID)).thenReturn(topic);
        when(breadcrumbBuilder.getForumBreadcrumb(topic)).thenReturn(new ArrayList<Breadcrumb>());

        //invoke the object under test
        ModelAndView mav = controller.getAnswerPage(TOPIC_ID, isValid, BRANCH_ID);

        //check expectations
        verify(topicService).get(TOPIC_ID);
        verify(breadcrumbBuilder).getForumBreadcrumb(topic);

        //check result
        assertAndReturnModelAttributeOfType(mav, "topic", Topic.class);
        assertViewName(mav, "answer");
        assertModelAttributeAvailable(mav, "breadcrumbList");
    }

    @Test
    public void testSubmitAnswerValidationPass() throws NotFoundException {
        //invoke the object under test
        ModelAndView mav = controller.submitAnswer(TOPIC_ID, ANSWER_BODY, BRANCH_ID);

        //check expectations
        verify(topicService).addAnswer(TOPIC_ID, ANSWER_BODY);

        //check result
        assertViewName(mav, "redirect:/topic/" + TOPIC_ID + ".html");
    }

    @Test
    public void testSubmitAnswerValidationFail() throws NotFoundException {
        //invoke the object under test
        ModelAndView mav = controller.submitAnswer(TOPIC_ID, SHORT_ANSWER_BODY, BRANCH_ID);

        //check expectations
        verify(topicService, never()).addAnswer(TOPIC_ID, SHORT_ANSWER_BODY);

        //check result
        assertViewName(mav, "answer");
        assertAndReturnModelAttributeOfType(mav, "validationError", Boolean.class);
    }
}
