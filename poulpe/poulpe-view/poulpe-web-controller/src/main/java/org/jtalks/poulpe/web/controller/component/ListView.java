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

/**
 * The interface for managing actions and represents information about components
 * displayed in administrator panel.
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
    PlainComponent getSelectedItem();

    /**
     * Updates the list of the components.
     * 
     * @param list
     *            the new list of the components
     */
    void updateList(List<PlainComponentItem> list);

//    /**
//     * Checks if user selected item in the list of the components.
//     * 
//     * @return true if there is selected item, false otherwise
//     */
//    boolean hasSelectedItem();
//
//    /**
//     * Adds to the displayed component list the {@code component} item. As
//     * result user will see updated list.
//     * 
//     * @param component
//     *            item to be added.
//     */
//    void addToList(PlainComponentItem component);
//
//    /**
//     * Replaces in the displayed component list the old value of
//     * {@code component} item by the new one. As result user will see updated
//     * list. It searches the component to be replaced by its id.
//     * 
//     * @param component
//     *            the replacing item.
//     */
//    void updateInList(PlainComponentItem component);
}
