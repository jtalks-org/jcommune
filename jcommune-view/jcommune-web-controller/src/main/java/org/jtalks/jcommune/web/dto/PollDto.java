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

import org.jtalks.jcommune.model.entity.Poll;
import org.jtalks.jcommune.model.entity.PollItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Data transfer object for transferring poll to the client side.
 *
 * @author Anuar Nurmakanov
 */
public class PollDto {
    public static final String DATE_FORMAT = "dd-MM-yyyy";
    private long id;
    private int totalVotesCount;
    private List<PollOptionDto> pollOptions = new ArrayList<PollOptionDto>();

    /**
     * Default constructor.
     * It is also required for JSON.
     */
    public PollDto() {
    }

    /**
     * Creates data transfer object, that represents info about the poll.
     *
     * @param poll the poll
     */
    public PollDto(Poll poll) {
        this.id = poll.getId();
        this.totalVotesCount = poll.getTotalVotesCount();
        for (PollItem option : poll.getPollItems()) {
            this.pollOptions.add(new PollOptionDto(option));
        }
    }

    /**
     * Get the poll id.
     *
     * @return the poll id
     */
    public long getId() {
        return id;
    }

    /**
     * Set the poll id.
     * It is also required for JSON.
     *
     * @param id the poll id
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Get the total count of votes.
     *
     * @return the total count of votes
     */
    public int getTotalVotesCount() {
        return totalVotesCount;
    }

    /**
     * Set the total count of votes.
     * It is also required for JSON.
     *
     * @param totalVotesCount the total count of votes
     */
    public void setTotalVotesCount(int totalVotesCount) {
        this.totalVotesCount = totalVotesCount;
    }

    /**
     * Get the list of data transfer objects, that represent info
     * about poll options.
     *
     * @return the list of data transfer objects, that represent info
     *         about poll options.
     */
    public List<PollOptionDto> getPollOptions() {
        return pollOptions;
    }

    /**
     * Get the list of data transfer objects, that represent info
     * about poll options. It is also required for JSON.
     *
     * @param pollOptions the list of data transfer objects, that represent info
     *                    about poll options.
     */
    public void setPollOptions(List<PollOptionDto> pollOptions) {
        this.pollOptions = pollOptions;
    }

    /**
     * Gets identifiers of poll options from data transfer objects.
     *
     * @return identifiers of poll options
     */
    public List<Long> getPollOptionIds() {
        List<Long> pollOptionIds = new ArrayList<Long>();
        for (PollOptionDto dto : pollOptions) {
            pollOptionIds.add(dto.getId());
        }
        return pollOptionIds;
    }
}
