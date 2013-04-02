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

import ch.lambdaj.function.closure.Closure1;
import org.jtalks.common.model.dao.ChildRepository;
import org.jtalks.common.model.dao.GroupDao;
import org.jtalks.common.model.permissions.GeneralPermission;
import org.jtalks.common.security.SecurityService;
import org.jtalks.jcommune.model.entity.Poll;
import org.jtalks.jcommune.model.entity.PollItem;
import org.jtalks.jcommune.service.PollService;
import org.jtalks.jcommune.service.UserService;
import org.jtalks.jcommune.service.security.AdministrationGroup;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.ListIterator;

import static ch.lambdaj.Lambda.*;

/**
 * The implementation of the {@link PollService}.
 *
 * @author Anuar Nurmakanov
 * @author Alexandre Teterin
 * @see org.jtalks.jcommune.model.entity.Poll
 */
public class TransactionalPollService extends AbstractTransactionalEntityService<Poll, ChildRepository<Poll>>
        implements PollService {
    private ChildRepository<PollItem> pollOptionDao;
    private GroupDao groupDao;
    private SecurityService securityService;
    private UserService userService;

    /**
     * Create an instance of service for operations with a poll.
     *
     * @param pollDao                   data access object, which should be able do
     *                                  all CRUD operations with {@link org.jtalks.jcommune.model.entity.Poll}.
     * @param groupDao                  this dao returns user group for permission granting
     * @param pollOptionDao             data access object, which should be able do
     *                                  all CRUD operations with {@link org.jtalks.jcommune.model.entity.PollItem}.
     * @param securityService           the service for security operations
     * @param userService               to fetch the user currently logged in
     */
    public TransactionalPollService(ChildRepository<Poll> pollDao,
                                    GroupDao groupDao,
                                    ChildRepository<PollItem> pollOptionDao,
                                    SecurityService securityService,
                                    UserService userService) {
        super(pollDao);
        this.pollOptionDao = pollOptionDao;
        this.groupDao = groupDao;
        this.securityService = securityService;
        this.userService = userService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @PreAuthorize("hasPermission(#pollId, 'POLL', 'GeneralPermission.WRITE')")
    public Poll vote(Long pollId, List<Long> selectedOptionsIds) {
        Poll poll = getDao().get(pollId);
        if (poll.isActive()) {
            prohibitRevote(poll);
            for (PollItem option : poll.getPollItems()) {
                if (selectedOptionsIds.contains(option.getId())) {
                    option.increaseVotesCount();
                    pollOptionDao.update(option);
                }
            }
        }
        return poll;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void createPoll(Poll poll) {
        this.getDao().update(poll);
        Closure1<PollItem> closure = closure(PollItem.class);
        of(pollOptionDao).update(var(PollItem.class));
        closure.each(poll.getPollItems());
        securityService.createAclBuilder().grant(GeneralPermission.WRITE)
                .to(groupDao.getGroupByName(AdministrationGroup.USER.getName()))
                .on(poll).flush();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mergePollItems(Poll poll, List<PollItem> newItems) {
        List<PollItem> existing = poll.getPollItems();
        ListIterator<PollItem> updated = newItems.listIterator();
        while (updated.hasNext()) {
            PollItem item = updated.next();
            for (PollItem old : existing){
                if (item.getName().equals(old.getName())){
                    updated.set(old);
                }
            }
        }
        existing.clear();
        existing.addAll(newItems);
    }


    /**
     * Prohibit the re-vote. In this poll a user will no longer be able to participate.
     *
     * @param poll a poll, in which the user will no longer be able to participate
     */
    private void prohibitRevote(final Poll poll) {
        securityService.createAclBuilder().restrict(GeneralPermission.WRITE).
                to(userService.getCurrentUser()).on(poll).flush();
    }
}
