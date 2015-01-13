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
package org.jtalks.jcommune.plugin.questionsandanswers.controller;

import org.apache.velocity.app.VelocityEngine;
import org.jtalks.common.model.entity.Entity;
import org.jtalks.jcommune.model.entity.Branch;
import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.model.entity.Post;
import org.jtalks.jcommune.model.entity.Topic;
import org.jtalks.jcommune.plugin.api.exceptions.NotFoundException;
import org.jtalks.jcommune.plugin.api.service.*;
import org.jtalks.jcommune.plugin.api.web.dto.PostDto;
import org.jtalks.jcommune.plugin.api.web.dto.TopicDto;
import org.jtalks.jcommune.plugin.api.web.util.BreadcrumbBuilder;
import org.jtalks.jcommune.plugin.questionsandanswers.QuestionsAndAnswersPlugin;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.context.ApplicationContext;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.Date;
import java.util.Properties;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.testng.Assert.assertEquals;

/**
 * @author Mikhail Stryzhonok
 */
public class QuestionsAndAnswersControllerTest {
    @Mock
    private PluginBranchService branchService;
    @Mock
    private PluginLastReadPostService lastReadPostService;
    @Mock
    private TypeAwarePluginTopicService topicService;
    @Mock
    private PluginPostService postService;
    @Mock
    private HttpServletRequest request;
    @Mock
    private UserReader userReader;
    @Mock
    private ApplicationContext context;
    @Mock
    private BreadcrumbBuilder breadcrumbBuilder;
    @Mock
    private BindingResult result;
    @Mock
    private PluginLocationService locationService;
    @Spy
    private QuestionsAndAnswersController controller = new QuestionsAndAnswersController();
    private String content = "some html";
    private String answerContent = "some answer";

    @BeforeMethod
    public void init() {
        initMocks(this);
        when(controller.getPluginBranchService()).thenReturn(branchService);
        when(controller.getPluginLastReadPostService()).thenReturn(lastReadPostService);
        when(controller.getTypeAwarePluginTopicService()).thenReturn(topicService);
        when(controller.getPluginPostService()).thenReturn(postService);
        when(controller.getProperties()).thenReturn(new Properties());
        when(controller.getUserReader()).thenReturn(userReader);
        when(controller.getLocationService()).thenReturn(locationService);
        when(userReader.getCurrentUser()).thenReturn(new JCUser("name", "example@mail.ru", "pwd"));
        controller.setApplicationContext(context);
        controller.setBreadcrumbBuilder(breadcrumbBuilder);
        when(breadcrumbBuilder.getForumBreadcrumb()).thenReturn(Collections.EMPTY_LIST);
        when(locationService.getUsersViewing(any(Entity.class))).thenReturn(Collections.EMPTY_LIST);
        doReturn(content).when(controller).getMergedTemplate(any(VelocityEngine.class), anyString(),
                anyString(), anyMap());
    }

    @Test
    public void showNewQuestionPageTest() throws Exception {
        Branch branch = new Branch("name", "description");
        Model model = new ExtendedModelMap();
        when(branchService.get(anyLong())).thenReturn(branch);

        String result = controller.showNewQuestionPage(1L, model, request);

        assertEquals(result, QuestionsAndAnswersController.PLUGIN_VIEW_NAME);
        assertEquals(model.asMap().get(QuestionsAndAnswersController.CONTENT), content);
    }

    @Test(expectedExceptions = NotFoundException.class)
    public void showNewQuestionPageShouldThrowExceptionIfBranchNotFound() throws Exception {
        when(branchService.get(anyLong())).thenThrow(new NotFoundException());

        controller.showNewQuestionPage(1L, new ExtendedModelMap(), request);
    }

    @Test
    public void createQuestionSuccessTest() throws Exception {
        Branch branch = new Branch("name", "description");
        Topic createdQuestion = new Topic();
        createdQuestion.setId(1);
        TopicDto topicDto = new TopicDto(new Topic());
        topicDto.setBodyText("text");
        Model model = new ExtendedModelMap();

        when(branchService.get(anyLong())).thenReturn(branch);
        when(result.hasErrors()).thenReturn(false);
        when(topicService.createTopic(topicDto.getTopic(), "text")).thenReturn(createdQuestion);

        String actual = controller.createQuestion(topicDto, result , model, 1L, request);

        assertEquals(actual, "redirect:" + QuestionsAndAnswersPlugin.CONTEXT + "/" + createdQuestion.getId());
    }

