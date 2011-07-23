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
import java.util.Map;

import org.jtalks.poulpe.model.entity.Component;
import org.jtalks.poulpe.model.entity.ComponentType;
import org.jtalks.poulpe.service.exceptions.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The class for mediating between model and view representation of components.
 * 
 * @author Dmitriy Sukharev
 * 
 */
public class ItemPresenter extends AbstractPresenter {

    private static final Logger LOGGER = LoggerFactory.getLogger(ItemPresenter.class);

    /**
     * The object that is responsible for storing and updating view of the added
     * or edited component item.
     */
    private ItemView view;

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
        initArgs();
    }

    /** Initialises this window using received arguments. */
    private void initArgs() {
        Map<String, Object> args = view.getArgs();
        long id = (Long) args.get("componentId");
        Component component = obtainComponent(id);
        if (component != null) {
            view.setCid(component.getId());
            view.setName(component.getName());
            view.setDescription(component.getDescription());
            initTypes(component);
        }
    }

    /**
     * Obtains the component by its ID.
     * 
     * @param id
     *            the ID of the component, {@code -1L} to return new instance
     * @return the component by its ID, new instance if {@code id} is equal to
     *         {@code -1L}, and null if there is no component with such ID.
     */
    private Component obtainComponent(long id) {
        Component component = null;
        if (id == -1L) {
            component = new Component();
        } else {
            try {
                component = getComponentService().get(id);
            } catch (NotFoundException e) {
                LOGGER.warn("Attempt to change non-existing item.", e);
                getDialogManager().notify("item.doesnt.exist");
                getWindowManager().closeWindow(view);
                // component will never be returned here
            }
        }
        return component;
    }

    /**
     * Initialises the list of possible types for the specified component.
     * 
     * @param component
     *            the component whose types are being determined.
     */
    private void initTypes(Component component) {
        if (component.getComponentType() == null) {
            view.setComponentType(null);
        } else {
            view.setComponentType(component.getComponentType().toString());
        }
        view.setComponentTypes(getComponentService().getAvailableTypes());
    }

    /** Saves the created or edited component in component list. */
    public void saveComponent() {
        long pos = getCidByName(view.getName());
        if (pos == -1 || pos == view.getCid()) {
            Component newbie = view2Model(view);
            getComponentService().saveComponent(newbie);
            getWindowManager().closeWindow(view);
        } else {
            LOGGER.warn("Attempt to create item ({}) with duplicate title.", view.getName());
            view.wrongName("item.already.exist");
        }
    }

    /** Checks if name of created or edited component is a duplicate. Invokes notification if it is. */
    public void checkComponent() {
        long pos = getCidByName(view.getName());
        if (pos != -1 && pos != view.getCid()) {
            view.wrongName("item.already.exist");
        }
        
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
        List<Component> list = getComponentService().getAll();
        for (Component component : list) {
            if (name.equals(component.getName())) {
                return component.getId();
            }
        }
        return -1;
    }

}
