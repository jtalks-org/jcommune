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
import org.jtalks.jcommune.service.UserDataCacheService;
import org.jtalks.jcommune.service.UserService;
import org.jtalks.jcommune.service.exceptions.NotFoundException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;

import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * @author Pavel Vervenko
 * @author Kirill Afonin
 * @author Max Malakhov
 */
public class TransactionalPrivateMessageServiceTest {

    private PrivateMessageDao pmDao;
    private SecurityService securityService;
    private TransactionalPrivateMessageService pmService;
    private UserService userService;
    private UserDataCacheService userDataCache;
    private static final long PM_ID = 1L;
    private static final String USERNAME = "username";

    @BeforeMethod
    public void setUp() throws Exception {
        pmDao = mock(PrivateMessageDao.class);
        securityService = mock(SecurityService.class);
        userService = mock(UserService.class);
        userDataCache = mock(UserDataCacheService.class);
        pmService = new TransactionalPrivateMessageService(pmDao, securityService, userService, userDataCache);
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

        PrivateMessage pm = pmService.sendMessage("body", "title", USERNAME);

        assertEquals(pm.getStatus(), PrivateMessageStatus.NOT_READED);
        verify(securityService).getCurrentUser();
        verify(userService).getByUsername(USERNAME);
        verify(userDataCache).incrementNewMessageCountFor(USERNAME);
        verify(pmDao).saveOrUpdate(pm);
        verify(securityService).grantReadPermissionToCurrentUser(pm);
        verify(securityService).grantReadPermissionToUser(pm, USERNAME);
    }

    @Test(expectedExceptions = NotFoundException.class)
    public void testSendMessageToWrongUser() throws NotFoundException {
        when(userService.getByUsername(USERNAME)).thenThrow(new NotFoundException());

        PrivateMessage pm = pmService.sendMessage("body", "title", USERNAME);

        verify(pmDao, never()).saveOrUpdate(pm);
        verify(userService).getByUsername(USERNAME);
    }

    @Test
    public void testMarkAsReaded() {
        PrivateMessage pm = new PrivateMessage();
        User userTo = new User();
        userTo.setUsername(USERNAME);
        pm.setUserTo(userTo);

        pmService.markAsReaded(pm);

        assertTrue(pm.isReaded());
        verify(pmDao).saveOrUpdate(pm);
        verify(userDataCache).decrementNewMessageCountFor(USERNAME);
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

        PrivateMessage pm = pmService.saveDraft(PM_ID, "body", "title", USERNAME);

        assertEquals(pm.getId(), PM_ID);
        verify(securityService).getCurrentUser();
        verify(userService).getByUsername(USERNAME);
        verify(pmDao).saveOrUpdate(pm);
        verify(securityService).grantAdminPermissionToCurrentUser(pm);
    }

    @Test
    public void testCurrentUserNewPmCount() {
        int expectedPmCount = 2;
        when(securityService.getCurrentUserUsername()).thenReturn(USERNAME);
        when(pmDao.getNewMessagesCountFor(USERNAME)).thenReturn(expectedPmCount);
        when(userDataCache.getNewPmCountFor(USERNAME)).thenReturn(null);

        int newPmCount = pmService.currentUserNewPmCount();

        assertEquals(newPmCount, expectedPmCount);
        verify(securityService).getCurrentUserUsername();
        verify(pmDao).getNewMessagesCountFor(USERNAME);
        verify(userDataCache).putNewPmCount(USERNAME, newPmCount);
    }

    @Test
    public void testCurrentUserNewPmCountCached() {
        int expectedPmCount = 2;
        when(securityService.getCurrentUserUsername()).thenReturn(USERNAME);
        when(userDataCache.getNewPmCountFor(USERNAME)).thenReturn(expectedPmCount);

        int newPmCount = pmService.currentUserNewPmCount();

        assertEquals(newPmCount, expectedPmCount);
        verify(pmDao, never()).getNewMessagesCountFor(anyString());
        verify(userDataCache).getNewPmCountFor(USERNAME);
    }

    @Test
    public void testCurrentUserNewPmCountWithoutUser() {
        when(securityService.getCurrentUserUsername()).thenReturn(null);

        int newPmCount = pmService.currentUserNewPmCount();

        assertEquals(newPmCount, 0);
        verify(securityService).getCurrentUserUsername();
    }

    @Test
    public void testSendDraft() throws NotFoundException {

        PrivateMessage pm = pmService.sendDraft(1L, "body", "title", USERNAME);

        assertEquals(pm.getStatus(), PrivateMessageStatus.NOT_READED);
        verify(securityService).getCurrentUser();
        verify(userService).getByUsername(USERNAME);
        verify(userDataCache).incrementNewMessageCountFor(USERNAME);
        verify(pmDao).saveOrUpdate(pm);
        verify(securityService).deleteFromAcl(pm);
        verify(securityService).grantReadPermissionToCurrentUser(pm);
        verify(securityService).grantReadPermissionToUser(pm, USERNAME);
    }
}
