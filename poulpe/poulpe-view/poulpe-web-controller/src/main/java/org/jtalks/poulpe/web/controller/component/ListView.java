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

/**
 * The interface for managing actions and represents information about
 * components displayed in administrator panel.
 * 
 * @author Dmitriy Sukharev
 * 
 */
public interface ListView {

    /**
     * Returns the selected component from the list of the visible components.
     * 
     * @return the selected component from the list of the visible components
     */
    Component getSelectedItem();

    /**
     * Updates the list of the components.
     * 
     * @param list
     *            the new list of the components
     */
    void updateList(List<Component> list);

    /**
     * Initialises model (content of list-box) by component.
     * 
     * @param list
     *            new list of components to be shown
     */
    void createModel(List<Component> list);

    /**
     * Determines if there is selected item in the component list.
     * 
     * @return true if there is selected item in the component list, false
     *         otherwise
     */
    boolean hasSelectedItem();

    /**
     * Returns the object to be used for updating this view after some action
     * happen.
     * 
     * @return the object to be used for updating this view after some action
     *         happen
     */
    Object getUpdater();

}
