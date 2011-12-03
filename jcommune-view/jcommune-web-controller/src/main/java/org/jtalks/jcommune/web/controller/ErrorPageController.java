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

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controlles serving different error pages.
 *
 * @author Evgeniy Naumenko
 */
@Controller
public class ErrorPageController {

    public static final String NOT_FOUND_PAGE_VIEW = "/errors/404";

    /**
     * This controller handles all pages we're unable to find mapping for.
     * By default DispatcherSerlet will return bare 404 code to the user and
     * we can't override this behavior. We can, however, provide DispacherServlet
     * with this fake mapping to handle all unmapped requests.
     *
     * @return 404 page view
     */
    @RequestMapping(value = "/errors/404")
    public String get404Page() {
        return NOT_FOUND_PAGE_VIEW;
    }

    /**
     * This controller redirects all 404-code responses to the method above.
     * We need an explicit redirect since default web.xml error code forwarding
     * bypasses filters, interceptors and so on, which is inappropriate as we
     * want our filter to be applied even to the error pages.
     *
     * @return ErrorPageController.NOT_FOUND_PAGE_VIEW redirect url
     */
    @RequestMapping(value = "/errors/redirect/404")
    public String redirect404() {
        return "redirect:" + NOT_FOUND_PAGE_VIEW;
    }
}
