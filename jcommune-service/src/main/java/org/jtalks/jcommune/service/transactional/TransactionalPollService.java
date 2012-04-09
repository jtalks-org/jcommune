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

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.jtalks.jcommune.model.dao.PollDao;
import org.jtalks.jcommune.model.dao.PollOptionDao;
import org.jtalks.jcommune.model.entity.Poll;
import org.jtalks.jcommune.model.entity.PollOption;
import org.jtalks.jcommune.model.entity.Topic;
import org.jtalks.jcommune.service.PollService;
import org.springframework.security.access.prepost.PreAuthorize;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import static org.jtalks.jcommune.service.security.SecurityConstants.HAS_USER_OR_ADMIN_ROLE;

/**
 * The implementation of the {@link PollService}.
 *
 * @author Anuar Nurmakanov
 * @author Alexandre Teterin
 * @see org.jtalks.jcommune.model.entity.Poll
 */
public class TransactionalPollService extends AbstractTransactionalEntityService<Poll, PollDao>
        implements PollService {
    private PollOptionDao pollOptionDao;

    /**
     * Create an instance of service for operations with a poll.
     *
     * @param pollDao       data access object, which should be able do
     *                      all CRUD operations with {@link Poll}.
     * @param pollOptionDao data access object, which should be able do
     *                      all CRUD operations with {@link PollOption}.
     */
    public TransactionalPollService(PollDao pollDao, PollOptionDao pollOptionDao) {
        super(pollDao);
        this.pollOptionDao = pollOptionDao;
    }

    /**
     * {@inheritDoc}
     */
    @PreAuthorize(HAS_USER_OR_ADMIN_ROLE)
    @Override
    public Poll addSingleVote(Long pollId, Long selectedOptionId) {
        PollOption option = pollOptionDao.get(selectedOptionId);
        Poll poll = option.getPoll();
        if (poll.isActive()) {
            increaseVoteCount(option);
            pollOptionDao.update(option);
        }
        //TODO is exception needed?
        return poll;
    }

    /**
     * {@inheritDoc}
     */
    @PreAuthorize(HAS_USER_OR_ADMIN_ROLE)
    @Override
    public Poll addMultipleVote(Long pollId, List<Long> selectedOptionIds) {
        Poll poll = getDao().get(pollId);
        if (poll.isActive()) {
            for (PollOption option : poll.getPollOptions()) {
                if (selectedOptionIds.contains(option.getId())) {
                    increaseVoteCount(option);
                    pollOptionDao.update(option);
                }
            }
        }
        return poll;
    }

    @Override
    public void createPoll(String pollTitle, String pollOptions, String single, String endingDate, Topic topic) {
        Poll poll = new Poll(pollTitle);
        //TODO is need to handle broken string here?
        poll.setSingle(Boolean.parseBoolean(single));
        poll.setEndingDate(parseDate(endingDate));
        poll.setTopic(topic);
        try {
            poll.addPollOptions(parseOptions(pollOptions));
        } catch (IOException e) {
            poll = null;
        }

        if (poll != null) {
            this.getDao().update(poll);
            for (PollOption option : poll.getPollOptions()) {
                pollOptionDao.update(option);
            }
        }
    }

    private List<PollOption> parseOptions(String pollOptions) throws IOException {
        BufferedReader reader = new BufferedReader(new StringReader(pollOptions));
        String line;
        List<PollOption> result = new ArrayList<PollOption>();
        while ((line = reader.readLine()) != null) {
            if (!line.equals("")) {
                PollOption option = new PollOption(line);
                result.add(option);
            }
        }
        return result;
    }


    private DateTime parseDate(String date) {
        try {
            return DateTimeFormat.forPattern("dd-MM-yyyy").parseDateTime(date);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }


    /**
     * Increases a vote count in the option of poll.
     *
     * @param option the option of poll
     */
    private void increaseVoteCount(PollOption option) {
        int voteCount = option.getVoteCount();
        option.setVoteCount(++voteCount);
    }
}
