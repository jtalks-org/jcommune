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
package org.jtalks.jcommune.service;

import org.jtalks.jcommune.model.entity.Poll;
import org.jtalks.jcommune.model.entity.Topic;

import java.util.List;

/**
 * Service for working with the poll.
 * Performs all needed operations for voting.
 *
 * @author Anuar Nurmakanov
 * @see org.jtalks.jcommune.model.entity.Poll
 */
public interface PollService extends EntityService<Poll> {
    /**
     * Adds one vote for all selected options of poll.
     *
     * @param pollId        id of a poll
     * @param pollOptionIds id of a option of a poll
     * @return changed poll
     */
    Poll vote(Long pollId, List<Long> pollOptionIds);

    void createPoll(String pollTitle, String pollOptions, String single, String endingDate, Topic topic);
}
