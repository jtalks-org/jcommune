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
import org.jtalks.jcommune.service.exceptions.NotFoundException;
import org.jtalks.jcommune.web.dto.Breadcrumb;
import org.jtalks.jcommune.web.dto.CodeReviewDto;
import org.jtalks.jcommune.web.dto.TopicDto;
import org.jtalks.jcommune.web.dto.json.JsonResponse;
import org.jtalks.jcommune.web.dto.json.JsonResponseStatus;
import org.jtalks.jcommune.web.util.BreadcrumbBuilder;
import org.mockito.Mock;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.servlet.ModelAndView;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.test.web.ModelAndViewAssert.*;
import static org.testng.Assert.assertEquals;

/**
 * @author Vyacheslav Mishcheryakov
 */
public class CodeReviewControllerTest {
    public long BRANCH_ID = 1L;
    private String TOPIC_CONTENT = "content here";
    private long REVIEW_ID = 1L;

    private JCUser user;
    private Branch branch;

    @Mock
    private BranchService branchService;
    @Mock
    private BreadcrumbBuilder breadcrumbBuilder;
    @Mock
    private TopicModificationService topicModificationService;
    @Mock
    private LastReadPostService lastReadPostService;
    @Mock
    private CodeReviewService codeReviewService;
    @Mock
    private UserService userService;


    private CodeReviewController controller;

    @BeforeMethod
    public void initEnvironment() {
        initMocks(this);
        controller = new CodeReviewController(
                branchService,
                breadcrumbBuilder,
                topicModificationService,
                lastReadPostService,
                codeReviewService,
                userService);
    }

    @BeforeMethod
    public void prepareTestData() {
        branch = new Branch("", "description");
        branch.setId(BRANCH_ID);
        user = new JCUser("username", "email@mail.com", "password");
    }

    @Test
    public void createPage() throws NotFoundException {
        //set expectations
        when(branchService.get(BRANCH_ID)).thenReturn(branch);
        when(breadcrumbBuilder.getNewTopicBreadcrumb(branch)).thenReturn(new ArrayList<Breadcrumb>());

        //invoke the object under test
        ModelAndView mav = controller.showNewCodeReviewPage(BRANCH_ID);

        //check result
        assertViewName(mav, "codeReviewForm");
        assertModelAttributeAvailable(mav, "topicDto");
        long branchId = assertAndReturnModelAttributeOfType(mav, "branchId", Long.class);
        assertEquals(branchId, BRANCH_ID);
        String submitUrl = assertAndReturnModelAttributeOfType(mav, "submitUrl", String.class);
        assertEquals(submitUrl, "/reviews/new?branchId=" + branchId);
        assertModelAttributeAvailable(mav, "breadcrumbList");
    }

    @Test
    public void createValidationPass() throws Exception {
        user.setAutosubscribe(true);
        Branch branch = createBranch();
        Topic topic = createTopic();
        TopicDto dto = getDto();
        BindingResult result = mock(BindingResult.class);

        //set expectations
        when(branchService.get(BRANCH_ID)).thenReturn(branch);
        when(topicModificationService.createCodeReview(topic, TOPIC_CONTENT))
                .thenReturn(topic);

        //invoke the object under test
        ModelAndView mav = controller.createCodeReview(dto, result, BRANCH_ID);

        //check expectations
        verify(topicModificationService).createCodeReview(topic, TOPIC_CONTENT);

        //check result
        assertViewName(mav, "redirect:/topics/1");
    }

    @Test
    public void createValidationFail() throws Exception {
        BindingResult result = mock(BindingResult.class);

        //set expectations
        when(result.hasErrors()).thenReturn(true);
        when(branchService.get(BRANCH_ID)).thenReturn(branch);
        when(breadcrumbBuilder.getForumBreadcrumb(branch)).thenReturn(new ArrayList<Breadcrumb>());

        //invoke the object under test
        ModelAndView mav = controller.createCodeReview(getDto(), result, BRANCH_ID);

        //check result
        assertViewName(mav, "codeReviewForm");
        long branchId = assertAndReturnModelAttributeOfType(mav, "branchId", Long.class);
        assertEquals(branchId, BRANCH_ID);
    }

    @Test
    public void getCodeReviewSuccess() throws NotFoundException {
        CodeReview review = new CodeReview();
        review.setId(REVIEW_ID);
        when(codeReviewService.get(REVIEW_ID)).thenReturn(review);

        JsonResponse response = controller.getCodeReview(REVIEW_ID);

        assertEquals(response.getStatus(), JsonResponseStatus.SUCCESS);
        assertEquals(((CodeReviewDto) response.getResult()).getId(), REVIEW_ID);
    }

    @Test(expectedExceptions = NotFoundException.class)
    public void getCodeReviewNotFound() throws NotFoundException {
        when(codeReviewService.get(REVIEW_ID)).thenThrow(new NotFoundException());

        controller.getCodeReview(REVIEW_ID);
    }

    private Branch createBranch() {
        Branch branch = new Branch("branch name", "branch description");
        branch.setId(BRANCH_ID);
        return branch;
    }

    private Topic createTopic() {
        Branch branch = createBranch();
        Topic topic = new Topic(user, "Topic theme");
        topic.setId(1L);//we don't care what id is set
        topic.setUuid("uuid");
        topic.setBranch(branch);
        topic.addPost(new Post(user, TOPIC_CONTENT));
        return topic;
    }

    private TopicDto getDto() {
        TopicDto dto = new TopicDto();
        Topic topic = createTopic();
        dto.setBodyText(TOPIC_CONTENT);
        dto.setTopic(topic);
        return dto;
    }

}
