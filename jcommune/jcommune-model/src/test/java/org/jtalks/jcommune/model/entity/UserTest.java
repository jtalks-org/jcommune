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
package org.jtalks.jcommune.model.entity;

import org.jtalks.jcommune.model.dao.hibernate.ObjectsFactory;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

/**
 * Test {@link User} logical methods
 * 
 * @author Osadchuck Eugeny
 *
 */
public class UserTest {
    

    
    @Test
    public void testCopyUser(){
        User user = ObjectsFactory.getDefaultUser();
        User anotherUser = ObjectsFactory.getAnotherUser();
        
        user.copyUser(anotherUser);
        
        assertEquals(user.getFirstName(), anotherUser.getFirstName(),"first name was not copied");
        assertEquals(user.getLastName(), anotherUser.getLastName(),"last name was not copied");
        assertEquals(user.getEmail(), anotherUser.getEmail(),"email was not copied");
    }
    
    @Test
    public void testCopyUserSourceIsEmpty(){
        User user = ObjectsFactory.getDefaultUser();
        User anotherUser = new User();

        user.copyUser(anotherUser);
        
        assertNotNull(user.getFirstName(),"first name was copied");
        assertNotNull(user.getLastName(),"last name was copied");
        assertNotNull(user.getEmail(),"email was copied");        
    }


}
