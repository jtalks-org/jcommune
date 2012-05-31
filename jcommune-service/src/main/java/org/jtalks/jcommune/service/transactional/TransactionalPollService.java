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

import org.jtalks.common.model.dao.ChildRepository;
import org.jtalks.common.model.permissions.GeneralPermission;
import org.jtalks.jcommune.model.entity.Poll;
import org.jtalks.jcommune.model.entity.PollItem;
import org.jtalks.jcommune.service.PollService;
import org.jtalks.common.security.SecurityService;
import org.jtalks.jcommune.service.security.SecurityConstants;
import org.jtalks.jcommune.service.security.TemporaryAuthorityManager;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

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
    private SecurityService securityService;
    private TemporaryAuthorityManager temporaryAuthorityManager;

    /**
     * Create an instance of service for operations with a poll.
     *
     * @param pollDao                   data access object, which should be able do
     *                                  all CRUD operations with {@link Poll}.
     * @param pollOptionDao             data access object, which should be able do
     *                                  all CRUD operations with {@link org.jtalks.jcommune.model.entity.PollItem}.
     * @param securityService           the service for security operations
     * @param temporaryAuthorityManager the  manager of temporary authorities, that
     *                                  allows to execute an operation with the
     *                                  needed authority
     */
    public TransactionalPollService(ChildRepository<Poll> pollDao,
                                    ChildRepository<PollItem> pollOptionDao,
                                    SecurityService securityService,
                                    TemporaryAuthorityManager temporaryAuthorityManager) {
        super(pollDao);
        this.pollOptionDao = pollOptionDao;
        this.securityService = securityService;
        this.temporaryAuthorityManager = temporaryAuthorityManager;
    }

    /**
     * {@inheritDoc}
     */
    @PreAuthorize("hasPermission('11', null)")
    @Override
    public Poll vote(Long pollId, List<Long> selectedOptionIds) {
        Poll poll = getDao().get(pollId);
        if (poll.isActive()) {
            prohibitRevote(poll);
            for (PollItem option : poll.getPollItems()) {
                if (selectedOptionIds.contains(option.getId())) {
                    option.increaseVotesCount();
                    pollOptionDao.update(option);
                }
            }
        }
        return poll;
    }

    @Override
    public void createPoll(Poll poll) {
        this.getDao().update(poll);
        for (PollItem option : poll.getPollItems()) {
            pollOptionDao.update(option);
        }
    }

    /**
     * Prohibit the re-vote. In this poll a user will no longer be able to participate.
     *
     * @param poll a poll, in which the user will no longer be able to participate
     */
    private void prohibitRevote(final Poll poll) {
        //TODO It should be changed after the transition to the new security.
        temporaryAuthorityManager.runWithTemporaryAuthority(
                new TemporaryAuthorityManager.SecurityOperation() {
                    @Override
                    public void doOperation() {
                        securityService.createAclBuilder().
                                grant(GeneralPermission.WRITE).to(securityService.getCurrentUser()).on(poll).flush();
                    }
                },
                SecurityConstants.ROLE_ADMIN);
    }
}
