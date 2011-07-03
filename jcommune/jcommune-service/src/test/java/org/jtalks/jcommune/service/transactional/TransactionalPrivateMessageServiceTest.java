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

import org.jtalks.jcommune.model.dao.PrivateMessageDao;
import org.jtalks.jcommune.model.entity.PrivateMessage;
import org.jtalks.jcommune.model.entity.PrivateMessageStatus;
import org.jtalks.jcommune.model.entity.User;
import org.jtalks.jcommune.service.SecurityService;
import org.jtalks.jcommune.service.UserService;
import org.jtalks.jcommune.service.exceptions.NotFoundException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;

import static org.mockito.Mockito.*;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * @author Pavel Vervenko
 * @author Kirill Afonin
 */
public class TransactionalPrivateMessageServiceTest {

    private PrivateMessageDao pmDao;
    private SecurityService securityService;
    private TransactionalPrivateMessageService pmService;
    private UserService userService;
    private static final long PM_ID = 1L;


    @BeforeMethod
    public void setUp() throws Exception {
        pmDao = mock(PrivateMessageDao.class);
        securityService = mock(SecurityService.class);
        userService = mock(UserService.class);
        pmService = new TransactionalPrivateMessageService(pmDao, securityService, userService);
    }

    @Test
    public void testGetInboxForCurrentUser() {
        User user = new User();
        when(pmDao.getAllForUser(user)).thenReturn(new ArrayList<PrivateMessage>());
        when(securityService.getCurrentUser()).thenReturn(user);

        pmService.getInboxForCurrentUser();

        verify(pmDao).getAllForUser(user);
        verify(securityService).getCurrentUser();
    }

    @Test
    public void testGetOutboxForCurrentUser() {
        User user = new User();
        when(pmDao.getAllFromUser(user)).thenReturn(new ArrayList<PrivateMessage>());
        when(securityService.getCurrentUser()).thenReturn(user);

        pmService.getOutboxForCurrentUser();

        verify(pmDao).getAllFromUser(user);
        verify(securityService).getCurrentUser();
    }

    @Test
    public void testSendMessage() throws NotFoundException {
        String userTo = "UserTo";

        PrivateMessage pm = pmService.sendMessage("body", "title", userTo);

        assertEquals(pm.getStatus(), PrivateMessageStatus.NOT_READED);
        verify(securityService).getCurrentUser();
        verify(userService).getByUsername(userTo);
        verify(pmDao).saveOrUpdate(pm);
    }

    @Test(expectedExceptions = NotFoundException.class)
    public void testSendMessageToWrongUser() throws NotFoundException {
        String wrongUsername = "wrong";
        when(userService.getByUsername(wrongUsername)).thenThrow(new NotFoundException());

        PrivateMessage pm = pmService.sendMessage("body", "title", wrongUsername);

        verify(pmDao, never()).saveOrUpdate(pm);
        verify(userService).getByUsername(wrongUsername);
    }

    @Test
    public void testMarkAsReaded() {
        PrivateMessage pm = new PrivateMessage();

        pmService.markAsReaded(pm);

        assertTrue(pm.isReaded());
        verify(pmDao).saveOrUpdate(pm);
    }

    @Test
    public void testGetDraftsFromCurrentUser() {
        User user = new User();
        when(pmDao.getDraftsFromUser(user)).thenReturn(new ArrayList<PrivateMessage>());
        when(securityService.getCurrentUser()).thenReturn(user);

        pmService.getDraftsFromCurrentUser();

        verify(pmDao).getDraftsFromUser(user);
        verify(securityService).getCurrentUser();
    }

    @Test
    public void testSaveDraft() throws NotFoundException {
        String recipient = "recipient";

        PrivateMessage pm = pmService.saveDraft(PM_ID, "body", "title", recipient);

        assertEquals(pm.getId(), PM_ID);
        verify(securityService).getCurrentUser();
        verify(userService).getByUsername(recipient);
        verify(pmDao).saveOrUpdate(pm);
    }

    @Test
    public void testCurrentUserNewPmCount() {
        String username = "username";
        int expectedPmCount = 2;
        when(securityService.getCurrentUserUsername()).thenReturn(username);
        when(pmDao.getNewMessagesCountFor(username)).thenReturn(expectedPmCount);

        int newPmCount = pmService.currentUserNewPmCount();

        assertEquals(newPmCount, expectedPmCount);
        verify(securityService).getCurrentUserUsername();
        verify(pmDao).getNewMessagesCountFor(username);
    }

    @Test
    public void testCurrentUserNewPmCountWithoutUser() {
        when(securityService.getCurrentUserUsername()).thenReturn(null);

        int newPmCount = pmService.currentUserNewPmCount();

        assertEquals(newPmCount, 0);
        verify(securityService).getCurrentUserUsername();
    }
}
