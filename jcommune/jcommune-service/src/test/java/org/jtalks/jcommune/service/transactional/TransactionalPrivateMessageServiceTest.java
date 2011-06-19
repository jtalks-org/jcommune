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
package org.jtalks.jcommune.service.transactional;

import java.util.ArrayList;
import org.testng.Assert;
import org.jtalks.jcommune.model.entity.PrivateMessage;
import org.jtalks.jcommune.model.entity.User;
import org.testng.annotations.Test;
import org.jtalks.jcommune.model.dao.PrivateMessageDao;
import org.jtalks.jcommune.service.SecurityService;
import org.testng.annotations.BeforeMethod;

import static org.mockito.Mockito.*;

/**
 *
 * @author Pavel Vervenko
 */
public class TransactionalPrivateMessageServiceTest {

    private PrivateMessageDao pmDao;
    private SecurityService securityService;
    private TransactionalPrivateMessageService pmService;
    
    private static final long PM_ID = 100L;
    private static final String PM_UUID = "xxxx1";

    @BeforeMethod
    public void setUp() throws Exception {
        pmDao = mock(PrivateMessageDao.class);
        securityService = mock(SecurityService.class);
        pmService = new TransactionalPrivateMessageService(pmDao, securityService);
    }

    @Test
    public void getByIdTest() {
        when(pmDao.get(PM_ID)).thenReturn(getPrivateMessage());
        
        PrivateMessage pm = pmService.get(PM_ID);
        
        Assert.assertEquals(pm, getPrivateMessage());
        verify(pmDao, times(1)).get(PM_ID);        
    }

    @Test
    public void deleteByIdTest(){
        pmService.delete(PM_ID);
        
        verify(pmDao, times(1)).delete(PM_ID);
    }
    
    @Test
    public void getInboxForCurrentUserTest() {
        User user = getUser("User");
        when(pmDao.getAllForUser(user)).thenReturn(new ArrayList());
        when(securityService.getCurrentUser()).thenReturn(user);
        
        pmService.getInboxForCurrentUser();
        
        verify(pmDao, times(1)).getAllForUser(user);
        verify(securityService, times(1)).getCurrentUser();
    }
    
    @Test
    public void getOutboxForCurrentUserTest() {
        User user = getUser("User");
        when(pmDao.getAllFromUser(user)).thenReturn(new ArrayList());
        when(securityService.getCurrentUser()).thenReturn(user);
        
        pmService.getOutboxForCurrentUser();
        
        verify(pmDao, times(1)).getAllFromUser(user);        
        verify(securityService, times(1)).getCurrentUser();
    }
    
    @Test
    public void sendMessageTest() {
        PrivateMessage pm = getPrivateMessage();
        
        pmService.sendMessage(pm);
        
        verify(pmDao, times(1)).saveOrUpdate(pm);
        verify(securityService, times(1)).getCurrentUser();
    }
    
    private PrivateMessage getPrivateMessage() {
        PrivateMessage pm = PrivateMessage.createNewPrivateMessage();
        pm.setId(PM_ID);
        pm.setUuid(PM_UUID);
        pm.setBody("body");
        pm.setTitle("title");
        pm.setUserFrom(getUser("UserFrom"));
        pm.setUserTo(getUser("UserTo"));
        return pm;
    }

    private User getUser(String username) {
        User user = new User();
        user.setUsername(username);
        return user;
    }
}
