/**
 * Copyright (C) 2011  JTalks.org Team
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
 */
package org.jtalks.jcommune.service;

import org.jtalks.jcommune.model.entity.UserContact;
import org.jtalks.jcommune.model.entity.UserContactType;

import java.util.List;

/**
  * This interface should have methods which give us more abilities in manipulating UserContactType persistent entity.
  *
  * @author Michael Gamov
 */
public interface UserContactsService extends EntityService<UserContactType> {

    /**
     * Returns a list of contact types permitted in the current configuration.
     * These types are to be configured from Poulpe.
     *
     * @return valid contact type list, e.g (skype, icq, jabber, mail, cell)
     */
    List<UserContactType> getAvailableContactTypes();

    /**
     * Adds contact to current user contacts.
     * @param contact
     */
    UserContact addContact(UserContact contact);

    /**
     * Removes contact from contacts of current user.
     * @param userContactId
     */
    void removeContact(Long userContactId);
}
