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

import org.jtalks.jcommune.model.entity.Branch;
import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.model.entity.Poll;
import org.jtalks.jcommune.model.entity.Post;
import org.jtalks.jcommune.model.entity.Topic;
import org.jtalks.jcommune.service.BranchService;
import org.jtalks.jcommune.service.LastReadPostService;
import org.jtalks.jcommune.service.PostService;
import org.jtalks.jcommune.service.TopicService;
import org.jtalks.jcommune.service.UserService;
import org.jtalks.jcommune.service.exceptions.NotFoundException;
import org.jtalks.jcommune.service.nontransactional.LocationService;
import org.jtalks.jcommune.web.dto.Breadcrumb;
import org.jtalks.jcommune.web.dto.TopicDto;
import org.jtalks.jcommune.web.util.BreadcrumbBuilder;
import org.mockito.Mock;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.servlet.ModelAndView;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.test.web.ModelAndViewAssert.assertAndReturnModelAttributeOfType;
import static org.springframework.test.web.ModelAndViewAssert.assertModelAttributeAvailable;
import static org.springframework.test.web.ModelAndViewAssert.assertViewName;
import static org.testng.Assert.assertEquals;

/**
 * @author Teterin Alexandre
 * @author Kirill Afonin
 * @author Max Malakhov
 * @author Eugeny Batov
 */
public class TopicControllerTest {
    public long BRANCH_ID = 1L;
    private long TOPIC_ID = 1L;
    private final String BRANCH_NAME = "branch name";
    private final String BRANCH_DESCRIPTION = "branch description";
    private String TOPIC_CONTENT = "content here";
    private String TOPIC_THEME = "Topic theme";

    private JCUser user;
    private Branch branch;

    @Mock
    private TopicService topicService;
    @Mock
    private PostService postService;
    @Mock
    private BranchService branchService;
    @Mock
    private UserService userService;
    @Mock
    private BreadcrumbBuilder breadcrumbBuilder;
    @Mock
    private LocationService locationService;
    @Mock
    private SessionRegistry registry;
    @Mock
    private LastReadPostService lastReadPostService;

    private TopicController controller;

    @BeforeMethod
    public void initEnvironment() {
        initMocks(this);
        controller = new TopicController(
                topicService,
                postService,
                branchService,
                lastReadPostService,
                userService,
                breadcrumbBuilder,
                locationService,
                registry);
    }

    @BeforeMethod
    public void prepareTestData() {
        branch = new Branch("", "description");
        branch.setId(BRANCH_ID);
        user = new JCUser("username", "email@mail.com", "password");
    }

    @Test
    public void testInitBinder() {
        WebDataBinder binder = mock(WebDataBinder.class);
        controller.initBinder(binder);
        verify(binder).registerCustomEditor(eq(String.class), any(StringTrimmerEditor.class));
    }

    @Test
    public void testDelete() throws NotFoundException {
        Topic topic = new Topic(null, null);
        branch.addTopic(topic);
        when(topicService.get(anyLong())).thenReturn(topic);

        ModelAndView actualMav = controller.deleteTopic(TOPIC_ID);

        assertViewName(actualMav, "redirect:/branches/" + BRANCH_ID);
        verify(topicService).deleteTopic(TOPIC_ID);
    }

