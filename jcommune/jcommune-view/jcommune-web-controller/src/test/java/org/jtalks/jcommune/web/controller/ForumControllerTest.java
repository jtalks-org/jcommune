/**
 * JTalks for uniting people
 * Copyright (C) 2011  JavaTalks Team     *
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
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.ModelAndViewAssert.assertViewName;
import static org.testng.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;


public class ForumControllerTest {
    private TopicService topicService;
    private ForumController forumController;

    @BeforeMethod
    public void init() {
        topicService = mock(TopicService.class);
        forumController = new ForumController(topicService);
    }

    @Test
    public void testShowAllTopics() {
        ModelAndView mav = forumController.showAllTopics();
        assertViewName(mav, "forum");
    }

    @Test
    public void testPostPage() {
        ModelAndView mav = forumController.postPage();
        assertViewName(mav, "newTopic");
    }

    @Test
    public void testPopulateForm() {
        forumController = new ForumController(topicService);

        Topic firstTopic = new Topic();
        firstTopic.setTitle("1");

        Topic secondTopic = new Topic();
        secondTopic.setTitle("2");

        List<Topic> topics = new ArrayList<Topic>();
        topics.add(firstTopic);
        topics.add(secondTopic);

        when(topicService.getAll()).thenReturn(topics);

        List<Topic> returnedList = forumController.populateForum();
        verify(topicService).getAll();

        assertEquals(topics.get(0), returnedList.get(0));
        assertEquals(topics.get(1), returnedList.get(1));

    }
}
