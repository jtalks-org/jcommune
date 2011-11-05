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

import org.jtalks.jcommune.model.entity.PrivateMessage;
import org.jtalks.jcommune.model.entity.User;
import org.jtalks.jcommune.service.PrivateMessageService;
import org.jtalks.jcommune.service.exceptions.NotFoundException;
import org.jtalks.jcommune.web.dto.Breadcrumb;
import org.jtalks.jcommune.web.dto.BreadcrumbBuilder;
import org.jtalks.jcommune.web.dto.PrivateMessageDto;
import org.jtalks.jcommune.web.dto.PrivateMessageDtoBuilder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.ModelAndViewAssert.*;
import static org.testng.Assert.assertEquals;

/**
 * @author Pavel Vervenko
 * @author Max Malakhov
 * @author Alexandre Teterin
 */
public class PrivateMessageControllerTest {

    public static final long PM_ID = 2L;
    private PrivateMessageController controller;
    @Mock
    private PrivateMessageService pmService;
    @Mock
    private PrivateMessageDtoBuilder pmDtoBuilder;
    @Mock
    private PrivateMessageDto pmDto;
    @Mock
    private BreadcrumbBuilder breadcrumbBuilder;

    @BeforeMethod
    public void init() {
        MockitoAnnotations.initMocks(this);
        controller = new PrivateMessageController(pmService, breadcrumbBuilder, pmDtoBuilder);
    }

    @Test
    public void inboxPage() {
        //set expectations
        when(breadcrumbBuilder.getForumBreadcrumb()).thenReturn(new ArrayList<Breadcrumb>());

        //invoke the object under test
        ModelAndView mav = controller.inboxPage();

        //check expectations
        verify(pmService).getInboxForCurrentUser();
        verify(breadcrumbBuilder).getForumBreadcrumb();

        //check result
        assertViewName(mav, "pm/inbox");
        assertAndReturnModelAttributeOfType(mav, "pmList", List.class);
        assertModelAttributeAvailable(mav, "breadcrumbList");
    }

    @Test
    public void outboxPage() {
        //set expectations
        when(breadcrumbBuilder.getForumBreadcrumb()).thenReturn(new ArrayList<Breadcrumb>());

        //invoke the object under test
        ModelAndView mav = controller.outboxPage();

        //check expectations
        verify(pmService).getOutboxForCurrentUser();
        verify(breadcrumbBuilder).getForumBreadcrumb();

        //check result
        assertViewName(mav, "pm/outbox");
        assertAndReturnModelAttributeOfType(mav, "pmList", List.class);
        assertModelAttributeAvailable(mav, "breadcrumbList");
    }
    
    @Test
    public void draftsPage() {
        //set expectations
        when(breadcrumbBuilder.getForumBreadcrumb()).thenReturn(new ArrayList<Breadcrumb>());

        //invoke the object under test
        ModelAndView mav = controller.draftsPage();

        //check expectations
        verify(pmService).getDraftsFromCurrentUser();
        verify(breadcrumbBuilder).getForumBreadcrumb();

        //check result
        assertViewName(mav, "pm/drafts");
        assertAndReturnModelAttributeOfType(mav, "pmList", List.class);
        assertModelAttributeAvailable(mav, "breadcrumbList");
    }
    
    @Test
    public void newPmPage() {
        //set expectations
        when(breadcrumbBuilder.getForumBreadcrumb()).thenReturn(new ArrayList<Breadcrumb>());

        //invoke the object under test
        ModelAndView mav = controller.newPmPage();

        //check expectations
        verify(breadcrumbBuilder).getForumBreadcrumb();

        //check result
        assertViewName(mav, "pm/pmForm");
        assertAndReturnModelAttributeOfType(mav, "privateMessageDto", PrivateMessageDto.class);
        assertModelAttributeAvailable(mav, "breadcrumbList");
    }

    @Test
    public void sendMessage() throws NotFoundException {
        PrivateMessageDto dto = getPrivateMessageDto();
        BindingResult bindingResult = new BeanPropertyBindingResult(dto, "privateMessageDto");

        String view = controller.sendMessage(dto, bindingResult);

        assertEquals(view, "redirect:/outbox");
        verify(pmService).sendMessage(dto.getTitle(), dto.getBody(), dto.getRecipient());
    }

    @Test
    public void sendDraftMessage() throws NotFoundException {
        PrivateMessageDto dto = getPrivateMessageDto();
        dto.setId(4);
        BindingResult bindingResult = new BeanPropertyBindingResult(dto, "privateMessageDto");

        String view = controller.sendMessage(dto, bindingResult);

        assertEquals(view, "redirect:/outbox");
        verify(pmService).sendDraft(dto.getId(), dto.getTitle(), dto.getBody(), dto.getRecipient());
    }

