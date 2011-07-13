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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jtalks.poulpe.model.entity.Component;
import org.jtalks.poulpe.model.entity.ComponentType;
import org.jtalks.poulpe.service.ComponentService;
import org.jtalks.poulpe.web.controller.WindowManager;

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
    public void initListView(ListViewImpl view) {
        this.listView = view;
        view.updateList(getComponents());
    }
    
    /**
     * Returns view representation of all components.
     * 
     * @return the list of the components
     */
    public List<PlainComponentItem> getComponents() {
        return ViewModelConverter.model2View(componentService.getAll());
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
        //selectedComponent = new ComponentViewItem();
        Map<String, Object> args = new HashMap<String, Object>();
        args.put("component", new PlainComponentItem());
        args.put("types", getTypes());
        args.put("callbackWin", listView);          // FIXIT: TEMPORARY SOLUTION
        WindowManager.showEditComponentWindow(args);
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
        List<String> types = getTypes();
        types.add(listView.getSelectedItem().getComponentType());
        Map<String, Object> args = new HashMap<String, Object>();
        args.put("component", listView.getSelectedItem());
        args.put("types", types);
        args.put("callbackWin", listView);      // FIXIT: TEMPORARY SOLUTION
        WindowManager.showEditComponentWindow(args);
    }

    /** Removes the selected component from the component list. */
    public void deleteComponent() {
        Component victim = ViewModelConverter.view2Model(listView.getSelectedItem());
        componentService.deleteComponent(victim);
        //listView.removeFromModel(listView.getSelectedItem());
        listView.updateList(getComponents());
    }

    /**
     * Obtains all unoccupied types of components and returns them.
     * 
     * @return the list unoccupied component types as strings
     */
    // TODO: candidate to be removed.
    private List<String> getTypes() {
        Set<ComponentType> origTypes = componentService.getAvailableTypes();
        List<String> strTypes = new ArrayList<String>();
        for (ComponentType orig : origTypes) {
            strTypes.add(orig.toString());
        }
        return strTypes;
    }

//    /**
//     * Delegates to the View searching the component's id by its name.
//     * 
//     * @param name
//     *            the component's name
//     * @return the component's id whose name is {@code name}
//     */
//    public long getCidByName(String name) {
//        return listView.getCidByName(name);
//    }
}
