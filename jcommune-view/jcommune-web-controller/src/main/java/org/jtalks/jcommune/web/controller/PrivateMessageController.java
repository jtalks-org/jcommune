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

import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.model.entity.PrivateMessage;
import org.jtalks.jcommune.model.entity.PrivateMessageStatus;
import org.jtalks.jcommune.service.PrivateMessageService;
import org.jtalks.jcommune.service.UserService;
import org.jtalks.jcommune.plugin.api.exceptions.NotFoundException;
import org.jtalks.jcommune.service.nontransactional.BBCodeService;
import org.jtalks.jcommune.web.dto.PrivateMessageDraftDto;
import org.jtalks.jcommune.web.dto.PrivateMessageDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;

/**
 * MVC controller for Private Messaging. Handles request for inbox, outbox and new private messages.
 *
 * @author Pavel Vervenko
 * @author Max Malakhov
 * @author Kirill Afonin
 * @author Alexandre Teterin
 * @author Guram Savinov
 */
@Controller
public class PrivateMessageController {

    public static final String PM_IDENTIFIERS = "pmIdentifiers";
    private PrivateMessageService pmService;
    private BBCodeService bbCodeService;
    private UserService userService;

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
     * @param userService   to get current user
     */
    @Autowired
    public PrivateMessageController(PrivateMessageService pmService, BBCodeService bbCodeService,
                                    UserService userService) {
        this.pmService = pmService;
        this.bbCodeService = bbCodeService;
        this.userService = userService;
    }

    /**
     * Render the PM page with the list of incoming messages for the /inbox URI.
     *
     * @param page the private message page number.
     * @return {@code ModelAndView} with added {@link Page} instance with of private messages.
     */
    @RequestMapping(value = {"/inbox","/pm"}, method = RequestMethod.GET)
    public ModelAndView inboxPage(@RequestParam(value = "page", defaultValue = "1", required = false) String page) {

        Page<PrivateMessage> inboxPage = pmService.getInboxForCurrentUser(page);

        return new ModelAndView("pm/inbox")
                .addObject("inboxPage", inboxPage);
    }

    /**
     * Render the PM outbox page with the list of sent messages for the /outbox URI.
     *
     * @param page the private message page number.
     * @return {@code ModelAndView} with added {@link Page} instance with of private messages.
     */
    @RequestMapping(value = "/outbox", method = RequestMethod.GET)
    public ModelAndView outboxPage(@RequestParam(value = "page", defaultValue = "1", required = false) String page) {
        Page<PrivateMessage> outboxPage = pmService.getOutboxForCurrentUser(page);

        return new ModelAndView("pm/outbox")
                .addObject("outboxPage", outboxPage);
    }

    /**
     * Render the PM draft page with the list of draft messages for the /outbox URI.
     *
     * @param page the private message page number.
     * @return {@code ModelAndView} with added {@link Page} instance with of private messages.
     */
    @RequestMapping(value = "/drafts", method = RequestMethod.GET)
    public ModelAndView draftsPage(@RequestParam(value = "page", defaultValue = "1", required = false) String page) {
        Page<PrivateMessage> draftsPage = pmService.getDraftsForCurrentUser(page);
        return new ModelAndView("pm/drafts")
                .addObject("draftsPage", draftsPage);
    }

    /**
     * Render the page with a form for creation new Private Message with empty {@link PrivateMessageDto} bound.
     *
     * @return {@code ModelAndView} with the form
     */
    @RequestMapping(value = "/pm/new", method = RequestMethod.GET)
    public ModelAndView newPmPage(@RequestParam(value = "recipientId", required = false) Long recipientId)
            throws NotFoundException {
        Long senderId = userService.getCurrentUser().getId();
        pmService.checkPermissionsToSend(senderId);
        PrivateMessageDto pmDto = new PrivateMessageDto();
        if (recipientId != null) {
            String name = userService.get(recipientId).getUsername();
            pmDto.setRecipient(name);
        }
        return new ModelAndView(PM_FORM)
                .addObject(DTO, pmDto);
    }