    @Test
    public void createQuestionValidationErrorsTest() throws Exception {
        Branch branch = new Branch("name", "description");
        Topic createdQuestion = new Topic();
        createdQuestion.setId(1);
        TopicDto topicDto = new TopicDto(new Topic());
        topicDto.setBodyText("text");
        Model model = new ExtendedModelMap();

        when(result.hasErrors()).thenReturn(true);
        when(branchService.get(anyLong())).thenReturn(branch);

        String actual = controller.createQuestion(topicDto, result , model, 1L, request);

        assertEquals(actual, QuestionsAndAnswersController.PLUGIN_VIEW_NAME);
        assertEquals(model.asMap().get(QuestionsAndAnswersController.CONTENT), content);
    }

    @Test(expectedExceptions = NotFoundException.class)
    public void createQuestionShouldThrowExceptionIfBranchNotFound() throws Exception {
        when(branchService.get(anyLong())).thenThrow(new NotFoundException());
        controller.createQuestion(new TopicDto(), result, new ExtendedModelMap(), 1L, request);
    }

    @Test
    public void showQuestionSuccessTest() throws Exception {
        Branch branch = new Branch("name", "description");
        Topic topic = new Topic();
        topic.setBranch(branch);
        Model model = new ExtendedModelMap();

        when(topicService.get(anyLong(), anyString())).thenReturn(topic);

        String result = controller.showQuestion(request, model, 1L);

        assertEquals(result, QuestionsAndAnswersController.PLUGIN_VIEW_NAME);
        assertEquals(model.asMap().get(QuestionsAndAnswersController.CONTENT), content);
        verify(topicService).get(1L, QuestionsAndAnswersPlugin.TOPIC_TYPE);
    }

    @Test(expectedExceptions = NotFoundException.class)
    public void showQuestionShouldThrowExceptionIfQuestionNotFound() throws NotFoundException {
        when(topicService.get(anyLong(), anyString())).thenThrow(new NotFoundException());

        controller.showQuestion(request, new ExtendedModelMap(), 1L);
    }

    @Test(expectedExceptions = AccessDeniedException.class)
    public void showQuestionShouldThrowExceptionIfUserRestrictedToViewTopicsInBranch() throws Exception {
        Branch branch = new Branch("name", "description");
        branch.setId(1);
        Topic topic = new Topic();
        topic.setBranch(branch);

        when(topicService.get(anyLong(), anyString())).thenReturn(topic);
        doThrow(new AccessDeniedException("")).when(topicService).checkViewTopicPermission(branch.getId());

        controller.showQuestion(request, new ExtendedModelMap(), 1L);
    }

    @Test
    public void editQuestionPageSuccessTest() throws Exception {
        Model model = new ExtendedModelMap();

        when(topicService.get(anyLong(),eq(QuestionsAndAnswersPlugin.TOPIC_TYPE))).thenReturn(new Topic());

        String result = controller.editQuestionPage(request, model, 1L);

        assertEquals(result, QuestionsAndAnswersController.PLUGIN_VIEW_NAME);
        assertEquals(model.asMap().get(QuestionsAndAnswersController.CONTENT), content);
        verify(topicService).get(1L, QuestionsAndAnswersPlugin.TOPIC_TYPE);
    }

    @Test(expectedExceptions = NotFoundException.class)
    public void editQuestionPageShouldThrowExceptionIfQuestionNotFound() throws Exception {
        when(topicService.get(anyLong(),eq(QuestionsAndAnswersPlugin.TOPIC_TYPE))).thenThrow(new NotFoundException());

        controller.editQuestionPage(request, new ExtendedModelMap(), 1L);
    }

    @Test
    public void updateQuestionSuccessTest() throws Exception{
        Topic createdQuestion = new Topic();
        createdQuestion.setId(1);
        TopicDto topicDto = new TopicDto(new Topic());
        topicDto.setBodyText("text");
        Topic topicWithPost = new Topic();
        topicWithPost.setId(1L);
        topicWithPost.addPost(new Post(new JCUser("name", "mail@.ex.com", "pwd"), "text"));
        Long topicId = 1L;
        Model model = new ExtendedModelMap();

        when(result.hasErrors()).thenReturn(false);
        when(topicService.get(eq(topicId), anyString())).thenReturn(topicWithPost);

        String actual = controller.updateQuestion(topicDto, result, model, topicId, request);

        assertEquals(actual, "redirect:" + QuestionsAndAnswersPlugin.CONTEXT + "/" + topicWithPost.getId());
        verify(topicService).updateTopic(topicWithPost);
    }

