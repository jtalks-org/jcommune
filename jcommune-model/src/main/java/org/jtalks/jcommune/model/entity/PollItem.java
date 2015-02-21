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

package org.jtalks.jcommune.model.entity;

import org.jtalks.common.model.entity.Entity;

/**
 * Represents the option of poll of the topic. Counts the number of votes
 * for each option, but it doesn't track the selected answer for each user.
 *
 * @author Anuar Nurmakanov
 */
public class PollItem extends Entity {
    private String name;
    private int votesCount;
    private Poll poll;

    public static final int MIN_ITEM_LENGTH = 1;
    public static final int MAX_ITEM_LENGTH = 50;

    /**
     * Used only by Hibernate.
     */
    public PollItem() {
    }


    /**
     * Constructs the PollOption instance with required fields.
     *
     * @param name the name of the poll option
     */
    public PollItem(String name) {
        this.name = name;
    }

    /**
     * Get the poll option name.
     *
     * @return the poll option name
     */
    public String getName() {
        return name;
    }

    /**
     * Set the poll option name.
     *
     * @param name the poll option name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the count of votes for this option.
     *
     * @return the count of votes for this option
     */
    public int getVotesCount() {
        return votesCount;
    }

    /**
     * Set the count of votes for this option.
     *
     * @param votesCount the count of votes for this option
     */
    public void setVotesCount(int votesCount) {
        this.votesCount = votesCount;
    }

    /**
     * Get the poll that contains this poll option.
     *
     * @return the poll that contains this poll option
     */
    public Poll getPoll() {
        return poll;
    }

    /**
     * Set the poll that contains this poll option.
     *
     * @param poll the poll that contains this poll option
     */
    public void setPoll(Poll poll) {
        this.poll = poll;
    }

    /**
     * Increases the count of votes in the option of poll.
     */
    public void increaseVotesCount() {
        this.votesCount++;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return name;
    }
}