    /**
     * Render the page with the form for the reply to original message.
     * The form has the next filled fields: recipient, title
     *
     * @param id {@link PrivateMessage} id
     * @return {@code ModelAndView} with the message having filled recipient, title fields
     * @throws NotFoundException when message not found
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
     * @throws NotFoundException when message not found
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
    @RequestMapping(value = "/pm/new", method = RequestMethod.POST)
    public ModelAndView sendMessage(@Valid @ModelAttribute PrivateMessageDto pmDto,
                                    BindingResult result) throws NotFoundException {
        if (result.hasErrors()) {
            return new ModelAndView(PM_FORM)
                    .addObject(DTO, pmDto);
        }
        JCUser userFrom = userService.getCurrentUser();
        JCUser userTo = userService.getByUsername(pmDto.getRecipient());
        // todo: we can easily get current user in service
        if (pmDto.getId() > 0) {
            pmService.sendDraft(pmDto.getId(), pmDto.getTitle(), pmDto.getBody(), userTo, userFrom);
        } else {
            pmService.sendMessage(pmDto.getTitle(), pmDto.getBody(), userTo, userFrom);
        }
        return new ModelAndView("redirect:/outbox");
    }

    /**
     * Show page with private message details.
     *
     * @param id {@link PrivateMessage} id
     * @return {@code ModelAndView} with a message
     * @throws NotFoundException when message not found
     */
    @RequestMapping(value = {"/pm/inbox/{pmId}", "/pm/outbox/{pmId}"}, method = RequestMethod.GET)
    public ModelAndView showPmPage(@PathVariable(PM_ID) Long id) throws NotFoundException {
        PrivateMessage pm = pmService.get(id);
        return new ModelAndView("pm/showPm")
                .addObject("pm", pm)
                .addObject("user", userService.getCurrentUser());
    }

    /**
     * Edit private message page.
     *
     * @param id {@link PrivateMessage} id
     * @return private message form view and populated form dto
     * @throws NotFoundException when message not found
     */
    @RequestMapping(value = "/pm/drafts/{pmId}/edit", method = RequestMethod.GET)
    public ModelAndView editDraftPage(@PathVariable(PM_ID) Long id) throws NotFoundException {
        PrivateMessage pm = pmService.get(id);
        if (!pm.getStatus().equals(PrivateMessageStatus.DRAFT)) {
            // todo: 404? we need something more meaninful here
            throw new NotFoundException("Edit allowed only for draft messages.");
        }
        return new ModelAndView(PM_FORM).addObject(DTO, PrivateMessageDto.getFullPmDtoFor(pm));
    }

    /**
     * Save private message as draft. As draft message is not requred to be valid
     *
     * @param pmDto  Dto populated in form
     * @param result validation result
     * @return redirect to "drafts" folder if saved successfully or show form with error message
     */
    @RequestMapping(value = "/pm/save", method = {RequestMethod.POST, RequestMethod.GET})
    public String saveDraft(@Valid @ModelAttribute PrivateMessageDraftDto pmDto, BindingResult result) {
        String targetView = "redirect:/drafts";
        if (result.hasFieldErrors() && result.hasGlobalErrors()) {
            if (pmDto.getId() != 0) { //means that we try to edit existing draft
                try {
                    pmService.delete(Arrays.asList(pmDto.getId()));
                } catch (NotFoundException e) {
                    // Catch block is empty because we don't need any additional logic in case if user removed
                    // draft in separate browser tab. We should just redirect him to list of drafts
                }
            }
            return targetView;
        }

        JCUser userFrom = userService.getCurrentUser();
        JCUser userTo = null;
        if (pmDto.getRecipient() != null) {
            try {
                userTo = userService.getByUsername(pmDto.getRecipient());
            } catch (NotFoundException e) {
                //Catch block is empty because we don't need any logic if recipient not found. We should leave it null
            }
        }
        pmService.saveDraft(pmDto.getId(), userTo, pmDto.getTitle(), pmDto.getBody(), userFrom);

        return targetView;
    }

    /**
     * Delete private messages.
     *
     * @param ids Comma-separated identifiers of the private messages for deletion
     * @return redirect to folder from what request is come
     * @throws NotFoundException if message hasn't been found
     */
    @RequestMapping(value = "/pm", method = {RequestMethod.DELETE})
    public String deleteMessages(@RequestParam(PM_IDENTIFIERS) List<Long> ids) throws NotFoundException {
        String url = pmService.delete(ids);
        return "redirect:/" + url;
    }

}
