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

import org.jtalks.common.model.entity.User;
import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.model.entity.PrivateMessage;
import org.jtalks.jcommune.model.entity.PrivateMessageStatus;
import org.jtalks.jcommune.plugin.api.exceptions.NotFoundException;
import org.jtalks.jcommune.service.PrivateMessageService;
import org.jtalks.jcommune.service.UserService;
import org.jtalks.jcommune.service.nontransactional.BBCodeService;
import org.jtalks.jcommune.web.dto.PrivateMessageDto;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.servlet.ModelAndView;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static io.qala.datagen.RandomShortApi.alphanumeric;
import static io.qala.datagen.RandomValue.between;
import static io.qala.datagen.StringModifier.Impls.prefix;
import static org.mockito.Matchers.any;
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
 * @author Oleg Tkachenko
 */
public class PrivateMessageControllerTest {

    private static final long PM_ID = 2L;
    private static final String PAGE_NUMBER = "1";
    private static final JCUser JC_USER = getRandomJCUser();
    private static final String USERNAME = JC_USER.getUsername();
    private static final String TITLE = alphanumeric(PrivateMessage.MIN_TITLE_LENGTH, PrivateMessage.MAX_TITLE_LENGTH);
    private static final String BODY = alphanumeric(PrivateMessage.MIN_MESSAGE_LENGTH, PrivateMessage.MAX_MESSAGE_LENGTH);

    private PrivateMessageController controller;
    @Mock
    private PrivateMessageService pmService;
    @Mock
    private BBCodeService bbCodeService;
    @Mock
    private UserService userService;

    private MockMvc mockMvc;
    private Page<PrivateMessage> expectedPage;

    @BeforeClass
    public void setUp(){
        expectedPage = new PageImpl<>(Collections.singletonList(getPrivateMessage()));
    }

    @BeforeMethod
    public void init() {
        JC_USER.setId(1);
        MockitoAnnotations.initMocks(this);
        controller = new PrivateMessageController(pmService, bbCodeService, userService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    public void testInitBinder() {
        WebDataBinder binder = mock(WebDataBinder.class);
        controller.initBinder(binder);
        verify(binder).registerCustomEditor(eq(String.class), any(StringTrimmerEditor.class));
    }

    @Test
    public void inboxPage() {
        when(pmService.getInboxForCurrentUser(PAGE_NUMBER)).thenReturn(expectedPage);
        when(userService.getCurrentUser()).thenReturn(JC_USER);

        //invoke the object under test
        ModelAndView mav = controller.inboxPage(PAGE_NUMBER);

        //check expectations
        verify(pmService).getInboxForCurrentUser(PAGE_NUMBER);

        //check result
        assertViewName(mav, "pm/inbox");
        assertModelAttributeAvailable(mav, "inboxPage");
    }

    @Test
    public void outboxPage() {
        when(pmService.getOutboxForCurrentUser(PAGE_NUMBER)).thenReturn(expectedPage);
        when(userService.getCurrentUser()).thenReturn(JC_USER);

        //invoke the object under test
        ModelAndView mav = controller.outboxPage(PAGE_NUMBER);

        //check expectations
        verify(pmService).getOutboxForCurrentUser(PAGE_NUMBER);
        //check result
        assertViewName(mav, "pm/outbox");
        assertModelAttributeAvailable(mav, "outboxPage");
    }

    @Test
    public void draftsPage() {
        when(pmService.getDraftsForCurrentUser(PAGE_NUMBER)).thenReturn(expectedPage);

        //invoke the object under test
        ModelAndView mav = controller.draftsPage(PAGE_NUMBER);

        //check expectations
        verify(pmService).getDraftsForCurrentUser(PAGE_NUMBER);
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
        when(userService.getByUsername(USERNAME)).thenReturn(JC_USER);
        when(userService.getCurrentUser()).thenReturn(JC_USER);

        saveDraft(TITLE, BODY, USERNAME)
                .andExpect(model().hasNoErrors())
                .andExpect(status().isMovedTemporarily())
                .andExpect(redirectedUrl("/drafts"));
        verify(pmService).saveDraft(PM_ID, JC_USER, TITLE, BODY, JC_USER);
    }

    @Test
    public void testSaveDraftGet() throws Exception {
        when(userService.getByUsername(USERNAME)).thenReturn(JC_USER);
        when(userService.getCurrentUser()).thenReturn(JC_USER);

        mockMvc.perform(get("/pm/save")
                .param("title", TITLE)
                .param("body", BODY)
                .param("recipient", USERNAME))
                .andExpect(model().hasNoErrors())
                .andExpect(status().isMovedTemporarily())
                .andExpect(redirectedUrl("/drafts"));
        verify(pmService).saveDraft(0, JC_USER, TITLE, BODY, JC_USER);
    }

    @Test
    public void saveDraftShouldBackToPmFormIfTitleIsInvalid() throws Exception {
        String invalidTitle = alphanumeric(PrivateMessage.MAX_TITLE_LENGTH + 1);
        saveDraft(invalidTitle, BODY, USERNAME)
                .andExpect(model()
                        .attributeHasFieldErrors("privateMessageDto", "title"))
                .andExpect(status().isOk())
                .andExpect(forwardedUrl("pm/pmForm"));
    }

    @Test
    public void saveDraftShouldDeleteDraftIfTitleAndBodyAreEmptyAndRecipientIsNotExists() throws Exception {
        saveDraft("", "", null)
                .andExpect(model()
                        .attributeHasErrors("privateMessageDto"))
                .andExpect(view().name("redirect:/drafts"))
                .andExpect(status().isMovedTemporarily());
        verify(pmService).delete(anyList());
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
        dto.setBody(BODY);
        dto.setTitle(TITLE);
        dto.setRecipient(USERNAME);
        return dto;
    }

    private PrivateMessage getPrivateMessage() {
        return new PrivateMessage(JC_USER,
                getRandomJCUser(), TITLE, BODY);
    }

    private static JCUser getRandomJCUser() {
        String username = alphanumeric(User.USERNAME_MIN_LENGTH, User.USERNAME_MAX_LENGTH);
        String email = alphanumeric(1, 30) +
                between(3, 15).with(prefix("@")).alphanumeric() +
                between(3, 5).with(prefix(".")).english();
        String password = alphanumeric(User.PASSWORD_MIN_LENGTH, User.PASSWORD_MAX_LENGTH);

        return new JCUser(username, email, password);
    }

    private ResultActions saveDraft(String title, String body, String recipient) throws Exception {
        return this.mockMvc.perform(post("/pm/save")
                .param("id", String.valueOf(PM_ID))
                .param("title", title)
                .param("body", body)
                .param("recipient", recipient));
    }
}
