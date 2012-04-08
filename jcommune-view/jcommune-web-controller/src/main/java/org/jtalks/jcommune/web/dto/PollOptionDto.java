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

/**
 * Data transfer object for transferring poll option to the client side.
 * 
 * @author Anuar Nurmakanov
 *
 */
public class PollOptionDto {
    private long id;
    private int voteCount;

    /**
     * Default constructor.
     * It is also required for JSON.
     */
    public PollOptionDto() {
    }

    /**
     * Creates a dto with the passed data.
     * 
     * @param id id of poll option
     * @param voteCount the count of the votes
     */
    public PollOptionDto(long id, int voteCount) {
        this.id = id;
        this.voteCount = voteCount;
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
    public int getVoteCount() {
        return voteCount;
    }

    /**
     * Set the count of votes.
     * It is also required for JSON.
     * 
     * @param voteCount the count of votes
     */
    public void setVoteCount(int voteCount) {
        this.voteCount = voteCount;
    }
}
