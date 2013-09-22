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
package org.jtalks.jcommune.web.listeners;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Sets up properties at the application startup.
 *
 * @author Andrey Pogorelov
 */
public class AppPropertySetupListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        // This property needed to prevent error occurred during unmarshalling of response from rest service.
        // Used in Poulpe Auth Plugin.
        System.setProperty("com.sun.xml.bind.v2.bytecode.ClassTailor.noOptimize", "true");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.getProperties().remove("com.sun.xml.bind.v2.bytecode.ClassTailor.noOptimize");
    }
}
