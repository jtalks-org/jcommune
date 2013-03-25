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

import org.jtalks.jcommune.service.LastReadPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Handles all "mark all read" requests that aren't related to the list
 * of topics or to concrete branch. So it's handles specific "mark all read"
 * requests, for example "mark all forum as read from recent activity" and
 * "mark all forum as read from main page". 
 * 
 * @author Anuar_Nurmakanov
 *
 */
@Controller
public class ReadPostsController {
    private LastReadPostService lastReadPostService;

    /**
     * Constructs an instance with required fields.
     * 
     * @param lastReadPostService to mark all forum as read for current user
     */
    @Autowired
    public ReadPostsController(LastReadPostService lastReadPostService) {
        this.lastReadPostService = lastReadPostService;
    }

    /**
     * Mark all forum as read for current user from "recent activity" page.
     */
    @RequestMapping(value = "/recent/forum/markread", method = RequestMethod.GET)
    public String markAllForumAsReadFromRecentActivity() {
        lastReadPostService.markAllForumAsReadForCurrentUser();
        return "redirect:/topics/recent";
    }
    
    /**
     * Mark all forum as read for current user from "main page" page.
     */
    @RequestMapping(value = "/main/forum/markread", method = RequestMethod.GET)
    public String markAllForumAsReadFromMainPage() {
        lastReadPostService.markAllForumAsReadForCurrentUser();
        return "redirect:/sections";
    }
}
