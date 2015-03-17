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

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.model.entity.PrivateMessage;
import org.jtalks.jcommune.model.entity.PrivateMessageStatus;
import org.jtalks.jcommune.service.PrivateMessageService;
import org.jtalks.jcommune.service.UserService;
import org.jtalks.jcommune.plugin.api.exceptions.NotFoundException;
import org.jtalks.jcommune.service.nontransactional.BBCodeService;
import org.jtalks.jcommune.web.dto.PrivateMessageDraftDto;
import org.jtalks.jcommune.web.dto.PrivateMessageDto;
import org.jtalks.jcommune.web.validation.validators.ValidatorStub;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.servlet.ModelAndView;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.ModelAndViewAssert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.testng.Assert.assertEquals;

/**
 * @author Pavel Vervenko
 * @author Max Malakhov
 * @author Alexandre Teterin
 * @author Evheniy Naumenko
 */
public class PrivateMessageControllerTest {

    public static final long PM_ID = 2L;

    private PrivateMessageController controller;
    @Mock
    private PrivateMessageService pmService;
    @Mock
    private BBCodeService bbCodeService;
    @Mock
    private UserService userService;

    private MockMvc mockMvc;

    private static final String USERNAME = "username";
    private static final JCUser JC_USER = new JCUser(USERNAME, "123@123.ru", "123");
    private static final String TITLE = "test title";
    private static final String BODY = "test body";

    @BeforeMethod
    public void init() {
        JC_USER.setId(1);
        MockitoAnnotations.initMocks(this);
        controller = new PrivateMessageController(pmService, bbCodeService, userService);
    }

    @Test
    public void testInitBinder() {
        WebDataBinder binder = mock(WebDataBinder.class);
        controller.initBinder(binder);
        verify(binder).registerCustomEditor(eq(String.class), any(StringTrimmerEditor.class));
    }

    @Test
    public void inboxPage() {
        String page = "1";
        List<PrivateMessage> messages = Arrays.asList(new PrivateMessage(JC_USER, JC_USER,
                "Message title", "Private message body"));
        Page<PrivateMessage> expectedPage = new PageImpl<>(messages);

        when(pmService.getInboxForCurrentUser(page)).thenReturn(expectedPage);
        when(userService.getCurrentUser()).thenReturn(JC_USER);

        //invoke the object under test
        ModelAndView mav = controller.inboxPage(page);

        //check expectations
        verify(pmService).getInboxForCurrentUser(page);

        //check result
        assertViewName(mav, "pm/inbox");
        assertModelAttributeAvailable(mav, "inboxPage");
    }

    @Test
    public void outboxPage() {
        String page = "1";
        List<PrivateMessage> messages = Arrays.asList(new PrivateMessage(JC_USER, JC_USER,
                "Message title", "Private message body"));
        Page<PrivateMessage> expectedPage = new PageImpl<>(messages);

        when(pmService.getOutboxForCurrentUser(page)).thenReturn(expectedPage);
        when(userService.getCurrentUser()).thenReturn(JC_USER);

        //invoke the object under test
        ModelAndView mav = controller.outboxPage(page);

        //check expectations
        verify(pmService).getOutboxForCurrentUser(page);
        //check result
        assertViewName(mav, "pm/outbox");
        assertModelAttributeAvailable(mav, "outboxPage");
    }

    @Test
    public void draftsPage() {
        String page = "1";
        List<PrivateMessage> messages = Arrays.asList(new PrivateMessage(JC_USER, JC_USER,
                "Message title", "Private message body"));
        Page<PrivateMessage> expectedPage = new PageImpl<>(messages);

        when(pmService.getDraftsForCurrentUser(page)).thenReturn(expectedPage);

        //invoke the object under test
        ModelAndView mav = controller.draftsPage(page);

        //check expectations
        verify(pmService).getDraftsForCurrentUser(page);
        //check result
        assertViewName(mav, "pm/drafts");
        assertModelAttributeAvailable(mav, "draftsPage");
    }

    @Test
    public void newPmPage() throws NotFoundException {
        when(userService.getCurrentUser()).thenReturn(JC_USER);
        //invoke the object under test
        ModelAndView mav = controller.newPmPage(null);

        //check result
        verify(pmService).checkPermissionsToSend(JC_USER.getId());
        assertViewName(mav, "pm/pmForm");
        assertAndReturnModelAttributeOfType(mav, "privateMessageDto", PrivateMessageDto.class);
    }

    @Test
    public void newPmPageWithUserSet() throws NotFoundException {
        //invoke the object under test
        when(userService.getCurrentUser()).thenReturn(JC_USER);
        when(userService.get(JC_USER.getId())).thenReturn(JC_USER);
        ModelAndView mav = controller.newPmPage(JC_USER.getId());

        //check result
        verify(pmService).checkPermissionsToSend(JC_USER.getId());
        assertViewName(mav, "pm/pmForm");
        PrivateMessageDto dto = assertAndReturnModelAttributeOfType(mav, "privateMessageDto", PrivateMessageDto.class);
        assertEquals(dto.getRecipient(), JC_USER.getUsername());
    }

