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


/**
 * The interface represents the manager for showing different types of dialog
 * messages.
 * 
 * @author Dmitriy Sukharev
 * 
 */
public interface DialogManager {
    // It's just 2 methods I need right now. I don't mind if somebody change
    // this interface.
    // But it looks like all our current user stories need this two methods.

    /**
     * Notifies user using {@code localeMessage} message.
     * 
     * @param localeMessage
     *            i18n key whose value should be shown as a message
     */
    void notify(String localeMessage);

    /**
     * Asks user if they want to delete item.
     * 
     * @param victim
     *            the item to be deleted
     * @param performable
     *            the action to be performed when user confirms item deletion
     */
    void confirmDeletion(String victim, Performable performable);

    /**
     * The interface for storing some actions that ought to be performed when
     * user confirms them.
     * 
     * @author Dmitriy Sukharev
     * 
     */
    interface Performable {
        /**
         * The actions to be executed after the user confirms that he/she wants
         * them to be executed.
         */
        void execute();
    }
}
