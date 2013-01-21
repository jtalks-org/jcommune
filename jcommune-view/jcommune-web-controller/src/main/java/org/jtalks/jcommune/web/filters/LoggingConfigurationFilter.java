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

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.commons.lang.StringUtils;
import org.jtalks.common.security.SecurityService;
import org.jtalks.jcommune.web.logging.LoggingMDCService;

/**
 * This filter provides an ability to register
 * 
 * @author Anuar_Nurmakanov
 *
 */
public class LoggingConfigurationFilter implements Filter {
    
    private SecurityService securityService;
    private LoggingMDCService loggingMDCService;
    
    /**
     * Constructs an instance with required fields.
     * 
     * @param securityService to get current user for registration in logging context
     * @param loggingMDCService to register and unregister user in MDC
     */
    public LoggingConfigurationFilter(
            SecurityService securityService,
            LoggingMDCService loggingMDCService) {
        this.securityService = securityService;
        this.loggingMDCService = loggingMDCService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        //empty
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        String currentUserName = securityService.getCurrentUserUsername();
        boolean successfulRegistered = registerCurrentUserName(currentUserName);
        try {
            chain.doFilter(request, response);
        } finally {
            if (successfulRegistered) {
                loggingMDCService.unregisterUser();
            }
        }
    }
    
    /**
     * Register the user in the MDC under USER_KEY.
     * 
     * @param userName the name of current user
     * @return true id the user can be successfully registered
     */
    private boolean registerCurrentUserName(String userName) {
      if (!StringUtils.isEmpty(userName)) {
          loggingMDCService.registerUser(userName);
          return true;
      }
      return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void destroy() {
      //empty  
    }
}
