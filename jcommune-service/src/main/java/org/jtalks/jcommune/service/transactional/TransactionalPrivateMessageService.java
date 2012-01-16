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

import org.jtalks.jcommune.model.dao.PrivateMessageDao;
import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.model.entity.PrivateMessage;
import org.jtalks.jcommune.model.entity.PrivateMessageStatus;
import org.jtalks.jcommune.service.PrivateMessageService;
import org.jtalks.jcommune.service.UserService;
import org.jtalks.jcommune.service.exceptions.NotFoundException;
import org.jtalks.jcommune.service.nontransactional.MailService;
import org.jtalks.jcommune.service.nontransactional.SecurityService;
import org.jtalks.jcommune.service.nontransactional.UserDataCacheService;
import org.jtalks.jcommune.service.security.SecurityConstants;
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
        JCUser currentUser = securityService.getCurrentUser();
        return this.getDao().getAllForUser(currentUser);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<PrivateMessage> getOutboxForCurrentUser() {
        JCUser currentUser = securityService.getCurrentUser();
        return this.getDao().getAllFromUser(currentUser);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    @PreAuthorize("hasAnyRole('" + SecurityConstants.ROLE_USER + "','" + SecurityConstants.ROLE_ADMIN + "')")
    public PrivateMessage sendMessage(String title, String body, String recipientUsername) throws NotFoundException {
        JCUser recipient = userService.getByUsername(recipientUsername);
        PrivateMessage pm = populateMessage(title, body, recipient);
        pm.setStatus(PrivateMessageStatus.NOT_READ);
        this.getDao().saveOrUpdate(pm);

        userDataCache.incrementNewMessageCountFor(recipientUsername);

        securityService.grantToCurrentUser().user(recipientUsername).read().on(pm);

        long pmId = pm.getId();
        mailService.sendReceivedPrivateMessageNotification(recipient, pmId);

        logger.debug("Private message to user {} was sent. Message id={}", recipientUsername, pmId);

        return pm;
    }

    /**
     * Populate {@link PrivateMessage} from values.
     *
     * @param title     title
     * @param body      message content
     * @param recipient message recipient
     * @return created {@link PrivateMessage}
     * @throws NotFoundException if current user of recipient not found
     */
    private PrivateMessage populateMessage(String title, String body, JCUser recipient) throws NotFoundException {
        JCUser userFrom = securityService.getCurrentUser();
        return new PrivateMessage(recipient, userFrom, title, body);
    }

    /**
     * {@inheritDoc}
     */
    public void markAsRead(PrivateMessage pm) {
        if (!pm.isRead()) {
            pm.markAsRead();
            this.getDao().saveOrUpdate(pm);
            userDataCache.decrementNewMessageCountFor(pm.getUserTo().getUsername());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<PrivateMessage> getDraftsFromCurrentUser() {
        JCUser currentUser = securityService.getCurrentUser();
        return this.getDao().getDraftsFromUser(currentUser);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @PreAuthorize("hasAnyRole('" + SecurityConstants.ROLE_USER + "','" + SecurityConstants.ROLE_ADMIN + "')")
    public PrivateMessage saveDraft(long id, String title, String body, String recipientUsername)
            throws NotFoundException {
        JCUser recipient = userService.getByUsername(recipientUsername);
        PrivateMessage pm = populateMessage(title, body, recipient);
        pm.setId(id);
        pm.markAsDraft();
        this.getDao().saveOrUpdate(pm);

        securityService.grantToCurrentUser().admin().on(pm);

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
    @PreAuthorize("hasPermission(#id, 'org.jtalks.jcommune.model.entity.PrivateMessage', admin)")
    public PrivateMessage sendDraft(long id, String title, String body,
                                    String recipientUsername) throws NotFoundException {
        JCUser recipient = userService.getByUsername(recipientUsername);
        PrivateMessage pm = populateMessage(title, body, recipient);
        pm.setId(id);
        pm.setStatus(PrivateMessageStatus.NOT_READ);
        this.getDao().saveOrUpdate(pm);

        userDataCache.incrementNewMessageCountFor(recipientUsername);

        securityService.deleteFromAcl(pm);
        securityService.grantToCurrentUser().user(recipientUsername).read().on(pm);

        logger.debug("Private message(was draft) to user {} was sent. Message id={}",
                recipientUsername, pm.getId());

        return pm;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @PreAuthorize("hasPermission(#id, 'org.jtalks.jcommune.model.entity.PrivateMessage', admin) or " +
            "hasPermission(#id, 'org.jtalks.jcommune.model.entity.PrivateMessage', read)")
    public PrivateMessage get(Long id) throws NotFoundException {
        return super.get(id);
    }
}
