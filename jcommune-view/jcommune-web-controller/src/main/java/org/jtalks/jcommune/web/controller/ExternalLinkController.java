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

import org.jtalks.jcommune.model.entity.ExternalLink;
import org.jtalks.jcommune.service.ExternalLinkService;
import org.jtalks.jcommune.web.dto.json.JsonResponse;
import org.jtalks.jcommune.web.dto.json.JsonResponseStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * Handles CRUD operations for {@link ExternalLink}
 * @author Alexandre Teterin
 *         Date: 03.02.13
 */


@Controller
public class ExternalLinkController {

    private ExternalLinkService service;

    @Autowired
    public ExternalLinkController(ExternalLinkService service) {
        this.service = service;
    }

    @RequestMapping(value="/links", method = RequestMethod.GET)
    @ResponseBody
    public ExternalLink[] getLinks() {
        List<ExternalLink> links = service.getLinks();
        return links.toArray(new ExternalLink[links.size()]);
    }

    @RequestMapping(value = "/links/add", method = RequestMethod.POST)
    @ResponseBody
    public JsonResponse addLink(@RequestBody ExternalLink link) {
        service.addLink(link);
        return new JsonResponse(JsonResponseStatus.SUCCESS);
    }

    @RequestMapping(value = "/links/remove/{id}", method = RequestMethod.DELETE)
    public void removeLink(@PathVariable Long id) {
        service.removeLink(id);
    }
}
