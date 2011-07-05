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

import org.jtalks.jcommune.model.entity.PrivateMessage;
import org.jtalks.jcommune.model.entity.User;
import org.jtalks.jcommune.service.PrivateMessageService;
import org.jtalks.jcommune.service.exceptions.NotFoundException;
import org.jtalks.jcommune.web.dto.PrivateMessageDto;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.ModelAndViewAssert.assertAndReturnModelAttributeOfType;
import static org.springframework.test.web.ModelAndViewAssert.assertViewName;
import static org.testng.Assert.assertEquals;

/**
 * @author Pavel Vervenko
 * @author Max Malakhov
 */
public class PrivateMessageControllerTest {

    public static final long PM_ID = 2L;
    private PrivateMessageController controller;
    @Mock
    private PrivateMessageService pmService;

    @BeforeMethod
    public void init() {
        MockitoAnnotations.initMocks(this);
        controller = new PrivateMessageController(pmService);
    }

    @Test
    public void displayInboxPageTest() {
        ModelAndView mav = controller.displayInboxPage();

        assertViewName(mav, "pm/inbox");
        assertAndReturnModelAttributeOfType(mav, "pmList", List.class);
        verify(pmService).getInboxForCurrentUser();
    }

    @Test
    public void displayOutboxPageTest() {
        ModelAndView mav = controller.displayOutboxPage();

        assertViewName(mav, "pm/outbox");
        assertAndReturnModelAttributeOfType(mav, "pmList", List.class);
        verify(pmService).getOutboxForCurrentUser();
    }

    @Test
    public void displayNewPMPageTest() {
        ModelAndView mav = controller.displayNewPMPage();

        assertAndReturnModelAttributeOfType(mav, "privateMessageDto", PrivateMessageDto.class);
        assertViewName(mav, "pm/pmForm");
    }

    @Test
    public void testSend() throws NotFoundException {
        PrivateMessageDto dto = getPrivateMessageDto();
        BindingResult bindingResult = new BeanPropertyBindingResult(dto, "privateMessageDto");

        String view = controller.send(dto, bindingResult);

        assertEquals(view, "redirect:/pm/outbox.html");
        verify(pmService).sendMessage(dto.getTitle(), dto.getBody(), dto.getRecipient());
    }

    @Test
    public void testSendDraft() throws NotFoundException {
        PrivateMessageDto dto = getPrivateMessageDto();
        dto.setId(4);
        BindingResult bindingResult = new BeanPropertyBindingResult(dto, "privateMessageDto");

        String view = controller.send(dto, bindingResult);

        assertEquals(view, "redirect:/pm/outbox.html");
        verify(pmService).sendDraft(dto.getId(), dto.getTitle(), dto.getBody(), dto.getRecipient());
    }

    @Test
    public void testSendWithErrors() {
        PrivateMessageDto dto = getPrivateMessageDto();
        BeanPropertyBindingResult resultWithErrors = mock(BeanPropertyBindingResult.class);
        when(resultWithErrors.hasErrors()).thenReturn(true);

        String view = controller.send(dto, resultWithErrors);

        assertEquals(view, "pm/pmForm");
    }

    @Test
    public void testSendWithWrongUser() throws NotFoundException {
        PrivateMessageDto dto = getPrivateMessageDto();
        doThrow(new NotFoundException()).when(pmService).sendMessage(dto.getTitle(), dto.getBody(), dto.getRecipient());
        BindingResult bindingResult = new BeanPropertyBindingResult(dto, "privateMessageDto");

        String view = controller.send(dto, bindingResult);

        assertEquals(view, "pm/pmForm");
        verify(pmService).sendMessage(dto.getTitle(), dto.getBody(), dto.getRecipient());
    }

    @Test
    public void testShowInbox() throws NotFoundException {
        String box = "inbox";
        PrivateMessage pm = new PrivateMessage();
        when(pmService.get(PM_ID)).thenReturn(pm);

        ModelAndView mav = controller.show(box, PM_ID);

        assertViewName(mav, "pm/showPm");
        PrivateMessage actualPm = assertAndReturnModelAttributeOfType(mav, "pm", PrivateMessage.class);
        assertEquals(actualPm, pm);

        verify(pmService).get(PM_ID);
        verify(pmService).markAsRead(pm);
    }

    @Test
    public void testShowOutbox() throws NotFoundException {
        String box = "outbox";
        PrivateMessage pm = new PrivateMessage();
        when(pmService.get(PM_ID)).thenReturn(pm);

        ModelAndView mav = controller.show(box, PM_ID);

        assertViewName(mav, "pm/showPm");
        PrivateMessage actualPm = assertAndReturnModelAttributeOfType(mav, "pm", PrivateMessage.class);
        assertEquals(actualPm, pm);
        verify(pmService).get(PM_ID);
    }

    @Test
    public void testDisplayDraftsPage() {
        ModelAndView mav = controller.displayDraftsPage();

        assertViewName(mav, "pm/drafts");
        assertAndReturnModelAttributeOfType(mav, "pmList", List.class);
        verify(pmService).getDraftsFromCurrentUser();
    }

    @Test
    public void testEdit() throws NotFoundException {
        PrivateMessage pm = PrivateMessage.createNewPrivateMessage();
        pm.setId(PM_ID);
        pm.markAsDraft();
        pm.setUserTo(new User());
        when(pmService.get(PM_ID)).thenReturn(pm);

        ModelAndView mav = controller.edit(PM_ID);

        assertViewName(mav, "pm/pmForm");
        PrivateMessageDto dto = assertAndReturnModelAttributeOfType(mav, "privateMessageDto", PrivateMessageDto.class);
        assertEquals(dto.getId(), PM_ID);
        verify(pmService).get(PM_ID);
    }

    @Test
    public void testEditNotDraft() throws NotFoundException {
        PrivateMessage pm = PrivateMessage.createNewPrivateMessage();
        when(pmService.get(PM_ID)).thenReturn(pm);

        ModelAndView mav = controller.edit(PM_ID);

        assertViewName(mav, "pm/inbox");
    }

    @Test
    public void testSave() throws NotFoundException {
        PrivateMessageDto dto = getPrivateMessageDto();
        BindingResult bindingResult = new BeanPropertyBindingResult(dto, "privateMessageDto");

        String view = controller.save(dto, bindingResult);

        assertEquals(view, "redirect:/pm/drafts.html");
        verify(pmService).saveDraft(dto.getId(), dto.getTitle(), dto.getBody(), dto.getRecipient());
    }

    @Test
    public void testSaveWithErrors() throws NotFoundException {
        PrivateMessageDto dto = getPrivateMessageDto();
        BeanPropertyBindingResult resultWithErrors = mock(BeanPropertyBindingResult.class);
        when(resultWithErrors.hasErrors()).thenReturn(true);

        String view = controller.save(dto, resultWithErrors);

        assertEquals(view, "pm/pmForm");
        verify(pmService, never()).saveDraft(anyLong(), anyString(), anyString(), anyString());
    }

    @Test
    public void testSaveWithWrongUser() throws NotFoundException {
        PrivateMessageDto dto = getPrivateMessageDto();
        doThrow(new NotFoundException()).when(pmService)
                .saveDraft(dto.getId(), dto.getTitle(), dto.getBody(), dto.getRecipient());
        BindingResult bindingResult = new BeanPropertyBindingResult(dto, "privateMessageDto");

        String view = controller.save(dto, bindingResult);

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
}