    @Test
    public void sendMessageWithErrors() {
        PrivateMessageDto dto = getPrivateMessageDto();
        BeanPropertyBindingResult resultWithErrors = mock(BeanPropertyBindingResult.class);
        when(resultWithErrors.hasErrors()).thenReturn(true);

        String view = controller.sendMessage(dto, resultWithErrors);

        assertEquals(view, "pm/pmForm");
    }

    @Test
    public void sendMessageWithWrongUser() throws NotFoundException {
        PrivateMessageDto dto = getPrivateMessageDto();
        doThrow(new NotFoundException()).when(pmService).sendMessage(dto.getTitle(), dto.getBody(), dto.getRecipient());
        BindingResult bindingResult = new BeanPropertyBindingResult(dto, "privateMessageDto");

        String view = controller.sendMessage(dto, bindingResult);

        assertEquals(view, "pm/pmForm");
        verify(pmService).sendMessage(dto.getTitle(), dto.getBody(), dto.getRecipient());
    }

    @Test
    public void replyPage() throws NotFoundException {
        PrivateMessage pm = new PrivateMessage();
        //set expectations
        when(pmService.get(PM_ID)).thenReturn(pm);
        when(pmDtoBuilder.getReplyDtoFor(pm)).thenReturn(pmDto);
        when(breadcrumbBuilder.getForumBreadcrumb()).thenReturn(new ArrayList<Breadcrumb>());

        //invoke the object under test
        ModelAndView mav = controller.replyPage(PM_ID);

        //check expectations
        verify(pmService).get(PM_ID);
        verify(pmDtoBuilder).getReplyDtoFor(pm);
        verify(breadcrumbBuilder).getForumBreadcrumb();

        //check result
        assertViewName(mav, "pm/pmForm");
        assertAndReturnModelAttributeOfType(mav, "privateMessageDto", PrivateMessageDto.class);
        assertModelAttributeAvailable(mav, "breadcrumbList");
    }

    @Test
    public void quotePage() throws NotFoundException {
        PrivateMessage pm = new PrivateMessage();
        //set expectations
        when(pmService.get(PM_ID)).thenReturn(pm);
        when(pmDtoBuilder.getQuoteDtoFor(pm)).thenReturn(pmDto);
        when(breadcrumbBuilder.getForumBreadcrumb()).thenReturn(new ArrayList<Breadcrumb>());

        //invoke the object under test
        ModelAndView mav = controller.quotePage(PM_ID);

        //check expectations
        verify(pmService).get(PM_ID);
        verify(pmDtoBuilder).getQuoteDtoFor(pm);

        //check result
        assertViewName(mav, "pm/pmForm");
        assertAndReturnModelAttributeOfType(mav, "privateMessageDto", PrivateMessageDto.class);
        assertModelAttributeAvailable(mav, "breadcrumbList");
    }

    @Test
    public void showPmPageInbox() throws NotFoundException {
        String box = "inbox";
        PrivateMessage pm = new PrivateMessage();

        //set expectations
        when(pmService.get(PM_ID)).thenReturn(pm);
        when(breadcrumbBuilder.getInboxBreadcrumb()).thenReturn(new ArrayList<Breadcrumb>());

        //invoke the object under test
        ModelAndView mav = controller.showPmPage(box, PM_ID);

        //check expectations
        verify(pmService).get(PM_ID);
        verify(pmService).markAsRead(pm);
        verify(breadcrumbBuilder).getInboxBreadcrumb();

        //check result
        assertViewName(mav, "pm/showPm");
        PrivateMessage actualPm = assertAndReturnModelAttributeOfType(mav, "pm", PrivateMessage.class);
        assertEquals(actualPm, pm);
        assertModelAttributeAvailable(mav, "breadcrumbList");
    }

    @Test
    public void showPmPageOutbox() throws NotFoundException {
        String box = "outbox";
        PrivateMessage pm = new PrivateMessage();

        //set expectations
        when(pmService.get(PM_ID)).thenReturn(pm);
        when(breadcrumbBuilder.getOutboxBreadcrumb()).thenReturn(new ArrayList<Breadcrumb>());

        //invoke the object under test
        ModelAndView mav = controller.showPmPage(box, PM_ID);

        //check expectations
        verify(pmService).get(PM_ID);
        verify(breadcrumbBuilder).getOutboxBreadcrumb();

        //check result
        assertViewName(mav, "pm/showPm");
        PrivateMessage actualPm = assertAndReturnModelAttributeOfType(mav, "pm", PrivateMessage.class);
        assertEquals(actualPm, pm);
        assertModelAttributeAvailable(mav, "breadcrumbList");
    }

