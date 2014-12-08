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


import com.google.common.collect.Lists;
import org.jtalks.jcommune.model.dao.UserContactsDao;
import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.model.entity.UserContact;
import org.jtalks.jcommune.model.entity.UserContactType;
import org.jtalks.jcommune.service.UserContactsService;
import org.jtalks.jcommune.service.UserService;
import org.jtalks.jcommune.service.dto.UserContactContainer;
import org.jtalks.jcommune.plugin.api.exceptions.NotFoundException;

import java.util.List;

/**
 * User contacts service class. This class contains method needed to manipulate with UserContactTypes persistent entity.
 *
 * @author Michael Gamov
 * @author Andrey Pogorelov
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
    public JCUser saveEditedUserContacts(long editedUserId, List<UserContactContainer> contacts)
            throws NotFoundException {
        JCUser user = userService.get(editedUserId);
        // temporary contact list for excluding concurrent modification ex
        List<UserContact> tmpContactList = Lists.newArrayList(user.getContacts());
        // Remove deleted contacts
        for (UserContact contact : tmpContactList) {
            if (!contactExistInList(contact, contacts)) {
                user.removeContact(contact);
            }
        }
        // Add new and edit existing contacts
        for (UserContactContainer contactContainer: contacts) {
            UserContactType actualType = get(contactContainer.getTypeId());
            UserContact contact = contactContainer.getId() == null ? null
                    : this.getDao().getContactById(contactContainer.getId());
            if (contact != null && contact.getOwner().getId() == user.getId()) {
                contact.setValue(contactContainer.getValue());
                contact.setType(actualType);
            } else {
                contact = new UserContact(contactContainer.getValue(), actualType);
                user.addContact(contact);
            }
        }
        return user;
    }

    /**
     * Check if contact exist in list of contact dto's
     * @param userContact user contact
     * @param contacts list of edited contacts
     * @return contact exist in list
     */
    private boolean contactExistInList(UserContact userContact, List<UserContactContainer> contacts) {
        for (UserContactContainer contact : contacts) {
            if (contact.getId() != null && userContact.getId() == contact.getId()) {
                return true;
            }
        }
        return false;
    }
}
