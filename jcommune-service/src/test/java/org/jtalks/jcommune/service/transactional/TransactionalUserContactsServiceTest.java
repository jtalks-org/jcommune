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
import org.jtalks.jcommune.model.dao.UserDao;
import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.model.entity.UserContact;
import org.jtalks.jcommune.model.entity.UserContactType;
import org.jtalks.jcommune.service.UserContactsService;
import org.jtalks.jcommune.service.exceptions.NotFoundException;
import org.jtalks.jcommune.service.nontransactional.SecurityService;
import org.mockito.Mock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.*;

import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.testng.Assert.*;

/**
 * @author Michael Gamov
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
    private SecurityService securityService;
    
    private JCUser user;
    
    @BeforeMethod
    public void setUp() {
        initMocks(this);
        userContactsService = new TransactionalUserContactsService(userContactsDao, securityService);
        user = new JCUser(USERNAME, EMAIL, PASSWORD);
        when(securityService.getCurrentUser()).thenReturn(user);
    }

    @Test
    public void testsGetAvailableContactTypes() {
        
        List<UserContactType> expectedTypes = new ArrayList<UserContactType>();
        expectedTypes.add(createUserContactType());
        
        when(userContactsDao.getAvailableContactTypes()).thenReturn(expectedTypes);
        
        List<UserContactType> types = userContactsService.getAvailableContactTypes();
        assertTrue(types.containsAll(expectedTypes));
    }
    
    @Test
    public void testAddContact() throws NotFoundException {
        UserContactType type = createUserContactType();
        UserContact contact = new UserContact(CONTACT, type);
        
        when(userContactsDao.isExist(type.getId())).thenReturn(true);
        when(userContactsDao.get(type.getId())).thenReturn(type);
        
        UserContact addedContact = userContactsService.addContact(contact.getValue(), contact.getType());
        assertEquals(addedContact.getValue(), CONTACT);
        assertEquals(addedContact.getType(), type);
    }
    
    @Test
    public void testRemoveUnfamiliarContact() {
        UserContact userContact = new UserContact(CONTACT, createUserContactType());
        userContact.setId(1L);
        user.addContact(userContact);        
        
        userContactsService.removeContact(2L);
        assertEquals(user.getUserContacts().size(), 1);
    }

    @Test
    public void testRemoveContact() {
        UserContact userContact = new UserContact(CONTACT, createUserContactType());
        userContact.setId(1L);
        user.addContact(userContact);
        when(userContactsDao.getContactById(1L)).thenReturn(userContact);

        userContactsService.removeContact(1L);
        assertEquals(user.getUserContacts().size(), 0);
    }

    
    private static UserContactType createUserContactType() {
        UserContactType userContactType = new UserContactType();
        userContactType.setId(1);
        userContactType.setTypeName(TYPENAME);
        userContactType.setIcon(ICON);
        return userContactType;
    }
   
}
