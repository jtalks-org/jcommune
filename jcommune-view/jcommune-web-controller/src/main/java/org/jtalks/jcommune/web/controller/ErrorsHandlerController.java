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

import org.jtalks.jcommune.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * This controller is needed to handle errors and set correct status
 *
 * @author Andrey Ivanov
 */
@Controller
@RequestMapping("/errors/")
public class ErrorsHandlerController {

    @Autowired
    private UserService userService;

    @RequestMapping(value = "500")
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleInternalServerError() {
        return "/errors/500";
    }

    @RequestMapping(value = "404")
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public String handleNotFoundError() {
        return "/errors/404";
    }

    @RequestMapping(value = "redirect/404")
    public String handleNotFoundRedirect() {
        return "redirect:/errors/404";
    }

    @RequestMapping(value = "400")
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public String handleBadRequestError() {
        return "/errors/400";
    }

    @RequestMapping(value = "redirect/403")
    public String handleForbiddenRedirect() {
        return "redirect:/errors/403";
    }

    @RequestMapping(value = "403")
    @ResponseStatus(value = HttpStatus.FORBIDDEN)
    public String handleForbiddenError() {
        return "/errors/accessDenied";
    }

    @RequestMapping(value = "redirect/501")
    public String handleNotImplementedRedirect() {
        return "redirect:/errors/501";
    }

    @RequestMapping(value = "501")
    @ResponseStatus(value = HttpStatus.NOT_IMPLEMENTED)
    public String handleNotImplementedError() {
        return "/errors/501";
    }
    @RequestMapping(value = "405")
    @ResponseStatus(value = HttpStatus.METHOD_NOT_ALLOWED)
    public String handleMethodNotSupportedError() {
        return "/errors/405";
    }
}
