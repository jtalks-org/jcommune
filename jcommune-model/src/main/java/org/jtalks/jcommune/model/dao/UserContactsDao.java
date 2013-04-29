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
package org.jtalks.jcommune.model.dao;

import org.jtalks.common.model.dao.Crud;
import org.jtalks.jcommune.model.entity.UserContact;
import org.jtalks.jcommune.model.entity.UserContactType;

import java.util.List;

/**
 * Inteneded to perform various operations on user contacts and contact types.
 * Please note, that there is no need to explicitly store/delete contacts, just
 * add/remove them to/from User's collection.
 *
 * @author Evgeniy Naumenko
 */
public interface UserContactsDao extends Crud<UserContactType> {

    /**
     * Returns a list of contact types permitted in the current configuration.
     * These types are to be configured from Poulpe.
     *
     * @return valid contact type list, e.g (skype, icq, jabber, mail, cell)
     */
    List<UserContactType> getAvailableContactTypes();

    /**
     * Fetches user contact by id
     *
     * @param id database identifier of the contact required
     * @return UserContact with this id or nill, if this id is unknown
     */
    UserContact getContactById(long id);
}
