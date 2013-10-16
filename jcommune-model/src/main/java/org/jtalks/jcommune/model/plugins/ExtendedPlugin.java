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

package org.jtalks.jcommune.model.plugins;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Provides processing any available actions supported by plugin.
 *
 * @author Andrey Pogorelov
 */
public interface ExtendedPlugin extends Plugin {

    /**
     * Performs supported by plugin specified action.
     *
     * @param pluginId plugin id
     * @param action action name
     * @param response http response
     * @param out servlet output stream
     * @param session http session
     * @return any result provided by plugin
     */
    Object doAction(String pluginId, String action, HttpServletResponse response, ServletOutputStream out, HttpSession session);
}
