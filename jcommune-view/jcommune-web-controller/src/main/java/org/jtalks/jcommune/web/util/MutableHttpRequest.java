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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.util.HashMap;

/**
 * Class provides possibility for adding params to HttpServletRequest.
 * We need this one for adding special key for {@link org.springframework.security.web.authentication.rememberme}
 * Also we can use it in another cases when we should add new parameter to request.
 *
 * @author Andrey Ivanov <a.nigredo@gmail.com>
 */
public class MutableHttpRequest extends HttpServletRequestWrapper {

    private HashMap<String, String> params = new HashMap();

    public MutableHttpRequest(HttpServletRequest request) {
        super(request);
    }

    /**
     * Get parameter from request
     *
     * @param name parameter name
     * @return value of parameter
     */
    public String getParameter(String name) {
        if (params.containsKey(name)) {
            return params.get(name);
        }
        HttpServletRequest req = (HttpServletRequest) super.getRequest();
        return req.getParameter(name);
    }

    /**
     * Add parameter to request
     *
     * @param name  parameter name
     * @param value parameter value
     */
    public void addParameter(String name, String value) {
        params.put(name, value);
    }
}