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

import org.jtalks.jcommune.model.entity.Post;
import org.jtalks.jcommune.model.entity.Topic;
import org.jtalks.jcommune.model.entity.User;
import org.jtalks.jcommune.service.PostService;
import org.jtalks.jcommune.service.SecurityService;
import org.jtalks.jcommune.service.TopicService;
import org.jtalks.jcommune.service.exceptions.NotFoundException;
import org.jtalks.jcommune.web.dto.Breadcrumb;
import org.jtalks.jcommune.web.dto.BreadcrumbBuilder;
import org.jtalks.jcommune.web.dto.PostDto;
import org.mockito.Matchers;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.servlet.ModelAndView;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;

import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.ModelAndViewAssert.*;
import static org.testng.Assert.assertEquals;

/**
 * This is test for <code>PostController<code> class.
 * Test should cover view resolution and logic validation.
 *
 * @author Osadchuck Eugeny
 * @author Evgeniy Naumenko
 */
public class PostControllerTest {
    private PostService postService;
    private PostController controller;
    private TopicService topicService;

    public static final long POST_ID = 1;
    public static final long TOPIC_ID = 1L;
    public static final long PAGE = 1L;
    private final String POST_CONTENT = "postContent";
    private BreadcrumbBuilder breadcrumbBuilder;
    private Topic topic;

    @BeforeMethod
    public void init() throws NotFoundException {
        postService = mock(PostService.class);
        topicService = mock(TopicService.class);
        breadcrumbBuilder = mock(BreadcrumbBuilder.class);
        controller = new PostController(postService, breadcrumbBuilder, topicService);

        when(topicService.get(TOPIC_ID)).thenReturn(topic);
        when(breadcrumbBuilder.getForumBreadcrumb(topic)).thenReturn(new ArrayList<Breadcrumb>());
        topic = new Topic(null, "");
    }

    @Test
    public void testInitBinder() {
        WebDataBinder binder = mock(WebDataBinder.class);
        controller.initBinder(binder);
        verify(binder).registerCustomEditor(eq(String.class), any(StringTrimmerEditor.class));
    }

    @Test
    public void testDeletePost() throws NotFoundException {
        Post post = new Post(null, null);
        topic.setId(TOPIC_ID);
        topic.addPost(post);
        when(postService.get(Matchers.<Long>any())).thenReturn(post);
        //invoke the object under test
        String view = controller.delete(POST_ID);

        //check expectations
        verify(postService).deletePost(POST_ID);

        //check result
        assertEquals(view, "redirect:/topics/" + TOPIC_ID);
    }

    @Test(expectedExceptions = NotFoundException.class)
    public void testDeleteUnexistingPost() throws NotFoundException {
        doThrow(new NotFoundException()).when(postService).deletePost(POST_ID);
        controller.delete(POST_ID);
    }

    @Test
    public void editPost() throws NotFoundException {
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
        ModelAndView actualMav = controller.editPage(TOPIC_ID, POST_ID, PAGE);

        //check expectations
        verify(postService).get(POST_ID);

        //check result
        this.assertEditPostFormMavIsCorrect(actualMav);

        PostDto dto = assertAndReturnModelAttributeOfType(actualMav, "postDto", PostDto.class);
        assertEquals(dto.getId(), TOPIC_ID);

        assertModelAttributeAvailable(actualMav, "breadcrumbList");
    }

    @Test
    public void testUpdatePost() throws NotFoundException {
        PostDto dto = getDto();
        BindingResult bindingResult = new BeanPropertyBindingResult(dto, "postDto");
        ModelAndView mav = controller.update(dto, bindingResult, TOPIC_ID, PAGE, POST_ID);
        assertViewName(mav, "redirect:/posts/" + dto.getId());
        verify(postService).updatePost(POST_ID, POST_CONTENT);
    }

    @Test
    public void updateWithError() throws NotFoundException {
        PostDto dto = this.getDto();
        BeanPropertyBindingResult resultWithErrors = mock(BeanPropertyBindingResult.class);

        when(resultWithErrors.hasErrors()).thenReturn(true);

        ModelAndView mav = controller.update(dto, resultWithErrors, TOPIC_ID, PAGE, POST_ID);

        this.assertEditPostFormMavIsCorrect(mav);

        verify(postService, never()).updatePost(anyLong(), anyString());
    }

    @Test
    public void testAnswer() throws NotFoundException {
        //invoke the object under test
        ModelAndView mav = controller.addPost(TOPIC_ID);

        //check expectations
        verify(topicService).get(TOPIC_ID);
        verify(breadcrumbBuilder).getForumBreadcrumb(Matchers.<Topic>any());

        //check result
        this.assertAnswerMavIsCorrect(mav);
    }

    @Test(expectedExceptions = NotFoundException.class)
    public void testAnswerForUnexistingTopic() throws NotFoundException {
        doThrow(new NotFoundException()).when(topicService).get(TOPIC_ID);
        controller.addPost(TOPIC_ID);
    }

