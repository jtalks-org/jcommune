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
package org.jtalks.jcommune.web.controller.migration;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpServletResponse;

/**
 * @author Evgeniy Naumenko
 */
@Controller
public class PhpbbRedirectionController {

    @RequestMapping("/ftopic{id}/**")
    public void showTopic(@PathVariable String id, HttpServletResponse response, WebRequest request) {
        String redirectUrl = request.getContextPath() +  "/topics/" + id;
        this.setHttp301Headers(response, redirectUrl);
    }

    @RequestMapping("/sutra{id}.php")
    public void showPost(@PathVariable String id, HttpServletResponse response, WebRequest request) {
        String redirectUrl = request.getContextPath() +  "/posts/" + id;
        this.setHttp301Headers(response, redirectUrl);
    }

    private void setHttp301Headers(HttpServletResponse response, String newUrl) {
        response.setStatus(301);
        response.setHeader("Location", newUrl);
        response.setHeader("Connection", "close");
    }
}
