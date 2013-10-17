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
package org.jtalks.jcommune.web.filters;

import org.apache.commons.lang.StringUtils;
import org.jtalks.common.security.SecurityService;
import org.jtalks.jcommune.web.logging.LoggerMdc;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * This filter binds a current user to the thread therefore allowing to show her username in each line of logs (which is
 * helpful when it comes to combining several actions of user and looking after the logs). It's implemented by using
 * features like {@link org.slf4j.MDC}, we need to register a username in the beginning and then unregister it in the
 * end of the request so that the memory doesn't leak. <p>Note, that if a user is anonymous, we don't have means to
 * distinguish between them except by using a session id, and that's what we're actually doing. </p>See logger
 * configuration to see where the username is going to appear, for instance in log4j it may look like {@code
 * %X{userName}}.
 *
 * @author Anuar_Nurmakanov
 * @see LoggerMdc
 */
public class LoggingConfigurationFilter implements Filter {
    /**
     * A prefix to be used an anonymous user 'username'.
     */
    private static final String ANONYMOUS_PREFIX = "anonymous-";
    /**
     * How much of session id we need to cut to construct a 'username' for anonymous user. We don't want this number to
     * be big because it will complicate reading of logs.
     */
    private static final Integer SESSION_ID_LENGTH = 4;
    private final SecurityService securityService;
    private final LoggerMdc loggerMdc;

    /**
     * @param securityService to get current user for registration in logging context
     * @param loggerMdc       to register and unregister user in MDC, we don't want to use static methods of logger
     */
    public LoggingConfigurationFilter(SecurityService securityService, LoggerMdc loggerMdc) {
        this.securityService = securityService;
        this.loggerMdc = loggerMdc;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        String currentUserName = securityService.getCurrentUserUsername();
        String sessionId = ((HttpServletRequest) request).getSession().getId();//session always gets created
        boolean successfulRegistered = registerCurrentUserName(currentUserName, sessionId);
        try {
            chain.doFilter(request, response);
        } finally {
            if (successfulRegistered) {
                loggerMdc.unregisterUser();
            }
        }
    }

    /**
     * Register the user in the MDC under USER_KEY.
     *
     * @param userName the name of current user
     * @return true if the username was registered in MDC, false only if the user is anonymous and session id is empty.
     *         If true, you have to unregister the user at some point otherwise the memory will leak.
     */
    private boolean registerCurrentUserName(String userName, String sessionId) {
        if (!StringUtils.isEmpty(userName)) {
            loggerMdc.registerUser(userName);
            return true;
        } else if (isSessionIdValid(sessionId)) {
            loggerMdc.registerUser(ANONYMOUS_PREFIX + StringUtils.right(sessionId, SESSION_ID_LENGTH));
            return true;
        } else {
            return false;
        }
    }

    /**
     * Determines whether session id is possible to use to construct a 'username' for an anonymous user.
     *
     * @param sessionId the session id of the anonymous user
     * @return true if the id is not null
     */
    private boolean isSessionIdValid(String sessionId) {
        return !StringUtils.isEmpty(sessionId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void destroy() {
        //empty
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        //empty
    }

}
