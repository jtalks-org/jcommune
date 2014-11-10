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
import org.mockito.Mock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * @author Michael Gamov
 * @author Andrey Pogorelov
 */
public class TransactionalUserContactsServiceTest {

    private static final String TYPENAME = "New type";
    private static final String ICON = "/some/icon/path";
    private static final String CONTACT = "Some contact";
    private static final String USERNAME = "username";
    private static final String EMAIL = "username@mail.com";
    private static final String PASSWORD = "password";

    private UserContactsService userContactsService;

    @Mock
    private UserContactsDao userContactsDao;

    @Mock
    private UserService userService;

    private JCUser user;

    @BeforeMethod
    public void setUp() {
        initMocks(this);
        userContactsService = new TransactionalUserContactsService(userContactsDao, userService);
        user = new JCUser(USERNAME, EMAIL, PASSWORD);
        when(userService.getCurrentUser()).thenReturn(user);
    }

    @Test
    public void testsGetAvailableContactTypes() {

        List<UserContactType> expectedTypes = new ArrayList<>();
        expectedTypes.add(createUserContactType(1));

        when(userContactsDao.getAvailableContactTypes()).thenReturn(expectedTypes);

        List<UserContactType> types = userContactsService.getAvailableContactTypes();
        assertTrue(types.containsAll(expectedTypes));
    }

    @Test
    public void saveEditedContactsShouldUpdateEditedContacts() throws NotFoundException {
        long contactTypeId1 = 1;
        long contactTypeId2 = 2;
        String newValue2 = "new value 2";

        List<UserContactContainer> contacts = new ArrayList<>(addContactsToUser(user, 1, contactTypeId1));
        UserContactContainer contact1 = contacts.get(0);
        contact1.setTypeId(contactTypeId2);
        contact1.setValue(newValue2);

        prepareContactsMocks();
        UserContactType contactType = createUserContactType(contactTypeId2);
        when(userContactsDao.get(contactType.getId())).thenReturn(contactType);
        when(userContactsDao.isExist(anyLong())).thenReturn(true);

        JCUser resultUser = userContactsService.saveEditedUserContacts(user.getId(), contacts);
        assertEquals(resultUser.getContacts().size(), 1);
        List<UserContact> resultContacts = new ArrayList<>(resultUser.getContacts());
        assertEquals(resultContacts.get(0).getValue(), newValue2, "Value of contact 1 was not changed");
        assertEquals(resultContacts.get(0).getType().getId(), contactTypeId2, "Type of contact 1 was not changed");
    }

    @Test
    public void saveEditedContactsShouldAddNewContacts() throws NotFoundException {
        long contactTypeId1 = 1;
        long typeIdForNewContact = 2;

        List<UserContactContainer> contacts = new ArrayList<>(addContactsToUser(user, 2, contactTypeId1));
        UserContactType typeForNewContact = createUserContactType(typeIdForNewContact);
        contacts.addAll(createNewContacts(3, typeIdForNewContact));

        prepareContactsMocks();
        when(userContactsDao.get(typeForNewContact.getId())).thenReturn(typeForNewContact);
        when(userContactsDao.isExist(anyLong())).thenReturn(true);

        JCUser resultUser = userContactsService.saveEditedUserContacts(user.getId(), contacts);
        assertEquals(resultUser.getContacts().size(), 5);
    }

    @Test
    public void saveEditedContactsShouldRemoveDeletedContacts() throws NotFoundException {
        long contactTypeId1 = 1;
        long contactTypeId2 = 2;
        List<UserContactContainer> contacts = new ArrayList<>(addContactsToUser(user, 3, contactTypeId1));
        contacts.addAll(addContactsToUser(user, 2, contactTypeId2));
        contacts.remove(1);

        prepareContactsMocks();

        when(userContactsDao.isExist(anyLong())).thenReturn(true);

        JCUser resultUser = userContactsService.saveEditedUserContacts(user.getId(), contacts);
        assertEquals(resultUser.getContacts().size(), 4);
    }

    @Test(expectedExceptions = NotFoundException.class)
    public void saveEditedContactsShouldThrowNotFoundExceptionIfContactTypeNotFound() throws NotFoundException {
        long contactTypeId = 1;
        List<UserContactContainer> contacts = new ArrayList<>(addContactsToUser(user, 3, contactTypeId));

        prepareContactsMocks();
        when(userContactsDao.isExist(contactTypeId)).thenReturn(false);

        userContactsService.saveEditedUserContacts(user.getId(), contacts);
    }

    private void prepareContactsMocks() throws NotFoundException {
        when(userService.get(user.getId())).thenReturn(user);
        List<UserContact> userContactList = Lists.newArrayList(user.getContacts());
        for (UserContact contact : userContactList) {
            if (contact.getId() != 0) {
                when(userContactsDao.getContactById(contact.getId())).thenReturn(contact);
                UserContactType contactType = contact.getType();
                when(userContactsDao.get(contactType.getId())).thenReturn(contactType);
            }
        }
    }

    private static UserContactType createUserContactType(long contactTypeId) {
        UserContactType userContactType = new UserContactType();
        userContactType.setId(contactTypeId);
        userContactType.setTypeName(TYPENAME);
        userContactType.setIcon(ICON);
        return userContactType;
    }

    /**
     *
     * @param user user to whom add contacts
     * @param contactsCount contacts count
     * @param contactTypeId contact type id
     * @return added contacts as contact containers
     */
    private static List<UserContactContainer> addContactsToUser(JCUser user, long contactsCount, long contactTypeId) {
        List<UserContactContainer> contacts = new ArrayList<>();
        for (int i = 0; i < contactsCount; i++) {
            UserContact contact = new UserContact("contact" + i, createUserContactType(contactTypeId));
            contact.setId(user.getContacts().size() + 1);
            user.addContact(contact);
            contacts.add(new UserContactContainer(contact.getId(), contact.getValue(), contact.getType().getId()));
        }
        return contacts;
    }

    private static List<UserContactContainer> getContacts(Set<UserContact> userContacts) {
        List<UserContactContainer> contacts = new ArrayList<>();
        for (UserContact contact : userContacts) {
            contacts.add(new UserContactContainer(contact.getId(), contact.getValue(), contact.getType().getId()));
        }
        return contacts;
    }

    private static List<UserContactContainer> createNewContacts(long newContactsSize, long contactTypeId) {
        List<UserContactContainer> contacts = new ArrayList<>();
        for (long i = 0; i < newContactsSize; i++) {
            contacts.add(new UserContactContainer(null, "contact" + (contacts.size() + 1), contactTypeId));
        }
        return contacts;
    }
}
