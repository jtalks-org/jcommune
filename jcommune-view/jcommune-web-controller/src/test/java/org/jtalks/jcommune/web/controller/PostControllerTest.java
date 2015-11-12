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

import org.jtalks.jcommune.model.entity.*;
import org.jtalks.jcommune.service.*;
import org.jtalks.jcommune.plugin.api.exceptions.NotFoundException;
import org.jtalks.jcommune.service.dto.EntityToDtoConverter;
import org.jtalks.jcommune.service.nontransactional.BBCodeService;
import org.jtalks.jcommune.service.nontransactional.LocationService;
import org.jtalks.jcommune.plugin.api.web.dto.Breadcrumb;
import org.jtalks.jcommune.plugin.api.web.dto.PostDto;
import org.jtalks.jcommune.plugin.api.web.dto.PostDraftDto;
import org.jtalks.jcommune.plugin.api.web.dto.TopicDto;
import org.jtalks.jcommune.plugin.api.web.dto.json.JsonResponse;
import org.jtalks.jcommune.plugin.api.web.util.BreadcrumbBuilder;
import org.jtalks.jcommune.plugin.api.web.dto.json.JsonResponseStatus;
import org.mockito.ArgumentMatcher;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;

import static java.util.Arrays.asList;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
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

    @Mock
    private PostService postService;
    @Mock
    private BBCodeService bbCodeService;
    @Mock
    private TopicModificationService topicModificationService;
    @Mock
    private TopicFetchService topicFetchService;
    @Mock
    private BreadcrumbBuilder breadcrumbBuilder;
    @Mock
    private LastReadPostService lastReadPostService;
    @Mock
    private UserService userService;
    @Mock
    private LocationService locationService;
    @Mock
    private EntityToDtoConverter converter;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpSession session;
    @Mock
    private BindingResult result;

    public static final long POST_ID = 1;
    public static final long TOPIC_ID = 1L;
    private static final Long BRANCH_ID = 1L;
    private final String POST_CONTENT = "postContent";
    private Post post;
    private JCUser user = new JCUser("username", "email@mail.com", "password");

    private PostController controller;

    @BeforeMethod
    public void init() throws NotFoundException {
        initMocks(this);

        Branch branch = new Branch("branch", "branch");
        branch.setId(BRANCH_ID);

        Topic topic = new Topic(user, "title");
        topic.setBranch(branch);
        topic.setId(TOPIC_ID);

        post = new Post(user, POST_CONTENT);
        post.setId(POST_ID);
        post.setTopic(topic);
        topic.getPosts().addAll(asList(post));

        when(postService.get(POST_ID)).thenReturn(post);
        when(topicFetchService.get(TOPIC_ID)).thenReturn(topic);
        when(breadcrumbBuilder.getForumBreadcrumb(topic)).thenReturn(new ArrayList<Breadcrumb>());

        controller = new PostController(
                postService, breadcrumbBuilder, topicFetchService, topicModificationService,
                bbCodeService, lastReadPostService, userService, locationService, converter);
    }

    @Test
    public void testInitBinder() {
        WebDataBinder binder = mock(WebDataBinder.class);
        controller.initBinder(binder);
        verify(binder).registerCustomEditor(eq(String.class), any(StringTrimmerEditor.class));
    }

	@Test
	public void testDeletePost() throws NotFoundException {
		ModelAndView mav = controller.delete(POST_ID);
		verify(postService).deletePost(post);
		assertViewName(mav, "redirect:/posts/" + POST_ID);
	}

    @Test(expectedExceptions = NotFoundException.class)
    public void testDeleteUnexistingPost() throws NotFoundException {
        doThrow(new NotFoundException()).when(postService).get(anyLong());
        controller.delete(POST_ID);
    }

    @Test
    public void editPost() throws NotFoundException {
        //invoke the object under test
        ModelAndView actualMav = controller.editPage(POST_ID);
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
        ModelAndView mav = controller.update(dto, bindingResult, POST_ID);
        assertViewName(mav, "redirect:/posts/" + dto.getId());
        verify(postService).updatePost(Matchers.<Post>any(), anyString());
    }

    @Test
    public void updateWithError() throws NotFoundException {
        PostDto dto = this.getDto();
        BeanPropertyBindingResult resultWithErrors = mock(BeanPropertyBindingResult.class);

        when(resultWithErrors.hasErrors()).thenReturn(true);

        ModelAndView mav = controller.update(dto, resultWithErrors, POST_ID);

        this.assertEditPostFormMavIsCorrect(mav);

        verify(postService, never()).updatePost(Matchers.<Post>any(), anyString());
    }

    @Test
    public void testGetQuotedAjax() throws NotFoundException {
        String expected = "[quote=\"user\"]" + POST_CONTENT + "[/quote]";
        when(bbCodeService.quote(POST_CONTENT, user)).thenReturn(expected);
        JsonResponse response = controller.getQuote(post.getId(), null);

        assertEquals(response.getResult(), expected);
    }

    @Test
    public void testPartialQuotedAjax() throws NotFoundException {
        String selection = "Content";
        String expected = "[quote=\"user\"]" + selection + "[/quote]";
        when(postService.get(anyLong())).thenReturn(post);
        when(bbCodeService.quote(selection, user)).thenReturn(expected);

        JsonResponse response = controller.getQuote(post.getId(), selection);

        assertEquals(response.getResult(), expected);
    }

    @Test
    public void testSubmitAnswerValidationPass() throws NotFoundException {
        BeanPropertyBindingResult resultWithoutErrors = mock(BeanPropertyBindingResult.class);
        TopicDto dto = new TopicDto(post.getTopic());
        dto.setTopicUrl("/topics/" + TOPIC_ID);

        when(converter.convertTopicToDto(any(Topic.class))).thenReturn(dto);
        when(resultWithoutErrors.hasErrors()).thenReturn(false);
        when(topicModificationService.replyToTopic(anyLong(), Matchers.<String>any(), eq(BRANCH_ID))).thenReturn(post);
        when(postService.calculatePageForPost(post)).thenReturn(1);
        //invoke the object under test
        ModelAndView mav = controller.create(null, TOPIC_ID, getDto(), resultWithoutErrors, null);

        //check expectations
        verify(topicModificationService).replyToTopic(TOPIC_ID, POST_CONTENT, BRANCH_ID);

        //check result
        assertViewName(mav, "redirect:" + dto.getTopicUrl() + "?page=1#" + POST_ID);

    }

    @Test
    public void testSubmitAnswerValidationFail() throws NotFoundException {
        BeanPropertyBindingResult resultWithErrors = mock(BeanPropertyBindingResult.class);
        when(resultWithErrors.hasErrors()).thenReturn(true);
        //invoke the object under test
        ModelAndView mav = controller.create(null, TOPIC_ID, getDto(), resultWithErrors, new RedirectAttributesModelMap());

        //check expectations
        verify(topicModificationService, never()).replyToTopic(anyLong(), anyString(), eq(BRANCH_ID));

        //check result
        assertEquals(mav.getViewName(), "redirect:/topics/error/" + TOPIC_ID + "?page=null");
    }

    @Test
    public void testRedirectToPageWithPost() throws NotFoundException {
        TopicDto dto = new TopicDto(post.getTopic());
        dto.setTopicUrl("/topics/" + TOPIC_ID);

        when(converter.convertTopicToDto(any(Topic.class))).thenReturn(dto);
        when(postService.calculatePageForPost(post)).thenReturn(5);

        String result = controller.redirectToPageWithPost(POST_ID);

        assertEquals(result, "redirect:" + dto.getTopicUrl() + "?page=5#" + POST_ID);
    }

    @Test(expectedExceptions = NotFoundException.class)
    public void testRedirectToPageWithPostNotFound() throws NotFoundException {
        doThrow(new NotFoundException()).when(postService).get(anyLong());

        controller.redirectToPageWithPost(POST_ID);
    }

    @Test
    public void testPostPreview() throws Exception {
        BeanPropertyBindingResult BindingResult = mock(BeanPropertyBindingResult.class);
        ModelAndView mav = controller.preview(getDto(), BindingResult);
        assertViewName(mav, "ajax/postPreview");
        assertModelAttributeAvailable(mav, "errors");
        assertModelAttributeAvailable(mav, "content");
        assertModelAttributeAvailable(mav, "isInvalid");
    }

    @Test
    public void testVoteUpSuccess() throws Exception {
        JCUser user = new JCUser("username", "email@mail.com", "password");
        Post post = new Post(user, "text");

        when(postService.get(anyLong())).thenReturn(post);
        when(request.getSession()).thenReturn(session);

        JsonResponse response = controller.voteUp(1L, request);

        assertEquals(response.getStatus(), JsonResponseStatus.SUCCESS);
        verify(postService).vote(eq(post), argThat(new PostVoteMacher(true)));
    }

    @Test
    public void testVoteDownSuccess() throws Exception{
        JCUser user = new JCUser("username", "email@mail.com", "password");
        Post post = new Post(user, "text");

        when(postService.get(anyLong())).thenReturn(post);
        when(request.getSession()).thenReturn(session);

        JsonResponse response = controller.voteDown(1L, request);

        assertEquals(response.getStatus(), JsonResponseStatus.SUCCESS);
        verify(postService).vote(eq(post), argThat(new PostVoteMacher(false)));
    }

    @Test(expectedExceptions = NotFoundException.class)
    public void voteUpShouldThrowExceptionIfPostNotFound() throws Exception {
        when(postService.get(anyLong())).thenThrow(new NotFoundException());
        when(request.getSession()).thenReturn(session);

        controller.voteUp(1L, request);
    }

    @Test(expectedExceptions = NotFoundException.class)
    public void voteDownShouldThrowExceptionIfPostNotFound() throws Exception {
        when(postService.get(anyLong())).thenThrow(new NotFoundException());
        when(request.getSession()).thenReturn(session);

        controller.voteDown(1L, request);
    }

    @Test
    public void testSaveDraft() throws Exception {
        PostDraftDto dto = getPostDraftDto();
        Topic topic = new Topic();
        PostDraft  saved = new PostDraft("content", new JCUser("name", null, null));
        saved.setId(1);

        when(topicFetchService.getTopicSilently(dto.getTopicId())).thenReturn(topic);
        when(postService.saveOrUpdateDraft(topic, dto.getBodyText())).thenReturn(saved);

        JsonResponse response = controller.saveDraft(dto, result);

        assertEquals(response.getStatus(), JsonResponseStatus.SUCCESS);
        assertEquals(1, (long)response.getResult());
    }

    @Test
    public void saveDraftShouldReturnFailResponseIfValidationErrorsOccurred() throws Exception {
        when(result.hasErrors()).thenReturn(true);

        JsonResponse response = controller.saveDraft(getPostDraftDto(), result);

        assertEquals(response.getStatus(), JsonResponseStatus.FAIL);
    }

    @Test(expectedExceptions = NotFoundException.class)
    public void saveDraftShouldThrowExceptionIfTopicNotFound() throws Exception {
        PostDraftDto dto = getPostDraftDto();

        when(topicFetchService.getTopicSilently(dto.getTopicId())).thenThrow(new NotFoundException());

        controller.saveDraft(dto, result);
    }

    @Test
    public void testDeleteDraft() throws Exception {

        controller.deleteDraft(1l);

        verify(postService).deleteDraft(1l);
    }

    private class PostVoteMacher extends ArgumentMatcher<PostVote> {

        private boolean isVotedUp;
        public PostVoteMacher(boolean isVotedUp) {
            this.isVotedUp = isVotedUp;
        }

        @Override
        public boolean matches(Object o) {
            //should be both true or both false
            return o instanceof PostVote && !(isVotedUp ^ ((PostVote) o).isVotedUp());
        }
    }
