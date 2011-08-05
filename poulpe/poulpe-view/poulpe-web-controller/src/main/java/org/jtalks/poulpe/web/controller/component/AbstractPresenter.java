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
package org.jtalks.poulpe.web.controller.component;

import org.jtalks.poulpe.service.ComponentService;
import org.jtalks.poulpe.web.controller.DialogManager;
import org.jtalks.poulpe.web.controller.WindowManager;

/**
 * The abstract class which contains common fields and methods for
 * {@link ItemPresenter} and {@link ListPresenter} classes.
 * 
 * @author Dmitriy Sukharev
 * 
 */
public class AbstractPresenter {
    /** The service instance to manipulate with stored components. */
    private ComponentService componentService;

    private WindowManager windowManager;

    private DialogManager dialogManager;

    /**
     * Returns the service instance which is used for manipulating with stored
     * components.
     * 
     * @return the service instance which is used for manipulating with stored
     *         components
     */
    public ComponentService getComponentService() {
        return componentService;
    }

    /**
     * Returns the window manager which is used for showing and closing windows.
     * 
     * @return the window manager which is used for showing and closing windows
     */
    public WindowManager getWindowManager() {
        return windowManager;
    }

    /**
     * Returns the dialog manager which is used for showing different types of
     * dialog messages.
     * 
     * @return the dialog manager which is used for showing different types of
     *         dialog messages
     */
    public DialogManager getDialogManager() {
        return dialogManager;
    }

    /**
     * Sets the service instance which is used for manipulating with stored
     * components.
     * 
     * @param componentService
     *            the new value of the service instance
     */
    public void setComponentService(ComponentService componentService) {
        this.componentService = componentService;
    }

    /**
     * Sets the window manager which is used for showing and closing windows.
     * 
     * @param windowManager
     *            the new value of the window manager
     */
    public void setWindowManager(WindowManager windowManager) {
        this.windowManager = windowManager;
    }

    /**
     * Sets the dialog manager which is used for showing different types of
     * dialog messages.
     * 
     * @param dialogManager
     *            the new value of the dialog manager
     */
    public void setDialogManager(DialogManager dialogManager) {
        this.dialogManager = dialogManager;
    }
}
