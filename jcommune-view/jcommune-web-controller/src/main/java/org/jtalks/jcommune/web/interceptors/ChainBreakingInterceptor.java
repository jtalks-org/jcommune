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
package org.jtalks.jcommune.web.interceptors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.HttpRequestHandler;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * This interceptor simply breaks interceptor chain and forwards request to
 * the handler directly. We need it, for example, when processing static
 * resources. In this case we don't need all these complicated actions
 * like database access or localization, which are performed by
 * the other interceptors in a chain.
 *
 * @author Evgeniy Naumenko
 */
public class ChainBreakingInterceptor extends HandlerInterceptorAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChainBreakingInterceptor.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response, Object handler) throws IOException, ServletException {

        if (handler instanceof HttpRequestHandler) {
            HttpRequestHandler resourcehandler = ((HttpRequestHandler) handler);
            resourcehandler.handleRequest(request, response);
            //break interceptor chain
            return false;
        } else {
            LOGGER.warn("handler can not be cast to HttpRequestHandler. It is of class " +
                    handler.getClass().getCanonicalName());
            return true;
        }
    }
}
