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

import org.jtalks.jcommune.model.entity.Post;
import org.jtalks.jcommune.model.entity.Topic;
import org.jtalks.jcommune.service.PostService;
import org.jtalks.jcommune.service.TopicService;
import org.jtalks.jcommune.service.exceptions.NotFoundException;
import org.jtalks.jcommune.web.dto.TopicDto;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.ModelAndViewAssert.*;
import static org.testng.Assert.assertEquals;

/**
 * @author Teterin Alexandre
 * @author Kirill Afonin
 */
public class TopicControllerTest {
    public static final long BRANCH_ID = 1L;
    private final String TOPIC_CONTENT = "content here";
    private final String TOPIC_THEME = "Topic theme";

    private TopicService topicService;
    private PostService postService;
    private TopicController controller;
    public static final long TOPIC_ID = 1;

    @BeforeMethod
    public void init() {
        topicService = mock(TopicService.class);
        postService = mock(PostService.class);
        controller = new TopicController(topicService, postService);
    }


    @Test
    public void testDeleteConfirmPage() {

        ModelAndView actualMav = controller.deleteConfirmPage(TOPIC_ID, BRANCH_ID);

        assertViewName(actualMav, "deleteTopic");
        Map<String, Object> expectedModel = new HashMap<String, Object>();
        expectedModel.put("topicId", TOPIC_ID);
        expectedModel.put("branchId", BRANCH_ID);
        assertModelAttributeValues(actualMav, expectedModel);

    }

    @Test
    public void testDelete() throws NotFoundException {
        ModelAndView actualMav = controller.delete(TOPIC_ID, BRANCH_ID);

        assertViewName(actualMav, "redirect:/branch/" + BRANCH_ID + ".html");
        verify(topicService).deleteTopic(TOPIC_ID);
    }

    @Test
    public void testShow() throws NotFoundException {
        int page = 2;
        int pageSize = 5;
        int startIndex = page * pageSize - pageSize;
        Topic topic = Topic.createNewTopic();
        when(postService.getPostRangeInTopic(TOPIC_ID, startIndex, pageSize)).thenReturn(new ArrayList<Post>());
        when(postService.getPostsInTopicCount(TOPIC_ID)).thenReturn(10);
        when(topicService.get(TOPIC_ID)).thenReturn(topic);

        ModelAndView mav = controller.show(BRANCH_ID, TOPIC_ID, page, pageSize);

        assertViewName(mav, "postList");
        assertModelAttributeAvailable(mav, "posts");
        Topic actualTopic = assertAndReturnModelAttributeOfType(mav, "topic", Topic.class);
        Long actualTopicId = assertAndReturnModelAttributeOfType(mav, "topicId", Long.class);
        Long actualBranchId = assertAndReturnModelAttributeOfType(mav, "branchId", Long.class);
        Integer actualMaxPages = assertAndReturnModelAttributeOfType(mav, "maxPages", Integer.class);
        Integer actualPage = assertAndReturnModelAttributeOfType(mav, "page", Integer.class);
        assertEquals((Topic) actualTopic, topic);
        assertEquals((long) actualTopicId, TOPIC_ID);
        assertEquals((long) actualBranchId, TOPIC_ID);
        assertEquals((int) actualMaxPages, 2);
        assertEquals((int) actualPage, page);
        verify(postService).getPostRangeInTopic(TOPIC_ID, startIndex, pageSize);
        verify(postService).getPostsInTopicCount(TOPIC_ID);
        verify(topicService).get(TOPIC_ID);
    }

    @Test
    public void testCreate() throws Exception {
        Topic topic = Topic.createNewTopic();
        topic.setId(TOPIC_ID);
        when(topicService.createTopic(TOPIC_THEME, TOPIC_CONTENT, BRANCH_ID)).thenReturn(topic);
        TopicDto dto = getDto();
        BindingResult result = mock(BindingResult.class);

        ModelAndView view = controller.create(dto, result, BRANCH_ID);

        assertEquals(view.getViewName(), "redirect:/branch/1/topic/1.html");
        verify(topicService).createTopic(TOPIC_THEME, TOPIC_CONTENT, BRANCH_ID);
    }

    @Test
    public void testCreateValidationFail() throws Exception {
        BindingResult result = mock(BindingResult.class);
        when(result.hasErrors()).thenReturn(true);

        ModelAndView mav = controller.create(getDto(), result, BRANCH_ID);

        assertViewName(mav, "newTopic");
        long branchId = assertAndReturnModelAttributeOfType(mav, "branchId", Long.class);
        assertEquals(branchId, BRANCH_ID);
    }

    @Test
    public void testCreatePage() {
        ModelAndView mav = controller.createPage(BRANCH_ID);

        assertViewName(mav, "newTopic");
        assertModelAttributeAvailable(mav, "topicDto");
        long branchId = assertAndReturnModelAttributeOfType(mav, "branchId", Long.class);
        assertEquals(branchId, BRANCH_ID);
    }

    private TopicDto getDto() {
        TopicDto dto = new TopicDto();
        dto.setBodyText(TOPIC_CONTENT);
        dto.setTopicName(TOPIC_THEME);
        return dto;
    }
}
