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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.jtalks.jcommune.model.entity.Post;
import org.jtalks.jcommune.model.entity.Topic;
import org.jtalks.jcommune.service.PostService;
import org.jtalks.jcommune.service.TopicService;
import org.jtalks.jcommune.service.exceptions.NotFoundException;
import org.jtalks.jcommune.web.dto.TopicDto;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.ModelAndViewAssert.assertAndReturnModelAttributeOfType;
import static org.springframework.test.web.ModelAndViewAssert.assertViewName;
import static org.springframework.test.web.ModelAndViewAssert.assertModelAttributeValues;
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

    @BeforeMethod
    public void init() {
        topicService = mock(TopicService.class);
        postService = mock(PostService.class);
        controller = new TopicController(topicService, postService);
    }


    @Test
    public void testDeleteConfirmPage() {
        long topicId = 1;

        ModelAndView actualMav = controller.deleteConfirmPage(topicId, 1L);

        assertViewName(actualMav, "delete");
        Map<String, Object> expectedModel = new HashMap<String, Object>();
        expectedModel.put("topicId", topicId);
        expectedModel.put("branchId", 1L);
        assertModelAttributeValues(actualMav, expectedModel);

    }

    @Test
    public void testDelete() throws NotFoundException {
        long topicId = 1L;
        long branchId = 1L;

        ModelAndView actualMav = controller.delete(topicId, branchId);

        assertViewName(actualMav, "redirect:/branch/" + branchId + ".html");
        verify(topicService, times(1)).delete(topicId);
    }

    @Test
    public void testShow() throws NotFoundException {
        long topicId = 1L;
        long branchId = 1L;
        int page = 2;
        int size = 5;
        int start = page * size - size;
        Topic topic = Topic.createNewTopic();
        topic.setTitle("title");
        when(postService.getPostRangeInTopic(topicId, start, size)).thenReturn(new ArrayList<Post>());
        when(postService.getPostsInTopicCount(topicId)).thenReturn(10);
        when(topicService.get(topicId)).thenReturn(topic);

        ModelAndView mav = controller.show(branchId, topicId, page, size);

        assertViewName(mav, "postList");
        assertAndReturnModelAttributeOfType(mav, "posts", List.class);
        String title = assertAndReturnModelAttributeOfType(mav, "topicTitle", String.class);
        assertEquals(title, "title");
        Long _topic = assertAndReturnModelAttributeOfType(mav, "topicId", Long.class);
        assertEquals((long) _topic, topicId);
        Long _branch = assertAndReturnModelAttributeOfType(mav, "branchId", Long.class);
        assertEquals((long) _branch, branchId);
        Integer _maxPages = assertAndReturnModelAttributeOfType(mav, "maxPages", Integer.class);
        Integer _page = assertAndReturnModelAttributeOfType(mav, "page", Integer.class);
        assertEquals((int) _maxPages, 2);
        assertEquals((int) _page, page);
        verify(postService, times(1)).getPostRangeInTopic(topicId, start, size);
        verify(postService, times(1)).getPostsInTopicCount(topicId);
        verify(topicService, times(1)).get(topicId);
    }

    @Test
    public void testCreate() throws Exception {
        Topic topic = Topic.createNewTopic();
        topic.setId(1L);
        when(topicService.createTopic(TOPIC_THEME, TOPIC_CONTENT, BRANCH_ID)).thenReturn(topic);
        TopicDto dto = getDto();
        BindingResult result = mock(BindingResult.class);

        ModelAndView view = controller.create(dto, result, BRANCH_ID);

        assertEquals(view.getViewName(), "redirect:/branch/1/topic/1.html");
        verify(topicService, times(1)).createTopic(TOPIC_THEME, TOPIC_CONTENT, BRANCH_ID);
    }

    @Test
    public void testCreateValidationFail() throws Exception {
        Long expectedBranchId = 1L;
        BindingResult result = mock(BindingResult.class);
        when(result.hasErrors()).thenReturn(true);

        ModelAndView mav = controller.create(getDto(), result, expectedBranchId);

        assertViewName(mav, "newTopic");
        Long branchId = assertAndReturnModelAttributeOfType(mav, "branchId", Long.class);
        assertEquals(branchId, expectedBranchId);
    }

    @Test
    public void testCreatePage() {
        ModelAndView mav = controller.createPage(1L);

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
