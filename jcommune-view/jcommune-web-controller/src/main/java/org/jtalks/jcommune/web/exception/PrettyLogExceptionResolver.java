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
package org.jtalks.jcommune.web.exception;

import org.jtalks.jcommune.plugin.api.exceptions.NotFoundException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.Principal;

/**
 * Catches all the exceptions thrown in controllers, logs them and directs to the error pages. The standard
 * {@link SimpleMappingExceptionResolver} wasn't sufficient because it was logging all exceptions as warnings while
 * some of them are expected and should be logged as INFO (such as 404 topic not found).
 *
 * @author Vitaliy Kravchenko
 */
public class PrettyLogExceptionResolver extends SimpleMappingExceptionResolver {
    /** Template message for logging AccessDeniedException */
    private static final String ACCESS_DENIED_MESSAGE = "Access was denied for user [%s] trying to %s %s";
    /** Constant for anonymous user */
    private static final String NOT_AUTHORIZED_USERNAME = "anonymousUser";

    /**
     * {@inheritDoc}
     */
    @Override
    protected void logException(Exception ex, HttpServletRequest request) {
        if (ex instanceof NotFoundException) {
            logger.info(ex.getMessage());
        } else if (ex instanceof AccessDeniedException) {
            String url = request.getRequestURL().toString();
            Principal principal = request.getUserPrincipal();
            String user = principal != null ? principal.getName() : NOT_AUTHORIZED_USERNAME;
            String accessDeniedMessage = String.format(ACCESS_DENIED_MESSAGE, user, request.getMethod(), url);
            logger.info(accessDeniedMessage);
        } else {
            super.logException(ex, request);
        }
        logger.info(getLogMessage(request));
    }

    /**
     * Get info about occured exception: request method, url, cookies and data.
     * @param request request
     * @return log message
     */
    private String getLogMessage(HttpServletRequest request) {
        String data = "";
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()));
            String line;
            StringBuilder stringBuilder = new StringBuilder();
            while ((line=reader.readLine()) != null ) {
                stringBuilder.append(line).append("\n");
            }
            data = stringBuilder.toString();
        } catch (IOException e) {
            logger.warn("Could not parse data from request");
        }
        String queryString = request.getQueryString();
        String url = request.getRequestURL().toString();
        if (queryString != null && !queryString.isEmpty()) {
            url += "?" + queryString;
        }
        return String.format("[%s][%s][%s][%s]", request.getMethod(), url, request.getHeader("Cookie"), data);
    }
}
