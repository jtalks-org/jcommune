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

import java.util.HashMap;
import java.util.Map;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Window;

/**
 * The class which is responsible for creating and closing application windows.
 * 
 * @author Dmitriy Sukharev
 * 
 */
public final class WindowManagerImpl implements WindowManager {

    /** The path to the web-page for adding / editing component. */
    private static final String EDIT_COMPONENT_URL = "/WEB-INF/pages/edit_component.zul";

    /** {@inheritDoc} */
    @Override
    public void showEditComponentWindow(long componentId, Object listener) {
        Map<String, Object> args = new HashMap<String, Object>();
        args.put("componentId", componentId);
        Window win = (Window) Executions.createComponents(EDIT_COMPONENT_URL, null, args);
        win.setAttribute("CLOSE_LISTENER", listener);
        try {
            win.doModal();
        } catch (Exception e) {
            // It's extremely incredible that this exception will appear,
            // on the other hand it's bad to throw the Exception instance,
            // plus it complicates API. Therefore it's wrapped with
            // uncontrolled error.
            throw new AssertionError(e);
        }
    }
    
    /** {@inheritDoc} */
    @Override
    public void closeWindow(Object object) {
        Window win = (Window) object;
        win.detach();

        Object attr = win.getAttribute("CLOSE_LISTENER");
        if (attr instanceof EventListener) {
            try {
                ((EventListener) attr).onEvent(null);
            } catch (Exception e) {
                // This has the same explanation as throwing AssertionError in
                // the showEditComponentWindow method.
                throw new AssertionError(e);
            }
        }
    }

}
