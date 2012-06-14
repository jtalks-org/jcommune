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

import org.jtalks.common.model.permissions.GeneralPermission;
import org.jtalks.common.security.SecurityService;
import org.jtalks.jcommune.model.dao.PrivateMessageDao;
import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.model.entity.PrivateMessage;
import org.jtalks.jcommune.model.entity.PrivateMessageStatus;
import org.jtalks.jcommune.service.PrivateMessageService;
import org.jtalks.jcommune.service.UserService;
import org.jtalks.jcommune.service.exceptions.NotFoundException;
import org.jtalks.jcommune.service.nontransactional.MailService;
import org.jtalks.jcommune.service.nontransactional.UserDataCacheService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

/**
 * The implementation of PrivateMessageServices.
 *
 * @author Pavel Vervenko
 * @author Kirill Afonin
 * @author Max Malakhov
 */
public class TransactionalPrivateMessageService
        extends AbstractTransactionalEntityService<PrivateMessage, PrivateMessageDao> implements PrivateMessageService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final SecurityService securityService;
    private final UserService userService;
    private final UserDataCacheService userDataCache;
    private final MailService mailService;

    /**
     * Creates the instance of service.
     *
     * @param pmDao           PrivateMessageDao
     * @param securityService for retrieving current user
     * @param userService     for getting user by name
     * @param userDataCache   service for cache for user data
     * @param mailService     for sending email notifications
     */
    public TransactionalPrivateMessageService(PrivateMessageDao pmDao,
                                              SecurityService securityService,
                                              UserService userService,
                                              UserDataCacheService userDataCache,
                                              MailService mailService) {
        super(pmDao);
        this.securityService = securityService;
        this.userService = userService;
        this.userDataCache = userDataCache;
        this.mailService = mailService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<PrivateMessage> getInboxForCurrentUser() {
        JCUser currentUser = (JCUser) securityService.getCurrentUser();
        return this.getDao().getAllForUser(currentUser);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<PrivateMessage> getOutboxForCurrentUser() {
        JCUser currentUser = (JCUser) securityService.getCurrentUser();
        return this.getDao().getAllFromUser(currentUser);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    @PreAuthorize("hasPermission(#userFrom.id, 'org.jtalks.jcommune.model.entity.JCUser', 'ProfilePermission.SEND_PRIVATE_MESSAGES')")
    public PrivateMessage sendMessage(String title, String body, JCUser recipient, JCUser userFrom) throws NotFoundException {

        PrivateMessage pm = new PrivateMessage(recipient, userFrom, title, body);
        pm.setRead(false);
        pm.setStatus(PrivateMessageStatus.SENT);
        this.getDao().saveOrUpdate(pm);

        userDataCache.incrementNewMessageCountFor(recipient.getUsername());

        securityService.createAclBuilder().grant(GeneralPermission.READ).to(recipient).on(pm).flush();

        long pmId = pm.getId();
        mailService.sendReceivedPrivateMessageNotification(recipient, pm);

        logger.debug("Private message to user {} was sent. Message id={}", recipient.getUsername(), pmId);

        return pm;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<PrivateMessage> getDraftsFromCurrentUser() {
        JCUser currentUser = (JCUser) securityService.getCurrentUser();
        return this.getDao().getDraftsFromUser(currentUser);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @PreAuthorize("hasPermission(#userFrom.id, 'org.jtalks.jcommune.model.entity.JCUser', 'ProfilePermission.SEND_PRIVATE_MESSAGES')")
    public PrivateMessage saveDraft(long id, String title, String body, JCUser recipient, JCUser userFrom)
            throws NotFoundException {
        if (recipient != null) {
            recipient = userService.getByUsername(recipient.getUsername());
        }
        PrivateMessage pm = new PrivateMessage(recipient, userFrom, title, body);
        pm.setId(id);
        pm.setStatus(PrivateMessageStatus.DRAFT);
        this.getDao().saveOrUpdate(pm);

        securityService.createAclBuilder().grant(GeneralPermission.WRITE).to(securityService.getCurrentUser()).
                on(pm).flush();

        logger.debug("Updated private message draft. Message id={}", pm.getId());

        return pm;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int currentUserNewPmCount() {
        String username = securityService.getCurrentUserUsername();
        if (username == null) {
            return 0;
        }

        Integer count = userDataCache.getNewPmCountFor(username);
        if (count != null) {
            return count;
        }
        count = this.getDao().getNewMessagesCountFor(username);
        userDataCache.putNewPmCount(username, count);
        return count;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    @PreAuthorize("hasPermission(#userFrom.id, 'org.jtalks.jcommune.model.entity.JCUser', 'ProfilePermission.SEND_PRIVATE_MESSAGES')")
    public PrivateMessage sendDraft(long id, String title, String body,
                                    JCUser recipient, JCUser userFrom) throws NotFoundException {
        PrivateMessage pm = new PrivateMessage(recipient, userFrom, title, body);
        pm.setId(id);
        pm.setRead(false);
        pm.setStatus(PrivateMessageStatus.SENT);
        this.getDao().saveOrUpdate(pm);

        userDataCache.incrementNewMessageCountFor(recipient.getUsername());

        securityService.deleteFromAcl(pm);
        securityService.createAclBuilder().grant(GeneralPermission.READ).to(recipient).on(pm).flush();

        long pmId = pm.getId();
        mailService.sendReceivedPrivateMessageNotification(recipient, pm);

        logger.debug("Private message(was draft) to user {} was sent. Message id={}",
                recipient.getUsername(), pmId);

        return pm;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @PreAuthorize("hasPermission(#id, 'org.jtalks.jcommune.model.entity.PrivateMessage', 'GeneralPermission.READ')")
    public PrivateMessage get(Long id) throws NotFoundException {
        PrivateMessage pm = super.get(id);
        if (!hasCurrentUserAccessToPM(pm)) {
            throw new NotFoundException(String.format("current user has no right to read pm %s with id %d",
                    securityService.getCurrentUser(), id));
        }
        if (this.ifMessageShouldBeMarkedAsRead(pm)) {
            pm.setRead(true);
            this.getDao().saveOrUpdate(pm);
            userDataCache.decrementNewMessageCountFor(pm.getUserTo().getUsername());
        }
        return pm;
    }

    /**
     * Checks if the private message should be marked as read.
     * The follwing conditions are checked:
     * <p>1. Current user is the recepient
     * <p>2. Message is not read already
     * <p>3. Message is not a draft
     *
     * @param pm private messag to be tested
     * @return if message should be marked as read
     */
    private boolean ifMessageShouldBeMarkedAsRead(PrivateMessage pm) {
        return securityService.getCurrentUser().equals(pm.getUserTo())
                && !pm.isRead()
                && !pm.getStatus().equals(PrivateMessageStatus.DRAFT);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String delete(List<Long> ids) {
        JCUser currentUser = (JCUser) securityService.getCurrentUser();

        String result = "inbox";
        for (Long id : ids) {

            PrivateMessage message;
            try {
                message = get(id);
            } catch (NotFoundException e) {
                logger.warn("Message #" + id + " not found", e);
                continue;
            }

            switch (message.getStatus()) {
                case DRAFT:
                    this.getDao().delete(message);
                    result = "drafts";
                    break;
                case DELETED_FROM_INBOX:
                    this.getDao().delete(message);
                    result = "outbox";
                    break;
                case DELETED_FROM_OUTBOX:
                    this.getDao().delete(message);
                    result = "inbox";
                    break;
                case SENT:
                    if (currentUser.equals(message.getUserFrom())) {
                        message.setStatus(PrivateMessageStatus.DELETED_FROM_OUTBOX);
                        result = "outbox";
                    } else {
                        message.setStatus(PrivateMessageStatus.DELETED_FROM_INBOX);
                        result = "inbox";
                    }
                    break;
            }
        }
        return result;
    }

    private boolean hasCurrentUserAccessToPM(PrivateMessage privateMessage) throws NotFoundException {
        JCUser currentUser = (JCUser) securityService.getCurrentUser();
        PrivateMessageStatus messageStatus = privateMessage.getStatus();

        if (currentUser.equals(privateMessage.getUserFrom()) &&
                (messageStatus.equals(PrivateMessageStatus.DELETED_FROM_OUTBOX))) {
            return false;
        }

        if (currentUser.equals(privateMessage.getUserTo()) &&
                (messageStatus.equals(PrivateMessageStatus.DELETED_FROM_INBOX))) {
            return false;
        }

        return true;
    }

}
