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

import org.jtalks.common.model.entity.Component;
import org.jtalks.jcommune.model.entity.ExternalLink;
import org.jtalks.jcommune.service.ComponentService;
import org.jtalks.jcommune.service.ExternalLinkService;
import org.jtalks.jcommune.plugin.api.web.dto.json.JsonResponse;
import org.jtalks.jcommune.plugin.api.web.dto.json.JsonResponseStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;

/**
 * Handles CRUD operations for {@link ExternalLink}.
 *
 * @author Alexandre Teterin
 *         Date: 03.02.13
 */
@Controller
public class ExternalLinkController {
    private ComponentService componentService;
    private ExternalLinkService service;

    /**
     * @param service          {@link ExternalLinkService} for link CRUD operations.
     * @param componentService {@link ComponentService} for authorization check purpose.
     */
    @Autowired
    public ExternalLinkController(ExternalLinkService service, ComponentService componentService) {
        this.componentService = componentService;
        this.service = service;
    }

    /**
     * Create new or save existing {@link ExternalLink}.
     *
     * @param link   {@link ExternalLink} for saving.
     * @param result link validation result.
     * @return response with SUCCESS status and saved link.
     */
    @RequestMapping(value = "/links/save", method = RequestMethod.POST)
    @ResponseBody
    public JsonResponse saveLink(@Valid @RequestBody ExternalLink link, BindingResult result) {
        if (result.hasErrors()) {
            return new JsonResponse(JsonResponseStatus.FAIL, result.getAllErrors());
        }
        Component component = componentService.getComponentOfForum();
        service.saveLink(link, component);
        return new JsonResponse(JsonResponseStatus.SUCCESS, link);
    }

    /**
     * Delete {@link ExternalLink} with specified id.
     *
     * @param id link id to deletion.
     * @return {@code true} if link was successfully deleted.
     */
    @RequestMapping(value = "/links/delete/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    public JsonResponse deleteLink(@PathVariable Long id) {
        Component component = componentService.getComponentOfForum();
        boolean result = service.deleteLink(id, component);
        return new JsonResponse(JsonResponseStatus.SUCCESS, result);
    }
}
