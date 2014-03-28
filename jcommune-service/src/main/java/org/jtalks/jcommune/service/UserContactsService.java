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

import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.model.entity.UserContact;
import org.jtalks.jcommune.model.entity.UserContactType;
import org.jtalks.jcommune.service.dto.UserContactContainer;
import org.jtalks.jcommune.service.exceptions.NotFoundException;

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
     * Update user contacts.
     *
     * @param editedUserId an identifier of edited user
     * @return updated contacts
     * @throws NotFoundException if edited user or contacts don't exists in system
     */
    JCUser saveEditedUserContacts(long editedUserId, List<UserContactContainer> contacts) throws NotFoundException;
}
