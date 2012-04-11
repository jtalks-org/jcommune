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
import org.jtalks.jcommune.model.entity.PollOption;

import java.util.ArrayList;
import java.util.List;

/**
 * Data transfer object for transferring poll to the client side.
 *
 * @author Anuar Nurmakanov
 */
public class PollDto {
    private long id;
    private int totalPollCount;
    private List<PollOptionDto> pollOptions = new ArrayList<PollOptionDto>();

    /**
     * Default constructor.
     * It is also required for JSON.
     */
    public PollDto() {
    }

    /**
     * Creates a dto with the passed data.
     *
     * @param id             id of poll
     * @param totalVoteCount total vote count
     * @param pollOptions    the list of poll options
     */
    public PollDto(long id, int totalVoteCount, List<PollOptionDto> pollOptions) {
        this.id = id;
        this.totalPollCount = totalVoteCount;
        this.pollOptions = pollOptions;
    }

    /**
     * Creates data transfer object, that represents info about the poll.
     *
     * @param poll the poll
     */
    public PollDto(Poll poll) {
        this.id = poll.getId();
        this.totalPollCount = poll.getTotalPollCount();
        for (PollOption option : poll.getPollOptions()) {
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
     * Get the total poll count.
     *
     * @return the total poll count
     */
    public int getTotalPollCount() {
        return totalPollCount;
    }

    /**
     * Set the total poll count.
     * It is also required for JSON.
     *
     * @param totalPollCount the total poll count
     */
    public void setTotalPollCount(int totalPollCount) {
        this.totalPollCount = totalPollCount;
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
