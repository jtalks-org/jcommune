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

import org.jtalks.common.model.entity.Property;
import org.jtalks.common.model.entity.User;
import org.jtalks.common.model.permissions.GeneralPermission;
import org.jtalks.common.security.acl.builders.CompoundAclBuilder;
import org.jtalks.jcommune.model.dao.PrivateMessageDao;
import org.jtalks.jcommune.model.dao.PropertyDao;
import org.jtalks.jcommune.model.dto.PageRequest;
import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.model.entity.JCommuneProperty;
import org.jtalks.jcommune.model.entity.PrivateMessage;
import org.jtalks.jcommune.model.entity.PrivateMessageStatus;
import org.jtalks.jcommune.service.UserService;
import org.jtalks.jcommune.plugin.api.exceptions.NotFoundException;
import org.jtalks.jcommune.service.nontransactional.MailService;
import org.jtalks.jcommune.service.nontransactional.UserDataCacheService;
import org.jtalks.jcommune.service.security.SecurityService;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

import static org.jtalks.jcommune.service.TestUtils.mockAclBuilder;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.testng.Assert.*;

/**
 * @author Pavel Vervenko
 * @author Kirill Afonin
 * @author Max Malakhov
 */
public class TransactionalPrivateMessageServiceTest {

    private static final String PROPERTY_NAME = "property";
    private static final boolean SENDING_NOTIFICATIONS_ENABLED = true;
    private static final boolean SENDING_NOTIFICATIONS_DISABLED = false;

    @Mock
    private PrivateMessageDao pmDao;
    @Mock
    private SecurityService securityService;
    @Mock
    private UserService userService;
    @Mock
    private UserDataCacheService userDataCache;
    @Mock
    private MailService mailService;
    @Mock
    private PropertyDao propertyDao;
    private JCommuneProperty sendingNotificationsEnabledProperty = JCommuneProperty.SENDING_NOTIFICATIONS_ENABLED;

    private TransactionalPrivateMessageService pmService;

    private static final long PM_ID = 1L;
    private static final String USERNAME = "username";
    private static final JCUser JC_USER = new JCUser(USERNAME, "123@123.ru", "123");
    private CompoundAclBuilder<User> aclBuilder;

    private static final String DRAFTS = "drafts";
    private static final String OUTBOX = "outbox";
    private static final String INBOX = "inbox";

    private JCUser user = new JCUser(USERNAME, "email", "password");

    @BeforeMethod
    public void setUp() throws Exception {
        initMocks(this);
        sendingNotificationsEnabledProperty.setName(PROPERTY_NAME);
        sendingNotificationsEnabledProperty.setPropertyDao(propertyDao);
        aclBuilder = mockAclBuilder();
        pmService = new TransactionalPrivateMessageService(pmDao, securityService, userService, userDataCache,
                mailService, sendingNotificationsEnabledProperty);
        when(userService.getCurrentUser()).thenReturn(user);
    }

    @Test
    public void testGetInboxForCurrentUser() {
        String pageNumber = "1";
        List<PrivateMessage> messages = Arrays.asList(new PrivateMessage(user, user,
                "Message title", "Private message body"));
        Page<PrivateMessage> expectedPage = new PageImpl<>(messages);
        when(pmDao.getAllForUser(eq(user), Matchers.<PageRequest>any())).thenReturn(expectedPage);

        Page<PrivateMessage> actual = pmService.getInboxForCurrentUser(pageNumber);

        verify(pmDao).getAllForUser(eq(user), Matchers.<PageRequest>any());
        assertEquals(expectedPage, actual);
    }

    @Test
    public void testGetOutboxForCurrentUser() {
        String pageNumber = "1";
        List<PrivateMessage> messages = Arrays.asList(new PrivateMessage(user, user,
                "Message title", "Private message body"));
        Page<PrivateMessage> expectedPage = new PageImpl<>(messages);
        when(pmDao.getAllFromUser(eq(user), Matchers.<PageRequest>any())).thenReturn(expectedPage);

        Page<PrivateMessage> actual = pmService.getOutboxForCurrentUser(pageNumber);

        verify(pmDao).getAllFromUser(eq(user), Matchers.<PageRequest>any());
        assertEquals(expectedPage, actual);
    }

