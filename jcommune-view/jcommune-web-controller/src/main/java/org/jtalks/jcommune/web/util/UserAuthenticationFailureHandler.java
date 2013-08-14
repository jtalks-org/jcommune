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
package org.jtalks.jcommune.web.util;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * <tt>AuthenticationFailureHandler</tt> which extends <tt>SimpleUrlAuthenticationFailureHandler</tt>
 * by adding to the Session an attribute with value of username which was used in authentication
 * @author Andrei Alikov
 */
public class UserAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    /**
     * Default session attribute name storing username
     */
    public static final String DEFAULT_USERNAME_SESSION_ATTRIBUTE = "username";

    private String usernameSessionAttribute = DEFAULT_USERNAME_SESSION_ATTRIBUTE;

    /**
     * {@inheritDoc}
     */
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {

        HttpSession session = request.getSession(false);
        if ((session != null || isAllowSessionCreation()) && exception.getAuthentication() != null) {
            request.getSession().setAttribute(usernameSessionAttribute, exception.getAuthentication().getName());
        }

        super.onAuthenticationFailure(request, response, exception);
    }

    /**
     * Gets name of Session's attribute which is used to store username.
     * Default value is DEFAULT_USERNAME_SESSION_ATTRIBUTE
     * @return name of Session attribute which is used to store username
     */
    public String getUsernameSessionAttribute() {
        return usernameSessionAttribute;
    }

    /**
     * Set the name of Session's attribute which is used to store username
     * @param usernameSessionAttribute
     */
    public void setUsernameSessionAttribute(String usernameSessionAttribute) {
        this.usernameSessionAttribute = usernameSessionAttribute;
    }
}
