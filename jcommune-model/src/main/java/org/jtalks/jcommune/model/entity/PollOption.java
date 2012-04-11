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
public class PollOption extends Entity {
    private String name;
    private int pollCount;
    private Poll poll;

    public static final int MIN_OPTION_LENGTH = 3;
    public static final int MAX_OPTION_LENGTH = 50;

    /**
     * Used only by Hibernate.
     */
    protected PollOption() {
    }


    /**
     * Constructs the PollOption instance with required fields.
     *
     * @param name the name of the poll option
     */
    public PollOption(String name) {
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
     * Get the poll count for this option.
     *
     * @return the poll count for this option
     */
    public int getPollCount() {
        return pollCount;
    }

    /**
     * Set the poll count for this option.
     *
     * @param pollCount the poll count for this option
     */
    public void setPollCount(int pollCount) {
        this.pollCount = pollCount;
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
     * Increases a poll count in the option of poll.
     */
    public void increasePollCount() {
        this.pollCount++;
    }
}
