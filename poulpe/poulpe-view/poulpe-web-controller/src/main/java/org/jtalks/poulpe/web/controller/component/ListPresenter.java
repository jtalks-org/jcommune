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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private ListView listView;

    /** The service instance to manipulate with stored components. */
    private ComponentService componentService;

    /**
     * Initialises the object that is responsible for updating view of the
     * component list.
     * 
     * @param view
     *            the object that is responsible for updating view of the
     *            component list
     */
    public void initListView(ListView view) {
        this.listView = view;
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
     * Shows the window for adding new component to component list.
     * 
     * @throws InterruptedException
     *             when a thread is waiting, sleeping, or otherwise occupied,
     *             and the thread is interrupted, either before or during the
     *             activity
     */
    public void addComponent() throws InterruptedException {
        Map<String, Object> args = new HashMap<String, Object>();
        args.put("componentId", 0L);
        args.put("listener", new EditListListener());
        WindowManager.showEditComponentWindow(args);
//        win.addEventListener(Events.ON_CLOSE, new EditListListener());
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
        Map<String, Object> args = new HashMap<String, Object>();
        args.put("componentId", listView.getSelectedItem().getId());
        args.put("listener", new EditListListener());
        WindowManager.showEditComponentWindow(args);
//        win.addEventListener(Events.ON_CLOSE, new EditListListener());
    }

    /** Removes the selected component from the component list. */
    public void deleteComponent() {
        Component victim = listView.getSelectedItem();
        componentService.deleteComponent(victim);
        listView.updateList(getComponents());
    }
    
    /**
     * The class for listening changing of components and updating
     * {@link ListView} in accordance with this changes.
     * 
     * @author Dmitriy Sukharev
     * 
     */
    class EditListListener implements EventListener {
        /** {@inheritDoc} */
        @Override
        public void onEvent(Event event) {
            listView.updateList(getComponents());
        }        
    }
}
