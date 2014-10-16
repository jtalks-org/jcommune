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
package org.jtalks.jcommune.plugin.api.web;

/**
 * Interface-marker. Plugin controller should implement this interface to allow {@link PluginHandlerMapping} distinguish
 * them from application controllers. It's necessary because we should map plugin controllers separately from
 * application controllers. It allow us to change plugin version without application restart.
 *
 * @author Mikhail Stryzhonok
 *
 * @see PluginHandlerMapping
 */
public interface PluginController {

    /**
     * Sets path to jcommune-plugin-api.jar. This path should be used to load common templates from plugin-api.
     *
     * @param apiPath path to jcommune-plugin-api.jar
     */
    public void setApiPath(String apiPath);
}
