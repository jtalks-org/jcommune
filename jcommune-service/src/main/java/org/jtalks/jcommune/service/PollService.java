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
import org.jtalks.jcommune.model.entity.PollItem;

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
     * @param selectedOptionsIds id of selected options of a poll
     * @return changed poll
     */
    Poll vote(Long pollId, List<Long> selectedOptionsIds);

    /**
     * Save {@link org.jtalks.jcommune.model.entity.Poll} to database.
     * @param poll poll to save.
     */
    void createPoll(Poll poll);

    /**
     * Merges edited poll items for the poll with existing ones.
     * This implementation preserves vote count for items, whose
     * name hasn't been changed. It can also recognize the item if
     * it has been moved up/down the list. Newly added items have
     * vote count = 0;
     *
     * @param poll persistent object from hibernate session
     * @param newItems edited items list to merge
     */
    void mergePollItems(Poll poll, List<PollItem> newItems);
}