/*    @Test
    public void testPreview() {
        String postText = "[code]123[/code]";
        String html = "<code>123</code>";
        when(bbCodeService.convertBbToHtml(anyString())).thenReturn(html);
        when(userService.getCurrentUser()).thenReturn(user);

        String result = controller.preview(postText).getBody();

        assertEquals(result, html);
        verify(bbCodeService).convertBbToHtml(postText);
    }*/

    private void assertAnswerMavIsCorrect(ModelAndView mav) {
        assertViewName(mav, "topic/answer");
        assertAndReturnModelAttributeOfType(mav, "topic", Topic.class);
        long topicId = assertAndReturnModelAttributeOfType(mav, "topicId", Long.class);
        assertEquals(topicId, TOPIC_ID);
        PostDto dto = assertAndReturnModelAttributeOfType(mav, "postDto", PostDto.class);
        assertEquals(dto.getId(), 0);
        assertModelAttributeAvailable(mav, "breadcrumbList");
    }

    private void assertEditPostFormMavIsCorrect(ModelAndView mav) {
        assertViewName(mav, "topic/editPost");
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

    private PostDraftDto getPostDraftDto() {
        PostDraftDto dto = new PostDraftDto();
        dto.setBodyText(POST_CONTENT);
        dto.setTopicId(TOPIC_ID);
        return dto;
    }
}