    @Test
    public void testQuotedAnswer() throws NotFoundException {
        User user = new User("user", null, null);
        Post post = new Post(user, POST_CONTENT);
        topic.addPost(post);
        when(postService.get(anyLong())).thenReturn(post);

        ModelAndView mav = controller.addPostWithQuote(post.getId(), null);
        //check expectations
        String expected = "[quote=\"user\"]" + POST_CONTENT + "[/quote]";
        PostDto actual = assertAndReturnModelAttributeOfType(mav, "postDto", PostDto.class);
        assertEquals(actual.getBodyText(), expected);
    }

    @Test
    public void testPartialQuotedAnswer() throws NotFoundException {
        String selection = "selected content";
        User user = new User("user", null, null);
        Post post = new Post(user, POST_CONTENT);
        topic.addPost(post);
        when(postService.get(anyLong())).thenReturn(post);

        ModelAndView mav = controller.addPostWithQuote(TOPIC_ID, selection);
        //check expectations
        String expected = "[quote=\"user\"]" + selection + "[/quote]";
        PostDto actual = assertAndReturnModelAttributeOfType(mav, "postDto", PostDto.class);
        assertEquals(actual.getBodyText(), expected);
    }

    @Test(expectedExceptions = NotFoundException.class)
    public void testQuotedAnswerForUnexistingTopic() throws NotFoundException {
        doThrow(new NotFoundException()).when(postService).get(anyLong());
        controller.addPostWithQuote(TOPIC_ID, "");
    }

    @Test
    public void testSubmitAnswerValidationPass() throws NotFoundException {
        BeanPropertyBindingResult resultWithoutErrors = mock(BeanPropertyBindingResult.class);
        when(resultWithoutErrors.hasErrors()).thenReturn(false);
        Post post = new Post(null, null);
        topic.addPost(post);
        topic.setId(TOPIC_ID);
        when(topicService.replyToTopic(anyLong(), Matchers.<String>any())).thenReturn(post);
        when(postService.getPageForPost(post)).thenReturn(1);
        when(postService.get(Matchers.<Long>any())).thenReturn(post);
        //invoke the object under test
        ModelAndView mav = controller.create(getDto(), resultWithoutErrors);

        //check expectations
        verify(topicService).replyToTopic(TOPIC_ID, POST_CONTENT);

        //check result
        assertViewName(mav, "redirect:/topics/" + TOPIC_ID + "?page=1#0");
    }

    @Test
    public void testSubmitAnswerValidationFail() throws NotFoundException {
        BeanPropertyBindingResult resultWithErrors = mock(BeanPropertyBindingResult.class);
        when(resultWithErrors.hasErrors()).thenReturn(true);
        //invoke the object under test
        ModelAndView mav = controller.create(getDto(), resultWithErrors);

        //check expectations
        verify(topicService, never()).replyToTopic(anyLong(), anyString());

        //check result
        assertViewName(mav, "answer");
    }

    @Test
    public void testRedirectToPageWithPost() throws NotFoundException {
        Post post = new Post(null, null);
        topic.addPost(post);
        topic.setId(TOPIC_ID);
        when(postService.getPageForPost(post)).thenReturn(5);
        when(postService.get(POST_ID)).thenReturn(post);

        String result = controller.redirectToPageWithPost(POST_ID);

        assertEquals(result, "redirect:/topics/" + TOPIC_ID + "?page=5#" + POST_ID);
    }

    @Test(expectedExceptions = NotFoundException.class)
    public void testRedirectToPageWithPostNotFound() throws NotFoundException {
        doThrow(new NotFoundException()).when(postService).get(anyLong());

        controller.redirectToPageWithPost(POST_ID);
    }

    private void assertAnswerMavIsCorrect(ModelAndView mav) {
        assertViewName(mav, "answer");
        assertAndReturnModelAttributeOfType(mav, "topic", Topic.class);
        long topicId = assertAndReturnModelAttributeOfType(mav, "topicId", Long.class);
        assertEquals(topicId, TOPIC_ID);
        PostDto dto = assertAndReturnModelAttributeOfType(mav, "postDto", PostDto.class);
        assertEquals(dto.getId(), 0);
        assertModelAttributeAvailable(mav, "breadcrumbList");
    }

    private void assertEditPostFormMavIsCorrect(ModelAndView mav) {
        assertViewName(mav, "editForm");
        long topicId = assertAndReturnModelAttributeOfType(mav, "topicId", Long.class);
        long postId = assertAndReturnModelAttributeOfType(mav, "postId", Long.class);
        assertEquals(topicId, TOPIC_ID);
        assertEquals(postId, POST_ID);
    }

    private PostDto getDto() {
        PostDto dto = new PostDto();
        dto.setId(POST_ID);
        dto.setBodyText(POST_CONTENT);
        dto.setTopicId(TOPIC_ID);
        return dto;
    }
}
