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
 * Represents the option of poll of the topic.
 *
 * @author Anuar Nurmakanov
 */
public class PollOption extends Entity {
    private String name;
    private int voteCount;
    private Poll poll;

    /**
     * Used only by Hibernate.
     */
    protected PollOption() {
    }


    /**
     * Constructs the VotingOption instance with required fields.
     *
     * @param name the name of the voting option
     */
    public PollOption(String name) {
        this.name = name;
    }

    /**
     * Get the voting option name.
     *
     * @return the voting option name
     */
    public String getName() {
        return name;
    }

    /**
     * Set the voting option name.
     *
     * @param name the voting option name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the count of votes for this option.
     *
     * @return the count of votes for this option
     */
    public int getVoteCount() {
        return voteCount;
    }

    /**
     * Set the count of votes for this option.
     *
     * @param voteCount the count of votes for this option
     */
    public void setVoteCount(int voteCount) {
        this.voteCount = voteCount;
    }

    /**
     * Get the voting that contains this poll option.
     *
     * @return the voting that contains this poll option
     */
    public Poll getPoll() {
        return poll;
    }

    /**
     * Set the poll that contains this voting option.
     *
     * @param poll the poll that contains this voting option
     */
    public void setPoll(Poll poll) {
        this.poll = poll;
    }
}
