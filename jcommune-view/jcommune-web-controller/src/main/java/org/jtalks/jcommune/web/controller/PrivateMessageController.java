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
import org.jtalks.jcommune.service.PrivateMessageService;
import org.jtalks.jcommune.service.exceptions.NotFoundException;
import org.jtalks.jcommune.service.nontransactional.BBCodeService;
import org.jtalks.jcommune.web.dto.PrivateMessageDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;

/**
 * MVC controller for Private Messaging. Handles request for inbox, outbox and new private messages.
 *
 * @author Pavel Vervenko
 * @author Max Malakhov
 * @author Kirill Afonin
 * @author Alexandre Teterin
 */
@Controller
public class PrivateMessageController {

    public static final String BREADCRUMB_LIST = "breadcrumbList";
    private PrivateMessageService pmService;
    private BBCodeService bbCodeService;

    //constants are moved here when occurs 4 or more times, as project PMD rule states
    private static final String PM_FORM = "pm/pmForm";
    private static final String PM_ID = "pmId";
    private static final String DTO = "privateMessageDto";

    /**
     * This method turns the trim binder on. Trim builder
     * removes leading and trailing spaces from the submitted fields.
     * So, it ensures, that all validations will be applied to
     * trimmed field values only.
     *
     * @param binder Binder object to be injected
     */
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
    }

    /**
     * @param pmService     for PrivateMessage-related operation
     * @param bbCodeService for qutes creation
     */
    @Autowired
    public PrivateMessageController(PrivateMessageService pmService, BBCodeService bbCodeService) {
        this.pmService = pmService;
        this.bbCodeService = bbCodeService;
    }

    /**
     * Render the PM inbox page with the list of incoming messages for the /inbox URI.
     *
     * @return {@code ModelAndView} with added list of inbox messages
     */
    @RequestMapping(value = "/inbox", method = RequestMethod.GET)
    public ModelAndView inboxPage() {
        return new ModelAndView("pm/inbox").addObject("pmList", pmService.getInboxForCurrentUser());
    }

    /**
     * Render the PM outbox page with the list of sent messages for the /outbox URI.
     *
     * @return {@code ModelAndView} with added list of outbox messages
     */
    @RequestMapping(value = "/outbox", method = RequestMethod.GET)
    public ModelAndView outboxPage() {
        return new ModelAndView("pm/outbox").addObject("pmList", pmService.getOutboxForCurrentUser());
    }

    /**
     * Get list of current user's list of draft messages.
     *
     * @return {@code ModelAndView} with list of messages
     */
    @RequestMapping(value = "/drafts", method = RequestMethod.GET)
    public ModelAndView draftsPage() {
        return new ModelAndView("pm/drafts").addObject("pmList", pmService.getDraftsFromCurrentUser());
    }

    /**
     * Render the page with a form for creation new Private Message with empty binded {@link PrivateMessageDto}.
     *
     * @return {@code ModelAndView} with the form
     */
    @RequestMapping(value = "/pm/new", method = RequestMethod.GET)
    public ModelAndView newPmPage() {
        return new ModelAndView(PM_FORM).addObject(DTO, new PrivateMessageDto());
    }

    /**
     * Render the page with the form for the reply to original message.
     * The form has the next filled fields: recipient, title
     *
     * @param id {@link PrivateMessage} id
     * @return {@code ModelAndView} with the message having filled recipient, title fields
     * @throws org.jtalks.jcommune.service.exceptions.NotFoundException
     *          when message not found
     */
    @RequestMapping(value = "/reply/{pmId}", method = RequestMethod.GET)
    public ModelAndView replyPage(@PathVariable(PM_ID) Long id) throws NotFoundException {
        PrivateMessage pm = pmService.get(id);
        PrivateMessageDto object = PrivateMessageDto.getReplyDtoFor(pm);
        return new ModelAndView(PM_FORM).addObject(DTO, object);
    }

    /**
     * Render the page with the form for the reply with quoting to original message.
     * The form has the next filled fields: recipient, title, message
     *
     * @param id {@link PrivateMessage} id
     * @return {@code ModelAndView} with the message having filled recipient, title, message fields
     * @throws org.jtalks.jcommune.service.exceptions.NotFoundException
     *          when message not found
     */
    @RequestMapping(value = "/quote/{pmId}", method = RequestMethod.GET)
    public ModelAndView quotePage(@PathVariable(PM_ID) Long id) throws NotFoundException {
        PrivateMessage pm = pmService.get(id);
        PrivateMessageDto dto = PrivateMessageDto.getReplyDtoFor(pm);
        dto.setBody(bbCodeService.quote(pm.getBody(), pm.getUserFrom()));
        return new ModelAndView(PM_FORM).addObject(DTO, dto);
    }

    /**
     * Save the PrivateMessage for the filled in PrivateMessageDto.
     *
     * @param pmDto  {@link PrivateMessageDto} populated in form
     * @param result result of {@link PrivateMessageDto} validation
     * @return redirect to /inbox on success or back to "/new_pm" on validation errors
     * @throws NotFoundException is invalid user set as recipient
     */
    @RequestMapping(value = "/pm", method = {RequestMethod.POST, RequestMethod.GET})
    public String sendMessage(@Valid @ModelAttribute PrivateMessageDto pmDto,
                              BindingResult result) throws NotFoundException {
        if (result.hasErrors()) {
            return PM_FORM;
        }
        if (pmDto.getId() > 0) {
            pmService.sendDraft(pmDto.getId(), pmDto.getTitle(), pmDto.getBody(), pmDto.getRecipient());
        } else {
            pmService.sendMessage(pmDto.getTitle(), pmDto.getBody(), pmDto.getRecipient());
        }
        return "redirect:/outbox";
    }

    /**
     * Show page with private message details.
     *
     * @param id     {@link PrivateMessage} id
     * @return {@code ModelAndView} with a message
     * @throws NotFoundException when message not found
     */
    @RequestMapping(value = "/pm/{pmId}", method = RequestMethod.GET)
    public ModelAndView showPmPage(@PathVariable(PM_ID) Long id) throws NotFoundException {
        PrivateMessage pm = pmService.get(id);
        return new ModelAndView("pm/showPm").addObject("pm", pm);
    }

    /**
     * Edit private message page.
     *
     * @param id {@link PrivateMessage} id
     * @return private message form view and populated form dto
     * @throws NotFoundException when message not found
     */
    @RequestMapping(value = "/pm/{pmId}/edit", method = RequestMethod.GET)
    public ModelAndView editDraftPage(@PathVariable(PM_ID) Long id) throws NotFoundException {
        PrivateMessage pm = pmService.get(id);
        if (!pm.isDraft()) {
            // todo: 404? we need something more meaninful here
            throw new NotFoundException("Edit allowed only for draft messages.");
        }
        return new ModelAndView(PM_FORM).addObject(DTO, PrivateMessageDto.getFullPmDtoFor(pm));
    }

    /**
     * Save private message as draft. As draft message is not requred to be valid
     *
     * @param pmDto  Dto populated in form
     * @return redirect to "drafts" folder if saved successfully or show form with error message
     * @throws NotFoundException if incorrect User is set as recipient
     */
    @RequestMapping(value = "/pm/save", method = {RequestMethod.POST, RequestMethod.GET})
    public String saveDraft(@ModelAttribute PrivateMessageDto pmDto) throws NotFoundException {
        pmService.saveDraft(pmDto.getId(), pmDto.getTitle(), pmDto.getBody(), pmDto.getRecipient());
        return "redirect:/drafts";
    }

}