    @Test
    public void testSendMessageNotificationEnabled() throws NotFoundException {

        when(securityService.<User>createAclBuilder()).thenReturn(aclBuilder);
        when(propertyDao.getByName(PROPERTY_NAME)).
                thenReturn(new Property(PROPERTY_NAME, String.valueOf(SENDING_NOTIFICATIONS_ENABLED)));

        JC_USER.setSendPmNotification(SENDING_NOTIFICATIONS_ENABLED);
        PrivateMessage pm = pmService.sendMessage("body", "title", JC_USER, user);

        assertFalse(pm.isRead());
        assertEquals(pm.getStatus(), PrivateMessageStatus.SENT);
        verify(userDataCache).incrementNewMessageCountFor(USERNAME);
        verify(pmDao).saveOrUpdate(pm);
        verify(aclBuilder, times(2)).grant(GeneralPermission.READ);
        verify(propertyDao).getByName(PROPERTY_NAME);
        verify(mailService, times(1)).sendReceivedPrivateMessageNotification(JC_USER, pm);
    }

    @Test
    public void testSendMessageNotificationDisabled() throws NotFoundException {
        when(securityService.<User>createAclBuilder()).thenReturn(aclBuilder);

        when(propertyDao.getByName(PROPERTY_NAME)).
                thenReturn(new Property(PROPERTY_NAME, String.valueOf(SENDING_NOTIFICATIONS_DISABLED)));

        JC_USER.setSendPmNotification(SENDING_NOTIFICATIONS_DISABLED);
        PrivateMessage pm = pmService.sendMessage("body", "title", JC_USER, user);

        assertFalse(pm.isRead());
        assertEquals(pm.getStatus(), PrivateMessageStatus.SENT);
        verify(userDataCache).incrementNewMessageCountFor(USERNAME);
        verify(pmDao).saveOrUpdate(pm);
        verify(aclBuilder, times(2)).grant(GeneralPermission.READ);
        verify(propertyDao).getByName(PROPERTY_NAME);
        verify(mailService, times(0)).sendReceivedPrivateMessageNotification(JC_USER,pm);
    }

    @Test
    public void testGetDraftsForCurrentUser() {
        String pageNumber = "1";
        List<PrivateMessage> messages = Arrays.asList(new PrivateMessage(user, user,
                "Message title", "Private message body"));
        Page<PrivateMessage> expectedPage = new PageImpl<>(messages);
        when(pmDao.getDraftsForUser(eq(user), Matchers.<PageRequest>any())).thenReturn(expectedPage);

        Page<PrivateMessage> actual = pmService.getDraftsForCurrentUser(pageNumber);

        verify(pmDao).getDraftsForUser(eq(user), Matchers.<PageRequest>any());
        assertEquals(expectedPage, actual);
    }

