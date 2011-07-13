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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.jtalks.poulpe.model.entity.Component;
import org.jtalks.poulpe.model.entity.ComponentType;
import org.jtalks.poulpe.service.ComponentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
public class ComponentPresenter {
    
    private final Logger logger = LoggerFactory.getLogger(getClass());

    /** The object that is responsible for updating view of the component list. */
    private ComponentListView listView;
    
    /**
     * The object that is responsible for storing and updating view of the added
     * or edited component item.
     */
    private ComponentItemView itemView;

    /** The current (selected) component from the list of components. */
    private ComponentViewItem selectedComponent;

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
    public void initListView(ComponentListView view) {
        this.listView = view;
    }
    
    /**
     * Initialises the object that is responsible for storing and updating view
     * of the added or edited component item.
     * 
     * @param view
     *            the object that is responsible for storing and updating view
     *            of the added or edited component item
     */
    public void initItemView(ComponentItemView view) {
        this.itemView = view;
    }

    /**
     * Returns view representation of all components.
     * 
     * @return the list of the components
     */
    public List<ComponentView> getComponents() {
        return ComponentViewModelConverter.model2View(componentService.getAll());
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
     * Returns the selected component from the list of components.
     * 
     * @return the selected component from the list of components
     */
    public ComponentView getSelectedComponent() {
        return selectedComponent;
    }

    /**
     * Sets the selected component from the list of components.
     * 
     * @param currentComponent
     *            the new value of the selected component from the list of
     *            components
     */
    public void setSelectedComponent(ComponentViewItem currentComponent) {
        this.selectedComponent = currentComponent;
    }

    /** Shows the window for adding new component to component list. */
    // TODO maybe it's too complicated: view delegates showing window to
    // presenter which delegates it to view %-) 
    public void addComponent() {
        selectedComponent = new ComponentViewItem();
        List<String> types = getTypes();
        listView.showEditWindow(selectedComponent, types);
    }

    /** Removes the selected component from the component list. */
    public void deleteComponent() {
        Component victim = ComponentViewModelConverter.view2Model(selectedComponent);
        componentService.deleteComponent(victim);
        listView.removeFromModel(selectedComponent);
        //listView.updateList(getComponents());
    }

    /**
     * Shows the window for editing the selected component from component list.
     */
    public void editComponent() {
        List<String> types = getTypes();
        types.add(selectedComponent.getComponentType());
        listView.showEditWindow(selectedComponent, types);
    }

    /**
     * Obtains all unoccupied types of components and returns them.
     * 
     * @return the list unoccupied component types as strings
     */
    private List<String> getTypes() {
        Set<ComponentType> origTypes = componentService.getAvailableTypes();
        List<String> strTypes = new ArrayList<String>();
        for (ComponentType orig : origTypes) {
            strTypes.add(orig.toString());
        }
        return strTypes;
    }

    /** Saves the created or edited component in component list. */
    public void saveComponent() {
        Component newbie = ComponentViewModelConverter.view2Model(itemView);
        ComponentViewItem view = (ComponentViewItem) ComponentViewModelConverter.model2View(newbie);
        logger.debug("Newbie.getId() = {}", view.getCid());
        componentService.saveComponent(newbie);        
        if (view.getCid() == 0) { 
            view.setCid(newbie.getId());    // elements in table should have real IDs.
            listView.addToModel(view);
        } else {
            listView.replaceInModel(view);
        }
        //listView.updateList(getComponents());
    }

    /**
     * Delegates to the View searching the component's id by its name.
     * 
     * @param name
     *            the component's name
     * @return the component's id whose name is {@code name}
     */
    public long getCidByName(String name) {
        return listView.getCidByName(name);
    }
}

/**
 * The utility class which contains methods for converting model representation
 * of components to view representation and and vice versa.
 * 
 * @author Dmitriy Sukharev
 * 
 */
final class ComponentViewModelConverter {

    /**
     * The empty constructor to prevent creating instances of utility class.
     */
    private ComponentViewModelConverter() {
        throw new UnsupportedOperationException();
    }

    /**
     * Converts the component from the model representation to the view
     * representation.
     * 
     * @param model
     *            the model representation of the component
     * @return the component in view representation
     */
    public static ComponentView model2View(Component model) {
        ComponentViewItem view = new ComponentViewItem();
        view.setCid(model.getId());
        view.setName(model.getName());
        view.setDescription(model.getDescription());
        view.setComponentType(model.getComponentType().toString());
        return view;
    }

    /**
     * Converts the components from the model representation to the view
     * representation.
     * 
     * @param model
     *            the list of the model representations of the components
     * @return the list of the components in view representation
     */
    public static List<ComponentView> model2View(List<Component> model) {
        List<ComponentView> view = new ArrayList<ComponentView>();
        for (Component modelItem : model) {
            view.add(model2View(modelItem));
        }
        return view;
    }

    /**
     * Converts the component from the view representation to the model
     * representation.
     * 
     * @param view
     *            the view representation of the component
     * @return the component in model representation
     */
    public static Component view2Model(ComponentView view) {
        Component model = new Component();
        model.setId(view.getCid());
        model.setName(view.getName());
        model.setDescription(view.getDescription());
        model.setComponentType(ComponentType.valueOf(view.getComponentType()));
        return model;
    }

    /**
     * Converts the components from the view representation to the model
     * representation.
     * 
     * @param view
     *            the list of the view representations of the components
     * @return the list of the components in model representation
     */
    public static List<Component> view2Model(List<ComponentView> view) {
        List<Component> model = new ArrayList<Component>();
        for (ComponentView viewItem : view) {
            model.add(view2Model(viewItem));
        }
        return model;
    }
}