    @Test
    public void updateQuestionValidationErrorsTest() throws Exception {
        Model model = new ExtendedModelMap();

        when(result.hasErrors()).thenReturn(true);
        when(topicService.get(anyLong(), anyString())).thenReturn(new Topic());

        String actual = controller.updateQuestion(new TopicDto(new Topic()), result, model, 1L, request);

        assertEquals(actual, QuestionsAndAnswersController.PLUGIN_VIEW_NAME);
        assertEquals(model.asMap().get(QuestionsAndAnswersController.CONTENT), content);
    }

    @Test(expectedExceptions = NotFoundException.class)
    public void updateQuestionShouldThrowExceptionIfQuestionNotFound() throws Exception{
        when(topicService.get(anyLong(), anyString())).thenThrow(new NotFoundException());

        controller.updateQuestion(new TopicDto(new Topic()), result, new ExtendedModelMap(), 1L, request);
    }

    @Test
    public void testGetIcon() throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();

        controller.getIcon(request, response, "read.png");

        assertEquals(response.getContentType(), "image/png");
        assertEquals(response.getHeader("Pragma"), "public");
        assertEquals(response.getHeader("Cache-Control"), "public");
    }

    @Test
    public void getIconShouldSetNotModifiedStatusIfRequestHaveIfModifiedSinceHeader() {
        MockHttpServletResponse response = new MockHttpServletResponse();
        when(request.getHeader("If-Modified-Since")).thenReturn((new Date()).toString());

        controller.getIcon(request, response,  "read.png");

        assertEquals(response.getStatus(), HttpServletResponse.SC_NOT_MODIFIED);
    }

    @Test
    public void getIconShouldSetNotFoundStatusForIncorrectIconName() {
        MockHttpServletResponse response = new MockHttpServletResponse();

        controller.getIcon(request, response,  "incorrect");

        assertEquals(response.getStatus(), HttpServletResponse.SC_NOT_FOUND);
    }

    @Test
    public void testOpenQuestionSuccess() throws Exception {
        Long id = 1L;
        Topic topic = new Topic();
        topic.setId(id);

        when(topicService.get(id, QuestionsAndAnswersPlugin.TOPIC_TYPE)).thenReturn(topic);

        String result = controller.openQuestion(id);

        assertEquals(result, "redirect:" + QuestionsAndAnswersPlugin.CONTEXT + "/" + topic.getId());
        verify(topicService).openTopic(topic);
    }

    @Test(expectedExceptions = NotFoundException.class)
    public void openQuestionShouldThrowExceptionWhenQuestionNotFound() throws Exception {
        when(topicService.get(anyLong(), anyString())).thenThrow(new NotFoundException());

        controller.openQuestion(1L);
    }

    @Test
    public void testCloseQuestionSuccess() throws Exception {
        Long id = 1L;
        Topic topic = new Topic();
        topic.setId(id);

        when(topicService.get(id, QuestionsAndAnswersPlugin.TOPIC_TYPE)).thenReturn(topic);

        String result = controller.closeQuestion(id);

        assertEquals(result, "redirect:" + QuestionsAndAnswersPlugin.CONTEXT + "/" + topic.getId());
        verify(topicService).closeTopic(topic);
    }

    @Test(expectedExceptions = NotFoundException.class)
    public void closeQuestionShouldThrowExceptionWhenQuestionNotFound() throws Exception {
        when(topicService.get(anyLong(), anyString())).thenThrow(new NotFoundException());

        controller.closeQuestion(1L);
    }

    @Test(expectedExceptions = NotFoundException.class)
    public void editAnswerPageShouldThrowExceptionWhenAnswerNotFound() throws Exception {
        when(postService.get(anyLong())).thenThrow(new NotFoundException());

        Model model = new ExtendedModelMap();
        controller.editAnswerPage(request, model, 42L);
    }

    @Test()
    public void editAnswerPageShouldRenderThePageIfAnswerIdIsCorrect() throws Exception {
        when(postService.get(42L)).thenReturn(createAnswer());

        Model model = new ExtendedModelMap();
        String result = controller.editAnswerPage(request, model, 42L);

        assertEquals(result, QuestionsAndAnswersController.PLUGIN_VIEW_NAME);
        assertEquals(model.asMap().get(QuestionsAndAnswersController.CONTENT), content);
    }

    private Post createAnswer() {
        JCUser userCreated = new JCUser("name", "mail@.ex.com", "pwd");
        Post post = new Post(userCreated, answerContent);
        post.setId(50L);
        Topic topic = new Topic(userCreated, content);
        topic.setId(43L);
        post.setTopic(topic);
        return post;
    }

    @Test()
    public void updateAnswerShouldRedirectBackIfValidationFails() throws Exception {
        when(postService.get(anyLong())).thenReturn(createAnswer());
        when(result.hasErrors()).thenReturn(true);

        Model model = new ExtendedModelMap();
        PostDto postDto = new PostDto();
        String methodResult = controller.updateAnswer(postDto, result, model, 42L, request);

        assertEquals(methodResult, QuestionsAndAnswersController.PLUGIN_VIEW_NAME);
        assertEquals(model.asMap().get(QuestionsAndAnswersController.CONTENT), content);
    }

    @Test(expectedExceptions = NotFoundException.class)
    public void updateAnswerPageShouldThrowExceptionWhenAnswerNotFound() throws Exception {
        when(postService.get(anyLong())).thenThrow(new NotFoundException());
        Model model = new ExtendedModelMap();
        PostDto postDto = new PostDto();

        controller.updateAnswer(postDto, result, model, 42L, request);
    }

    @Test()
    public void updateAnswerShouldUpdateAnswerIfValidationSuccess() throws Exception {
        Post answer = createAnswer();
        when(postService.get(anyLong())).thenReturn(answer);
        when(result.hasErrors()).thenReturn(false);

        Model model = new ExtendedModelMap();
        PostDto postDto = new PostDto();
        postDto.setBodyText(answerContent);
        String methodResult = controller.updateAnswer(postDto, result, model, answer.getId(), request);

        String redirectedResult = "redirect:" + QuestionsAndAnswersPlugin.CONTEXT + "/"
                + answer.getTopic().getId() + "#" + answer.getId();
        assertEquals(methodResult, redirectedResult);
        verify(postService).updatePost(answer, answerContent);
    }

    @Test()
    public void createAnswerShouldRedirectBackIfValidationFails() throws Exception {
        when(topicService.get(anyLong(), anyString())).thenReturn(createTopic());
        when(result.hasErrors()).thenReturn(true);

        Model model = new ExtendedModelMap();
        PostDto postDto = new PostDto();
        String methodResult = controller.create(42L, postDto, result, model, request);

        assertEquals(methodResult, QuestionsAndAnswersController.PLUGIN_VIEW_NAME);
        assertEquals(model.asMap().get(QuestionsAndAnswersController.CONTENT), content);
    }

    @Test(expectedExceptions = NotFoundException.class)
    public void createAnswerPageShouldThrowExceptionWhenTopicNotFound() throws Exception {
        when(topicService.get(anyLong(), anyString())).thenThrow(new NotFoundException());
        Model model = new ExtendedModelMap();
        PostDto postDto = new PostDto();

        controller.create(42L, postDto, result, model, request);
    }

    @Test()
    public void createAnswerShouldUpdateAnswerIfValidationSuccess() throws Exception {
        Topic topic = createTopic();
        Post answer = createAnswer();

        when(topicService.get(anyLong(), anyString())).thenReturn(topic);
        when(topicService.replyToTopic(anyLong(), anyString(), anyLong())).thenReturn(answer);

        Model model = new ExtendedModelMap();
        PostDto postDto = new PostDto();
        postDto.setBodyText(answerContent);
        String methodResult = controller.create(42L, postDto, result, model, request);

        String redirectedResult = "redirect:" + QuestionsAndAnswersPlugin.CONTEXT + "/"
                + 42L + "#" + answer.getId();
        assertEquals(methodResult, redirectedResult);
        verify(topicService).replyToTopic(42L, answerContent, topic.getBranch().getId());
    }

    private Topic createTopic() {
        Branch branch = new Branch("name", "description");
        branch.setId(1);
        Topic topic = new Topic();
        topic.setId(42L);
        topic.setBranch(branch);
        return topic;
    }

    @Test(expectedExceptions = NotFoundException.class)
    public void deleteAnswerPageShouldThrowExceptionWhenTopicNotFound() throws Exception {
        when(postService.get(anyLong())).thenThrow(new NotFoundException());
        controller.deleteAnswer(42L);
    }

    @Test
    public void deleteAnswerPageShouldRedirectToNeighborAnswer() throws Exception {
        Post answer = createAnswer();
        Post neighborAnswer = createAnswer();
        final long neighborPostId = 24L;
        neighborAnswer.setId(neighborPostId);

        Topic topic = mock(Topic.class);
        when(topic.getId()).thenReturn(42L);
        when(topic.getNeighborPost(answer)).thenReturn(neighborAnswer);
        answer.setTopic(topic);

        when(postService.get(answer.getId())).thenReturn(answer);

        String result = controller.deleteAnswer(answer.getId());
        assertEquals(result, "redirect:" + QuestionsAndAnswersPlugin.CONTEXT + "/" + answer.getTopic().getId()
                + "#" + neighborPostId);
    }
}

