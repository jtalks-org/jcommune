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

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.web.authentication.rememberme.RememberMeAuthenticationException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * This controller is needed to handle exceptions that occurred during 
 * servlet filter. Note, that to handle such kind exceptions we must define
 * error-page rule in web.xml file. 
 * @see <a href="http://jira.jtalks.org/browse/JC-1185"/>
 * 
 * @author Anuar_Nurmakanov
 *
 */
@Controller
@RequestMapping("/exception")
public class ExceptionHandlerController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionHandlerController.class);
    static final String REDIRECT_TO_LOGIN = "redirect:/login?login_error=2";
    
    /**
     * Handle the remember me exception. This method is called by servlet container,
     * because web.xml contains error-page rule for this type exception and this rule
     * defines this path of this controller as handler.
     * 
     * @param request it's needed to get an exception
     * @return the path of redirect url 
     */
    @RequestMapping("/rememberMe")
    public String handleRememberMeException(HttpServletRequest request) {
        RememberMeAuthenticationException exception = (RememberMeAuthenticationException)
                request.getAttribute("javax.servlet.error.exception");
        LOGGER.error("RememberMe exception:", exception);
        return REDIRECT_TO_LOGIN;
    }
}
