/**
 * Copyright (C) 2011  JTalks.org Team
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
 */
package org.jtalks.jcommune.web.controller;

import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.ModelAndViewAssert.assertAndReturnModelAttributeOfType;
import static org.springframework.test.web.ModelAndViewAssert.assertModelAttributeAvailable;
import static org.springframework.test.web.ModelAndViewAssert.assertModelAttributeValues;
import static org.springframework.test.web.ModelAndViewAssert.assertViewName;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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

/**
 * This is test for <code>PostController<code> class.
 * Test should cover view resolution and logic validation.
 *
 * @author Osadchuck Eugeny
 */
public class PostControllerTest {
    private PostService postService;
    private PostController controller;
    private TopicService topicService;

    public static final long POST_ID = 1;
    public static final String ANSWER_BODY = "Body Text";
    public static final String SHORT_ANSWER_BODY = " a  ";
    public static final long TOPIC_ID = 1L;
    private final String POST_CONTENT = "postContent";
    private BreadcrumbBuilder breadcrumbBuilder = new BreadcrumbBuilder();

    @BeforeMethod
    public void init() {
        postService = mock(PostService.class);
        topicService = mock(TopicService.class);
        breadcrumbBuilder = mock(BreadcrumbBuilder.class);
        controller = new PostController(postService, breadcrumbBuilder, topicService);
    }


    @Test
    public void deleteConfirmPage() {
        long topicId = 1;
        long postId = 5;

        ModelAndView actualMav = controller.deleteConfirmPage(topicId, postId);

        assertViewName(actualMav, "deletePost");
        Map<String, Object> expectedModel = new HashMap<String, Object>();
        expectedModel.put("topicId", topicId);
        expectedModel.put("postId", postId);
        assertModelAttributeValues(actualMav, expectedModel);

    }

    @Test
    public void delete() throws NotFoundException {
        long topicId = 1;
        long postId = 5;

        //invoke the object under test
        ModelAndView actualMav = controller.delete(topicId, postId);

        //check expectations
        verify(postService).deletePost(postId);

        //check result
        assertViewName(actualMav, "redirect:/topics/" + topicId);
    }

    @Test
    public void editPage() throws NotFoundException {
        User user = new User("username", "email@mail.com", "password");
        Topic topic = new Topic(user, "title");
        topic.setId(TOPIC_ID);
        Post post = new Post(user, "content");
        post.setId(POST_ID);
        topic.addPost(post);

        //set expectations
        when(postService.get(POST_ID)).thenReturn(post);
        when(breadcrumbBuilder.getForumBreadcrumb(topic)).thenReturn(new ArrayList<Breadcrumb>());

        //invoke the object under test
        ModelAndView actualMav = controller.editPage(TOPIC_ID, POST_ID);

        //check expectations
        verify(postService).get(POST_ID);

        //check result
        assertViewName(actualMav, "postForm");

        PostDto dto = assertAndReturnModelAttributeOfType(actualMav, "postDto", PostDto.class);
        assertEquals(dto.getId(), TOPIC_ID);

        long topicId = assertAndReturnModelAttributeOfType(actualMav, "topicId", Long.class);
        assertEquals(topicId, TOPIC_ID);

        long postId = assertAndReturnModelAttributeOfType(actualMav, "postId", Long.class);
        assertEquals(postId, POST_ID);

        assertModelAttributeAvailable(actualMav, "breadcrumbList");
    }

    @Test
    public void update() throws NotFoundException {
        PostDto dto = getDto();
        BindingResult bindingResult = new BeanPropertyBindingResult(dto, "postDto");

        ModelAndView mav = controller.update(dto, bindingResult, TOPIC_ID, POST_ID);
        assertViewName(mav, "redirect:/topics/" + TOPIC_ID);

        verify(postService).updatePost(POST_ID, POST_CONTENT);

    }

    @Test
    public void updateWithError() throws NotFoundException {
        PostDto dto = getDto();
        BeanPropertyBindingResult resultWithErrors = mock(BeanPropertyBindingResult.class);

        when(resultWithErrors.hasErrors()).thenReturn(true);

        ModelAndView mav = controller.update(dto, resultWithErrors, TOPIC_ID, POST_ID);

        assertViewName(mav, "postForm");
        long topicId = assertAndReturnModelAttributeOfType(mav, "topicId", Long.class);
        long postId = assertAndReturnModelAttributeOfType(mav, "postId", Long.class);
        assertEquals(topicId, TOPIC_ID);
        assertEquals(postId, POST_ID);

        verify(postService, never()).updatePost(anyLong(), anyString());
    }

    @Test
    public void createPage() throws NotFoundException {
        Topic topic = mock(Topic.class);

        //set expectations
        when(topicService.get(TOPIC_ID)).thenReturn(topic);
        when(breadcrumbBuilder.getForumBreadcrumb(topic)).thenReturn(new ArrayList<Breadcrumb>());

        //invoke the object under test
        ModelAndView mav = controller.createPage(TOPIC_ID);

        //check expectations
        verify(topicService).get(TOPIC_ID);
        verify(breadcrumbBuilder).getForumBreadcrumb(topic);

        //check result
        assertViewName(mav, "answer");
        assertAndReturnModelAttributeOfType(mav, "topic", Topic.class);
        long topicId = assertAndReturnModelAttributeOfType(mav, "topicId", Long.class);
        assertEquals(topicId, TOPIC_ID);
        PostDto dto = assertAndReturnModelAttributeOfType(mav, "postDto", PostDto.class);
        assertEquals(dto.getId(), 0);
        assertNull(dto.getBodyText());
        assertModelAttributeAvailable(mav, "breadcrumbList");
    }

    @Test
    public void testSubmitAnswerValidationPass() throws NotFoundException {
        BeanPropertyBindingResult resultWithoutErrors = mock(BeanPropertyBindingResult.class);
        when(resultWithoutErrors.hasErrors()).thenReturn(false);

        //invoke the object under test
        String view = controller.create(getDto(), resultWithoutErrors);

        //check expectations
        verify(topicService).replyToTopic(TOPIC_ID, POST_CONTENT);

        //check result
        assertEquals(view, "redirect:/topics/" + TOPIC_ID);
    }

    @Test
    public void testSubmitAnswerValidationFail() throws NotFoundException {
        BeanPropertyBindingResult resultWithErrors = mock(BeanPropertyBindingResult.class);
        when(resultWithErrors.hasErrors()).thenReturn(true);

        //invoke the object under test
        String view = controller.create(getDto(), resultWithErrors);

        //check expectations
        verify(topicService, never()).replyToTopic(anyLong(), anyString());

        //check result
        assertEquals(view, "answer");
    }

    private PostDto getDto() {
        PostDto dto = new PostDto();
        dto.setId(POST_ID);
        dto.setBodyText(POST_CONTENT);
        dto.setTopicId(TOPIC_ID);
        return dto;
    }
}