    @Test
    public void showPmPageDrafts() throws NotFoundException {
        String box = "drafts";
        PrivateMessage pm = new PrivateMessage();

        //set expectations
        when(pmService.get(PM_ID)).thenReturn(pm);
        when(breadcrumbBuilder.getDraftsBreadcrumb()).thenReturn(new ArrayList<Breadcrumb>());

        //invoke the object under test
        ModelAndView mav = controller.showPmPage(box, PM_ID);

        //check expectations
        verify(pmService).get(PM_ID);
        verify(breadcrumbBuilder).getDraftsBreadcrumb();

        //check result
        assertViewName(mav, "pm/showPm");
        PrivateMessage actualPm = assertAndReturnModelAttributeOfType(mav, "pm", PrivateMessage.class);
        assertEquals(actualPm, pm);
        assertModelAttributeAvailable(mav, "breadcrumbList");
    }

    @Test(expectedExceptions = NotFoundException.class)
    public void showPmPageIncorrectFolder() throws NotFoundException {
        String box = "dick";
        
        controller.showPmPage(box, PM_ID);
    }
    
    @Test
    public void editDraftPage() throws NotFoundException {
        PrivateMessage pm = getPrivateMessage();
        pm.setId(PM_ID);
        pm.markAsDraft();

        //set expectations
        when(pmService.get(PM_ID)).thenReturn(pm);
        when(breadcrumbBuilder.getForumBreadcrumb()).thenReturn(new ArrayList<Breadcrumb>());

        //invoke the object under test
        ModelAndView mav = controller.editDraftPage(PM_ID);

        //check expectations
        verify(pmService).get(PM_ID);
        verify(breadcrumbBuilder).getForumBreadcrumb();

        //check result
        assertViewName(mav, "pm/pmForm");
        PrivateMessageDto dto = assertAndReturnModelAttributeOfType(mav, "privateMessageDto", PrivateMessageDto.class);
        assertEquals(dto.getId(), PM_ID);
        assertModelAttributeAvailable(mav, "breadcrumbList");
    }

    @Test(expectedExceptions = NotFoundException.class)
    public void editDraftPageNotDraft() throws NotFoundException {
        PrivateMessage pm = getPrivateMessage();
        when(pmService.get(PM_ID)).thenReturn(pm);

        controller.editDraftPage(PM_ID);
    }

    @Test
    public void saveDraft() throws NotFoundException {
        PrivateMessageDto dto = getPrivateMessageDto();
        BindingResult bindingResult = new BeanPropertyBindingResult(dto, "privateMessageDto");

        String view = controller.saveDraft(dto, bindingResult);

        assertEquals(view, "redirect:/drafts");
        verify(pmService).saveDraft(dto.getId(), dto.getTitle(), dto.getBody(), dto.getRecipient());
    }

    @Test
    public void saveDraftWithErrors() throws NotFoundException {
        PrivateMessageDto dto = getPrivateMessageDto();
        BeanPropertyBindingResult resultWithErrors = mock(BeanPropertyBindingResult.class);
        when(resultWithErrors.hasErrors()).thenReturn(true);

        String view = controller.saveDraft(dto, resultWithErrors);

        assertEquals(view, "pm/pmForm");
        verify(pmService, never()).saveDraft(anyLong(), anyString(), anyString(), anyString());
    }

    @Test
    public void saveDraftWithWrongUser() throws NotFoundException {
        PrivateMessageDto dto = getPrivateMessageDto();
        doThrow(new NotFoundException()).when(pmService)
                .saveDraft(dto.getId(), dto.getTitle(), dto.getBody(), dto.getRecipient());
        BindingResult bindingResult = new BeanPropertyBindingResult(dto, "privateMessageDto");

        String view = controller.saveDraft(dto, bindingResult);

        assertEquals(view, "pm/pmForm");
        assertEquals(bindingResult.getErrorCount(), 1);
        verify(pmService).saveDraft(dto.getId(), dto.getTitle(), dto.getBody(), dto.getRecipient());
    }

    private PrivateMessageDto getPrivateMessageDto() {
        PrivateMessageDto dto = new PrivateMessageDto();
        dto.setBody("body");
        dto.setTitle("title");
        dto.setRecipient("Recipient");
        return dto;
    }

    private PrivateMessage getPrivateMessage() {
        return new PrivateMessage(new User("username", "email", "password"),
                new User("username2", "email2", "password2"), "title", "body");
    }
}
