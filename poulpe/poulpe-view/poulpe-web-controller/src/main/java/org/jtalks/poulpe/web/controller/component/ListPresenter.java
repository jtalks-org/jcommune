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
import org.jtalks.poulpe.web.controller.DialogManager;


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
     * 
     * @param component to be edited
     */
    public void editComponent(Component component) {
        invokeEditWindowFor(component.getId());
    }
    
    /**
     * Invokes creation of the new window for editing existing or new component.
     * 
     * @param componentId
     *            identifier of component which will be edited
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
            DeletePerformable dc = new DeletePerformable();
            getDialogManager().confirmDeletion(victim.getName(), dc);
        }
    }

    /** Update the list of component which is displayed. */
    public void updateList() {
        view.updateList(getComponents());
    }

    /**
     * The class for executing deletion of the selected item, delegates this
     * task to the component service and view.
     * 
     * @author Dmitriy Sukharev
     * 
     */
    class DeletePerformable implements DialogManager.Performable {
        /** {@inheritDoc} */
        @Override
        public void execute() {
            Component victim = view.getSelectedItem();
            getComponentService().deleteComponent(victim);
            updateList();
        }        
    }

}