    @Test
    public void showTopicPage() throws NotFoundException {
        int page = 2;
        boolean pagingEnabled = true;
        Topic topic = new Topic(null, null);
        branch.addTopic(topic);
        Page<Post> postsPage = new PageImpl<Post>(Collections.<Post>emptyList());

        //set expectations
        when(topicService.get(TOPIC_ID)).thenReturn(topic);
        when(breadcrumbBuilder.getForumBreadcrumb(topic)).thenReturn(new ArrayList<Breadcrumb>());
        when(postService.getPosts(topic, page, pagingEnabled)).thenReturn(postsPage);

        //invoke the object under test
        ModelAndView mav = controller.showTopicPage(TOPIC_ID, page, pagingEnabled);


        //check expectations
        verify(topicService).get(TOPIC_ID);
        verify(breadcrumbBuilder).getForumBreadcrumb(topic);


        //check result
        assertViewName(mav, "postList");
        assertAndReturnModelAttributeOfType(mav, "postsPage", Page.class);

        Topic actualTopic = assertAndReturnModelAttributeOfType(mav, "topic", Topic.class);
        assertEquals(actualTopic, topic);

        Long actualTopicId = assertAndReturnModelAttributeOfType(mav, "topicId", Long.class);
        assertEquals((long) actualTopicId, TOPIC_ID);

        Long actualBranchId = assertAndReturnModelAttributeOfType(mav, "branchId", Long.class);
        assertEquals((long) actualBranchId, TOPIC_ID);

        assertModelAttributeAvailable(mav, "breadcrumbList");
    }

    @Test
    public void testCreateValidationPass() throws Exception {
        Branch branch = createBranch();
        Topic topic = createTopic();
        TopicDto dto = getDto();
        BindingResult result = mock(BindingResult.class);

        //set expectations
        when(branchService.get(BRANCH_ID)).thenReturn(branch);
        when(topicService.createTopic(topic, TOPIC_CONTENT, false)).thenReturn(topic);

        //invoke the object under test
        ModelAndView mav = controller.createTopic(dto, result, BRANCH_ID);

        //check expectations
        verify(topicService).createTopic(topic, TOPIC_CONTENT, false);

        //check result
        assertViewName(mav, "redirect:/topics/1");
    }

    @Test
    public void testCreateValidationFail() throws Exception {
        BindingResult result = mock(BindingResult.class);

        //set expectations
        when(result.hasErrors()).thenReturn(true);
        when(branchService.get(BRANCH_ID)).thenReturn(branch);
        when(breadcrumbBuilder.getForumBreadcrumb(branch)).thenReturn(new ArrayList<Breadcrumb>());

        //invoke the object under test
        ModelAndView mav = controller.createTopic(getDto(), result, BRANCH_ID);

        //check expectations
        verify(branchService).get(BRANCH_ID);
        verify(breadcrumbBuilder).getForumBreadcrumb(branch);
        //check result
        assertViewName(mav, "newTopic");
        long branchId = assertAndReturnModelAttributeOfType(mav, "branchId", Long.class);
        assertEquals(branchId, BRANCH_ID);
    }

    @Test
    public void testCreatePage() throws NotFoundException {

        //set expectations
        when(branchService.get(BRANCH_ID)).thenReturn(branch);
        when(breadcrumbBuilder.getNewTopicBreadcrumb(branch)).thenReturn(new ArrayList<Breadcrumb>());

        //invoke the object under test
        ModelAndView mav = controller.showNewTopicPage(BRANCH_ID);

        //check expectations
        verify(branchService).get(BRANCH_ID);
        verify(breadcrumbBuilder).getNewTopicBreadcrumb(branch);

        //check result
        assertViewName(mav, "newTopic");
        assertModelAttributeAvailable(mav, "topicDto");
        long branchId = assertAndReturnModelAttributeOfType(mav, "branchId", Long.class);
        assertEquals(branchId, BRANCH_ID);
        assertModelAttributeAvailable(mav, "breadcrumbList");
    }

    @Test
    public void editTopicPageNotSubscribedUser() throws NotFoundException {
        Topic topic = new Topic(user, "title");
        topic.setId(TOPIC_ID);
        Post post = new Post(user, "content");
        topic.addPost(post);

        editTopicStubs(topic);

        //invoke the object under test
        ModelAndView mav = controller.editTopicPage(BRANCH_ID, TOPIC_ID);

        editTopicVerification(topic);

        editTopicAssertions(mav);
    }

    @Test
    public void editTopicPageSubscribedUser() throws NotFoundException {
        Topic topic = new Topic(user, "title");
        topic.setId(TOPIC_ID);
        Set<JCUser> subscribers = new HashSet<JCUser>();
        subscribers.add(user);
        topic.setSubscribers(subscribers);
        Post post = new Post(user, "content");
        topic.addPost(post);

        editTopicStubs(topic);

        //invoke the object under test
        ModelAndView mav = controller.editTopicPage(BRANCH_ID, TOPIC_ID);

        editTopicVerification(topic);

        editTopicAssertions(mav);
    }

