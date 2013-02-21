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
import org.jtalks.jcommune.web.dto.json.JsonResponse;
import org.jtalks.jcommune.web.dto.json.JsonResponseStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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

    @Autowired
    public ExternalLinkController(ExternalLinkService service, ComponentService componentService) {
        this.componentService = componentService;
        this.service = service;
    }

    @RequestMapping(value = "/links/save", method = RequestMethod.POST)
    @ResponseBody
    public JsonResponse saveLink(@RequestBody ExternalLink link) {
        Component component = componentService.getComponentOfForum();
        service.saveLink(link, component);
        return new JsonResponse(JsonResponseStatus.SUCCESS, link);
    }

    @RequestMapping(value = "/links/delete/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    public JsonResponse deleteLink(@PathVariable Long id) {
        Component component = componentService.getComponentOfForum();
        boolean result = service.deleteLink(id, component);
        return new JsonResponse(JsonResponseStatus.SUCCESS, result);
    }
}
