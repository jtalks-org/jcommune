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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The class for mediating between model and view representation of components.
 * 
 * @author Dmitriy Sukharev
 * 
 */
public class ItemPresenter {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * The object that is responsible for storing and updating view of the added
     * or edited component item.
     */
    private ItemView view;

    /** The service instance to manipulate with stored components. */
    private ComponentService componentService;

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

    /** Saves the created or edited component in component list. */
    public void saveComponent() {
        Component newbie = ViewModelConverter.view2Model(view);
        PlainComponentItem item = (PlainComponentItem) ViewModelConverter.model2View(newbie);
        logger.debug("Newbie.getId() = {}", item.getCid());
        componentService.saveComponent(newbie);

        if (item.getCid() == 0) { 
            item.setCid(newbie.getId());    // elements in table should have real IDs.
            view.updateCallbackWindow(item, true);
        } else {
            view.updateCallbackWindow(item, false);
        }
    }

    /**
     * Delegates to the View searching the component's id by its name.
     * 
     * @param name
     *            the component's name
     * @return the component's id whose name is {@code name}
     */
    public long getCidByName(String name) {
        // TODO: not good to do it through DB
        List<Component> list = componentService.getAll();
        for (Component component : list) {
            if (name.equals(component.getName())) {
                return component.getId();
            }
        }
        return -1;
    }
}
