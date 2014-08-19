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
package org.jtalks.jcommune.plugin.api.core;

import javax.servlet.http.HttpServletRequest;

/**
 * Interface for the plugin which can process http request and generate content for the response
 * @author Andrei Alikov
 */
public interface WebControllerPlugin {

    /**
     * Processes the http request in the plugin
     * @param request http request to process
     * @return content which will be used to create response page (decorator will be applied)
     */
    String processHttpRequest(HttpServletRequest request);

    /**
     * @return part of request path that should be processed by the plugin - all requests
     * plugins/[getRequestSubPath()]/* should be processed by the plugin
     */
    String getRequestSubPath();
}
