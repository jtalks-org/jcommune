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

import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import java.util.List;
import org.jtalks.jcommune.service.PrivateMessageService;
import org.jtalks.jcommune.service.exceptions.NotFoundException;
import org.jtalks.jcommune.web.dto.PrivateMessageDto;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.servlet.ModelAndView;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.springframework.test.web.ModelAndViewAssert.assertAndReturnModelAttributeOfType;
import static org.springframework.test.web.ModelAndViewAssert.assertViewName;
import static org.mockito.Mockito.*;

/**
 *
 * @author Pavel Vervenko
 */
public class PrivateMessageControllerTest {

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
        assertViewName(mav, "pm/newPm");
    }

    @Test
    public void submitNewPMTest() throws NotFoundException {
        PrivateMessageDto dto = getPrivateMessageDto();
        BindingResult bindingResult = new BeanPropertyBindingResult(dto, "privateMessageDto");
        ModelAndView mav = controller.submitNewPM(dto, bindingResult);

        assertViewName(mav, "redirect:/pm/outbox.html");
        verify(pmService).sendMessage(dto.getTitle(), dto.getBody(), dto.getRecipient());
    }

    @Test
    public void submitWithErrors() {
        PrivateMessageDto dto = getPrivateMessageDto();
        BeanPropertyBindingResult resultWithErrors = mock(BeanPropertyBindingResult.class);
        when(resultWithErrors.hasErrors()).thenReturn(Boolean.TRUE);
        
        ModelAndView mav = controller.submitNewPM(dto, resultWithErrors);
        
        assertViewName(mav, "pm/newPm");
    }
    
    @Test
    public void submitWithWrongUser() throws NotFoundException {
        PrivateMessageDto dto = getPrivateMessageDto();
        doThrow(new NotFoundException()).when(pmService).sendMessage(dto.getTitle(), dto.getBody(), dto.getRecipient());
        
        BindingResult bindingResult = new BeanPropertyBindingResult(dto, "privateMessageDto");
        ModelAndView mav = controller.submitNewPM(dto, bindingResult);

        assertViewName(mav, "pm/newPm");
        verify(pmService).sendMessage(dto.getTitle(), dto.getBody(), dto.getRecipient());
    }
    
    private PrivateMessageDto getPrivateMessageDto() {
        PrivateMessageDto dto = new PrivateMessageDto();
        dto.setBody("body");
        dto.setTitle("title");
        dto.setRecipient("Recipient");
        return dto;
    }
}
