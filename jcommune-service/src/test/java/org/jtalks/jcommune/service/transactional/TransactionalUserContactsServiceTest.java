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
import org.jtalks.jcommune.service.exceptions.NotFoundException;
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
        
        List<UserContactType> expectedTypes = new ArrayList<UserContactType>();
        expectedTypes.add(createUserContactType(1));
        
        when(userContactsDao.getAvailableContactTypes()).thenReturn(expectedTypes);
        
        List<UserContactType> types = userContactsService.getAvailableContactTypes();
        assertTrue(types.containsAll(expectedTypes));
    }

    @Test
    public void saveEditedContactsShouldUpdateThemAndReturnItFromRepository() throws NotFoundException {
        JCUser editedUser = new JCUser(USERNAME, EMAIL, PASSWORD);
        AddContactsToUser(editedUser, 1, 1);
        long typeIdForNewContact = 2;
        UserContactType typeForNewContact = createUserContactType(typeIdForNewContact);
        List<UserContactContainer> contacts = getContacts(3, typeIdForNewContact, editedUser.getUserContacts());
        when(userService.get(editedUser.getId())).thenReturn(editedUser);
        List<UserContact> userContactList = Lists.newArrayList(editedUser.getUserContacts());
        UserContactType contactType = userContactList.get(0).getType();
        when(userContactsDao.getContactById(userContactList.get(0).getId())).thenReturn(userContactList.get(0));

        when(userContactsDao.get(contactType.getId())).thenReturn(contactType);
        when(userContactsDao.get(typeForNewContact.getId())).thenReturn(typeForNewContact);
        when(userContactsDao.isExist(anyLong())).thenReturn(true);

        JCUser resultUser = userContactsService.saveEditedUserContacts(editedUser.getId(), contacts);
        assertEquals(resultUser.getUserContacts().size(), 4);
    }
    
    @Test
    public void removeContactShouldNotRemoveItIfContaxtDoesNotExist() throws NotFoundException {
        UserContact userContact = new UserContact(CONTACT, createUserContactType(1));
        userContact.setId(1L);
        user.addContact(userContact); 
        long userId = 1L;
        user.setId(userId);
        when(userService.get(userId)).thenReturn(user);
        
        userContactsService.removeContact(userId, 2L);
        
        assertEquals(user.getUserContacts().size(), 1);
    }

    @Test
    public void removeContactShouldRemoveItIfContaxtExists() throws NotFoundException {
        UserContact userContact = new UserContact(CONTACT, createUserContactType(1));
        userContact.setId(1L);
        user.addContact(userContact);
        when(userContactsDao.getContactById(1L)).thenReturn(userContact);
        long userId = 1L;
        user.setId(userId);
        when(userService.get(userId)).thenReturn(user);

        userContactsService.removeContact(userId, 1L);
        
        assertEquals(user.getUserContacts().size(), 0);
    }

    
    private static UserContactType createUserContactType(long contactTypeId) {
        UserContactType userContactType = new UserContactType();
        userContactType.setId(contactTypeId);
        userContactType.setTypeName(TYPENAME);
        userContactType.setIcon(ICON);
        return userContactType;
    }

    private static JCUser AddContactsToUser(JCUser user, long contactsSize, long contactTypeId) {
        for (int i = 0; i < contactsSize; i++) {
            UserContact contact = new UserContact("contact" +i, createUserContactType(contactTypeId));
            contact.setId(i);
            user.addContact(contact);
        }
        return user;
    }

    private static List<UserContactContainer> getContacts(long newContactsSize, long contactTypeId,
                                                          Set<UserContact> userContacts) {
        List<UserContactContainer> contacts = new ArrayList<>();
        for (UserContact contact : userContacts) {
            contacts.add(new UserContactContainer(contact.getId(), contact.getValue(), contact.getType().getId()));
        }
        for (long i = 0; i < newContactsSize; i++) {
            contacts.add(new UserContactContainer(null, "contact" + i, contactTypeId));
        }
        return contacts;
    }
}
