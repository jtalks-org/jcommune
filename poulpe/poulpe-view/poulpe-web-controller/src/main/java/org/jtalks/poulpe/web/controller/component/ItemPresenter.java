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
import java.util.List;
import java.util.Set;

import org.jtalks.poulpe.model.entity.Component;
import org.jtalks.poulpe.model.entity.ComponentType;
import org.jtalks.poulpe.service.ComponentService;
import org.jtalks.poulpe.service.exceptions.NotFoundException;
import org.jtalks.poulpe.web.controller.WindowManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The class for mediating between model and view representation of components.
 * 
 * @author Dmitriy Sukharev
 * 
 */
public class ItemPresenter {

    private static final Logger LOGGER = LoggerFactory.getLogger(ItemPresenter.class);

    /**
     * The object that is responsible for storing and updating view of the added
     * or edited component item.
     */
    private ItemView view;

    /** The service instance to manipulate with stored components. */
    private ComponentService componentService;
    
    private WindowManager windowManager;

    /**
     * Initialises the object that is responsible for storing and updating view
     * of the added or edited component item.
     * 
     * @param view
     *            the object that is responsible for storing and updating view
     *            of the added or edited component item
     */
    public void initView(ItemView view) {
        this.view = view;
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
     * Saves the created or edited component in component list.
     * 
     * @throws Exception
     *             when error of closing window including problems with
     *             execution of the "onDetach" listener action
     */
    public void saveComponent() throws Exception {
        Component newbie = view2Model(view);
        LOGGER.debug("Newbie.getId() = {}", view.getCid());
        componentService.saveComponent(newbie);
        windowManager.closeWindow(view);
    }
    
    /**
     * Converts the component from the view representation to the model
     * representation.
     * 
     * @param view
     *            the view representation of the component
     * @return the component in model representation
     */
    private Component view2Model(ItemView view) {
        Component model = new Component();
        model.setId(view.getCid());
        model.setName(view.getName());
        model.setDescription(view.getDescription());
        model.setComponentType(ComponentType.valueOf(view.getComponentType()));
        return model;
    }

    /**
     * Delegates to the View searching the component's id by its name.
     * 
     * @param name
     *            the component's name
     * @return the component's id whose name is {@code name}
     */
    public long getCidByName(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Component's name can't be empty");
        }
        List<Component> list = componentService.getAll();
        for (Component component : list) {
            if (name.equals(component.getName())) {
                return component.getId();
            }
        }
        return -1;
    }

    /**
     * Obtains all unoccupied types of components and returns them.
     * 
     * @return the list unoccupied component types as strings
     */
    public List<String> getTypes() {
        Set<ComponentType> origTypes = componentService.getAvailableTypes();
        List<String> strTypes = new ArrayList<String>();
        for (ComponentType orig : origTypes) {
            strTypes.add(orig.toString());
        }
        return strTypes;
    }
    
    /**
     * Returns the component by its id delegating obtaining of this component to
     * {@link ComponentService}.
     * 
     * @param id
     *            id of the component to be fetched
     * @return the component by its id
     * @throws NotFoundException
     *             when entity can't be found
     */
    public Component getComponent(long id) throws NotFoundException {
        return componentService.get(id);
    }
}
