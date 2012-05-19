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

package org.jtalks.jcommune.web.dto;

import org.jtalks.jcommune.model.entity.PollItem;

/**
 * Data transfer object for transferring poll option to the client side.
 *
 * @author Anuar Nurmakanov
 */
public class PollOptionDto {
    private long id;
    private int votesCount;

    /**
     * Default constructor.
     * It is also required for JSON.
     */
    public PollOptionDto() {
    }

    /**
     * Creates data transfer object, that represents info about the poll option.
     *
     * @param option the poll option
     */
    public PollOptionDto(PollItem option) {
        this.id = option.getId();
        this.votesCount = option.getVotesCount();
    }

    /**
     * Get the poll option id.
     *
     * @return the poll option id
     */
    public long getId() {
        return id;
    }

    /**
     * Set the poll option id.
     * It is also required for JSON.
     *
     * @param id the poll option id
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Get the count of votes.
     *
     * @return the count of votes
     */
    public int getVotesCount() {
        return votesCount;
    }

    /**
     * Set the count of votes.
     * It is also required for JSON.
     *
     * @param votesCount the count of votes
     */
    public void setVotesCount(int votesCount) {
        this.votesCount = votesCount;
    }
}
