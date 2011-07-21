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

//TODO: tasks which are going to be done
//2) unit-test;
//4) javadoc;

/**
 * The class for mediating between model and view representation of components.
 * 
 * @author Dmitriy Sukharev
 * 
 */
public class ListPresenter extends AbstractPresenter {
    
    /** The object that is responsible for updating view of the component list. */
    private ListView view;
    
    /**
     * Initialises the object that is responsible for updating view of the
     * component list.
     * 
     * @param listView
     *            the object that is responsible for updating view of the
     *            component list
     */
    public void initView(ListView listView) {
        this.view = listView;
        listView.createModel(getComponents());
    }
    
    /**
     * Returns view representation of all components.
     * 
     * @return the list of the components
     */
    public List<Component> getComponents() {
        return getComponentService().getAll();
    }

    /**
     * Shows the window for adding new component to component list.
     */
    public void addComponent() {
        invokeEditWindowFor(-1L);
    }

    /**
     * Shows the window for editing the selected component from component list.
     */
    public void editComponent() {
        invokeEditWindowFor(view.getSelectedItem().getId());
    }
    
    /**
     * Invokes creation of the new window for editing existing or new component.
     * 
     * @param componentId
     *            component to be edited
     */
    private void invokeEditWindowFor(long componentId) {
        getWindowManager().showEditComponentWindow(componentId, view.getUpdater());
    }

    /** Removes the selected component from the component list. */
    public void deleteComponent() {
        if (!view.hasSelectedItem()) { // if there is no selected item
            getDialogManager().notify("item.no.selected.item");
        } else {
            Component victim = view.getSelectedItem();
            if (getDialogManager().confirmDeletion(victim.getName())) {
                getComponentService().deleteComponent(victim);
                view.updateList(getComponents());
            }
        }
    }

    /** Update the list of component which is displayed. */
    public void updateList() {
        view.updateList(getComponents());
    }

}
