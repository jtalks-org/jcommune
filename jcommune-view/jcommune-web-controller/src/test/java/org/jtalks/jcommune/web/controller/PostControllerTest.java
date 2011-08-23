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
import org.jtalks.jcommune.model.entity.User;
import org.jtalks.jcommune.service.PostService;
import org.jtalks.jcommune.service.TopicService;
import org.jtalks.jcommune.service.exceptions.NotFoundException;
import org.jtalks.jcommune.web.dto.Breadcrumb;
import org.jtalks.jcommune.web.dto.BreadcrumbBuilder;
import org.jtalks.jcommune.web.dto.PostDto;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.ModelAndViewAssert.assertAndReturnModelAttributeOfType;
import static org.springframework.test.web.ModelAndViewAssert.assertModelAttributeAvailable;
import static org.springframework.test.web.ModelAndViewAssert.assertModelAttributeValues;
import static org.springframework.test.web.ModelAndViewAssert.assertViewName;
import static org.testng.Assert.assertEquals;

/**
 * This is test for <code>PostController<code> class.
 * Test should cover view resolution and logic validation.
 *
 * @author Osadchuck Eugeny
 */
public class PostControllerTest {
    private TopicService topicService;
    private PostService postService;
    private PostController controller;
    public static final long TOPIC_ID = 1;
    public static final long POST_ID = 1;
    public static final long BRANCH_ID = 1L;
    private final String POST_CONTENT = "postContent";
    private BreadcrumbBuilder breadcrumbBuilder;

    @BeforeMethod
    public void init() {
        topicService = mock(TopicService.class);
        postService = mock(PostService.class);
        breadcrumbBuilder = mock(BreadcrumbBuilder.class);
        controller = new PostController(topicService, postService);
        controller.setBreadcrumbBuilder(breadcrumbBuilder);
    }


    @Test
    public void confirmTest() {
        long topicId = 1;
        long branchId = 1;
        long postId = 5;

        ModelAndView actualMav = controller.deleteConfirmPage(topicId, postId, branchId);

        assertViewName(actualMav, "deletePost");
        Map<String, Object> expectedModel = new HashMap<String, Object>();
        expectedModel.put("topicId", topicId);
        expectedModel.put("postId", postId);
        expectedModel.put("branchId", branchId);
        assertModelAttributeValues(actualMav, expectedModel);

    }

    @Test
    public void deleteTest() throws NotFoundException {
        long topicId = 1;
        long postId = 5;
        long branchId = 1;

        //invoke the object under test
        ModelAndView actualMav = controller.delete(topicId, postId, branchId);

        //check expectations
        verify(topicService, times(1)).deletePost(topicId, postId);

        //check result
        assertViewName(actualMav, "redirect:/topic/" + topicId + ".html");
    }

    @Test
    public void editTest() throws NotFoundException {
        User user = new User("username", "email@mail.com", "password");
        Topic topic = Topic.createNewTopic();
        topic.setId(TOPIC_ID);
        Post post = new Post(user, "content");
        post.setId(POST_ID);
        topic.addPost(post);

        //set expectations
        when(postService.get(POST_ID)).thenReturn(post);
        when(breadcrumbBuilder.getForumBreadcrumb(topic)).thenReturn(new ArrayList<Breadcrumb>());

        //invoke the object under test
        ModelAndView actualMav = controller.edit(BRANCH_ID, TOPIC_ID, POST_ID);

        //check expectations
        verify(postService).get(POST_ID);

        //check result
        assertViewName(actualMav, "postForm");

        PostDto dto = assertAndReturnModelAttributeOfType(actualMav, "postDto", PostDto.class);
        assertEquals(dto.getId(), TOPIC_ID);

        long branchId = assertAndReturnModelAttributeOfType(actualMav, "branchId", Long.class);
        assertEquals(branchId, BRANCH_ID);

        long topicId = assertAndReturnModelAttributeOfType(actualMav, "topicId", Long.class);
        assertEquals(topicId, TOPIC_ID);

        long postId = assertAndReturnModelAttributeOfType(actualMav, "postId", Long.class);
        assertEquals(postId, POST_ID);

        assertModelAttributeAvailable(actualMav, "breadcrumbList");
    }

    @Test
    public void saveTest() throws NotFoundException {
        PostDto dto = getDto();
        BindingResult bindingResult = new BeanPropertyBindingResult(dto, "postDto");

        ModelAndView mav = controller.save(dto, bindingResult, BRANCH_ID, TOPIC_ID, POST_ID);

        assertViewName(mav, "redirect:/branch/" + BRANCH_ID + "/topic/" + TOPIC_ID + ".html");

        verify(topicService).savePost(TOPIC_ID, POST_ID, POST_CONTENT);

    }

    @Test
    public void testSaveWithError() throws NotFoundException {
        PostDto dto = getDto();
        BeanPropertyBindingResult resultWithErrors = mock(BeanPropertyBindingResult.class);

        when(resultWithErrors.hasErrors()).thenReturn(true);

        ModelAndView mav = controller.save(dto, resultWithErrors, BRANCH_ID, TOPIC_ID, POST_ID);

        assertViewName(mav, "postForm");
        long branchId = assertAndReturnModelAttributeOfType(mav, "branchId", Long.class);
        long topicId = assertAndReturnModelAttributeOfType(mav, "topicId", Long.class);
        long postId = assertAndReturnModelAttributeOfType(mav, "postId", Long.class);
        assertEquals(branchId, BRANCH_ID);
        assertEquals(topicId, TOPIC_ID);
        assertEquals(postId, POST_ID);

        verify(topicService, never()).savePost(anyLong(), anyLong(), anyString());
    }

    private PostDto getDto() {
        PostDto dto = new PostDto();
        dto.setId(POST_ID);
        dto.setBodyText(POST_CONTENT);
        return dto;
    }
}
