/**
 * Copyright (C) 2011  jtalks.org Team
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
 * Also add information on how to contact you by electronic and paper mail.
 * Creation date: Apr 12, 2011 / 8:05:19 PM
 * The jtalks.org Project
 */
package org.jtalks.poulpe.web.controller;

import java.util.Map;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.Window;

/**
 * The class which is responsible for creating windows of the application.
 * 
 * @author Dmitriy Sukharev
 * 
 */
public final class WindowManager {
    
    /**
     * The empty constructor to prevent creating instances of utility class. 
     */
    private WindowManager() {
    }

    /** The path to the web-page for adding / editing component. */
    private static final String EDIT_COMPONENT_URL = "/WEB-INF/pages/edit_component.zul";

    /**
     * Creates and shows new window which is responsible for editing components.
     * 
     * @param args
     *            mandatory arguments which includes {@code component} to be
     *            edited, {@code types} to be shown in the list of the available
     *            for this component types
     * @throws InterruptedException
     *             when a thread is waiting, sleeping, or otherwise occupied,
     *             and the thread is interrupted
     */
    public static void showEditComponentWindow(Map<String, Object> args) throws InterruptedException {
        Window win = (Window) Executions.createComponents(EDIT_COMPONENT_URL, null, args);
        win.doModal();
    }

}
