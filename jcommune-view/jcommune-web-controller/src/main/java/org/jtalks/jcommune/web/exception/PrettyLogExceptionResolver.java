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
import org.springframework.beans.TypeMismatchException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.Principal;
import java.util.Enumeration;
import java.util.Properties;

/**
 * Catches all the exceptions thrown in controllers, logs them and directs to the error pages. The standard
 * {@link org.springframework.web.servlet.handler.SimpleMappingExceptionResolver} wasn't sufficient because it was logging all exceptions as warnings while
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
            logger.info(getLogMessage(request, ex));
        } else if (ex instanceof TypeMismatchException) {
            logger.info(getLogMessage(request, ex));
        } else if (ex instanceof AccessDeniedException) {
            String url = request.getRequestURL().toString();
            Principal principal = request.getUserPrincipal();
            String user = principal != null ? principal.getName() : NOT_AUTHORIZED_USERNAME;
            String accessDeniedMessage = String.format(ACCESS_DENIED_MESSAGE, user, request.getMethod(), url);
            logger.info(accessDeniedMessage);
        } else {
            super.logException(ex, request);
        }
    }

    /**
     * Get info about occured exception: request method, url, cookies and data.
     * @param request request
     * @param ex exception
     * @return log message
     */
    private String getLogMessage(HttpServletRequest request, Exception ex) {
        String data = "";
        String exceptionMessage = ex.getMessage();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()));
            String line;
            StringBuilder stringBuilder = new StringBuilder();
            while ((line=reader.readLine()) != null ) {
                stringBuilder.append(line).append("\n");
            }
            data = stringBuilder.append(exceptionMessage).toString();
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

    /**
     * {@inheritDoc}
     */
    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response,
                                         Object handler, Exception ex) {
        if (shouldApplyTo(request, handler)) {
            logException(ex, request);
            prepareResponse(ex, response);
            return doResolveException(request, response, handler, ex);
        }
        else {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected ModelAndView getModelAndView(String viewName, Exception ex) {
        return new ModelAndView(viewName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String findMatchingViewName(Properties exceptionMappings, Exception ex) {
        String viewName = null;
        String dominantMapping = null;
        int deepest = Integer.MAX_VALUE;
        for (Enumeration<?> names = exceptionMappings.propertyNames(); names.hasMoreElements();) {
            String exceptionMapping = (String) names.nextElement();
            int depth = getDepth(exceptionMapping, ex);
            if (depth >= 0 && (depth < deepest || (depth == deepest &&
                    dominantMapping != null && exceptionMapping.length() > dominantMapping.length()))) {
                deepest = depth;
                dominantMapping = exceptionMapping;
                viewName = exceptionMappings.getProperty(exceptionMapping);
            }
        }
        return viewName;
    }
}