    @Test(expectedExceptions = NotFoundException.class)
    public void newPmPageWithWrongUserSet() throws NotFoundException {
        when(userService.getCurrentUser()).thenReturn(JC_USER);
        doThrow(new NotFoundException()).when(userService).get(JC_USER.getId());

        controller.newPmPage(JC_USER.getId());
    }

    @Test
    public void sendMessage() throws NotFoundException {
        when(userService.getByUsername(USERNAME)).thenReturn(JC_USER);
        when(userService.getCurrentUser()).thenReturn(JC_USER);
        PrivateMessageDto dto = getPrivateMessageDto();
        dto.setRecipient(USERNAME);
        BindingResult bindingResult = new BeanPropertyBindingResult(dto, "privateMessageDto");

        ModelAndView mav = controller.sendMessage(dto, bindingResult);

        assertEquals(mav.getViewName(), "redirect:/outbox");
        verify(pmService).sendMessage(dto.getTitle(), dto.getBody(), JC_USER, JC_USER);
    }

    @Test
    public void sendDraftMessage() throws NotFoundException {
        when(userService.getByUsername(USERNAME)).thenReturn(JC_USER);
        when(userService.getCurrentUser()).thenReturn(JC_USER);
        PrivateMessageDto dto = getPrivateMessageDto();
        dto.setRecipient(USERNAME);
        dto.setId(4);
        BindingResult bindingResult = new BeanPropertyBindingResult(dto, "privateMessageDto");

        ModelAndView mav = controller.sendMessage(dto, bindingResult);

        assertEquals(mav.getViewName(), "redirect:/outbox");
        verify(pmService).sendDraft(dto.getId(), dto.getTitle(), dto.getBody(), JC_USER, JC_USER);
    }

    @Test
    public void sendMessageWithErrors() throws NotFoundException {
        PrivateMessageDto dto = getPrivateMessageDto();
        BeanPropertyBindingResult resultWithErrors = mock(BeanPropertyBindingResult.class);
        when(resultWithErrors.hasErrors()).thenReturn(true);

        ModelAndView mav = controller.sendMessage(dto, resultWithErrors);

        assertEquals(mav.getViewName(), "pm/pmForm");
    }

    @Test(expectedExceptions = NotFoundException.class)
    public void sendMessageWithWrongUser() throws NotFoundException {
        PrivateMessageDto dto = getPrivateMessageDto();
        doThrow(new NotFoundException()).when(pmService).
                sendMessage(anyString(), anyString(), any(JCUser.class), any(JCUser.class));
        BindingResult bindingResult = new BeanPropertyBindingResult(dto, "privateMessageDto");

        controller.sendMessage(dto, bindingResult);
    }

    @Test
    public void replyPage() throws NotFoundException {
        PrivateMessage pm = getPrivateMessage();
        //set expectations
        when(pmService.get(PM_ID)).thenReturn(pm);

        //invoke the object under test
        ModelAndView mav = controller.replyPage(PM_ID);

        //check expectations
        verify(pmService).get(PM_ID);

        //check result
        assertViewName(mav, "pm/pmForm");
        assertAndReturnModelAttributeOfType(mav, "privateMessageDto", PrivateMessageDto.class);
    }

    @Test
    public void quotePage() throws NotFoundException {
        PrivateMessage pm = getPrivateMessage();
        //set expectations
        when(pmService.get(PM_ID)).thenReturn(pm);

        //invoke the object under test
        ModelAndView mav = controller.quotePage(PM_ID);

        //check expectations
        verify(pmService).get(PM_ID);

        //check result
        assertViewName(mav, "pm/pmForm");
        assertAndReturnModelAttributeOfType(mav, "privateMessageDto", PrivateMessageDto.class);
    }

    @Test
    public void showPmPageInbox() throws NotFoundException {
        PrivateMessage pm = getPrivateMessage();

        //set expectations
        when(pmService.get(PM_ID)).thenReturn(pm);

        //invoke the object under test
        ModelAndView mav = controller.showPmPage(PM_ID);

        //check expectations
        verify(pmService).get(PM_ID);

        //check result
        assertViewName(mav, "pm/showPm");
        PrivateMessage actualPm = assertAndReturnModelAttributeOfType(mav, "pm", PrivateMessage.class);
        assertEquals(actualPm, pm);
    }

    @Test(expectedExceptions = NotFoundException.class)
    public void cannotBeShowedPm() throws NotFoundException {
        //set expectations
        when(pmService.get(PM_ID)).thenThrow(new NotFoundException());
        //invoke the object under test
        controller.showPmPage(PM_ID);

        //check expectations
        verify(pmService).get(PM_ID);
    }

