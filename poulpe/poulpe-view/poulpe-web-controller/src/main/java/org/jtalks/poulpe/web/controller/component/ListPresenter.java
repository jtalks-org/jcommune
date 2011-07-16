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

import java.util.List;

import org.jtalks.poulpe.model.entity.Component;
import org.jtalks.poulpe.service.ComponentService;
import org.jtalks.poulpe.web.controller.WindowManager;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;

//TODO: tasks which are going to be done from the bottom
//1) i18n support;
//2) unit-test;
//4) javadoc;
//6) logger

/**
 * The class for mediating between model and view representation of components.
 * 
 * @author Dmitriy Sukharev
 * 
 */
public class ListPresenter {
    
    /** The object that is responsible for updating view of the component list. */
    private ListView view;

    /** The service instance to manipulate with stored components. */
    private ComponentService componentService;
    
    private WindowManager windowManager;

    /**
     * Initialises the object that is responsible for updating view of the
     * component list.
     * 
     * @param view
     *            the object that is responsible for updating view of the
     *            component list
     */
    public void initView(ListView view) {
        this.view = view;
        view.updateList(getComponents());
    }
    
    /**
     * Returns view representation of all components.
     * 
     * @return the list of the components
     */
    public List<Component> getComponents() {
        return componentService.getAll();
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
     * Shows the window for adding new component to component list.
     * 
     * @throws InterruptedException
     *             when a thread is waiting, sleeping, or otherwise occupied,
     *             and the thread is interrupted, either before or during the
     *             activity
     */
    public void addComponent() throws InterruptedException {
        invokeEditWindowFor(-1L);
    }

    /**
     * Shows the window for editing the selected component from component list.
     * 
     * @throws InterruptedException
     *             when a thread is waiting, sleeping, or otherwise occupied,
     *             and the thread is interrupted, either before or during the
     *             activity
     */
    public void editComponent() throws InterruptedException {
        invokeEditWindowFor(view.getSelectedItem().getId());
    }
    
    /**
     * Invokes creation of the new window for editing existing or new component.
     * 
     * @param componentId
     *            component to be edited
     * @throws InterruptedException
     *             when a thread is waiting, sleeping, or otherwise occupied,
     *             and the thread is interrupted, either before or during the
     *             activity
     */
    private void invokeEditWindowFor(long componentId) throws InterruptedException {
        windowManager.showEditComponentWindow(componentId, new EditListListener());
    }

    /** Removes the selected component from the component list. */
    public void deleteComponent() {
        Component victim = view.getSelectedItem();
        componentService.deleteComponent(victim);
        view.updateList(getComponents());
    }
    
    /**
     * The class for listening changing of components and updating
     * {@link ListView} in accordance with this changes.
     * 
     * @author Dmitriy Sukharev
     * 
     */
    public class EditListListener implements EventListener {
        /** {@inheritDoc} */
        @Override
        public void onEvent(Event event) {
            view.updateList(getComponents());
        }        
    }
}
