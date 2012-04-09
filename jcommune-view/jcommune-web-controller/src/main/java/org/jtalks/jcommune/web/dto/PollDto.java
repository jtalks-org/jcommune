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

import java.util.ArrayList;
import java.util.List;

import org.jtalks.jcommune.model.entity.Poll;
import org.jtalks.jcommune.model.entity.PollOption;

/**
 * Data transfer object for transferring poll to the client side.
 *
 * @author Anuar Nurmakanov
 */
public class PollDto {
    private long id;
    private int totalVoteCount;
    private List<PollOptionDto> pollOptions;

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
        this.totalVoteCount = totalVoteCount;
        this.pollOptions = pollOptions;
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
    public int getTotalVoteCount() {
        return totalVoteCount;
    }

    /**
     * Set the total count of votes.
     * It is also required for JSON.
     *
     * @param totalVoteCount the total count of votes
     */
    public void setTotalVoteCount(int totalVoteCount) {
        this.totalVoteCount = totalVoteCount;
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
     * Creates data transfer object, that represents info about the poll.
     * 
     * @param poll the poll
     * @return data transfer object, that represents info about the poll
     */
    public static PollDto getDtoFor(Poll poll) {
        List<PollOptionDto> optionDtos = new ArrayList<PollOptionDto>();
        for (PollOption option : poll.getPollOptions()) {
            optionDtos.add(PollOptionDto.getDtoFor(option));
        }
        return new PollDto(poll.getId(), poll.getTotalVoteCount(), optionDtos);
    }
}
