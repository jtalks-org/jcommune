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
package org.jtalks.jcommune.web.controller;

import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.model.entity.UserContact;
import org.jtalks.jcommune.model.entity.UserContactType;
import org.jtalks.jcommune.service.UserContactsService;
import org.jtalks.jcommune.service.exceptions.NotFoundException;
import org.jtalks.jcommune.web.dto.UserContactDto;
import org.jtalks.jcommune.web.dto.json.JsonResponse;
import org.jtalks.jcommune.web.dto.json.JsonResponseStatus;
import org.mockito.Mock;
import org.springframework.validation.BindingResult;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.testng.Assert.assertEquals;

/**
 * @author Michael Gamov
 */
public class UserContactsControllerTest {

    private static final String TYPENAME = "Some type";
    private static final String ICON = "/some/icon/path";

    @Mock
    private BindingResult result;
    private UserContactsController controller;

    @Mock
    private UserContactsService service;

    @BeforeMethod
    public void setUp() {
        initMocks(this);
        controller = new UserContactsController(service);    
    }
    
    @Test
    public void testGetContactTypes() {
        List<UserContactType> expectedTypes = new ArrayList<UserContactType>();
        UserContactType expectedType = createUserContactType();
        expectedTypes.add(expectedType);
        
        when(service.getAvailableContactTypes()).thenReturn(expectedTypes);

        UserContactType[] types = controller.getContactTypes();
        assertEquals(types.length, expectedTypes.size());
        assertEquals(types[0].getIcon(), expectedType.getIcon());
        assertEquals(types[0].getTypeName(), expectedType.getTypeName());
    }

    private UserContact createUserContact(long ownerId, UserContactType contactType){
        JCUser owner = new JCUser("username", "email", "password");
        owner.setId(ownerId);
        UserContact contact = new UserContact("gateway", contactType);
        contact.setOwner(owner);
        return contact;
    }

    private UserContactType createUserContactType(){
        UserContactType userContactType = new UserContactType();
        userContactType.setIcon(ICON);
        userContactType.setTypeName(TYPENAME);
        return userContactType;
    }
}
