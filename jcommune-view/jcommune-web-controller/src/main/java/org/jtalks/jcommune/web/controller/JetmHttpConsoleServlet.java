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

import etm.contrib.integration.spring.web.SpringHttpConsoleServlet;
import org.jtalks.jcommune.model.utils.JndiAwarePropertyPlaceholderConfigurer;
import org.jtalks.jcommune.service.ComponentService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.apache.commons.lang3.StringUtils.defaultIfBlank;

/**
 * <p>Servlet that provides access to JETM HTTP-console like the
 * {@link etm.contrib.integration.spring.web.SpringHttpConsoleServlet}, but additionally check JNDI for the performance
 * Spring profile activation and user permissions to view JETM HTTP-console.</p>
 * <p>Note: probably the work of this overridden servlet can be replaced by the The Servlet 3.0 Dynamic Registration
 * when servlet container will support it.</p>
 *
 * @author Guram Savinov
 */
public class JetmHttpConsoleServlet extends SpringHttpConsoleServlet {

    private static final String ACTIVE_PROFILE_PROPERTY = "spring.profiles.active";
    private static final String ACTIVE_PROFILE_ENV_VAR = "SPRING_PROFILES_ACTIVE";
    private static final String PERFORMANCE_PROFILE = "performance";

    private ComponentService componentService;

    /**
     * Invoke servlet initialization only when Spring performance profile is set, otherwise skip it. This skipping is
     * needed because without it {@code ServletException} will be thrown and servlet container return
     * HTTP 500 Internal Server Error.
     *
     * @param aServletConfig the servlet configuration
     * @throws ServletException
     */
    @Override
    public void init(ServletConfig aServletConfig) throws ServletException {
        if (profileIsActive()) {
            super.init(aServletConfig);
            WebApplicationContext ctx = WebApplicationContextUtils
                    .getRequiredWebApplicationContext(servletConfig.getServletContext());
            componentService = (ComponentService) ctx.getBean("componentService");
        }
    }

    /**
     * Check for the Spring performance profile and depends from this return JETM HTTP-console page
     * or HTTP 404 Not Found error.
     * Return HTTP 403 Forbidden error if current user haven't permissions to view the JETM HTTP-console.
     *
     * @param req the HTTP request
     * @param resp the HTTP response
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (profileIsActive()) {
            long componentId = componentService.getComponentOfForum().getId();
            try {
                componentService.checkPermissionsForComponent(componentId);
                super.doGet(req, resp);
            } catch (AccessDeniedException e) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN);
            }
        } else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private boolean profileIsActive() {
        String profile = new JndiAwarePropertyPlaceholderConfigurer().resolveJndiProperty(ACTIVE_PROFILE_PROPERTY);
        profile = defaultIfBlank(profile, System.getProperty(ACTIVE_PROFILE_PROPERTY));
        profile = defaultIfBlank(profile, System.getenv(ACTIVE_PROFILE_ENV_VAR));
        return PERFORMANCE_PROFILE.equals(profile);
    }
}
