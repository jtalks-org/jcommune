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
package org.jtalks.jcommune.service.transactional;


import org.jtalks.jcommune.model.dao.UserContactsDao;
import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.model.entity.UserContact;
import org.jtalks.jcommune.model.entity.UserContactType;
import org.jtalks.jcommune.service.UserContactsService;
import org.jtalks.jcommune.service.UserService;
import org.jtalks.jcommune.service.exceptions.NotFoundException;

import java.util.List;

/**
 * User contacts service class. This class contains method needed to manipulate with UserContactTypes persistent entity.
 *
 * @author Michael Gamov
 */
public class TransactionalUserContactsService
        extends AbstractTransactionalEntityService<UserContactType, UserContactsDao> implements UserContactsService {

    private UserService userService;

    /**
     * Create an instance of User entity based service
     *
     * @param dao             for operations with data storage
     * @param userService for security
     */
    public TransactionalUserContactsService(UserContactsDao dao, UserService userService) {
        super(dao);
        this.userService = userService;
    }

    /**
     * {@inheritDoc}
     */
    public List<UserContactType> getAvailableContactTypes() {
        return getDao().getAvailableContactTypes();
    }

    /**
     * {@inheritDoc}
     */
    public UserContact addContact(long ownerId, String value, long typeId) throws NotFoundException {
        JCUser user = userService.get(ownerId);

        //explicitly getting UserContactType because we need to populate it with data before returning
        UserContactType actualType = get(typeId);
        UserContact contact = new UserContact(value, actualType);
        user.addContact(contact);
        return contact;
    }

    /**
    * {@inheritDoc}
    */
    public void removeContact(long removedContactOwnerId, long userContactId) throws NotFoundException {
        JCUser user = userService.get(removedContactOwnerId);
        UserContact contact = this.getDao().getContactById(userContactId);
        user.removeContact(contact);
    }
}