    @Test
    public void editDraftPage() throws NotFoundException {
        PrivateMessage pm = getPrivateMessage();
        pm.setId(PM_ID);
        pm.setStatus(PrivateMessageStatus.DRAFT);

        //set expectations
        when(pmService.get(PM_ID)).thenReturn(pm);

        //invoke the object under test
        ModelAndView mav = controller.editDraftPage(PM_ID);

        //check expectations
        verify(pmService).get(PM_ID);

        //check result
        assertViewName(mav, "pm/pmForm");
        PrivateMessageDto dto = assertAndReturnModelAttributeOfType(mav, "privateMessageDto", PrivateMessageDto.class);
        assertEquals(dto.getId(), PM_ID);
    }

    @Test(expectedExceptions = NotFoundException.class)
    public void editDraftPageNotDraft() throws NotFoundException {
        PrivateMessage pm = getPrivateMessage();
        when(pmService.get(PM_ID)).thenReturn(pm);

        controller.editDraftPage(PM_ID);
    }

    @Test
    public void saveDraftPost() throws Exception {
        Validator validator = new ValidatorStub();
        mockMvc = MockMvcBuilders.standaloneSetup(controller).setValidator(validator).build();

        when(userService.getByUsername(USERNAME)).thenReturn(JC_USER);
        when(userService.getCurrentUser()).thenReturn(JC_USER);

        mockMvc.perform(post("/pm/save")
                .param("title", TITLE)
                .param("body", BODY)
                .param("recipient", USERNAME))
                .andExpect(status().isMovedTemporarily())
                .andExpect(redirectedUrl("/drafts"));
        verify(pmService).saveDraft(0, USERNAME, "test title", "test body", JC_USER);
    }

    @Test
    public void testSaveDraftGet() throws Exception {
        Validator validator = new ValidatorStub();
        mockMvc = MockMvcBuilders.standaloneSetup(controller).setValidator(validator).build();

        when(userService.getByUsername(USERNAME)).thenReturn(JC_USER);
        when(userService.getCurrentUser()).thenReturn(JC_USER);

        mockMvc.perform(get("/pm/save")
                .param("title", TITLE)
                .param("body", BODY)
                .param("recipient", USERNAME))
                .andExpect(status().isMovedTemporarily())
                .andExpect(redirectedUrl("/drafts"));
        verify(pmService).saveDraft(0, USERNAME, "test title", "test body", JC_USER);
    }

    @Test
    public void saveDraftShouldReturnPmFormIfExceptionWasThrown() throws Exception {
        Validator validator = new ValidatorStub();
        mockMvc = MockMvcBuilders.standaloneSetup(controller).setValidator(validator).build();

        when(userService.getByUsername(USERNAME)).thenReturn(JC_USER);
        when(userService.getCurrentUser()).thenReturn(JC_USER);
        doThrow(new NotFoundException()).when(pmService).saveDraft(anyLong(), anyString(), anyString(), anyString(),
                any(JCUser.class));

        mockMvc.perform(post("/pm/save")
                .param("title", TITLE)
                .param("body", BODY)
                .param("recipient", USERNAME))
                .andExpect(status().isOk())
                .andExpect(forwardedUrl("pm/pmForm"));
    }

    @Test
    public void saveDraftShouldReturnPmFormIfValidationErrorsOccurs() throws Exception {
        String[] errorFields = new String[] {"title", "body", "recipient"};
        Validator validator = new ValidatorStub(errorFields);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).setValidator(validator).build();

        mockMvc.perform(post("/pm/save")
                .param("title", TITLE)
                .param("body", BODY)
                .param("recipient", USERNAME))
                .andExpect(status().isMovedTemporarily())
                .andExpect(redirectedUrl("/drafts"));
    }

    @Test
    public void testDeletePm() throws NotFoundException {
        List<Long> pmIds = Arrays.asList(1L, 2L);

        when(pmService.delete(Arrays.asList(1L, 2L))).thenReturn("aaa");

        String result = controller.deleteMessages(pmIds);

        assertEquals(result, "redirect:/aaa");
    }

    private PrivateMessageDto getPrivateMessageDto() {
        PrivateMessageDto dto = new PrivateMessageDto();
        dto.setBody("body");
        dto.setTitle("title");
        dto.setRecipient(USERNAME);
        return dto;
    }

    private PrivateMessage getPrivateMessage() {
        return new PrivateMessage(new JCUser("username", "email", "password"),
                new JCUser("username2", "email2", "password2"), "title", "body");
    }

    private static class PrivateMessageDraftDtoMatcher extends BaseMatcher<PrivateMessageDraftDto> {
        @Override
        public boolean matches(Object o) {
            if (o != null && o instanceof PrivateMessageDraftDto) {
                PrivateMessageDraftDto dto = (PrivateMessageDraftDto)o;
                return TITLE.equals(dto.getTitle()) && BODY.equals(dto.getBody()) && USERNAME.equals(dto.getRecipient());
            }
            return false;
        }

        @Override
        public void describeTo(Description description) {

        }
    }
}