    private void editTopicStubs(Topic topic) throws NotFoundException {
        //set expectations
        when(topicService.get(TOPIC_ID)).thenReturn(topic);
        when(breadcrumbBuilder.getForumBreadcrumb(topic)).thenReturn(new ArrayList<Breadcrumb>());
        when(userService.getCurrentUser()).thenReturn(user);
    }

    private void editTopicVerification(Topic topic) throws NotFoundException {
        //check expectations
        verify(topicService).get(TOPIC_ID);
        verify(breadcrumbBuilder).getForumBreadcrumb(topic);
    }

    private void editTopicAssertions(ModelAndView mav) {
        //check result
        assertViewName(mav, "editTopic");

        TopicDto dto = assertAndReturnModelAttributeOfType(mav, "topicDto", TopicDto.class);
        assertEquals(dto.getTopic().getId(), TOPIC_ID);

        long branchId = assertAndReturnModelAttributeOfType(mav, "branchId", Long.class);
        assertEquals(branchId, BRANCH_ID);

        long topicId = assertAndReturnModelAttributeOfType(mav, "topicId", Long.class);
        assertEquals(topicId, TOPIC_ID);

        assertModelAttributeAvailable(mav, "breadcrumbList");
    }

    @Test
    public void testSaveValidationPass() throws NotFoundException {
        TopicDto dto = getDto();
        BindingResult bindingResult = new BeanPropertyBindingResult(dto, "topicDto");

        //invoke the object under test
        ModelAndView mav = controller.editTopic(dto, bindingResult, BRANCH_ID, TOPIC_ID);
        Topic topic = dto.getTopic();
        topic.setId(TOPIC_ID);
        //check expectations
        verify(topicService).updateTopic(topic, TOPIC_CONTENT, false);

        //check result
        assertViewName(mav, "redirect:/topics/" + TOPIC_ID);
    }

    @Test
    public void testSaveValidationFail() throws NotFoundException {
        TopicDto dto = getDto();
        BeanPropertyBindingResult resultWithErrors = mock(BeanPropertyBindingResult.class);

        when(resultWithErrors.hasErrors()).thenReturn(true);

        ModelAndView mav = controller.editTopic(dto, resultWithErrors, BRANCH_ID, TOPIC_ID);

        assertViewName(mav, "editTopic");
        long branchId = assertAndReturnModelAttributeOfType(mav, "branchId", Long.class);
        long topicId = assertAndReturnModelAttributeOfType(mav, "topicId", Long.class);
        assertEquals(branchId, BRANCH_ID);
        assertEquals(topicId, TOPIC_ID);

        verify(topicService, never()).updateTopic((Topic) anyObject(), anyString(), anyBoolean());
    }

    @Test
    public void testMoveTopic() throws NotFoundException {
        controller.moveTopic(TOPIC_ID, BRANCH_ID);

        verify(topicService).moveTopic(TOPIC_ID, BRANCH_ID);
    }

    private Branch createBranch() {
        Branch branch = new Branch(BRANCH_NAME, BRANCH_DESCRIPTION);
        branch.setId(BRANCH_ID);
        return branch;
    }

    private Topic createTopic() {
        Branch branch = createBranch();
        Topic topic = new Topic(user, TOPIC_THEME);
        topic.setId(TOPIC_ID);
        topic.setUuid("uuid");
        topic.setBranch(branch);
        return topic;
    }

    private TopicDto getDto() {
        TopicDto dto = new TopicDto();
        Topic topic = createTopic();
        dto.setBodyText(TOPIC_CONTENT);
        Poll poll = new Poll();
        poll.setPollItemsValue("123");
        topic.setPoll(poll);
        dto.setTopic(topic);
        return dto;
    }
}
