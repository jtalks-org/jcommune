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

import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Mikhail Stryzhonok
 */
public class PluginHandlerMapping extends RequestMappingHandlerMapping {

    private static final PluginHandlerMapping INSTANCE = new PluginHandlerMapping();
    private List<String> mappedClasses = new ArrayList<>();

    private PluginHandlerMapping() {

    }

    public static PluginHandlerMapping getInstance() {
        return INSTANCE;
    }

    /**
     * Adds handlers from controller to handler mapping
     * Note: class should be annotated with {@link org.springframework.stereotype.Controller} annotation and all
     * handler method should be annotated with {@link org.springframework.web.bind.annotation.RequestMapping} annotation
     *
     * @param controller controller object to map
     */
    public void addController(Object controller) {
        if (!mappedClasses.contains(controller.getClass().getName())) {
            mappedClasses.add(controller.getClass().getName());
            INSTANCE.detectHandlerMethods(controller);
        }
    }

}