    @Test
    public void testSaveDraft() throws NotFoundException {
        JCUser recipient = new JCUser("name", "example@example.com", "pwd");

        when(securityService.<User>createAclBuilder()).thenReturn(aclBuilder);

        pmService.saveDraft(PM_ID, recipient, "title", "body", JC_USER);

        verify(pmDao).saveOrUpdate(any(PrivateMessage.class));
        verify(aclBuilder).grant(GeneralPermission.WRITE);
        verify(aclBuilder).grant(GeneralPermission.READ);
        verify(aclBuilder, times(2)).on(any(PrivateMessage.class));
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
    public void testSendDraftNotificationEnabled() throws NotFoundException {
        when(securityService.<User>createAclBuilder()).thenReturn(aclBuilder);
        when(propertyDao.getByName(PROPERTY_NAME)).
                thenReturn(new Property(PROPERTY_NAME, String.valueOf(SENDING_NOTIFICATIONS_ENABLED)));

        JC_USER.setSendPmNotification(SENDING_NOTIFICATIONS_ENABLED);
        PrivateMessage pm = pmService.sendDraft(1L, "body", "title", JC_USER, user);

        assertFalse(pm.isRead());
        assertEquals(pm.getStatus(), PrivateMessageStatus.SENT);
        verify(userDataCache).incrementNewMessageCountFor(USERNAME);
        verify(pmDao).saveOrUpdate(pm);
        verify(securityService).deleteFromAcl(pm);
        verify(aclBuilder, times(2)).grant(GeneralPermission.READ);
        verify(propertyDao).getByName(PROPERTY_NAME);
        verify(mailService, times(1)).sendReceivedPrivateMessageNotification(JC_USER, pm);
    }

    @Test
    public void testSendDraftNotificationDisabled() throws NotFoundException {
        when(securityService.<User>createAclBuilder()).thenReturn(aclBuilder);
        when(propertyDao.getByName(PROPERTY_NAME)).
                thenReturn(new Property(PROPERTY_NAME, String.valueOf(SENDING_NOTIFICATIONS_DISABLED)));

        JC_USER.setSendPmNotification(SENDING_NOTIFICATIONS_DISABLED);
        PrivateMessage pm = pmService.sendDraft(1L, "body", "title", JC_USER, user);

        assertFalse(pm.isRead());
        assertEquals(pm.getStatus(), PrivateMessageStatus.SENT);
        verify(userDataCache).incrementNewMessageCountFor(USERNAME);
        verify(pmDao).saveOrUpdate(pm);
        verify(securityService).deleteFromAcl(pm);
        verify(aclBuilder, times(2)).grant(GeneralPermission.READ);
        verify(propertyDao).getByName(PROPERTY_NAME);
        verify(mailService, times(0)).sendReceivedPrivateMessageNotification(JC_USER, pm);
    }

    @Test
    public void testGetMessageToMe() throws NotFoundException {
        PrivateMessage expected = new PrivateMessage(user, user, "title", "body");
        when(pmDao.get(PM_ID)).thenReturn(expected);
        when(pmDao.isExist(PM_ID)).thenReturn(true);

        PrivateMessage pm = pmService.get(PM_ID);

        assertEquals(pm, expected);
        assertTrue(pm.isRead());
        verify(pmDao).saveOrUpdate(pm);
        verify(userDataCache).decrementNewMessageCountFor(USERNAME);
    }

    @Test(expectedExceptions = NotFoundException.class)
    public void testGetNotFound() throws NotFoundException {
        when(pmDao.isExist(PM_ID)).thenReturn(false);

        PrivateMessage pm = pmService.get(PM_ID);
    }

    @Test(expectedExceptions = NotFoundException.class)
    public void testGetNotFoundIfUserHasNoAccessToDeletedPmFromOutBox() throws NotFoundException {
        PrivateMessage message = new PrivateMessage(user, user, null, null);
        message.setStatus(PrivateMessageStatus.DELETED_FROM_OUTBOX);

        when(pmDao.get(PM_ID)).thenReturn(message);
        when(pmDao.isExist(PM_ID)).thenReturn(true);

        pmService.get(PM_ID);

        verify(pmDao).get(PM_ID);
    }

    @Test(expectedExceptions = NotFoundException.class)
    public void testGetNotFoundIfUserHasNoAccessToDeletePmFromInbox() throws NotFoundException {
        PrivateMessage message = new PrivateMessage(user, user, null, null);
        message.setStatus(PrivateMessageStatus.DELETED_FROM_INBOX);

        when(pmDao.get(PM_ID)).thenReturn(message);
        when(pmDao.isExist(PM_ID)).thenReturn(true);

        pmService.get(PM_ID);

        verify(pmDao).get(PM_ID);
    }


    @Test
    public void testGetReadAlreadyRead() throws NotFoundException {
        PrivateMessage expected = new PrivateMessage(user, user, "title", "body");
        expected.setRead(true);
        when(pmDao.get(PM_ID)).thenReturn(expected);
        when(pmDao.isExist(PM_ID)).thenReturn(true);

        PrivateMessage pm = pmService.get(PM_ID);

        verify(pmDao, never()).saveOrUpdate(pm);
        verify(userDataCache, never()).decrementNewMessageCountFor(USERNAME);
    }

    @Test
    public void testGetPrivateMessageInDraftStatus() throws NotFoundException {
        PrivateMessage message = new PrivateMessage(user, user, "title", "body");
        message.setStatus(PrivateMessageStatus.DRAFT);

        when(pmDao.get(PM_ID)).thenReturn(message);
        when(pmDao.isExist(PM_ID)).thenReturn(true);

        PrivateMessage resultMessage = pmService.get(PM_ID);

        assertEquals(resultMessage.isRead(), false,
                "Message status is draft, so message shouldn't be marked as read");
        verify(pmDao, never()).saveOrUpdate(resultMessage);
        verify(userDataCache, never()).decrementNewMessageCountFor(USERNAME);
    }

    @Test
    public void testGetPrivateMessageUserToNotCurrentUser() throws NotFoundException {
        PrivateMessage message = new PrivateMessage(user, user, "title", "body");
        JCUser currentUser = new JCUser(USERNAME, "email", "password");

        when(pmDao.get(PM_ID)).thenReturn(message);
        when(pmDao.isExist(PM_ID)).thenReturn(true);
        when(userService.getCurrentUser()).thenReturn(currentUser);

        PrivateMessage resultMessage = pmService.get(PM_ID);

        assertEquals(resultMessage.isRead(), false,
                "The message isn't addressed to the current user, so message shouldn't be marked as read.");
        verify(pmDao, never()).saveOrUpdate(resultMessage);
        verify(userDataCache, never()).decrementNewMessageCountFor(USERNAME);
    }

    @Test
    public void testDeleteDrafts() throws NotFoundException {
        PrivateMessage message = new PrivateMessage(null, null, null, null);
        message.setStatus(PrivateMessageStatus.DRAFT);

        when(pmDao.get(1L)).thenReturn(message);
        when(pmDao.get(2L)).thenReturn(message);
        when(pmDao.isExist(1L)).thenReturn(true);
        when(pmDao.isExist(2L)).thenReturn(true);

        String resultSingle = pmService.delete(Arrays.asList(1L));

        assertEquals(resultSingle, DRAFTS);
        verify(pmDao).delete(any(PrivateMessage.class));

        String resultMultiple = pmService.delete(Arrays.asList(1L, 2L));

        assertEquals(resultMultiple, DRAFTS);
        verify(pmDao, times(3)).delete(any(PrivateMessage.class));
    }

    @Test
    public void testDeleteFromInbox() throws NotFoundException {
        JCUser otherUser = new JCUser(USERNAME, null, null);

        PrivateMessage message1 = new PrivateMessage(user, otherUser, null, null);
        message1.setStatus(PrivateMessageStatus.SENT);

        PrivateMessage message2 = new PrivateMessage(user, otherUser, null, null);
        message2.setStatus(PrivateMessageStatus.SENT);

        PrivateMessage message3 = new PrivateMessage(user, otherUser, null, null);
        message3.setStatus(PrivateMessageStatus.DELETED_FROM_OUTBOX);


        when(pmDao.get(1L)).thenReturn(message1);
        when(pmDao.get(2L)).thenReturn(message2);
        when(pmDao.get(3L)).thenReturn(message3);
        when(pmDao.isExist(1L)).thenReturn(true);
        when(pmDao.isExist(2L)).thenReturn(true);
        when(pmDao.isExist(3L)).thenReturn(true);

        String resultSingle = pmService.delete(Arrays.asList(1L));

        assertEquals(resultSingle, INBOX);
        assertEquals(message1.getStatus(), PrivateMessageStatus.DELETED_FROM_INBOX);
        verify(pmDao, never()).delete(any(PrivateMessage.class));

        String resultMultiple = pmService.delete(Arrays.asList(2L, 3L));

        assertEquals(resultMultiple, INBOX);
        assertEquals(message2.getStatus(), PrivateMessageStatus.DELETED_FROM_INBOX);
        verify(pmDao, times(1)).delete(any(PrivateMessage.class));
    }

    @Test
    public void testDeleteFromOutbox() throws NotFoundException {
        JCUser otherUser = new JCUser(USERNAME, null, null);

        PrivateMessage message1 = new PrivateMessage(otherUser, user, null, null);
        message1.setStatus(PrivateMessageStatus.SENT);

        PrivateMessage message2 = new PrivateMessage(otherUser, user, null, null);
        message2.setStatus(PrivateMessageStatus.SENT);

        PrivateMessage message3 = new PrivateMessage(otherUser, user, null, null);
        message3.setStatus(PrivateMessageStatus.DELETED_FROM_INBOX);

        when(pmDao.get(1L)).thenReturn(message1);
        when(pmDao.get(2L)).thenReturn(message2);
        when(pmDao.get(3L)).thenReturn(message3);
        when(pmDao.isExist(1L)).thenReturn(true);
        when(pmDao.isExist(2L)).thenReturn(true);
        when(pmDao.isExist(3L)).thenReturn(true);

        String resultSingle = pmService.delete(Arrays.asList(1L));

        assertEquals(resultSingle, OUTBOX);
        assertEquals(message1.getStatus(), PrivateMessageStatus.DELETED_FROM_OUTBOX);
        verify(pmDao, never()).delete(any(PrivateMessage.class));

        String resultMultiple = pmService.delete(Arrays.asList(2L, 3L));

        assertEquals(resultMultiple, OUTBOX);
        assertEquals(message2.getStatus(), PrivateMessageStatus.DELETED_FROM_OUTBOX);
        verify(pmDao, times(1)).delete(any(PrivateMessage.class));
    }

    @Test(expectedExceptions = NotFoundException.class)
    public void testDeleteNotFound() throws NotFoundException {
        PrivateMessage message1 = new PrivateMessage(user, user, null, null);
        message1.setStatus(PrivateMessageStatus.DRAFT);

        PrivateMessage message2 = new PrivateMessage(user, user, null, null);
        message2.setStatus(PrivateMessageStatus.DRAFT);

        when(pmDao.get(1L)).thenReturn(message1);
        when(pmDao.get(2L)).thenReturn(message2);
        when(pmDao.isExist(1L)).thenReturn(true);
        when(pmDao.isExist(2L)).thenReturn(true);

        String result = pmService.delete(Arrays.asList(1L, 1234L, 2L));
        assertEquals(result, DRAFTS);
        verify(pmDao, times(2)).delete(any(PrivateMessage.class));
    }

    @Test
    public void testHandleDraft() throws Exception{
        JCUser currentUser = new JCUser(USERNAME, "email", "password");
        when(userService.getCurrentUser()).thenReturn(currentUser);

    }
/*
    @Test
    public void testIfCurrentUserHasNoAccesToDeletedOutboxPm() throws NotFoundException {
        PrivateMessage message1 = new PrivateMessage(user, user, null, null);
        message1.setStatus(PrivateMessageStatus.DELETED_FROM_OUTBOX);

        when(userService.getCurrentUser()).thenReturn(user);
        when(pmDao.get(PM_ID)).thenReturn(message1);
        when(pmDao.isExist(PM_ID)).thenReturn(true);

        boolean expectedPermision = pmService.hasCurrentUserAccessToPM(PM_ID);

        //todo assertFalse(expectedPermision);
        //todo verify(pmDao).get(PM_ID);
    }

    @Test
    public void testIfCurrentUserHasNoAccessToDeletedInboxPm() throws NotFoundException {
        PrivateMessage message1 = new PrivateMessage(user, user, null, null);
        message1.setStatus(PrivateMessageStatus.DELETED_FROM_INBOX);

        when(userService.getCurrentUser()).thenReturn(user);
        when(pmDao.get(PM_ID)).thenReturn(message1);
        when(pmDao.isExist(PM_ID)).thenReturn(true);

        //todo boolean expectedPermision = pmService.hasCurrentUserAccessToPM(PM_ID);

        //todo  assertFalse(expectedPermision);
        //todo  verify(pmDao).get(PM_ID);
    }

    @Test
    public void testIfCurrentUserHasAccessToAllPmExceptDeleted() throws NotFoundException {
        PrivateMessage message1 = new PrivateMessage(user, user, null, null);
        message1.setStatus(PrivateMessageStatus.DRAFT);

        PrivateMessage message2 = new PrivateMessage(user, user, null, null);
        message2.setStatus(PrivateMessageStatus.SENT);

        when(pmDao.get(1L)).thenReturn(message1);
        when(pmDao.isExist(1L)).thenReturn(true);
        when(pmDao.get(2L)).thenReturn(message1);
        when(pmDao.isExist(2L)).thenReturn(true);

        //todo  boolean expectedPermisionForDraftPm = pmService.hasCurrentUserAccessToPM(1L);
        //todo   boolean expectedPermisionForSentPm = pmService.hasCurrentUserAccessToPM(2L);

        //todo  assertTrue(expectedPermisionForDraftPm);
        //todo   assertTrue(expectedPermisionForSentPm);

        //todo   verify(pmDao).get(1L);
        //todo  verify(pmDao).get(2L);
    }*/

}
